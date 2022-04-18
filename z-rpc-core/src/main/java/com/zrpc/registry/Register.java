package com.zrpc.registry;

import com.zrpc.annotation.SPI;
import com.zrpc.registry.protocol.URL;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: Zjw
 * @Description: the interface of register
 * @Create 2022-04-15 19:12
 * @Modifier:
 */
@SPI("nacosRegister")
public interface Register {
    void registerService(URL url);

    List<URL> lookupServices(URL condition);

    void removeService(URL url);

    void removeAllService(InetSocketAddress inetSocketAddress);
}
