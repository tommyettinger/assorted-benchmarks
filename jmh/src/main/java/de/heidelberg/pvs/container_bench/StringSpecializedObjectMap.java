/*
 * Copyright (c) 2022-2025 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.digital.BitConversion;
import com.github.tommyettinger.ds.*;
import com.github.tommyettinger.ds.support.util.Appender;
import com.github.tommyettinger.ds.support.util.PartialParser;
import com.github.tommyettinger.function.ObjObjToObjBiFunction;

import java.io.IOException;
import java.util.*;

import static com.github.tommyettinger.ds.Utilities.neverIdentical;
import static com.github.tommyettinger.ds.Utilities.tableSize;

/**
 * An unordered map where the keys and values are objects. Null keys are not allowed. No allocation is done except when growing
 * the table size.
 * <p>
 * This class performs fast contains and remove (typically O(1), worst case O(n) but that is rare in practice). Add may be
 * slightly slower, depending on hash collisions. Hash codes are mixed to reduce collisions and the need to resize. Load factors
 * greater than 0.91 greatly increase the chances to resize to the next higher POT size.
 * <p>
 * Unordered sets and maps are not designed to provide especially fast iteration. Iteration is faster with {@link Ordered} types like
 * ObjectOrderedSet and ObjectObjectOrderedMap.
 * <p>
 * You can customize most behavior of this map by extending it. {@link #place(Object)} can be overridden to change how hashCodes
 * are calculated (which can be useful for types like {@link StringBuilder} that don't implement hashCode()), and
 * {@link #equate(Object, Object)} can be overridden to change how equality is calculated.
 * <p>
 * This implementation uses linear probing with the backward shift algorithm for removal.
 * Linear probing continues to work even when all hashCodes collide; it just works more slowly in that case.
 *
 * @author Nathan Sweet
 * @author Tommy Ettinger
 */
public class StringSpecializedObjectMap<V> implements Map<String, V>, Iterable<Map.Entry<String, V>> {

	protected int size;

	protected String[] keyTable;
	protected V[] valueTable;

	/**
	 * Between 0f (exclusive) and 1f (inclusive, if you're careful), this determines how full the backing tables
	 * can get before this increases their size. Larger values use less memory but make the data structure slower.
	 */
	protected float loadFactor;

	/**
	 * Precalculated value of {@code (int)(keyTable.length * loadFactor)}, used to determine when to resize.
	 */
	protected int threshold;

	/**
	 * Used by {@link #place(Object)} typically, this should always equal {@code BitConversion.countLeadingZeros(mask)}.
	 * For a table that could hold 2 items (with 1 bit indices), this would be {@code 64 - 1 == 63}. For a table that
	 * could hold 256 items (with 8 bit indices), this would be {@code 64 - 8 == 56}.
	 */
	protected int shift;

	/**
	 * A bitmask used to confine hashcodes to the size of the table. Must be all 1-bits in its low positions, ie a power of two
	 * minus 1. If {@link #place(Object)} is overridden, this can be used instead of {@link #shift} to isolate usable bits of a
	 * hash.
	 */
	protected int mask;

	/**
	 * Returned by {@link #get(Object)} when no value exists for the given key, as well as some other methods to indicate that
	 * no value in the Map could be returned.
	 */
	public V defaultValue = null;

	/**
	 * Creates a new map with an initial capacity of {@link Utilities#getDefaultTableCapacity()} and a load factor of {@link Utilities#getDefaultLoadFactor()}.
	 */
	public StringSpecializedObjectMap() {
		this(Utilities.getDefaultTableCapacity(), Utilities.getDefaultLoadFactor());
	}

