package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectObjectMap;

import java.util.Map;

/**
 * Created by Tommy Ettinger on 9/16/2020.
 */
public class ObjectMapMulXor<K, V> extends ObjectObjectMap<K, V> {
	/**
	 * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
	 */
	public ObjectMapMulXor() {
		super();
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public ObjectMapMulXor(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 * @param loadFactor
	 */
	public ObjectMapMulXor(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map
	 */
	public ObjectMapMulXor(ObjectObjectMap<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Creates a new map identical to the specified map.
	 *
	 * @param map
	 */
	public ObjectMapMulXor(Map<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Returns an index &gt;= 0 and &lt;= {@link #mask} for the specified {@code item}.
	 * <p>
	 * The default implementation uses Fibonacci hashing on the item's {@link Object#hashCode()}: the hashcode is multiplied by a
	 * long constant (2 to the 64th, divided by the golden ratio) then the uppermost bits are shifted into the lowest positions to
	 * obtain an index in the desired range. Multiplication by a long may be slower than int (eg on GWT) but greatly improves
	 * rehashing, allowing even very poor hashcodes, such as those that only differ in their upper bits, to be used without high
	 * collision rates. Fibonacci hashing has increased collision rates when all or most hashcodes are multiples of larger
	 * Fibonacci numbers (see <a href=
	 * "https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte
	 * Skarupke's blog post</a>).
	 * <p>
	 * This method can be overridden to customize hashing. This may be useful, e.g. in the unlikely event that most hashcodes are
	 * Fibonacci numbers, if keys provide poor or incorrect hashcodes, or to simplify hashing. If keys provide high quality
	 * hashcodes and don't need Fibonacci hashing, a good implementation is: {@code return item.hashCode() & mask;}
	 *
	 * @param item a non-null Object; its hashCode() method should be used by most implementations.
	 */
	@Override
	protected int place(Object item) {
		return (item.hashCode() * 0x9E377 ^ 0x7F4A7C15) * 0x9E377 >>> shift;
	}
	//0x9E3779B97F4A7C15 0x9B97F4A7 0x7F4A7C15
}
