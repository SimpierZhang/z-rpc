package com.zrpc.loadbalancer;

import com.zrpc.annotation.SPI;
import com.zrpc.registry.protocol.URL;
import com.zrpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @Author: Zjw
 * @Description: Interface to the load balancing policy
 * @Create 2022-04-15 20:35
 * @Modifier:
 */
@SPI("random")
public interface LoadBalancer {

    /**
     *
     * @Author Zjw
     * @Description Choose one from the list of existing service addresses list
     * @Date  2022/4/15 20:36
     * @param urls
     * @param rpcRequest
     * @return com.zrpc.registry.protocol.URL
     **/
    URL selectServiceUrl(List<URL> urls, RpcRequest rpcRequest);
}
