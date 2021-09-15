package com.threadpool.common.enums;



/**
 * 拒绝策略类型枚举
 *
 * @author cyy
 * @date 2021/04/09 10:35
 **/
public enum RejectedExecutionHandlerEnum {

    /**
     * 不会抛弃任务,也不会抛出异常,而是将某些任务回退给调用者,从而降低新任务的流量
     */
    CALLER_RUNS_POLICY("CallerRunsPolicy"),

    /**
     * 直接抛出RejectedException异常阻止系统正常运行
     */
    ABORT_POLICY("AbortPolicy"),

    /**
     * 直接丢弃任务,不予任何处理也不抛出异常.如果允许任务丢失,这是最好的拒绝策略
     */
    DISCARD_POLICY("DiscardPolicy"),

    /**
     * 抛弃队列中等待最久的任务,然后把当前任务加入队列中尝试再次提交
     */
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy");

    /**
     * 类型
     */
    private final String rejectedType;

    RejectedExecutionHandlerEnum(String rejectedType){
        this.rejectedType = rejectedType;
    }

    public String getRejectedType() {
        return rejectedType;
    }

    /**
     * 获取拒绝策略
     * @author cyy
     * @date 2021/04/09 15:10
     * @param rejectedType 拒绝策略类型
     * @return com.cyy.threadpool.common.enums.RejectedExecutionHandlerEnum
     */
    public static RejectedExecutionHandlerEnum getRejectedExecutionHandlerEnum(String rejectedType){
        RejectedExecutionHandlerEnum rejectedExecutionHandlerEnum = null;
        for (RejectedExecutionHandlerEnum value : RejectedExecutionHandlerEnum.values()) {
            if(value.getRejectedType().equals(rejectedType)){
                return value;
            }
        }
        return ABORT_POLICY;
    }

}
