package com.zrpc.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-09 20:48
 * @Modifier:
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum
{
    PROPERTIES_FILE_PATH("rpc.properties"),
    ZK_ADDRESS("zkAddress"),
    NACOS_ADDRESS("nacosAddress");
    private String value;
}
