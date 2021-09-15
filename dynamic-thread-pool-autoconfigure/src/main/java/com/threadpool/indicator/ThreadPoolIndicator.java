package com.threadpool.indicator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池数据指标
 *
 * @author cyy
 * @date 2021/04/09 19:29
 **/
public class ThreadPoolIndicator {
    /**
     * 存储线程池拒绝次数，Key:名称 Value:次数
     */
    private static final Map<String, AtomicLong> THREAD_POOL_EXECUTOR_REJECT_COUNT_MAP = new ConcurrentHashMap<>();
    /**
     * 存储上次告警的时间，Key:名称 Value:时间戳
     */
    private static final Map<String, AtomicLong> THREAD_POOL_EXECUTOR_ALARM_TIME_MAP = new ConcurrentHashMap<>();

    /**
     * 获取线程池拒绝次数
     * @author cyy
     * @date 2021/04/09 15:14
     * @param threadPoolName 线程池名称
     * @return java.util.concurrent.atomic.AtomicLong
     */
    public static long getRejectCount(String threadPoolName) {
        AtomicLong atomicLong = THREAD_POOL_EXECUTOR_REJECT_COUNT_MAP.get(threadPoolName);
        return atomicLong == null ? 0L : atomicLong.get();
    }
    /**
     * 清空拒绝次数数据
     * @author cyy
     * @date 2021/04/09 15:12
     * @param threadPoolName 线程池名称
     */
    public static void clearRejectCount(String threadPoolName) {
        THREAD_POOL_EXECUTOR_REJECT_COUNT_MAP.remove(threadPoolName);
    }
    /**
     * 增加拒绝次数
     * @author cyy
     * @date 2021/04/09 15:13
     * @param threadPoolName 线程池名称
     */
    public static void incrRejectCount(String threadPoolName){
        AtomicLong atomicLong = THREAD_POOL_EXECUTOR_REJECT_COUNT_MAP.putIfAbsent(threadPoolName, new AtomicLong(1));
        if (atomicLong != null) {
            atomicLong.incrementAndGet();
        }
    }
    /**
     * 获取上次预警时间
     * @author cyy
     * @date 2021/04/09 19:35
     * @param alarmName 预警名称
     * @return java.util.concurrent.atomic.AtomicLong
     */
    public static AtomicLong getAlarmTime(String alarmName){
        return THREAD_POOL_EXECUTOR_ALARM_TIME_MAP.get(alarmName);
    }
    /**
     * 设置预警时间
     * @author cyy
     * @date 2021/04/09 19:35
     * @param alarmName 预警名称
     * @param alarmTime 预警时间
     */
    public static void putAlarmTime(String alarmName,long alarmTime){
        THREAD_POOL_EXECUTOR_ALARM_TIME_MAP.put(alarmName, new AtomicLong(alarmTime));
    }


}
