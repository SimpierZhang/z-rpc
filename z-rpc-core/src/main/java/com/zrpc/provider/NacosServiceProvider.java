package com.zrpc.provider;

import com.zrpc.extension.ExtensionLoader;
import com.zrpc.loadbalancer.LoadBalancer;
import com.zrpc.registry.Register;
import com.zrpc.registry.protocol.URL;
import com.zrpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-18 10:41
 * @Modifier:
 */
public class NacosServiceProvider extends AbstractServiceProvider {

    private final Register register = ExtensionLoader.getExtensionLoader(Register.class).getExtension("nacosRegister");
    private final LoadBalancer loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getDefaultExtension();

    @Override
    void doPublishService(URL url) {
        register.registerService(url);
    }

    @Override
    URL doLookupServiceURL(URL condition, RpcRequest rpcRequest) {
        List<URL> urls = register.lookupServices(condition);
        return loadBalancer.selectServiceUrl(urls, rpcRequest);
    }
}
