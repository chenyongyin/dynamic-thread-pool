package com.threadpool.common.enums;


/**
 * 队列类型枚举
 *
 * @author cyy
 * @date 2021/04/09 10:35
 **/
public enum QueueTypeEnum {
    /**
     * 由链表结构组成的有界(但大小默认值Integer>MAX_VALUE)阻塞队列,可调节大小
     */
    DYNAMIC_LINKED_BLOCKING_QUEUE("DynamicLinkedBlockingQueue"),
    /**
     * 由链表结构组成的有界(但大小默认值Integer>MAX_VALUE)阻塞队列
     */
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),
    /**
     * 不存储元素的阻塞队列,也即是单个元素的队列
     */
    SYNCHRONOUS_QUEUE("SynchronousQueue"),
    /**
     * 由数组结构组成的有界阻塞队列
     */
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),
    /**
     * 使用优先级队列实现的延迟无界阻塞队列.
     */
    DELAY_QUEUE("DelayQueue"),
    /**
     * 由链表结构组成的无界阻塞队列
     */
    LINKED_TRANSFER_DEQUE("LinkedTransferQueue"),
    /**
     * 支持优先级排序的无界阻塞队列
     */
    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue"),
    /**
     * 由了解结构组成的双向阻塞队列
     */
    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque");

    private final String queueType;

    QueueTypeEnum(String queueType){
        this.queueType = queueType;
    }

    public String getQueueType() {
        return queueType;
    }

    public static QueueTypeEnum getQueueTypeEnum(String queueType){
        for (QueueTypeEnum value : QueueTypeEnum.values()) {
            if(value.getQueueType().equals(queueType)){
                return value;
            }
        }
        return DYNAMIC_LINKED_BLOCKING_QUEUE;
    }



}
