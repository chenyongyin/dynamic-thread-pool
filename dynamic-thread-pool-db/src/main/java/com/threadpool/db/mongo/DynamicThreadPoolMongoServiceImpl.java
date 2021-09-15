package com.threadpool.db.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threadpool.common.inter.ThreadPoolAlarmNotify;
import com.threadpool.common.pojo.AlarmNotifyMessage;
import com.threadpool.db.entity.AlarmRecord;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;
import com.threadpool.db.enums.DynamicThreadPoolStatusEnum;
import com.threadpool.db.service.DynamicThreadPoolDbService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * mongodb 实现
 *
 * @author cyy
 * @date 2021/04/13 15:52
 **/
public class DynamicThreadPoolMongoServiceImpl implements DynamicThreadPoolDbService, ThreadPoolAlarmNotify {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public String insertApplicationInfo(ApplicationInfo applicationInfo) {
        Update update = new Update();
        JSONObject applicationInfoJson = (JSONObject) JSON.toJSON(applicationInfo);
        applicationInfoJson.forEach(update::set);
        UpdateResult updateResult = mongoTemplate.upsert(new Query(Criteria.where("appName").is(applicationInfo.getAppName())), update, APP_INFO_TABLE_NAME);
        return String.valueOf(Objects.requireNonNull(updateResult.getUpsertedId()).asString());

    }

    @Override
    public Page<ApplicationInfo> queryApplicationInfoPage(ApplicationInfo param) {
        Criteria criteria = Criteria.where("is_deleted").is(param.getIsDeleted());
        Page<ApplicationInfo> page = new Page<>();
        page.setRecords(mongoTemplate.find(new Query(criteria).skip((long) (param.getPage() - 1) * param.getLimit()).limit(param.getLimit()), ApplicationInfo.class, APPLICATION_INSTANCE_INFO_TABLE_NAME));
        page.setTotal(mongoTemplate.count(new Query(criteria), APP_INFO_TABLE_NAME));
        return page;
    }

    @Override
    public String insertInstanceInfo(ApplicationInstanceInfo properties) {
        Update update = new Update();
        JSONObject propertiesJson = (JSONObject) JSON.toJSON(properties);
        propertiesJson.forEach(update::set);
        UpdateResult updateResult =  mongoTemplate.upsert(new Query(Criteria.where("instanceIp").is(properties.getInstanceIp()).and("appName").is(properties.getAppName())),update, APPLICATION_INSTANCE_INFO_TABLE_NAME);
        return String.valueOf(Objects.requireNonNull(updateResult.getUpsertedId()).asString());
    }

    @Override
    public ApplicationInstanceInfo queryInstanceInfo(String instanceId) {
        return null;
    }

    @Override
    public Page<ApplicationInstanceInfo> queryInstanceInfoPage(ApplicationInstanceInfo param) {
        Criteria criteria = Criteria.where("is_deleted").is(param.getIsDeleted());
        if (!StringUtils.isEmpty(param.getAppName())) {
            criteria.and("appName").is(param.getAppName());
        }
        if (!StringUtils.isEmpty(param.getStatus())) {
            criteria.and("status").is(param.getStatus());
        }
        if (!StringUtils.isEmpty(param.getAppId())) {
            criteria.and("appId").is(param.getAppId());
        }
        Page<ApplicationInstanceInfo> page = new Page<>();
        page.setRecords(mongoTemplate.find(new Query(criteria).skip((long) (param.getPage() - 1) * param.getLimit()).limit(param.getLimit()), ApplicationInstanceInfo.class, APPLICATION_INSTANCE_INFO_TABLE_NAME));
        page.setTotal(mongoTemplate.count(new Query(criteria), APPLICATION_INSTANCE_INFO_TABLE_NAME));
        return page;
    }

