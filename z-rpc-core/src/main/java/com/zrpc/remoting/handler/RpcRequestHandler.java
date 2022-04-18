package com.zrpc.remoting.handler;

import com.zrpc.extension.ExtensionLoader;
import com.zrpc.provider.ServiceProvider;
import com.zrpc.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-15 15:46
 * @Modifier:
 */
@Slf4j
public class RpcRequestHandler implements RequestHandler {


    @Override
    public Object handler(RpcRequest rpcRequest, String handlerName) {
        ServiceProvider serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension(handlerName);
        Object serviceInstance = serviceProvider.getService(rpcRequest.getServiceName());
        return invokeTargetMethod(serviceInstance, rpcRequest);
    }

    private Object invokeTargetMethod(Object serviceInstance, RpcRequest rpcRequest){
        try {
            Method method = serviceInstance.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParametersType());
            if(method == null) throw new RuntimeException("the target method isn't exist...");
            log.info("method [{}] invoke...", method.getName());
            return method.invoke(serviceInstance, rpcRequest.getParameters());
        }catch (Exception e){
            log.error("the target method call failed...[{}]", e.getMessage(), e);
        }
        return null;
    }
}
