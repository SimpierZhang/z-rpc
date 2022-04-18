package com.zrpc.client;

import com.zrpc.remoting.annotation.scan.RpcScan;
import com.zrpc.service.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 20:41
 * @Modifier:
 */
@RpcScan(basePackages = "com.zrpc")
public class NettyClientMain {


    public static void main(String[] args) {
        //find service automatically
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
