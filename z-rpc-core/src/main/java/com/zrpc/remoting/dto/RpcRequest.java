package com.zrpc.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @Author: Zjw
 * @Description: the protocol of request
 * @Create 2022-04-14 10:26
 * @Modifier:
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RpcRequest implements Serializable
{
    private static final long serialVersionUID = 1905122041950251207L;

    /**
     * the uid of each request
     * */
    private String requestId;

    /**
     * the interface name of request
     * */
    private String interfaceName;

    /**
     * the version of interface, which was used for distinguish multi implementation classes
     * */
    private String version;

    /**
     * the group of interface, which was used for distinguish multi implementation classes
     * */
    private String group;

    /**
     * the method name of request
     * */
    private String methodName;

    /**
     * the parameters of request method
     * */
    private Object[] parameters;

    /**
     * the parameters type of request method
     * */
    private Class<?>[] parametersType;

    public String getServiceName(){
        return this.interfaceName + "#" + this.version + "#" + this.group;
    }


}
