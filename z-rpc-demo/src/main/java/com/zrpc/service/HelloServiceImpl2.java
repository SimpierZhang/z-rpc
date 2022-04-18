package com.zrpc.service;

import com.zrpc.remoting.annotation.RpcService;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 14:45
 * @Modifier:
 */
@RpcService(version = "hello2", group = "hello2")
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String hello(Hello hello) {
        return hello.getWord() + "hello2";
    }

    @Override
    public Hello hello(Hello hello, String word) {
        return new Hello(hello.getWord() + "===" + word + "===hello2");
    }
}
