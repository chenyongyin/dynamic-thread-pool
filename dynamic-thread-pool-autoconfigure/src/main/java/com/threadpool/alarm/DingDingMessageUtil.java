package com.threadpool.alarm;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
/**
 * 钉钉消息工具类
 * @author cyy
 * @date 2021/04/09 19:05
 **/
@Slf4j
public class DingDingMessageUtil {

    /**
     * 发送钉钉消息
     * @author cyy
     * @date 2021/04/09 19:15
     * @param accessToken accessToken
     * @param secret secret
     * @param msg 消息
     */
    public static void sendTextMessage(String accessToken, String secret, String msg) {
        try {
            Message message = new Message();
            message.setMsgType("text");
            message.setText(new MessageInfo(msg));
            Long timestamp = System.currentTimeMillis();
            String sign = getSign(secret, timestamp);
            String url = "https://oapi.dingtalk.com/robot/send?access_token=" + accessToken
                    + "&timestamp=" + timestamp
                    + "&sign=" + sign;
            post(url, JSON.toJSONString(message));
        } catch (Exception e) {
            log.error("发送钉钉消息异常",e);
        }
    }

    /**
     * 发送post请求
     * @author cyy
     * @date 2021/04/09 19:15
     * @param url url
     * @param jsonBody 参数
     */
    public static String post(String url, String jsonBody) {
        HttpPost httpPost = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            entity.setContentEncoding(StandardCharsets.UTF_8.name());
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            } else {
                body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("发送通知异常",e);
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return body;
    }
    /**
     * 获取签名
     * @author cyy
     * @date 2021/04/09 19:14
     * @param secret 私钥
     * @param timestamp 时间戳
     * @return java.lang.String
     */
    private static String getSign(String secret, Long timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("钉钉发送消息签名失败");
        }
    }
}

@Data
class Message {
    private String msgType;
    private MessageInfo text;
}

@Data
@AllArgsConstructor
class MessageInfo {
    private String content;
}