package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.ds.ObjectObjectMap;

import java.util.Collection;
import java.util.Map;

public class ObjectMapWatched<K, V> extends ObjectObjectMap<K, V> {
    /**
     * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
     */
    public ObjectMapWatched() {
        super();
    }

    /**
     * Creates a new map with the given starting capacity and a load factor of 0.8.
     *
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
     */
    public ObjectMapWatched(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
     * growing the backing table.
     *
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
     * @param loadFactor      what fraction of the capacity can be filled before this has to resize; 0 &lt; loadFactor &lt;= 1
     */
    public ObjectMapWatched(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Creates a new map identical to the specified map.
     *
     * @param map an ObjectObjectMap to copy
     */
    public ObjectMapWatched(ObjectObjectMap<? extends K, ? extends V> map) {
        super(map);
    }

    /**
     * Creates a new map identical to the specified map.
     *
     * @param map a Map to copy; ObjectObjectMap or its subclasses will be faster
     */
    public ObjectMapWatched(Map<? extends K, ? extends V> map) {
        super(map);
    }

    /**
     * Given two side-by-side arrays, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
     * If keys and values have different lengths, this only uses the length of the smaller array.
     *
     * @param keys   an array of keys
     * @param values an array of values
     */
    public ObjectMapWatched(K[] keys, V[] values) {
        super(keys, values);
    }

    /**
     * Given two side-by-side collections, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
     * If keys and values have different lengths, this only uses the length of the smaller collection.
     *
     * @param keys   a Collection of keys
     * @param values a Collection of values
     */
    public ObjectMapWatched(Collection<? extends K> keys, Collection<? extends V> values) {
        super(keys, values);
    }

    public int capacity = 0;
    @Override
    protected void resize(int newSize) {
        super.resize(newSize);
        capacity = newSize;
        System.out.println("\nObjectMapWatched resized to " + newSize);
    }
}
