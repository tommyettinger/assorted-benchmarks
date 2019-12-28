/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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
 ******************************************************************************/

package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An unordered map. This implementation is a cuckoo hash map using 3 hashes, random walking, and a small stash for problematic
 * keys. Null keys are not allowed. Null values are allowed. No allocation is done except when growing the table size. <br>
 * <br>
 * This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit slower,
 * depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have to rehash to the
 * next higher POT size.
 * @author Nathan Sweet */
public class MerryObjectMap<K, V> implements Iterable<MerryObjectMap.Entry<K, V>> {
//	private static final int PRIME1 = 0x17C231;//0xbe1f14b1;
//	private static final int PRIME2 = 0x174DF9;//0xb4b82e39;
//	private static final int PRIME3 = 0x19E151;//0xced1c241;

	public int size;

	K[] keyTable;
	V[] valueTable;
	/**
	 * Initial Bucket positions.
	 */
	private int[] ib;

	private float loadFactor;
	private int mask, threshold, shift;

	private Entries entries1, entries2;
	private Values values1, values2;
	private Keys keys1, keys2;

	/** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
	public MerryObjectMap() {
		this(51, 0.8f);
	}

	/** Creates a new map with a load factor of 0.8.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
	public MerryObjectMap(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	/** Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
	public MerryObjectMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		if (loadFactor <= 0f || loadFactor >= 1f) throw new IllegalArgumentException("loadFactor must be > 0 and < 1: " + loadFactor);
		initialCapacity = MathUtils.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
		if (initialCapacity > 1 << 30) throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);

		this.loadFactor = loadFactor;


		threshold = (int)(initialCapacity * loadFactor);
		mask = initialCapacity - 1;
		shift = Long.numberOfLeadingZeros(mask);

		keyTable = (K[])new Object[initialCapacity];
		valueTable = (V[])new Object[initialCapacity];
		ib = new int[initialCapacity];
	}

	/** Creates a new map identical to the specified map. */
	public MerryObjectMap(MerryObjectMap<? extends K, ? extends V> map) {
		this((int) Math.floor(map.ib.length * map.loadFactor), map.loadFactor);
		System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
		System.arraycopy(map.ib, 0, ib, 0, map.ib.length);
		size = map.size;
	}

	private int bucket(final int hashCode) {
		// fibonacci hashing; may improve resistance to bad hashCode()s
		// shift is always greater than 32, less than 64
		// 0x9E3779B97F4A7C15L is 2 to the 64 divided by the golden ratio
		// the golden ratio has specific properties that make it work well here
		return (int) (hashCode * 0x9E3779B97F4A7C15L >>> shift);
	}

	private int bucketDistance(final int initialBucket, final int curBucketIndex) {
		return curBucketIndex - initialBucket & mask;
	}

	private int locateKey(K key) {

		int bucket = bucket(key.hashCode());

		for (int i = bucket; ; i = (i + 1) & mask) {
			// empty space is available
			if (keyTable[i] == null) {
				return -1;
			}
			if (key.equals(keyTable[i])) {
				return i;
			}
			// ib holds the initial bucket position before probing offset the item
			// if the distance required to probe to a position is greater than the
			// stored distance for an item at that position, we can Robin Hood and swap them.
			if (bucketDistance(ib[i], i) < bucketDistance(bucket, i)) {
				return -1;
			}
		}
	}

	private int locateKey(K key, int bucket) {
		for (int i = bucket; ; i = (i + 1) & mask) {
			// empty space is available
			if (keyTable[i] == null) {
				return -1;
			}
			if (key.equals(keyTable[i])) {
				return i;
			}
			// ib holds the initial bucket position before probing offset the item
			// if the distance required to probe to a position is greater than the
			// stored distance for an item at that position, we can Robin Hood and swap them.
			if (bucketDistance(ib[i], i) < bucketDistance(bucket, i)) {
				return -1;
			}
		}
	}


