package com.threadpool.db.mysql.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threadpool.common.inter.ThreadPoolAlarmNotify;
import com.threadpool.common.inter.ThreadPoolIndicatorNotify;
import com.threadpool.common.pojo.AlarmNotifyMessage;
import com.threadpool.common.pojo.ThreadPoolIndicatorInfo;
import com.threadpool.common.utils.IpUtils;
import com.threadpool.db.mysql.mapper.AlarmRecordMapper;
import com.threadpool.db.mysql.mapper.ApplicationInfoMapper;
import com.threadpool.db.mysql.mapper.ApplicationInstanceMapper;
import com.threadpool.db.entity.AlarmRecord;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;
import com.threadpool.db.enums.DynamicThreadPoolStatusEnum;
import com.threadpool.db.mysql.mapper.DynamicThreadPoolInfoMapper;
import com.threadpool.db.service.DynamicThreadPoolDbService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * mysql db 实现
 *
 * @author cyy
 * @date 2021/04/12 11:57
 **/
public class DynamicThreadPoolMySqlServiceImpl implements DynamicThreadPoolDbService,ThreadPoolAlarmNotify, ThreadPoolIndicatorNotify {

    @Autowired
    private ApplicationInstanceMapper applicationInstanceMapper;
    @Autowired
    private DynamicThreadPoolInfoMapper dynamicThreadPoolInfoMapper;
    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;
    @Autowired
    private AlarmRecordMapper alarmRecordMapper;

    @Override
    public String insertApplicationInfo(ApplicationInfo param) {
        param.setId(UUID.randomUUID().toString());
        applicationInfoMapper.upsert(param);
        QueryWrapper<ApplicationInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_name",param.getAppName());
        ApplicationInfo applicationInfo = applicationInfoMapper.selectOne(queryWrapper);
        if(applicationInfo != null){
            return applicationInfo.getId();
        }
        applicationInfoMapper.insert(param);
        return param.getId();
    }

    @Override
    public Page<ApplicationInfo> queryApplicationInfoPage(ApplicationInfo param) {
        QueryWrapper<ApplicationInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted",0).orderByDesc("create_time");
        Page<ApplicationInfo> page = new Page<>();
        page.setCurrent(param.getPage());
        page.setSize(param.getLimit());
        return applicationInfoMapper.selectPage(page, queryWrapper);
    }

    @Override
    public String insertInstanceInfo(ApplicationInstanceInfo properties) {
        // 根据ip和appName查询，如果存在则返回 否则插入
        QueryWrapper<ApplicationInstanceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("instance_ip",properties.getInstanceIp()).eq("app_name",properties.getAppName()).eq("app_id",properties.getAppId());
        ApplicationInstanceInfo instanceThreadPoolProperties = applicationInstanceMapper.selectOne(queryWrapper);
        if(instanceThreadPoolProperties != null){
            instanceThreadPoolProperties.setStatus(DynamicThreadPoolStatusEnum.RUNNING.name());
            applicationInstanceMapper.updateById(instanceThreadPoolProperties);
            return instanceThreadPoolProperties.getId();
        }
        properties.setId(UUID.randomUUID().toString());
        applicationInstanceMapper.insert(properties);
        return properties.getId();
    }

