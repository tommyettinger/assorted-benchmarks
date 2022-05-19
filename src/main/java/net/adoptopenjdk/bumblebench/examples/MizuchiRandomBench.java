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

import com.github.tommyettinger.random.MizuchiRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MizuchiRandomBench score: 859692928.000000 (859.7M 2057.2%)
 *                uncertainty:   2.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MizuchiRandomBench score: 1101915520.000000 (1.102G 2082.0%)
 *                uncertainty:   0.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * MizuchiRandomBench score: 710107200.000000 (710.1M 2038.1%)
 *                uncertainty:   0.3%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * MizuchiRandomBench score: 865937664.000000 (865.9M 2057.9%)
 *                uncertainty:   0.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MizuchiRandomBench score: 793049216.000000 (793.0M 2049.1%)
 *                uncertainty:   0.2%
 */
public final class MizuchiRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		MizuchiRandom rng = new MizuchiRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
