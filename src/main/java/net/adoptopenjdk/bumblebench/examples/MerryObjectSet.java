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

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An unordered set where the keys are objects. This implementation uses Robin Hood Hashing with the backward-shift
 * algorithm for removal, and finds space for keys using Fibonacci hashing instead of the more-common power-of-two mask.
 * Null keys are not allowed. No allocation is done except when growing the table size.
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
 * the capacity. {@link MerryOrderedSet} provides much faster iteration.
 * <br>
 * The <a href="http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/">backward-shift algorithm</a>
 * used during removal apparently is key to the good performance of this implementation. Thanks to Maksym Stepanenko,
 * who wrote a similar class that provided valuable insight into how Robin Hood hashing works in Java:
 * <a href="https://github.com/mstepan/algorithms/blob/master/src/main/java/com/max/algs/hashing/robin_hood/RobinHoodHashMap.java">Maksym's code is here</a>.
 * @author Tommy Ettinger
 * @author Nathan Sweet */
public class MerryObjectSet<T> implements Iterable<T> {
	public int size;

	T[] keyTable;
	/**
	 * Initial Bucket positions.
	 */
	int[] ib;

	float loadFactor;
	int threshold;
	/**
	 * Used by {@link #place(Object)} to bit-shift the upper bits of a {@code long} into a usable range (less than or
	 * equal to {@link #mask}, greater than or equal to 0). If you're setting it in a subclass, this shift can be
	 * negative, which is a convenient way to match the number of bits in mask; if mask is a 7-bit number, then a shift
	 * of -7 will correctly shift the upper 7 bits into the lowest 7 positions. If using what this class sets, shift
	 * will be greater than 32 and less than 64; if you use this shift with an int, it will still correctly move the
	 * upper bits of an int to the lower bits, thanks to Java's implicit modulus on shifts.
	 * <br>
	 * You can also use {@link #mask} to mask the low bits of a number, which may be faster for some hashCode()s, if you
	 * reimplement {@link #place(Object)}.
	 */
	protected int shift;
	/**
	 * The bitmask used to contain hashCode()s to the indices that can be fit into the key array this uses. This should
	 * always be all-1-bits in its low positions; that is, it must be a power of two minus 1. If you subclass and change
	 * {@link #place(Object)}, you may want to use this instead of {@link #shift} to isolate usable bits of a hash.
	 */
	protected int mask;

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

	/**
	 * Finds an array index between 0 and {@link #mask}, both inclusive, corresponding to the hash code of {@code item}.
	 * By default, this uses "Fibonacci Hashing" on the {@link Object#hashCode()} of {@code item}; this multiplies
	 * {@code item.hashCode()} by a long constant (2 to the 64, divided by the golden ratio) and shifts the high-quality
	 * uppermost bits into the lowest positions so they can be used as array indices. The multiplication by a long may
	 * be somewhat slow on GWT, but it will be correct across all platforms and won't lose precision. Using Fibonacci
	 * Hashing allows even very poor hashCode() implementations, such as those that only differ in their upper bits, to
	 * work in a hash table without heavy collision rates. It has known problems when all or most hashCode()s are
	 * multiples of larger Fibonacci numbers; see <a href="https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">this blog post by Malte Skarupke</a>
	 * for more details. In the unlikely event that most of your hashCode()s are Fibonacci numbers, you can subclass
	 * this to change this method, which is a one-liner in this form:
	 * {@code return (int) (item.hashCode() * 0x9E3779B97F4A7C15L >>> shift);}
	 * <br>
	 * This can be overridden by subclasses, which you may want to do if your key type needs special consideration for
	 * its hash (such as if you use arrays as keys, which still requires that the arrays are not modified). Subclasses
	 * that don't need the collision decrease of Fibonacci Hashing (assuming the key class has a good hashCode()) may do
	 * fine with a simple implementation:
	 * {@code return (item.hashCode() & mask);}
	 * @param item a key that this method will hash, by default by calling {@link Object#hashCode()} on it; non-null
	 * @return an int between 0 and {@link #mask}, both inclusive
	 */
	protected int place(final T item) {
		// shift is always greater than 32, less than 64
		return (int) (item.hashCode() * 0x9E3779B97F4A7C15L >>> shift);
	}

	private int locateKey(final T key) {

		return locateKey(key, place(key));
	}

	/**
	 * Given a key and its initial placement to try in an array, this finds the actual location of the key in the array
	 * if it is present, or -1 if the key is not present. This can be overridden if a subclass needs to compare for
	 * equality differently than just by calling {@link Object#equals(Object)}, but only within the same package.
	 * @param key a K key that will be checked for equality if a similar-seeming key is found
	 * @param placement as calculated by {@link #place(Object)}, almost always with {@code place(key)}
	 * @return the location in the key array of key, if found, or -1 if it was not found.
	 */
	int locateKey(final T key, final int placement) {
		for (int i = placement; ; i = i + 1 & mask) {
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
			if ((i - ib[i] & mask) < (i - placement & mask)) {
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
		int b = place(key);
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
			else if ((i - ib[i] & mask) < (i - b & mask)) {
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
	
	public boolean addAll (T... array) {
		return addAll(array, 0, array.length);
	}

	public boolean addAll (T[] array, int offset, int length) {
		ensureCapacity(length);
		int oldSize = size;
		for (int i = offset, n = i + length; i < n; i++)
			add(array[i]);
		return oldSize != size;
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
		int b = place(key);
		for (int i = b; ; i = (i + 1) & mask) {
			// space is available so we insert and break (resize is later)
			if (keyTable[i] == null) {
				keyTable[i] = key;
				ib[i] = b;
				break;
			}
			// if there is a key with a lower probe distance, we swap with it
			// and keep going until we find a place we can insert
			else if ((i - ib[i] & mask) < (i - b & mask)) {
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
		for (int i = (loc + 1) & mask; (keyTable[i] != null && (i - ib[loc] & mask) != 0); i = (i + 1) & mask) {
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
