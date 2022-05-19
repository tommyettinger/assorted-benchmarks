
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
 * TrimRandomBench score: 791450560.000000 (791.5M 2048.9%)
 *             uncertainty:   3.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * TrimRandomBench score: 947973824.000000 (948.0M 2067.0%)
 *             uncertainty:   0.9%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * TrimRandomBench score: 1616763904.000000 (1.617G 2120.4%)
 *             uncertainty:   1.5%
 * <br>
 * GraalVM Java 16:
 * <br> 
 * TrimRandomBench score: 1646932352.000000 (1.647G 2122.2%)
 *             uncertainty:   5.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * TrimRandomBench score: 1307109504.000000 (1.307G 2099.1%)
 *             uncertainty:   1.2%
 */
public final class Trim3RandomBench extends MicroBench {

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
			stateA = Long.rotateLeft(fb ^ fc, 57);
			stateB = Long.rotateLeft(fc ^ fd, 11);
			stateC = fa + fb;
			stateD = fd + 0xADB5B12149E93C39L;
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
			stateA = Long.rotateLeft(fb ^ fc, 57);
			stateB = Long.rotateLeft(fc ^ fd, 11);
			stateC = fa + fb;
			stateD = fd + 0xADB5B12149E93C39L;
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