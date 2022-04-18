package com.zrpc.service;

import com.zrpc.remoting.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 20:42
 * @Modifier:
 */
@Component
public class HelloController {

    @RpcReference(version = "hello1", group = "hello1")
    private HelloService helloService1;

    @RpcReference(version = "hello2", group = "hello2")
    private HelloService helloService2;

    public void test(){
        Hello hello = new Hello("hello world!!!");
        for(int i = 0; i < 4; i++){
            System.out.println(helloService1.hello(hello));
            System.out.println(helloService1.hello(hello, "==>hello1").getWord());
            System.out.println(helloService2.hello(hello));
            System.out.println(helloService2.hello(hello, "==>hello2").getWord());
            System.out.println("-------------------------------------------------------");
            System.out.println();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
