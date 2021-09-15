package com.threadpool.db.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.threadpool.db.mongo.DynamicThreadPoolMongoServiceImpl;
import com.threadpool.db.mysql.impl.DynamicThreadPoolMySqlServiceImpl;
import com.threadpool.db.service.DynamicThreadPoolDbService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * db自动注入
 *
 * @author cyy
 * @date 2021/04/12 16:12
 **/
@Configuration
@MapperScan("com.threadpool.db.mysql.mapper")
public class DynamicThreadPoolDbAutoconfigure {

    /**
     * mysql持久化
     * @author cyy
     * @date 2021/04/13 17:25
     * @return com.cyy.threadpool.db.service.DynamicThreadPoolDbService
     */
    @Primary
    @Bean(name = "dynamicThreadPoolMySqlService")
    @ConditionalOnExpression("'${dynamic.threadpools.db.type:}'.equals('mysql')")
    public DynamicThreadPoolDbService dynamicThreadPoolManager() {
        return new DynamicThreadPoolMySqlServiceImpl();
    }

    /**
     * mongo 持久化实现
     * @author cyy
     * @date 2021/04/13 17:26
     * @return com.cyy.threadpool.db.service.DynamicThreadPoolDbService
     */
    @Primary
    @Bean(name = "dynamicThreadPoolMongoService")
    @ConditionalOnExpression("'${dynamic.threadpools.db.type:}'.equals('mongodb')")
    public DynamicThreadPoolDbService dynamicThreadPoolMongoService() {
        return new DynamicThreadPoolMongoServiceImpl();
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
