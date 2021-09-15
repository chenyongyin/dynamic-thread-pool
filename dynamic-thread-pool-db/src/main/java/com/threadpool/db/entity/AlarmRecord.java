package com.threadpool.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 告警记录
 *
 * @author cyy
 * @date 2021/04/16 15:42
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("alarm_record")
public class AlarmRecord extends BasePageReq {

    @TableId("alarm_id")
    private String id;
    @TableField("app_name")
    private String appName;

    private String threadPoolName;

    private String owner;

    private String instanceIp;

    private String alarmType;

    private String reason;

    private String instanceId;

    private String createTime;

    private Integer isDeleted;

    private String threadPoolIndicator;

    private String alarmMessage;

    @TableField(exist = false)
    private int alarmTimeInterval;

    public AlarmRecord() {

    }

    public AlarmRecord(String appName, String threadName, String owner, String instanceIp, String reason, int alarmTimeInterval,String threadPoolIndicator) {
        this.appName = appName;
        this.threadPoolName = threadName;
        this.owner = owner;
        this.alarmTimeInterval = alarmTimeInterval;
        this.reason = reason;
        this.instanceIp = instanceIp;
        this.threadPoolIndicator = threadPoolIndicator;
    }

    public String getAlarmMessageStr() {
        return "[告警应用]:" + this.appName + "\n" +
                "[应用ip]:" + this.instanceIp + "\n" +
                "[线程池名称]:" + this.threadPoolName + "\n" +
                "[告警原因]:" + this.reason + "\n" +
                "[线程池参数]:" + threadPoolIndicator +
                "[业务负责人]:" + this.owner + "\n" +
                "[告警间隔]:" + this.alarmTimeInterval + "分钟\n";
    }
}
