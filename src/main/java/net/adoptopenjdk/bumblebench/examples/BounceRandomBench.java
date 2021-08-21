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
 *
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16:
 * <br>
 *
 */
public final class BounceRandomBench extends MicroBench {

	public static class BounceRandom extends Random implements Serializable{
		private long stateA, stateB;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public BounceRandom() {
			super();
			stateA = super.nextLong();
			if(stateA == 0) stateA = -1L;
			stateB = super.nextLong();
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
		public BounceRandom(long seed) {
			super(seed);
			stateA = seed == 0L ? -1L : seed;
			stateB = seed;
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
			stateA = seed == 0L ? -1L : seed;
			stateB = seed;
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
			long x = (stateB += 0x9E3779B97F4A7C15L);
			long a = stateA;
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			a ^= a >>> 7;
			x ^= x >>> 33;
			x *= ((stateA = a ^ a << 9) | 1L);
			return (int)(x ^ x >>> 27) >>> 32 - bits;
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
//			final long s = (stateA = (stateA >>> 1 ^ (-(stateA & 1L) & 0xD800000000000000L)));
			final long z = (stateA = (stateA << 1 ^ (stateA >> 63 & 0x000000000000001BL)))
					* (stateB += 0x9E3779B97F4A7C16L);
			return z ^ z >>> 27;
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		BounceRandom rng = new BounceRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}