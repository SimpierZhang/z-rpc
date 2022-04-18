package com.zrpc.remoting.annotation;

import java.lang.annotation.*;

/**
 * @Author: Zjw
 * @Description: service discovery annotation
 * if the attribute is marked with this annotation,
 * it indicates that it is calling a remote method
 * @Create 2022-04-17 13:54
 * @Modifier:
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {

    String version() default "";

    String group() default "";
}
