# dynamic-thread-pool(动态线程池)
### 一、描述
#### 实现功能
##### 1. 支持实时查看线程池运行状态
##### 2. 支持动态调整线程池参数
##### 3. 支持负载监控和告警

#### 未来规划
##### 1. 接入监控平台
##### 2. 接入管理平台


---

### 二、Maven 引用方式
```xml
<!--在pom.xml中添加依赖-->
	<dependency>
		<groupId>com.cyy.threadpool</groupId>
		<artifactId>dynamic-thread-pool-starter</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
```

---

### 三、配置文件
 
 ```yml
## application.yml
nacos:
  config:
    bootstrap:
      enable: true
      log:
        enable: true
    type: yaml
    server-addr: 127.0.0.1:8848
    namespace: xxxxxxx
    context-path: nacos
    data-id: shop-service
    auto-refresh: true
    group: DEFAULT_GROUP
	##在nacos原有配置中增加一下配置
    ext-config:
      - auto-refresh: true
        config-long-poll-timeout: 46000
        config-retry-time: 30000
        data-id: fcs-web-dynamic-thread-pool ##当前应用线程池的data-id，对应下方线程池配置
        enable-remote-sync-config: true
        group: dynamic_thread_pool   ##固定不变
        max-retry: 10
        type: yaml



## dynamic-thread-pool.yml 为了动态刷新线程池配置，修改线程池配置实时生效
dynamic:
  threadpools:
    nacosDataId: dynamic-thread-pool
    nacosGroup: dynamic_thread_pool
    nacosWaitRefreshConfigSeconds: 1
    db:
      type: mysql
    alarm:
      enabled: true
      alarmTimeInterval: 2
      apiAlarmUrl: https://127.0.0.1/api/test
      wxRobotApiUrl: https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxx
      dingDing:
        accessToken: xxxxx
        secret: xxxxx
      email:
        host: smtp.exmail.qq.com
        username: cyy@qq.com
        password: xxxxx
        port: 587
        properties:
        toUsers:
        - zhangsan@qq.com
        - lisi@qq.com
    executors:
    - threadPoolName: dynamic-thread-pool
      owner: cyy
      alarmEnable: true
      corePoolSize: 5
      maximumPoolSize: 10
      queueCapacity: 10
      keepAliveTime: 0
      queueType: DynamicLinkedBlockingQueue
      rejectedExecutionType: AbortPolicy
      activeRateCapacityThreshold: 80
      queueCapacityThreshold: 10
      fair: true
 ```

---
### 四、参数描述

参数名 | 必须 | 默认值 | 说明
---|---|---|---
dynamic.threadpools.nacosDataId | 否 | 无 | nacos配置中心的dataId，如果要实现动态调整线程池参数，则必须配置
dynamic.threadpools.nacosGroup | 否 | dynamic_thread_pool | nacos配置中心的nacosGroup，固定为dynamic_thread_pool，不能修改，如果要实现动态调整线程池参数，则必须配置
dynamic.threadpools.db.type | 否 | 空 | 持久化类型 目前支持mysql mongodb
dynamic.threadpools.alarm.enabled | 否 | false | 是否开启预警推送
dynamic.threadpools.alarm.alarmTimeInterval | 否 | 1分钟 | 预警推送间隔时间，单位为分钟
dynamic.threadpools.alarm.apiAlarmUrl | 否 | 空 | 预警推送的url，为post，json请求
dynamic.threadpools.alarm.wxRobotApiUrl | 否 | 空 | 企业微信机器人推送地址
dynamic.threadpools.alarm.dingDing.accessToken | 否 | 空 | 钉钉token
dynamic.threadpools.alarm.dingDing.secret | 否 | 空 | 钉钉secret
dynamic.threadpools.alarm.email.host | 否 | 空 | host
dynamic.threadpools.alarm.email.username | 否 | 空 | 邮箱账号
dynamic.threadpools.alarm.email.password | 否 | 空 | 邮箱密码
dynamic.threadpools.alarm.email.port | 否 | 空 | 端口
dynamic.threadpools.alarm.email.properties | 否 | 空 | 额外参数
dynamic.threadpools.alarm.email.toUsers | 否 | 空 | 接收人邮箱列表
dynamic.threadpools.executors.threadPoolName | 是 | Dynamic-Thread-Pool | 线程池名称
dynamic.threadpools.executors.ower | 否 | 空 | 线程池负责人
dynamic.threadpools.executors.alarmEnable | 否 | false | 是否开启预警
dynamic.threadpools.executors.corePoolSize | 是 | 1 | 常驻线程数大小
dynamic.threadpools.executors.maximumPoolSize | 是 | cpu核心数 | 最大线程数，默认为cpu核心数
dynamic.threadpools.executors.queueCapacity | 是 | 1000 | 任务队列大小
dynamic.threadpools.executors.keepAliveTime | 是 | 0 | 多余的空闲线程存活时间,当空间时间达到keepAliveTime值时,多余的线程会被销毁直到只剩下corePoolSize个线程为止
dynamic.threadpools.executors.queueType | 否 | DynamicLinkedBlockingQueue | 任务队列类型，DynamicLinkedBlockingQueue(可动态调整大小)、LinkedBlockingQueue、SynchronousQueue、ArrayBlockingQueue、DelayQueue、LinkedTransferQueue、LinkedBlockingDeque、PriorityBlockingQueue
dynamic.threadpools.executors.rejectedExecutionType | 否 | DISCARD_POLICY | 拒绝策略，CallerRunsPolicy、AbortPolicy、DiscardPolicy、DiscardOldestPolicy
dynamic.threadpools.executors.activeRateCapacityThreshold | 否 | -1 | 活跃度预警阈值，默认-1，为不预警
dynamic.threadpools.executors.queueCapacityThreshold | 否 | -1 | 队列堆积预警阈值，默认-1，为不预警
dynamic.threadpools.executors.fair | 否 | false | 是否公平策略，当queueType为SynchronousQueue时配置生效

