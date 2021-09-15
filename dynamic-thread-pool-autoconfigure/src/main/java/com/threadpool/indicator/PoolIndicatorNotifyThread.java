package com.threadpool.indicator;

import com.threadpool.common.constant.ThreadPoolConstant;
import com.threadpool.common.inter.ThreadPoolIndicatorNotify;
import com.threadpool.common.properties.DynamicThreadPoolProperties;
import com.threadpool.core.DynamicThreadPoolManager;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cyy
 * @date 2021/04/17 10:36
 **/
public class PoolIndicatorNotifyThread extends Thread{

    private final DynamicThreadPoolProperties dynamicThreadPoolProperties;

    private final DynamicThreadPoolManager dynamicThreadPoolManager;

    public static final List<ThreadPoolIndicatorNotify> THREAD_POOL_INDICATOR_NOTIFIES = new ArrayList<>();

    public PoolIndicatorNotifyThread(DynamicThreadPoolProperties dynamicThreadPoolProperties,DynamicThreadPoolManager dynamicThreadPoolManager){
        this.dynamicThreadPoolProperties = dynamicThreadPoolProperties;
        this.dynamicThreadPoolManager = dynamicThreadPoolManager;
    }

    @Override
    public void run(){
        if(CollectionUtils.isEmpty(THREAD_POOL_INDICATOR_NOTIFIES)){
            return;
        }
        while(true){
            for (ThreadPoolIndicatorNotify threadPoolIndicatorNotify : THREAD_POOL_INDICATOR_NOTIFIES) {
                threadPoolIndicatorNotify.indicatorNotify(dynamicThreadPoolManager.getAllThreadPoolIndicator());
            }
            try {
                Thread.sleep(ThreadPoolConstant.THREAD_POOL_INDICATOR_REFRESH_TIME_INTERVAL);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

    }

}