	/**
	 * Creates a new map with the given starting capacity and a load factor of {@link Utilities#getDefaultLoadFactor()}.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public StringSpecializedObjectMap(int initialCapacity) {
		this(initialCapacity, Utilities.getDefaultLoadFactor());
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 * @param loadFactor      what fraction of the capacity can be filled before this has to resize; 0 &lt; loadFactor &lt;= 1
	 */
	public StringSpecializedObjectMap(int initialCapacity, float loadFactor) {
		if (loadFactor <= 0f || loadFactor > 1f) {
			throw new IllegalArgumentException("loadFactor must be > 0 and <= 1: " + loadFactor);
		}
		this.loadFactor = loadFactor;

		int tableSize = tableSize(initialCapacity, loadFactor);
		threshold = (int) (tableSize * loadFactor);
		mask = tableSize - 1;
		shift = BitConversion.countLeadingZeros(mask) + 32;
		keyTable = new String[tableSize];
		valueTable = (V[]) new Object[tableSize];
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map an StringSpecializedObjectMap to copy
	 */
	public StringSpecializedObjectMap(StringSpecializedObjectMap<? extends V> map) {
		this.loadFactor = map.loadFactor;
		this.threshold = map.threshold;
		this.mask = map.mask;
		this.shift = map.shift;
		keyTable = Arrays.copyOf(map.keyTable, map.keyTable.length);
		valueTable = Arrays.copyOf(map.valueTable, map.valueTable.length);
		size = map.size;
		defaultValue = map.defaultValue;
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map a Map to copy; StringSpecializedObjectMap or its subclasses will be faster
	 */
	public StringSpecializedObjectMap(Map<String, ? extends V> map) {
		this(map.size());
		putAll(map);
	}

	/**
	 * Given two side-by-side arrays, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
	 * If keys and values have different lengths, this only uses the length of the smaller array.
	 *
	 * @param keys   an array of keys
	 * @param values an array of values
	 */
	public StringSpecializedObjectMap(String[] keys, V[] values) {
		this(Math.min(keys.length, values.length));
		putAll(keys, values);
	}

	/**
	 * Given two side-by-side collections, one of keys, one of values, this constructs a map and inserts each pair of key and value into it.
	 * If keys and values have different lengths, this only uses the length of the smaller collection.
	 *
	 * @param keys   a Collection of keys
	 * @param values a Collection of values
	 */
	public StringSpecializedObjectMap(Collection<? extends String> keys, Collection<? extends V> values) {
		this(Math.min(keys.size(), values.size()));
		putAll(keys, values);
	}

	/**
	 * Returns an index &gt;= 0 and &lt;= {@link #mask} for the specified {@code item}, mixed.
	 *
	 * @param item a non-null Object; its hashCode() method should be used by most implementations
	 * @return an index between 0 and {@link #mask} (both inclusive)
	 */
	protected int place(Object item) {
		return BitConversion.imul(item.hashCode() ^ 0xC143F257, 0xFAB9E45B) >>> shift;
		// This can be used if you know hashCode() has few collisions normally, and won't be maliciously manipulated.
//		return item.hashCode() & mask;
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
	protected boolean equate(Object left, Object right) {
		return left.equals(right);
	}

	/**
	 * Returns the index of the key if already present, else {@code ~index} for the next empty index. This calls
	 * {@link #equate(Object, Object)} to determine if two keys are equivalent.
	 *
	 * @param key a non-null String key
	 * @return a negative index if the key was not found, or the non-negative index of the existing key if found
	 */
	protected int locateKey(Object key) {
		String[] keyTable = this.keyTable;
		for (int i = place(key); ; i = i + 1 & mask) {
			String other = keyTable[i];
			if (equate(key, other))
				return i; // Same key was found.
			if (other == null)
				return ~i; // Always negative; means empty space is available at i.
		}
	}

	/**
	 * Returns the old value associated with the specified key, or this map's {@link #defaultValue} if there was no prior value.
	 */
	@Override
	public V put(String key, V value) {
		if (key == null) return defaultValue;
		int i = locateKey(key);
		if (i >= 0) { // Existing key was found.
			V oldValue = valueTable[i];
			valueTable[i] = value;
			return oldValue;
		}
		i = ~i; // Empty space was found.
		keyTable[i] = key;
		valueTable[i] = value;
		if (++size >= threshold) {
			resize(keyTable.length << 1);
		}
		return defaultValue;
	}

	public V putOrDefault(String key, V value, V defaultValue) {
		if (key == null) return defaultValue;
		int i = locateKey(key);
		if (i >= 0) { // Existing key was found.
			V oldValue = valueTable[i];
			valueTable[i] = value;
			return oldValue;
		}
		i = ~i; // Empty space was found.
		keyTable[i] = key;
		valueTable[i] = value;
		if (++size >= threshold) {
			resize(keyTable.length << 1);
		}
		return defaultValue;
	}

	/**
	 * Copies all the mappings from the specified map to this map
	 * (optional operation).  The effect of this call is equivalent to that
	 * of calling {@link #put(String, Object) put(k, v)} on this map once
	 * for each mapping from key {@code k} to value {@code v} in the
	 * specified map.  The behavior of this operation is undefined if the
	 * specified map is modified while the operation is in progress.
	 * <br>
	 * Note that {@link #putAll(StringSpecializedObjectMap)} is more specific and can be
	 * more efficient by using the internal details of this class.
	 *
	 * @param m mappings to be stored in this map
	 * @throws UnsupportedOperationException if the {@code putAll} operation
	 *                                       is not supported by this map
	 * @throws ClassCastException            if the class of a key or value in the
	 *                                       specified map prevents it from being stored in this map
	 * @throws NullPointerException          if the specified map is null, or if
	 *                                       this map does not permit null keys or values, and the
	 *                                       specified map contains null keys or values
	 * @throws IllegalArgumentException      if some property of a key or value in
	 *                                       the specified map prevents it from being stored in this map
	 */
	@Override
	public void putAll(Map<? extends String, ? extends V> m) {
		ensureCapacity(m.size());
		for (Map.Entry<? extends String, ? extends V> kv : m.entrySet()) {
			put(kv.getKey(), kv.getValue());
		}
	}

	/**
	 * Puts every key-value pair in the given map into this, with the values from the given map
	 * overwriting the previous values if two keys are identical.
	 *
	 * @param map a map with compatible key and value types; will not be modified
	 */
	public void putAll(StringSpecializedObjectMap<? extends V> map) {
		ensureCapacity(map.size);
		String[] keyTable = map.keyTable;
		V[] valueTable = map.valueTable;
		String key;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			key = keyTable[i];
			if (key != null) {
				put(key, valueTable[i]);
			}
		}
	}

	/**
	 * Given two side-by-side collections, one of keys, one of values, this inserts each pair of key and value into this map with put().
	 *
	 * @param keys   a Collection of keys
	 * @param values a Collection of values
	 */
	public void putAll(Collection<? extends String> keys, Collection<? extends V> values) {
		int length = Math.min(keys.size(), values.size());
		ensureCapacity(length);
		String key;
		Iterator<? extends String> ki = keys.iterator();
		Iterator<? extends V> vi = values.iterator();
		while (ki.hasNext() && vi.hasNext()) {
			key = ki.next();
			if (key != null) {
				put(key, vi.next());
			}
		}
	}

	/**
	 * Given two side-by-side arrays, one of keys, one of values, this inserts each pair of key and value into this map with put().
	 *
	 * @param keys   an array of keys
	 * @param values an array of values
	 */
	public void putAll(String[] keys, V[] values) {
		putAll(keys, 0, values, 0, Math.min(keys.length, values.length));
	}

	/**
	 * Given two side-by-side arrays, one of keys, one of values, this inserts each pair of key and value into this map with put().
	 *
	 * @param keys   an array of keys
	 * @param values an array of values
	 * @param length how many items from keys and values to insert, at-most
	 */
	public void putAll(String[] keys, V[] values, int length) {
		putAll(keys, 0, values, 0, length);
	}

	/**
	 * Given two side-by-side arrays, one of keys, one of values, this inserts each pair of key and value into this map with put().
	 *
	 * @param keys        an array of keys
	 * @param keyOffset   the first index in keys to insert
	 * @param values      an array of values
	 * @param valueOffset the first index in values to insert
	 * @param length      how many items from keys and values to insert, at-most
	 */
	public void putAll(String[] keys, int keyOffset, V[] values, int valueOffset, int length) {
		length = Math.min(length, Math.min(keys.length - keyOffset, values.length - valueOffset));
		ensureCapacity(length);
		String key;
		for (int k = keyOffset, v = valueOffset, i = 0, n = length; i < n; i++, k++, v++) {
			key = keys[k];
			if (key != null) {
				put(key, values[v]);
			}
		}
	}

	/**
	 * Skips checks for existing keys, doesn't increment size.
	 */
	protected void putResize(String key, V value) {
		String[] keyTable = this.keyTable;
		for (int i = place(key); ; i = i + 1 & mask) {
			if (keyTable[i] == null) {
				keyTable[i] = key;
				valueTable[i] = value;
				return;
			}
		}
	}

	/**
	 * Returns the value for the specified key, or {@link #defaultValue} if the key is not in the map.
	 * Note that {@link #defaultValue} is often null, which is also a valid value that can be assigned to a
	 * legitimate key. Checking that the result of this method is null does not guarantee that the
	 * {@code key} is not present.
	 *
	 * @param key a non-null Object that should almost always be a {@code String} (or an instance of a subclass of {@code String})
	 */
	@Override
	public V get(Object key) {
		if (key == null) return defaultValue;
		String[] keyTable = this.keyTable;
		for (int i = place(key); ; i = i + 1 & mask) {
			String other = keyTable[i];
			if (equate(key, other))
				return valueTable[i];
			if (other == null)
				return defaultValue;
		}
	}

	/**
	 * Returns the value for the specified key, or the given default value if the key is not in the map.
	 */
	public V getOrDefault(Object key, V defaultValue) {
		if (key == null) return defaultValue;
		String[] keyTable = this.keyTable;
		for (int i = place(key); ; i = i + 1 & mask) {
			String other = keyTable[i];
			if (equate(key, other))
				return valueTable[i];
			if (other == null)
				return defaultValue;
		}
	}

	@Override
	public V remove(Object key) {
		if (key == null) return defaultValue;
		int pos = locateKey(key);
		if (pos < 0) return defaultValue;
		String rem;
		String[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		V oldValue = valueTable[pos];

		int mask = this.mask, last, slot;
		size--;
		for (; ; ) {
			pos = ((last = pos) + 1) & mask;
			for (; ; ) {
				if ((rem = keyTable[pos]) == null) {
					keyTable[last] = null;
					valueTable[last] = null;
					return oldValue;
				}
				slot = place(rem);
				if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
				pos = (pos + 1) & mask;
			}
			keyTable[last] = rem;
			valueTable[last] = valueTable[pos];
		}
	}

	/**
	 * Returns true if the map has one or more items.
	 */
	public boolean notEmpty() {
		return size != 0;
	}

	/**
	 * Returns the number of key-value mappings in this map.  If the
	 * map contains more than {@code Integer.MAX_VALUE} elements, returns
	 * {@code Integer.MAX_VALUE}.
	 *
	 * @return the number of key-value mappings in this map
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns true if the map is empty.
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Gets the default value, a {@code V} which is returned by {@link #get(Object)} if the key is not found.
	 * If not changed, the default value is null.
	 *
	 * @return the current default value
	 */
	public V getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value, a {@code V} which is returned by {@link #get(Object)} if the key is not found.
	 * If not changed, the default value is null. Note that {@link #getOrDefault(Object, Object)} is also available,
	 * which allows specifying a "not-found" value per-call.
	 *
	 * @param defaultValue may be any V object or null; should usually be one that doesn't occur as a typical value
	 */
	public void setDefaultValue(V defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Reduces the size of the backing arrays to be the specified capacity / loadFactor, or less. If the capacity is already less,
	 * nothing is done. If the map contains more items than the specified capacity, the next highest power of two capacity is used
	 * instead.
	 */
	public void shrink(int maximumCapacity) {
		if (maximumCapacity < 0) {
			throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
		}
		int tableSize = tableSize(Math.max(maximumCapacity, size), loadFactor);
		if (keyTable.length > tableSize) {
			resize(tableSize);
		}
	}

	/**
	 * Clears the map and reduces the size of the backing arrays to be the specified capacity / loadFactor, if they are larger.
	 */
	public void clear(int maximumCapacity) {
		int tableSize = tableSize(maximumCapacity, loadFactor);
		if (keyTable.length <= tableSize) {
			clear();
			return;
		}
		size = 0;
		resize(tableSize);
	}

	@Override
	public void clear() {
		if (size == 0) {
			return;
		}
		size = 0;
		Utilities.clear(keyTable);
		Utilities.clear(valueTable);
	}

	/**
	 * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may
	 * be an expensive operation.
	 *
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *                 {@link #equals(Object)}.
	 */
	public boolean containsValue(Object value, boolean identity) {
		V[] valueTable = this.valueTable;
		if (value == null) {
			String[] keyTable = this.keyTable;
			for (int i = valueTable.length - 1; i >= 0; i--) {
				if (keyTable[i] != null && valueTable[i] == null) {
					return true;
				}
			}
		} else if (identity) {
			for (int i = valueTable.length - 1; i >= 0; i--) {
				if (valueTable[i] == value) {
					return true;
				}
			}
		} else {
			for (int i = valueTable.length - 1; i >= 0; i--) {
				if (value.equals(valueTable[i])) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) return false;
		String[] keyTable = this.keyTable;
		for (int i = place(key); ; i = i + 1 & mask) {
			String other = keyTable[i];
			if (equate(key, other))
				return true;
			if (other == null)
				return false;
		}
	}

	/**
	 * Returns {@code true} if this map maps one or more keys to the
	 * specified value.  More formally, returns {@code true} if and only if
	 * this map contains at least one mapping to a value {@code v} such that
	 * {@code (value==null ? v==null : value.equals(v))}.  This operation
	 * will probably require time linear in the map size for most
	 * implementations of the {@code Map} interface.
	 *
	 * @param value value whose presence in this map is to be tested
	 * @return {@code true} if this map maps one or more keys to the
	 * specified value
	 * @throws ClassCastException   if the value is of an inappropriate type for
	 *                              this map
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified value is null and this
	 *                              map does not permit null values
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public boolean containsValue(Object value) {
		return containsValue(value, false);
	}

	/**
	 * Returns a key that maps to the specified value, or null if value is not in the map.
	 * Note, this traverses the entire map and compares
	 * every value using {@link Object#equals(Object)}, which may be an expensive operation.
	 * This is the same as calling {@code findKey(value, false)}.
	 *
	 * @param value the value to search for
	 * @return a key that maps to value, if present, or null if value cannot be found
	 */
	public String findKey(Object value) {
		return findKey(value, false);
	}

	/**
	 * Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and compares
	 * every value, which may be an expensive operation.
	 *
	 * @param value    the value to search for
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *                 {@link #equals(Object)}.
	 * @return a key that maps to value, if present, or null if value cannot be found
	 */
	public String findKey(Object value, boolean identity) {
		V[] valueTable = this.valueTable;
		if (value == null) {
			String[] keyTable = this.keyTable;
			for (int i = valueTable.length - 1; i >= 0; i--) {
				if (keyTable[i] != null && valueTable[i] == null) {
					return keyTable[i];
				}
			}
		} else if (identity) {
			for (int i = valueTable.length - 1; i >= 0; i--) {
				if (valueTable[i] == value) {
					return keyTable[i];
				}
			}
		} else {
			for (int i = valueTable.length - 1; i >= 0; i--) {
				if (value.equals(valueTable[i])) {
					return keyTable[i];
				}
			}
		}
		return null;
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items / loadFactor. Useful before
	 * adding many items to avoid multiple backing array resizes.
	 *
	 * @param additionalCapacity how many additional items this should be able to hold without resizing (probably)
	 */
	public void ensureCapacity(int additionalCapacity) {
		int tableSize = tableSize(size + additionalCapacity, loadFactor);
		if (keyTable.length < tableSize) {
			resize(tableSize);
		}
	}

	protected void resize(int newSize) {
		int oldCapacity = keyTable.length;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		shift = BitConversion.countLeadingZeros(mask) + 32;

		String[] oldKeyTable = keyTable;
		V[] oldValueTable = valueTable;

		keyTable = (String[]) new Object[newSize];
		valueTable = (V[]) new Object[newSize];

		if (size > 0) {
			for (int i = 0; i < oldCapacity; i++) {
				String key = oldKeyTable[i];
				if (key != null) {
					putResize(key, oldValueTable[i]);
				}
			}
		}
	}

	/**
	 * Effectively does nothing here because the hashMultiplier is not used currently.
	 *
	 * @return 1; a hashMultiplier is not used in this class
	 */
	public int getHashMultiplier() {
		return 1;
	}

	/**
	 * Effectively does nothing here because the hashMultiplier is not used currently.
	 * Subclasses can use this to set some kind of identifier or user data, though.
	 *
	 * @param hashMultiplier any int; will not be used
	 */
	public void setHashMultiplier(int hashMultiplier) {
	}

	/**
	 * Gets the length of the internal array used to store all keys, as well as empty space awaiting more items to be
	 * entered. This length is equal to the length of the array used to store all values, and empty space for values,
	 * here. This is also called the capacity.
	 *
	 * @return the length of the internal array that holds all keys
	 */
	public int getTableSize() {
		return keyTable.length;
	}

	public float getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(float loadFactor) {
		if (loadFactor <= 0f || loadFactor > 1f) {
			throw new IllegalArgumentException("loadFactor must be > 0 and <= 1: " + loadFactor);
		}
		this.loadFactor = loadFactor;
		int tableSize = tableSize(size, loadFactor);
		if (tableSize - 1 != mask) {
			resize(tableSize);
		}
	}

	@Override
	public int hashCode() {
		int h = size;
		String[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			String key = keyTable[i];
			if (key != null) {
				h ^= key.hashCode();
				V value = valueTable[i];
				if (value != null) {
					h ^= value.hashCode();
				}
			}
		}
		return h;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Map)) {
			return false;
		}
		Map other = (Map) obj;
		if (other.size() != size) {
			return false;
		}
		String[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		try {
			for (int i = 0, n = keyTable.length; i < n; i++) {
				String key = keyTable[i];
				if (key != null) {
					V value = valueTable[i];
					if (value == null) {
						if (other.getOrDefault(key, neverIdentical) != null) {
							return false;
						}
					} else {
						if (!value.equals(other.get(key))) {
							return false;
						}
					}
				}
			}
		} catch (ClassCastException | NullPointerException unused) {
			return false;
		}

		return true;
	}

	/**
	 * Uses == for comparison of each value.
	 */
	public boolean equalsIdentity(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StringSpecializedObjectMap)) {
			return false;
		}
		StringSpecializedObjectMap other = (StringSpecializedObjectMap) obj;
		if (other.size != size) {
			return false;
		}
		String[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			String key = keyTable[i];
			if (key != null && valueTable[i] != other.getOrDefault(key, neverIdentical)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets a String representation of this map using {@code Appender::append} to get the String form of keys and of
	 * values. Separates keys from values using "=", and separates entries using ", ". Wraps the output in curly braces.
	 * @return a String representation of this map
	 */
	@Override
	public String toString() {
		return toString(", ", true);
	}

	/**
	 * Delegates to {@link #toString(String, boolean)} with the given entrySeparator and without braces.
	 * This is different from {@link #toString()}, which includes braces by default.
	 *
	 * @param entrySeparator how to separate entries, such as {@code ", "}
	 * @return a new String representing this map
	 */
	public String toString(String entrySeparator) {
		return toString(entrySeparator, false);
	}

	/**
	 * Gets a String representation of this map using {@code Appender::append} to get the String form of keys and of
	 * values. Separates keys from values using "=", and separates entries using {@code entrySeparator}.
	 * Wraps the output in curly braces if {@code braces} is true.
	 * @return a String representation of this map
	 */
	public String toString(String entrySeparator, boolean braces) {
		return appendTo(new StringBuilder(8 * size()), entrySeparator, braces).toString();
	}

	/**
	 * Makes a String from the contents of this StringSpecializedObjectMap, but uses the given {@link Appender}s
	 * to convert each key and each value to a customizable representation and append them
	 * to a temporary StringBuilder. To use
	 * the default String representation, you can use {@code Appender::append} as an appender.
	 *
	 * @param entrySeparator    how to separate entries, such as {@code ", "}
	 * @param keyValueSeparator how to separate each key from its value, such as {@code "="} or {@code ":"}
	 * @param braces            true to wrap the output in curly braces, or false to omit them
	 * @param keyAppender       an Appender that can take a String key
	 * @param valueAppender     an Appender that can take a V value
	 * @return a new String representing this map
	 */
	public String toString(String entrySeparator, String keyValueSeparator, boolean braces,
						   Appender<String> keyAppender, Appender<V> valueAppender) {
		return appendTo(new StringBuilder(8 * size()), entrySeparator, keyValueSeparator, braces, keyAppender, valueAppender).toString();
	}

	/**
	 * Appends to an Appendable CharSequence from the contents of this StringSpecializedObjectMap, using
	 * {@code Appender::append} to append keys and to append values.
	 * Uses "=" to separate keys from their values.
	 *
	 * @param sb                an Appendable CharSequence that this can append to
	 * @param entrySeparator    how to separate entries, such as {@code ", "}
	 * @param braces            true to wrap the output in curly braces, or false to omit them
	 * @return {@code sb}, with the appended keys and values of this map
	 * @param <S>  any type that is both a CharSequence and an Appendable, such as StringBuilder, StringBuffer, CharBuffer, or CharList
	 */
	public <S extends CharSequence & Appendable> S appendTo(S sb, String entrySeparator, boolean braces) {
		return appendTo(sb, entrySeparator, "=", braces, Appender::append, Appender::append);
	}

	/**
	 * Appends to an Appendable CharSequence from the contents of this StringSpecializedObjectMap, but uses the given {@link Appender}s
	 * to convert each key and each value to a customizable representation and append them to {@code sb}. To use
	 * the default String representation, you can use {@code Appender::append} as an appender.
	 *
	 * @param sb                an Appendable CharSequence that this can append to
	 * @param entrySeparator    how to separate entries, such as {@code ", "}
	 * @param keyValueSeparator how to separate each key from its value, such as {@code "="} or {@code ":"}
	 * @param braces            true to wrap the output in curly braces, or false to omit them
	 * @param keyAppender       an Appender that can take a String key
	 * @param valueAppender     an Appender that can take a V value
	 * @return {@code sb}, with the appended keys and values of this map
	 * @param <S>  any type that is both a CharSequence and an Appendable, such as StringBuilder, StringBuffer, CharBuffer, or CharList
	 */
	public <S extends CharSequence & Appendable> S appendTo(S sb, String entrySeparator, String keyValueSeparator, boolean braces,
								  Appender<String> keyAppender, Appender<V> valueAppender) {
		try {
			if (size == 0) {
				if (braces) sb.append("{}");
				return sb;
			}
			if (braces) {
				sb.append('{');
			}
			String[] keyTable = this.keyTable;
			V[] valueTable = this.valueTable;
			int i = keyTable.length;
			while (i-- > 0) {
				String key = keyTable[i];
				if (key == null) {
					continue;
				}
				keyAppender.apply(sb, key);
				sb.append(keyValueSeparator);
				V value = valueTable[i];
				if (value == this) sb.append("(this)");
				else valueAppender.apply(sb, value);
				break;
			}
			while (i-- > 0) {
				String key = keyTable[i];
				if (key == null) {
					continue;
				}
				sb.append(entrySeparator);
				keyAppender.apply(sb, key);
				sb.append(keyValueSeparator);
				V value = valueTable[i];
				if (value == this) sb.append("(this)");
				else valueAppender.apply(sb, value);
			}
			if (braces) {
				sb.append('}');
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb;
	}

	/**
	 * Reduces the size of the map to the specified size. If the map is already smaller than the specified
	 * size, no action is taken. This indiscriminately removes items from the backing array until the
	 * requested newSize is reached, or until the full backing array has had its elements removed.
	 * <br>
	 * This tries to remove from the end of the iteration order, but because the iteration order is not
	 * guaranteed by an unordered map, this can remove essentially any item(s) from the map if it is larger
	 * than newSize.
	 *
	 * @param newSize the target size to try to reach by removing items, if smaller than the current size
	 */
	public void truncate(int newSize) {
		String[] keyTable = this.keyTable;
		V[] valTable = this.valueTable;
		newSize = Math.max(0, newSize);
		for (int i = keyTable.length - 1; i >= 0 && size > newSize; i--) {
			if (keyTable[i] != null) {
				keyTable[i] = null;
				valTable[i] = null;
				--size;
			}
		}
	}

	@Override
	public V replace(String key, V value) {
		if (key == null) return defaultValue;
		int i = locateKey(key);
		if (i >= 0) {
			V oldValue = valueTable[i];
			valueTable[i] = value;
			return oldValue;
		}
		return defaultValue;
	}

	/**
	 * Just like Map's merge() default method, but this doesn't use Java 8 APIs (so it should work on RoboVM), and this
	 * won't remove entries if the remappingFunction returns null (in that case, it will call {@code put(key, null)}).
	 * This also uses a functional interface from Funderby instead of the JDK, for RoboVM support.
	 *
	 * @param key               key with which the resulting value is to be associated
	 * @param value             the value to be merged with the existing value
	 *                          associated with the key or, if no existing value
	 *                          is associated with the key, to be associated with the key
	 * @param remappingFunction given a V from this and the V {@code value}, this should return what V to use
	 * @return the value now associated with key
	 */
	public V combine(String key, V value, ObjObjToObjBiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		if (key == null) return defaultValue;
		int i = locateKey(key);
		V next = (i < 0) ? value : remappingFunction.apply(valueTable[i], value);
		put(key, next);
		return next;
	}

	/**
	 * Simply calls {@link #combine(String, Object, ObjObjToObjBiFunction)} on this map using every
	 * key-value pair in {@code other}. If {@code other} isn't empty, calling this will probably modify
	 * this map, though this depends on the {@code remappingFunction}.
	 *
	 * @param other             a non-null Map (or subclass) with compatible key and value types
	 * @param remappingFunction given a V value from this and a value from other, this should return what V to use
	 */
	public void combine(Map<? extends String, ? extends V> other, ObjObjToObjBiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		for (Map.Entry<? extends String, ? extends V> e : other.entrySet()) {
			combine(e.getKey(), e.getValue(), remappingFunction);
		}
	}

	/**
	 * Creates a new {@link Entries} and gets its iterator.
	 * You can remove an Entry from this StringSpecializedObjectMap using this Iterator.
	 *
	 * @return an {@link Iterator} over key-value pairs as {@link Map.Entry} values
	 */
	@Override
	public MapIterator<V, Map.Entry<String, V>> iterator() {
		return entrySet().iterator();
	}

	/**
	 * Returns a {@link Set} view of the keys contained in this map.
	 * The set is backed by the map, so changes to the map are
	 * reflected in the set, and vice versa.  If the map is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own {@code remove} operation), the results of
	 * the iteration are undefined.  The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * {@code Iterator.remove}, {@code Set.remove},
	 * {@code removeAll}, {@code retainAll}, and {@code clear}
	 * operations.  It does not support the {@code add} or {@code addAll}
	 * operations.
	 *
	 * @return a set view of the keys contained in this map
	 */
	@Override
	public Keys<V> keySet() {
		return new Keys<>(this);
	}

	/**
	 * Returns a Collection of the values in the map. Remove is supported.
	 *
	 * @return a {@link Collection} of V values
	 */
	@Override
	public Values<V> values() {
		return new Values<>(this);
	}

	/**
	 * Returns a Set of Map.Entry, containing the entries in the map. Remove is supported by the Set's iterator.
	 *
	 * @return a {@link Set} of {@link Map.Entry} key-value pairs
	 */
	@Override
	public Entries<V> entrySet() {
		return new Entries<>(this);
	}

	public static class Entry<V> implements Map.Entry<String, V> {
		public String key;
		public V value;

		public Entry() {
		}

		public Entry(String key, V value) {
			this.key = key;
			this.value = value;
		}

		public Entry(Map.Entry<? extends String, ? extends V> entry) {
			key = entry.getKey();
			value = entry.getValue();
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}

		/**
		 * Returns the key corresponding to this entry.
		 *
		 * @return the key corresponding to this entry
		 * @throws IllegalStateException implementations may, but are not
		 *                               required to, throw this exception if the entry has been
		 *                               removed from the backing map.
		 */
		@Override
		public String getKey() {
			Objects.requireNonNull(key);
			return key;
		}

		/**
		 * Returns the value corresponding to this entry.  If the mapping
		 * has been removed from the backing map (by the iterator's
		 * {@code remove} operation), the results of this call are undefined.
		 *
		 * @return the value corresponding to this entry
		 * @throws IllegalStateException implementations may, but are not
		 *                               required to, throw this exception if the entry has been
		 *                               removed from the backing map.
		 */
		@Override
		public V getValue() {
			return value;
		}

		/**
		 * Replaces the value corresponding to this entry with the specified
		 * value (optional operation).  (Writes through to the map.)  The
		 * behavior of this call is undefined if the mapping has already been
		 * removed from the map (by the iterator's {@code remove} operation).
		 *
		 * @param value new value to be stored in this entry
		 * @return old value corresponding to the entry
		 * @throws UnsupportedOperationException if the {@code put} operation
		 *                                       is not supported by the backing map
		 * @throws ClassCastException            if the class of the specified value
		 *                                       prevents it from being stored in the backing map
		 * @throws NullPointerException          if the backing map does not permit
		 *                                       null values, and the specified value is null
		 * @throws IllegalArgumentException      if some property of this value
		 *                                       prevents it from being stored in the backing map
		 * @throws IllegalStateException         implementations may, but are not
		 *                                       required to, throw this exception if the entry has been
		 *                                       removed from the backing map.
		 */
		@Override
		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Entry<?> entry = (Entry<?>) o;

			if (!Objects.equals(key, entry.key)) {
				return false;
			}
			return Objects.equals(value, entry.value);
		}

		@Override
		public int hashCode() {
			int result = key != null ? key.hashCode() : 0;
			result = 31 * result + (value != null ? value.hashCode() : 0);
			return result;
		}
	}

	public static abstract class MapIterator<V, I> implements Iterable<I>, Iterator<I> {
		public boolean hasNext;

		protected final StringSpecializedObjectMap<V> map;
		protected int nextIndex, currentIndex;

		public MapIterator(StringSpecializedObjectMap<V> map) {
			this.map = map;
			reset();
		}

		public void reset() {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
		}

		protected void findNextIndex() {
			String[] keyTable = map.keyTable;
			for (int n = keyTable.length; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					return;
				}
			}
			hasNext = false;
		}

		@Override
		public void remove() {
			int i = currentIndex;
			if (i < 0) {
				throw new IllegalStateException("next must be called before remove.");
			}
			String[] keyTable = map.keyTable;
			V[] valueTable = map.valueTable;
			int mask = map.mask, next = i + 1 & mask;
			String key;
			while ((key = keyTable[next]) != null) {
				int placement = map.place(key);
				if ((next - placement & mask) > (i - placement & mask)) {
					keyTable[i] = key;
					valueTable[i] = valueTable[next];
					i = next;
				}
				next = next + 1 & mask;
			}
			keyTable[i] = null;
			valueTable[i] = null;
			map.size--;
			if (i != currentIndex) {
				--nextIndex;
			}
			currentIndex = -1;
		}
	}

	public static class Entries<V> extends AbstractSet<Map.Entry<String, V>> implements EnhancedCollection<Map.Entry<String, V>> {
		protected StringSpecializedObjectMap<V> map;

		public Entries(StringSpecializedObjectMap<V> map) {
			this.map = map;
		}

		@Override
		public boolean contains(Object o) {
			return map.containsKey(o);
		}

		/**
		 * Returns an iterator over the elements contained in this collection.
		 *
		 * @return an iterator over the elements contained in this collection
		 */
		@Override
		public MapIterator<V, Map.Entry<String, V>> iterator() {
			return new MapIterator<V, Map.Entry<String, V>>(map) {
				@Override
				public MapIterator<V, Map.Entry<String, V>> iterator() {
					return this;
				}

				/**
				 * This allocates and returns a new Entry every time it is called.
				 *
				 * @return a new Entry that will have its key and value set to the next pair
				 */
				@Override
				public Map.Entry<String, V> next() {
					if (!hasNext) {
						throw new NoSuchElementException();
					}
					Entry<V> entry = new Entry<>(map.keyTable[nextIndex], map.valueTable[nextIndex]);
					currentIndex = nextIndex;
					findNextIndex();
					return entry;
				}

				@Override
				public boolean hasNext() {
					return hasNext;
				}
			};
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public int hashCode() {
			return map.hashCode();
		}

		@Override
		public String toString() {
			return toString(", ", true);
		}

		/**
		 * Returns a new {@link ObjectList} containing the remaining items.
		 * Does not change the position of this iterator.
		 */
		public ObjectList<Map.Entry<String, V>> toList() {
			ObjectList<Map.Entry<String, V>> list = new ObjectList<>(map.size);
			MapIterator<V,Map.Entry<String, V>> iter = iterator();
			while (iter.hasNext) {
				list.add(iter.next());
			}
			return list;
		}

		/**
		 * Append the remaining items that this can iterate through into the given Collection.
		 * Does not change the position of this iterator.
		 *
		 * @param coll any modifiable Collection; may have items appended into it
		 * @return the given collection
		 */
		public Collection<Map.Entry<String, V>> appendInto(Collection<Map.Entry<String, V>> coll) {
			MapIterator<V,Map.Entry<String, V>> iter = iterator();
			while (iter.hasNext) {
				coll.add(iter.next());
			}
			return coll;
		}

		/**
		 * Append the remaining items that this can iterate through into the given Map.
		 * Does not change the position of this iterator. Note that a Map is not a Collection.
		 *
		 * @param map any modifiable Map; may have items appended into it
		 * @return the given map
		 */
		public Map<String, V> appendInto(Map<String, V> map) {
			MapIterator<V,Map.Entry<String, V>> iter = iterator();
			while (iter.hasNext) {
				map.put(iter.map.keyTable[iter.nextIndex], iter.map.valueTable[iter.nextIndex]);
				iter.findNextIndex();
			}
			return map;
		}
	}

	public static class Values<V> extends AbstractCollection<V> implements EnhancedCollection<V> {
		protected StringSpecializedObjectMap<V> map;

		public Values(StringSpecializedObjectMap<V> map) {
			this.map = map;
		}

		/**
		 * Returns an iterator over the elements contained in this collection.
		 *
		 * @return an iterator over the elements contained in this collection
		 */
		@Override
		public MapIterator<V, V> iterator() {
			return new MapIterator<V, V>(map) {
				@Override
				public MapIterator<V, V> iterator() {
					return this;
				}

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public V next() {
					if (!hasNext) {
						throw new NoSuchElementException();
					}
					V value = map.valueTable[nextIndex];
					currentIndex = nextIndex;
					findNextIndex();
					return value;
				}
			};
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public String toString() {
			return toString(", ", true);
		}

		/**
		 * Returns a new {@link ObjectList} containing the remaining items.
		 * Does not change the position of this iterator.
		 */
		public ObjectList<V> toList() {
			ObjectList<V> list = new ObjectList<>(map.size);
			MapIterator<V, V> iter = iterator();
			while (iter.hasNext) {
				list.add(iter.next());
			}
			return list;
		}

		/**
		 * Append the remaining items that this can iterate through into the given Collection.
		 * Does not change the position of this iterator.
		 *
		 * @param coll any modifiable Collection; may have items appended into it
		 * @return the given collection
		 */
		public Collection<V> appendInto(Collection<V> coll) {
			MapIterator<V, V> iter = iterator();
			while (iter.hasNext) {
				coll.add(iter.next());
			}
			return coll;
		}
	}

	public static class Keys<V> extends AbstractSet<String> implements EnhancedCollection<String> {
		protected StringSpecializedObjectMap<V> map;

		public Keys(StringSpecializedObjectMap<V> map) {
			this.map = map;
		}

		@Override
		public boolean contains(Object o) {
			return map.containsKey(o);
		}

		/**
		 * Returns an iterator over the elements contained in this collection.
		 *
		 * @return an iterator over the elements contained in this collection
		 */
		@Override
		public MapIterator<V, String> iterator() {
			return new MapIterator<V, String>(map) {
				@Override
				public MapIterator<V, String> iterator() {
					return this;
				}

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public String next() {
					if (!hasNext) {
						throw new NoSuchElementException();
					}
					String key = map.keyTable[nextIndex];
					currentIndex = nextIndex;
					findNextIndex();
					return key;
				}
			};
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public String toString() {
			return toString(", ", true);
		}

		/**
		 * Returns a new {@link ObjectList} containing the remaining items.
		 * Does not change the position of this iterator.
		 */
		public ObjectList<String> toList() {
			ObjectList<String> list = new ObjectList<>(map.size);
			MapIterator<V, String> iter = iterator();
			while (iter.hasNext) {
				list.add(iter.next());
			}
			return list;
		}

		/**
		 * Append the remaining items that this can iterate through into the given Collection.
		 * Does not change the position of this iterator.
		 *
		 * @param coll any modifiable Collection; may have items appended into it
		 * @return the given collection
		 */
		public Collection<String> appendInto(Collection<String> coll) {
			MapIterator<V, String> iter = iterator();
			while (iter.hasNext) {
				coll.add(iter.next());
			}
			return coll;
		}
	}

	/**
	 * Constructs an empty map given the types as generic type arguments.
	 * This is usually less useful than just using the constructor, but can be handy
	 * in some code-generation scenarios when you don't know how many arguments you will have.
	 *
	 * @param <V> the type of values
	 * @return a new map containing nothing
	 */
	public static <V> StringSpecializedObjectMap<V> with() {
		return new StringSpecializedObjectMap<>(0);
	}

	/**
	 * Constructs a single-entry map given one key and one value.
	 * This is mostly useful as an optimization for {@link #with(String, Object, Object...)}
	 * when there's no "rest" of the keys or values.
	 *
	 * @param key0   the first and only key
	 * @param value0 the first and only value
	 * @param <V>    the type of value0
	 * @return a new map containing just the entry mapping key0 to value0
	 */
	public static <V> StringSpecializedObjectMap<V> with(String key0, V value0) {
		StringSpecializedObjectMap<V> map = new StringSpecializedObjectMap<>(1);
		map.put(key0, value0);
		return map;
	}

	/**
	 * Constructs a single-entry map given two key-value pairs.
	 * This is mostly useful as an optimization for {@link #with(String, Object, Object...)}
	 * when there's no "rest" of the keys or values.
	 *
	 * @param key0   a String key
	 * @param value0 a V value
	 * @param key1   a String key
	 * @param value1 a V value
	 * @param <V>    the type of value0
	 * @return a new map containing entries mapping each key to the following value
	 */
	public static <V> StringSpecializedObjectMap<V> with(String key0, V value0, String key1, V value1) {
		StringSpecializedObjectMap<V> map = new StringSpecializedObjectMap<>(2);
		map.put(key0, value0);
		map.put(key1, value1);
		return map;
	}

	/**
	 * Constructs a single-entry map given three key-value pairs.
	 * This is mostly useful as an optimization for {@link #with(String, Object, Object...)}
	 * when there's no "rest" of the keys or values.
	 *
	 * @param key0   a String key
	 * @param value0 a V value
	 * @param key1   a String key
	 * @param value1 a V value
	 * @param key2   a String key
	 * @param value2 a V value
	 * @param <V>    the type of value0
	 * @return a new map containing entries mapping each key to the following value
	 */
	public static <V> StringSpecializedObjectMap<V> with(String key0, V value0, String key1, V value1, String key2, V value2) {
		StringSpecializedObjectMap<V> map = new StringSpecializedObjectMap<>(3);
		map.put(key0, value0);
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

	/**
	 * Constructs a single-entry map given four key-value pairs.
	 * This is mostly useful as an optimization for {@link #with(String, Object, Object...)}
	 * when there's no "rest" of the keys or values.
	 *
	 * @param key0   a String key
	 * @param value0 a V value
	 * @param key1   a String key
	 * @param value1 a V value
	 * @param key2   a String key
	 * @param value2 a V value
	 * @param key3   a String key
	 * @param value3 a V value
	 * @param <V>    the type of value0
	 * @return a new map containing entries mapping each key to the following value
	 */
	public static <V> StringSpecializedObjectMap<V> with(String key0, V value0, String key1, V value1, String key2, V value2, String key3, V value3) {
		StringSpecializedObjectMap<V> map = new StringSpecializedObjectMap<>(4);
		map.put(key0, value0);
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		return map;
	}

	/**
	 * Constructs a map given alternating keys and values.
	 * This can be useful in some code-generation scenarios, or when you want to make a
	 * map conveniently by-hand and have it populated at the start. You can also use
	 * {@link #StringSpecializedObjectMap(String[], Object[])}, which takes all keys and then all values.
	 * This needs all keys to have the same type and all values to have the same type, because
	 * it gets those types from the first key parameter and first value parameter. Any keys that don't
	 * have String as their type or values that don't have V as their type have that entry skipped.
	 *
	 * @param key0   the first key; will be used to determine the type of all keys
	 * @param value0 the first value; will be used to determine the type of all values
	 * @param rest   a varargs or non-null array of alternating String, V, String, V... elements
	 * @param <V>    the type of values, inferred from value0
	 * @return a new map containing the given keys and values
	 */
	public static <V> StringSpecializedObjectMap<V> with(String key0, V value0, Object... rest) {
		StringSpecializedObjectMap<V> map = new StringSpecializedObjectMap<>(1 + (rest.length >>> 1));
		map.put(key0, value0);
		map.putPairs(rest);
		return map;
	}

	/**
	 * Attempts to put alternating key-value pairs into this map, drawing a key, then a value from {@code pairs}, then
	 * another key, another value, and so on until another pair cannot be drawn. Any keys that don't
	 * have String as their type or values that don't have V as their type have that entry skipped.
	 * <br>
	 * If any item in {@code pairs} cannot be cast to the appropriate String or V type for its position in the arguments,
	 * that pair is ignored and neither that key nor value is put into the map. If any key is null, that pair is
	 * ignored, as well. If {@code pairs} is an Object array that is null, the entire call to putPairs() is ignored.
	 * If the length of {@code pairs} is odd, the last item (which will be unpaired) is ignored.
	 *
	 * @param pairs an array or varargs of alternating String, V, String, V... elements
	 */
	@SuppressWarnings("unchecked")
	public void putPairs(Object... pairs) {
		if (pairs != null) {
			for (int i = 1; i < pairs.length; i += 2) {
				try {
					if (pairs[i - 1] != null)
						put((String) pairs[i - 1], (V) pairs[i]);
				} catch (ClassCastException ignored) {
				}
			}
		}
	}

	/**
	 * Adds items to this map drawn from the result of {@link #toString(String)} or
	 * {@link #appendTo(CharSequence, String, boolean)}. Every key-value pair should be separated by
	 * {@code ", "}, and every key should be followed by {@code "="} before the value (which
	 * {@link #toString()} does).
	 * A PartialParser will be used to parse keys from sections of {@code str}, and a different PartialParser to
	 * parse values. Any brackets inside the given range
	 * of characters will ruin the parsing, so increase offset by 1 and
	 * reduce length by 2 if the original String had brackets added to it.
	 *
	 * @param str         a String containing parseable text
	 * @param keyParser   a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser a PartialParser that returns a {@code V} value from a section of {@code str}
	 */
	public void putLegible(String str, PartialParser<String> keyParser, PartialParser<V> valueParser) {
		putLegible(str, ", ", "=", keyParser, valueParser, 0, -1);
	}

	/**
	 * Adds items to this map drawn from the result of {@link #toString(String)} or
	 * {@link #appendTo(CharSequence, String, boolean)}. Every key-value pair should be separated by
	 * {@code entrySeparator}, and every key should be followed by "=" before the value (which
	 * {@link #toString(String)} does).
	 * A PartialParser will be used to parse keys from sections of {@code str}, and a different PartialParser to
	 * parse values. Any brackets inside the given range
	 * of characters will ruin the parsing, so increase offset by 1 and
	 * reduce length by 2 if the original String had brackets added to it.
	 *
	 * @param str            a String containing parseable text
	 * @param entrySeparator the String separating every key-value pair
	 * @param keyParser      a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser    a PartialParser that returns a {@code V} value from a section of {@code str}
	 */
	public void putLegible(String str, String entrySeparator, PartialParser<String> keyParser, PartialParser<V> valueParser) {
		putLegible(str, entrySeparator, "=", keyParser, valueParser, 0, -1);
	}

	/**
	 * Adds items to this map drawn from the result of {@link #toString(String)} or
	 * {@link #appendTo(CharSequence, String, String, boolean, Appender, Appender)}. A PartialParser will be used to
	 * parse keys from sections of {@code str}, and a different PartialParser to parse values. Any brackets
	 * inside the given range of characters will ruin the parsing, so increase offset by 1 and
	 * reduce length by 2 if the original String had brackets added to it.
	 *
	 * @param str               a String containing parseable text
	 * @param entrySeparator    the String separating every key-value pair
	 * @param keyValueSeparator the String separating every key from its corresponding value
	 * @param keyParser         a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser       a PartialParser that returns a {@code V} value from a section of {@code str}
	 */
	public void putLegible(String str, String entrySeparator, String keyValueSeparator, PartialParser<String> keyParser, PartialParser<V> valueParser) {
		putLegible(str, entrySeparator, keyValueSeparator, keyParser, valueParser, 0, -1);
	}

	/**
	 * Puts key-value pairs into this map drawn from the result of {@link #toString(String)} or
	 * {@link #appendTo(CharSequence, String, String, boolean, Appender, Appender)}. A PartialParser will be used
	 * to parse keys from sections of {@code str}, and a different PartialParser to parse values. Any brackets
	 * inside the given range of characters will ruin the parsing, so increase offset by 1 and
	 * reduce length by 2 if the original String had brackets added to it.
	 *
	 * @param str               a String containing parseable text
	 * @param entrySeparator    the String separating every key-value pair
	 * @param keyValueSeparator the String separating every key from its corresponding value
	 * @param keyParser         a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser       a PartialParser that returns a {@code V} value from a section of {@code str}
	 * @param offset            the first position to read parseable text from in {@code str}
	 * @param length            how many chars to read; -1 is treated as maximum length
	 */
	public void putLegible(String str, String entrySeparator, String keyValueSeparator, PartialParser<String> keyParser, PartialParser<V> valueParser, int offset, int length) {
		int sl, el, kvl;
		if (str == null || entrySeparator == null || keyValueSeparator == null || keyParser == null || valueParser == null
			|| (sl = str.length()) < 1 || (el = entrySeparator.length()) < 1 || (kvl = keyValueSeparator.length()) < 1
			|| offset < 0 || offset > sl - 1) return;
		final int lim = length < 0 ? sl : Math.min(offset + length, sl);
		int end = str.indexOf(keyValueSeparator, offset + 1);
		String k = null;
		boolean incomplete = false;
		while (end != -1 && end + kvl < lim) {
			k = keyParser.parse(str, offset, end);
			offset = end + kvl;
			end = str.indexOf(entrySeparator, offset + 1);
			if (end != -1 && end + el < lim) {
				put(k, valueParser.parse(str, offset, end));
				offset = end + el;
				end = str.indexOf(keyValueSeparator, offset + 1);
			} else {
				incomplete = true;
			}
		}
		if (incomplete && offset < lim) {
			put(k, valueParser.parse(str, offset, lim));
		}
	}

	/**
	 * Creates a new map by parsing all of {@code str} with the given PartialParser for keys and
	 * for values, with entries separated by {@code entrySeparator}, such as {@code ", "} and
	 * the keys separated from values by {@code keyValueSeparator}, such as {@code "="}.
	 * <br>
	 * Various {@link PartialParser} instances are defined as constants, such as
	 * {@link PartialParser#DEFAULT_STRING}, and others can be created by static methods in PartialParser, such as
	 * {@link PartialParser#objectListParser(PartialParser, String, boolean)}.
	 *
	 * @param str               a String containing parseable text
	 * @param entrySeparator    the String separating every key-value pair
	 * @param keyValueSeparator the String separating every key from its corresponding value
	 * @param keyParser         a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser       a PartialParser that returns a {@code V} value from a section of {@code str}
	 */
	public static <V> StringSpecializedObjectMap<V> parse(String str,
													 String entrySeparator,
													 String keyValueSeparator,
													 PartialParser<String> keyParser,
													 PartialParser<V> valueParser) {
		return parse(str, entrySeparator, keyValueSeparator, keyParser, valueParser, false);
	}

	/**
	 * Creates a new map by parsing all of {@code str} (or if {@code brackets} is true, all but the first and last
	 * chars) with the given PartialParser for keys and for values, with entries separated by {@code entrySeparator},
	 * such as {@code ", "} and the keys separated from values by {@code keyValueSeparator}, such as {@code "="}.
	 * <br>
	 * Various {@link PartialParser} instances are defined as constants, such as
	 * {@link PartialParser#DEFAULT_STRING}, and others can be created by static methods in PartialParser, such as
	 * {@link PartialParser#objectListParser(PartialParser, String, boolean)}.
	 *
	 * @param str               a String containing parseable text
	 * @param entrySeparator    the String separating every key-value pair
	 * @param keyValueSeparator the String separating every key from its corresponding value
	 * @param keyParser         a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser       a PartialParser that returns a {@code V} value from a section of {@code str}
	 * @param brackets          if true, the first and last chars in {@code str} will be ignored
	 */
	public static <V> StringSpecializedObjectMap<V> parse(String str,
													 String entrySeparator,
													 String keyValueSeparator,
													 PartialParser<String> keyParser,
													 PartialParser<V> valueParser,
													 boolean brackets) {
		StringSpecializedObjectMap<V> m = new StringSpecializedObjectMap<>();
		if (brackets)
			m.putLegible(str, entrySeparator, keyValueSeparator, keyParser, valueParser, 1, str.length() - 1);
		else
			m.putLegible(str, entrySeparator, keyValueSeparator, keyParser, valueParser, 0, -1);
		return m;
	}

	/**
	 * Creates a new map by parsing the given subrange of {@code str} with the given PartialParser for keys and for
	 * values, with entries separated by {@code entrySeparator}, such as {@code ", "} and the keys separated from values
	 * by {@code keyValueSeparator}, such as {@code "="}.
	 * <br>
	 * Various {@link PartialParser} instances are defined as constants, such as
	 * {@link PartialParser#DEFAULT_STRING}, and others can be created by static methods in PartialParser, such as
	 * {@link PartialParser#objectListParser(PartialParser, String, boolean)}.
	 *
	 * @param str               a String containing parseable text
	 * @param entrySeparator    the String separating every key-value pair
	 * @param keyValueSeparator the String separating every key from its corresponding value
	 * @param keyParser         a PartialParser that returns a {@code String} key from a section of {@code str}
	 * @param valueParser       a PartialParser that returns a {@code V} value from a section of {@code str}
	 * @param offset            the first position to read parseable text from in {@code str}
	 * @param length            how many chars to read; -1 is treated as maximum length
	 */
	public static <V> StringSpecializedObjectMap<V> parse(String str,
													 String entrySeparator,
													 String keyValueSeparator,
													 PartialParser<String> keyParser,
													 PartialParser<V> valueParser,
													 int offset,
													 int length) {
		StringSpecializedObjectMap<V> m = new StringSpecializedObjectMap<>();
		m.putLegible(str, entrySeparator, keyValueSeparator, keyParser, valueParser, offset, length);
		return m;
	}
}
