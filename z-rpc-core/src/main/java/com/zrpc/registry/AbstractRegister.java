package com.zrpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncoder;
import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLKeyConstants;
import com.zrpc.remoting.exception.RpcErrorMessageEnum;
import com.zrpc.remoting.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description: abstract class of register, which can extend the register
 * @Create 2022-04-16 13:36
 * @Modifier:
 */
@Slf4j
public abstract class AbstractRegister implements Register {

    private final Map<String, List<URL>> servicesMap = new ConcurrentHashMap<>();
    private final Set<URL> registeredServiceSet = new ConcurrentHashSet<>();
    protected static final Charset CHARSET = Charset.defaultCharset();
    protected static final URLEncoder URL_ENCODER = URLEncoder.createPathSegment();


    public abstract void doRegisterService(URL url);

    public abstract List<URL> doLookupServices(URL condition);

    public abstract void doRemoveService(URL url);


    @Override
    public void registerService(URL url) {
        Assert.notNull(url, "the register url can't be null...");
        if (registeredServiceSet.contains(url)) {
            log.info("the service had registered....[{}]", url);
            return;
        }
        doRegisterService(url);
        registeredServiceSet.add(url);
        addLocalCache(url);
        log.info("the service register success...[{}]", url);
    }

    @Override
    public List<URL> lookupServices(URL condition) {
        Assert.notNull(condition, "the url lookup can't be null...");
        //first lookup service from local cache
        String serviceName = getServiceNameFromUrl(condition);
        List<URL> urls = servicesMap.get(serviceName);
        if (urls == null || urls.size() == 0) {
            //if can't find service from local cache, then lookup service from registration center
            urls = doLookupServices(condition);
            if (urls == null || urls.size() == 0) {
                log.error("the target service can't be found...");
                throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
            }
        }
        return urls;
    }

    @Override
    public void removeService(URL url) {
        Assert.notNull(url, "the url removed can't be null...");
        removeFromLocalCache(url);
        doRemoveService(url);
        log.info("remove service success...[{}]", url);
    }

    @Override
    public void removeAllService(InetSocketAddress inetSocketAddress) {
        registeredServiceSet.parallelStream().forEach(url -> {
            if (inetSocketAddress.toString().equals(url.getAddress())) {
                removeService(url);
            }
        });
        log.info("all locally registered services have been removed");
    }

    private void addLocalCache(URL url) {
        String serviceName = getServiceNameFromUrl(url);
        List<URL> urls = servicesMap.computeIfAbsent(serviceName, k -> new ArrayList<>());
        urls.add(url);
    }


    private void removeFromLocalCache(URL url) {
        registeredServiceSet.remove(url);
        List<URL> urls = servicesMap.get(url.getPath());
        if (urls != null) urls.remove(url);
    }

    protected String buildPath(URL url) {
        String path = getServiceNameFromUrl(url);
        path = path + "/" + URL_ENCODER.encode(url.toFullString(), CHARSET);
        return path;
    }

    protected String getServiceNameFromUrl(URL url) {
        return url.getParam(URLKeyConstants.INTERFACE, url.getPath());
    }

}