    @Override
    public ApplicationInstanceInfo queryInstanceInfo(String instanceId) {
        return applicationInstanceMapper.selectById(instanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<ApplicationInstanceInfo> queryInstanceInfoPage(ApplicationInstanceInfo param) {
        QueryWrapper<ApplicationInstanceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted",0).eq(!StringUtils.isEmpty(param.getAppName()),"app_name",param.getAppName())
                .eq(!StringUtils.isEmpty(param.getStatus()),"status",param.getStatus())
                .eq(!StringUtils.isEmpty(param.getAppId()),"app_id",param.getAppId());
        Page<ApplicationInstanceInfo> page = new Page<>();
        page.setCurrent(param.getPage());
        page.setSize(param.getLimit());
        return applicationInstanceMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Map<String,String> insertThreadPoolByBatch(String appInstanceId,List<DynamicThreadPoolInfo> dynamicThreadPoolInfoList) {
        // 先根据appInstanceId查询
        QueryWrapper<DynamicThreadPoolInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted",0).eq("app_instance_id",appInstanceId);
        List<DynamicThreadPoolInfo> threadPoolInfoList = dynamicThreadPoolInfoMapper.selectList(queryWrapper);
        Map<String, DynamicThreadPoolInfo> dynamicThreadPoolInfoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(threadPoolInfoList)){
            dynamicThreadPoolInfoMap = threadPoolInfoList.stream().collect(Collectors.toMap(DynamicThreadPoolInfo::getThreadPoolName, dynamicThreadPoolInfo -> dynamicThreadPoolInfo));
        }
        Map<String,String> resultMap = new HashMap<>();
        List<String> updateIdList = new ArrayList<>();
        for (DynamicThreadPoolInfo dynamicThreadPoolInfo : dynamicThreadPoolInfoList) {
            DynamicThreadPoolInfo threadPoolInfo = dynamicThreadPoolInfoMap.get(dynamicThreadPoolInfo.getThreadPoolName());
            if(threadPoolInfo !=null){
                resultMap.put(threadPoolInfo.getThreadPoolName(),threadPoolInfo.getId());
                updateIdList.add(threadPoolInfo.getId());
                continue;
            }
            String id = UUID.randomUUID().toString();
            dynamicThreadPoolInfo.setId(id);
            dynamicThreadPoolInfo.setAppInstanceId(appInstanceId);
            dynamicThreadPoolInfoMapper.insert(dynamicThreadPoolInfo);
            resultMap.put(dynamicThreadPoolInfo.getThreadPoolName(),dynamicThreadPoolInfo.getId());;
        }
        if(!CollectionUtils.isEmpty(updateIdList)){
            // 更新线程池运行状态
            UpdateWrapper<DynamicThreadPoolInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("thread_pool_id",updateIdList);
            DynamicThreadPoolInfo dynamicThreadPoolInfo = new DynamicThreadPoolInfo();
            dynamicThreadPoolInfo.setStatus(DynamicThreadPoolStatusEnum.RUNNING.name());
            dynamicThreadPoolInfoMapper.update(dynamicThreadPoolInfo,updateWrapper);
        }
        return resultMap;
    }

    @Override
    public boolean updateThreadPoolInfo(DynamicThreadPoolInfo dynamicThreadPoolInfo) {
        return dynamicThreadPoolInfoMapper.updateById(dynamicThreadPoolInfo) > 0;
    }

    @Override
    public int deleteThreadPoolByWhere(DynamicThreadPoolInfo param) {
        return 0;
    }

    @Override
    public DynamicThreadPoolInfo queryThreadPoolInfo(String id) {
        return dynamicThreadPoolInfoMapper.selectById(id);
    }

    @Override
    public Page<DynamicThreadPoolInfo> queryThreadPoolPage(DynamicThreadPoolInfo param) {
        QueryWrapper<DynamicThreadPoolInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isEmpty(param.getAppInstanceId()),"app_instance_id",param.getAppInstanceId())
                .eq(!StringUtils.isEmpty(param.getThreadPoolName()),"thread_pool_name",param.getThreadPoolName())
                .orderByDesc("create_time");
        Page<DynamicThreadPoolInfo> page = new Page<>();
        page.setCurrent(param.getPage());
        page.setSize(param.getLimit());
        return dynamicThreadPoolInfoMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void shutDown(String appInstanceId) {
        // 更新应用实例状态
        ApplicationInstanceInfo instanceThreadPoolProperties = new ApplicationInstanceInfo();
        instanceThreadPoolProperties.setStatus(DynamicThreadPoolStatusEnum.SHUTDOWN.name());
        instanceThreadPoolProperties.setId(appInstanceId);
        applicationInstanceMapper.updateById(instanceThreadPoolProperties);
        // 更新线程池状态
        UpdateWrapper<DynamicThreadPoolInfo> dynamicThreadPoolInfoUpdateWrapper = new UpdateWrapper<>();
        dynamicThreadPoolInfoUpdateWrapper.eq("app_instance_id",appInstanceId);
        DynamicThreadPoolInfo dynamicThreadPoolInfo = new DynamicThreadPoolInfo();
        dynamicThreadPoolInfo.setStatus(DynamicThreadPoolStatusEnum.SHUTDOWN.name());
        dynamicThreadPoolInfoMapper.update(dynamicThreadPoolInfo,dynamicThreadPoolInfoUpdateWrapper);
    }

    @Override
    public Page<AlarmRecord> queryAlarmRecordPage(AlarmRecord alarmRecord) {
        QueryWrapper<AlarmRecord> queryWrapper = new QueryWrapper<>();
        Page<AlarmRecord> page = new Page<>();
        page.setCurrent(alarmRecord.getPage());
        page.setSize(alarmRecord.getLimit());
        queryWrapper.likeRight(!StringUtils.isEmpty(alarmRecord.getAppName()),"app_name",alarmRecord.getAppName())
                .likeRight(!StringUtils.isEmpty(alarmRecord.getThreadPoolName()),"thread_pool_name",alarmRecord.getThreadPoolName())
                .eq(!StringUtils.isEmpty(alarmRecord.getOwner()),"owner",alarmRecord.getOwner())
                .orderByDesc("create_time");
        return alarmRecordMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void alarmNotify(AlarmNotifyMessage alarmNotifyMessage) {
        AlarmRecord alarmRecord = new AlarmRecord();
        BeanUtils.copyProperties(alarmNotifyMessage,alarmRecord);
        alarmRecord.setInstanceIp(IpUtils.getHostIp());
        alarmRecord.setAlarmType(alarmNotifyMessage.getAlarmType().name());
        alarmRecord.setId(UUID.randomUUID().toString());
        alarmRecord.setThreadPoolIndicator(alarmNotifyMessage.getThreadPoolIndicatorInfo().toString());
        alarmRecordMapper.insert(alarmRecord);
    }

    @Override
    public void indicatorNotify(List<ThreadPoolIndicatorInfo> indicatorInfoList) {
        DynamicThreadPoolInfo dynamicThreadPoolInfo;
        for (ThreadPoolIndicatorInfo threadPoolIndicatorInfo : indicatorInfoList) {
            dynamicThreadPoolInfo = new DynamicThreadPoolInfo();
            dynamicThreadPoolInfo.setThreadPoolIndicatorInfo(JSON.toJSONString(threadPoolIndicatorInfo));
            dynamicThreadPoolInfo.setId(threadPoolIndicatorInfo.getThreadPoolId());
            dynamicThreadPoolInfoMapper.updateById(dynamicThreadPoolInfo);
        }
    }
}
