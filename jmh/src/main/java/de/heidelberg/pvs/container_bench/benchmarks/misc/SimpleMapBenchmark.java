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
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
/*
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

public class SimpleMapBenchmark {

	/** To run from command line: $ mvn clean install exec:java -Dexec.args="-f 4 -wi 5 -i 3 -t 2 -w 2s -r 2s"
	 * <p>
	 * Fork 0 can be used for debugging/development, eg: -f 0 -wi 1 -i 1 -t 1 -w 1s -r 1s [benchmarkClassName] */
	static public void main (String[] args) throws Exception {
		if (args.length == 0) {
			String commandLine = "-f 3 -wi 3 -i 3 -t 1 -w 2s -r 2s " // For development only (fork 0, short runs).
					// + "-bs 2500000 ArrayBenchmark" //
					// + "-rf csv FieldSerializerBenchmark.field FieldSerializerBenchmark.tagged" //
					+ "SimpleMapBenchmark.read" //
					;
			System.out.println(commandLine);
			args = commandLine.split(" ");
		}
		Main.main(args);
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
		@Param({"kryo4", "kryo5", "hashmap"}) public MapType mapType;

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
		kryo4, kryo5, hashmap
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
}