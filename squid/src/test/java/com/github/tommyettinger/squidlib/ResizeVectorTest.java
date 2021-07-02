package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.utils.NumberUtils;

public class ResizeVectorTest {
    private static class Vector2 extends com.badlogic.gdx.math.Vector2{
        public Vector2() {
            super();
        }

        public Vector2(float x, float y) {
            super(x, y);
        }

        public Vector2(com.badlogic.gdx.math.Vector2 v) {
            super(v);
        }

        @Override
        public int hashCode() {
            return (int) (NumberUtils.floatToRawIntBits(x) * 0xC13FA9A902A6328FL + NumberUtils.floatToRawIntBits(y) * 0x91E10DA5C79E7B1DL >>> 32);
        }
    }
    public static void main(String[] args) {
//        SplittableRandom random = new SplittableRandom(0xB0BAFE77);
//        for(int size : new int[]{100, 1000, 10000, 100000}) {
//            Vector2[] arr = new Vector2[size];
//            for (int i = 0; i < size; i++) {
////                long n = random.nextLong();
////                arr[i] = new Vector2((int)n, (int) (n >>> 32));
//
//                // cantor unpairing function
//                int w = (int)((Math.sqrt(8 * i + 1) - 1) * 0.5f);
//                int t = w * w + w >> 1;
//                int y = i - t;
//                arr[i] = new Vector2(w - y, y);
//            }
        for(int edge : new int[]{10, 40, 90, 160, 250}) {
            final int size = edge * edge, half = edge >>> 1;
            Vector2[] arr = new Vector2[size];
            for (int i = 0, c = 0; i < edge; i++) {
                for (int j = 0; j < edge; j++) {
                    arr[c++] = new Vector2(i - half, j - half);
                }
            }
            {
                FastUtilMap<Vector2, Object> map = new FastUtilMap<>(16, 0.8f);
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
                ObjectMapWatched<Vector2, Object> map = new ObjectMapWatched<>(12, 0.8f);
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
                ObjectMapX<Vector2, Object> map = new ObjectMapX<>(12, 0.8f);
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
                ObjectMapY<Vector2, Object> map = new ObjectMapY<>(12, 0.8f);
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
                CuckooObjectMap<Vector2, Object> map = new CuckooObjectMap<>(16, 0.8f);
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
