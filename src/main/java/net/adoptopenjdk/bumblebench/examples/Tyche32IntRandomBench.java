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
 * Tyche32IntRandomBench score: 682918464.000000 (682.9M 2034.2%)
 *                  uncertainty:   1.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Tyche32IntRandomBench score: 610401344.000000 (610.4M 2023.0%)
 *                  uncertainty:   0.4%
 * <br>
 * HotSpot Java 16:
 * <br>
 * Tyche32IntRandomBench score: 729931008.000000 (729.9M 2040.8%)
 *                  uncertainty:   0.9%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * Tyche32IntRandomBench score: 647353920.000000 (647.4M 2028.8%)
 *                  uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Tyche32IntRandomBench score: 700037504.000000 (700.0M 2036.7%)
 *                  uncertainty:   0.1%
 */
public final class Tyche32IntRandomBench extends MicroBench {

	/**
	 * This is TycheI2, as-is from another project.
	 * It is moderately fast as a 32-bit generator at producing int values.
	 * It has some tricks for long generation that should speed it up.
	 */
	public static class Tyche32Random implements RandomnessSource {
		private int stateA, stateB, stateC, stateD;

		public Tyche32Random(long seed) {
			setSeed(seed);
		}

		public void setSeed(long seed) {
			long x = (seed += 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateA = (int) (x ^ x >>> 27);
			x = (seed += 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateB = (int) (x ^ x >>> 27);
			x = (seed += 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateC = (int) (x ^ x >>> 27);
			x = (seed + 0x9E3779B97F4A7C15L);
			x ^= x >>> 27;
			x *= 0x3C79AC492BA7B653L;
			x ^= x >>> 33;
			x *= 0x1C69B3F74AC4AE35L;
			stateD = (int) (x ^ x >>> 27);
		}

		public Tyche32Random(int stateA, int stateB, int stateC, int stateD) {
			this.stateA = stateA;
			this.stateB = stateB;
			this.stateC = stateC;
			this.stateD = stateD;
		}

		public long nextLong() {
			stateB = (stateB << 7 | stateB >>> 25) ^ stateC;
			stateC -= stateD;
			stateD = (stateD << 8 | stateD >>> 24) ^ stateA;
			stateA -= stateB;
			stateB = (stateD << 12 | stateD >>> 20) ^ stateC;
			stateC -= stateD;
			stateD = (stateD << 16 | stateD >>> 16) ^ stateA;
			stateA -= stateB;
			return (long) stateA << 32 ^ stateB;
		}

		public int next(int bits) {
			stateB = (stateB << 7 | stateB >>> 25) ^ stateC;
			stateC -= stateD;
			stateD = (stateD << 8 | stateD >>> 24) ^ stateA;
			stateA -= stateB;
			stateB = (stateD << 12 | stateD >>> 20) ^ stateC;
			stateC -= stateD;
			stateD = (stateD << 16 | stateD >>> 16) ^ stateA;
			stateA -= stateB;
			return stateA >>> (32 - bits);
		}

		public int nextInt() {
			stateB = (stateB << 7 | stateB >>> 25) ^ stateC;
			stateC -= stateD;
			stateD = (stateD << 8 | stateD >>> 24) ^ stateA;
			stateA -= stateB;
			stateB = (stateD << 12 | stateD >>> 20) ^ stateC;
			stateC -= stateD;
			stateD = (stateD << 16 | stateD >>> 16) ^ stateA;
			stateA -= stateB;
			return stateA;
		}

		public Tyche32Random copy() {
			return new Tyche32Random(stateA, stateB, stateC, stateD);
		}
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		Tyche32Random rng = new Tyche32Random(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}
