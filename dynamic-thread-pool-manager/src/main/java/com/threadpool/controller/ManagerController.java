package com.threadpool.controller;

import com.threadpool.core.DynamicThreadPoolManager;
import com.threadpool.db.entity.AlarmRecord;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;
import com.threadpool.service.DynamicThreadPoolManageService;
import com.threadpool.vo.ResultModelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cyy
 * @date 2021/04/14 15:43
 **/
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/manager/")
public class ManagerController {
    private final DynamicThreadPoolManageService dynamicThreadPoolManageService;

    private final DynamicThreadPoolManager dynamicThreadPoolManager;

    @RequestMapping("appList")
    @ResponseBody
    public ResultModelVo<List<ApplicationInfo>> appList(ApplicationInfo param){
        return dynamicThreadPoolManageService.appList(param);
    }

    @RequestMapping("appInstanceList")
    @ResponseBody
    public ResultModelVo<List<ApplicationInstanceInfo>> appInstanceList(ApplicationInstanceInfo param){
        return dynamicThreadPoolManageService.appInstanceList(param);
    }

    @RequestMapping("threadPoolList")
    @ResponseBody
    public ResultModelVo<List<DynamicThreadPoolInfo>> threadPoolList(DynamicThreadPoolInfo param){
        return dynamicThreadPoolManageService.threadPoolList(param);
    }

    @PostMapping("updateDynamicThreadPoolInfo")
    @ResponseBody
    public ResultModelVo<Object> updateDynamicThreadPoolInfo(@RequestBody DynamicThreadPoolInfo param){
        return dynamicThreadPoolManageService.updateDynamicThreadPoolInfo(param);
    }

    @GetMapping("queryAlarmRecordList")
    @ResponseBody
    public ResultModelVo<List<AlarmRecord>> queryAlarmList(AlarmRecord param){
        return dynamicThreadPoolManageService.queryAlarmRecordList(param);
    }

    @RequestMapping("task")
    @ResponseBody
    public ResultModelVo<Object> task(int num){
        for (int i = 0; i < num; i++) {
            dynamicThreadPoolManager.getThreadPoolExecutor("test-fans-thread-pool").execute(()->{
                try {
                    Thread.sleep(1000*20);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            });
        }
        return null;
    }

}
