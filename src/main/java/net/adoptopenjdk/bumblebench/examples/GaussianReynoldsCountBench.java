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

import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * GaussianReynoldsCountBench score: 384986880.000000 (385.0M 1976.9%)
 *                        uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GaussianReynoldsCountBench score: 313583552.000000 (313.6M 1956.4%)
 *                        uncertainty:   0.7%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GaussianReynoldsCountBench score: 351991456.000000 (352.0M 1967.9%)
 *                        uncertainty:   1.9%
 */
public final class GaussianReynoldsCountBench extends MicroBench {

	private double nextGaussian(final LaserRandom rng){
		final long bd = Long.bitCount(rng.nextLong()) - 32L;
		final long u1 = rng.nextLong();
		return 0x1.fb760cp-35 * ((bd << 32) + (u1 & 0xFFFFFFFFL) - (u1 >>> 32));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		LaserRandom rng = new LaserRandom(0x12345678);
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextGaussian(rng);
		return numIterations;
	}
}
