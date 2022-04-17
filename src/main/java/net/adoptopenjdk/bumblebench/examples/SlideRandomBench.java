
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

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SlideRandomBench score: 1106750464.000000 (1.107G 2082.5%)
 *              uncertainty:   8.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SlideRandomBench score: 721886592.000000 (721.9M 2039.7%)
 *              uncertainty:   1.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SlideRandomBench score: 1753669376.000000 (1.754G 2128.5%)
 *              uncertainty:   3.0%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * SlideRandomBench score: 1728498560.000000 (1.728G 2127.1%)
 *              uncertainty:   3.8%
 */
public final class SlideRandomBench extends MicroBench {

	public static class SlideRandom implements RandomnessSource {
		private long stateA, stateB, stateC, stateD;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public SlideRandom() {
			stateA = (long) ((Math.random() - 0.5) * 0x10000000000000L) ^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L);
			stateB = (long) ((Math.random() - 0.5) * 0x10000000000000L) ^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L);
			stateC = (long) ((Math.random() - 0.5) * 0x10000000000000L) ^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L);
			stateD = (long) ((Math.random() - 0.5) * 0x10000000000000L) ^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L);
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
		public SlideRandom(long seed) {
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = ~seed;
			stateD = seed ^ 0x05CB93402A76F7DAL;
		}
		public SlideRandom(long stateA, long stateB, long stateC, long stateD) {
			this.stateA = stateA;
			this.stateB = stateB;
			this.stateC = stateC;
			this.stateD = stateD;
		}

		/**
		 * Sets the seed of this random number generator using a single
		 * {@code long} seed.
		 * @param seed the initial seed
		 */
		public void setSeed(long seed) {
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
		public int next(int bits) {
			final long fa = this.stateA;
			final long fb = this.stateB;
			final long fc = this.stateC;
			final long fd = this.stateD;
			this.stateA = fb ^ fc ^ fd;
			this.stateB = Long.rotateLeft(fa, 42);
			this.stateC = fa + fb;
			this.stateD = fc + 0xC6BC279692B5C323L;
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
			this.stateA = fb ^ fc ^ fd;
			this.stateB = Long.rotateLeft(fa, 42);
			this.stateC = fa + fb;
			this.stateD = fc + 0xC6BC279692B5C323L;
			return fc;
		}

		@Override
		public SlideRandom copy() {
			return new SlideRandom(stateA, stateB, stateC, stateD);
		}

		public static void main(String[] args) {
//			long count = 0xffffffL;
			long count = 0xffffffffL;
			long[] buf = new long[256];

			SlideRandom rand = new SlideRandom(0L);
			for (long i = 0; i <= count; i++) {
				buf[(int)(rand.nextLong() & 255)]++;
			}
/*
			long lcg = 0L;
			for (long i = 0; i <= count; i++) {
				buf[(int)((lcg = lcg * 0xD1342543DE82EF95L + 0xC6BC279692B5C323L) & 255)]++;
			}

 */
			for (int i = 0; i < 256; i++) {
				System.out.printf("%d\n", buf[i]);
			}
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		SlideRandom rng = new SlideRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}