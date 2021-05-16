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
 * LoraxRandomBench score: 935660416.000000 (935.7M 2065.7%)
 *              uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * LoraxRandomBench score: 938006720.000000 (938.0M 2065.9%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 16:
 * <br>
 * LoraxRandomBench score: 1637239168.000000 (1.637G 2121.6%)
 *              uncertainty:   0.5%
 * <br>
 * Unlike the Marx Brother RNGs, this one is built around a bijection for each state transition.
 * It starts by defining a0, b0, and c0, but does so differently -- a0 receives the previous stateC, b0 receives the
 * previous stateA and stateC XORed, and c0 receives the previous stateA and stateB XORed. This particular type of mix
 * is important. After that, there's a bijective step performed on each of a0, b0, and c0, and c0 is returned. The three
 * steps can be performed in parallel; stateA gets a constant added to a0, and stateB and stateC get rotated versions of
 * b0 and c0.
 * <br>
 * If you know the full state of the generator, you can tell what state came before it. This requires subtracting the
 * same constant from stateA to get the previous stateC, rotating stateB and stateC right (if before you rotated left)
 * to get the previous {@code stateA ^ stateC} and {@code stateA ^ stateB}, and XORing the known previous stateC with
 * the previous {@code stateA ^ stateC} to get the previous stateA, which can similarly be used to get the previous
 * stateB. There you go.
 * <br>
 * Alternate implementation with better avalanche properties, but worse speed:
 * <br>
 * HotSpot Java 8:
 * <br>
 * LoraxRandomBench score: 776639872.000000 (776.6M 2047.0%)
 *              uncertainty:   0.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * LoraxRandomBench score: 854053184.000000 (854.1M 2056.6%)
 *              uncertainty:   0.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * LoraxRandomBench score: 1574072448.000000 (1.574G 2117.7%)
 *              uncertainty:   0.6%
 */
public final class LoraxRandomBench extends MicroBench {

	// this one is the faster of the two implementations here.
	public static class LoraxRandom extends Random implements Serializable{
		private long stateA, stateB, stateC;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public LoraxRandom() {
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
		public LoraxRandom(long seed) {
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
		public void setSeed(long seed) {
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
			final long a0 = stateC;
			final long b0 = stateA ^ stateC;
			final long c0 = stateA ^ stateB;
			stateA = 0xC6BC279692B5C323L + a0;
			stateB = Long.rotateLeft(b0, 23);
			stateC = Long.rotateLeft(c0, 56);
			return (int) c0 >>> 32 - bits;
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
			final long a0 = stateC;
			final long b0 = stateA ^ stateC;
			final long c0 = stateA ^ stateB;
			stateA = 0xC6BC279692B5C323L + a0;
			stateB = Long.rotateLeft(b0, 23);
			stateC = Long.rotateLeft(c0, 56);
			return c0;
		}
	}

	// this one appears to have better avalanche qualities, but is slower.
	public static class LoraxAlternateRandom extends Random implements Serializable{
		private long stateA, stateB, stateC;
		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public LoraxAlternateRandom() {
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
		public LoraxAlternateRandom(long seed) {
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
		public void setSeed(long seed) {
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
			final long a0 = stateB ^ stateC;
			final long b0 = stateA;
			final long c0 = stateA ^ stateB;
			stateA = 0xC6BC279692B5C323L + a0;
			stateB = Long.rotateLeft(b0, 18);
			stateC = Long.rotateLeft(c0, 47);
			return (int) a0 >>> 32 - bits;
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
			final long a0 = stateB ^ stateC;
			final long b0 = stateA;
			final long c0 = stateA ^ stateB;
			stateA = 0xC6BC279692B5C323L + a0;
			stateB = Long.rotateLeft(b0, 18);
			stateC = Long.rotateLeft(c0, 47);
			return a0;
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		LoraxRandom rng = new LoraxRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}