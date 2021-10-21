
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
 * This was not expected to be a high-quality generator, but instead to see how fast we can go.
 * Some small tweaks seem to have both sped it up and improved quality, so it passes at least 256GB
 * of PractRand without anomalies.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * BlipRandomBench score: 939625664.000000 (939.6M 2066.1%)
 *             uncertainty:   3.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BlipRandomBench score: 692315392.000000 (692.3M 2035.6%)
 *             uncertainty:   1.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BlipRandomBench score: 1940002304.000000 (1.940G 2138.6%)
 *             uncertainty:   6.1%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * BlipRandomBench score: 1570536704.000000 (1.571G 2117.5%)
 *             uncertainty:  10.6%
 */
public final class BlipRandomBench extends MicroBench {

	public static class BlipRandom extends Random {
		private long stateA, stateB, stateC, stateD;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public BlipRandom() {
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
		public BlipRandom(long seed) {
			super(seed);
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = ~seed;
			stateD = seed ^ 0x05CB93402A76F7DAL;
		}
		public BlipRandom(long stateA, long stateB, long stateC, long stateD) {
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
			stateA = fd ^ fc;
			stateB = (fa << 41 | fa >>> 64 - 41);
			stateC = fa - fb;
			stateD = fc + 0xC6BC279692B5C323L;
			return (int) fc >>> 32 - bits;
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
			stateA = fd ^ fc;
			stateB = (fa << 41 | fa >>> 64 - 41);
			stateC = fa - fb;
			stateD = fc + 0xC6BC279692B5C323L;
			return fc;
		}

		public static void main(String[] args) {
//			long count = 0xffffffL;
			long count = 0xffffffffL;
			long[] buf = new long[256];

			BlipRandom rand = new BlipRandom(1234567L);
			for (long i = 0; i <= count; i++) {
				buf[(int)(rand.nextLong() & 255)]++;
			}
/*
			long lcg = 0L;
			for (long i = 0; i <= count; i++) {
				buf[(int)((lcg = lcg * 0xD1342543DE82EF95L + 0xC6BC279692B5C323L) & 255)]++;
			}

 */
			for (int i = 0; i < 0x100; i++) {
				System.out.printf("%d\n", buf[i]);
			}
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		BlipRandom rng = new BlipRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}