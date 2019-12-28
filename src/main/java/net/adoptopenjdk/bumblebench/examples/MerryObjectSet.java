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
import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.OrderedSet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An unordered set where the keys are objects. This implementation uses Robin Hood Hashing with the backward-shift
 * algorithm for removal, and finds space for keys using Fibonacci hashing instead of the more-common power-of-two mask.
 * Null keys are not allowed. No allocation is done except when growing the table size.
 * <br>
 * See <a href="https://codecapsule.com/2013/11/11/robin-hood-hashing/">Emmanuel Goossaert's blog post</a> for more
 * information on Robin Hood hashing. It isn't state-of-the art in C++ or Rust any more, but newer techniques like Swiss
 * Tables aren't applicable to the JVM anyway. The name "Merry" was picked because Robin Hood has a band of Merry Men,
 * "Merry" is shorter to type than "RobinHood" and this was written around Christmas time.
 * <br>
 * This set performs very fast contains and remove (typically O(1), worst case O(log(n))). Add may be a bit slower, depending on
 * hash collisions. Load factors greater than 0.91 greatly increase the chances the set will have to rehash to the next higher POT
 * size.
 * <br>
 * Iteration can be very slow for a set with a large capacity. {@link #clear(int)} and {@link #shrink(int)} can be used to reduce
 * the capacity. {@link OrderedSet} provides much faster iteration.
 * <br>
 * Unlike ObjectSet, this doesn't have known cases where it tries to resize forever and exhausts the available heap.
 * It is much more robust in regards to hashCode() quality, and tolerates even very bad hashCode()s. Part of this is due
 * to the Fibonacci hashing used, but mostly it has to do with not using a stash for problematic keys when most or all
 * keys could be problematic (ObjectSet uses a stash, and ties its size to the capacity of the ObjectSet).
 * <br>
 * The <a href="http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/">backward-shift algorithm</a>
 * used during removal apparently is key to the good performance of this implementation. Thanks to Maksym Stepanenko,
 * who wrote a similar class that provided valuable insight into how Robin Hood hashing works in Java:
 * <a href="https://github.com/mstepan/algorithms/blob/master/src/main/java/com/max/algs/hashing/robin_hood/RobinHoodHashMap.java">Maksym's code is here</a>.
 * @author Tommy Ettinger
 * @author Nathan Sweet */
public class MerryObjectSet<T> implements Iterable<T> {
//	private static final int PRIME1 = 0x15BA25;
//	private static final int PRIME2 = 0x13C6EF;

	public int size;

	T[] keyTable;
	/**
	 * Initial Bucket positions.
	 */
	private int[] ib;

	private float loadFactor;
	private int mask, threshold, shift;

	private MerryObjectSetIterator iterator1, iterator2;

	/** Creates a new set with an initial capacity of 51 and a load factor of 0.8. */
	public MerryObjectSet() {
		this(51, 0.8f);
	}

	/** Creates a new set with a load factor of 0.8.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
	public MerryObjectSet(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	/** Creates a new set with the specified initial capacity and load factor. This set will hold initialCapacity items before
	 * growing the backing table.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
	public MerryObjectSet(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		if (loadFactor <= 0f || loadFactor >= 1f) throw new IllegalArgumentException("loadFactor must be > 0 and < 1: " + loadFactor);
		initialCapacity = MathUtils.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
		if (initialCapacity > 1 << 30) throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
		
		this.loadFactor = loadFactor;

		threshold = (int)(initialCapacity * loadFactor);
		mask = initialCapacity - 1;
		shift = Long.numberOfLeadingZeros(mask);
		keyTable = (T[])(new Object[initialCapacity]);
		ib = new int[initialCapacity];
	}

	/** Creates a new set identical to the specified set. */
	public MerryObjectSet(MerryObjectSet<? extends T> set) {
		this((int) Math.ceil(set.ib.length * set.loadFactor), set.loadFactor);
		System.arraycopy(set.keyTable, 0, keyTable, 0, set.keyTable.length);
		System.arraycopy(set.ib, 0, ib, 0, set.ib.length);
		size = set.size;
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

	private int locateKey(T key) {

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

	private int locateKey(T key, int bucket) {
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


	/** Returns true if the key was not already in the set. If this set already contains the key, the call leaves the set unchanged
	 * and returns false. */
	public boolean add (T key) {
		if (key == null) throw new IllegalArgumentException("key cannot be null.");
		T[] keyTable = this.keyTable;
		int[] ib = this.ib;
		int b = bucket(key.hashCode());
		// an identical key already exists
		if (locateKey(key, b) != -1) {
			return false;
		}
		for (int i = b; ; i = (i + 1) & mask) {
			// space is available so we insert and break (resize is later)
			if (keyTable[i] == null) {
				keyTable[i] = key;
				ib[i] = b;
				break;
			}
			// if there is a key with a lower probe distance, we swap with it
			// and keep going until we find a place we can insert
			else if (bucketDistance(ib[i], i) < bucketDistance(b, i)) {
				T temp = keyTable[i];
				int tb = ib[i];
				keyTable[i] = key;
				ib[i] = b;
				key = temp;
				b = tb;
			}
		}
		if (++size >= threshold) {
			resize(ib.length << 1);
		}
		return true;
	}

	public void addAll (Array<? extends T> array) {
		addAll(array.items, 0, array.size);
	}

	public void addAll (Array<? extends T> array, int offset, int length) {
		if (offset + length > array.size)
			throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
		addAll((T[])array.items, offset, length);
	}

	public void addAll (T... array) {
		addAll(array, 0, array.length);
	}

	public void addAll (T[] array, int offset, int length) {
		ensureCapacity(length);
		for (int i = offset, n = i + length; i < n; i++)
			add(array[i]);
	}

	public void addAll (MerryObjectSet<T> set) {
		ensureCapacity(set.size);
		for (T key : set)
			add(key);
	}

	/** Skips checks for existing keys. */
	private void addResize (T key) {
		T[] keyTable = this.keyTable;
		int[] ib = this.ib;
		int b = bucket(key.hashCode());
		for (int i = b; ; i = (i + 1) & mask) {
			// space is available so we insert and break (resize is later)
			if (keyTable[i] == null) {
				keyTable[i] = key;
				ib[i] = b;
				break;
			}
			// if there is a key with a lower probe distance, we swap with it
			// and keep going until we find a place we can insert
			else if (bucketDistance(ib[i], i) < bucketDistance(b, i)) {
				T temp = keyTable[i];
				int tb = ib[i];
				keyTable[i] = key;
				ib[i] = b;
				key = temp;
				b = tb;
			}
		}
		if (++size >= threshold) {
			resize(ib.length << 1);
		}
	}

	/** Returns true if the key was removed. */
	public boolean remove(T key) {
		int loc = locateKey(key);
		if (loc == -1) {
			return false;
		}
		T[] keyTable = this.keyTable;
		keyTable[loc] = null;
		for (int i = (loc + 1) & mask; (keyTable[i] != null && bucketDistance(ib[loc], i) != 0); i = (i + 1) & mask) {
			keyTable[i - 1 & mask] = keyTable[i];
			ib[i - 1 & mask] = ib[i];
			keyTable[i] = null;
			ib[i] = 0;
		}
		--size;
		return true;
	}
	
	/** Returns true if the set has one or more items. */
	public boolean notEmpty () {
		return size > 0;
	}

	/** Returns true if the set is empty. */
	public boolean isEmpty () {
		return size == 0;
	}

	/** Reduces the size of the backing arrays to be the specified capacity or less. If the capacity is already less, nothing is
	 * done. If the set contains more items than the specified capacity, the next highest power of two capacity is used instead. */
	public void shrink (int maximumCapacity) {
		if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
		if (size > maximumCapacity) maximumCapacity = size;
		if (ib.length <= maximumCapacity) return;
		maximumCapacity = MathUtils.nextPowerOfTwo(maximumCapacity);
		resize(maximumCapacity);
	}

	/** Clears the set and reduces the size of the backing arrays to be the specified capacity, if they are larger. The reduction
	 * is done by allocating new arrays, though for large arrays this can be faster than clearing the existing array. */
	public void clear (int maximumCapacity) {
		if (ib.length <= maximumCapacity) {
			clear();
			return;
		}
		size = 0;
		resize(maximumCapacity);
	}

	/** Clears the set, leaving the backing arrays at the current capacity. When the capacity is high and the population is low,
	 * iteration can be unnecessarily slow. {@link #clear(int)} can be used to reduce the capacity. */
	public void clear () {
		if (size == 0) return;
		T[] keyTable = this.keyTable;
		for (int i = keyTable.length - 1; i > 0;)
		{
			keyTable[--i] = null;
			ib[i] = 0;
		}
		size = 0;
	}

	public boolean contains (T key) {
		return locateKey(key) != -1;
	}

	/** @return May be null. */
	public T get (T key) {
		final int loc = locateKey(key);
		return loc == -1 ? null : keyTable[loc];
	}

	public T first () {
		T[] keyTable = this.keyTable;
		for (int i = 0, n = ib.length; i < n; i++)
			if (keyTable[i] != null) return keyTable[i];
		throw new IllegalStateException("MerryObjectSet is empty.");
	}

	/** Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes. */
	public void ensureCapacity (int additionalCapacity) {
		if (additionalCapacity < 0) throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= threshold) resize(MathUtils.nextPowerOfTwo((int) Math.ceil(sizeNeeded / loadFactor)));
	}

	private void resize (int newSize) {
		int oldCapacity = ib.length;
		threshold = (int)(newSize * loadFactor);
		mask = newSize - 1;
		shift = Long.numberOfLeadingZeros(mask);
		T[] oldKeyTable = keyTable;

		keyTable = (T[])(new Object[newSize]);
		ib = new int[newSize];

		int oldSize = size;
		size = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldCapacity; i++) {
				T key = oldKeyTable[i];
				if (key != null) addResize(key);
			}
		}
	}
	
	public int hashCode () {
		int h = 0;
		for (int i = 0, n = ib.length; i < n; i++)
			if (keyTable[i] != null) {
				h += keyTable[i].hashCode();
			}
		return h;
	}

	public boolean equals (Object obj) {
		if (!(obj instanceof MerryObjectSet)) return false;
		MerryObjectSet other = (MerryObjectSet)obj;
		if (other.size != size) return false;
		T[] keyTable = this.keyTable;
		for (int i = 0, n = keyTable.length; i < n; i++)
			if (keyTable[i] != null && !other.contains(keyTable[i])) return false;
		return true;
	}

	public String toString () {
		return '{' + toString(", ") + '}';
	}

	public String toString (String separator) {
		if (size == 0) return "";
		StringBuilder buffer = new StringBuilder(32);
		T[] keyTable = this.keyTable;
		int i = keyTable.length;
		while (i-- > 0) {
			T key = keyTable[i];
			if (key == null) continue;
			buffer.append(key);
			break;
		}
		while (i-- > 0) {
			T key = keyTable[i];
			if (key == null) continue;
			buffer.append(separator);
			buffer.append(key);
		}
		return buffer.toString();
	}

	/** Returns an iterator for the keys in the set. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called. Use the
	 * {@link MerryObjectSetIterator} constructor for nested or multithreaded iteration. */
	public MerryObjectSetIterator<T> iterator () {
		if (Collections.allocateIterators) return new MerryObjectSetIterator(this);
		if (iterator1 == null) {
			iterator1 = new MerryObjectSetIterator(this);
			iterator2 = new MerryObjectSetIterator(this);
		}
		if (!iterator1.valid) {
			iterator1.reset();
			iterator1.valid = true;
			iterator2.valid = false;
			return iterator1;
		}
		iterator2.reset();
		iterator2.valid = true;
		iterator1.valid = false;
		return iterator2;
	}

	static public <T> MerryObjectSet<T> with (T... array) {
		MerryObjectSet<T> set = new MerryObjectSet<T>();
		set.addAll(array);
		return set;
	}

	static public class MerryObjectSetIterator<K> implements Iterable<K>, Iterator<K> {
		public boolean hasNext;

		final MerryObjectSet<K> set;
		int nextIndex, currentIndex;
		boolean valid = true;

		public MerryObjectSetIterator (MerryObjectSet<K> set) {
			this.set = set;
			reset();
		}

		public void reset () {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
		}

		private void findNextIndex () {
			hasNext = false;
			K[] keyTable = set.keyTable;
			for (int n = set.ib.length; ++nextIndex < n;) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove () {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			
			set.keyTable[currentIndex] = null;
			set.ib[currentIndex] = 0;
			currentIndex = -1;
			set.size--;
		}

		public boolean hasNext () {
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public K next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			K key = set.keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		public MerryObjectSetIterator<K> iterator () {
			return this;
		}

		/** Adds the remaining values to the array. */
		public Array<K> toArray (Array<K> array) {
			while (hasNext)
				array.add(next());
			return array;
		}

		/** Returns a new array containing the remaining values. */
		public Array<K> toArray () {
			return toArray(new Array<K>(true, set.size));
		}
	}
}
