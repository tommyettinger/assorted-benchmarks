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
 * ThreadLocalRandomLongBench score: 732366400.000000 (732.4M 2041.2%)
 *                        uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ThreadLocalRandomLongBench score: 386017344.000000 (386.0M 1977.1%)
 *                        uncertainty:   1.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * ThreadLocalRandomLongBench score: 1035104384.000000 (1.035G 2075.8%)
 *                        uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ThreadLocalRandomLongBench score: 944645376.000000 (944.6M 2066.6%)
 *                        uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * ThreadLocalRandomLongBench score: 1080588544.000000 (1.081G 2080.1%)
 *                        uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * ThreadLocalRandomLongBench score: 1680627200.000000 (1.681G 2124.2%)
 *                        uncertainty:   2.3%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * ThreadLocalRandomLongBench score: 938666048.000000 (938.7M 2066.0%)
 *                        uncertainty:   1.0%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * ThreadLocalRandomLongBench score: 941379520.000000 (941.4M 2066.3%)
 *                        uncertainty:   0.7%
 */
public final class ThreadLocalRandomLongBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
