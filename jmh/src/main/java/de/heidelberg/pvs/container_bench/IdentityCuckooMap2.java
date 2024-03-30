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

package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.IdentitySet;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.Utilities;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

/**
 * A cuckoo hash table based implementation of the <tt>Map</tt> interface, which
 * compares and hashes keys by identity.
 * This implementation provides all the optional map operations, and permits
 * {@code null} values, but not {@code null} keys. This class makes no
 * guarantees as to the order of the map; in particular, it does not guarantee
 * that the order will remain constant over time.
 * <p>
 * This implementation provides constant-time performance for most basic operations
 * (including but not limited to <tt>get</tt> and <tt>put</tt>). Specifically,
 * the implementation guarantees O(1) time performance on <tt>get</tt> calls and
 * amortized O(1) on <tt>put</tt> (when and only when identity hash codes are
 * unique).
 * <p>
 * Iterating over the collection requires a time proportional to the capacity
 * of the map. The default capacity of an empty map is 16. The map will resize
 * its internal capacity whenever it grows past the load factor specified for the
 * current instance. The default load factor for this map is <code>0.45</code>.
 * Beware that this implementation can only guarantee non-amortized O(1) on
 * <tt>get</tt> iff the load factor is relatively low (generally below 0.60).
 * For more details, it's interesting to read <a href="http://www.it-c.dk/people/pagh/papers/cuckoo-jour.pdf">the
 * original Cuckoo Hash Map paper</a>.
 * <p>
 * If many mappings are to be stored in a <tt>IdentityCuckooMap</tt> instance, creating
 * it with a sufficiently large capacity will allow the mappings to be stored more
 * efficiently than letting it perform automatic rehashing as needed to grow the table.
 * <p>
 * Note that this implementation is not synchronized and not thread safe. If you need
 * thread safety, you'll need to implement your own locking around the map or wrap
 * the instance around a call to {@link Collections#synchronizedMap(Map)}.
 * <p>
 * This is derived from <a href="https://github.com/ivgiuliani/cuckoohash">this Github repo</a>
 * by Ivan Giuliani.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class IdentityCuckooMap2<K, V> extends AbstractMap<K, V> implements Map<K, V> {

	protected static final int THRESHOLD_LOOP = 8;
	protected static final int DEFAULT_START_SIZE = 16;
	protected static final float DEFAULT_LOAD_FACTOR = 0.45f;

	protected float loadFactor;

	protected int mask;
	protected int hashMultiplier1;
	protected int hashMultiplier2;

	protected int size;

	protected K[] keyTable;
	protected V[] valueTable;

	private transient V unplacedValue;

	/**
	 * Constructs an empty <tt>IdentityCuckooMap</tt> with the default initial capacity (16).
	 */
	public IdentityCuckooMap2() {
		this(DEFAULT_START_SIZE, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty <tt>IdentityCuckooMap</tt> with the specified initial capacity.
	 * The given capacity will be rounded to the nearest power of two.
	 *
	 * @param initialCapacity the initial capacity.
	 */
	public IdentityCuckooMap2(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty <tt>IdentityCuckooMap</tt> with the specified load factor.
	 * <p>
	 * The load factor will cause the Cuckoo hash map to double in size when the number
	 * of items it contains has filled up more than <tt>loadFactor</tt>% of the available
	 * space.
	 *
	 * @param loadFactor the load factor.
	 */
	public IdentityCuckooMap2(float loadFactor) {
		this(DEFAULT_START_SIZE, loadFactor);
	}

	@SuppressWarnings("unchecked")
	public IdentityCuckooMap2(int initialCapacity, float loadFactor) {
		if (initialCapacity <= 0) {
			throw new IllegalArgumentException("initial capacity must be strictly positive");
		}
		if (loadFactor <= 0.f || loadFactor > 1.f) {
			throw new IllegalArgumentException("load factor must be a value in the (0.0f, 1.0f] range.");
		}

		size = 0;
		int tableSize = Math.max(2, 1 << -Integer.numberOfLeadingZeros(initialCapacity));
		mask = -1 >>> Integer.numberOfLeadingZeros(tableSize - 1);

		keyTable = (K[])new Object[tableSize];
		valueTable = (V[])new Object[tableSize];
		this.loadFactor = loadFactor;

		regenHashFunctions(tableSize);
	}

	public IdentityCuckooMap2(IdentityCuckooMap2<? extends K, ? extends V> other) {
		size = other.size;
		mask = other.mask;
		loadFactor = other.loadFactor;
		hashMultiplier1 = other.hashMultiplier1;
		hashMultiplier2 = other.hashMultiplier2;
		keyTable = Arrays.copyOf(other.keyTable, other.keyTable.length);
		valueTable = Arrays.copyOf(other.valueTable, other.valueTable.length);
	}

	@Override
	public boolean containsKey (Object key) {
		final int hc = System.identityHashCode(key);

		return key != null && (key == keyTable[(hashMultiplier1 * hc & mask) | 1] ||
				key == keyTable[(hashMultiplier2 * hc & mask) & -2]);
	}

	@Override
	public V get (Object key) {
		return get(key, null);
	}

	public V getOrDefault (Object key, V defaultValue) {
		return get(key, defaultValue);
	}

	private V get (Object key, V defaultValue) {
		if(key == null) return defaultValue;

		int hc = System.identityHashCode(key);
		int hr1 = (hashMultiplier1 * hc & mask) | 1;
		if (key == keyTable[hr1]) {
			return valueTable[hr1];
		}

		int hr2 = (hashMultiplier2 * hc & mask) & -2;
		if (key == keyTable[hr2]) {
			return valueTable[hr2];
		}

		return defaultValue;
	}

	@Override
	public V put (K key, V value) {
		if(key == null) throw new NullPointerException("IdentityCuckooMap does not permit null keys.");

		boolean absent = true;
		V old = null;
		int hc = System.identityHashCode(key);
		int hr1 = (hashMultiplier1 * hc & mask) | 1;
		if (key == keyTable[hr1]) {
			old = valueTable[hr1];
			absent = false;
		} else {
			int hr2 = (hashMultiplier2 * hc & mask) & -2;
			if (key == keyTable[hr2]) {
				old = valueTable[hr2];
				absent = false;
			}
		}

		if (absent) {
			// If we need to grow after adding this item, it's probably best to grow before we add it.
			if (size() + 1 >= loadFactor * keyTable.length) {
				grow();
			}
		}
		while ((key = putSafe(key, value)) != null) {
			value = unplacedValue;
			unplacedValue = null;
			if (!rehash()) {
				grow();
			}
		}

		if (absent) {
			// Only increase the size if no item was already there.
			size++;
		}

		return old;
	}

	/**
	 * @return the key we failed to move because of collisions or <tt>null</tt> if
	 * successful.
	 */
	private K putSafe (K key, V value) {
		int loop = 0;
		while (loop++ < THRESHOLD_LOOP) {
			int hc = System.identityHashCode(key);
			int hr1 = (hashMultiplier1 * hc & mask) | 1;
			K k1 = keyTable[hr1];
			if (k1 == null || key == k1) {
				valueTable[hr1] = value;
				return null;
			}
			int hr2 = (hashMultiplier2 * hc & mask) & -2;
			K k2 = keyTable[hr2];
			if (k2 == null || key == k2) {
				valueTable[hr2] = value;
				return null;
			}

			// Both tables have an item in the required position that doesn't have the same key, we need to move things around.
			// Prefer always moving from T1 for simplicity.
			V temp = valueTable[hr1];
			keyTable[hr1] = key;
			key = k1;
			valueTable[hr1] = value;
			value = temp;
		}
		unplacedValue = value;
		return key;
	}

	@Override
	public V remove (Object key) {
		if (key == null)
			return null;
		int hc = System.identityHashCode(key);
		int hr1 = (hashMultiplier1 * hc & mask) | 1;
		V oldValue = null;

		if (key == keyTable[hr1]) {
			oldValue = valueTable[hr1];
			keyTable[hr1] = null;
			valueTable[hr1] = null;
			size--;
		} else {
			int hr2 = (hashMultiplier2 * hc & mask) & -2;
			if (key == keyTable[hr2]) {
				oldValue = valueTable[hr2];
				keyTable[hr2] = null;
				valueTable[hr2] = null;
				size--;
			}
		}

		return oldValue;
	}

	@Override
	public void clear () {
		size = 0;
		Arrays.fill(keyTable, null);
		Arrays.fill(valueTable, null);
	}

	private void regenHashFunctions (int modifier) {
		//This is close to a kind of Xor-Square-Or pattern, or XQO, that (if modifier weren't added) would be a passable
		//random number generator. The result is used to select one of 256 possible long values for hashMultiplier1, and
		//a different result selects from a different 256 possible long values for hashMultiplier2. The modifier here
		//usually refers to a new size of the key and value tables, but where this is called in rehash(), it is
		//different in each loop iteration there.
		int idx1 = -(hashMultiplier2 ^ ((modifier + hashMultiplier2) * hashMultiplier2 | 5)) >>> 24;
		int idx2 = (-(hashMultiplier1 ^ ((modifier + hashMultiplier1) * hashMultiplier1 | 7)) >>> 24) | 256;
		hashMultiplier1 = (int)Utilities.GOOD_MULTIPLIERS[idx1];
		hashMultiplier2 = (int)Utilities.GOOD_MULTIPLIERS[idx2];
	}

	/**
	 * Double the size of the map until we can successfully manage to re-add all the items
	 * we currently contain.
	 */
	private void grow () {
		int newSize = keyTable.length;
		do {
			newSize <<= 1;
		} while (!grow(newSize));
	}

	@SuppressWarnings("unchecked")
	private boolean grow (final int newSize) {
		// Save old state as we may need to restore it if the grow() operation fails.
		K[] oldK = keyTable;
		V[] oldV = valueTable;
		int oldH1 = hashMultiplier1;
		int oldH2 = hashMultiplier2;
		mask = -1 >>> Integer.numberOfLeadingZeros(newSize - 1);

		// Already point keyTable and valueTable to the new tables since putSafe operates on them.
		keyTable = (K[])new Object[newSize];
		valueTable = (V[])new Object[newSize];

		regenHashFunctions(newSize);

		for (int i = 0; i < oldK.length; i++) {
			if (oldK[i] != null) {
				if (putSafe(oldK[i], oldV[i]) != null) {
					keyTable = oldK;
					valueTable = oldV;
					hashMultiplier1 = oldH1;
					hashMultiplier2 = oldH2;
					return false;
				}
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean rehash () {
		// Save old state as we may need to restore it if the grow() operation fails.
		K[] oldK = keyTable;
		V[] oldV = valueTable;
		int oldH1 = hashMultiplier1;
		int oldH2 = hashMultiplier2;

		keyTable = (K[])new Object[oldK.length];
		valueTable = (V[])new Object[oldV.length];

		RETRIAL:
		for (int threshold = 0; threshold < THRESHOLD_LOOP; threshold++) {

			regenHashFunctions(keyTable.length + threshold);

			for (int i = 0; i < oldK.length; i++) {
				if (oldK[i] != null) {
					if (putSafe(oldK[i], oldV[i]) != null) {
						clear();
						continue RETRIAL;
					}
				}
			}
			return true;
		}
		// Restore state; we need to change back the hash multipliers.
		keyTable = oldK;
		valueTable = oldV;
		hashMultiplier1 = oldH1;
		hashMultiplier2 = oldH2;
		return false;
	}

	@Override
	public int size () {
		return size;
	}

	@Override
	public boolean isEmpty () {
		return size == 0;
	}

	@Override
	public void putAll (Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public @NonNull Set<K> keySet () {
		IdentitySet<K> set = new IdentitySet<>(size);
		for (int i = 0; i < keyTable.length; i++) {
			if (keyTable[i] != null) {
				set.add(keyTable[i]);
			}
		}
		return set;
	}

	@Override
	public @NonNull Collection<V> values () {
		ObjectList<V> values = new ObjectList<>(size);
		for (int i = 0; i < keyTable.length; i++) {
			if (keyTable[i] != null) {
				values.add(valueTable[i]);
			}
		}
		return values;
	}

	@Override
	public @NonNull Set<Entry<K, V>> entrySet () {
		IdentitySet<Entry<K, V>> set = new IdentitySet<>(size);
		for (int i = 0; i < keyTable.length; i++) {
			if (keyTable[i] != null) {
				set.add(new SimpleImmutableEntry<>(keyTable[i], valueTable[i]));
			}
		}
		return set;
	}

	@Override
	public boolean containsValue (Object value) {
		for (int i = 0; i < keyTable.length; i++) {
			if (keyTable[i] != null && Objects.equals(valueTable[i], value)) {
				return true;
			}
		}
		return false;
	}

}