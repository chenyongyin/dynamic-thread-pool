package com.threadpool.common.pojo;

import com.threadpool.common.enums.AlarmTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cyy
 * @date 2021/04/17 10:56
 **/
@AllArgsConstructor
@Data
public class AlarmNotifyMessage implements Serializable {
    /**
     * 应用名
     */
    private String appName;
    /**
     * 线程池名称
     */
    private String threadPoolName;
    /**
     * 负责人
     */
    private String owner;
    /**
     * 时间间隔（分钟）
     */
    private int alarmTimeInterval;
    /**
     * 告警类型
     */
    private AlarmTypeEnum alarmType;
    /**
     * 告警原因
     */
    private String reason;
    /**
     * 告警消息
     */
    private String alarmMessage;
    /**
     * 线程池相关信息
     */
    private ThreadPoolIndicatorInfo threadPoolIndicatorInfo;
    /**
     * ip
     */
    private String instanceIp;

    public String getAlarmMessage(){
        return "[告警应用]:" + this.appName + "\n" +
                "[应用ip]:" + this.instanceIp + "\n" +
                "[线程池名称]:" + this.threadPoolName + "\n" +
                "[告警原因]:" + this.reason + "\n" +
                "[线程池参数]:" + threadPoolIndicatorInfo.toString() +
                "[业务负责人]:" + this.owner + "\n" +
                "[告警间隔]:" + this.alarmTimeInterval + "分钟\n";
    }
}
