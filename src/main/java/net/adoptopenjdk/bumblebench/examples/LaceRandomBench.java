
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
 * LaceRandomBench score: 741275648.000000 (741.3M 2042.4%)
 *             uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * LaceRandomBench score: 1204668544.000000 (1.205G 2090.9%)
 *             uncertainty:   1.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * LaceRandomBench score: 1764408832.000000 (1.764G 2129.1%)
 *             uncertainty:   1.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * LaceRandomBench score: 1535541376.000000 (1.536G 2115.2%)
 *             uncertainty:   3.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * LaceRandomBench score: 1891062528.000000 (1.891G 2136.0%)
 *             uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * LaceRandomBench score: 655184640.000000 (655.2M 2030.0%)
 *             uncertainty:   0.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * LaceRandomBench score: 1530879488.000000 (1.531G 2114.9%)
 *             uncertainty:   0.5%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * LaceRandomBench score: 1838544640.000000 (1.839G 2133.2%)
 *             uncertainty:   1.0%
 */
public final class LaceRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		LaceRandom rng = new LaceRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
