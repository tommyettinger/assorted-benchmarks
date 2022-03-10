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
 * ChopIntRandomBench score: 1063630848.000000 (1.064G 2078.5%)
 *                uncertainty:   2.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ChopIntRandomBench score: 1126248576.000000 (1.126G 2084.2%)
 *                uncertainty:   1.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * ChopIntRandomBench score: 1031597888.000000 (1.032G 2075.4%)
 *                uncertainty:   1.6%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * ChopIntRandomBench score: 1373170176.000000 (1.373G 2104.0%)
 *                uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ChopIntRandomBench score: 1055195584.000000 (1.055G 2077.7%)
 *                uncertainty:   0.4%
 */
public final class ChopIntRandomBench extends MicroBench {

	public static class ChopRandom implements RandomnessSource {
		private int stateA, stateB, stateC, stateD;
		public ChopRandom (long seed) {
			setSeed(seed);
		}
		public void setSeed(long seed) {
			long x = (seed += 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateA = (int)(x ^ x >>> 27);
			x = (seed += 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateB = (int)(x ^ x >>> 27);
			x = (seed += 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateC = (int)(x ^ x >>> 27);
			x = (seed + 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateD = (int)(x ^ x >>> 27);
		}
		public ChopRandom (int stateA, int stateB, int stateC, int stateD) {
			this.stateA = stateA;
			this.stateB = stateB;
			this.stateC = stateC;
			this.stateD = stateD;
		}

		@Override
		public long nextLong() {
//			final int fa = stateA;
//			final int fb = stateB;
//			final int fc = stateC;
//			final int fd = stateD;
//			int ga = fb ^ fc; ga = (ga << 26 | ga >>>  6);
//			int gb = fc ^ fd; gb = (gb << 11 | gb >>> 21);
//			final int gc = fa ^ fb + fc;
//			final int gd = fd + 0xADB5B165;
//			int sa = gb ^ gc; stateA = (sa << 26 | sa >>>  6);
//			int sb = gc ^ gd; stateB = (sb << 11 | sb >>> 21);
//			stateC = ga ^ gb + gc;
//			stateD = gd + 0xADB5B165;
//			return (long)fc << 32 ^ gc;

			final int fa = stateA;
			final int fb = stateB;
			final int fc = stateC;
			final int fd = stateD;
			final int sa = fb ^ fc; stateA = (sa << 26 | sa >>>  6);
			final int sb = fc ^ fd; stateB = (sb << 11 | sb >>> 21);
			stateC = fa ^ fb + fc;
			stateD = fd + 0xADB5B165;
			return (long) (fb + fd) << 32 ^ (fa + fc);
		}

		@Override
		public int next(int bits) {
			final int fa = stateA;
			final int fb = stateB;
			final int fc = stateC;
			final int fd = stateD;
			final int sa = fb ^ fc; stateA = (sa << 26 | sa >>>  6);
			final int sb = fc ^ fd; stateB = (sb << 11 | sb >>> 21);
			stateC = fa ^ fb + fc;
			stateD = fd + 0xADB5B165;
			return fc >>> (32 - bits);
		}

		public int nextInt () {
			final int fa = stateA;
			final int fb = stateB;
			final int fc = stateC;
			final int fd = stateD;
			final int sa = fb ^ fc; stateA = (sa << 26 | sa >>>  6);
			final int sb = fc ^ fd; stateB = (sb << 11 | sb >>> 21);
			stateC = fa ^ fb + fc;
			stateD = fd + 0xADB5B165;
			return fc;
		}

    @Override
    public ChopRandom copy() {
        return new ChopRandom(stateA, stateB, stateC, stateD);
    }

	}

	protected long doBatch(long numIterations) throws InterruptedException {
		ChopRandom rng = new ChopRandom(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}
