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

/** An unordered map that uses identity comparison for keys. This implementation is a cuckoo hash map using 3 hashes, random
 * walking, and a small stash for problematic keys. Null keys are not allowed. Null values are allowed. No allocation is done
 * except when growing the table size. <br>
 * <br>
 * This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit slower,
 * depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have to rehash to the
 * next higher POT size.
 * @author Nathan Sweet */
public class MerryIdentityMap<K, V> extends MerryObjectMap<K, V> {

	/** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
	public MerryIdentityMap() {
		super();
	}
	/** Creates a new map with a load factor of 0.8.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
	public MerryIdentityMap(int initialCapacity) {
		super(initialCapacity);
	}

	/** Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
	public MerryIdentityMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	
	/** Creates a new map identical to the specified map. */
	public MerryIdentityMap(MerryIdentityMap<? extends K, ? extends V> map) {
		super(map);
	}

	@Override
	protected int place(K item) {
		return (int) (System.identityHashCode(item) * 0x9E3779B97F4A7C15L >>> shift);
		//return (System.identityHashCode(item) & mask);
	}

	@Override
	int locateKey(K key, int placement) {
		for (int i = placement; ; i = i + 1 & mask) {
			// empty space is available
			if (keyTable[i] == null) {
				return -1;
			}
			if (key == (keyTable[i])) {
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
}
