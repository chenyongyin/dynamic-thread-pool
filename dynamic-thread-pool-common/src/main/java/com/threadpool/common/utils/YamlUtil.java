package com.threadpool.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.Map;


/**
 * yaml工具类
 *
 * @author cyy
 * @date 2021/04/08 16:01
 **/
public class YamlUtil {
    /**
     * 转json
     * @author cyy
     * @date 2021/04/08 16:11
     * @param yamlStr
     * @return com.alibaba.fastjson.JSONObject
     */
    public static JSONObject convertJson(String yamlStr){
        Yaml yaml = new Yaml();
        return new JSONObject(yaml.load(yamlStr));
    }
    /**
     * 转任意类型
     * @author cyy
     * @date 2021/04/08 16:11
     * @param yamlStr
     * @param tClass
     * @return T
     */
    public static <T> T convert(String yamlStr,Class<T> tClass){
        return convertJson(yamlStr).toJavaObject(tClass);
    }
    /**
     * map转yaml字符串
     * @author cyy
     * @date 2021/04/15 20:02
     * @param map
     * @return java.lang.String
     */
    public static String multilayerMapToYaml(Map<String, Object> map) {
        Yaml yaml = createYaml();
        return yaml.dumpAsMap(map);
    }

    private static Yaml createYaml() {
        return new Yaml(new Constructor());
    }

}
