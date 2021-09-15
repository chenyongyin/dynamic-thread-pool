package com.threadpool.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.threadpool.common.properties.DynamicThreadPoolProperties;
import com.threadpool.core.DynamicThreadPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * nacos配置中心数据变动监听
 *
 * @author cyy
 * @date 2021/04/09 10:34
 **/
@Slf4j
public class NacosConfigChangeListener {

    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    private final String serverAddr;

    private final String namespace;

    public NacosConfigChangeListener(String serverAddr,String namespace){
        this.serverAddr = serverAddr;
        this.namespace = namespace;
    }

    @PostConstruct
    public void init() {
        initConfigUpdateListener();
    }

    public void initConfigUpdateListener() {
        Assert.hasText(serverAddr, "nacos.config.server-addr");
        Assert.hasText(dynamicThreadPoolProperties.getNacosDataId(), "dynamic.threadpools.nacosDataId");
        Assert.hasText(dynamicThreadPoolProperties.getNacosGroup(), "dynamic.threadpools.nacosGroup");

        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", StringUtils.isEmpty(namespace) ? "public":namespace);
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            configService.addListener(dynamicThreadPoolProperties.getNacosDataId(), dynamicThreadPoolProperties.getNacosGroup(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    dynamicThreadPoolProperties.refreshYaml(configInfo);
                    dynamicThreadPoolManager.refreshThreadPoolExecutor(true);
                    log.info("线程池配置有变化，刷新完成:"+ JSON.toJSONString(dynamicThreadPoolProperties));
                }
            });
        } catch (NacosException e) {
            log.error("Nacos配置监听异常", e);
        }
    }
}
