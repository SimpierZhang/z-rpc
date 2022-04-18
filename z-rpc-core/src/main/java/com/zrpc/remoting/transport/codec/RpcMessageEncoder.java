package com.zrpc.remoting.transport.codec;

import com.zrpc.compress.Compressor;
import com.zrpc.compress.CompressorTypeEnum;
import com.zrpc.extension.ExtensionLoader;
import com.zrpc.remoting.constants.consts.RpcConstants;
import com.zrpc.remoting.dto.RpcMessage;
import com.zrpc.remoting.dto.RpcResponse;
import com.zrpc.serializer.Serializer;
import com.zrpc.serializer.SerializerTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
 * @Description: custom z-rpc message encoder
 * @Create 2022-04-15 10:21
 * @Modifier:
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    public static final AtomicInteger REQUEST_ID = new AtomicInteger(0);
    private final Map<String, Serializer> serializerMap = new ConcurrentHashMap<>();

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            out.writeByte(msg.getMessageType());
            out.writeByte(msg.getSerializeType());
            out.writeByte(msg.getCompressType());
            out.writeInt(REQUEST_ID.incrementAndGet());
            int dataLen = RpcConstants.HEAD_LENGTH;
            byte[] buffer = null;
            //get serializer and serialize data
            String serializerType = SerializerTypeEnum.getType(msg.getSerializeType());
            log.info("the serializer type is [{}]", serializerType);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerType);
            buffer = serializer.serialize(msg.getData());
            //get compressor and compress data
            String compressorType = CompressorTypeEnum.getType(msg.getCompressType());
            log.info("the compressor type is [{}]", compressorType);
            Compressor compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(compressorType);
            buffer = compressor.compress(buffer);
            if(buffer != null){
                dataLen += buffer.length;
            }
            out.writeInt(dataLen);
            if(buffer != null) out.writeBytes(buffer);
        }catch (Exception e){
            log.error("the process of encoding has error....[{}]", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
