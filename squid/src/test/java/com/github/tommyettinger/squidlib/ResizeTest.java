package com.github.tommyettinger.squidlib;

import de.heidelberg.pvs.container_bench.generators.StringDictionaryGenerator;

import java.io.IOException;

public class ResizeTest {
    public static void main(String[] args) throws IOException {
        StringDictionaryGenerator gen = new StringDictionaryGenerator();
        gen.init(100000, 123);
        for(int size : new int[]{100, 1000, 10000, 100000}) {
            String[] arr = gen.generateArray(size);
            {
                FastUtilMap<String, Object> map = new FastUtilMap<>(16, 0.8f);
                int cap = map.getCapacity();
                for (int i = 0; i < size; i++) {
                    map.put(arr[i], null);
                    if(cap != map.getCapacity()){
                        cap = map.getCapacity();
                        System.out.println("Resized on " + i + ": " + arr[i]);
                    }
                }
            }
            {
                ObjectMapWatched<String, Object> map = new ObjectMapWatched<>(12, 0.8f);
                int cap = map.capacity;
                for (int i = 0; i < size; i++) {
                    map.put(arr[i], null);
                    if(cap != map.capacity){
                        cap = map.capacity;
                        System.out.println("Resized on " + i + ": " + arr[i]);
                    }
                }
            }
            {
                ObjectMapX<String, Object> map = new ObjectMapX<>(12, 0.8f);
                int cap = map.capacity;
                for (int i = 0; i < size; i++) {
                    map.put(arr[i], null);
                    if(cap != map.capacity){
                        cap = map.capacity;
                        System.out.println("Resized on " + i + ": " + arr[i]);
                    }
                }
            }
            {
                ObjectMapY<String, Object> map = new ObjectMapY<>(12, 0.8f);
                int cap = map.capacity;
                for (int i = 0; i < size; i++) {
                    map.put(arr[i], null);
                    if(cap != map.capacity){
                        cap = map.capacity;
                        System.out.println("Resized on " + i + ": " + arr[i]);
                    }
                }
            }
            {
                CuckooObjectMap<String, Object> map = new CuckooObjectMap<>(16, 0.8f);
                int cap = map.capacity;
                for (int i = 0; i < size; i++) {
                    map.put(arr[i], null);
                    if(cap != map.capacity){
                        cap = map.capacity;
                        System.out.println("Resized on " + i + ": " + arr[i]);
                    }
                }
            }
        }
    }
}