    @Override
    public Map<String,String> insertThreadPoolByBatch(String appInstanceId, List<DynamicThreadPoolInfo> dynamicThreadPoolInfoList) {
        Map<String,String> resultMap = new HashMap<>();
        Criteria criteria = Criteria.where("appInstanceId").is(appInstanceId);
        List<DynamicThreadPoolInfo> threadPoolInfoList = mongoTemplate.find(new Query(criteria), DynamicThreadPoolInfo.class, DYNAMIC_THREAD_POOL_INFO_TABLE_NAME);
        Map<String, DynamicThreadPoolInfo> dynamicThreadPoolInfoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(threadPoolInfoList)) {
            dynamicThreadPoolInfoMap = threadPoolInfoList.stream().collect(Collectors.toMap(DynamicThreadPoolInfo::getThreadPoolName, dynamicThreadPoolInfo -> dynamicThreadPoolInfo));
        }
        List<String> updateList = new ArrayList<>();
        for (DynamicThreadPoolInfo dynamicThreadPoolInfo : dynamicThreadPoolInfoList) {
            DynamicThreadPoolInfo threadPoolInfo = dynamicThreadPoolInfoMap.get(dynamicThreadPoolInfo.getThreadPoolName());
            if (threadPoolInfo != null) {
                resultMap.put(dynamicThreadPoolInfo.getThreadPoolName(),threadPoolInfo.getId());
                updateList.add(threadPoolInfo.getId());
                continue;
            }
            dynamicThreadPoolInfo.setAppInstanceId(appInstanceId);
            mongoTemplate.insert(dynamicThreadPoolInfo);
            resultMap.put(dynamicThreadPoolInfo.getThreadPoolName(),dynamicThreadPoolInfo.getId());

        }
        if (!CollectionUtils.isEmpty(updateList)) {
            Criteria updateCriteria = Criteria.where("threadPoolId").in(updateList);
            Update update = new Update();
            update.set("status", DynamicThreadPoolStatusEnum.RUNNING.name());
            mongoTemplate.upsert(new Query(updateCriteria), update, DYNAMIC_THREAD_POOL_INFO_TABLE_NAME);
        }
        return resultMap;
    }

    @Override
    public boolean updateThreadPoolInfo(DynamicThreadPoolInfo dynamicThreadPoolInfo) {
        Update update = new Update();
        update.set("threadPoolName", dynamicThreadPoolInfo.getThreadPoolName());
        update.set("owner", dynamicThreadPoolInfo.getOwner());
        update.set("corePoolSize", dynamicThreadPoolInfo.getCorePoolSize());
        update.set("maximumPoolSize", dynamicThreadPoolInfo.getMaximumPoolSize());
        update.set("queueCapacity", dynamicThreadPoolInfo.getQueueCapacity());
        update.set("keepAliveTime", dynamicThreadPoolInfo.getKeepAliveTime());
        update.set("queueCapacityThreshold", dynamicThreadPoolInfo.getQueueCapacityThreshold());
        update.set("rejectedExecutionType", dynamicThreadPoolInfo.getRejectedExecutionType());
        update.set("activeRateCapacityThreshold", dynamicThreadPoolInfo.getActiveRateCapacityThreshold());
        update.set("appInstanceId", dynamicThreadPoolInfo.getAppInstanceId());
        update.set("status", dynamicThreadPoolInfo.getStatus());
        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(dynamicThreadPoolInfo.getId())), update, DYNAMIC_THREAD_POOL_INFO_TABLE_NAME).getModifiedCount() > 0;
    }

    @Override
    public int deleteThreadPoolByWhere(DynamicThreadPoolInfo param) {
        return 0;
    }

    @Override
    public DynamicThreadPoolInfo queryThreadPoolInfo(String id) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), DynamicThreadPoolInfo.class, DYNAMIC_THREAD_POOL_INFO_TABLE_NAME);
    }

    @Override
    public Page<DynamicThreadPoolInfo> queryThreadPoolPage(DynamicThreadPoolInfo param) {
        Criteria criteria = Criteria.where("appInstanceId").is(param.getAppInstanceId());
        if (!StringUtils.isEmpty(param.getThreadPoolName())) {
            criteria.and("threadPoolName").is(param.getThreadPoolName());
        }
        Page<DynamicThreadPoolInfo> page = new Page<>();
        page.setRecords(mongoTemplate.find(new Query(criteria).skip((long) (param.getPage() - 1) * param.getLimit()).limit(param.getLimit()), DynamicThreadPoolInfo.class, DYNAMIC_THREAD_POOL_INFO_TABLE_NAME));
        page.setTotal(mongoTemplate.count(new Query(criteria), DYNAMIC_THREAD_POOL_INFO_TABLE_NAME));
        return page;
    }

    @Override
    public void shutDown(String appInstanceId) {
        // 更新应用实例状态
        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(appInstanceId)), new Update().addToSet("status", DynamicThreadPoolStatusEnum.SHUTDOWN.name()), APPLICATION_INSTANCE_INFO_TABLE_NAME);
        // 更新线程池状态
        mongoTemplate.upsert(new Query(Criteria.where("appInstanceId").is(appInstanceId)), new Update().addToSet("status", DynamicThreadPoolStatusEnum.SHUTDOWN.name()), DYNAMIC_THREAD_POOL_INFO_TABLE_NAME);
    }


    @Override
    public Page<AlarmRecord> queryAlarmRecordPage(AlarmRecord alarmRecord) {
        return null;
    }

    @Override
    public void alarmNotify(AlarmNotifyMessage alarmNotifyMessage) {

    }
}
