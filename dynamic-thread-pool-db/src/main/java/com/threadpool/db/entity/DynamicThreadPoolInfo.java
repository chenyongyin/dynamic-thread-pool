package com.threadpool.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 线程池信息
 *
 * @author cyy
 * @date 2021/04/12 11:31
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dynamic_thread_pool_info")
public class DynamicThreadPoolInfo extends BasePageReq{
    @TableId("thread_pool_id")
    private String id;
    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数, 默认值为CPU核心数量
     */
    private int maximumPoolSize;

    /**
     * 队列最大数量，默认1000
     */
    private int queueCapacity;

    /**
     * 空闲线程存活时间
     */
    private long keepAliveTime;

    /**
     * 队列容量阀值，超过此值告警
     */
    private int queueCapacityThreshold;

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
    private String fair;

    /**
     * 线程活跃度阈值
     */
    private int activeRateCapacityThreshold;

    /**
     * 状态
     */
    private String status;
    /**
     * 应用实例id
     */
    private String appInstanceId;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 是否开启告警
     */
    private Boolean alarmEnable;
    /**
     * 线程池运行信息
     */
    private String threadPoolIndicatorInfo;
}
