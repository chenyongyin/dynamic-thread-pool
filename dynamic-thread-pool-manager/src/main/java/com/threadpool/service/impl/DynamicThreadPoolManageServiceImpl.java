package com.threadpool.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threadpool.common.properties.AlarmProperties;
import com.threadpool.common.properties.DbTypeProperties;
import com.threadpool.common.properties.DynamicThreadPoolProperties;
import com.threadpool.common.properties.ThreadPoolProperties;
import com.threadpool.common.utils.BeanUtils;
import com.threadpool.common.utils.YamlUtil;
import com.threadpool.db.entity.AlarmRecord;
import com.threadpool.db.entity.ApplicationInfo;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import com.threadpool.db.entity.DynamicThreadPoolInfo;
import com.threadpool.db.service.DynamicThreadPoolDbService;
import com.threadpool.factory.NacosConfigServiceFactory;
import com.threadpool.service.DynamicThreadPoolManageService;
import com.threadpool.vo.ResultModelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyy
 * @date 2021/04/15 20:25
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class DynamicThreadPoolManageServiceImpl implements DynamicThreadPoolManageService {

    private final DynamicThreadPoolDbService dynamicThreadPoolDbService;

    @Override
    public ResultModelVo<List<ApplicationInfo>> appList(ApplicationInfo param) {
        Page<ApplicationInfo> pageInfo = dynamicThreadPoolDbService.queryApplicationInfoPage(param);
        return new ResultModelVo<>(pageInfo.getTotal(),pageInfo.getRecords());
    }

    @Override
    public ResultModelVo<List<ApplicationInstanceInfo>> appInstanceList(ApplicationInstanceInfo param) {
        Page<ApplicationInstanceInfo> pageInfo = dynamicThreadPoolDbService.queryInstanceInfoPage(param);
        return new ResultModelVo<>(pageInfo.getTotal(),pageInfo.getRecords());
    }

    @Override
    public ResultModelVo<List<DynamicThreadPoolInfo>> threadPoolList(DynamicThreadPoolInfo param) {
        Page<DynamicThreadPoolInfo> pageInfo = dynamicThreadPoolDbService.queryThreadPoolPage(param);
        return new ResultModelVo<>(pageInfo.getTotal(),pageInfo.getRecords());
    }

    @Override
    public ResultModelVo<Object> updateDynamicThreadPoolInfo(DynamicThreadPoolInfo param) {
        // 根据appInstanceId查询应用实例信息
        ApplicationInstanceInfo applicationInstanceInfo = dynamicThreadPoolDbService.queryInstanceInfo(param.getAppInstanceId());
        List<ThreadPoolProperties> threadPoolProperties = JSON.parseArray(applicationInstanceInfo.getExecutors(), ThreadPoolProperties.class);
        AlarmProperties alarm = JSON.parseObject(applicationInstanceInfo.getAlarm(),AlarmProperties.class);
        for (ThreadPoolProperties threadPoolProperty : threadPoolProperties) {
            if(threadPoolProperty.getThreadPoolName().equals(param.getThreadPoolName())){
                BeanUtils.copyIgnoreNullProperties(param,threadPoolProperty);
                break;
            }
        }
        DynamicThreadPoolProperties dynamicThreadPoolProperties = new DynamicThreadPoolProperties();
        dynamicThreadPoolProperties.setAlarm(alarm);
        dynamicThreadPoolProperties.setExecutors(threadPoolProperties);
        dynamicThreadPoolProperties.setNacosDataId(applicationInstanceInfo.getNacosDataId());
        dynamicThreadPoolProperties.setNacosGroup(applicationInstanceInfo.getNacosGroup());
        dynamicThreadPoolProperties.setDb(JSON.parseObject(applicationInstanceInfo.getDb(),DbTypeProperties.class));
        Map<String, Object> dynamicThreadPoolMap = JSON.parseObject(JSON.toJSONString(dynamicThreadPoolProperties), new TypeReference<Map<String, Object>>(){});
        Map<String,Object> dynamicMap = new HashMap<>();
        Map<String,Object> threadPoolsMap = new HashMap<>();
        threadPoolsMap.put("threadpools",dynamicThreadPoolMap);
        dynamicMap.put("dynamic",threadPoolsMap);
        String yamlStr = YamlUtil.multilayerMapToYaml(dynamicMap);
        log.info("yamlStr\n{}",yamlStr);
        try {
            boolean publishState = NacosConfigServiceFactory.getNacosConfigService(applicationInstanceInfo.getNacosAddress(),applicationInstanceInfo.getNacosNamespace())
                    .publishConfig(applicationInstanceInfo.getNacosDataId(), applicationInstanceInfo.getNacosGroup(), yamlStr, ConfigType.YAML.getType());
            log.info("发布状态:{}",publishState);
        } catch (NacosException e) {
            log.error("发布到nacos异常",e);
        }
        dynamicThreadPoolDbService.updateThreadPoolInfo(param);
        ResultModelVo<Object> resultModelVo = new ResultModelVo<>();
        resultModelVo.setCode("0");
        return resultModelVo;
    }

    @Override
    public ResultModelVo<List<AlarmRecord>> queryAlarmRecordList(AlarmRecord param) {
        Page<AlarmRecord> pageInfo = dynamicThreadPoolDbService.queryAlarmRecordPage(param);
        return new ResultModelVo<>(pageInfo.getTotal(),pageInfo.getRecords());
    }

}
