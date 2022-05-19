
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
 * Trim4RandomBench score: 1092523008.000000 (1.093G 2081.2%)
 *              uncertainty:   2.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Trim4RandomBench score: 810780864.000000 (810.8M 2051.4%)
 *              uncertainty:   2.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Trim4RandomBench score: 1484576512.000000 (1.485G 2111.8%)
 *              uncertainty:   1.3%
 * <br>
 * GraalVM Java 16:
 * <br> 
 * Trim4RandomBench score: 1420749440.000000 (1.421G 2107.4%)
 *              uncertainty:   0.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Trim4RandomBench score: 1494746752.000000 (1.495G 2112.5%)
 *              uncertainty:   0.4%
 */
public final class Trim4RandomBench extends MicroBench {

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
			final long fa = this.stateA;
			final long fb = this.stateB;
			final long fc = this.stateC;
			final long fd = this.stateD;
			stateA = Long.rotateLeft(fb ^ fc, 37);
			stateB = Long.rotateLeft(fc ^ fd, 57);
			stateC = fa ^ fb + fc;
			stateD = fd + 0xA6766DC536E4D933L;
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
			final long bc = fb ^ fc;
			final long cd = fc ^ fd;
			stateA = (bc << 57 | bc >>> 7);
			stateB = (cd << 18 | cd >>> 46);
			stateC = fa + bc;
			stateD = fd + 0xDE916ABCC965815BL;
			return fc;
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