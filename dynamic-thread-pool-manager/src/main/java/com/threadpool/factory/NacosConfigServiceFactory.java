package com.threadpool.factory;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cyy
 * @date 2021/04/16 10:42
 **/
public class NacosConfigServiceFactory {


    private static final Map<Integer,ConfigService> CONFIG_SERVICE_MAP = new ConcurrentHashMap<>();

    public static ConfigService getNacosConfigService(String serverAddr,String namespace) throws NacosException {
        Integer key = serverAddr.hashCode();
        if(CONFIG_SERVICE_MAP.containsKey(key)){
            return CONFIG_SERVICE_MAP.get(key);
        }
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace",namespace);
        ConfigService configService = NacosFactory.createConfigService(properties);
        CONFIG_SERVICE_MAP.put(key,configService);
        return configService;
    }

}
