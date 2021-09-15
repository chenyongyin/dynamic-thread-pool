package com.threadpool.alarm;

import com.threadpool.common.annotation.ThreadPoolAlarmListener;
import com.threadpool.common.enums.AlarmTypeEnum;
import com.threadpool.common.inter.ThreadPoolAlarmNotify;
import com.threadpool.common.pojo.AlarmNotifyMessage;
import com.threadpool.common.pojo.ThreadPoolIndicatorInfo;
import com.threadpool.common.properties.DynamicThreadPoolProperties;
import com.threadpool.common.properties.ThreadPoolProperties;
import com.threadpool.common.utils.IpUtils;
import com.threadpool.core.DynamicThreadPoolExecutor;
import com.threadpool.core.DynamicThreadPoolManager;
import com.threadpool.indicator.ThreadPoolIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 线程池告警类
 *
 * @author cyy
 * @date 2021/04/09 10:42
 **/
@Slf4j
@RequiredArgsConstructor
public class DynamicThreadPoolAlarm implements ApplicationContextAware {
    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    @Autowired(required = false)
    private JavaMailSenderImpl mailSender;

    @Autowired(required = false)
    private ThreadPoolAlarmNotify threadPoolAlarmNotify;

    private String currIp;

    private static final List<ThreadPoolAlarmNotify> THREAD_POOL_ALARM_NOTIFY_LIST = new ArrayList<>();

    /**
     * 应用名称，告警用到
     */
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    /**
     * 是否使用默认告警
     */
    @Value("${kitty.threadpools.alarm.default:true}")
    private boolean useDefaultAlarm;

    @PostConstruct
    public void init() {
        currIp = IpUtils.getHostIp();
        new AlarmDaemonThread(this, dynamicThreadPoolProperties, dynamicThreadPoolManager).start();
    }

    /**
     * 发送任务拒绝告警
     *
     * @param rejectCount          拒绝数量
     * @param threadPoolProperties 线程池参数
     * @author cyy
     * @date 2021/04/09 19:27
     */
    public void sendRejectAlarmMessage(long rejectCount, ThreadPoolProperties threadPoolProperties) {
        AlarmNotifyMessage alarmNotifyMessage = getRejectCountMessage(rejectCount, threadPoolProperties);
        alarmNotify(alarmNotifyMessage);
    }

    /**
     * 发送任务堆积告警
     *
     * @param threadPoolProperties 线程池参数
     * @param taskCount            任务数
     * @author cyy
     * @date 2021/04/09 19:25
     */
    public void sendQueueCapacityThresholdAlarmMessage(ThreadPoolProperties threadPoolProperties, int taskCount) {
        AlarmNotifyMessage alarmNotifyMessage = getQueueCapacityThresholdMessage(taskCount, threadPoolProperties);
        alarmNotify(alarmNotifyMessage);
    }

    private void alarmNotify(AlarmNotifyMessage alarmNotifyMessage){
        for (ThreadPoolAlarmNotify threadPoolAlarmNotify : THREAD_POOL_ALARM_NOTIFY_LIST) {
            threadPoolAlarmNotify.alarmNotify(alarmNotifyMessage);
        }
    }

    /**
     * 发送活跃度告警信息
     *
     * @param activeCount          活跃线程数
     * @param threadPoolProperties 线程池参数
     * @author cyy
     * @date 2021/04/10 13:19
     */
    public void sendActiveThresholdMessage(int activeCount, ThreadPoolProperties threadPoolProperties) {
        AlarmNotifyMessage alarmNotifyMessage = getActiveThresholdMessage(activeCount, threadPoolProperties);
        alarmNotify(alarmNotifyMessage);
    }

    /**
     * 任务堆积告警message
     *
     * @param taskCount            任务数
     * @param threadPoolProperties 线程池参数
     * @return java.lang.String
     * @author cyy
     * @date 2021/04/09 18:23
     */
    private AlarmNotifyMessage getQueueCapacityThresholdMessage(int taskCount, ThreadPoolProperties threadPoolProperties) {
        return getAlarmMessage("线程池出现任务堆积情况,队列容量:" + threadPoolProperties.getQueueCapacity() + ",等待执行任务数量:" + taskCount, AlarmTypeEnum.QUEUE_CAPACITY, threadPoolProperties);
    }

    /**
     * 任务拒绝告警message
     *
     * @param rejectCount          拒绝数量
     * @param threadPoolProperties 线程池参数
     * @return java.lang.String
     * @author cyy
     * @date 2021/04/09 18:23
     */
    private AlarmNotifyMessage getRejectCountMessage(long rejectCount, ThreadPoolProperties threadPoolProperties) {
        return getAlarmMessage("线程池中出现RejectedExecutionException(" + rejectCount + "次)",AlarmTypeEnum.REJECT, threadPoolProperties);
    }

