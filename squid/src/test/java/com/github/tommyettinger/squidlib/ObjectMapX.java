package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.ds.ObjectObjectMap;

import java.util.Map;

/**
 * Created by Tommy Ettinger on 9/16/2020.
 */
public class ObjectMapX<K, V> extends ObjectObjectMap<K, V> {
	public int capacity = 0;
	@Override
	protected void resize(int newSize) {
		super.resize(newSize);
		capacity = newSize;
		System.out.println("\nObjectMapX resized to " + newSize);

	}

	/**
	 * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
	 */
	public ObjectMapX() {
		super();
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public ObjectMapX(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 * @param loadFactor
	 */
	public ObjectMapX(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map
	 */
	public ObjectMapX(ObjectObjectMap<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map
	 */
	public ObjectMapX(Map<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Returns an index &gt;= 0 and &lt;= {@link #mask} for the specified {@code item}.
	 * @param item a non-null Object; its hashCode() method should be used by most implementations.
	 */
	@Override
	protected int place(Object item) {
		return item.hashCode() & mask;
	}
}
