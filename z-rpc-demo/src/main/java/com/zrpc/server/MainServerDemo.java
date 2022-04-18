package com.zrpc.server;

import com.zrpc.config.RpcServiceConfig;
import com.zrpc.remoting.transport.server.NettyRpcServer;
import com.zrpc.service.HelloService;
import com.zrpc.service.HelloServiceImpl1;
import com.zrpc.service.HelloServiceImpl2;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 14:42
 * @Modifier:
 */
public class MainServerDemo {
    public static void main(String[] args) {
        //register service manually
        HelloService helloService1 = new HelloServiceImpl1();
        HelloService helloService2 = new HelloServiceImpl2();
        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        RpcServiceConfig config1 = RpcServiceConfig.builder()
                .version("hello1")
                .group("hello1")
                .service(helloService1)
                .build();
        nettyRpcServer.registerService(config1);
        RpcServiceConfig config2 = RpcServiceConfig.builder()
                .version("hello2")
                .group("hello2")
                .service(helloService2)
                .build();
        nettyRpcServer.registerService(config2);
        nettyRpcServer.start();
    }
}
