package com.threadpool.common.properties;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 *
 * @author cyy
 * @date 2021/04/09 10:39
 **/
@Data
public class ThreadPoolProperties implements Serializable {

    /**
     * 线程池名称
     */
    private String threadPoolName = "Dynamic-Thread-Pool";

    /**
     * 负责人
     */
    private String owner;

    /**
     * 核心线程数
     */
    private int corePoolSize = 1;

    /**
     * 最大线程数, 默认值为CPU核心数量
     */
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 队列最大数量，默认1000
     */
    private int queueCapacity = 1000;

    /**
     * 空闲线程存活时间
     */
    private long keepAliveTime;

    /**
     * 空闲线程存活时间单位
     */
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    /**
     * 队列容量阀值，超过此值告警
     */
    private int queueCapacityThreshold = -1;

    /**
     * 拒绝策略
     */
    private String rejectedExecutionType;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * SynchronousQueue 是否公平策略
     */
    private boolean fair;

    /**
     * 线程活跃度阈值
     */
    private int activeRateCapacityThreshold = -1;
    /**
     * 是否开启预警
     */
    private boolean alarmEnable;

}
