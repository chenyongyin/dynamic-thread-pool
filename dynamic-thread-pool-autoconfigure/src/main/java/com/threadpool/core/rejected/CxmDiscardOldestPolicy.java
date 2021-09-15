package com.threadpool.core.rejected;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 抛弃队列中等待最久的任务,然后把当前任务加入队列中尝试再次提交
 * @author cyy
 * @date 2021/04/09 14:52
 **/
@AllArgsConstructor
public class CxmDiscardOldestPolicy extends AbstractRejectedExecutionHandler{

    /**
     * 线程池名称
     */
    private String threadPoolName;

    public CxmDiscardOldestPolicy() {}

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            executor.getQueue().poll();
            executor.execute(r);
            incrRejectNum(this.threadPoolName);
        }
    }
}
