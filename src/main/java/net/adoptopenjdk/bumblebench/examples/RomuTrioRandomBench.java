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
import squidpony.squidmath.LightRNG;

import java.io.Serializable;
import java.util.Random;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RomuTrioRandomBench score: 936142016.000000 (936.1M 2065.7%)
 *                 uncertainty:   5.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RomuTrioRandomBench score: 948451776.000000 (948.5M 2067.0%)
 *                 uncertainty:   1.7%
 * HotSpot Java 16:
 * <br>
 * RomuTrioRandomBench score: 1272620544.000000 (1.273G 2096.4%)
 *                 uncertainty:   1.2%
 * <br>
 * RomuTrio is very fast on HotSpot, just behind Harpo here on Java 16 and ahead of it on Java 8.
 * Oddly, it doesn't seem to be optimized well on OpenJ9, and is significantly slower than Harpo there; this could have
 * to do with the one large multiplication this uses for each random number.
 * <br>
 * Credit to Mark Overton for creating the Romu generators, https://www.romu-random.org/
 */
public final class RomuTrioRandomBench extends MicroBench {

	public static class RomuTrioRandom extends Random implements Serializable{
		private long stateA, stateB, stateC;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public RomuTrioRandom() {
			super();
			stateA = super.nextLong();
			stateB = LightRNG.determine(stateA);
			stateC = LightRNG.determine(stateA + 1L);
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
		public RomuTrioRandom(long seed) {
			super(seed);
			stateA = LightRNG.determine(seed);
			stateB = LightRNG.determine(seed+1L);
			stateC = LightRNG.determine(seed+2L);
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
			stateA = LightRNG.determine(seed);
			stateB = LightRNG.determine(seed+1L);
			stateC = LightRNG.determine(seed+2L);
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

			final long xp = this.stateA;
			final long yp = this.stateB;
			final long zp = this.stateC;
			this.stateA = 0xD3833E804F4C574BL * zp;
			this.stateB = yp - xp;
			this.stateB = Long.rotateLeft(this.stateB, 12);
			this.stateC = zp - yp;
			this.stateC = Long.rotateLeft(this.stateC, 44);
			return (int) (xp) >>> 32 - bits;
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
			final long xp = this.stateA;
			final long yp = this.stateB;
			final long zp = this.stateC;
			this.stateA = 0xD3833E804F4C574BL * zp;
			this.stateB = yp - xp;
			this.stateB = Long.rotateLeft(this.stateB, 12);
			this.stateC = zp - yp;
			this.stateC = Long.rotateLeft(this.stateC, 44);

			return xp;
		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		RomuTrioRandom rng = new RomuTrioRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}