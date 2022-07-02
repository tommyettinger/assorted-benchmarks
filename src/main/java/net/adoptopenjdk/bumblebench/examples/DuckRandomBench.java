
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
 * DuckRandomBench score: 825979264.000000 (826.0M 2053.2%)
 *             uncertainty:   7.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DuckRandomBench score: 1251600256.000000 (1.252G 2094.8%)
 *             uncertainty:   0.7%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DuckRandomBench score: 1661501056.000000 (1.662G 2123.1%)
 *             uncertainty:   1.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DuckRandomBench score: 1652765824.000000 (1.653G 2122.6%)
 *             uncertainty:   1.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DuckRandomBench score: 1592694272.000000 (1.593G 2118.9%)
 *             uncertainty:   1.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * DuckRandomBench score: 643055232.000000 (643.1M 2028.2%)
 *             uncertainty:   2.0%
 */
public final class DuckRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		DuckRandom rng = new DuckRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
