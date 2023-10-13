
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
 * StaunchRandomBench score: 210687760.000000 (210.7M 1916.6%)
 *                uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * StaunchRandomBench score: 1807417600.000000 (1.807G 2131.5%)
 *               uncertainty:   3.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 *  StaunchRandomBench score: 243313952.000000 (243.3M 1931.0%)
 *                uncertainty:   1.0%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * StaunchRandomBench score: 248051392.000000 (248.1M 1932.9%)
 *                uncertainty:   1.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * StaunchRandomBench score: 610656384.000000 (610.7M 2023.0%)
 *                uncertainty:   2.1%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * StaunchRandomBench score: 831231296.000000 (831.2M 2053.8%)
 *               uncertainty:   1.0%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * StaunchRandomBench score: 247760144.000000 (247.8M 1932.8%)
 *                uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * StaunchRandomBench score: 243001280.000000 (243.0M 1930.9%)
 *                uncertainty:   0.8%
 */
public final class StaunchRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		StaunchRandom rng = new StaunchRandom(0x12345678);
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
