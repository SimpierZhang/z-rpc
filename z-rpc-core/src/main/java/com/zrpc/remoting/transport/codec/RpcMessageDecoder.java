package com.zrpc.remoting.transport.codec;

import com.zrpc.compress.Compressor;
import com.zrpc.compress.CompressorTypeEnum;
import com.zrpc.extension.ExtensionLoader;
import com.zrpc.remoting.constants.consts.RpcConstants;
import com.zrpc.remoting.dto.RpcMessage;
import com.zrpc.serializer.Serializer;
import com.zrpc.serializer.SerializerTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * custom z-rpc message protocol
 * </p>
 *
 * <pre>
 *   0     1     2     3     4        5              6         7          8           12            16
 *   +-----+-----+-----+-----+--------+-------------+---------+-----------+-----------+-------------+
 *   |   magic   code        |version | messageType |  codec   |  compress | RequestId | full length |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+---+
 *   |                                                                                              |
 *   |                                         body                                                 |
 *   |                                                                                              |
 *   |                                        ... ...                                               |
 *   +----------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id） 4B full length（消息长度）
 * body（object类型数据）
 * </pre>
 *
 * @Author: Zjw
 * @Description: custom z-rpc message decoder
 * @Create 2022-04-15 10:21
 * @Modifier:
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder{

    public RpcMessageDecoder(){
        this(RpcConstants.MAX_FRAME_LENGTH, 12, 4, -16, 0);
    }

    private RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        try {
            Object o = super.decode(ctx, in);
            if(o instanceof ByteBuf){
                ByteBuf frame = (ByteBuf) o;
                if(frame.readableBytes() > 0){
                    return decodeFrame(frame);
                }
            }
        }catch (Exception e){
            log.error("the process of decoding has error....[{}]", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Object decodeFrame(ByteBuf frame) {
        checkMagicNum(frame.readBytes(4));
        checkVersion(frame.readByte());
        byte messageType = frame.readByte();
        byte serializerCode = frame.readByte();
        byte compressorCode = frame.readByte();
        int requestId = frame.readInt();
        int dataLen = frame.readInt() - RpcConstants.HEAD_LENGTH;
        //checkRequestId(requestId);
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .serializeType(serializerCode)
                .compressType(compressorCode)
                .requestId(requestId)
                .build();
        if(dataLen > 0){
            byte[] dataBuffer = new byte[dataLen];
            frame.readBytes(dataBuffer);
            //get compressor and decompress data
            String compressorType = CompressorTypeEnum.getType(compressorCode);
            log.info("the compressor type is [{}]", compressorType);
            Compressor compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(compressorType);
            dataBuffer = compressor.decompress(dataBuffer);
            //get serializer and deserialize data
            String serializerType = SerializerTypeEnum.getType(serializerCode);
            log.info("the serializer type is [{}]", serializerType);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerType);
            Object data = serializer.deserialize(dataBuffer);
            if(data != null) rpcMessage.setData(data);
        }
        return rpcMessage;
    }

    private void checkRequestId(int requestId) {
        if(RpcMessageEncoder.REQUEST_ID.intValue() != requestId) throw new RuntimeException("request id check error...");
    }

    private void checkMagicNum(ByteBuf magicNumBuf) {
        byte[] magicNum = RpcConstants.MAGIC_NUMBER;
        byte[] checkedMagicNum = new byte[magicNum.length];
        magicNumBuf.readBytes(checkedMagicNum);
        for(int i = 0; i < magicNum.length; i++){
            if(magicNum[i] != checkedMagicNum[i]) throw new RuntimeException("magic num check error...");
        }
    }

    private void checkVersion(byte checkedVersion){
        byte version = RpcConstants.VERSION;
        if(checkedVersion != version) throw new RuntimeException("version check error...");
    }
}
