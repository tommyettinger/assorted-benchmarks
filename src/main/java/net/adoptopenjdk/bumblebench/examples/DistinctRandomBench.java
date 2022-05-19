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

import com.github.tommyettinger.random.DistinctRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * DistinctRandomBench score: 1017463424.000000 (1.017G 2074.1%)
 *                 uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DistinctRandomBench score: 4043275520.000000 (4.043G 2212.0%)
 *                 uncertainty:   1.7%
 * <br>
 * HotSpot Java 16:
 * <br>
 * DistinctRandomBench score: 1076447232.000000 (1.076G 2079.7%)
 *                 uncertainty:   0.7%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * DistinctRandomBench score: 1126188544.000000 (1.126G 2084.2%)
 *                 uncertainty:   2.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DistinctRandomBench score: 1087576320.000000 (1.088G 2080.7%)
 *                 uncertainty:   1.0%
 */
public final class DistinctRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		DistinctRandom rng = new DistinctRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
