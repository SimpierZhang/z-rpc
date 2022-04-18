package com.zrpc.service;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 14:44
 * @Modifier:
 */
public interface HelloService {

    String hello(Hello hello);

    Hello hello(Hello hello, String word);

}
