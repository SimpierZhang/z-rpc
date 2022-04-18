package com.zrpc.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-15 11:01
 * @Modifier:
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum SerializerTypeEnum {

    KRYO_SERIALIZER((byte) 0x01, "kryo");

    private byte code;
    private String type;

    public static String getType(byte code){
        for(SerializerTypeEnum typeEnum : SerializerTypeEnum.values()){
            if(typeEnum.code == code) return typeEnum.type;
        }
        throw new RuntimeException("no such serializer code or type...");
    }
}
