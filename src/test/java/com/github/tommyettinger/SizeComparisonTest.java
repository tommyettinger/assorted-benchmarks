package com.github.tommyettinger;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.cyphercove.gdxtokryo.gdxserializers.utils.GdxArraySerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.luben.zstd.Zstd;
import com.github.tommyettinger.tantrum.libgdx.ArraySerializer;
import com.github.tommyettinger.tantrum.libgdx.QueueSerializer;
import com.github.tommyettinger.tantrum.libgdx.Vector3Serializer;
import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.apache.fury.meta.zstd.ZstdMetaCompressor;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class SizeComparisonTest {
    /**
     * Fury bytes length : 53
     * Fury compressed   : 62
     * Kryo bytes length : 47
     * Kryo compressed   : 56
     * JSON bytes length : 33
     * JSON compressed   : 42
     */
    @Test
    public void testSmallStringArray() {
        System.out.println("Small Array<String>");

        Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
        fury.registerSerializer(Array.class, new ArraySerializer(fury));
        fury.register(Array.class);

        Json json = new Json();

        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(Array.class, new GdxArraySerializer());

        Array<String> data = Array.with("Hello", "World", "!", "I", "am", "a", "test", "!", "yay");

        byte[] bytesFury = fury.serializeJavaObject(data);
        {
            Array<?> data2 = fury.deserializeJavaObject(bytesFury, Array.class);
            Assert.assertEquals(data, data2);
        }
        byte[] bytesFuryZstd = Zstd.compress(bytesFury);
        {
            Array<?> data2 = fury.deserializeJavaObject(Zstd.decompress(bytesFuryZstd, (int)Zstd.getFrameContentSize(bytesFuryZstd)), Array.class);
            Assert.assertEquals(data, data2);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        Output output = new Output(baos);
        kryo.writeObject(output, data);
        output.close();
        byte[] bytesKryo = baos.toByteArray();
        {
            Array<?> data2 = kryo.readObject(new Input(bytesKryo), Array.class);
            Assert.assertEquals(data, data2);
        }
        byte[] bytesKryoZstd = Zstd.compress(bytesKryo);
        {
            Array<?> data2 = kryo.readObject(new Input(Zstd.decompress(bytesKryoZstd, (int)Zstd.getFrameContentSize(bytesKryoZstd))), Array.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Array.class, String.class);
        byte[] bytesJson = text.getBytes(StandardCharsets.ISO_8859_1);
        {
            Array<?> data2 = json.fromJson(Array.class, String.class, text);;
            Assert.assertEquals(data, data2);
        }
        byte[] bytesJsonZstd = Zstd.compress(bytesJson);
        {
            String textJsonZstd = new String(Zstd.decompress(bytesJsonZstd, (int)Zstd.getFrameContentSize(bytesJsonZstd)), StandardCharsets.ISO_8859_1);
            Array<?> data2 = json.fromJson(Array.class, String.class, textJsonZstd);
            Assert.assertEquals(data, data2);
        }

        System.out.println("Fury bytes length : " + bytesFury.length);
        System.out.println("Fury compressed   : " + bytesFuryZstd.length);
        System.out.println("Kryo bytes length : " + bytesKryo.length);
        System.out.println("Kryo compressed   : " + bytesKryoZstd.length);
        System.out.println("JSON bytes length : " + bytesJson.length);
        System.out.println("JSON compressed   : " + bytesJsonZstd.length);
//        System.out.println("JSON data: " + text);
    }

    /**
     * Fury bytes length : 10072
     * Fury compressed   : 2847
     * Kryo bytes length : 9049
     * Kryo compressed   : 2848
     * JSON bytes length : 8021
     * JSON compressed   : 3738
     */
    @Test
    public void testLargeStringArray() {
        System.out.println("Large Array<String>");

        Fury fury = Fury.builder().withLanguage(Language.JAVA)
                .requireClassRegistration(true)
                .build();
        fury.registerSerializer(Array.class, new ArraySerializer(fury));
        fury.register(Array.class);

        Json json = new Json();

        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(Array.class, new GdxArraySerializer());

        Array<String> data = new Array<>(true, 1024, String.class);

        for (int i = 0; i < 1024; i++) {
            data.add(i + " " + i);
        }

        byte[] bytesFury = fury.serializeJavaObject(data);
        {
            Array<?> data2 = fury.deserializeJavaObject(bytesFury, Array.class);
            Assert.assertEquals(data, data2);
        }
        byte[] bytesFuryZstd = Zstd.compress(bytesFury);
        {
            Array<?> data2 = fury.deserializeJavaObject(Zstd.decompress(bytesFuryZstd, (int)Zstd.getFrameContentSize(bytesFuryZstd)), Array.class);
            Assert.assertEquals(data, data2);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        Output output = new Output(baos);
        kryo.writeObject(output, data);
        output.close();
        byte[] bytesKryo = baos.toByteArray();
        {
            Array<?> data2 = kryo.readObject(new Input(bytesKryo), Array.class);
            Assert.assertEquals(data, data2);
        }
        byte[] bytesKryoZstd = Zstd.compress(bytesKryo);
        {
            Array<?> data2 = kryo.readObject(new Input(Zstd.decompress(bytesKryoZstd, (int)Zstd.getFrameContentSize(bytesKryoZstd))), Array.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Array.class, String.class);
        byte[] bytesJson = text.getBytes(StandardCharsets.ISO_8859_1);
        {
            Array<?> data2 = json.fromJson(Array.class, String.class, text);;
            Assert.assertEquals(data, data2);
        }
        byte[] bytesJsonZstd = Zstd.compress(bytesJson);
        {
            String textJsonZstd = new String(Zstd.decompress(bytesJsonZstd, (int)Zstd.getFrameContentSize(bytesJsonZstd)), StandardCharsets.ISO_8859_1);
            Array<?> data2 = json.fromJson(Array.class, String.class, textJsonZstd);
            Assert.assertEquals(data, data2);
        }

        System.out.println("Fury bytes length : " + bytesFury.length);
        System.out.println("Fury compressed   : " + bytesFuryZstd.length);
        System.out.println("Kryo bytes length : " + bytesKryo.length);
        System.out.println("Kryo compressed   : " + bytesKryoZstd.length);
        System.out.println("JSON bytes length : " + bytesJson.length);
        System.out.println("JSON compressed   : " + bytesJsonZstd.length);
//        System.out.println("JSON data: " + text);
    }

    @Test
    public void testSmallVector3Array() {
        MathUtils.random.setSeed(1234567890L);
        Fury fury = Fury.builder().withLanguage(Language.JAVA).withRefTracking(true).build();
        fury.registerSerializer(Array.class, new ArraySerializer(fury));
        fury.register(Array.class);
        fury.register(Array.ArrayIterable.class);
        fury.register(Array.ArrayIterator.class);
        fury.registerSerializer(Vector3.class, new Vector3Serializer(fury));

        Json json = new Json();

        Array<Vector3> data = new Array<>(9);
        Vector3 sum = new Vector3();
        for (int i = 0; i < 9; i++) {
            data.add(new Vector3(MathUtils.random(), MathUtils.random(), MathUtils.random()));
        }
        for(Vector3 d : data){
            sum.add(d);
        }
        data.add(sum);

        byte[] bytes = fury.serializeJavaObject(data);
        {
            Array<?> data2 = fury.deserializeJavaObject(bytes, Array.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Array.class, Vector3.class);
        {
            Array<?> data2 = json.fromJson(Array.class, Vector3.class, text);;
            Assert.assertEquals(data, data2);
        }

        System.out.println("Small Array<Vector3>");
        System.out.println("Fury bytes length : " + bytes.length);
        System.out.println("Fury String length: " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        System.out.println("JSON bytes length : " + text.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println("JSON String length: " + text.length());
        System.out.println("JSON data: " + text);
    }

    @Test
    public void testLargeVector3Array() {
        MathUtils.random.setSeed(1234567890L);
        Fury fury = Fury.builder().withLanguage(Language.JAVA).withRefTracking(true).build();
        fury.registerSerializer(Array.class, new ArraySerializer(fury));
        fury.register(Array.class);
        fury.register(Array.ArrayIterable.class);
        fury.register(Array.ArrayIterator.class);
        fury.registerSerializer(Vector3.class, new Vector3Serializer(fury));

        Json json = new Json();

        Array<Vector3> data = new Array<>(1024);
        Vector3 sum = new Vector3();
        for (int i = 0; i < 1024; i++) {
            data.add(new Vector3(MathUtils.random(), MathUtils.random(), MathUtils.random()));
        }
        for(Vector3 d : data){
            sum.add(d);
        }
        data.add(sum);

        byte[] bytes = fury.serializeJavaObject(data);
        {
            Array<?> data2 = fury.deserializeJavaObject(bytes, Array.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Array.class, Vector3.class);
        {
            Array<?> data2 = json.fromJson(Array.class, Vector3.class, text);;
            Assert.assertEquals(data, data2);
        }

        System.out.println("Large Array<Vector3>");
        System.out.println("Fury bytes length : " + bytes.length);
        System.out.println("Fury String length: " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        System.out.println("JSON bytes length : " + text.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println("JSON String length: " + text.length());
        System.out.println("JSON data: " + text);
    }

    @Test
    public void testSmallStringQueue() {
        Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
        fury.registerSerializer(Queue.class, new QueueSerializer(fury));

        Json json = new Json();

        Queue<String> data = new Queue<>(9);
        for(String s : new String[]{"Hello", "World", "!", "I", "am", "a", "test", "!", "yay"}) {
            data.addLast(s);
        }

        byte[] bytes = fury.serializeJavaObject(data);
        {
            Queue<?> data2 = fury.deserializeJavaObject(bytes, Queue.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Queue.class, String.class);
        {
            Queue<?> data2 = json.fromJson(Queue.class, String.class, text);;
            Assert.assertEquals(data, data2);
        }

        System.out.println("Small Queue<String>");
        System.out.println("Fury bytes length : " + bytes.length);
        System.out.println("Fury String length: " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        System.out.println("JSON bytes length : " + text.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println("JSON String length: " + text.length());
        System.out.println("JSON data: " + text);
    }

    @Test
    public void testLargeStringQueue() {
        Fury fury = Fury.builder().withLanguage(Language.JAVA)
                .requireClassRegistration(true)
                .build();
        fury.registerSerializer(Queue.class, new QueueSerializer(fury));

        Json json = new Json();

        Queue<String> data = new Queue<>(1024);

        for (int i = 0; i < 1024; i++) {
            data.addLast(i + " " + i);
        }

        byte[] bytes = fury.serializeJavaObject(data);
        {
            Queue<?> data2 = fury.deserializeJavaObject(bytes, Queue.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Queue.class, String.class);
        {
            Queue<?> data2 = json.fromJson(Queue.class, String.class, text);;
            Assert.assertEquals(data, data2);
        }

        System.out.println("Large Queue<String>");
        System.out.println("Fury bytes length : " + bytes.length);
        System.out.println("Fury String length: " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        System.out.println("JSON bytes length : " + text.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println("JSON String length: " + text.length());
        System.out.println("JSON data: " + text);
    }

    @Test
    public void testSmallVector3Queue() {
        MathUtils.random.setSeed(1234567890L);
        Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
        fury.registerSerializer(Queue.class, new QueueSerializer(fury));
        fury.registerSerializer(Vector3.class, new Vector3Serializer(fury));

        Json json = new Json();

        Queue<Vector3> data = new Queue<>(9);
        for (int i = 0; i < 9; i++) {
            data.addLast(new Vector3(MathUtils.random(), MathUtils.random(), MathUtils.random()));
        }

        byte[] bytes = fury.serializeJavaObject(data);
        {
            Queue<?> data2 = fury.deserializeJavaObject(bytes, Queue.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Queue.class, Vector3.class);
        {
            Queue<?> data2 = json.fromJson(Queue.class, Vector3.class, text);;
            Assert.assertEquals(data, data2);
        }

        System.out.println("Small Queue<Vector3>");
        System.out.println("Fury bytes length : " + bytes.length);
        System.out.println("Fury String length: " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        System.out.println("JSON bytes length : " + text.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println("JSON String length: " + text.length());
        System.out.println("JSON data: " + text);
    }

    @Test
    public void testLargeVector3Queue() {
        MathUtils.random.setSeed(1234567890L);
        Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
        fury.registerSerializer(Queue.class, new QueueSerializer(fury));
        fury.registerSerializer(Vector3.class, new Vector3Serializer(fury));

        Json json = new Json();

        Queue<Vector3> data = new Queue<>(1024);
        Vector3 sum = new Vector3();
        for (int i = 0; i < 1024; i++) {
            data.addLast(new Vector3(MathUtils.random(), MathUtils.random(), MathUtils.random()));
        }
        for(Vector3 d : data){
            sum.add(d);
        }
        data.addLast(sum);

        byte[] bytes = fury.serializeJavaObject(data);
        {
            Queue<?> data2 = fury.deserializeJavaObject(bytes, Queue.class);
            Assert.assertEquals(data, data2);
        }

        String text = json.toJson(data, Queue.class, Vector3.class);
        {
            Queue<?> data2 = json.fromJson(Queue.class, Vector3.class, text);;
            Assert.assertEquals(data, data2);
        }

        System.out.println("Large Queue<Vector3>");
        System.out.println("Fury bytes length : " + bytes.length);
        System.out.println("Fury String length: " + new String(bytes, StandardCharsets.ISO_8859_1).length());
        System.out.println("JSON bytes length : " + text.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println("JSON String length: " + text.length());
        System.out.println("JSON data: " + text);
    }
}
