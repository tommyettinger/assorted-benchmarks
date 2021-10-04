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
 * StrangerRandomBench score: 605248448.000000 (605.2M 2022.1%)
 *                 uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * StrangerRandomBench score: 701112384.000000 (701.1M 2036.8%)
 *                 uncertainty:   0.7%
 * <br>
 * HotSpot Java 16:
 * <br>
 * StrangerRandomBench score: 1303557888.000000 (1.304G 2098.8%)
 *                 uncertainty:   0.3%
 * <br>
 * This has somewhat onerous seeding requirements; stateA and stateB must be sufficiently different from each other to
 * have them produce different streams. As you can see above, it isn't very fast on Java 8 or OpenJ9, and FourWheel/Mars
 * is probably a better choice.
 */
public final class StrangerRandomBench extends MicroBench {

	public static class StrangerRandom extends Random {
		private long stateA, stateB, stateC, stateD;

		/**
		 * Jumps {@code state} ahead by 0x9E3779B97F4A7C15 steps of the generator StrangerRandom uses for its stateA
		 * and stateB. When used how it is here, it ensures stateB is 11.4 quintillion steps ahead of stateA in their
		 * shared sequence, or 7 quintillion behind if you look at it another way. It would typically take decades of
		 * continuously running this generator at 100GB/s to have stateA become any state that stateB has already been.
		 * <br>
		 * Massive credit to Spencer Fleming for writing essentially all of this function over several days.
		 * @param state the initial state of a 7-9 xorshift generator
		 * @return state jumped ahead 0x9E3779B97F4A7C15 times (unsigned)
		 */
		private static long jump(long state){
			final long poly = 0x5556837749D9A17FL;
			long val = 0L, b = 1L;
			for (int i = 0; i < 63; i++, b <<= 1) {
				if((poly & b) != 0L) val ^= state;
				state ^= state << 7;
				state ^= state >>> 9;
			}
			return val;
		}
		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public StrangerRandom() {
			super();
			stateA = super.nextLong();
			if(stateA == 0L) stateA = 0xD3833E804F4C574BL;
			stateB = jump(stateA);
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
		public StrangerRandom(long seed) {
			super(seed);
			stateA = seed ^ 0xFA346CBFD5890825L;
			if(stateA == 0L) stateA = 0xD3833E804F4C574BL;
			stateB = jump(stateA);
			stateC = seed ^ 0x05CB93402A76F7DAL;
			stateD = ~seed;
		}
		public StrangerRandom(long stateA, long stateB, long stateC, long stateD) {
			super(stateA);
			this.stateA = (stateA == 0L) ? 0xD3833E804F4C574BL : stateA;
			this.stateB = (stateB == 0L) ? 0x790B300BF9FE738FL : stateB;
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
			if(stateA == 0L) stateA = 0xD3833E804F4C574BL;
			stateB = jump(stateA);
			stateC = seed ^ 0x05CB93402A76F7DAL;
			stateD = ~seed;
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
			this.stateA = fb ^ fb << 7;
			this.stateB = fa ^ fa >>> 9;
			this.stateC = Long.rotateLeft(fd, 39) - fb;
			this.stateD = fa - fc + 0xC6BC279692B5C323L;
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
			this.stateA = fb ^ fb << 7;
			this.stateB = fa ^ fa >>> 9;
			this.stateC = Long.rotateLeft(fd, 39) - fb;
			this.stateD = fa - fc + 0xC6BC279692B5C323L;
			return fc;
		}

//		public static void main(String[] args){
//			MarsRandom rng = new MarsRandom(0L, 0L, 0L, 0L);
//			for (int i = 0; i < 256; i++) {
//				System.out.printf("%04d returned 0x%3$016X with weight %2$02d, has state 0x%4$016X 0x%5$016X 0x%6$016X 0x%7$016X\n",
//						i, Long.bitCount(rng.stateD), rng.nextLong(), rng.stateA, rng.stateB, rng.stateC, rng.stateD);
//				if(0L == (rng.stateA | rng.stateB | rng.stateC | rng.stateD)){
//					System.out.printf("UH OH at %04d\n", i);
//				}
//			}
//		}
		public static void main(String[] args) {
//			long count = 0xffffffL;
			long count = 0xffffffffL;
			long[] buf = new long[256];

			StrangerRandom rand = new StrangerRandom(1234567L);
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

			long original = 0x4AD46E4797DADE53L;
			long jumped = 0xB69A5C2F5FBC9EC6L;
			System.out.printf("0x%016X == 0x%016X\n", jumped, jump(original));
//			System.out.printf("0x%016X\n", jump(0xD3833E804F4C574BL));

		}
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		StrangerRandom rng = new StrangerRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}

	public static void main(String[] args) {

	}
}