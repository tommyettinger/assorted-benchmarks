package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectObjectMap;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Tommy Ettinger on 3/16/2024.
 */
public class ObjectMapToString<K, V> extends ObjectObjectMap<K, V> {
	/**
	 * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
	 */
	public ObjectMapToString() {
		super();
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public ObjectMapToString(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 * @param loadFactor
	 */
	public ObjectMapToString(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map
	 */
	public ObjectMapToString(ObjectObjectMap<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map
	 */
	public ObjectMapToString(Map<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Given two side-by-side arrays, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
	 * If keys and values have different lengths, this only uses the length of the smaller array.
	 *
	 * @param keys   an array of keys
	 * @param values an array of values
	 */
	public ObjectMapToString(K[] keys, V[] values) {
		super(keys, values);
	}

	/**
	 * Given two side-by-side collections, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
	 * If keys and values have different lengths, this only uses the length of the smaller collection.
	 *
	 * @param keys   a Collection of keys
	 * @param values a Collection of values
	 */
	public ObjectMapToString(Collection<? extends K> keys, Collection<? extends V> values) {
		super(keys, values);
	}

	/**
	 * Returns an index &gt;= 0 and &lt;= {@link #mask} for the specified {@code item}.
	 *
	 * @param item a non-null Object; its toString() method's hash code is used
	 */
	@Override
	protected int place(Object item) {
		return item.toString().hashCode() & mask;
	}

	/**
	 * Compares the objects left and right, which are usually keys, for equality, returning true if they are considered
	 * equal. This is used by the rest of this class to determine whether two keys are considered equal. Normally, this
	 * returns {@code left.equals(right)}, but subclasses can override it to use reference equality, fuzzy equality, deep
	 * array equality, or any other custom definition of equality. Usually, {@link #place(Object)} is also overridden if
	 * this method is.
	 *
	 * @param left  must be non-null; typically a key being compared, but not necessarily
	 * @param right may be null; typically a key being compared, but can often be null for an empty key slot, or some other type
	 * @return true if left and right are considered equal for the purposes of this class
	 */
	@Override
	protected boolean equate(Object left, @Nullable Object right) {
		return right != null && left.toString().equals(right.toString());
	}
}
