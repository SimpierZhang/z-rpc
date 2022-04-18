package com.zrpc.remoting.transport.client;

import com.zrpc.compress.CompressorTypeEnum;
import com.zrpc.extension.ExtensionLoader;
import com.zrpc.provider.ServiceProvider;
import com.zrpc.registry.protocol.URL;
import com.zrpc.registry.protocol.utils.URLParser;
import com.zrpc.remoting.constants.consts.RpcConstants;
import com.zrpc.remoting.dto.RpcMessage;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.dto.RpcResponse;
import com.zrpc.remoting.transport.client.handler.NettyRpcClientHandler;
import com.zrpc.remoting.transport.client.handler.UnProcessedRequest;
import com.zrpc.remoting.transport.codec.RpcMessageDecoder;
import com.zrpc.remoting.transport.codec.RpcMessageEncoder;
import com.zrpc.serializer.SerializerTypeEnum;
import com.zrpc.utils.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description: the netty client of this rpc framework
 * @Create 2022-04-12 21:07
 * @Modifier:
 */
@Slf4j
public class NettyRpcClient
{
    private final ServiceProvider serviceProvider;
    private final Map<String, Channel> channelMap;
    private final Bootstrap bootstrap;
    private final UnProcessedRequest unProcessedRequest;

    public NettyRpcClient(){
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getDefaultExtension();
        channelMap = new ConcurrentHashMap<>();
        bootstrap = new Bootstrap();
        unProcessedRequest = SingletonFactory.getInstance(UnProcessedRequest.class);
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler())
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(0, 60, 0))
                                    .addLast(new RpcMessageEncoder())
                                    .addLast(new RpcMessageDecoder())
                                    .addLast(new NettyRpcClientHandler());
                        }
                    });
            log.info("client start success...");
        }catch (Exception e){
            log.error("client start error...[{}]", e.getMessage(), e);
        }

    }

    public Object sendRpcRequest(RpcRequest rpcRequest){
        try {
            CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();
            URL url = serviceProvider.lookupServiceURL(URLParser.buildPathToUrl(rpcRequest.getServiceName()), rpcRequest);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(url.getHost(), url.getPort());
            Channel channel = getChannel(inetSocketAddress);
            if(channel.isActive()){
                RpcMessage rpcMessage = RpcMessage.builder()
                        .serializeType(SerializerTypeEnum.KRYO_SERIALIZER.getCode())
                        .compressType(CompressorTypeEnum.GZIP_SERIALIZER.getCode())
                        .messageType(RpcConstants.REQUEST_TYPE)
                        .data(rpcRequest)
                        .build();
                unProcessedRequest.put(rpcRequest.getRequestId(), completableFuture);
                channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                    if(future.isSuccess()){
                        log.info("client send request success...[{}]", rpcMessage);
                    }else {
                        future.channel().close();
                        completableFuture.completeExceptionally(future.cause());
                        log.info("client send request fail...[{}]", rpcMessage);
                    }
                });
                log.info("send request to [{}] success", inetSocketAddress.toString());
                return completableFuture;
            }else {
                throw new RuntimeException("the channel to send is closed...");
            }
        }catch (Exception e){
            log.error("client send request fail...");
            throw e;
        }
    }

    private Channel getChannel(InetSocketAddress inetSocketAddress) {
        Assert.notNull(inetSocketAddress, "the connect address can't be null...");
        Channel channel = channelMap.get(inetSocketAddress.toString());
        if(channel == null){
            synchronized (NettyRpcClient.class){
                channel = channelMap.get(inetSocketAddress.toString());
                if(channel == null){
                    channel = doConnect(inetSocketAddress);
                    channelMap.put(inetSocketAddress.toString(), channel);
                }
            }
        }
        return channel;
    }

    @SneakyThrows
    private Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                log.info("client connect [{}] success...", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }else {
                log.error("client connect [{}] fail...", inetSocketAddress.toString());
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}
