package com.threadpool.core.rejected;

import com.threadpool.indicator.ThreadPoolIndicator;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 拒绝策略抽象类
 *
 * @author cyy
 * @date 2021/04/09 14:39
 **/
public abstract class AbstractRejectedExecutionHandler implements RejectedExecutionHandler {
    /**
     * 咋鞥家拒绝次数
     * @author cyy
     * @date 2021/04/09 15:19
     * @param threadPoolName 线程池名称
     */
    void incrRejectNum(String threadPoolName){
        ThreadPoolIndicator.incrRejectCount(threadPoolName);
    }

}
