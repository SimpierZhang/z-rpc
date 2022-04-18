package com.zrpc.remoting.transport.client.handler;

import com.zrpc.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-16 21:14
 * @Modifier:
 */
public class UnProcessedRequest {

    private final Map<String, CompletableFuture<RpcResponse<Object>>> unProcessedRequestMap = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> completableFuture){
        this.unProcessedRequestMap.put(requestId, completableFuture);
    }

    public CompletableFuture<RpcResponse<Object>> get(String requestId){
        CompletableFuture<RpcResponse<Object>> completableFuture = unProcessedRequestMap.get(requestId);
        if(completableFuture == null) throw new IllegalStateException("nu such unprocessed request...");
        return completableFuture;
    }

    public void completeRequest(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = unProcessedRequestMap.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        }
        else {
            throw new IllegalStateException("nu such unprocessed request...");
        }
    }

}
