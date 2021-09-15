package com.threadpool.core;

import com.alibaba.fastjson.JSON;
import com.threadpool.common.annotation.ThreadPoolIndicatorListener;
import com.threadpool.common.enums.QueueTypeEnum;
import com.threadpool.common.enums.RejectedExecutionHandlerEnum;
import com.threadpool.common.inter.ThreadPoolIndicatorNotify;
import com.threadpool.common.pojo.ThreadPoolIndicatorInfo;
import com.threadpool.common.properties.DynamicThreadPoolProperties;
import com.threadpool.common.properties.ThreadPoolProperties;
import com.threadpool.common.utils.BeanUtils;
import com.threadpool.common.utils.IpUtils;
import com.threadpool.core.rejected.CxmAbortPolicy;
import com.threadpool.core.rejected.CxmDiscardOldestPolicy;
import com.threadpool.core.rejected.CxmDiscardPolicy;
import com.threadpool.indicator.PoolIndicatorNotifyThread;
import com.threadpool.indicator.ThreadPoolIndicator;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;
import com.threadpool.db.enums.DynamicThreadPoolStatusEnum;
import com.threadpool.db.service.DynamicThreadPoolDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 动态线程池管理类
 *
 * @author cyy
 * @date 2021/04/09 10:36
 **/
@Slf4j
public class DynamicThreadPoolManager implements SignalHandler, ApplicationContextAware {

    /**
     * 存储线程池对象，Key:名称 Value:对象
     */
    private static final Map<String, DynamicThreadPoolExecutor> THREAD_POOL_EXECUTOR_MAP = new ConcurrentHashMap<>();
    /**
     * 存储线程池配置对象，Key:名称 Value:对象
     */
    private static final Map<String, ThreadPoolProperties> THREAD_POOL_PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    @Autowired(required = false)
    private DynamicThreadPoolDbService dynamicThreadPoolDbService;

    @Autowired(required = false)
    private ThreadPoolIndicatorNotify threadPoolIndicatorNotify;

    @Value("${nacos.config.server-addr:}")
    private String nacosServerAddress;

    @Value("${nacos.config.namespace:}")
    private String namespace;

    private String appInstanceId;

    /**
     * 应用名称，告警用到
     */
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @PostConstruct
    public void init() {
        dynamicThreadPoolProperties.getExecutors().forEach(executor -> {
            if (!threadPoolExist(executor.getThreadPoolName())) {
                THREAD_POOL_PROPERTIES_MAP.put(executor.getThreadPoolName(), executor);
                DynamicThreadPoolExecutor threadPoolExecutor = new DynamicThreadPoolExecutor(
                        executor.getCorePoolSize(),
                        executor.getMaximumPoolSize(),
                        executor.getKeepAliveTime(),
                        executor.getUnit(),
                        getBlockingQueue(executor.getQueueType(), executor.getQueueCapacity(), executor.isFair()),
                        new DynamicThreadFactory(executor.getThreadPoolName()),
                        getRejectedExecutionHandler(executor.getRejectedExecutionType(), executor.getThreadPoolName()), executor.getThreadPoolName());
                THREAD_POOL_EXECUTOR_MAP.put(executor.getThreadPoolName(), threadPoolExecutor);
            }
        });
        // 持久化
        if (dynamicThreadPoolDbService != null) {
            new PersistThreadPoolThread(dynamicThreadPoolProperties, this).start();
        }
        // 线程池运行信息通知
        new PoolIndicatorNotifyThread(dynamicThreadPoolProperties,this).start();
    }

