package com.threadpool.common.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 持久化类型
 *
 * @author cyy
 * @date 2021/04/16 14:45
 **/
@Data
public class DbTypeProperties implements Serializable {

    private String type;

}
