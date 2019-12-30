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

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.NoSuchElementException;

/** An {@link ObjectMap} that also stores keys in an {@link Array} using the insertion order. Iteration over the
 * {@link #entries()}, {@link #keys()}, and {@link #values()} is ordered and faster than an unordered map. Keys can also be
 * accessed and the order changed using {@link #orderedKeys()}. There is some additional overhead for put and remove. When used
 * for faster iteration versus ObjectMap and the order does not actually matter, copying during remove can be greatly reduced by
 * setting {@link Array#ordered} to false for {@link MerryOrderedMap#orderedKeys()}.
 * @author Nathan Sweet */
public class MerryOrderedMap<K, V> extends MerryObjectMap<K, V> {
	private final Array<K> keys;

	public MerryOrderedMap() {
		keys = new Array();
	}

	public MerryOrderedMap(int initialCapacity) {
		super(initialCapacity);
		keys = new Array(keyTable.length);
	}

	public MerryOrderedMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		keys = new Array(keyTable.length);
	}

	public MerryOrderedMap(MerryOrderedMap<? extends K, ? extends V> map) {
		super(map);
		keys = new Array(map.keys);
	}

	public V put (K key, V value) {
		if (key == null) throw new IllegalArgumentException("key cannot be null.");
		V[] valueTable = this.valueTable;
		int b = place(key);
		int loc = locateKey(key, b);
		// an identical key already exists
		if (loc != -1) {
			V tv = valueTable[loc];
			valueTable[loc] = value;
			return tv;
		}
		keys.add(key);
		K[] keyTable = this.keyTable;
		int[] ib = this.ib;
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
			else if ((i - ib[i] & mask) < (i - b & mask)) {
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

	public V remove (K key) {
		keys.removeValue(key, false);
		return super.remove(key);
	}

	public V removeIndex (int index) {
		return super.remove(keys.removeIndex(index));
	}

	public void clear (int maximumCapacity) {
		keys.clear();
		super.clear(maximumCapacity);
	}

	public void clear () {
		keys.clear();
		super.clear();
	}

	public Array<K> orderedKeys () {
		return keys;
	}

	public Entries<K, V> iterator () {
		return entries();
	}

	/** Returns an iterator for the entries in the map. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
	 * Use the {@link OrderedMapEntries} constructor for nested or multithreaded iteration. */
	public Entries<K, V> entries () {
		if (Collections.allocateIterators) return new OrderedMapEntries(this);
		if (entries1 == null) {
			entries1 = new OrderedMapEntries(this);
			entries2 = new OrderedMapEntries(this);
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

	/** Returns an iterator for the values in the map. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
	 * Use the {@link OrderedMapValues} constructor for nested or multithreaded iteration. */
	public Values<V> values () {
		if (Collections.allocateIterators) return new OrderedMapValues(this);
		if (values1 == null) {
			values1 = new OrderedMapValues(this);
			values2 = new OrderedMapValues(this);
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

	/** Returns an iterator for the keys in the map. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
	 * Use the {@link OrderedMapKeys} constructor for nested or multithreaded iteration. */
	public Keys<K> keys () {
		if (Collections.allocateIterators) return new OrderedMapKeys(this);
		if (keys1 == null) {
			keys1 = new OrderedMapKeys(this);
			keys2 = new OrderedMapKeys(this);
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

	public String toString () {
		if (size == 0) return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		Array<K> keys = this.keys;
		for (int i = 0, n = keys.size; i < n; i++) {
			K key = keys.get(i);
			if (i > 0) buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(get(key));
		}
		buffer.append('}');
		return buffer.toString();
	}

	static public class OrderedMapEntries<K, V> extends Entries<K, V> {
		private Array<K> keys;

		public OrderedMapEntries (MerryOrderedMap<K, V> map) {
			super(map);
			keys = map.keys;
		}

		public void reset () {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public Entry next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			entry.key = keys.get(nextIndex);
			entry.value = map.get(entry.key);
			nextIndex++;
			hasNext = nextIndex < map.size;
			return entry;
		}

		public void remove () {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			map.remove(entry.key);
			nextIndex--;
		}
	}

	static public class OrderedMapKeys<K> extends Keys<K> {
		private Array<K> keys;

		public OrderedMapKeys (MerryOrderedMap<K, ?> map) {
			super(map);
			keys = map.keys;
		}

		public void reset () {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public K next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			K key = keys.get(nextIndex);
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < map.size;
			return key;
		}

		public void remove () {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			((MerryOrderedMap)map).removeIndex(nextIndex - 1);
			nextIndex = currentIndex;
			currentIndex = -1;
		}

		public Array<K> toArray (Array<K> array) {
			array.addAll(keys, nextIndex, keys.size - nextIndex);
			nextIndex = keys.size;
			hasNext = false;
			return array;
		}

		public Array<K> toArray () {
			return toArray(new Array(true, keys.size - nextIndex));
		}
	}

	static public class OrderedMapValues<V> extends Values<V> {
		private Array keys;

		public OrderedMapValues (MerryOrderedMap<?, V> map) {
			super(map);
			keys = map.keys;
		}

		public void reset () {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public V next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			V value = map.get(keys.get(nextIndex));
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < map.size;
			return value;
		}

		public void remove () {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			((MerryOrderedMap)map).removeIndex(currentIndex);
			nextIndex = currentIndex;
			currentIndex = -1;
		}

		public Array<V> toArray (Array<V> array) {
			int n = keys.size;
			array.ensureCapacity(n - nextIndex);
			Object[] keys = this.keys.items;
			for (int i = nextIndex; i < n; i++)
				array.add(map.get(keys[i]));
			currentIndex = n - 1;
			nextIndex = n;
			hasNext = false;
			return array;
		}

		public Array<V> toArray () {
			return toArray(new Array(true, keys.size - nextIndex));
		}
	}
}
