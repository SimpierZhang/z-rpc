package com.zrpc.client;

import com.zrpc.config.RpcServiceConfig;
import com.zrpc.remoting.transport.client.NettyRpcClient;
import com.zrpc.remoting.transport.client.proxy.RpcClientProxy;
import com.zrpc.service.Hello;
import com.zrpc.service.HelloService;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 14:42
 * @Modifier:
 */
public class MainClientDemo {



    public static void main(String[] args) {
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        RpcServiceConfig config = RpcServiceConfig.builder()
                .version("hello1")
                .group("hello1")
                .build();
        RpcClientProxy clientProxy = new RpcClientProxy(nettyRpcClient, config);
        HelloService helloService = (HelloService) clientProxy.getProxy(HelloService.class);
        Hello hello = new Hello("hello world!!!");
        for(int i = 0; i < 4; i++){
            System.out.println(helloService.hello(hello));
        }
//        System.out.println(helloService.hello(hello, "hello1").getWord());

//        RpcServiceConfig config2 = RpcServiceConfig.builder()
//                .version("hello2")
//                .group("hello2")
//                .build();
//        RpcClientProxy clientProxy2 = new RpcClientProxy(nettyRpcClient, config2);
//
//        HelloService helloService2 = (HelloService) clientProxy2.getProxy(HelloService.class);
//        Hello hello2 = new Hello("hello2 world!!!");
//        System.out.println(helloService2.hello(hello2));
//        System.out.println(helloService2.hello(hello2, "hello2").getWord());
    }
}