---
### 五、告警信息
##### 返回信息

```
[告警应用]:fcs-web
[ip]:192.168.8.167
[线程池名称]:dynamic-thread-pool
[告警原因]:activeCount/maximumPoolSize值为(100),触达阈值(80)
[线程池参数]:
threadPoolName：dynamic-thread-pool
corePoolSize：5
maximumPoolSize：10
queueCapacity：10
keepAliveTime：0
unit：MILLISECONDS
rejectedExecutionType：AbortPolicy
queueType：DynamicLinkedBlockingQueue
fair：false
queueCapacityThreshold：10
activeRateCapacityThreshold：80
queueSize：10
activeCount：10
activeRate：100
completeTaskCount：0
largestPoolSize：10
rejectCount：0
[业务负责人]:cyy
[告警间隔]:2分钟
```
##### 参数说明

参数 | 说明
---|---
threadPoolName | 线程池名称
corePoolSize | 常驻线程数
maximumPoolSize | 最大线程数
queueCapacity | 任务队列最大大小
keepAliveTime | 线程存活时间
unit | 时间单位
rejectedExecutionType | 任务拒绝策略
queueType | 队列类型
fair | 是否公平策略
queueCapacityThreshold | 队列任务堆积阈值
activeRateCapacityThreshold | 活跃度预警阈值
queueSize | 队列长度
activeCount | 活跃线程数
activeRate | 活跃度
completeTaskCount | 完成任务数
largestPoolSize | 任务队列历史最大数
rejectCount | 任务拒绝数
---
### 六、使用

```java
public class DynamicThreadPoolDemo{

    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;
    /**
     * 
     * @author cyy
     * @date 2021/04/10 17:37
     * @param threadPoolName 在配置中心配置过的线程池名称
     */
    public void submitTask(String threadPoolName){
        dynamicThreadPoolManager.getThreadPoolExecutor(threadPoolName).execute(() -> {
            // TODO 处理任务
        }, "我是一个无敌牛逼的任务");
    }
    
    /**
     * 实时获取线程池的运行信息
     * @author cyy
     * @date 2021/04/10 17:37
     * @param threadPoolName 在配置中心配置过的线程池名称
     */
    public ThreadPoolIndicatorInfo getThreadPoolIndicatorInfo(String threadPoolName){
        return dynamicThreadPoolManager.getThreadPoolIndicatorInfo(threadPoolName);
    }
    
}

// 自定义告警信息处理类
@ThreadPoolAlarmListener
public class MyThreadPoolAlarmNotify implements ThreadPoolAlarmNotify{

    @Override
    public void alarmNotify(AlarmMessage alarmMessage){
        // TODO 自己对告警信息进行处理
       log.info("message:", alarmMessage.getMessage());
    }
    
}

// 自定义线程池运行信息监听类,默认每隔5秒会收到信息
@ThreadPoolIndicatorListener
public class MyThreadPoolIndicatorNotify implements ThreadPoolIndicatorNotify {
    @Override
    public void indicatorNotify(List<ThreadPoolIndicatorInfo> indicatorInfoList) {
        System.out.println(JSON.toJSONString(indicatorInfoList));
    }
}

```


