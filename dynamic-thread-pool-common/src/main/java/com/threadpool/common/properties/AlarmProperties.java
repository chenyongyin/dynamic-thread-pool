package com.threadpool.common.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 预警配置
 *
 * @author cyy
 * @date 2021/04/09 11:45
 **/
@Data
public class AlarmProperties implements Serializable {
    /**
     * 是否开启
     */
    private boolean enabled;
    /**
     * 告警时间间隔，单位分钟
     */
    private int alarmTimeInterval = 1;
    /**
     * 钉钉
     */
    private AlarmDingdingProperties dingDing;
    /**
     * 邮箱
     */
    private AlarmEmailProperties email;
    /**
     * 微信机器人url
     */
    private String wxRobotApiUrl;
    /**
     * url
     */
    private String apiAlarmUrl;
}
