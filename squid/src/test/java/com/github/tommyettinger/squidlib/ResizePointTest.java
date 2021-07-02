package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.math.GridPoint2;

import java.util.SplittableRandom;

public class ResizePointTest {
    private static class GridPoint2 extends com.badlogic.gdx.math.GridPoint2{
        public GridPoint2() {
            super();
        }

        public GridPoint2(int x, int y) {
            super(x, y);
        }

        public GridPoint2(com.badlogic.gdx.math.GridPoint2 point) {
            super(point);
        }

        @Override
        public int hashCode() {
            //Cantor pairing function, but... with a little more meat on its bones.
            return (((x + y) * (x + y + 0xA7C15) >> 1) + y);
        }
    }
    public static void main(String[] args) {
//        SplittableRandom random = new SplittableRandom(0xB0BAFE77);
//        for(int size : new int[]{100, 1000, 10000, 100000}) {
//            GridPoint2[] arr = new GridPoint2[size];
//            for (int i = 0; i < size; i++) {
////                long n = random.nextLong();
////                arr[i] = new GridPoint2((int)n, (int) (n >>> 32));
//
//                // cantor unpairing function
//                int w = (int)((Math.sqrt(8 * i + 1) - 1) * 0.5f);
//                int t = w * w + w >> 1;
//                int y = i - t;
//                arr[i] = new GridPoint2(w - y, y);
//            }
        for(int edge : new int[]{10, 40, 90, 160, 250}) {
            final int size = edge * edge, half = edge >>> 1;
            GridPoint2[] arr = new GridPoint2[size];
            for (int i = 0, c = 0; i < edge; i++) {
                for (int j = 0; j < edge; j++) {
                    arr[c++] = new GridPoint2(i - half, j - half);
                }
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
