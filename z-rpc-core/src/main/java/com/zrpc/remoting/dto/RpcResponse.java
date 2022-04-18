package com.zrpc.remoting.dto;

import com.zrpc.remoting.constants.enums.RpcResponseEnum;
import lombok.*;

import java.io.Serializable;

/**
 * @Author: Zjw
 * @Description: the protocol of response
 * @Create 2022-04-14 10:26
 * @Modifier:
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RpcResponse<T> implements Serializable
{
    private static final long serialVersionUID = 715745410605631233L;

    /**
     * the requestId of response, which must be equal to the request id of send request
     */
    private String requestId;

    /**
     * the state code of response
     */
    private Integer responseCode;

    /**
     * the message of response
     */
    private String responseMessage;

    /**
     * the data of response
     */
    private T data;


    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setResponseCode(RpcResponseEnum.SUCCESS_RESPONSE.getCode());
        rpcResponse.setResponseMessage(RpcResponseEnum.SUCCESS_RESPONSE.getMessage());
        rpcResponse.setRequestId(requestId);
        if (data != null) rpcResponse.setData(data);
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(String message) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setResponseCode(RpcResponseEnum.FAIL_RESPONSE.getCode());
        rpcResponse.setResponseMessage(RpcResponseEnum.FAIL_RESPONSE.getMessage());
        if (message != null) {
            rpcResponse.setResponseMessage(":" + message);
        }
        return rpcResponse;
    }
}