	/** Returns the old value associated with the specified key, or null. */
	public V put (K key, V value) {
		if (key == null) throw new IllegalArgumentException("key cannot be null.");
		return put_internal(key, value);
	}

	private V put_internal (K key, V value) {
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		int[] ib = this.ib;
		int b = bucket(key.hashCode());
		int loc = locateKey(key, b);
		// an identical key already exists
		if (loc != -1) {
			V tv = valueTable[loc];
			valueTable[loc] = value;
			return tv;
		}
		for (int i = b; ; i = (i + 1) & mask) {
			// space is available so we insert and break (resize is later)
			if (keyTable[i] == null) {
				keyTable[i] = key;
				valueTable[i] = value;
				ib[i] = b;
				break;
			}
			// if there is a key with a lower probe distance, we swap with it
			// and keep going until we find a place we can insert
			else if (bucketDistance(ib[i], i) < bucketDistance(b, i)) {
				K temp = keyTable[i];
				V tv = valueTable[i];
				int tb = ib[i];
				keyTable[i] = key;
				valueTable[i] = value;
				ib[i] = b;
				key = temp;
				value = tv;
				b = tb;
			}
		}
		if (++size >= threshold) {
			resize(ib.length << 1);
		}
		return null;
	}

	public void putAll (MerryObjectMap<K, V> map) {
		ensureCapacity(map.size);
		for (Entry<K, V> entry : map)
			put(entry.key, entry.value);
	}

	/** Skips checks for existing keys. */
	private void putResize (K key, V value) {
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		int[] ib = this.ib;
		int b = bucket(key.hashCode());
		for (int i = b; ; i = (i + 1) & mask) {
			// space is available so we insert and break (resize is later)
			if (keyTable[i] == null) {
				keyTable[i] = key;
				valueTable[i] = value;
				ib[i] = b;
				break;
			}
			// if there is a key with a lower probe distance, we swap with it
			// and keep going until we find a place we can insert
			else if (bucketDistance(ib[i], i) < bucketDistance(b, i)) {
				K temp = keyTable[i];
				V tv = valueTable[i];
				int tb = ib[i];
				keyTable[i] = key;
				valueTable[i] = value;
				ib[i] = b;
				key = temp;
				value = tv;
				b = tb;
			}
		}
		if (++size >= threshold) {
			resize(ib.length << 1);
		}
	}

	/** Returns the value for the specified key, or null if the key is not in the map. */
	public V get (K key) {
		final int loc = locateKey(key);
		return loc == -1 ? null : valueTable[loc];
	}

	/** Returns the value for the specified key, or the default value if the key is not in the map. */
	public V get (K key, V defaultValue) {
		final int loc = locateKey(key);
		return loc == -1 ? defaultValue : valueTable[loc];
	}

	public V remove (K key) {
		int loc = locateKey(key);
		if (loc == -1) {
			return null;
		}
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		keyTable[loc] = null;
		V oldValue = valueTable[loc];
		valueTable[loc] = null;
		for (int i = (loc + 1) & mask; (keyTable[i] != null && bucketDistance(ib[loc], i) != 0); i = (i + 1) & mask) {
			keyTable[i - 1 & mask] = keyTable[i];
			valueTable[i - 1 & mask] = valueTable[i];
			ib[i - 1 & mask] = ib[i];
			keyTable[i] = null;
			valueTable[i] = null;
			ib[i] = 0;
		}
		--size;
		return oldValue;
	}

	/** Reduces the size of the backing arrays to be the specified capacity or less. If the capacity is already less, nothing is
	 * done. If the map contains more items than the specified capacity, the next highest power of two capacity is used instead. */
	public void shrink (int maximumCapacity) {
		if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
		if (size > maximumCapacity) maximumCapacity = size;
		if (ib.length <= maximumCapacity) return;
		resize(MathUtils.nextPowerOfTwo(maximumCapacity));
	}

	/** Clears the map and reduces the size of the backing arrays to be the specified capacity if they are larger. */
	public void clear (int maximumCapacity) {
		if (ib.length <= maximumCapacity) {
			clear();
			return;
		}
		size = 0;
		resize(maximumCapacity);
	}

