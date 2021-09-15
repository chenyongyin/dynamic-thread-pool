package com.threadpool.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cyy
 * @date 2021/04/15 20:26
 **/
@Data
public class BasePageReq implements Serializable {
    @TableField(exist = false)
    int page = 1;
    @TableField(exist = false)
    int limit = 10;
}
