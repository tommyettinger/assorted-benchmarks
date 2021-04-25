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

import java.io.Serializable;
import java.util.Random;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * HarpoRandomBench score: 817981376.000000 (818.0M 2052.2%)
 *              uncertainty:   4.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * HarpoRandomBench score: 1353653248.000000 (1.354G 2102.6%)
 *              uncertainty:   2.6%
 * HotSpot Java 16:
 * <br>
 * HarpoRandomBench score: 1298374912.000000 (1.298G 2098.4%)
 *              uncertainty:   1.3%
 * <br>
 * This is one of the few times I have seen where HotSpot Java 8 is significantly slower than a later version, here, 16.
 * The performance on Java 8 is still good here. This is also the fastest generator currently tested here on a HotSpot
 * JVM, although only on Java 16.
 */
public final class HarpoRandomBench extends MicroBench {

	public static class HarpoRandom extends Random implements Serializable{
		private long stateA, stateB, stateC;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public HarpoRandom() {
			super();
			stateA = super.nextLong();
			stateB = super.nextLong();
			stateC = super.nextLong();
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
		 * @param seed the initial seed
		 * @see #setSeed(long)
		 */
		public HarpoRandom(long seed) {
			super(seed);
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = seed ^ 0x05CB93402A76F7DAL;
		}

		/**
		 * Sets the seed of this random number generator using a single
		 * {@code long} seed. The general contract of {@code setSeed} is
		 * that it alters the state of this random number generator object
		 * so as to be in exactly the same state as if it had just been
		 * created with the argument {@code seed} as a seed. The method
		 * {@code setSeed} is implemented by class {@code Random} by
		 * atomically updating the seed to
		 * <pre>{@code (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1)}</pre>
		 * and clearing the {@code haveNextNextGaussian} flag used by {@link
		 * #nextGaussian}.
		 *
		 * <p>The implementation of {@code setSeed} by class {@code Random}
		 * happens to use only 48 bits of the given seed. In general, however,
		 * an overriding method may use all 64 bits of the {@code long}
		 * argument as a seed value.
		 *
		 * @param seed the initial seed
		 */
		@Override
		public synchronized void setSeed(long seed) {
			super.setSeed(seed);
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = seed ^ 0x05CB93402A76F7DAL;
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
		protected int next(int bits) {
			final long a0 = stateA;
			final long b0 = stateB;
			final long c0 = stateC;
			stateA = b0 + ~c0;
			stateB = Long.rotateLeft(a0, 46) ^ c0;
			stateC = Long.rotateLeft(b0, 23) - a0;
			return (int) (a0 + b0) >>> 32 - bits;
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
			final long a0 = stateA;
			final long b0 = stateB;
			final long c0 = stateC;
			stateA = b0 + ~c0;
			stateB = Long.rotateLeft(a0, 46) ^ c0;
			stateC = Long.rotateLeft(b0, 23) - a0;
			return a0 + b0;
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		HarpoRandom rng = new HarpoRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}