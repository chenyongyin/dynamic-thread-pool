package com.threadpool.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author cyy
 * @date 2021/04/14 15:50
 **/
@Data
public class ResultModelVo<T> {

    String code;

    private String msg;

    private long count;

    private T data;

    public ResultModelVo(){

    }
    public ResultModelVo(long count,T data){
        this.code = "0";
        this.data = data;
        this.count = count;
    }
}
