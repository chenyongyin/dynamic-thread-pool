package com.threadpool.common.inter;


import com.threadpool.common.pojo.ThreadPoolIndicatorInfo;

import java.util.List;

public interface ThreadPoolIndicatorNotify {
    /**
     * 线程池运行信息通知
     * @author cyy
     * @date 2021/04/17 15:29
     * @param indicatorInfoList
     */
    void indicatorNotify(List<ThreadPoolIndicatorInfo> indicatorInfoList);

}
