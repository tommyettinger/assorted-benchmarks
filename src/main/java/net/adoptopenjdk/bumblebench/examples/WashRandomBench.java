
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

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * WashRandomBench score: 836856640.000000 (836.9M 2054.5%)
 *             uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * WashRandomBench score: 1396819456.000000 (1.397G 2105.7%)
 *             uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * WashRandomBench score: 1406542208.000000 (1.407G 2106.4%)
 *             uncertainty:   1.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * WashRandomBench score: 1475117056.000000 (1.475G 2111.2%)
 *             uncertainty:   0.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * WashRandomBench score: 1821445504.000000 (1.821G 2132.3%)
 *             uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * WashRandomBench score: 675350528.000000 (675.4M 2033.1%)
 *             uncertainty:  10.2%
 */
public final class WashRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		WashRandom rng = new WashRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}

// older results
/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * WashRandomBench score: 945059200.000000 (945.1M 2066.7%)
 *             uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * WashRandomBench score: 1337468416.000000 (1.337G 2101.4%)
 *             uncertainty:   2.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * WashRandomBench score: 2195459584.000000 (2.195G 2151.0%)
 *             uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * WashRandomBench score: 2195852544.000000 (2.196G 2151.0%)
 *             uncertainty:   1.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * WashRandomBench score: 2260594944.000000 (2.261G 2153.9%)
 *             uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * WashRandomBench score: 694221312.000000 (694.2M 2035.8%)
 *             uncertainty:   2.7%
 */
