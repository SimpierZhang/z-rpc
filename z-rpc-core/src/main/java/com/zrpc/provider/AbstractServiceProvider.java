package com.zrpc.provider;

import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLKeyConstants;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.exception.RpcErrorMessageEnum;
import com.zrpc.remoting.exception.RpcException;
import com.zrpc.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-18 10:35
 * @Modifier:
 */
@Slf4j
public abstract class AbstractServiceProvider implements ServiceProvider {

    private static final Map<String, Object> serviceInstanceMap = new ConcurrentHashMap<>();


    /**
     *
     * @Author Zjw
     * @Description get service instance from local cache
     * @Date  2022/4/16 20:39
     * @param serviceName
     * @return java.lang.Object: service instance
     **/
    @Override
    public Object getService(String serviceName) {
        Object serviceInstance = serviceInstanceMap.get(serviceName);
        if (serviceInstance == null) throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        return serviceInstance;
    }

    /**
     *
     * @Author Zjw
     * @Description add service instance to local cache
     * @Date  2022/4/16 20:39
     * @param serviceName
     * @param serviceInstance
     **/
    @Override
    public void addService(String serviceName, Object serviceInstance) {
        if (serviceName == null || serviceInstance == null)
            throw new IllegalArgumentException("parameter can't be null...");
        if (!serviceInstanceMap.containsKey(serviceName)) serviceInstanceMap.put(serviceName, serviceInstance);
    }

    @Override
    public void publishService(URL url) {
        doPublishService(url);
    }

    abstract void doPublishService(URL url);



    @Override
    public URL lookupServiceURL(URL condition, RpcRequest rpcRequest) {
        return doLookupServiceURL(condition, rpcRequest);
    }

    abstract URL doLookupServiceURL(URL condition, RpcRequest rpcRequest);


    @Override
    public URL buildServiceToUrl(String serviceName) {
        URL url = null;
        try {
            url = URL.builder()
                    .protocol(URLKeyConstants.Z_RPC_PROTOCOL)
                    .host(InetAddress.getLocalHost().getHostAddress())
                    .port(NettyRpcServer.PORT)
                    .path(serviceName)
                    .build();
        } catch (Exception e) {
            log.error("build service to url fail...[{}]", e.getMessage(), e);
        }
        return url;
    }
}
