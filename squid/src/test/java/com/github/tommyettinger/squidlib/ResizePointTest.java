package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.math.GridPoint2;

import java.util.SplittableRandom;

public class ResizePointTest {
    public static void main(String[] args) {
//        SplittableRandom random = new SplittableRandom(0xB0BAFE77);
        for(int size : new int[]{100, 1000, 10000, 100000}) {
            GridPoint2[] arr = new GridPoint2[size];
            for (int i = 0; i < size; i++) {
//                long n = random.nextLong();
//                arr[i] = new GridPoint2((int)n, (int) (n >>> 32));

                // cantor unpairing function
                int w = (int)((Math.sqrt(8 * i + 1) - 1) * 0.5f);
                int t = w * w + w >> 1;
                int y = i - t;
                arr[i] = new GridPoint2(w - y, y);
            }
            {
                FastUtilMap<GridPoint2, Object> map = new FastUtilMap<>(16, 0.8f);
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
                ObjectMapWatched<GridPoint2, Object> map = new ObjectMapWatched<>(12, 0.8f);
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
                ObjectMapX<GridPoint2, Object> map = new ObjectMapX<>(12, 0.8f);
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
                ObjectMapY<GridPoint2, Object> map = new ObjectMapY<>(12, 0.8f);
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
                CuckooObjectMap<GridPoint2, Object> map = new CuckooObjectMap<>(16, 0.8f);
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
