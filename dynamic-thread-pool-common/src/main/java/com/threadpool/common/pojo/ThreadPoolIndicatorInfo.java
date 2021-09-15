package com.threadpool.common.pojo;

import com.threadpool.common.properties.ThreadPoolProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 线程池数据指标信息
 *
 * @author cyy
 * @date 2021/04/09 20:15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadPoolIndicatorInfo extends ThreadPoolProperties {
    /**
     * 队列长度
     */
    private long queueSize;
    /**
     * 完成任务数
     */
    private long completeTaskCount;
    /**
     * 任务拒绝数
     */
    private long rejectCount;
    /**
     * 出现的最大数
     */
    private long largestPoolSize;
    /**
     * 任务活跃数
     */
    private long activeCount;
    /**
     * 线程池活跃度
     */
    private int activeRate;
    /**
     * 线程池id
     */
    private String threadPoolId;

    @Override
    public String toString() {
        return '\n'+"threadPoolName：" + super.getThreadPoolName() + '\n' +
                "corePoolSize：" + super.getCorePoolSize() +'\n' +
                "maximumPoolSize：" + super.getMaximumPoolSize() +'\n' +
                "queueCapacity：" + super.getQueueCapacity() +'\n' +
                "keepAliveTime：" + super.getKeepAliveTime() +'\n' +
                "unit：" + super.getUnit() +'\n' +
                "rejectedExecutionType：" + super.getRejectedExecutionType() + '\n' +
                "queueType：" + super.getQueueType() + '\n' +
                "fair：" + super.isFair()+ '\n' +
                "queueCapacityThreshold：" + super.getQueueCapacityThreshold() +'\n' +
                "activeRateCapacityThreshold：" + super.getActiveRateCapacityThreshold()+ '\n' +
                "queueSize：" + queueSize +'\n' +
                "activeCount：" + activeCount +'\n' +
                "activeRate：" + activeRate +'\n' +
                "completeTaskCount：" + completeTaskCount +'\n' +
                "largestPoolSize：" + largestPoolSize +'\n'+
                "rejectCount：" + rejectCount +'\n';

    }
}
