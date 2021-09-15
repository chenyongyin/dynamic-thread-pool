package com.threadpool.controller;

import com.alibaba.fastjson.JSON;
import com.threadpool.db.entity.ApplicationInstanceInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * test
 *
 * @author cyy
 * @date 2021/04/14 15:01
 **/
@Controller
@RequestMapping("")
public class ViewController {
    @GetMapping("")
    public String main() {
        return "main";
    }

    @GetMapping("/appList")
    public String appList() {
        return "appList";
    }

    @GetMapping("/appInstance")
    public String appInstance() {
        return "appInstance";
    }

    @RequestMapping("/threadPool")
    public String threadPool() {
        return "threadPool";
    }

    @RequestMapping("/alarm")
    public String alarm() {
        return "alarm";
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    public static void main(String[] args) {
        String ss = "{\"id\":\"aa3cdf09-ce9b-4439-bb39-9a6f83074ff0\",\"naocsAddress\":null,\"nacosDataId\":\"\",\"nacosGroup\":\"\",\"appName\":\"crm-service-provider\",\"alarm\":\"{\\\"enabled\\\":true,\\\"wxRobotApiUrl\\\":\\\"https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=192547a4-8d93-4b55-a885-9ce424794ff0\\\"}\",\"alarmTimeInterval\":5,\"executors\":\"[{\\\"activeRateCapacityThreshold\\\":80,\\\"alarmEnable\\\":false,\\\"corePoolSize\\\":1,\\\"fair\\\":false,\\\"keepAliveTime\\\":60,\\\"maximumPoolSize\\\":10,\\\"owner\\\":\\\"chenyongyin\\\",\\\"queueCapacity\\\":100,\\\"queueCapacityThreshold\\\":10,\\\"queueType\\\":\\\"DynamicLinkedBlockingQueue\\\",\\\"rejectedExecutionType\\\":\\\"AbortPolicy\\\",\\\"threadPoolName\\\":\\\"clear-fans-thread-pool\\\",\\\"unit\\\":\\\"MILLISECONDS\\\"}]\",\"instanceIp\":\"192.168.8.167\",\"status\":\"RUNNING\",\"isDeleted\":0,\"createTime\":\"2021-04-15 16:01:08.0\",\"appId\":\"fead55f0-857e-453e-a9dd-c504034f8b79\"}";

//        Yaml yaml = new Yaml();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "Silenthand Olleander");
        data.put("race", "Human");
        data.put("traits", new String[] { "ONE_HAND", "ONE_EYE" });
        Yaml yaml = new Yaml();
        String output = yaml.dump(data);
        System.out.println(output);
        ApplicationInstanceInfo applicationInstanceInfo = JSON.parseObject(ss, ApplicationInstanceInfo.class);
        System.out.println(yaml.dump(applicationInstanceInfo));;
    }
}
