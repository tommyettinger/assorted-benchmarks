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

/** A {@link MerryObjectSet} that also stores keys in an {@link Array} using the insertion order.
 * {@link #iterator() Iteration} is ordered and faster than an unordered set. Keys can also be accessed and the order
 * changed using {@link #orderedItems()}. There is some additional overhead for put and remove. When used for faster
 * iteration versus ObjectSet and the order does not actually matter, copying during remove can be greatly reduced by
 * setting {@link Array#ordered} to false for {@link MerryOrderedSet#orderedItems()}.
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

public class MerryOrderedSet<T> extends MerryObjectSet<T> {
	final Array<T> items;
	MerryOrderedSetIterator iterator1, iterator2;

	public MerryOrderedSet() {
		items = new Array();
	}

	public MerryOrderedSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		items = new Array(keyTable.length);
	}

	public MerryOrderedSet(int initialCapacity) {
		super(initialCapacity);
		items = new Array(keyTable.length);
	}

	public MerryOrderedSet(MerryOrderedSet<? extends T> set) {
		super(set);
		items = new Array(keyTable.length);
		items.addAll(set.items);
	}

	public boolean add (T key) {
		if (!super.add(key)) return false;
		items.add(key);
		return true;
	}

	/** Sets the key at the specfied index. Returns true if the key was not already in the set. If this set already contains the
	 * key, the existing key's index is changed if needed and false is returned. */
	public boolean add (T key, int index) {
		if (!super.add(key)) {
			int oldIndex = items.indexOf(key, true);
			if (oldIndex != index) items.insert(index, items.removeIndex(oldIndex));
			return false;
		}
		items.insert(index, key);
		return true;
	}

	public boolean remove (T key) {
		if (!super.remove(key)) return false;
		items.removeValue(key, false);
		return true;
	}

	public T removeIndex (int index) {
		T key = items.removeIndex(index);
		super.remove(key);
		return key;
	}

	public void clear (int maximumCapacity) {
		items.clear();
		super.clear(maximumCapacity);
	}

	public void clear () {
		items.clear();
		super.clear();
	}

	public Array<T> orderedItems () {
		return items;
	}

	public MerryOrderedSetIterator<T> iterator () {
		if (Collections.allocateIterators) return new MerryOrderedSetIterator(this);
		if (iterator1 == null) {
			iterator1 = new MerryOrderedSetIterator(this);
			iterator2 = new MerryOrderedSetIterator(this);
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

	public String toString () {
		if (size == 0) return "{}";
		T[] items = this.items.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		buffer.append(items[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(items[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

	public String toString (String separator) {
		return items.toString(separator);
	}

	static public class MerryOrderedSetIterator<K> extends MerryObjectSetIterator<K> {
		private Array<K> items;

		public MerryOrderedSetIterator(MerryOrderedSet<K> set) {
			super(set);
			items = set.items;
		}

		public void reset () {
			nextIndex = 0;
			hasNext = set.size > 0;
		}

		public K next () {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
			K key = items.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < set.size;
			return key;
		}

		public void remove () {
			if (nextIndex < 0) throw new IllegalStateException("next must be called before remove.");
			nextIndex--;
			((MerryOrderedSet)set).removeIndex(nextIndex);
		}

		public Array<K> toArray (Array<K> array) {
			array.addAll(items, nextIndex, items.size - nextIndex);
			nextIndex = items.size;
			hasNext = false;
			return array;
		}

		public Array<K> toArray () {
			return toArray(new Array(true, set.size - nextIndex));
		}
	}
}
