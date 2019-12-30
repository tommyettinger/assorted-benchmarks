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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.NoSuchElementException;

/** A {@link MerryObjectMap} that also stores keys in an {@link Array} using the insertion order. Iteration over the
 * {@link #entries()}, {@link #keys()}, and {@link #values()} is ordered and faster than an unordered map. Keys can also
 * be accessed and the order changed using {@link #orderedKeys()}. There is some additional overhead for put and remove.
 * When used for faster iteration versus MerryObjectMap and the order does not actually matter, copying during remove
 * can be greatly reduced by setting {@link Array#ordered} to false for {@link MerryOrderedMap#orderedKeys()}.
 * <br>
 * See <a href="https://codecapsule.com/2013/11/11/robin-hood-hashing/">Emmanuel Goossaert's blog post</a> for more
 * information on Robin Hood hashing. It isn't state-of-the art in C++ or Rust any more, but newer techniques like Swiss
 * Tables aren't applicable to the JVM anyway, and Robin Hood hashing works well here.
 * <br>
 * See <a href="https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte Skarupke's blog post</a>
 * for more information on Fibonacci hashing. In the specific case of this data structure, Fibonacci hashing improves
 * protection against what are normally very bad hashCode() implementations. Generally speaking, most automatically
 * generated hashCode() implementations range from mediocre to very bad, and because library data structures can't
 * expect every hashCode() to be high-quality, it is the responsibility of the data structure to have some measure of
 * safeguard in case of frequent collisions. The JDK's HashMap class has a complex set of conditions to change how it
 * operates to counteract malicious insertions performed to deny service; this works very well unless the hashCode()
 * of keys is intentionally broken. This class uses a simpler approach. Some main approaches to using hash codes to
 * place keys in an array include:
 * <ul>
 *     <li>Prime Modulus: use a prime number for array capacity, and use modulus to wrap hashCode() into the table size</li>
 *     <li>Bitmask: use a power of two for array capacity, and get only the least significant bits of a hashCode() up
 *         until the area used is equal to capacity.</li>
 * </ul>
 * The first approach is robust, but quite slow due to modulus being needed sometimes several times per operation, and
 * modulus is one of the slowest numerical operations on ints. The second approach is widespread among fast hash
 * tables, but either requires the least significant bits to be varied between hashCode() results (the most significant
 * bits usually don't matter much), or for collisions to have some kind of extra position to place keys. The first
 * requirement is a no-go with most automatically generated hashCode()s; if a field is a float, they have to convert it
 * to a usable int via {@link Float#floatToRawIntBits(float)}, and in many cases only the most significant bits will
 * change between the results of those calls. The second is usually done by probing, where another position in the
 * array is checked to see if it's available, then another and so on until an available space is found. The second
 * requirement can also sometimes be achieved with a "stash," which stores a list of problematic keys next to the rest
 * of the keys, but if the stash gets too large, most operations on the set or map get very slow, and if the stash size
 * depends on the key array's size, then too many items going in the stash can force massive memory use. ObjectSet and
 * ObjectMap in libGDX have this last problem, and can run out of memory if their keys have poor hashCode()s.
 * <br>
 * This class does things differently, though it also uses a power of two for array capacity. Fibonacci hashing
 * takes a key's hashCode(), multiplies it by a specific long constant, and bitwise-shifts just enough of the most
 * significant bits of that multiplication down to the least significant area, where they are used as an index into the
 * key array. The constant has to be ((2 to the 64) divided by the golden ratio) to work effectively here, due to
 * properties of the golden ratio, and multiplying by that makes all of the bits of a 32-bit hashCode() contribute some
 * chance of changing the upper 32 bits of the multiplied product. What this means in practice, is that inserting
 * Vector2 items with just two float values (and mostly the upper bits changing in the hashCode()) goes from 11,279
 * items per second with the above Bitmask method to 2,594,801 items per second with Fibonacci hashing, <b>a 230x
 * speedup</b>. With some specific ranges of Vector2, you can crash ObjectSet with an OutOfMemoryError by inserting as
 * little as 7,040 Vector2 items, so this is a significant improvement!
 * <br>
 * In addition to Fibonacci hashing to figure out initial placement in the key array, this uses Robin Hood hashing to
 * mitigate problems from collisions. The ObjectMap and ObjectSet classes in libGDX use Cuckoo hashing with a stash, but
 * no probing at all. This implementation probes often (though Fibonacci hashing helps) and uses linear probing (which
 * just probes the next item in the array sequentially until it finds an empty space), but can swap the locations of
 * keys. The idea here is that if a key requires particularly lengthy probes while you insert it, and it probes past a
 * key that has a lower probe length, it swaps their positions to reduce the maximum probe length (which helps other
 * operations too). This swapping behavior acts like "stealing from the rich" (keys with low probe lengths) to "give to
 * the poor" (keys with unusually long probe lengths), hence the Robin Hood title.
 * <br>
 * The name "Merry" was picked because Robin Hood has a band of Merry Men, "Merry" is faster to type than "RobinHood"
 * and this was written around Christmas time.
 * <br>
 * This set performs very fast contains and remove (typically O(1), worst case O(log(n))). Add may be a bit slower, depending on
 * hash collisions. Load factors greater than 0.91 greatly increase the chances the set will have to rehash to the next higher POT
 * size.
 * <br>
 * Iteration can be very slow for a set with a large capacity. {@link #clear(int)} and {@link #shrink(int)} can be used to reduce
 * the capacity. {@link MerryOrderedMap} provides much faster iteration.
 * <br>
 * The <a href="http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/">backward-shift algorithm</a>
 * used during removal apparently is key to the good performance of this implementation. Thanks to Maksym Stepanenko,
 * who wrote a similar class that provided valuable insight into how Robin Hood hashing works in Java:
 * <a href="https://github.com/mstepan/algorithms/blob/master/src/main/java/com/max/algs/hashing/robin_hood/RobinHoodHashMap.java">Maksym's code is here</a>.
 * @author Tommy Ettinger
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
