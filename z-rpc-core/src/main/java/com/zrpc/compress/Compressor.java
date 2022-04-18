package com.zrpc.compress;

import com.zrpc.annotation.SPI;

/**
 * @Author: Zjw
 * @Description: the interface of compressor, which can be used to compress or decompress data
 * @Create 2022-04-15 11:34
 * @Modifier:
 */
@SPI("gzip")
public interface Compressor {

     byte[] compress(byte[] data);

     byte[] decompress(byte[] data);
}
