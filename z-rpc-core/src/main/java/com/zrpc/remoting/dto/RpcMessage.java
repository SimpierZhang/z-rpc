package com.zrpc.remoting.dto;

import lombok.*;

/**
 * @Author: Zjw
 * @Description: unified message protocol for sending requests and responses
 * @Create 2022-04-14 10:26
 * @Modifier:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RpcMessage
{
    /**
    * this param is used to determine whether the data is a request or response type
    * */
    private byte messageType;

    /**
     * the serialization type of message
     * */
    private byte serializeType;

    /**
     * the compression type of message
     * */
    private byte compressType;

    /**
     * the request id of message, which can be used to check the message
     * */
    private int requestId;

    /**
     * the real data of message
     * */
    private Object data;
}
