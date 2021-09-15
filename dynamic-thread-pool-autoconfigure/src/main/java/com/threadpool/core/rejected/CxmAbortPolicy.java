package com.threadpool.core.rejected;


import lombok.AllArgsConstructor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 直接抛出RejectedException异常阻止系统正常运行
 * @author cyy
 * @date 2021/04/09 14:51
 **/
@AllArgsConstructor
public class CxmAbortPolicy extends AbstractRejectedExecutionHandler{
    /**
     * 线程池名称
     */
    private String threadPoolName;

    public CxmAbortPolicy() { }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        incrRejectNum(this.threadPoolName);
        throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
    }
}
