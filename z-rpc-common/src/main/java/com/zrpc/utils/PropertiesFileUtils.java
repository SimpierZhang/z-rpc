package com.zrpc.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: Zjw
 * @Description: 获取配置文件的工具类
 * @Create 2022-04-09 20:09
 * @Modifier:
 */
@Slf4j
public final class PropertiesFileUtils
{
    private PropertiesFileUtils(){

    }

    public static Properties readPropertiesFile(String fileName){
        Properties properties = null;
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if(inputStream != null){
                properties = new Properties();
                properties.load(inputStream);
            }
        }catch (IOException e){
            log.error("读取配置文件失败..." + fileName);
        }
        return properties;
    }
}