	public void clear () {
		if (size == 0) return;
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		int[] ib = this.ib;
		for (int i = ib.length; i > 0;) {
			keyTable[--i] = null;
			valueTable[i] = null;
			ib[i] = 0;
		}
		size = 0;
	}

	/** Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may
	 * be an expensive operation.
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *           {@link #equals(Object)}. */
	public boolean containsValue (Object value, boolean identity) {
		V[] valueTable = this.valueTable;
		if (value == null) {
			K[] keyTable = this.keyTable;
			for (int i = valueTable.length; i-- > 0;)
				if (keyTable[i] != null && valueTable[i] == null) return true;
		} else if (identity) {
			for (int i = valueTable.length; i-- > 0;)
				if (valueTable[i] == value) return true;
		} else {
			for (int i = valueTable.length; i-- > 0;)
				if (value.equals(valueTable[i])) return true;
		}
		return false;
	}

	public boolean containsKey (K key) {
		return locateKey(key) != -1;
	}

	/** Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and compares
	 * every value, which may be an expensive operation.
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *           {@link #equals(Object)}. */
	public K findKey (Object value, boolean identity) {
		V[] valueTable = this.valueTable;
		if (value == null) {
			K[] keyTable = this.keyTable;
			for (int i = valueTable.length; i-- > 0;)
				if (keyTable[i] != null && valueTable[i] == null) return keyTable[i];
		} else if (identity) {
			for (int i = valueTable.length; i-- > 0;)
				if (valueTable[i] == value) return keyTable[i];
		} else {
			for (int i = valueTable.length; i-- > 0;)
				if (value.equals(valueTable[i])) return keyTable[i];
		}
		return null;
	}

