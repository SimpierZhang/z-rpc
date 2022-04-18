package com.zrpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author: Zjw
 * @Description: the serializer of kryo implementation
 * @Create 2022-04-15 15:13
 * @Modifier:
 */
public class KryoSerializer implements Serializer {
    //Since kryo is not thread safe, each thread should have its own kryo, input and output instances
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });



    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //Serialize object into byte arrays
            kryo.writeClassAndObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            throw new RuntimeException("the process of serialize failed...[{}]", e);
        }
    }


    @Override
    public <T> T deserialize(byte[] bytes) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //Deserialize byte arrays into object
            Object o = kryo.readClassAndObject(input);
            kryoThreadLocal.remove();
            return (T) o;
        }catch (Exception e){
            throw new RuntimeException("the process of deserialize failed...[{}]", e);

        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //Deserialize byte arrays into object
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return (T) o;
        }catch (Exception e){
            throw new RuntimeException("the process of serialize failed...[{}]", e);
        }
    }
}
