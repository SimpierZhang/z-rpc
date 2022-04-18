package com.zrpc.remoting.handler;

import com.zrpc.remoting.dto.RpcRequest;

/**
 * @Author: Zjw
 * @Description: the handler to handle rpcRequest
 * @Create 2022-04-15 15:48
 * @Modifier:
 */
public interface RequestHandler {
    /**
     *
     * @Author Zjw
     * @Description the method to handle rpcRequest
     * @Date  2022/4/15 15:49
     * @param rpcRequest
     * @return java.lang.Object, the result of  invoked method
     **/
    Object handler(RpcRequest rpcRequest, String handlerName);
}
