package com.zrpc.loadbalancer;


import cn.hutool.core.collection.CollectionUtil;
import com.zrpc.registry.protocol.URL;
import com.zrpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * Abstract class for a load balancing policy
 *
 * @author shuang.kou
 * @createTime 2020年06月21日 07:44:00
 */
public abstract class AbstractLoadBalance implements LoadBalancer {
    @Override
    public URL  selectServiceUrl(List<URL> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddresses)) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcRequest);
    }

    protected abstract URL doSelect(List<URL> serviceAddresses, RpcRequest rpcRequest);

}
