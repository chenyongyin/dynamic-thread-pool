package com.threadpool.core;

import com.threadpool.db.service.DynamicThreadPoolDbService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author cyy
 * @date 2021/04/12 17:01
 **/
@RequiredArgsConstructor
public class ShutdownListenerThread extends Thread{


    private final DynamicThreadPoolDbService dynamicThreadPoolDbService;

    private final String appInstanceId;

    private final List<String> threadPoolIdList;

    @Override
    public void run(){
        System.out.println("执行ShutdownListenerThread");
        dynamicThreadPoolDbService.shutDown(appInstanceId);
    }
}
