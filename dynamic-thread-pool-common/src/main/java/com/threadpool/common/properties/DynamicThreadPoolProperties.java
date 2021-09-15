package com.threadpool.common.properties;

import lombok.Data;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * 动态线程池配置类
 *
 * @author cyy
 * @date 2021/04/09 10:38
 **/
@Data
@ConfigurationProperties(prefix = "dynamic.threadpools")
public class DynamicThreadPoolProperties implements Serializable {
    /**
     * Nacos DataId, 监听配置修改用
     */
    private String nacosDataId;

    /**
     * Nacos Group, 监听配置修改用
     */
    private String nacosGroup;

    /**
     * Nacos 等待配置刷新时间间隔（监听器收到消息变更通知，此时Spring容器中的配置bean还没更新，需要等待固定的时间）
     */
    private int nacosWaitRefreshConfigSeconds = 1;

    /**
     * 持久化类型
     */
    private DbTypeProperties db;

    /**
     * 预警配置
     */
    private AlarmProperties alarm;

    /**
     * 线程池配置
     */
    private List<ThreadPoolProperties> executors = new ArrayList<>();

    /**
     * 刷新配置
     * @param content 整个配置文件的内容
     */
    public void refresh(String content) {
        Properties properties =  new Properties();
        try {
            properties.load(new ByteArrayInputStream(content.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        doRefresh(properties);
    }

    public void refreshYaml(String content) {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ByteArrayResource(content.getBytes()));
        Properties properties = bean.getObject();
        doRefresh(properties);
    }

    private void doRefresh(Properties properties) {
        Map<String, String> dataMap = new HashMap<String, String>((Map) properties);
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(dataMap);
        Binder binder = new Binder(sources);
        binder.bind("dynamic.threadpools", Bindable.ofInstance(this)).get();
    }
}
