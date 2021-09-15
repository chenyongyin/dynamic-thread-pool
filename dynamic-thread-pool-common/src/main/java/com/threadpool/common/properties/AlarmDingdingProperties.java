package com.threadpool.common.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 钉钉预警
 *
 * @author cyy
 * @date 2021/04/09 11:42
 **/
@Data
public class AlarmDingdingProperties implements Serializable {
    /**
     * 钉钉机器人access_token
     */
    private String accessToken;

    /**
     * 钉钉机器人secret
     */
    private String secret;
}
