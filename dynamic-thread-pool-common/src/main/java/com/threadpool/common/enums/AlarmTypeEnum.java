package com.threadpool.common.enums;

/**
 * 告警类型
 * @author cyy
 * @date 2021/04/16 15:49
 **/
public enum AlarmTypeEnum {
    /** 队列容量*/
    QUEUE_CAPACITY,
    /** 任务拒绝告警*/
    REJECT,
    /** 任务活跃度*/
    ACTIVE;
}
