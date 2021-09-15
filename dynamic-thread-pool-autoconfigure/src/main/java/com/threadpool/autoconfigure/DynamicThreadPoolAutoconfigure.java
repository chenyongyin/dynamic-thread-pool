package com.threadpool.autoconfigure;

import com.threadpool.alarm.DynamicThreadPoolAlarm;
import com.threadpool.common.properties.DynamicThreadPoolProperties;
import com.threadpool.common.properties.AlarmEmailProperties;
import com.threadpool.core.DynamicThreadPoolManager;
import com.threadpool.listener.NacosConfigChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

/**
 * 动态线程池自动注入类
 *
 * @author cyy
 * @date 2021/04/09 10:37
 **/
@Configuration
@EnableConfigurationProperties(DynamicThreadPoolProperties.class)
public class DynamicThreadPoolAutoconfigure {

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    @Value("${nacos.config.server-addr:}")
    private String serverAddr;

    @Value("${nacos.config.namespace:}")
    private String namespace;

    /**
     * 动态线程池管理
     * @author cyy
     * @date 2021/04/13 17:27
     * @return com.cyy.threadpool.core.DynamicThreadPoolManager
     */
    @Bean
    public DynamicThreadPoolManager dynamicThreadPoolManager() {
        return new DynamicThreadPoolManager();
    }
    /**
     * nacos配置变化监听
     * @author cyy
     * @date 2021/04/13 17:27
     * @return com.cyy.threadpool.listener.NacosConfigChangeListener
     */
    @ConditionalOnProperty(value = "dynamic.threadpools.nacosGroup",havingValue = "dynamic_thread_pool")
    @Bean
    public NacosConfigChangeListener nacosConfigChangeListener() {
        return new NacosConfigChangeListener(serverAddr,namespace);
    }

    /**
     * 邮件发送操作对象
     * @author cyy
     * @date 2021/04/13 17:28
     * @return org.springframework.mail.javamail.JavaMailSenderImpl
     */
    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    @ConditionalOnProperty(value = "dynamic.threadpools.alarm.email")
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(dynamicThreadPoolProperties.getAlarm().getEmail(), sender);
        return sender;
    }
    /**
     * 线程池告警服务
     * @author cyy
     * @date 2021/04/13 17:28
     * @return com.cyy.threadpool.alarm.DynamicThreadPoolAlarm
     */
    @Primary
    @Bean
    @ConditionalOnProperty(value = "dynamic.threadpools.alarm.enabled",havingValue = "true")
    public DynamicThreadPoolAlarm dynamicThreadPoolAlarm() {
        return new DynamicThreadPoolAlarm();
    }


    private void applyProperties(AlarmEmailProperties alarmEmailProperties, JavaMailSenderImpl sender) {
        sender.setHost(alarmEmailProperties.getHost());
        if (alarmEmailProperties.getPort() != null) {
            sender.setPort(alarmEmailProperties.getPort());
        }
        sender.setUsername(alarmEmailProperties.getUsername());
        sender.setPassword(alarmEmailProperties.getPassword());
        sender.setProtocol(alarmEmailProperties.getProtocol());
        if (alarmEmailProperties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(alarmEmailProperties.getDefaultEncoding().name());
        }
        if (!alarmEmailProperties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(alarmEmailProperties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

}
