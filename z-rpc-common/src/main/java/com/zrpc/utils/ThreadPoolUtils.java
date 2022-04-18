package com.zrpc.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zrpc.utils.config.CustomThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: Zjw
 * @Description: the tool for create ThreadPool and ThreadPoolFactory
 * @Create 2022-04-15 9:27
 * @Modifier:
 */
@Slf4j
public class ThreadPoolUtils
{
    private final static Map<String, ExecutorService> THREAD_POOL_MAP = new ConcurrentHashMap<>();


    /**
     *
     * @Author Zjw
     * @Description create custom {@link ExecutorService} with defined name
     * @Date  2022/4/15 10:01
     * @param threadPoolNamePrefix: threadPool name
     * @return java.util.concurrent.ExecutorService
     **/
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadPoolNamePrefix){
        return createCustomThreadPoolIfAbsent(threadPoolNamePrefix, new CustomThreadPoolConfig());
    }

    /**
     *
     * @Author Zjw
     * @Description  create custom {@link ExecutorService} with defined name and config
     * @Date  2022/4/15 10:02
     * @param threadPoolNamePrefix: threadPool name
     * @param config: threadPool config
     * @return java.util.concurrent.ExecutorService
     **/
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadPoolNamePrefix, CustomThreadPoolConfig config) {
        return createCustomThreadPoolIfAbsent(threadPoolNamePrefix, config, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadPoolNamePrefix, CustomThreadPoolConfig config, boolean daemon) {
        ExecutorService threadPool = THREAD_POOL_MAP.putIfAbsent(threadPoolNamePrefix, createThreadPool(threadPoolNamePrefix, config, daemon));
        if(threadPool == null || threadPool.isShutdown()){
            THREAD_POOL_MAP.remove(threadPoolNamePrefix);
            threadPool = createThreadPool(threadPoolNamePrefix, config, daemon);
            THREAD_POOL_MAP.put(threadPoolNamePrefix, threadPool);
        }
        return threadPool;
    }

    private static ExecutorService createThreadPool(String threadPoolNamePrefix, CustomThreadPoolConfig config, boolean daemon) {
        return new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaximumPoolSize(), config.getKeepAliveTime(),
                config.getUnit(), config.getWorkQueue(), createCustomThreadFactory(threadPoolNamePrefix, daemon));
    }


    /**
     *
     * @Author Zjw
     * @Description close all threadPool created by this tool class
     * @Date  2022/4/15 10:03
     **/
    public static void closeAllThreadPool(){
        THREAD_POOL_MAP.entrySet().parallelStream().forEach(entry -> {
            closeTargetThreadPool(entry.getKey(), entry.getValue());
        });
        log.info("all threadPool had closed....");
    }


    /**
     *
     * @Author Zjw
     * @Description close target threadPool
     * @Date  2022/4/15 10:03
     * @param threadNamePrefix: the name of target threaPool
     **/
    public static void closeTargetThreadPool(String threadNamePrefix){
        closeTargetThreadPool(threadNamePrefix, THREAD_POOL_MAP.get(threadNamePrefix));
    }

    private static void closeTargetThreadPool(String threadNamePrefix, ExecutorService threadPool){
        if(threadNamePrefix == null || threadPool == null) throw new RuntimeException("the threadPool needed to close is null...");
        threadPool.shutdown();
        log.info("the threadPool-[{}] had closed", threadNamePrefix);
    }


    /**
     *
     * @Author Zjw
     * @Description create custom threadPoolFactory
     * @Date  2022/4/15 10:04
     * @param threadNamePrefix
     * @param daemon
     * @return java.util.concurrent.ThreadFactory
     **/
    public static ThreadFactory createCustomThreadFactory(String threadNamePrefix, Boolean daemon){
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
