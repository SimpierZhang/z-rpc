package com.zrpc.provider;

import com.zrpc.serializer.SerializerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: Zjw
 * @Description: the enums of service provider, must be consistent with the {@link SerializerTypeEnum}
 * @Create 2022-04-15 19:03
 * @Modifier:
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ServiceProviderEnum {

    ZK_SERVICE_PROVIDER((byte) 0x01, "zkProvider");

    private byte code;
    private String type;

    public static String getType(byte code){
        for(ServiceProviderEnum typeEnum : ServiceProviderEnum.values()){
            if(typeEnum.code == code) return typeEnum.type;
        }
        throw new RuntimeException("no such serviceProvider code or type...");
    }
}
