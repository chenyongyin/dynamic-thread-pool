package com.threadpool.db.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threadpool.db.entity.ApplicationInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

/**
 * @author cyy
 * @date 2021/04/12 14:45
 **/
@Repository
public interface ApplicationInfoMapper extends BaseMapper<ApplicationInfo> {

    /**
     * 插入信息
     * @author cyy
     * @date 2021/04/15 13:37
     * @param applicationInfo
     * @return java.lang.String
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "app_id")
    @Insert("insert into application_info (app_id,nacos_data_id,nacos_group,app_name,owner) values (#{id},#{nacosDataId},#{nacosGroup},#{appName},#{owner}) ON DUPLICATE KEY UPDATE nacos_data_id=#{nacosDataId},nacos_group=#{nacosGroup},app_name=#{appName},owner=#{owner}")
    int upsert(ApplicationInfo applicationInfo);

}
