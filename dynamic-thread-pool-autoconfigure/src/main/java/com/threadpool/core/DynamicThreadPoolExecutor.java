package com.threadpool.core;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 动态线程池执行类
 *
 * @author cyy
 * @date 2021/04/09 13:46
 **/
public class DynamicThreadPoolExecutor extends ThreadPoolExecutor{
    /**
     * 默认任务名
     */
    private static final String DEFAULT_TASK_NAME = "defaultTask";

    /**
     * 默认拒绝策略
     */
    private static final RejectedExecutionHandler DEFAULT_REJECTED_HANDLER = new ThreadPoolExecutor.AbortPolicy();
    /**
     * 线程池名称
     */
    private String threadPoolName;

    private String threadPoolId;
    /**
     * 默认任务名计数器
     */
    private final AtomicLong defaultTaskNameCount = new AtomicLong(0);

    /**
     * 增加运行的任务名称
     */
    private final LruHashMap<String, String> runnableNameMap = new LruHashMap<>(1000);

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, DEFAULT_REJECTED_HANDLER);
    }

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String threadPoolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadPoolName = threadPoolName;
    }

    @Override
    public void execute(Runnable command) {
        runnableNameMap.putIfAbsent(command.getClass().getSimpleName(), getDefaultTaskName());
        super.execute(command);
    }

    public void execute(Runnable command, String taskName) {
        runnableNameMap.putIfAbsent(command.getClass().getSimpleName(), taskName);
        super.execute(command);
    }

    public Future<?> submit(Runnable task, String taskName) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), taskName);
        return super.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task, String taskName) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), taskName);
        return super.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result, String taskName) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), taskName);
        return super.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), getDefaultTaskName());
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), getDefaultTaskName());
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        runnableNameMap.putIfAbsent(task.getClass().getSimpleName(), getDefaultTaskName());
        return super.submit(task, result);
    }
    /**
     * 获取默认的任务名称
     * @author cyy
     * @date 2021/04/12 10:33
     * @return java.lang.String
     */
    private String getDefaultTaskName(){
        return String.format("%s-%s",DEFAULT_TASK_NAME,defaultTaskNameCount.incrementAndGet());
    }
    /**
     * 任务执行之前
     * @author cyy
     * @date 2021/04/12 10:34
     * @param t Thread
     * @param r Runnable
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }
    /**
     * 任务执行之后
     * @author cyy
     * @date 2021/04/12 10:34
     * @param r Runnable
     * @param t Throwable
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        String threadName = Thread.currentThread().getName();
    }

    public String getThreadPoolId() {
        return threadPoolId;
    }

    public void setThreadPoolId(String threadPoolId) {
        this.threadPoolId = threadPoolId;
    }
}
