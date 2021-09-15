package com.threadpool.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用实例动态线程池配置entity
 *
 * @author cyy
 * @date 2021/04/12 14:25
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("application_info")
public class ApplicationInfo extends BasePageReq{
    @TableId("app_id")
    private String id;

    private String nacosDataId;

    private String nacosGroup;

    private String appName;

    private Integer isDeleted = 0;

    private String createTime;

    private String owner;

    @TableField(exist = false)
    private String instanceNum;

}
