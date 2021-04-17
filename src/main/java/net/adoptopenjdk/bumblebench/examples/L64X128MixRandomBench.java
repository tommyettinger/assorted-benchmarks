/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

package net.adoptopenjdk.bumblebench.examples;

import net.adoptopenjdk.bumblebench.core.MicroBench;
import squidpony.squidmath.RandomnessSource;

import java.io.Serializable;

/**
 * This uses pre-release code from OpenJDK 17, which is scheduled to be released in late 2021.
 * This is one of many random number generators added in JEP 356 (<a href="https://github.com/openjdk/jdk/pull/1292">
 * code here</a>), which claims:
 * <br>
 * ...a new class of splittable PRNG algorithms (LXM) has also been discovered that are almost as fast, even easier
 * to implement...
 * <br>
 * So let's put that to the test.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * L64X128MixRandomBench score: 479769280.000000 (479.8M 1998.9%)
 *                   uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * L64X128MixRandomBench score: 840015872.000000 (840.0M 2054.9%)
 *                   uncertainty:   1.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * L64X128MixRandomBench score: 413480256.000000 (413.5M 1984.0%)
 *                   uncertainty:   3.1%
 * <br>
 * So, this particular LXM generator is 53% slower than SplittableRandom when both are run on Hotspot Java 8, which
 * apparently is considered almost as fast.
 * <br>
 * ... I give this 53% less than an A+. Its failing grade is almost an A.
 * <br>
 * Are we seriously in a mathematical field of study, or is this the advertising division of Oracle? It's possible JDK
 * 17 will have some optimizations for the new random number generators, but given that in general, the benchmarks I run
 * show a performance degradation when going from HotSpot JDK 8 to any HotSpot JDK after 8, I have my doubts. Why on
 * Earth isn't AES-NI being considered? The dragontamer version of aesrand is incredibly fast if native code can be
 * used, and if this is being added to the JDK then that is an option.
 */
public final class L64X128MixRandomBench extends MicroBench {

	public static class L64X128MixRandom implements Serializable, RandomnessSource {

		private final long a;
		private long s, x0, x1;

		/*
		 * Multiplier used in the LCG portion of the algorithm.
		 * Chosen based on research by Sebastiano Vigna and Guy Steele (2019).
		 * The spectral scores for dimensions 2 through 8 for the multiplier 0xd1342543de82ef95
		 * are [0.958602, 0.937479, 0.870757, 0.822326, 0.820405, 0.813065, 0.760215].
		 */
		private static final long M = 0xd1342543de82ef95L;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public L64X128MixRandom() {
			this((long) ((Math.random() - 0.5) * 0x10000000000000L)
							^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L),
					(long) ((Math.random() - 0.5) * 0x10000000000000L)
							^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L),
					(long) ((Math.random() - 0.5) * 0x10000000000000L)
							^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L),
					(long) ((Math.random() - 0.5) * 0x10000000000000L)
							^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L));
		}

		/**
		 * Creates a new random number generator using a single {@code long} seed.
		 * The seed is the initial value of the internal state of the pseudorandom
		 * number generator which is maintained by method {@link #next}.
		 *
		 * <p>The invocation {@code new Random(seed)} is equivalent to:
		 * <pre> {@code
		 * Random rnd = new Random();
		 * rnd.setSeed(seed);}</pre>
		 *
		 * @param seedA the initial seed
		 * @param seedB the initial seed
		 * @see #setSeed(long)
		 */
		public L64X128MixRandom(long a, long s, long seedA, long seedB) {
			this.a = a | 1L;
			this.s = s;
			x0 = seedA;
			if((seedA | seedB) == 0L)
				x1 = M;
			else
				x1 = seedB;

		}

		/**
		 * Sets the seed of this random number generator using a single
		 * {@code long} seed. The general contract of {@code setSeed} is
		 * that it alters the state of this random number generator object
		 * so as to be in exactly the same state as if it had just been
		 * created with the argument {@code seed} as a seed.
		 *
		 * <p>The implementation of {@code setSeed} by class {@code Random}
		 * happens to use only 48 bits of the given seed. In general, however,
		 * an overriding method may use all 64 bits of the {@code long}
		 * argument as a seed value.
		 *
		 * @param seed the initial seed
		 */
		public void setSeed(long seed) {
			s = seed;
			x0 = seed;
			if(seed == 0)
				x1 = M;
			else
				x1 = seed;
		}

		/**
		 * Generates the next pseudorandom number. Subclasses should
		 * override this, as this is used by all other methods.
		 *
		 * <p>The general contract of {@code next} is that it returns an
		 * {@code int} value and if the argument {@code bits} is between
		 * {@code 1} and {@code 32} (inclusive), then that many low-order
		 * bits of the returned value will be (approximately) independently
		 * chosen bit values, each of which is (approximately) equally
		 * likely to be {@code 0} or {@code 1}. The method {@code next} is
		 * implemented by class {@code Random} by atomically updating the seed to
		 * <pre>{@code (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1)}</pre>
		 * and returning
		 * <pre>{@code (int)(seed >>> (48 - bits))}.</pre>
		 * <p>
		 * This is a linear congruential pseudorandom number generator, as
		 * defined by D. H. Lehmer and described by Donald E. Knuth in
		 * <i>The Art of Computer Programming,</i> Volume 2:
		 * <i>Seminumerical Algorithms</i>, section 3.2.1.
		 *
		 * @param bits random bits
		 * @return the next pseudorandom value from this random number
		 * generator's sequence
		 * @since 1.1
		 */
		@Override
		public int next(int bits) {
			return (int)(nextLong()) >>> 32 - bits;
		}

		private static long mixLea64(long z) {
			z = (z ^ (z >>> 32)) * 0xdaba0b6eb09322e3L;
			z = (z ^ (z >>> 32)) * 0xdaba0b6eb09322e3L;
			return z ^ (z >>> 32);
		}

		/**
		 * Returns the next pseudorandom, uniformly distributed {@code long}
		 * value from this random number generator's sequence. The general
		 * contract of {@code nextLong} is that one {@code long} value is
		 * pseudorandomly generated and returned.
		 *
		 * @return the next pseudorandom, uniformly distributed {@code long}
		 * value from this random number generator's sequence
		 */
		@Override
		public long nextLong() {
			// Compute the result based on current state information
			// (this allows the computation to be overlapped with state update).
			final long result = mixLea64(s + x0);

			// Update the LCG subgenerator
			s = M * s + a;

			// Update the Xorshift subgenerator
			long q0 = x0, q1 = x1;
			{   // xoroshiro128v1_0
				q1 ^= q0;
				q0 = Long.rotateLeft(q0, 24);
				q0 = q0 ^ q1 ^ (q1 << 16);
				q1 = Long.rotateLeft(q1, 37);
			}
			x0 = q0; x1 = q1;

			return result;		}

		/**
		 * Produces a copy of this RandomnessSource that, if next() and/or nextLong() are called on this object and the
		 * copy, both will generate the same sequence of random numbers from the point copy() was called. This just needs to
		 * copy the state so it isn't shared, usually, and produce a new value with the same exact state.
		 *
		 * @return a copy of this RandomnessSource
		 */
		@Override
		public L64X128MixRandom copy() {
			return new L64X128MixRandom(a, s, x0, x1);
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		L64X128MixRandom rng = new L64X128MixRandom(1, 0, 0x12345678, 0x87654321);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}