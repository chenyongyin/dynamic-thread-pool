package com.threadpool.db.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threadpool.db.entity.AlarmRecord;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;

import java.util.List;
import java.util.Map;

/**
 * 动态线程池db服务接口
 *
 * @author cyy
 * @date 2021/04/12 11:31
 **/
public interface DynamicThreadPoolDbService {
    /**
     * 应用文档名称
     */
    String APP_INFO_TABLE_NAME = "app_info";
    /**
     * 应用实例文档名称
     */
    String APPLICATION_INSTANCE_INFO_TABLE_NAME = "application_instance_info";
    /**
     * 线程池文档名称
     */
    String DYNAMIC_THREAD_POOL_INFO_TABLE_NAME = "dynamic_thread_pool_info";

    /**
     * 插入应用信息
     * @author cyy
     * @date 2021/04/15 13:25
     * @param applicationInfo applicationInfo
     * @return java.lang.String
     */
    String insertApplicationInfo(ApplicationInfo applicationInfo);
    /**
     *
     * @author cyy
     * @date 2021/04/15 16:09
     * @param param 参数
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.cyy.threadpool.db.entity.ApplicationInfo>
     */
    Page<ApplicationInfo> queryApplicationInfoPage(ApplicationInfo param);
    /**
     * 应用实例
     * @author cyy
     * @date 2021/04/12 14:32
     * @param properties 参数
     * @return java.lang.String
     */
    String insertInstanceInfo(ApplicationInstanceInfo properties);
    /**
     * 查询应用实例信息
     * @author cyy
     * @date 2021/04/16 10:31
     * @param instanceId
     * @return java.lang.String
     */
    ApplicationInstanceInfo queryInstanceInfo(String instanceId);
    /**
     * 查询实例分页数据
     * @author cyy
     * @date 2021/04/12 14:32
     * @param param 参数
     * @return com.cyy.common.base.PageInfo<com.cyy.threadpool.db.entity.InstanceThreadPoolProperties>
     */
    Page<ApplicationInstanceInfo> queryInstanceInfoPage(ApplicationInstanceInfo param);
    /**
     * 批量插入
     * @author cyy
     * @date 2021/04/12 11:46
     * @param appInstanceId 应用实例id
     * @param dynamicThreadPoolInfoList 需要保存的集合
     * @return java.util.ma[<java.lang.String,java.lang.String>
     */
    Map<String,String> insertThreadPoolByBatch(String appInstanceId, List<DynamicThreadPoolInfo> dynamicThreadPoolInfoList);
    /**
     * 更新
     * @author cyy
     * @date 2021/04/12 11:46
     * @param dynamicThreadPoolInfo 线程池信息对象
     * @return boolean
     */
    boolean updateThreadPoolInfo(DynamicThreadPoolInfo dynamicThreadPoolInfo);
    /**
     * 删除
     * @author cyy
     * @date 2021/04/12 11:46
     * @param param 参数
     * @return int
     */
    int deleteThreadPoolByWhere(DynamicThreadPoolInfo param);
    /**
     * 查询单个信息
     * @author cyy
     * @date 2021/04/12 11:46
     * @param id id
     * @return com.cyy.threadpool.db.entity.DynamicThreadPoolInfo
     */
    DynamicThreadPoolInfo queryThreadPoolInfo(String id);
    /**
     * 分页查询
     * @author cyy
     * @date 2021/04/12 11:46
     * @param param 参数
     * @return com.cyy.common.base.PageInfo<com.cyy.threadpool.db.entity.DynamicThreadPoolInfo>
     */
    Page<DynamicThreadPoolInfo> queryThreadPoolPage(DynamicThreadPoolInfo param);
    /**
     * 应用关闭时，调用此方法
     * @author cyy
     * @date 2021/04/12 17:08
     * @param appInstanceId 应用id
     */
    void shutDown(String appInstanceId);
    /**
     * 查询告警记录
     * @author cyy
     * @date 2021/04/16 15:55
     * @param alarmRecord 参数
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.cyy.threadpool.db.entity.AlarmRecord>
     */
    Page<AlarmRecord> queryAlarmRecordPage(AlarmRecord alarmRecord);
}
