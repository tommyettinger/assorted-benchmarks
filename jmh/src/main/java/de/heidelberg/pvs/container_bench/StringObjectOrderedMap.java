package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectObjectOrderedMap;

import java.util.Collection;
import java.util.Map;

public class StringObjectOrderedMap<V> extends ObjectObjectOrderedMap<String, V> {
    /**
     * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
     */
    public StringObjectOrderedMap() {
        super();
    }

    /**
     * Creates a new map with the given starting capacity and a load factor of 0.8.
     *
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
     */
    public StringObjectOrderedMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
     * growing the backing table.
     *
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
     * @param loadFactor      what fraction of the capacity can be filled before this has to resize; 0 &lt; loadFactor &lt;= 1
     */
    public StringObjectOrderedMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Creates a new map identical to the specified map.
     *
     * @param map the map to copy
     */
    public StringObjectOrderedMap(ObjectObjectOrderedMap<? extends String, ? extends V> map) {
        super(map);
    }

    /**
     * Creates a new map identical to the specified map.
     *
     * @param map the map to copy
     */
    public StringObjectOrderedMap(Map<? extends String, ? extends V> map) {
        super(map);
    }

    /**
     * Given two side-by-side arrays, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
     * If keys and values have different lengths, this only uses the length of the smaller array.
     *
     * @param keys   an array of keys
     * @param values an array of values
     */
    public StringObjectOrderedMap(String[] keys, V[] values) {
        super(keys, values);
    }

    /**
     * Given two side-by-side collections, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
     * If keys and values have different lengths, this only uses the length of the smaller collection.
     *
     * @param keys   a Collection of keys
     * @param values a Collection of values
     */
    public StringObjectOrderedMap(Collection<? extends String> keys, Collection<? extends V> values) {
        super(keys, values);
    }

    /**
     * Returns an index &gt;= 0 and &lt;= {@link #mask} for the specified {@code item}.
     * <p>
     * The default behavior uses Fibonacci hashing; it simply gets the {@link Object#hashCode()}
     * of {@code item}, multiplies it by a specific long constant related to the golden ratio,
     * and makes an unsigned right shift by {@link #shift} before casting to int and returning.
     * This can be overridden to hash {@code item} differently, though all implementors must
     * ensure this returns results in the range of 0 to {@link #mask}, inclusive. If nothing
     * else is changed, then unsigned-right-shifting an int or long by {@link #shift} will also
     * restrict results to the correct range.
     *
     * @param item a non-null Object; its hashCode() method should be used by most implementations.
     */
    @Override
    protected int place(Object item) {
        return item.hashCode() & mask;
    }
}
