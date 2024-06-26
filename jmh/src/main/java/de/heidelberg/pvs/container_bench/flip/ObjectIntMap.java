/*
 * Copyright (c) 2024 See AUTHORS file.
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
 *
 */

package de.heidelberg.pvs.container_bench.flip;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;

/**
 * An Object-key, int-value map that starts using cuckoo hashing and can flip its
 * algorithm internally to use linear probing, if warranted.
 * This implementation provides all the optional map operations, but does not
 * permit {@code null} keys. This class makes no
 * guarantees as to the order of the map; in particular, it does not guarantee
 * that the order will remain constant over time. Cuckoo hashing can be extremely
 * fast (in most cases) or unusably slow (in the worst case), so being able to
 * flip the implementation to the more-reliable linear probing is a major benefit.
 * <br>
 * Both for cuckoo hashing and linear probing, this uses a family of hash functions
 * that are all similar to Fibonacci hashing. Cuckoo hashing uses two different
 * multipliers, one used to address "table 1" and the other used to address
 * "table 2" -- the names are misnomers because they really are one array, with
 * table 1 using odd indices and table 2 using even indices. Linear probing uses
 * a multiplier from the same set as the "table 1" multipliers, but can address
 * both even and odd indices in the array.
 * <br>
 * Cuckoo hashing can have severe problems if given multiple different items with
 * the same hashCode, for all bits. There are probably other cases that give it
 * trouble in the same way. This map switches to linear probing to resolve
 * collisions if it would be required to flip in a purely-cuckoo-hashed map.
 * This flip can only happen once per map, and makes it use linear probing for the
 * rest of its lifetime. Linear probing has much better worst-case performance on
 * {@link #put(Object, int)}, and flipping should only occur in that worst case.
 * The map won't return to cuckoo hashing because if the worst-case can occur at
 * all, this should be using linear probing, and if it flipped once, it could really
 * need linear probing when the same problematic combination of keys is inserted.
 * <br>
 * Iterating over the collection requires a time proportional to the capacity
 * of the map. The default capacity of an empty map is 16. The map will resize
 * its internal capacity whenever it grows past the load factor specified for the
 * current instance. The default load factor for this map is {@code 0.45}.
 * Beware that this implementation can only guarantee non-amortized O(1) on
 * {@link #get(Object)} iff the load factor is relatively low (generally below 0.60).
 * The performance is still fine in practice with a load factor of 0.75 (and still
 * better than HashMap on all tested operations), but the default is safer.
 * For more details, it's helpful to read
 * <a href="http://www.it-c.dk/people/pagh/papers/cuckoo-jour.pdf">the original Cuckoo Hash Map paper</a>.
 * <br>
 * Note that this implementation is not synchronized and not thread safe. If you need
 * thread safety, you'll need to implement your own locking around the map or wrap
 * the instance around a call to {@link Collections#synchronizedMap(Map)}.
 * <br>
 * This is partly based on <a href="https://github.com/ivgiuliani/cuckoohash">this Github repo</a> by
 * Ivan Giuliani, which is licensed under the Apache License 2.0. If you're looking through that repo,
 * it may help to know that where it calls {@code rehash()}, this code calls {@link #flip()}. After
 * flip() is called, different implementations are used (not present in the cuckoohash repo).
 * <br>
 * The concept of a map changing its algorithm when it hits a problem is not new, and in fact the JDK
 * HashMap can do this (though it switches from an algorithm this doesn't use to another algorithm this
 * doesn't use). It isn't just a nice-to-have feature in this case; if you ever try to enter three
 * different keys with the same hashCode() result, then the map this is based on (in Giuliani's
 * cuckoohash repo) would not terminate on the third put() call. Being able to flip to linear probing
 * as an alternative implementation gives this a much-needed safeguard. If you want to verify that this
 * map does not have the same issue, you can put the keys "0q1o", "0oq1", and "0ooo", with any values.
 * All those keys have the hashCode() 1540191; entering those keys will work here but not in Giuliani's
 * repo. Other cuckoo hashing implementations use mechanisms such as a "stash" for frequently-colliding
 * keys; this may work to some extent for a fixed-size stash, but it can fail badly for a resizable
 * stash if too many keys collide over all bits. The theoretical background behind a stash also seems
 * to be based on a different model for how hash code generation works (not the way Java does it),
 * which could explain why the fixed-size stash can fail on JVM implementations.
 *
 * @param <K> the type of keys maintained by this map
 */
public class ObjectIntMap<K> {

	protected static final int DEFAULT_START_SIZE = 16;
	protected static final float DEFAULT_LOAD_FACTOR = 0.45f;

	/**
	 * While cuckoo hashing, this is 4 or greater, and is used to determine how many times keys can be relocated before
	 * this decides to call {@link #flip()}. After this flips to using linear probing, this is always 0, and whether this
	 * is 0 or not determines the algorithm this class uses.
	 */
	protected int flipThreshold;

	/**
	 * Simply the size of the {@link #keyTable} times the {@link #loadFactor}, floored minus 1. Stored to avoid
	 * recalculating; this only changes upon {@link #resize(int)}.
	 */
	protected int loadThreshold;

	/**
	 * Between 0f (exclusive) and 1f (inclusive, if you're careful), this determines how full the backing tables
	 * can get before this increases their size. Larger values use less memory but make the data structure slower.
	 * On paper, this should be less than 0.6f while using cuckoo hashing, but in practice, somewhat-higher load
	 * factors can work without a slowdown (0.75 has been tested and doesn't show signs of slowdown).
	 */
	protected float loadFactor;
	/**
	 * A bitmask used to confine hash codes to the size of the table. Must be all 1-bits in its low positions, i.e. a
	 * power of two minus 1.
	 */
	protected int mask;

	/**
	 * A shift amount used to reduce a 64-bit value to a specific smaller range (less than 32 bits). The shift is always
	 * greater than 32 and less than 64.
	 */
	protected int shift;

