package com.zrpc.remoting.transport.client.handler;

import com.zrpc.compress.CompressorTypeEnum;
import com.zrpc.remoting.constants.consts.RpcConstants;
import com.zrpc.remoting.dto.RpcMessage;
import com.zrpc.remoting.dto.RpcRequest;
import com.zrpc.remoting.dto.RpcResponse;
import com.zrpc.remoting.exception.RpcErrorMessageEnum;
import com.zrpc.remoting.exception.RpcException;
import com.zrpc.serializer.SerializerTypeEnum;
import com.zrpc.utils.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @Author: Zjw
 * @Description: the handler of client
 * @Create 2022-04-16 20:51
 * @Modifier:
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private final UnProcessedRequest unProcessedRequest = SingletonFactory.getInstance(UnProcessedRequest.class);

    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        log.info("received response msg...[{}]", msg);
        byte messageType = msg.getMessageType();
        if(messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE){
            log.info("received heartbeat response...[{}]", msg.getData().toString());
        }else if(messageType == RpcConstants.RESPONSE_TYPE){
            Object responseData = msg.getData();
            if(responseData instanceof RpcResponse){
                log.info("received heartbeat response...[{}]", responseData.toString());
                RpcResponse<Object> response = (RpcResponse<Object>) responseData;
                unProcessedRequest.completeRequest(response);
            }else {
                throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.WRITER_IDLE){
                //send heartbeat request to detect whether the server is alive
                RpcMessage rpcMessage = RpcMessage.builder()
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .serializeType(SerializerTypeEnum.KRYO_SERIALIZER.getCode())
                        .compressType(CompressorTypeEnum.GZIP_SERIALIZER.getCode())
                        .data(RpcConstants.PING)
                        .build();
                ctx.channel().writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                log.info("send heart beat request...[{}]", RpcConstants.PING);
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("client handler error...[{}]", cause.getMessage(), cause);
    }
}
