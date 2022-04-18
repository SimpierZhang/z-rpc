package com.zrpc.loadbalancer;

import com.zrpc.registry.protocol.URL;
import com.zrpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @Author: Zjw
 * @Description: polling load balancer for services
 * @Create 2022-04-16 20:02
 * @Modifier:
 */
public class PollingLoadBalancer extends AbstractLoadBalance {

    private int index = 0;

    @Override
    protected URL doSelect(List<URL> serviceAddresses, RpcRequest rpcRequest) {
        if(index >= serviceAddresses.size()){
            index = 0;
        }
        return serviceAddresses.get(index++);
    }
}