    /**
     * 获取活跃度告警message
     *
     * @param activeCount          活跃度
     * @param threadPoolProperties 线程池参数配置
     * @return java.lang.String
     * @author cyy
     * @date 2021/04/10 13:16
     */
    private AlarmNotifyMessage getActiveThresholdMessage(int activeCount, ThreadPoolProperties threadPoolProperties) {
        return getAlarmMessage("activeCount/maximumPoolSize值为(" + (activeCount / threadPoolProperties.getMaximumPoolSize()) * 100 + "),触达阈值(" + threadPoolProperties.getActiveRateCapacityThreshold() + ")"
                ,AlarmTypeEnum.ACTIVE
                , threadPoolProperties);
    }

    /**
     * 获取预警信息
     *
     * @param reason 告警原因
     * @param prop   线程池参数
     * @return java.lang.String
     * @author cyy
     * @date 2021/04/09 18:22
     */
    private AlarmNotifyMessage getAlarmMessage(String reason,AlarmTypeEnum alarmTypeEnum, ThreadPoolProperties prop) {
        ThreadPoolIndicatorInfo threadPoolIndicatorInfo = dynamicThreadPoolManager.getDynamicThreadPoolIndicatorInfo(prop.getThreadPoolName());
        return new AlarmNotifyMessage(applicationName,
                prop.getThreadPoolName(),
                prop.getOwner(),
                dynamicThreadPoolProperties.getAlarm().getAlarmTimeInterval(),
                alarmTypeEnum,
                reason,
                threadPoolIndicatorInfo.toString(),
                threadPoolIndicatorInfo,currIp);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansWithAnnotation(ThreadPoolAlarmListener.class).forEach((name, bean) -> {
            try {
                ThreadPoolAlarmNotify threadPoolAlarmNotify = (ThreadPoolAlarmNotify) bean.getClass().newInstance();
                THREAD_POOL_ALARM_NOTIFY_LIST.add(threadPoolAlarmNotify);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        if(threadPoolAlarmNotify != null){
            THREAD_POOL_ALARM_NOTIFY_LIST.add(threadPoolAlarmNotify);
        }
        DefaultThreadPoolAlarmNotify defaultThreadPoolAlarmNotify = new DefaultThreadPoolAlarmNotify(mailSender, dynamicThreadPoolProperties.getAlarm());
        THREAD_POOL_ALARM_NOTIFY_LIST.add(defaultThreadPoolAlarmNotify);
    }

    /**
     * 告警守护线程
     *
     * @author cyy
     * @date 2021/04/12 20:42
     **/
    static class AlarmDaemonThread extends Thread {

        private final DynamicThreadPoolProperties dynamicThreadPoolProperties;
        private final DynamicThreadPoolManager dynamicThreadPoolManager;
        private final DynamicThreadPoolAlarm dynamicThreadPoolAlarm;

        public AlarmDaemonThread(DynamicThreadPoolAlarm dynamicThreadPoolAlarm, DynamicThreadPoolProperties dynamicThreadPoolProperties, DynamicThreadPoolManager dynamicThreadPoolManager) {
            this.dynamicThreadPoolProperties = dynamicThreadPoolProperties;
            this.dynamicThreadPoolManager = dynamicThreadPoolManager;
            this.dynamicThreadPoolAlarm = dynamicThreadPoolAlarm;
        }

        @Override
        public void run() {
            while (true) {
                dynamicThreadPoolProperties.getExecutors().forEach(prop -> {
                    try{
                        String threadPoolName = prop.getThreadPoolName();
                        DynamicThreadPoolExecutor threadPoolExecutor = dynamicThreadPoolManager.getThreadPoolExecutor(threadPoolName);
                        int queueCapacityThreshold = prop.getQueueCapacityThreshold();
                        int taskCount = threadPoolExecutor.getQueue().size();
                        if(!prop.isAlarmEnable()){
                            return;
                        }
                        // 发送活跃度预警
                        if (prop.getActiveRateCapacityThreshold() != -1 && (threadPoolExecutor.getActiveCount() / prop.getMaximumPoolSize()) * 100 > prop.getActiveRateCapacityThreshold()) {
                            dynamicThreadPoolAlarm.sendActiveThresholdMessage(threadPoolExecutor.getActiveCount(), prop);
                        }
                        // 发送任务堆积告警
                        if (queueCapacityThreshold != -1 && taskCount > queueCapacityThreshold) {
                            dynamicThreadPoolAlarm.sendQueueCapacityThresholdAlarmMessage(prop, taskCount);
                        }
                        // 发送任务拒绝告警message
                        long rejectCount = ThreadPoolIndicator.getRejectCount(threadPoolName);
                        if (rejectCount > 0) {
                            dynamicThreadPoolAlarm.sendRejectAlarmMessage(rejectCount, prop);
                            // 清空拒绝数据
                            ThreadPoolIndicator.clearRejectCount(threadPoolName);
                        }
                    }catch (Exception e){
                        log.error("执行预警任务异常",e);
                    }
                });
                try {
                    Thread.sleep(dynamicThreadPoolProperties.getAlarm().getAlarmTimeInterval()*60000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
