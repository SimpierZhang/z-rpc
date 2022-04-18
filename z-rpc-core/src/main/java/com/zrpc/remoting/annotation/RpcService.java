package com.zrpc.remoting.annotation;

import com.zrpc.remoting.annotation.scan.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: Zjw
 * @Description: service registration annotation
 *  All classes marked with this annotation will be automatically registered
 *  in the registration center when the server is started
 * @Create 2022-04-17 13:48
 * @Modifier:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {

    String version() default "";

    String group() default "";
}
