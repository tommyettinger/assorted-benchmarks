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

import com.github.tommyettinger.ds.support.EnhancedRandom;
import com.github.tommyettinger.ds.support.LaserRandom;
import com.github.tommyettinger.ds.support.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 *
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16:
 * <br>
 *
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
