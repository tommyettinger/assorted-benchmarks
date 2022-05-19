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

import com.github.tommyettinger.random.Xoshiro256StarStarRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * Xoshiro256StarStarRandomBench score: 672817472.000000 (672.8M 2032.7%)
 *                           uncertainty:   1.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Xoshiro256StarStarRandomBench score: 1322192640.000000 (1.322G 2100.3%)
 *                           uncertainty:   0.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * Xoshiro256StarStarRandomBench score: 914226560.000000 (914.2M 2063.4%)
 *                           uncertainty:   1.5%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * Xoshiro256StarStarRandomBench score: 837045056.000000 (837.0M 2054.5%)
 *                           uncertainty:   1.7%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Xoshiro256StarStarRandomBench score: 898909568.000000 (898.9M 2061.7%)
 *                           uncertainty:   0.6%
 */
public final class Xoshiro256StarStarRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Xoshiro256StarStarRandom rng = new Xoshiro256StarStarRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
