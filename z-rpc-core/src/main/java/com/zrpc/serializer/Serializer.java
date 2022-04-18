package com.zrpc.serializer;

import com.zrpc.annotation.SPI;

/**
 * @Author: Zjw
 * @Description: the interface of serializer
 * @Create 2022-04-15 11:26
 * @Modifier:
 */
@SPI("kryo")
public interface Serializer {

    /**
     * @Author: Zjw
     * @Description: 序列化对象
     * @Param: obj:要序列化的对象
     * @Return: 字节数组
     * @Date: 2022/4/7 14:14
     */
    byte[] serialize(Object obj);



    /**
     * @Author: Zjw
     * @Description: 反序列化对象
     * @Param: bytes: 对象的字节数组
     * @Return: 反序列化后的对象
     * @Date: 2022/4/7 14:16
     */
    <T> T deserialize(byte[] bytes);

    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
