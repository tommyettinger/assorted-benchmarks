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

import com.badlogic.gdx.math.RandomXS128;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RandomXS128Bench score: 773990272.000000 (774.0M 2046.7%)
 *              uncertainty:   3.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomXS128Bench score: 990374400.000000 (990.4M 2071.4%)
 *              uncertainty:   0.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomXS128Bench score: 963110592.000000 (963.1M 2068.6%)
 *              uncertainty:   1.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomXS128Bench score: 965328832.000000 (965.3M 2068.8%)
 *              uncertainty:   2.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomXS128Bench score: 976815360.000000 (976.8M 2070.0%)
 *              uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * RandomXS128Bench score: 685345984.000000 (685.3M 2034.5%)
 *              uncertainty:   0.4%
 */
public final class RandomXS128Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		RandomXS128 rng = new RandomXS128(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}

// old benchmarks
/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RandomXS128Bench score: 746991168.000000 (747.0M 2043.2%)
 *              uncertainty:   1.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomXS128Bench score: 978084160.000000 (978.1M 2070.1%)
 *              uncertainty:   1.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * RandomXS128Bench score: 955003264.000000 (955.0M 2067.7%)
 *              uncertainty:   0.4%
 * <br>
 * With Java 8, HotSpot, on an 8th-gen i7 hexacore mobile processor running Manjaro Linux:
 * <br>
 * RandomXS128Bench score: 709502592.000000 (709.5M 2038.0%)
 *              uncertainty:   0.0%
 * <br>
 * With Java 14, OpenJ9, same hardware:
 * <br>
 * RandomXS128Bench score: 909541824.000000 (909.5M 2062.8%)
 *              uncertainty:   0.2%
 */
