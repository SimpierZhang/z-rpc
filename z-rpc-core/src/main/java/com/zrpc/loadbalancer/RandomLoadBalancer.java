package com.zrpc.loadbalancer;



import com.zrpc.registry.protocol.URL;
import com.zrpc.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * Implementation of random load balancing strategy
 *
 * @author shuang.kou
 * @createTime 2020年06月21日 07:47:00
 */
public class RandomLoadBalancer extends AbstractLoadBalance
{
    @Override
    protected URL doSelect(List<URL> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
