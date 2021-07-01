package com.github.tommyettinger.squidlib;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class FastUtilMap<K, V> extends Object2ObjectOpenHashMap<K, V>{
    public FastUtilMap(int expected, float f) {
        super(expected, f);
    }

    public FastUtilMap(int expected) {
        super(expected);
    }

    public FastUtilMap() {
        super();
    }

    public FastUtilMap(Map<? extends K, ? extends V> m, float f) {
        super(m, f);
    }

    public FastUtilMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public FastUtilMap(Object2ObjectMap<K, V> m, float f) {
        super(m, f);
    }

    public FastUtilMap(Object2ObjectMap<K, V> m) {
        super(m);
    }

    public FastUtilMap(K[] k, V[] v, float f) {
        super(k, v, f);
    }

    public FastUtilMap(K[] k, V[] v) {
        super(k, v);
    }

    @Override
    protected void rehash(int newN) {
        super.rehash(newN);
        System.out.println("\nFastUtilMap resized to " + newN);
    }

    public int getCapacity(){
        return n;
    }
}
