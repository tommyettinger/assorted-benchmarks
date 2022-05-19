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
import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * GaussianProbitBench2 score: 131955432.000000 (132.0M 1869.8%)
 *                  uncertainty:   1.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GaussianProbitBench2 score: 90412312.000000 (90.41M 1832.0%)
 *                  uncertainty:   1.3%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GaussianProbitBench2 score: 133813112.000000 (133.8M 1871.2%)
 *                  uncertainty:   2.3%
 */
public final class GaussianProbitBench2 extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		LaserRandom rng = new LaserRandom(0x12345678);
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += EnhancedRandom.probit(rng.nextExclusiveDouble());
		return numIterations;
	}
}
