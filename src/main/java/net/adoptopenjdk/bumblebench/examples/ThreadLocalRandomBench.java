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

import java.util.concurrent.ThreadLocalRandom;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * ThreadLocalRandomBench score: 740075840.000000 (740.1M 2042.2%)
 *                   uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ThreadLocalRandomBench score: 401219488.000000 (401.2M 1981.0%)
 *                   uncertainty:   0.7%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * ThreadLocalRandomBench score: 1102351232.000000 (1.102G 2082.1%)
 *                   uncertainty:   1.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ThreadLocalRandomBench score: 990149760.000000 (990.1M 2071.3%)
 *                   uncertainty:   0.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * ThreadLocalRandomBench score: 1130547712.000000 (1.131G 2084.6%)
 *                   uncertainty:   1.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * ThreadLocalRandomBench score: 1669552512.000000 (1.670G 2123.6%)
 *                   uncertainty:   0.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * ThreadLocalRandomBench score: 1011617344.000000 (1.012G 2073.5%)
 *                   uncertainty:   2.0%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * ThreadLocalRandomBench score: 1006814208.000000 (1.007G 2073.0%)
 *                   uncertainty:   0.6%
 */
public final class ThreadLocalRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}
