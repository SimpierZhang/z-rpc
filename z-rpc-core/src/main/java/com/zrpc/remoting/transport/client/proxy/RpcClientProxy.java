package com.zrpc.remoting.transport.client.proxy;

import com.zrpc.config.RpcServiceConfig;
import com.zrpc.remoting.constants.enums.RpcResponseEnum;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.dto.RpcResponse;
import com.zrpc.remoting.exception.RpcErrorMessageEnum;
import com.zrpc.remoting.exception.RpcException;
import com.zrpc.remoting.transport.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-16 21:23
 * @Modifier:
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";
    private final NettyRpcClient client;
    private final RpcServiceConfig config;
    private final Map<Class<?>, Object> proxyInstanceMap = new ConcurrentHashMap<>();

    public RpcClientProxy(NettyRpcClient client, RpcServiceConfig config){
        this.client = client;
        this.config = config;
    }

    public Object getProxy(Class<?> clazz){
        Object proxyInstance = proxyInstanceMap.get(clazz);
        if(proxyInstance == null){
            synchronized (RpcClientProxy.class){
                proxyInstance = proxyInstanceMap.get(clazz);
                if(proxyInstance == null){
                    proxyInstance = Proxy.newProxyInstance(RpcClientProxy.class.getClassLoader(), new Class<?>[]{clazz}, this);
                    proxyInstanceMap.put(clazz, proxyInstance);
                }
            }
        }
        return proxyInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("method [{}] invoke...", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .group(config.getGroup())
                .version(config.getVersion())
                .methodName(method.getName())
                .parametersType(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString())
                .build();
        CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) client.sendRpcRequest(rpcRequest);
        RpcResponse<Object> rpcResponse = completableFuture.get();
        check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getResponseCode() == null || !rpcResponse.getResponseCode().equals(RpcResponseEnum.SUCCESS_RESPONSE.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
