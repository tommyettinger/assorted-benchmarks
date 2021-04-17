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

import java.util.SplittableRandom;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SplittableRandomBench score: 1021770880.000000 (1.022G 2074.5%)
 *                   uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SplittableRandomBench score: 893283072.000000 (893.3M 2061.0%)
 *                   uncertainty:   1.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * SplittableRandomBench score: 1060040960.000000 (1.060G 2078.2%)
 *                   uncertainty:   0.4%
 */
public final class SplittableRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SplittableRandom rng = new SplittableRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
