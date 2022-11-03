/* Copyright (c) 2008-2018, Nathan Sweet
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package de.heidelberg.pvs.container_bench.benchmarks.misc;

import com.esotericsoftware.kryo.kryo5.util.CuckooObjectMap;
import com.esotericsoftware.kryo.kryo5.util.IdentityObjectIntMap;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
/* // testing the benchmark as-stated, except for not using ByteBuddy to generate Class instances (just Object instances here).
Benchmark                     (mapType)  (maxCapacity)  (numClasses)   Mode  Cnt        Score       Error  Units
SimpleMapBenchmark.read           kryo4           2048           100  thrpt    4   991343.823 ± 40029.877  ops/s
SimpleMapBenchmark.read           kryo4           2048          1000  thrpt    4   210078.480 ± 29718.267  ops/s
SimpleMapBenchmark.read           kryo4           2048          3000  thrpt    4   212688.955 ±  2799.347  ops/s
SimpleMapBenchmark.read           kryo4           2048         10000  thrpt    4   186540.938 ± 10643.092  ops/s
SimpleMapBenchmark.read           kryo5           2048           100  thrpt    4   895205.941 ± 10656.588  ops/s
SimpleMapBenchmark.read           kryo5           2048          1000  thrpt    4   147079.363 ± 14186.925  ops/s
SimpleMapBenchmark.read           kryo5           2048          3000  thrpt    4   127864.509 ± 17311.464  ops/s
SimpleMapBenchmark.read           kryo5           2048         10000  thrpt    4   115903.985 ±  9577.557  ops/s
SimpleMapBenchmark.read         hashmap           2048           100  thrpt    4  1003598.116 ± 34424.652  ops/s
SimpleMapBenchmark.read         hashmap           2048          1000  thrpt    4   144651.126 ±  5932.599  ops/s
SimpleMapBenchmark.read         hashmap           2048          3000  thrpt    4   145787.679 ±  5172.823  ops/s
SimpleMapBenchmark.read         hashmap           2048         10000  thrpt    4   141603.128 ±  3510.215  ops/s
SimpleMapBenchmark.write          kryo4           2048           100  thrpt    4   641825.958 ±  9874.740  ops/s
SimpleMapBenchmark.write          kryo4           2048          1000  thrpt    4    71371.876 ±  1511.552  ops/s
SimpleMapBenchmark.write          kryo4           2048          3000  thrpt    4    22861.240 ±  3235.824  ops/s
SimpleMapBenchmark.write          kryo4           2048         10000  thrpt    4     4930.084 ±   495.116  ops/s
SimpleMapBenchmark.write          kryo5           2048           100  thrpt    4   898350.455 ±  7738.068  ops/s
SimpleMapBenchmark.write          kryo5           2048          1000  thrpt    4   101059.869 ±  8172.741  ops/s
SimpleMapBenchmark.write          kryo5           2048          3000  thrpt    4    21717.251 ±  4586.349  ops/s
SimpleMapBenchmark.write          kryo5           2048         10000  thrpt    4     5981.632 ±   538.728  ops/s
SimpleMapBenchmark.write        hashmap           2048           100  thrpt    4   844486.838 ± 13813.770  ops/s
SimpleMapBenchmark.write        hashmap           2048          1000  thrpt    4    84556.968 ±  7540.261  ops/s
SimpleMapBenchmark.write        hashmap           2048          3000  thrpt    4    27629.763 ±  2393.057  ops/s
SimpleMapBenchmark.write        hashmap           2048         10000  thrpt    4     5488.110 ±   561.659  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048           100  thrpt    4   199493.267 ± 17306.566  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048          1000  thrpt    4    25245.163 ±  4222.463  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048          3000  thrpt    4     4821.514 ±   166.601  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048         10000  thrpt    4     1316.694 ±    15.794  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048           100  thrpt    4   221862.240 ± 34291.385  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048          1000  thrpt    4    26870.239 ±  1555.476  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048          3000  thrpt    4     7123.652 ±  1108.615  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048         10000  thrpt    4     1595.865 ±    64.167  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048           100  thrpt    4   305864.667 ± 20119.797  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048          1000  thrpt    4    25843.993 ±  4610.457  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048          3000  thrpt    4     8546.445 ±  1272.654  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048         10000  thrpt    4     2084.322 ±   346.303  ops/s
 */
 