    /**
     * 刷新线程池
     *
     * @param isWaitConfigRefreshOver 是否等待Nacos配置刷新完成
     * @author cyy
     * @date 2021/04/09 15:17
     */
    public void refreshThreadPoolExecutor(boolean isWaitConfigRefreshOver) {
        try {
            if (isWaitConfigRefreshOver) {
                // 等待Nacos配置刷新完成
                Thread.sleep(dynamicThreadPoolProperties.getNacosWaitRefreshConfigSeconds() * 1000L);
            }
        } catch (InterruptedException e) {
            log.error("等待Nacos配置刷新异常", e);
        }
        dynamicThreadPoolProperties.getExecutors().forEach(executor -> {
            THREAD_POOL_PROPERTIES_MAP.put(executor.getThreadPoolName(), executor);
            ThreadPoolExecutor threadPoolExecutor = THREAD_POOL_EXECUTOR_MAP.get(executor.getThreadPoolName());
            threadPoolExecutor.setCorePoolSize(executor.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(executor.getMaximumPoolSize());
            threadPoolExecutor.setKeepAliveTime(executor.getKeepAliveTime(), executor.getUnit());
            threadPoolExecutor.setRejectedExecutionHandler(getRejectedExecutionHandler(executor.getRejectedExecutionType(), executor.getThreadPoolName()));
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            if (queue instanceof ResizableCapacityLinkedBlockingQueue) {
                ((ResizableCapacityLinkedBlockingQueue<Runnable>) queue).setCapacity(executor.getQueueCapacity());
            }
        });
    }

    /**
     *
     * @author cyy
     * @date 2021/04/15 17:18
     * @param queueType 队列类型
     * @param queueCapacity 队列大小
     * @param fair fair
     * @return java.util.concurrent.BlockingQueue
     */
    private BlockingQueue getBlockingQueue(String queueType, int queueCapacity, boolean fair){
        QueueTypeEnum queueTypeEnum = QueueTypeEnum.getQueueTypeEnum(queueType);
        switch (queueTypeEnum){
            case DELAY_QUEUE:
                return new DelayQueue<>();
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>(fair);
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<>(queueCapacity);
            case LINKED_BLOCKING_DEQUE:
                return new LinkedBlockingDeque<>(queueCapacity);
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>(queueCapacity);
            case LINKED_TRANSFER_DEQUE:
                return new LinkedTransferQueue();
            case PRIORITY_BLOCKING_QUEUE:
                return new PriorityBlockingQueue<>(queueCapacity);
            case DYNAMIC_LINKED_BLOCKING_QUEUE:
            default:
                return new ResizableCapacityLinkedBlockingQueue<>(queueCapacity);
        }
    }
    /**
     * 获取拒绝策略
     * @author cyy
     * @date 2021/04/15 17:18
     * @param rejectedExecutionType 拒绝策略类型
     * @param threadPoolName 线程池名称
     * @return java.util.concurrent.RejectedExecutionHandler
     */
    private RejectedExecutionHandler getRejectedExecutionHandler(String rejectedExecutionType,String threadPoolName){
        RejectedExecutionHandlerEnum rejectedExecutionHandlerEnum = RejectedExecutionHandlerEnum.getRejectedExecutionHandlerEnum(rejectedExecutionType);
        switch (rejectedExecutionHandlerEnum){
            case DISCARD_POLICY:
                return new CxmDiscardPolicy(threadPoolName);
            case CALLER_RUNS_POLICY:
                return new ThreadPoolExecutor.CallerRunsPolicy();
            case DISCARD_OLDEST_POLICY:
                return new CxmDiscardOldestPolicy(threadPoolName);
            case ABORT_POLICY:
            default:
                return new CxmAbortPolicy(threadPoolName);
        }
    }

    /**
     * 获取线程池
     *
     * @param threadPoolName 线程池名称
     * @return com.cyy.threadpool.core.DynamicThreadPoolExecutor
     * @author cyy
     * @date 2021/04/09 15:14
     */
    public DynamicThreadPoolExecutor getThreadPoolExecutor(String threadPoolName) {
        DynamicThreadPoolExecutor threadPoolExecutor = THREAD_POOL_EXECUTOR_MAP.get(threadPoolName);
        if (threadPoolExecutor == null) {
            throw new NullPointerException("找不到线程池 " + threadPoolName);
        }
        return threadPoolExecutor;
    }

    /**
     * 获取线程池信息
     *
     * @param threadPoolName 线程池名称
     * @return com.cyy.threadpool.core.ThreadPoolIndicatorInfo
     * @author cyy
     * @date 2021/04/10 13:26
     */
    public ThreadPoolIndicatorInfo getDynamicThreadPoolIndicatorInfo(String threadPoolName) {
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor = THREAD_POOL_EXECUTOR_MAP.get(threadPoolName);
        if (dynamicThreadPoolExecutor == null) {
            return null;
        }
        ThreadPoolProperties threadPoolProperties = THREAD_POOL_PROPERTIES_MAP.get(threadPoolName);
        ThreadPoolIndicatorInfo threadPoolIndicatorInfo = new ThreadPoolIndicatorInfo();
        BeanUtils.copyIgnoreNullProperties(threadPoolProperties, threadPoolIndicatorInfo);
        threadPoolIndicatorInfo.setCompleteTaskCount(dynamicThreadPoolExecutor.getCompletedTaskCount());
        threadPoolIndicatorInfo.setActiveCount(dynamicThreadPoolExecutor.getActiveCount());
        threadPoolIndicatorInfo.setActiveRate((int)(((double) dynamicThreadPoolExecutor.getActiveCount()/threadPoolIndicatorInfo.getMaximumPoolSize())*100));
        threadPoolIndicatorInfo.setRejectCount(ThreadPoolIndicator.getRejectCount(threadPoolName));
        threadPoolIndicatorInfo.setQueueSize(dynamicThreadPoolExecutor.getQueue().size());
        threadPoolIndicatorInfo.setCorePoolSize(dynamicThreadPoolExecutor.getCorePoolSize());
        threadPoolIndicatorInfo.setLargestPoolSize(dynamicThreadPoolExecutor.getLargestPoolSize());
        threadPoolIndicatorInfo.setThreadPoolId(dynamicThreadPoolExecutor.getThreadPoolId());
        return threadPoolIndicatorInfo;
    }
    /**
     * 获取所有线程池的数据
     * @author cyy
     * @date 2021/04/17 15:09
     * @return java.util.List<com.cyy.threadpool.common.pojo.ThreadPoolIndicatorInfo>
     */
    public List<ThreadPoolIndicatorInfo> getAllThreadPoolIndicator(){
        List<ThreadPoolIndicatorInfo> allThreadPoolIndicator = new ArrayList<>();
        THREAD_POOL_EXECUTOR_MAP.forEach((k,v)->{
            allThreadPoolIndicator.add(getDynamicThreadPoolIndicatorInfo(k));
        });
        return allThreadPoolIndicator;
    }

    /**
     * 判断线程池是否已经存在
     *
     * @param poolThreadName 线程池名称
     * @return boolean
     * @author cyy
     * @date 2021/04/09 20:01
     */
    private boolean threadPoolExist(String poolThreadName) {
        return THREAD_POOL_EXECUTOR_MAP.containsKey(poolThreadName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansWithAnnotation(ThreadPoolIndicatorListener.class).forEach((name, bean) -> {
            try {
                ThreadPoolIndicatorNotify threadPoolIndicatorNotify = (ThreadPoolIndicatorNotify) bean.getClass().newInstance();
                PoolIndicatorNotifyThread.THREAD_POOL_INDICATOR_NOTIFIES.add(threadPoolIndicatorNotify);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        if(threadPoolIndicatorNotify != null){
            PoolIndicatorNotifyThread.THREAD_POOL_INDICATOR_NOTIFIES.add(threadPoolIndicatorNotify);
        }
    }

    class PersistThreadPoolThread extends Thread {
        private final DynamicThreadPoolProperties dynamicThreadPoolProperties;
        private final SignalHandler signalHandler;

        public PersistThreadPoolThread(DynamicThreadPoolProperties dynamicThreadPoolProperties, SignalHandler signalHandler) {
            this.dynamicThreadPoolProperties = dynamicThreadPoolProperties;
            this.signalHandler = signalHandler;
        }

        /**
         * 创建线程池
         *
         * @author cyy
         * @date 2021/04/09 15:15
         */
        @Override
        public void run() {
            Signal.handle(new Signal("TERM"), signalHandler);
            Signal.handle(new Signal("INT"), signalHandler);
            ApplicationInstanceInfo properties = new ApplicationInstanceInfo();
            BeanUtils.copyIgnoreNullProperties(dynamicThreadPoolProperties, properties);
            properties.setExecutors(JSON.toJSONString(dynamicThreadPoolProperties.getExecutors()));
            properties.setAlarm(JSON.toJSONString(dynamicThreadPoolProperties.getAlarm()));
            properties.setInstanceIp(IpUtils.getHostIp());
            properties.setStatus(DynamicThreadPoolStatusEnum.RUNNING.name());
            properties.setAppName(applicationName);
            properties.setNacosAddress(nacosServerAddress);
            properties.setNacosNamespace(namespace);
            if(dynamicThreadPoolProperties.getDb() != null){
                properties.setDb(JSON.toJSONString(dynamicThreadPoolProperties.getDb()));
            }
            ApplicationInfo applicationInfo = new ApplicationInfo();
            BeanUtils.copyIgnoreNullProperties(properties,applicationInfo);
            properties.setAppId(dynamicThreadPoolDbService.insertApplicationInfo(applicationInfo));
            appInstanceId = dynamicThreadPoolDbService.insertInstanceInfo(properties);
            List<DynamicThreadPoolInfo> dynamicThreadPoolInfos = BeanUtils.convertListTo(dynamicThreadPoolProperties.getExecutors(), DynamicThreadPoolInfo::new);
            Map<String,String> threadIdMap = dynamicThreadPoolDbService.insertThreadPoolByBatch(appInstanceId, dynamicThreadPoolInfos);
            threadIdMap.forEach((threadPoolName,threadPoolId)->{
                THREAD_POOL_EXECUTOR_MAP.get(threadPoolName).setThreadPoolId(threadPoolId);
            });
            // Runtime.getRuntime().addShutdownHook(new ShutdownListenerThread(dynamicThreadPoolDbService,appInstanceId,threadPoolIdList));
        }

    }

    @Override
    public void handle(Signal signal) {
        log.info("enter into shutdown");
        dynamicThreadPoolDbService.shutDown(appInstanceId);
        System.exit(0);
    }
}
