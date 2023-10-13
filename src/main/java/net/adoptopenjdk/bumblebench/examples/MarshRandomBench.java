
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
 * MarshRandomBench score: 367028992.000000 (367.0M 1972.1%)
 *              uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MarshRandomBench score: 1827887872.000000 (1.828G 2132.6%)
 *              uncertainty:   0.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MarshRandomBench score: 353291392.000000 (353.3M 1968.3%)
 *              uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MarshRandomBench score: 326104640.000000 (326.1M 1960.3%)
 *              uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * MarshRandomBench score: 643820672.000000 (643.8M 2028.3%)
 *              uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * MarshRandomBench score: 839571904.000000 (839.6M 2054.8%)
 *              uncertainty:   0.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * MarshRandomBench score: 325307808.000000 (325.3M 1960.0%)
 *              uncertainty:   0.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * MarshRandomBench score: 351392032.000000 (351.4M 1967.7%)
 *              uncertainty:   0.6%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * MarshRandomBench score: 339320064.000000 (339.3M 1964.2%)
 *              uncertainty:   1.2%
 * <br>
 * GraalVM Java 20:
 * <br>
 * MarshRandomBench score: 4215795712.000000 (4.216G 2216.2%)
 *              uncertainty:   1.5%
 */
public final class MarshRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		MarshRandom rng = new MarshRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
// TEMPLATE
/*
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
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 *
 * <br>
 * GraalVM Java 20:
 * <br>
 *
 */
