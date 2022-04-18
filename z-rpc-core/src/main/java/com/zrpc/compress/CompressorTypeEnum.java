package com.zrpc.compress;

import com.zrpc.serializer.SerializerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: Zjw
 * @Description: a compressor type enum
 * @see SerializerTypeEnum
 * @Create 2022-04-15 14:25
 * @Modifier:
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum  CompressorTypeEnum {


    GZIP_SERIALIZER((byte) 0x01, "gzip");

    private byte code;
    private String type;

    public static String getType(byte code){
        for(CompressorTypeEnum typeEnum : CompressorTypeEnum.values()){
            if(typeEnum.code == code) return typeEnum.type;
        }
        throw new RuntimeException("no such compressor code or type...");
    }
}
