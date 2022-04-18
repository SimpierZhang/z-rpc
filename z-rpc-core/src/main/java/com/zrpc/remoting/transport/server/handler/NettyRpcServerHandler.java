package com.zrpc.remoting.transport.server.handler;

import com.zrpc.provider.ServiceProviderEnum;
import com.zrpc.remoting.constants.consts.RpcConstants;
import com.zrpc.remoting.constants.enums.RpcResponseEnum;
import com.zrpc.remoting.dto.RpcMessage;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.dto.RpcResponse;
import com.zrpc.remoting.handler.RpcRequestHandler;
import com.zrpc.utils.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Zjw
 * @Description: the custom handler of nettyRpcServer
 * @Create 2022-04-15 15:19
 * @Modifier:
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private final RpcRequestHandler rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        log.info("the server received message, msg: [{}]", msg);
        byte messageType = msg.getMessageType();
        RpcMessage rpcResponseMessage = RpcMessage.builder()
                .serializeType(msg.getSerializeType())
                .compressType(msg.getCompressType())
                .requestId(msg.getRequestId())
                .build();
        if(messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
            //heartbeat check
            log.info("received heartbeat check message: [{}]", msg.getData());
            rpcResponseMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
            rpcResponseMessage.setData(RpcConstants.PONG);
        }else {
            //real rpc request
            RpcRequest rpcRequestData = (RpcRequest) msg.getData();
            Object result = rpcRequestHandler.handler(rpcRequestData, ServiceProviderEnum.ZK_SERVICE_PROVIDER.getType());
            log.info("the service call result is：[{}]", result.toString());
            rpcResponseMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequestData.getRequestId());
                rpcResponseMessage.setData(rpcResponse);
            } else {
                RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseEnum.FAIL_RESPONSE.getMessage());
                rpcResponse.setData(rpcResponse);
                log.error("unable to write information, request missing");
            }
        }
        ctx.channel().writeAndFlush(rpcResponseMessage);
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                ctx.close();
                log.info("No data request is received at the specified time, close the channel...");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("the handler of server happened error...[{}]", cause.getMessage(), cause);
    }
}
