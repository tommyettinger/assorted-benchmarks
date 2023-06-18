
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
 * SpoonRandomBench score: 643042240.000000 (643.0M 2028.2%)
 *              uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SpoonRandomBench score: 1811387392.000000 (1.811G 2131.7%)
 *              uncertainty:   0.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SpoonRandomBench score: 699292608.000000 (699.3M 2036.6%)
 *              uncertainty:   1.7%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SpoonRandomBench score: 684971328.000000 (685.0M 2034.5%)
 *              uncertainty:   1.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SpoonRandomBench score: 777483456.000000 (777.5M 2047.2%)
 *              uncertainty:   1.8%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SpoonRandomBench score: 825804224.000000 (825.8M 2053.2%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SpoonRandomBench score: 685578816.000000 (685.6M 2034.6%)
 *              uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SpoonRandomBench score: 671149120.000000 (671.1M 2032.5%)
 *              uncertainty:   0.8%
 */
public final class SpoonRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SpoonRandom rng = new SpoonRandom(0x12345678);
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
 */
