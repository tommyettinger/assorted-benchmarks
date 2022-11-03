package com.github.tommyettinger.squidlib;

public class ResizePointTest {
    private static class GridPoint2 extends com.badlogic.gdx.math.GridPoint2{
        public GridPoint2(int x, int y) {
            super(x, y);
        }
        @Override
        public int hashCode() {
            //Cantor pairing function, but... with a little more meat on its bones.
//            return (((x + y) * (x + y + 0xA7C15) >> 1) + y);
//            return (((x + y) * (x + y + 0x99E73) >> 1) + y);
//            return (((x + y) * (x + y + 0xF9653) >> 1) + y);
//            final int sx = x >> 31, sy = y >> 31;
//            final int px = x ^ sx, py = y ^ sx;
//            return (((px + py) * (px + py + 1) >>> 1) + py << 2) - sx - sy - sy;

//            final int px = x << 1 ^ x >> 31, py = y << 1 ^ y >> 31;
//            return (((px + py) * (px + py + 1) >>> 1) + py);
            return (int) ((x * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >>> 9);
//            return (int) ((x * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >>> 8);
//            return (int) ((x * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >>> 24);
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
        final float LOAD = 0.9f;
        for(int edge : new int[]{10, 40, 90, 160, 250, 360, 490, 640}) {
            final int size = edge * edge, half = edge >>> 1;
            GridPoint2[] arr = new GridPoint2[size];
            for (int i = 0, c = 0; i < edge; i++) {
                for (int j = 0; j < edge; j++) {
                    arr[c++] = new GridPoint2(i - half, j - half);
                }
            }
            {
                FastUtilMap<GridPoint2, Object> map = new FastUtilMap<>(16, LOAD);
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
                ObjectMapWatched<GridPoint2, Object> map = new ObjectMapWatched<>(12, LOAD);
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
                ObjectMapX<GridPoint2, Object> map = new ObjectMapX<>(12, LOAD);
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
                ObjectMapY<GridPoint2, Object> map = new ObjectMapY<>(12, LOAD);
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
                CuckooObjectMap<GridPoint2, Object> map = new CuckooObjectMap<>(16, LOAD);
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
