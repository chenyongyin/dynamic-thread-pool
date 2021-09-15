package com.threadpool.alarm;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 企业微信机器人消息
 *
 * @author cyy
 * @date 2021/04/09 19:05
 **/
@AllArgsConstructor
@Data
public class WxRobotAlarmMessage {
    @JSONField(name = "msgtype")
    private String msgtype;

    private TextContent text;

    @Data
    public static class TextContent{

        private String content;

        private List<String> mentioned_list;

        private List<String> mentioned_mobile_list;
    }
}