	/** Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes. */
	public void ensureCapacity (int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= threshold) resize(MathUtils.nextPowerOfTwo((int) Math.ceil(sizeNeeded / loadFactor)));
	}

	private void resize (int newSize) {
		int oldCapacity = ib.length;
		threshold = (int)(newSize * loadFactor);
		mask = newSize - 1;
		shift = Long.numberOfLeadingZeros(mask);

		K[] oldKeyTable = keyTable;
		V[] oldValueTable = valueTable;

		keyTable = (K[])new Object[newSize];
		valueTable = (V[])new Object[newSize];
		ib = new int[newSize];

		int oldSize = size;
		size = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldCapacity; i++) {
				K key = oldKeyTable[i];
				if (key != null) putResize(key, oldValueTable[i]);
			}
		}
	}

	public int hashCode () {
		int h = 0;
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				h += key.hashCode() * 31;

				V value = valueTable[i];
				if (value != null) {
					h += value.hashCode();
				}
			}
		}
		return h;
	}

	public boolean equals (Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof MerryObjectMap)) return false;
		MerryObjectMap<K, V> other = (MerryObjectMap)obj;
		if (other.size != size) return false;
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		for (int i = 0, n = keyTable.length; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				V value = valueTable[i];
				if (value == null) {
					if (!other.containsKey(key) || other.get(key) != null) {
						return false;
					}
				} else {
					if (!value.equals(other.get(key))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public String toString (String separator) {
		return toString(separator, false);
	}

	public String toString () {
		return toString(", ", true);
	}

	private String toString (String separator, boolean braces) {
		if (size == 0) return braces ? "{}" : "";
		StringBuilder buffer = new StringBuilder(32);
		if (braces) buffer.append('{');
		K[] keyTable = this.keyTable;
		V[] valueTable = this.valueTable;
		int i = keyTable.length;
		while (i-- > 0) {
			K key = keyTable[i];
			if (key == null) continue;
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
			break;
		}
		while (i-- > 0) {
			K key = keyTable[i];
			if (key == null) continue;
			buffer.append(separator);
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		if (braces) buffer.append('}');
		return buffer.toString();
	}

	public Entries<K, V> iterator () {
		return entries();
	}

	/** Returns an iterator for the entries in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration. */
	public Entries<K, V> entries () {
		if (entries1 == null) {
			entries1 = new Entries(this);
			entries2 = new Entries(this);
		}
		if (!entries1.valid) {
			entries1.reset();
			entries1.valid = true;
			entries2.valid = false;
			return entries1;
		}
		entries2.reset();
		entries2.valid = true;
		entries1.valid = false;
		return entries2;
	}

	/** Returns an iterator for the values in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Values} constructor for nested or multithreaded iteration. */
	public Values<V> values () {
		if (values1 == null) {
			values1 = new Values(this);
			values2 = new Values(this);
		}
		if (!values1.valid) {
			values1.reset();
			values1.valid = true;
			values2.valid = false;
			return values1;
		}
		values2.reset();
		values2.valid = true;
		values1.valid = false;
		return values2;
	}

	/** Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Keys} constructor for nested or multithreaded iteration. */
	public Keys<K> keys () {
		if (keys1 == null) {
			keys1 = new Keys(this);
			keys2 = new Keys(this);
		}
		if (!keys1.valid) {
			keys1.reset();
			keys1.valid = true;
			keys2.valid = false;
			return keys1;
		}
		keys2.reset();
		keys2.valid = true;
		keys1.valid = false;
		return keys2;
	}

	static public class Entry<K, V> {
		public K key;
		public V value;

		public String toString () {
			return key + "=" + value;
		}
	}

	static private abstract class MapIterator<K, V, I> implements Iterable<I>, Iterator<I> {
		public boolean hasNext;

		final MerryObjectMap<K, V> map;
		int nextIndex, currentIndex;
		boolean valid = true;

		public MapIterator (MerryObjectMap<K, V> map) {
			this.map = map;
			reset();
		}

		public void reset () {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
		}

		void findNextIndex () {
			hasNext = false;
			K[] keyTable = map.keyTable;
			for (int n = keyTable.length; ++nextIndex < n;) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove () {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");				
			map.keyTable[currentIndex] = null;
			map.valueTable[currentIndex] = null;
			map.ib[currentIndex] = 0;
			currentIndex = -1;
			map.size--;
		}
	}

	static public class Entries<K, V> extends MapIterator<K, V, Entry<K, V>> {
		Entry<K, V> entry = new Entry<K, V>();

		public Entries (MerryObjectMap<K, V> map) {
			super(map);
		}

		/** Note the same entry instance is returned each time this method is called. */
		public Entry<K, V> next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			K[] keyTable = map.keyTable;
			entry.key = keyTable[nextIndex];
			entry.value = map.valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return entry;
		}

		public boolean hasNext () {
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public Entries<K, V> iterator () {
			return this;
		}
	}

	static public class Values<V> extends MapIterator<Object, V, V> {
		public Values (MerryObjectMap<?, V> map) {
			super((MerryObjectMap<Object, V>)map);
		}

		public boolean hasNext () {
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public V next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			V value = map.valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		public Values<V> iterator () {
			return this;
		}

		/** Returns a new array containing the remaining values. */
		public Array<V> toArray () {
			return toArray(new Array(true, map.size));
		}

		/** Adds the remaining values to the specified array. */
		public Array<V> toArray (Array<V> array) {
			while (hasNext)
				array.add(next());
			return array;
		}
	}

	static public class Keys<K> extends MapIterator<K, Object, K> {
		public Keys (MerryObjectMap<K, ?> map) {
			super((MerryObjectMap<K, Object>)map);
		}

		public boolean hasNext () {
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public K next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			K key = map.keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		public Keys<K> iterator () {
			return this;
		}

		/** Returns a new array containing the remaining keys. */
		public Array<K> toArray () {
			return toArray(new Array<K>(true, map.size));
		}

		/** Adds the remaining keys to the array. */
		public Array<K> toArray (Array<K> array) {
			while (hasNext)
				array.add(next());
			return array;
		}
	}
}
