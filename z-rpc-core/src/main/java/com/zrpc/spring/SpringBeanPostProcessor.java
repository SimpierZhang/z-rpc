package com.zrpc.spring;

import com.zrpc.config.RpcServiceConfig;
import com.zrpc.extension.ExtensionLoader;
import com.zrpc.provider.ServiceProvider;
import com.zrpc.provider.ZkServiceProvider;
import com.zrpc.remoting.annotation.RpcReference;
import com.zrpc.remoting.annotation.RpcService;
import com.zrpc.remoting.transport.client.NettyRpcClient;
import com.zrpc.remoting.transport.client.proxy.RpcClientProxy;
import com.zrpc.utils.SingletonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Author: Zjw
 * @Description: postProcessor
 * @Create 2022-04-17 19:38
 * @Modifier:
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getDefaultExtension();
    private final NettyRpcClient client = SingletonFactory.getInstance(NettyRpcClient.class);


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig config = RpcServiceConfig.builder()
                    .version(rpcService.version())
                    .group(rpcService.group())
                    .service(bean)
                    .build();
            serviceProvider.publishService(config);
            log.info("the service -- [{}] had been registered...", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if(rpcReference != null){
                RpcServiceConfig config = RpcServiceConfig.builder()
                        .version(rpcReference.version())
                        .group(rpcReference.group())
                        .build();
                RpcClientProxy clientProxy = new RpcClientProxy(client, config);
                Object proxy = clientProxy.getProxy(field.getType());
                field.setAccessible(true);
                try {
                    //replace the field in the class with the corresponding proxy class
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    log.error("add proxyField to target class fail...[{}]", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}

