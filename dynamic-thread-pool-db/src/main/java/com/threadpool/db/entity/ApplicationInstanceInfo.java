package com.threadpool.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 应用实例动态线程池配置entity
 *
 * @author cyy
 * @date 2021/04/12 14:25
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("application_instance_info")
public class ApplicationInstanceInfo extends BasePageReq implements Serializable {
    @TableId("instance_id")
    private String id;

    private String nacosAddress;

    private String nacosNamespace;

    private String nacosDataId;

    private String nacosGroup;

    private String appName;

    private String db;

    private String alarm;

    private String executors;

    private String instanceIp;

    private String status;

    private Integer isDeleted = 0;

    private String createTime;

    private String appId;

}
