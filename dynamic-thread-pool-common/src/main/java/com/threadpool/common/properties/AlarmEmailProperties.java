package com.threadpool.common.properties;

import lombok.Data;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱预警
 *
 * @author cyy
 * @date 2021/04/09 11:41
 **/
@Data
public class AlarmEmailProperties implements Serializable {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 接收人
     */
    private String[] toUsers;

    /**
     * 协议
     */
    private String protocol = "smtp";

    /**
     * 编码
     */
    private Charset defaultEncoding = DEFAULT_CHARSET;

    /**
     * 其他配置
     */
    private Map<String, String> properties = new HashMap<>();

}
