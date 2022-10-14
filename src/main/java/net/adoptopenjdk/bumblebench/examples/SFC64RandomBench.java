
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
 * SFC64RandomBench score: 851172992.000000 (851.2M 2056.2%)
 *              uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SFC64RandomBench score: 794539456.000000 (794.5M 2049.3%)
 *              uncertainty:   2.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SFC64RandomBench score: 1013789312.000000 (1.014G 2073.7%)
 *              uncertainty:   1.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SFC64RandomBench score: 896921664.000000 (896.9M 2061.4%)
 *              uncertainty:   1.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SFC64RandomBench score: 990144320.000000 (990.1M 2071.3%)
 *              uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SFC64RandomBench score: 501072320.000000 (501.1M 2003.2%)
 *              uncertainty:   3.7%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SFC64RandomBench score: 893886784.000000 (893.9M 2061.1%)
 *              uncertainty:   0.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SFC64RandomBench score: 1060832768.000000 (1.061G 2078.2%)
 *              uncertainty:   0.3%
 */
public final class SFC64RandomBench extends MicroBench {

	public static class SFC64Random extends Random {
		private long a, b, c, d;

		/**
		 * Creates a new random number generator. This constructor sets
		 * the seed of the random number generator to a value very likely
		 * to be distinct from any other invocation of this constructor.
		 */
		public SFC64Random() {
			super();
			a = super.nextLong();
			b = super.nextLong();
			c = super.nextLong();
			d = super.nextLong();
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
		public SFC64Random(long seed) {
			super(seed);
			a = seed ^ 0xFA346CBFD5890825L;
			b = seed;
			c = ~seed;
			d = seed ^ 0x05CB93402A76F7DAL;
		}
		public SFC64Random(long a, long b, long c, long d) {
			super(a);
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
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
			a = seed ^ 0xFA346CBFD5890825L;
			b = seed;
			c = ~seed;
			d = seed ^ 0x05CB93402A76F7DAL;
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
			final long a = this.a;
			final long b = this.b;
			final long c = this.c;
			final long tmp = a + b + this.d++;
			this.a = b ^ (b >>> 11);
			this.b = c + (c << 3);
			this.c = (c << 24 | c >>> 40) + tmp;
			return (int) tmp >>> 32 - bits;
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
			final long a = this.a;
			final long b = this.b;
			final long c = this.c;
			final long tmp = a + b + this.d++;
			this.a = b ^ (b >>> 11);
			this.b = c + (c << 3);
			this.c = (c << 24 | c >>> 40) + tmp;
			return tmp;
		}

		public static void main(String[] args) {
//			long count = 0xffffffL;
			long count = 0xffffffffL;
			long[] buf = new long[256];

			SFC64Random rand = new SFC64Random(1234567L);
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
		SFC64Random rng = new SFC64Random(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SFC64RandomBench score: 819207872.000000 (819.2M 2052.4%)
 *              uncertainty:   1.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SFC64RandomBench score: 780036096.000000 (780.0M 2047.5%)
 *              uncertainty:   1.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SFC64RandomBench score: 997751616.000000 (997.8M 2072.1%)
 *              uncertainty:   1.6%
 * <br>
 * GraalVM Java 16:
 * <br>
 * SFC64RandomBench score: 1012227200.000000 (1.012G 2073.5%)
 *              uncertainty:   1.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SFC64RandomBench score: 893554688.000000 (893.6M 2061.1%)
 *              uncertainty:   1.5%
 */
