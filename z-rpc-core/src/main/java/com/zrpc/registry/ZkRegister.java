package com.zrpc.registry;

import cn.hutool.core.net.URLDecoder;
import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLParser;
import com.zrpc.registry.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-15 19:19
 * @Modifier:
 */
public class ZkRegister extends AbstractRegister {

    @Override
    public void doRegisterService(URL url) {
        CuratorUtils.createEphemeralNode(buildPath(url));
    }

    @Override
    public List<URL> doLookupServices(URL condition) {
        List<String> urlPaths = CuratorUtils.getChildren(getServiceNameFromUrl(condition));

        return urlPaths.stream().map(urlPath -> {
            String decodedUrlPath = URLDecoder.decode(urlPath, CHARSET);
            return URLParser.toURL(decodedUrlPath);
        }).collect(Collectors.toList());
    }

    @Override
    public void doRemoveService(URL url) {
        CuratorUtils.removeTargetNode(buildPath(url));
    }
}
