package com.zrpc.remoting.transport.server.utils;

import com.zrpc.extension.ExtensionLoader;
import com.zrpc.registry.Register;
import com.zrpc.remoting.transport.server.NettyRpcServer;
import com.zrpc.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * When the server  is closed, do something such as unregister all services
 *
 * @author shuang.kou
 * @createTime 2020年06月04日 13:11:00
 */
@Slf4j
public class CustomShutdownHook
{
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();
    private static final Register REGISTRY = ExtensionLoader.getExtensionLoader(Register.class).getDefaultExtension();


    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                //when server closed, this code will be executed
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                REGISTRY.removeAllService(inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            ThreadPoolUtils.closeAllThreadPool();
        }));
    }
}
