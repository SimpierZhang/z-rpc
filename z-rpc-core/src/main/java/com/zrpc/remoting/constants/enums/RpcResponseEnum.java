package com.zrpc.remoting.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: Zjw
 * @Description: enum of rpc response
 * @Create 2022-04-12 20:41
 * @Modifier:
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum RpcResponseEnum
{
    SUCCESS_RESPONSE(200, "success!!!"),
    FAIL_RESPONSE(500, "fail!!!");

    private int code;
    private String message;
}
