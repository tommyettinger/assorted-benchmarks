
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
 * GraalVM Java 17:
 * <br>
 * WowRandomBench score: 1103560576.000000 (1.104G 2082.2%)
 *            uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * WowRandomBench score: 651132864.000000 (651.1M 2029.4%)
 *            uncertainty:   0.5%
 * <br>
 * GraalVM Java 20:
 * <br>
 * WowRandomBench score: 4274060544.000000 (4.274G 2217.6%)
 *            uncertainty:   0.5%
 * <br>
 * The selection of benchmarks here is different to highlight how wildly diverse the timings are here.
 */
public final class WowRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		WowRandom rng = new WowRandom(0x12345678);
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
