package com.zrpc.remoting.annotation.scan;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-17 14:00
 * @Modifier:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
public @interface RpcScan {
    String[] basePackages();
}
