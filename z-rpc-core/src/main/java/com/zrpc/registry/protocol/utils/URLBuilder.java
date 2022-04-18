package com.zrpc.registry.protocol.utils;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 * @author chenchuxin
 * @date 2021/8/1
 */
public class URLBuilder {

    /**
     * 获取url上相关服务的参数
     *
     * @return 参数
     */
    public static Map<String, String> getServiceParam(String interfaceName, String rpcVersion) {
        return MapUtil.<String, String>builder()
                .put("interface", interfaceName)
                .put("version", rpcVersion).build();
    }

    /**
     * 获取url上相关服务的参数
     *
     * @return 参数
     */
    public static Map<String, String> getServiceParam(Class<?> interfaceClass, String rpcVersion) {
        return getServiceParam(interfaceClass.getCanonicalName(), rpcVersion);
    }
}
