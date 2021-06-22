
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

import java.util.Random;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MarsRandomBench score: 949904384.000000 (949.9M 2067.2%)
 *             uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MarsRandomBench score: 720245440.000000 (720.2M 2039.5%)
 *             uncertainty:   1.1%
 * <br>
 * HotSpot Java 16:
 * <br>
 * MarsRandomBench score: 1790815104.000000 (1.791G 2130.6%)
 *             uncertainty:   0.2%
 * <br>
 * Note that on HotSpot Java 16, this gets 1.791 billion longs per second. The previous best I had written got 1.427
 * billion longs per second. Java's built-in java.util.Random gets 58.88 million longs per second, and the also-built-in
 * SplittableRandom gets 1.06 billion longs per second. The period for any particular state is unknown, but because this
 * uses a section that is LCG-like, it seems unlikely that any subcycle will have a shorter period than 2 to the 64. It
 * is still possible, though.
 * <br>
 * An earlier version seemed to get a whopping 1.996 billion longs per second, and passed early hwd testing, but
 * immediately failed PractRand testing with its BRank test. I think the number of false negatives for binary matrix
 * rank issues found by hwd (or not found) is somewhat startling.
 */
public final class MarsRandomBench extends MicroBench {

	public static class MarsRandom extends Random {
		private long stateA, stateB, stateC, stateD;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public MarsRandom() {
			super();
			stateA = super.nextLong();
			stateB = super.nextLong();
			stateC = super.nextLong();
			stateD = super.nextLong();
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
		public MarsRandom(long seed) {
			super(seed);
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = ~seed;
			stateD = seed ^ 0x05CB93402A76F7DAL;
		}
		public MarsRandom(long stateA, long stateB, long stateC, long stateD) {
			super(stateA);
			this.stateA = stateA;
			this.stateB = stateB;
			this.stateC = stateC;
			this.stateD = stateD;
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
			stateC = ~seed;
			stateD = seed ^ 0x05CB93402A76F7DAL;
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
			final long fa = this.stateA;
			final long fb = this.stateB;
			final long fc = this.stateC;
			final long fd = this.stateD;
			this.stateA = 0xD1342543DE82EF95L * fd;
			this.stateB = fa + 0xC6BC279692B5C323L;
			this.stateC = Long.rotateLeft(fb, 47) - fd;
			this.stateD = fb ^ fc;
			return (int) fd >>> 32 - bits;
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
			final long fa = this.stateA;
			final long fb = this.stateB;
			final long fc = this.stateC;
			final long fd = this.stateD;
			this.stateA = 0xD1342543DE82EF95L * fd;
			this.stateB = fa + 0xC6BC279692B5C323L;
			this.stateC = Long.rotateLeft(fb, 47) - fd;
			this.stateD = fb ^ fc;
			return fd;
			// returning fc is significantly faster, but has severe BRank issues.
		}

		public static void main(String[] args){
			MarsRandom rng = new MarsRandom(0L, 0L, 0L, 0L);
			for (int i = 0; i < 256; i++) {
				System.out.printf("%04d returned 0x%3$016X with weight %2$02d, has state 0x%4$016X 0x%5$016X 0x%6$016X 0x%7$016X\n",
						i, Long.bitCount(rng.stateD), rng.nextLong(), rng.stateA, rng.stateB, rng.stateC, rng.stateD);
				if(0L == (rng.stateA | rng.stateB | rng.stateC | rng.stateD)){
					System.out.printf("UH OH at %04d\n", i);
				}
			}
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		MarsRandom rng = new MarsRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}