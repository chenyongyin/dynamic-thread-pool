package com.threadpool.service;

import com.threadpool.db.entity.AlarmRecord;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;
import com.threadpool.vo.ResultModelVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface DynamicThreadPoolManageService {
    /**
     * 应用列表
     * @author cyy
     * @date 2021/04/15 22:10
     * @param param 入参
     * @return com.cyy.threadpool.vo.ResultModelVo<java.util.List<com.cyy.threadpool.db.entity.ApplicationInfo>>
     */
    ResultModelVo<List<ApplicationInfo>> appList(ApplicationInfo param);
    /**
     * 应用实例
     * @author cyy
     * @date 2021/04/15 22:10
     * @param param 入参
     * @return com.cyy.threadpool.vo.ResultModelVo<java.util.List<com.cyy.threadpool.db.entity.ApplicationInstanceInfo>>
     */
    ResultModelVo<List<ApplicationInstanceInfo>> appInstanceList(ApplicationInstanceInfo param);
    /**
     * 
     * @author cyy
     * @date 2021/04/15 22:10
     * @param param 入参
     * @return com.cyy.threadpool.vo.ResultModelVo<java.util.List<com.cyy.threadpool.db.entity.DynamicThreadPoolInfo>>
     */
    ResultModelVo<List<DynamicThreadPoolInfo>> threadPoolList(DynamicThreadPoolInfo param);
    /**
     * 更新线程池信息
     * @author cyy
     * @date 2021/04/15 22:10
     * @param param 入参
     * @return com.cyy.threadpool.vo.ResultModelVo<java.lang.Object>
     */
    ResultModelVo<Object> updateDynamicThreadPoolInfo(@RequestBody DynamicThreadPoolInfo param);
    /**
     * 查询告警列表
     * @author cyy
     * @date 2021/04/16 17:57
     * @param param
     * @return com.cyy.threadpool.vo.ResultModelVo<java.util.List<com.cyy.threadpool.db.entity.AlarmRecord>>
     */
    ResultModelVo<List<AlarmRecord>> queryAlarmRecordList(@RequestBody AlarmRecord param);

}
