package com.threadpool.service.impl;

import com.alibaba.fastjson.JSON;
import com.threadpool.common.annotation.ThreadPoolIndicatorListener;
import com.threadpool.common.inter.ThreadPoolIndicatorNotify;
import com.threadpool.common.pojo.ThreadPoolIndicatorInfo;

import java.util.List;

/**
 * @author cyy
 * @date 2021/04/17 15:49
 **/
@ThreadPoolIndicatorListener
public class ThreadPoolIndicatorNotifyImpl implements ThreadPoolIndicatorNotify {
    @Override
    public void indicatorNotify(List<ThreadPoolIndicatorInfo> indicatorInfoList) {
        System.out.println(JSON.toJSONString(indicatorInfoList));
    }
}
