package com.threadpool.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.threadpool.common.inter.ThreadPoolAlarmNotify;
import com.threadpool.common.pojo.AlarmNotifyMessage;
import com.threadpool.common.properties.AlarmDingdingProperties;
import com.threadpool.common.properties.AlarmEmailProperties;
import com.threadpool.common.properties.AlarmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.*;

/**
 * 告警管理类
 *
 * @author cyy
 * @date 2021/04/09 18:42
 **/
@Slf4j
public class DefaultThreadPoolAlarmNotify implements ThreadPoolAlarmNotify {

    private final JavaMailSenderImpl mailSender;


    private final AlarmProperties alarmProperties;

    public DefaultThreadPoolAlarmNotify(JavaMailSenderImpl mailSender,AlarmProperties alarmProperties){
        this.mailSender = mailSender;
        this.alarmProperties = alarmProperties;
    }
    /**
     * 检查邮件参数是否合规
     * @author cyy
     * @date 2021/04/09 21:14
     * @param alarmEmailProperties 邮件参数
     * @return boolean
     */
    public static boolean checkEmail(AlarmEmailProperties alarmEmailProperties){
        return alarmEmailProperties != null && StringUtils.isNotEmpty(alarmEmailProperties.getHost())
                && StringUtils.isNotEmpty(alarmEmailProperties.getUsername())
                && StringUtils.isNotEmpty(alarmEmailProperties.getPassword())
                && alarmEmailProperties.getPort() != null
                && alarmEmailProperties.getToUsers() != null && alarmEmailProperties.getToUsers().length > 0;
    }
    /**
     * 检查钉钉配置参数是否合规
     * @author cyy
     * @date 2021/04/09 21:14
     * @param alarmDingdingProperties 邮件参数
     * @return boolean
     */
    private static boolean checkDingDing(AlarmDingdingProperties alarmDingdingProperties){
        return alarmDingdingProperties != null && StringUtils.isNotEmpty(alarmDingdingProperties.getSecret())
                && StringUtils.isNotEmpty(alarmDingdingProperties.getAccessToken());
    }

    @Override
    public void alarmNotify(AlarmNotifyMessage alarmNotifyMessage) {
        // 发送钉钉预警
        if (checkDingDing(alarmProperties.getDingDing())) {
            DingDingMessageUtil.sendTextMessage(alarmProperties.getDingDing().getAccessToken(), alarmProperties.getDingDing().getSecret(), alarmNotifyMessage.getAlarmMessage());
        }
        // 发送邮件预警
        if(mailSender != null && checkEmail(alarmProperties.getEmail())){
            try{
                AlarmEmailProperties alarmEmailProperties = alarmProperties.getEmail();;
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setFrom(alarmEmailProperties.getUsername());
                simpleMailMessage.setText(alarmNotifyMessage.getAlarmMessage());
                simpleMailMessage.setTo(alarmEmailProperties.getToUsers());
                simpleMailMessage.setSubject("线程池预警");
                simpleMailMessage.setSentDate(new Date());
                mailSender.send(simpleMailMessage);
            }catch (Exception e){
                log.error("发送邮件预警异常",e);
            }
        }
        // 发送企业微信机器人
        if(StringUtils.isNotEmpty(alarmProperties.getWxRobotApiUrl())){
            WxRobotAlarmMessage.TextContent textContent = new WxRobotAlarmMessage.TextContent();
            textContent.setContent(alarmNotifyMessage.getAlarmMessage());
            List<String> mentionedList = new ArrayList<>();
            mentionedList.add("@all");
            textContent.setMentioned_list(mentionedList);
            DingDingMessageUtil.post(alarmProperties.getWxRobotApiUrl(), JSON.toJSONString(new WxRobotAlarmMessage("text",textContent)));
        }
        // 通过url发送
        if (StringUtils.isNotEmpty(alarmProperties.getApiAlarmUrl())) {
            Map<String, String> data = new HashMap<>(2);
            data.put("message", alarmNotifyMessage.getAlarmMessage());
            DingDingMessageUtil.post(alarmProperties.getApiAlarmUrl(), JSON.toJSONString(data));
        }
    }
}
