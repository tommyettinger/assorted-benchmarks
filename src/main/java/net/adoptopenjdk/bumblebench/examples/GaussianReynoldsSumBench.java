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
 * GaussianReynoldsSumBench score: 388949888.000000 (388.9M 1977.9%)
 *                      uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GaussianReynoldsSumBench score: 325008576.000000 (325.0M 1959.9%)
 *                      uncertainty:   1.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GaussianReynoldsSumBench score: 378548000.000000 (378.5M 1975.2%)
 *                      uncertainty:   0.8%
 */
public final class GaussianReynoldsSumBench extends MicroBench {

	private double nextGaussian(final LaserRandom rng){
		final long u0 = rng.nextLong();
		final long u1 = rng.nextLong();
		return 0x1.b566e2p-32 * ((u0 & 0xFFFFFFFFL) + (u0 >>> 32) - (u1 & 0xFFFFFFFFL) - (u1 >>> 32));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		LaserRandom rng = new LaserRandom(0x12345678);
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextGaussian(rng);
		return numIterations;
	}
}
