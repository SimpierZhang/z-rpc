package com.zrpc.server;

import com.zrpc.remoting.annotation.scan.RpcScan;
import com.zrpc.remoting.transport.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 20:39
 * @Modifier:
 */
@RpcScan(basePackages = "com.zrpc")
public class NettyServerMain {
    public static void main(String[] args) {
        //register service automatically
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        nettyRpcServer.start();
    }
}
