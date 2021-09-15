package com.threadpool.core.rejected;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 直接丢弃任务,不予任何处理也不抛出异常.如果允许任务丢失,这是最好的拒绝策略
 * @author cyy
 * @date 2021/04/09 14:53
 **/
@AllArgsConstructor
public class CxmDiscardPolicy extends AbstractRejectedExecutionHandler{

    /**
     * 线程池名称
     */
    private String threadPoolName;

    public CxmDiscardPolicy() {}

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        incrRejectNum(this.threadPoolName);
    }
}