/* // testing with "custom" that acts just like Kryo5's IdentityObjectIntMap but has a simpler place() method.
Benchmark                     (mapType)  (maxCapacity)  (numClasses)   Mode  Cnt        Score        Error  Units
SimpleMapBenchmark.read           kryo4           2048           100  thrpt    4   954142.774 ±  68435.049  ops/s
SimpleMapBenchmark.read           kryo4           2048          1000  thrpt    4   214180.374 ±  45604.081  ops/s
SimpleMapBenchmark.read           kryo4           2048          3000  thrpt    4   194853.003 ±  22154.523  ops/s
SimpleMapBenchmark.read           kryo4           2048         10000  thrpt    4   193660.331 ±  22441.117  ops/s
SimpleMapBenchmark.read           kryo5           2048           100  thrpt    4   743542.615 ±  76490.822  ops/s
SimpleMapBenchmark.read           kryo5           2048          1000  thrpt    4   191775.881 ±  11088.825  ops/s
SimpleMapBenchmark.read           kryo5           2048          3000  thrpt    4   174558.851 ±   9791.274  ops/s
SimpleMapBenchmark.read           kryo5           2048         10000  thrpt    4   177782.819 ±  76305.514  ops/s
SimpleMapBenchmark.read          custom           2048           100  thrpt    4   827768.176 ±  63289.895  ops/s
SimpleMapBenchmark.read          custom           2048          1000  thrpt    4   194554.040 ±  29039.417  ops/s
SimpleMapBenchmark.read          custom           2048          3000  thrpt    4   182388.289 ±  33658.718  ops/s
SimpleMapBenchmark.read          custom           2048         10000  thrpt    4   182975.076 ±  14010.889  ops/s
SimpleMapBenchmark.read         hashmap           2048           100  thrpt    4  1062217.661 ±  17081.241  ops/s
SimpleMapBenchmark.read         hashmap           2048          1000  thrpt    4   209171.880 ±  14824.787  ops/s
SimpleMapBenchmark.read         hashmap           2048          3000  thrpt    4   181105.026 ±  23764.953  ops/s
SimpleMapBenchmark.read         hashmap           2048         10000  thrpt    4   194113.563 ±  33979.994  ops/s
SimpleMapBenchmark.write          kryo4           2048           100  thrpt    4   926984.587 ±  70861.075  ops/s
SimpleMapBenchmark.write          kryo4           2048          1000  thrpt    4    82500.144 ±   8060.488  ops/s
SimpleMapBenchmark.write          kryo4           2048          3000  thrpt    4    25564.377 ±   6440.420  ops/s
SimpleMapBenchmark.write          kryo4           2048         10000  thrpt    4     5045.047 ±   1337.437  ops/s
SimpleMapBenchmark.write          kryo5           2048           100  thrpt    4   849485.806 ±  83740.002  ops/s
SimpleMapBenchmark.write          kryo5           2048          1000  thrpt    4   103063.733 ±  13031.252  ops/s
SimpleMapBenchmark.write          kryo5           2048          3000  thrpt    4    22453.524 ±   3256.723  ops/s
SimpleMapBenchmark.write          kryo5           2048         10000  thrpt    4     5756.729 ±    706.400  ops/s
SimpleMapBenchmark.write         custom           2048           100  thrpt    4  1007102.615 ± 123163.286  ops/s
SimpleMapBenchmark.write         custom           2048          1000  thrpt    4   104261.232 ±  18898.650  ops/s
SimpleMapBenchmark.write         custom           2048          3000  thrpt    4    22960.867 ±   2310.322  ops/s
SimpleMapBenchmark.write         custom           2048         10000  thrpt    4     6097.132 ±    896.405  ops/s
SimpleMapBenchmark.write        hashmap           2048           100  thrpt    4   888424.912 ±  60680.439  ops/s
SimpleMapBenchmark.write        hashmap           2048          1000  thrpt    4    87867.825 ±  13963.824  ops/s
SimpleMapBenchmark.write        hashmap           2048          3000  thrpt    4    25173.974 ±   6946.654  ops/s
SimpleMapBenchmark.write        hashmap           2048         10000  thrpt    4     5239.438 ±    400.350  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048           100  thrpt    4   184263.231 ±  17961.942  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048          1000  thrpt    4    24069.625 ±   3690.568  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048          3000  thrpt    4     4385.730 ±    200.461  ops/s
SimpleMapBenchmark.writeRead      kryo4           2048         10000  thrpt    4     1236.982 ±     72.957  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048           100  thrpt    4   209263.714 ±  27132.384  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048          1000  thrpt    4    25497.431 ±   2449.367  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048          3000  thrpt    4     7016.798 ±    538.767  ops/s
SimpleMapBenchmark.writeRead      kryo5           2048         10000  thrpt    4     1416.349 ±     77.229  ops/s
SimpleMapBenchmark.writeRead     custom           2048           100  thrpt    4   241397.521 ±  33966.247  ops/s
SimpleMapBenchmark.writeRead     custom           2048          1000  thrpt    4    26733.594 ±   3314.818  ops/s
SimpleMapBenchmark.writeRead     custom           2048          3000  thrpt    4     7597.491 ±   1998.471  ops/s
SimpleMapBenchmark.writeRead     custom           2048         10000  thrpt    4     1633.793 ±     64.714  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048           100  thrpt    4   276219.626 ±  29026.283  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048          1000  thrpt    4    24095.735 ±   4114.677  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048          3000  thrpt    4     8894.503 ±   1046.776  ops/s
SimpleMapBenchmark.writeRead    hashmap           2048         10000  thrpt    4     1951.340 ±    388.914  ops/s
 */

