package com.zrpc.provider;

import com.zrpc.extension.ExtensionLoader;
import com.zrpc.loadbalancer.LoadBalancer;
import com.zrpc.registry.Register;
import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLKeyConstants;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.exception.RpcErrorMessageEnum;
import com.zrpc.remoting.exception.RpcException;
import com.zrpc.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description: the zk provider implementation of {@link ServiceProvider}
 * @Create 2022-04-15 19:11
 * @Modifier:
 */
@Slf4j
public class ZkServiceProvider extends AbstractServiceProvider {

    private final Register register = ExtensionLoader.getExtensionLoader(Register.class).getExtension("zkRegister");
    private final LoadBalancer loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getDefaultExtension();

    /**
     *
     * @Author Zjw
     * @Description add service to registration center
     * @Date  2022/4/16 20:40
     * @param url
     **/
    @Override
    void doPublishService(URL url) {
        register.registerService(url);
    }

    /**
     *
     * @Author Zjw
     * @Description find corresponding service from registration center
     * @Date  2022/4/16 20:40
     * @param condition
     * @param rpcRequest
     * @return com.zrpc.registry.protocol.URL
     **/
    @Override
    URL doLookupServiceURL(URL condition, RpcRequest rpcRequest) {
        List<URL> urls = register.lookupServices(condition);
        return loadBalancer.selectServiceUrl(urls, rpcRequest);
    }


}
