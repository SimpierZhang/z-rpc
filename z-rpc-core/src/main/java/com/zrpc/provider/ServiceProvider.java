package com.zrpc.provider;

import com.zrpc.annotation.SPI;
import com.zrpc.config.RpcServiceConfig;
import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLKeyConstants;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * @Author: Zjw
 * @Description:
 * the provider of service, different serialization center has different provider,
 * which can be used to publish or lookup service from registration center,
 * and can be used to add or get service from local cache
 * @Create 2022-04-12 21:16
 * @Modifier:
 */
@SPI("nacosProvider")
public interface ServiceProvider
{
    Object getService(String serviceName);

    void addService(String serviceName, Object serviceInstance);

    void publishService(URL url);

    URL lookupServiceURL(URL condition, RpcRequest rpcRequest);

    default void publishService(RpcServiceConfig config){
        publishService(buildServiceToUrl(config.getRpcServiceName()));
        addService(config.getRpcServiceName(), config.getService());
    }

    URL buildServiceToUrl(String serviceName);
}