public class SimpleMapBenchmark {

	/** To run from command line: $ mvn clean install exec:java -Dexec.args="-f 4 -wi 5 -i 3 -t 2 -w 2s -r 2s"
	 * <p>
	 * Fork 0 can be used for debugging/development, eg: -f 0 -wi 1 -i 1 -t 1 -w 1s -r 1s [benchmarkClassName] */
	static public void main (String[] args) throws Exception {
		ObjectMap2Adapter<Object> adapter = new ObjectMap2Adapter<>(new IdentityIntMap2<>(), 2048);
		Object a = new Object();
		adapter.put(a, 1);
		Integer num = adapter.get(a);
		System.out.println(num);
//		if (args.length == 0) {
//			String commandLine = "-f 1 -wi 3 -i 3 -t 1 -w 4s -r 4s "
//					+ "SimpleMapBenchmark.read"
//					;
//			System.out.println(commandLine);
//			args = commandLine.split(" ");
//		}
//		Main.main(args);
	}

	@Benchmark
	public void read (ReadBenchmarkState state, Blackhole blackhole) {
		state.read(blackhole);
	}

	@Benchmark
	public void write (BenchmarkState state, Blackhole blackhole) {
		state.write(blackhole);
	}

	@Benchmark
	public void writeRead (BenchmarkState state, Blackhole blackhole) {
		state.readWrite(blackhole);
	}

	@State(Scope.Thread)
	public static class AbstractBenchmarkState {
		@Param({"100", "1000", "3000", "10000"}) public int numClasses;
		@Param({"2048"}) public int maxCapacity;
		@Param({"kryo4", "kryo5", "custom", "hashmap"}) public MapType mapType;

		MapAdapter<Object, Integer> map;
		List<Object> classes;
	}

	@State(Scope.Thread)
	public static class BenchmarkState extends AbstractBenchmarkState {

		@Setup(Level.Trial)
		public void setup () {
			map = createMap(mapType, maxCapacity);
			classes = IntStream.rangeClosed(0, numClasses).mapToObj(i -> new Object())
				.collect(Collectors.toList());
		}

		public void write (Blackhole blackhole) {
			classes.stream()
				.map(c -> map.put(c, 1))
				.forEach(blackhole::consume);
		}

		public void readWrite (Blackhole blackhole) {
			classes.forEach(c -> map.put(c, 1));
			Collections.shuffle(classes);

			final TangleRNG random = new TangleRNG();
			for (int i = 0; i < numClasses; i++) {
				final Object key = classes.get(random.nextInt(numClasses - 1));
				blackhole.consume(map.get(key));
			}

			map.clear();
		}
	}

	@State(Scope.Thread)
	public static class ReadBenchmarkState extends AbstractBenchmarkState {