	/**
	 * Used both by cuckoo hashing and linear probing; drawn from {@link Utilities#GOOD_MULTIPLIERS}.
	 */
	protected long hashMultiplier1;

	/**
	 * Used by cuckoo hashing; drawn from {@link Utilities#GOOD_MULTIPLIERS}.
	 */
	protected long hashMultiplier2;

	/**
	 * How many keys are in this map.
	 */
	protected int size;

	protected K[] keyTable;

	protected int[] valueTable;

	protected int defaultValue = 0;

	/**
	 * Holds a cached {@link #entrySet()}.
	 */
	@Nullable
	protected transient Set<Entry<K>> entrySet;

	/**
	 * Holds a cached {@link #keySet()}.
	 */
	@Nullable
	protected transient Set<K> keySet;

	/**
	 * Holds a cached {@link #values()}.
	 */
	protected transient PrimitiveCollection.@Nullable OfInt values;

	/**
	 * Constructs an empty {@code ObjectIntMap} with the default initial capacity (16)
	 * and the default load factor of {@code 0.45}.
	 */
	public ObjectIntMap() {
		this(DEFAULT_START_SIZE, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty {@code ObjectIntMap} with the specified initial capacity
	 * and the default load factor of {@code 0.45}.
	 * The given capacity will be rounded to the nearest power of two.
	 *
	 * @param initialCapacity the initial capacity
	 */
	public ObjectIntMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty {@code ObjectIntMap} with the specified load factor and an initial
	 * capacity of 16.
	 * <p>
	 * The load factor will cause the map to double in size when the number
	 * of items it contains has filled up more than {@code loadFactor} of the available
	 * space.
	 *
	 * @param loadFactor the load factor
	 */
	public ObjectIntMap(float loadFactor) {
		this(DEFAULT_START_SIZE, loadFactor);
	}

	/**
	 * Constructs an empty {@code ObjectIntMap} with the specified load factor and initial
	 * capacity.
	 * <p>
	 * The load factor will cause the map to double in size when the number
	 * of items it contains has filled up more than {@code loadFactor} of the available
	 * space.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor the load factor
	 */
	@SuppressWarnings("unchecked")
	public ObjectIntMap(int initialCapacity, float loadFactor) {
		if (initialCapacity <= 0) {
			throw new IllegalArgumentException("initial capacity must be strictly positive");
		}
		if (loadFactor <= 0.f || loadFactor > 1.f) {
			throw new IllegalArgumentException("load factor must be a value in the (0.0f, 1.0f] range.");
		}

		size = 0;
		int tableSize = Utilities.tableSize(initialCapacity, loadFactor);
		mask = tableSize - 1;
		shift = Long.numberOfLeadingZeros(tableSize - 1L);
		flipThreshold = Integer.numberOfTrailingZeros(tableSize) + 4;

		keyTable = (K[])new Object[tableSize];
		valueTable = new int[tableSize];
		this.loadFactor = loadFactor;
		loadThreshold = (int)(loadFactor * tableSize) - 1;

		regenHashMultipliers(tableSize);
	}

	public ObjectIntMap(ObjectIntMap<? extends K> other) {
		size = other.size;
		mask = other.mask;
		shift = other.shift;
		flipThreshold = other.flipThreshold;
		loadFactor = other.loadFactor;
		loadThreshold = other.loadThreshold;
		defaultValue = other.defaultValue;
		hashMultiplier1 = other.hashMultiplier1;
		hashMultiplier2 = other.hashMultiplier2;
		keyTable = Arrays.copyOf(other.keyTable, other.keyTable.length);
		valueTable = Arrays.copyOf(other.valueTable, other.valueTable.length);
	}

	/**
	 * Returns true if the internal algorithm this uses has changed by a call to {@link #flip()}. Before flip()
	 * is called (and it might never be), this uses cuckoo hashing to resolve collisions; after flip() has been
	 * called, this uses linear probing. This method is meant as a diagnostic tool if it becomes necessary to
	 * determine what algorithm is in use.
	 *
	 * @return true if {@link #flip()} has been called and has changed the internal algorithm this uses.
	 */
	public boolean hasFlipped() {
		return flipThreshold == 0;
	}

	/**
	 * Returns {@code true} if this map contains a mapping for the specified
	 * key. More formally, returns {@code true} if and only if
	 * this map contains a mapping for a key {@code k} such that
	 * {@code key.equals(k)}.
	 * (There can be at most one such mapping.)
	 * <br>
	 * If {@code key} is {@code null}, this returns {@code false}.
	 *
	 * @param key key whose presence in this map is to be tested
	 * @return {@code true} if this map contains a mapping for the specified key
	 */
	public boolean containsKey (Object key) {
		if(key == null) return false;
		if(flipThreshold == 0)
			return containsKeyLinear(key);

		final int hc = key.hashCode();

		return key.equals(keyTable[(int)(hashMultiplier1 * hc >>> shift) | 1]) ||
			key.equals(keyTable[(int)(hashMultiplier2 * hc >>> shift) & -2]);
	}


	/**
	 * A part of {@link #containsKey(Object)} that is used when this is linear-probing.
	 * @param key an Object that is usually a {@code K}; expected to be non-null
	 * @return true if {@code key} is present in the key table
	 */
	protected boolean containsKeyLinear (@NonNull Object key) {
		K[] keyTable = this.keyTable;
		for (int i = (int) (key.hashCode() * hashMultiplier1 >>> shift); ; i = i + 1 & mask) {
			K other = keyTable[i];
			if (key.equals(other))
				return true;
			if (other == null)
				return false;
		}
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@link #getDefaultValue()} if this map contains no mapping for the key.
	 * <br>
	 * More formally, if this map contains a mapping from a key
	 * {@code k} to a value {@code v} such that {@code key.equals(k)},
	 * then this method returns {@code v}; otherwise
	 * it returns {@link #getDefaultValue()}.
	 * (There can be at most one such mapping.)
	 * <br>
	 * The {@link #containsKey(Object)} operation may be used to distinguish
	 * the case where a given {@code int} is returned because it is mapped to
	 * {@code key} and the case where it is returned because it is the
	 * {@link #getDefaultValue()}. If the default value has not been set, it will
	 * simply be {@code null}.
	 * <br>
	 * If {@code key} is {@code null}, this returns {@link #getDefaultValue()}.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or
	 * {@link #getDefaultValue()} if this map contains no mapping for the key
	 */
	public int get (Object key) {
		return getOrDefault(key, defaultValue);
	}

	/**
	 * Returns the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key.
	 * <br>
	 * If {@code key} is {@code null}, this returns {@code defaultValue}.
	 *
	 * @param key the key whose associated value is to be returned
	 * @param defaultValue the default mapping of the key
	 * @return the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key
	 */
	public int getOrDefault (Object key, int defaultValue) {
		return get(key, defaultValue);
	}
	/**
	 * Returns the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key.
	 * <br>
	 * If {@code key} is {@code null}, this returns {@code defaultValue}.
	 *
	 * @param key the key whose associated value is to be returned
	 * @param defaultValue the default mapping of the key
	 * @return the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key
	 */
	public int get (Object key, int defaultValue) {
		if(key == null) return defaultValue;

		if(flipThreshold == 0)
			return getOrDefaultLinear(key, defaultValue);

		int hc = key.hashCode();
		int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
		if (key.equals(keyTable[hr1]))
			return valueTable[hr1];

		int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
		if (key.equals(keyTable[hr2]))
			return valueTable[hr2];

		return defaultValue;
	}

	/**
	 * A part of {@link #get(Object, int)} that is used when this is linear-probing.
	 * @param key the key whose associated value is to be returned
	 * @param defaultValue the default mapping of the key
	 * @return the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key
	 */
	public int getOrDefaultLinear (@NonNull Object key, int defaultValue) {
		K[] keyTable = this.keyTable;
		for (int i = (int)(key.hashCode() * hashMultiplier1 >>> shift); ; i = i + 1 & mask) {
			K other = keyTable[i];
			if (key.equals(other))
				return valueTable[i];
			if (other == null)
				return defaultValue;
		}
	}
	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for
	 * the key, the old value is replaced by the specified value.  (A map
	 * {@code m} is said to contain a mapping for a key {@code k} if and only
	 * if {@link #containsKey(Object) m.containsKey(k)} would return
	 * {@code true}.)
	 * <br>
	 * If {@code key} is {@code null}, this throws a {@link NullPointerException}.
	 * This map does not permit {@code null} keys, but does permit {@code null}
	 * values.
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with {@code key}, or
	 * {@link #getDefaultValue()} if there was no mapping for {@code key}.
	 * @throws NullPointerException if the specified key is null
	 */
	public int put (K key, int value) {
		if(key == null) throw new NullPointerException("ObjectIntMap does not permit null keys.");

		if(flipThreshold == 0)
			return putLinear(key, value);

		boolean absent = true;
		int old = defaultValue;
		int hc = key.hashCode();
		int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
		if (key.equals(keyTable[hr1])) {
			old = valueTable[hr1];
			absent = false;
		} else {
			int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
			if (key.equals(keyTable[hr2])) {
				old = valueTable[hr2];
				absent = false;
			}
		}

		if (absent && size >= loadThreshold) {
			// If we need to resize after adding this item, it's probably best to resize before we add it.
			resize();
		}
		// If placing key with cuckoo hashing fails, putFallback falls back to linear probing by calling flip().
        putFallback(key, value, hc);

        return old;
	}

	/**
	 * Attempts to place the given key and value, but is permitted to fail. If this fails, it returns true,
	 * and expects the caller to understand that a key was not placed where it should be.
	 * This is only used when this is cuckoo-hashing.
	 * @return true if there was a failure at some point, or false if nothing went wrong
	 */
	protected boolean putSafe (@NonNull K key, int value) {
		int loop = 0;
		while (loop++ < flipThreshold) {
			int hc = key.hashCode();
			int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
			K k1 = keyTable[hr1];
			if (k1 == null || key.equals(k1)) {
				valueTable[hr1] = value;
				return false;
			}
			int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
			K k2 = keyTable[hr2];
			if (k2 == null || key.equals(k2)) {
				valueTable[hr2] = value;
				return false;
			}

			// Both tables have an item in the required position that doesn't have the same key, we need to move things around.
			// Prefer always moving from the odd entries for simplicity.
			int temp = valueTable[hr1];
			keyTable[hr1] = key;
			key = k1;
			valueTable[hr1] = value;
			value = temp;
		}
		return true;
	}

	/**
	 * A part of {@link #put(Object, int)} that is used when this is linear-probing.
	 * @param key key with which the specified value is to be associated; must not be null
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with {@code key}, or
	 * {@link #getDefaultValue()} if there was no mapping for {@code key}.
	 */
	protected int putLinear(@NonNull K key, int value){
		int i = locateKey(key);
		if (i >= 0) { // Existing key was found.
			int oldValue = valueTable[i];
			valueTable[i] = value;
			return oldValue;
		}
		i = ~i; // Empty space was found.
		keyTable[i] = key;
		valueTable[i] = value;
		if (size++ >= loadThreshold) resizeLinear(keyTable.length << 1);
		return defaultValue;
	}

	/**
	 * Attempts to place the given key and value, but is permitted to fail. If this fails, it switches
	 * the map to use linear probing by calling {@link #flip()}, and completes the put operation using
	 * linear probing.
	 */
	protected void putFallback (K key, int value, int hc) {
		int loop = 0;
		while (loop++ < flipThreshold) {

			int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
			K k1 = keyTable[hr1];
			if (k1 == null) {
				valueTable[hr1] = value;
				++size;
				return;
			}
			if (key.equals(k1)) {
				valueTable[hr1] = value;
				return;
			}
			int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
			K k2 = keyTable[hr2];
			if (k2 == null) {
				valueTable[hr2] = value;
				++size;
				return;
			}
			if(key.equals(k2)){
				valueTable[hr2] = value;
				return;
			}

			// Both tables have an item in the required position that doesn't have the same key, we need to move things around.
			// Prefer always moving from the odd entries for simplicity.
			int temp = valueTable[hr1];
			keyTable[hr1] = key;
			key = k1;
			valueTable[hr1] = value;
			value = temp;
			hc = key.hashCode();
		}
		flip();
		// From this point on, we are using linear probing, not cuckoo hashing.
		putLinear(key, value);
	}

	/**
	 * Removes the mapping for a key from this map if it is present
	 * (optional operation). More formally, if this map contains a mapping
	 * from key {@code k} to value {@code v} such that
	 * {@code key.equals(k)}, that mapping is removed.
	 * (The map can contain at most one such mapping.)
	 * <br>
	 * Returns the value to which this map previously associated the key,
	 * or {@link #getDefaultValue()} if the map contained no mapping for the key.
	 * <br>
	 * The {@link #containsKey(Object)} operation (called before remove()) may be
	 * used to distinguish the case where a given {@code V} is returned because it
	 * is mapped to {@code key} and the case where it is returned because it is the
	 * {@link #getDefaultValue()}. If the default value has not been set, it will
	 * simply be {@code null}.
	 * <br>
	 * If {@code key} is {@code null}, this returns {@link #getDefaultValue()}.
	 * <br>
	 * The map will not contain a mapping for the specified key once the
	 * call returns.
	 *
	 * @param key key whose mapping is to be removed from the map
	 * @return the previous value associated with {@code key}, or
	 *         {@link #getDefaultValue()} if there was no mapping for {@code key}.
	 */
	public int remove (Object key) {
		return remove(key, defaultValue);
	}
	/**
	 * Removes the mapping for a key from this map if it is present
	 * (optional operation). More formally, if this map contains a mapping
	 * from key {@code k} to value {@code v} such that
	 * {@code key.equals(k)}, that mapping is removed.
	 * (The map can contain at most one such mapping.)
	 * <br>
	 * Returns the value to which this map previously associated the key,
	 * or {@link #getDefaultValue()} if the map contained no mapping for the key.
	 * <br>
	 * The {@link #containsKey(Object)} operation (called before remove()) may be
	 * used to distinguish the case where a given {@code V} is returned because it
	 * is mapped to {@code key} and the case where it is returned because it is the
	 * {@link #getDefaultValue()}. If the default value has not been set, it will
	 * simply be {@code null}.
	 * <br>
	 * If {@code key} is {@code null}, this returns {@link #getDefaultValue()}.
	 * <br>
	 * The map will not contain a mapping for the specified key once the
	 * call returns.
	 *
	 * @param key key whose mapping is to be removed from the map
	 * @return the previous value associated with {@code key}, or
	 *         {@link #getDefaultValue()} if there was no mapping for {@code key}.
	 */
	public int remove (Object key, int defaultValue) {
		if (key == null)
			return defaultValue;

		if(flipThreshold == 0)
			return removeLinear(key, defaultValue);

		int hc = key.hashCode();
		int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
		int oldValue = defaultValue;

		if (key.equals(keyTable[hr1])) {
			oldValue = valueTable[hr1];
			keyTable[hr1] = null;
			size--;
		} else {
			int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
			if (key.equals(keyTable[hr2])) {
				oldValue = valueTable[hr2];
				keyTable[hr2] = null;
				size--;
			}
		}

		return oldValue;
	}

	/**
	 * A part of {@link #remove(Object)} used while this is linear-probing.
	 * @param key key whose mapping is to be removed from the map; must not be null
	 * @return the previous value associated with {@code key}, or
	 * {@link #getDefaultValue()} if there was no mapping for {@code key}
	 */
	protected int removeLinear(@NonNull Object key, int defaultValue) {
		int i = locateKey(key);
		if (i < 0) return defaultValue;
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		K rem;
		int oldValue = valueTable[i];
		int mask = this.mask, next = i + 1 & mask;
		while ((rem = keyTable[next]) != null) {
			int placement = (int)(rem.hashCode() * hashMultiplier1 >>> shift);
			if ((next - placement & mask) > (i - placement & mask)) {
				keyTable[i] = rem;
				valueTable[i] = valueTable[next];
				i = next;
			}
			next = next + 1 & mask;
		}
		keyTable[i] = null;
		size--;
		return oldValue;
	}

	public void clearApproximate(int maximumCapacity) {
		// approximate table size
		int tableSize = Utilities.tableSize(maximumCapacity, loadFactor);
		if (keyTable.length <= tableSize) {
			clear();
			return;
		}
		size = 0;
		resize(tableSize);
	}

	/**
	 * Removes all the mappings from this map.
	 * The map will be empty after this call returns.
	 */
	public void clear() {
		if (size == 0) {
			return;
		}
		size = 0;
		Utilities.clearObjectArray(keyTable, 0, keyTable.length);
	}

	/**
	 * Gets the default value, a {@code V} which is returned by {@link #get(Object)} if the key is not found.
	 * If not changed, the default value is null.
	 *
	 * @return the current default value
	 */
	public int getDefaultValue () {
		return defaultValue;
	}

	/**
	 * Sets the default value, a {@code V} which is returned by {@link #get(Object)} if the key is not found.
	 * If not changed, the default value is null. Note that {@link #getOrDefault(Object, int)} is also available,
	 * which allows specifying a "not-found" value per-call.
	 *
	 * @param defaultValue may be any V object or null; should usually be one that doesn't occur as a typical value
	 */
	public void setDefaultValue (int defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Pseudo-randomly selects new values for hash multipliers 1 and 2, using the existing hash multipliers and a
	 * {@code modifier} to determine which multipliers to choose. This draws multipliers exactly from
	 * {@link Utilities#GOOD_MULTIPLIERS}, with {@code hashMultiplier1} receiving a value from the first 256 items in
	 * that table, and {@code hashMultiplier2} receiving a value from the last 256 items.
	 * @param modifier usually refers to a new size of table when they are being resized (but doesn't have to)
	 */
	protected void regenHashMultipliers(int modifier) {
		//This is close to a kind of Xor-Square-Or pattern, or XQO, that (if modifier weren't added) would be a passable
		//random number generator. The result is used to select one of 256 possible long values for hashMultiplier1, and
		//a different result selects from a different 256 possible long values for hashMultiplier2.
		int idx1 = (int)(-(hashMultiplier2 ^ ((modifier + hashMultiplier2) * hashMultiplier2 | 5L)) >>> 56);
		int idx2 = (int)(-(hashMultiplier1 ^ ((modifier + hashMultiplier1) * hashMultiplier1 | 7L)) >>> 56) | 256;
		hashMultiplier1 = Utilities.GOOD_MULTIPLIERS[idx1];
		hashMultiplier2 = Utilities.GOOD_MULTIPLIERS[idx2];
	}

	/**
	 * Double the size of the map until we can successfully manage to re-add all the items
	 * we currently contain.
	 */
	protected void resize() {
		if(size == 0) return;
		int newSize = keyTable.length;
		do {
			newSize <<= 1;
		} while (resize(newSize));
	}

	/**
	 * The actual implementation of {@link #resize()}, this changes the key and value tables
	 * to use {@code newSize} instead of their previous size, changes the hash multipliers by
	 * calling {@link #regenHashMultipliers(int)}, and then attempts to place all old keys and
	 * values by calling {@link #putSafe(Object, int)}. If putSafe() ever fails here, this
	 * reverts the key/value tables, hash multipliers, and all related state to their values
	 * before this was called, and returns true to indicate a failure did occur. Otherwise,
	 * this returns false.
	 * @param newSize must be a power of two
	 * @return true if there was a problem; false if nothing went wrong
	 */
	@SuppressWarnings("unchecked")
	protected boolean resize(final int newSize) {
		if(size == 0) return true;
		if(flipThreshold == 0) {
			resizeLinear(newSize);
			return false;
		}

		// Save old state as we may need to restore it if the resize() operation fails.
		K[] oldK = keyTable;
		int[] oldV = valueTable;
		long oldH1 = hashMultiplier1;
		long oldH2 = hashMultiplier2;
		mask = newSize - 1;
		shift = Long.numberOfLeadingZeros(newSize - 1L);
		flipThreshold = Integer.numberOfTrailingZeros(newSize) + 4;
		loadThreshold = (int)(loadFactor * newSize) - 1;

		// Already point keyTable and valueTable to the new tables since putSafe operates on them.
		keyTable = (K[])new Object[newSize];
		valueTable = new int[newSize];

		regenHashMultipliers(newSize);

		for (int i = 0; i < oldK.length; i++) {
			if (oldK[i] != null) {
				if (putSafe(oldK[i], oldV[i])) {
					// Placing the old key failed for any reason.
					keyTable = oldK;
					valueTable = oldV;
					hashMultiplier1 = oldH1;
					hashMultiplier2 = oldH2;
					mask = keyTable.length - 1;
					shift = Long.numberOfLeadingZeros(newSize - 1L);
					flipThreshold = Integer.numberOfTrailingZeros(keyTable.length) + 4;
					loadThreshold = (int)(loadFactor * keyTable.length) - 1;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * A part of {@link #resize(int)} used when this is linear-probing.
	 * Unlike {@link #resize(int)}, this can't enter a failure state during
	 * normal operation, so it doesn't return anything. It can, like any
	 * collection, throw an {@link OutOfMemoryError} if too much data is
	 * placed into it.
	 * @param newSize must be a power of two
	 */
	@SuppressWarnings("unchecked")
	protected void resizeLinear(int newSize) {
		int oldCapacity = keyTable.length;
		loadThreshold = (int)(newSize * loadFactor) - 1;
		mask = newSize - 1;
		shift = Long.numberOfLeadingZeros(newSize - 1L);

		hashMultiplier1 = Utilities.GOOD_MULTIPLIERS[(int)(hashMultiplier1 >>> 48 + shift) & 511];
		K[] oldKeyTable = keyTable;
		int[] oldValueTable = valueTable;

		keyTable = (K[])new Object[newSize];
		valueTable = new int[newSize];

		if (size > 0) {
			for (int i = 0; i < oldCapacity; i++) {
				K key = oldKeyTable[i];
				if (key != null) {
					putResizeLinear(key, oldValueTable[i]);}
			}
		}
	}

	/**
	 * "Flips the switch" from using cuckoo hashing to using linear probing.  This changes {@link #flipThreshold} to 0,
	 * always, which is what indicates we have switched to linear probing. This must only be called once, typically when
	 * cuckoo hashing has failed to place a key, and it cannot be reversed. While this degrades the performance of the
	 * map somewhat in the best-case and expected-case, it drastically improves performance in the worst-case, which
	 * should be the only time this has to be called.
	 */
	@SuppressWarnings("unchecked")
	protected void flip() {
		// Save old state as we may need to restore it if the resize() operation fails.
		K[] oldK = keyTable;
		int[] oldV = valueTable;

		keyTable = (K[]) new Object[oldK.length];
		valueTable = new int[oldV.length];

		loadFactor = (float) Math.sqrt(loadFactor);
		flipThreshold = 0;
		loadThreshold = (int)(loadFactor * keyTable.length) - 1;
		size = 0;

		for (int i = 0; i < oldK.length; i++) {
			if (oldK[i] != null) {
				putLinear(oldK[i], oldV[i]);
			}
		}
	}

	public int size () {
		return size;
	}

	public boolean isEmpty () {
		return size == 0;
	}

	public void putAll (ObjectIntMap<? extends K> m) {
		for (Entry<? extends K> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public boolean containsValue (int value) {
		for (int i = 0; i < keyTable.length; i++) {
			if (keyTable[i] != null && valueTable[i] == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the index of the key if already present, else {@code ~index} for the next empty index.
	 * Used during linear probing.
	 *
	 * @param key a non-null K key
	 * @return a negative index if the key was not found, or the non-negative index of the existing key if found
	 */
	protected int locateKey (@NonNull Object key) {
		K[] keyTable = this.keyTable;
		for (int i = (int)(key.hashCode() * hashMultiplier1 >>> shift); ; i = i + 1 & mask) {
			K other = keyTable[i];
			if (key.equals(other))
				return i; // Same key was found.
			if (other == null)
				return ~i; // Always negative; means empty space is available at position `i`.
		}
	}

	/**
	 * Puts key and value but skips checks for existing keys, and doesn't increment size. Meant for use during
	 * {@link #resizeLinear(int)}, hence the name, and only when using linear probing.
	 */
	protected void putResizeLinear(@NonNull K key, int value) {
		K[] keyTable = this.keyTable;
		for (int i = (int)(key.hashCode() * hashMultiplier1 >>> shift); ; i = i + 1 & mask) {
			if (keyTable[i] == null) {
				keyTable[i] = key;
				valueTable[i] = value;
				return;
			}
		}
	}

	/**
	 * If {@code key} is present in the map, this returns the value it is mapped to; otherwise, this inserts
	 * {@code key} into the map with its associated {@code value}. This will never remove or replace values.
	 * @param key a {@code K} key to look up and either get what it finds or put if it found nothing
	 * @param value the value to associate with {@code key} if it is not already present
	 * @return the value associated with {@code key} after this completes (and potentially inserts an entry)
	 */
	public int putOrGet(K key, int value) {
		if(key == null) throw new NullPointerException("ObjectIntMap does not permit null keys.");

		if(flipThreshold == 0)
			return putOrGetLinear(key, value);

		int hc = key.hashCode();
		int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
		if (key.equals(keyTable[hr1])) {
			return valueTable[hr1];
		} else {
			int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
			if (key.equals(keyTable[hr2])) {
				return valueTable[hr2];
			}
		}

        // If we need to resize after adding this item, it's probably best to resize before we add it.
        if (size >= loadThreshold)
            resize();
        // If placing key with cuckoo hashing fails, putOrGetFallback falls back to linear probing by calling flip().
		return putOrGetFallback(key, value, hc);
	}

	/**
	 * A part of {@link #putOrGet(Object, int)} that is used when this is linear-probing.
	 * @param key key with which the specified value is to be associated; must not be null
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with {@code key}, or
	 * {@link #getDefaultValue()} if this placed {@code key} into an empty space
	 */
	protected int putOrGetLinear(@NonNull K key, int value){
		// this mostly inlines locateKey() manually, but is able to remove some steps it would do.
		K[] keyTable = this.keyTable;
		int i = (int)(key.hashCode() * hashMultiplier1 >>> shift);
		for (; ; i = i + 1 & mask) {
			K other = keyTable[i];
			if (key.equals(other))
				return valueTable[i]; // Same key was found.
			if (other == null){
				i = ~i; // Empty space was found.
				keyTable[i] = key;
				valueTable[i] = value;
				if (size++ >= loadThreshold) resizeLinear(keyTable.length << 1);
				return defaultValue;
			}
		}
	}

	/**
	 * Attempts to place the given key and value, but is permitted to fail. If this fails, it starts the
	 * switch to use linear probing by calling {@link #flip()}, and completes the put operation using
	 * linear probing.
	 * @return the key we failed to move because of collisions or {@code null} if successful.
	 */
	protected int putOrGetFallback (K key, int value, int hc) {
		int loop = 0;
		while (loop++ < flipThreshold) {
			int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;
			K k1 = keyTable[hr1];
			if (k1 == null) {
				valueTable[hr1] = value;
				++size;
				return defaultValue;
			}
			if(key.equals(k1))
				return valueTable[hr1];

			int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
			K k2 = keyTable[hr2];
			if (k2 == null){
				valueTable[hr2] = value;
				++size;
				return defaultValue;
			}
			if(key.equals(k2))
				return valueTable[hr2];

			// Both tables have an item in the required position that doesn't have the same key, we need to move things around.
			// Prefer always moving from the odd entries for simplicity.
			int temp = valueTable[hr1];
			keyTable[hr1] = key;
			key = k1;
			valueTable[hr1] = value;
			value = temp;
			hc = key.hashCode();
		}
		flip();
		// From this point on, we are using linear probing, not cuckoo hashing.
		return putOrGetLinear(key, value);
	}

	public @NonNull Set<Entry<K>> entrySet () {
		Set<Entry<K>> entries;
		return (entries = entrySet) == null ? (entrySet = new EntrySet<>(this)) : entries;
	}

	@Override
	public int hashCode () {
		int h = size;
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				h += key.hashCode() + valueTable[i];
			}
		}
		return h;
	}

	@Override
	public boolean equals (Object obj) {
		if (obj == this) {return true;}
		if (!(obj instanceof ObjectIntMap)) {return false;}
		ObjectIntMap other = (ObjectIntMap)obj;
		if (other.size != size) {return false;}
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				int otherValue = other.getOrDefault(key, Integer.MIN_VALUE);
				if (otherValue == Integer.MIN_VALUE && !other.containsKey(key))
					return false;
				if (otherValue != valueTable[i])
					return false;
			}
		}
		return true;
	}

	public String toString (String separator) {
		return toString(separator, false);
	}

	@Override
	public String toString () {
		return toString(", ", true);
	}

	public String toString (String separator, boolean braces) {
		if (size == 0) {return braces ? "{}" : "";}
		StringBuilder buffer = new StringBuilder(32);
		if (braces) {buffer.append('{');}
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		int i = keyTable.length;
		while (i-- > 0) {
			K key = keyTable[i];
			if (key == null) {continue;}
			buffer.append(key == this ? "(this)" : key);
			buffer.append('=');
			int value = valueTable[i];
			buffer.append(value);
			break;
		}
		while (i-- > 0) {
			K key = keyTable[i];
			if (key == null) {continue;}
			buffer.append(separator);
			buffer.append(key == this ? "(this)" : key);
			buffer.append('=');
			int value = valueTable[i];
			buffer.append(value);
		}
		if (braces) {buffer.append('}');}
		return buffer.toString();
	}

	/**
	 * @see #toObjectObjectMap()
	 * @return a HashMap with the same keys and boxed values
	 */
	public Map<K, Integer> toHashMap() {
		Map<K, Integer> map = new HashMap<>(size);
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K k = keyTable[i];
			if (k != null) {
				map.put(k, valueTable[i]);
			}
		}
		return map;
	}

	/**
	 * Of the two toFooBarMap methods, this is recommended
	 * because it allows keeping the exact structure of an ObjectIntMap
	 * and only changing the value type.
	 * @return an ObjectObjectMap (from this package) with the same keys and boxed values
	 */
	public Map<K, Integer> toObjectObjectMap() {
		ObjectObjectMap<K, Integer> map = new ObjectObjectMap<>(1, loadFactor);
		map.size = size;
		map.mask = mask;
		map.shift = shift;
		map.flipThreshold = flipThreshold;
		map.loadFactor = loadFactor;
		map.loadThreshold = loadThreshold;
		map.defaultValue = defaultValue;
		map.hashMultiplier1 = hashMultiplier1;
		map.hashMultiplier2 = hashMultiplier2;
		map.keyTable = Arrays.copyOf(keyTable, keyTable.length);
		map.valueTable = new Integer[valueTable.length];
		for (int i = 0, n = keyTable.length; i < n; i++) {
			if(keyTable[i] != null) map.valueTable[i] = valueTable[i];
		}
		return map;
	}

	public void forEach(BiConsumer<? super K, Integer> action) {
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K k = keyTable[i];
			if (k != null) {
				action.accept(k, valueTable[i]);
			}
		}
	}

	public void forEach(ObjIntConsumer<? super K> action) {
		K[] keyTable = this.keyTable;
		int[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K k = keyTable[i];
			if (k != null) {
				action.accept(k, valueTable[i]);
			}
		}
	}


	/**
	 * Like a {@link Map.Entry} with a mutable key and a mutable int value, so it can be reused.
	 * @param <K> Should match the {@code K} of a Map that contains this Entry
	 */
	public static class Entry<K> {
		@Nullable public K key;
		public int value;

		public Entry () {
		}

		public Entry (@Nullable K key, int value) {
			this.key = key;
			this.value = value;
		}

		public Entry (Entry<? extends K> entry) {
			key = entry.getKey();
			value = entry.getValue();
		}

		@Override
		public String toString () {
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
		public K getKey () {
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
		public int getValue () {
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
		 */
		public int setValue (int value) {
			int old = this.value;
			this.value = value;
			return old;
		}

		@Override
		public boolean equals (@Nullable Object o) {
			if (this == o) {return true;}
			if (o == null || getClass() != o.getClass()) {return false;}

			Entry<?> entry = (Entry<?>)o;

			if (!Objects.equals(key, entry.key)) {return false;}
			return value == entry.value;
		}

		@Override
		public int hashCode () {
			int result = key != null ? key.hashCode() : 0;
			result = 31 * result + value;
			return result;
		}
	}

	/**
	 * A Set of {@link Entry} that is closely tied to a map, and represents the K,V entries in that map.
	 * You normally create an EntrySet via {@link #entrySet()}, but you can also call the constructor yourself if you
	 * want to iterate over the same map at the same time at different rates.
	 * @param <K> Should match the {@code K} of the related map
	 */
	public static class EntrySet<K> extends AbstractSet<Entry<K>> {

		protected final ObjectIntMap<K> map;
		public EntrySet(ObjectIntMap<K> map) {
			this.map = map;
		}
		/**
		 * Returns an iterator over the elements contained in this collection.
		 *
		 * @return an iterator over the elements contained in this collection
		 */
		@Override
		@NonNull
		public Iterator<Entry<K>> iterator() {
			return new EntryIterator<>(map);
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public boolean remove(Object o) {
			if(o == null) return false;
			Iterator<Entry<K>> it = iterator();
			while (it.hasNext()) {
				if (it.next().equals(o)) {
					it.remove();
					return true;
				}
			}
			return false;
		}

		/**
		 * Always throws an {@link UnsupportedOperationException}.
		 * @param c ignored
		 */
		@Override
		public boolean addAll(@NonNull Collection<? extends Entry<K>> c) {
			throw new UnsupportedOperationException("Adding to an EntrySet must be done through its connected Map.");
		}
	}

	public static class EntryIterator<K> implements Iterable<Entry<K>>, Iterator<Entry<K>> {
		public boolean hasNext;

		protected final ObjectIntMap<K> map;
		protected final Entry<K> entry;
		protected int nextIndex, currentIndex;
		public boolean valid = true;

		public EntryIterator(ObjectIntMap<K> map) {
			this.map = map;
			entry = new Entry<>();
			reset();
		}

		public void reset () {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
		}

		protected void findNextIndex () {
			K[] keyTable = map.keyTable;
			for (int n = keyTable.length; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					return;
				}
			}
			hasNext = false;
		}

		/**
		 * Note: the same entry instance is returned each time this method is called.
		 *
		 * @return a reused Entry that will have its key and value set to the next pair
		 */
		@Override
		public Entry<K> next () {
			if (!hasNext) {throw new NoSuchElementException();}
			if (!valid) {throw new RuntimeException("#iterator() cannot be used nested.");}
			K[] keyTable = map.keyTable;
			entry.key = keyTable[nextIndex];
			entry.value = map.valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return entry;
		}

		@Override
		public boolean hasNext () {
			if (!valid) {throw new RuntimeException("#iterator() cannot be used nested.");}
			return hasNext;
		}

		@Override
		public void remove () {
			int i = currentIndex;
			if (i < 0) {throw new IllegalStateException("next must be called before remove.");}
			K[] keyTable = map.keyTable;
			int[] valueTable = map.valueTable;

			if(map.flipThreshold == 0) {
				removeLinear(i, keyTable, valueTable);
				return;
			}
			K key = keyTable[i];
			final long hashMultiplier1 = map.hashMultiplier1, hashMultiplier2 = map.hashMultiplier2;
			int hc = key.hashCode(), shift = map.shift;
			int hr1 = (int)(hashMultiplier1 * hc >>> shift) | 1;

			if (key.equals(keyTable[hr1])) {
				keyTable[hr1] = null;
				map.size--;
				if (i != currentIndex) {--nextIndex;}
				currentIndex = -1;

			} else {
				int hr2 = (int)(hashMultiplier2 * hc >>> shift) & -2;
				if (key.equals(keyTable[hr2])) {
					keyTable[hr2] = null;
					map.size--;
					if (i != currentIndex) {--nextIndex;}
					currentIndex = -1;
				}
			}
		}

		protected void removeLinear(int i, final K[] keyTable, final int[] valueTable) {
			final long hashMultiplier1 = map.hashMultiplier1;
			K rem;
			int mask = map.mask, next = i + 1 & mask, shift = map.shift;
			while ((rem = keyTable[next]) != null) {
				int placement = (int)(rem.hashCode() * hashMultiplier1 >>> shift);
				if ((next - placement & mask) > (i - placement & mask)) {
					keyTable[i] = rem;
					valueTable[i] = valueTable[next];
					i = next;
				}
				next = next + 1 & mask;
			}
			keyTable[i] = null;
			map.size--;
			if (i != currentIndex) {--nextIndex;}
			currentIndex = -1;

		}

		/**
		 * Returns an iterator over elements of type {@code T}.
		 *
		 * @return an Iterator.
		 */
		@Override
		public @NonNull Iterator<Entry<K>> iterator() {
			return this;
		}
	}

	public static class KeySet<K> extends AbstractSet<K> {
		protected final ObjectIntMap<K> map;
		public KeySet(ObjectIntMap<K> map) {
			this.map = map;
		}

		public @NonNull Iterator<K> iterator() {
			return new KeyIterator<>(map);
		}

		public int size() {
			return map.size();
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public void clear() {
			map.clear();
		}

		public boolean contains(Object k) {
			return map.containsKey(k);
		}

		@Override
		public boolean remove(Object o) {
			if(o == null) return false;
			Iterator<K> it = iterator();
			while (it.hasNext()) {
				if (it.next().equals(o)) {
					it.remove();
					return true;
				}
			}
			return false;
		}

		/**
		 * Always throws an {@link UnsupportedOperationException}.
		 * @param c ignored
		 */
		@Override
		public boolean addAll(@NonNull Collection<? extends K> c) {
			throw new UnsupportedOperationException("Adding to a KeySet must be done through its connected Map.");
		}

	}

    public @NonNull Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet<>(this);
			keySet = ks;
        }
        return ks;
    }


	public static class ValueCollection implements PrimitiveCollection.OfInt {
		protected final ObjectIntMap<?> map;
		public ValueCollection(ObjectIntMap<?> map) {
			this.map = map;
		}

		@Override
		public boolean add(int item) {
			throw new UnsupportedOperationException("Add to a ValueCollection via its connected map.");
		}

		@Override
		public boolean remove(int item) {
			PrimitiveIterator.OfInt it = iterator();
			while (it.hasNext()) {
				if (item == (it.next())) {
					it.remove();
					return true;
				}
			}
			return false;
		}


		public PrimitiveIterator.@NonNull OfInt iterator() {
			return new ValueIterator(map);
		}

		public int size() {
			return map.size();
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public void clear() {
			map.clear();
		}

		public boolean contains(int v) {
			return map.containsValue(v);
		}

		/**
		 * Always throws an {@link UnsupportedOperationException}.
		 * @param c ignored
		 */
		public boolean addAll(PrimitiveCollection.@NonNull OfInt c) {
			throw new UnsupportedOperationException("Adding to a ValueCollection must be done through its connected Map.");
		}

	}

	public PrimitiveCollection.@NonNull OfInt values() {
		PrimitiveCollection.OfInt vals = values;
        if (vals == null) {
            vals = new ValueCollection(this);
            values = vals;
        }
        return vals;
    }

	protected static class KeyIterator<K> implements Iterator<K>, Iterable<K> {
		protected final Iterator<? extends Entry<K>> iter;

		public KeyIterator(ObjectIntMap<K> map) {
			iter = map.entrySet().iterator();
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public K next() {
			return iter.next().getKey();
		}

		public void remove() {
			iter.remove();
		}

		/**
		 * Returns an iterator over elements of type {@code K}.
		 *
		 * @return an Iterator.
		 */
		@Override
		public @NonNull Iterator<K> iterator () {
			return this;
		}
	}

	protected static class ValueIterator implements PrimitiveIterator.OfInt {
		protected final Iterator<? extends Entry<?>> iter;

		public ValueIterator(ObjectIntMap<?> map) {
			iter = map.entrySet().iterator();
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public int nextInt() {
			return iter.next().getValue();
		}

		public void remove() {
			iter.remove();
		}
	}

}