
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

import com.github.tommyettinger.random.EnhancedRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * Trim5RandomBench score: 1125434112.000000 (1.125G 2084.1%)
 *              uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Trim5RandomBench score: 837190400.000000 (837.2M 2054.6%)
 *              uncertainty:   0.9%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Trim5RandomBench score: 1510010496.000000 (1.510G 2113.5%)
 *              uncertainty:   1.4%
 * <br>
 * GraalVM Java 16:
 * <br> 
 * Trim5RandomBench score: 1412518272.000000 (1.413G 2106.9%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Trim5RandomBench score: 1505737088.000000 (1.506G 2113.3%)
 *              uncertainty:   1.1%
 */
public final class Trim5RandomBench extends MicroBench {

	public static class TrimRandom {
		private long stateA, stateB, stateC, stateD;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public TrimRandom() {
			stateA = EnhancedRandom.seedFromMath();
			stateB = EnhancedRandom.seedFromMath();
			stateC = EnhancedRandom.seedFromMath();
			stateD = EnhancedRandom.seedFromMath();
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
		public TrimRandom(long seed) {
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = ~seed;
			stateD = seed ^ 0x05CB93402A76F7DAL;
		}
		public TrimRandom(long stateA, long stateB, long stateC, long stateD) {
			this.stateA = stateA;
			this.stateB = stateB;
			this.stateC = stateC;
			this.stateD = stateD;
		}

		/**
		 * @param seed the initial seed
		 */
		public void setSeed(long seed) {
			stateA = seed ^ 0xFA346CBFD5890825L;
			stateB = seed;
			stateC = ~seed;
			stateD = seed ^ 0x05CB93402A76F7DAL;
		}

		/**
		 * @param bits random bits
		 * @return the next pseudorandom value from this random number
		 * generator's sequence
		 * @since 1.1
		 */
		protected int next(int bits) {
			final long fa = stateA;
			final long fb = stateB;
			final long fc = stateC;
			final long fd = stateD;
			stateA = Long.rotateLeft(fb ^ fc, 6);
			stateB = Long.rotateLeft(fc ^ fd, 6);
			stateC = fa * 0xD1342543DE82EF95L;
			stateD = fd + 0xD9501E54E3CE92E1L;
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
		public long nextLong() {
			final long fa = stateA;
			final long fb = stateB;
			final long fc = stateC;
			final long fd = stateD;
			final long bc = fb + fc;
			final long cd = fc ^ fd;
			stateA = (bc << 57 | bc >>> 7);
			stateB = (cd << 18 | cd >>> 46);
			stateC = fa ^ bc;
			stateD = fd + 0xA2623BAB769C24A7L;//0x8E44608B6A0EC52DL;
			return fa;
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		TrimRandom rng = new TrimRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}