		@Setup(Level.Trial)
		public void setup () {
			map = createMap(mapType, maxCapacity);
			classes = IntStream.rangeClosed(0, numClasses).mapToObj(i -> new Object())
				.collect(Collectors.toList());
			classes.forEach(c -> map.put(c, 1));
			Collections.shuffle(classes);
		}

		public void read (Blackhole blackhole) {
			classes.stream()
				.limit(500)
				.map(map::get)
				.forEach(blackhole::consume);
		}
	}

	public enum MapType {
		kryo4, kryo5, custom, hashmap
	}

	interface MapAdapter<K, V> {
		V get (K key);

		V put (K key, V value);

		void clear();
	}

	private static MapAdapter<Object, Integer> createMap (MapType mapType, int maxCapacity) {
		switch (mapType) {
			case kryo4:
				return new CuckooMapAdapter<>(new CuckooObjectMap<>(), maxCapacity);
			case kryo5:
				return new ObjectMapAdapter<>(new IdentityObjectIntMap<>(), maxCapacity);
			case custom:
				return new ObjectMap2Adapter<>(new IdentityIntMap2<>(), maxCapacity);
			case hashmap:
				return new HashMapAdapter<>(new IdentityHashMap<>());
			default:
				throw new IllegalStateException("Unexpected value: " + mapType);
		}
	}

	static class CuckooMapAdapter<K> implements MapAdapter<K, Integer> {
		private final CuckooObjectMap<K, Integer> delegate;
		private final int maxCapacity;

		public CuckooMapAdapter (CuckooObjectMap<K, Integer> delegate, int maxCapacity) {
			this.delegate = delegate;
			this.maxCapacity = maxCapacity;
		}

		@Override
		public Integer get (K key) {
			return delegate.get(key, -1);
		}

		@Override
		public Integer put (K key, Integer value) {
			delegate.put(key, value);
			return null;
		}

		@Override
		public void clear() {
			delegate.clear(maxCapacity);
		}

	}

	static class ObjectMapAdapter<K> implements MapAdapter<K, Integer> {
		private final IdentityObjectIntMap<K> delegate;
		private final int maxCapacity;

		public ObjectMapAdapter (IdentityObjectIntMap<K> delegate, int maxCapacity) {
			this.delegate = delegate;
			this.maxCapacity = maxCapacity;
		}

		@Override
		public Integer get (K key) {
			return delegate.get(key, -1);
		}

		@Override
		public Integer put (K key, Integer value) {
			delegate.put(key, value);
			return null;
		}

		@Override
		public void clear() {
			delegate.clear(maxCapacity);
		}
	}

	static class ObjectMap2Adapter<K> implements MapAdapter<K, Integer> {
		private final IdentityIntMap2<K> delegate;
		private final int maxCapacity;

		public ObjectMap2Adapter (IdentityIntMap2<K> delegate, int maxCapacity) {
			this.delegate = delegate;
			this.maxCapacity = maxCapacity;
		}

		@Override
		public Integer get (K key) {
			return delegate.get(key, -1);
		}

		@Override
		public Integer put (K key, Integer value) {
			delegate.put(key, value);
			return null;
		}

		@Override
		public void clear() {
			delegate.clear(maxCapacity);
		}
	}

	private static class HashMapAdapter<K> implements MapAdapter<K, Integer> {
		private final IdentityHashMap<K, Integer> delegate;

		public HashMapAdapter (IdentityHashMap<K, Integer> delegate) {
			this.delegate = delegate;
		}

		@Override
		public Integer get (K key) {
			return delegate.get(key);
		}

		@Override
		public Integer put (K key, Integer value) {
			return delegate.put(key, value);
		}

		@Override
		public void clear() {
			delegate.clear();
		}
	}

	public static class IdentityIntMap2<K> extends IdentityObjectIntMap<K> {
		/**
		 * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
		 */
		public IdentityIntMap2() {
			super();
		}

		/**
		 * Creates a new map with a load factor of 0.8.
		 *
		 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
		 */
		public IdentityIntMap2(int initialCapacity) {
			super(initialCapacity);
		}

		/**
		 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
		 * growing the backing table.
		 *
		 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
		 * @param loadFactor
		 */
		public IdentityIntMap2(int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
		}

		public IdentityIntMap2(IdentityObjectIntMap<K> map) {
			super(map);
		}

		@Override
		protected int place(K item) {
			return System.identityHashCode(item) & mask;
		}
	}
}