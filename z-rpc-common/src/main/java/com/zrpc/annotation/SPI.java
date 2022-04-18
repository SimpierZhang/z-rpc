package com.zrpc.annotation;

import java.lang.annotation.*;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-14 17:33
 * @Modifier:
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SPI
{
    String value() default "default";
}
