package com.zrpc.remoting.transport.server;

import com.zrpc.config.RpcServiceConfig;
import com.zrpc.extension.ExtensionLoader;
import com.zrpc.provider.ServiceProvider;
import com.zrpc.remoting.transport.codec.RpcMessageDecoder;
import com.zrpc.remoting.transport.codec.RpcMessageEncoder;
import com.zrpc.remoting.transport.server.handler.NettyRpcServerHandler;
import com.zrpc.remoting.transport.server.utils.CustomShutdownHook;
import com.zrpc.utils.ThreadPoolUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



/**
 * @Author: Zjw
 * @Description: the netty server of this rpc framework
 * @Create 2022-04-12 21:07
 * @Modifier:
 */
@Slf4j
@Component
public class NettyRpcServer {

    private final ServiceProvider serviceProvider;
    public final static int PORT = 5566;

    public NettyRpcServer() {
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getDefaultExtension();
    }

    public void start() {
        //remove all services registered by this server
        CustomShutdownHook customShutdownHook = new CustomShutdownHook();
        customShutdownHook.clearAll();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup handlerThreadPool = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2, ThreadPoolUtils.createCustomThreadFactory("handlerThreadPool", false));
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler())
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(10000, 0, 0))
                                    .addLast(new RpcMessageEncoder())//add encoder
                                    .addLast(new RpcMessageDecoder())//add decoder
                                    .addLast(handlerThreadPool, new NettyRpcServerHandler());//add custom handler
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            log.info("server started....");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server error....[{}]", e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void registerService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }



}
