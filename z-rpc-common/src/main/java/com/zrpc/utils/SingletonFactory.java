package com.zrpc.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description: the singleton instance factory
 * @Create 2022-04-15 15:28
 * @Modifier:
 */
@Slf4j
public class SingletonFactory {

    private static final Map<Class<?>, Object> INSTANCE_MAP = new ConcurrentHashMap<>();

    private SingletonFactory(){}

    /**
     *
     * @Author Zjw
     * @Description the method to get target instance with defined class
     * @Date  2022/4/15 15:36
     * @param clazz the template class of target instance
     * @return T
     **/
    public static <T> T getInstance(Class<T> clazz){
        Object instance = INSTANCE_MAP.get(clazz);
        try {
            if(instance == null){
                synchronized (SingletonFactory.class){
                    instance = INSTANCE_MAP.get(clazz);
                    if(instance == null){
                        instance = clazz.newInstance();
                        INSTANCE_MAP.put(clazz, instance);
                    }
                }
            }
        }catch (Exception e){
            log.error("single instance factory failed to obtain instance：" + e.getMessage());
        }
        return clazz.cast(instance);
    }

}
