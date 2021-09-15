package com.threadpool.alarm;

import com.threadpool.common.properties.AlarmProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 告警管理类
 *
 * @author cyy
 * @date 2021/04/09 18:37
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class AlarmMessage extends AlarmProperties {

    /**
     * 告警名称，区分唯一性，方便控制告警时间间隔
     */
    private String alarmName;

    /**
     * 预警信息
     */
    private String message;
}
