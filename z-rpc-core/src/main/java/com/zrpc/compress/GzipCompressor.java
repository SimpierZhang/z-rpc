package com.zrpc.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: Zjw
 * @Description: the compressor of gzip implementation
 * @Create 2022-04-15 15:09
 * @Modifier:
 */
public class GzipCompressor implements Compressor {

    private static final int READ_SIZE = 512;

    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes == null) throw new IllegalArgumentException("the buffer to compress can't be null...");
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)){
            gzipOutputStream.write(bytes);
            gzipOutputStream.flush();
            //you need close gzipOutputStream，otherwise Gzip will throw "Unexpected end of ZLIB input stream" exception
            gzipOutputStream.finish();
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException("the process of gzip compress failed...", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes == null) throw new IllegalArgumentException("the buffer to decompress can't be null...");
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes))){
            byte[] buffer = new byte[READ_SIZE];
            int len = 0;
            while ((len = gzipInputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException("the process of gzip decompress failed...", e);
        }
    }
}
