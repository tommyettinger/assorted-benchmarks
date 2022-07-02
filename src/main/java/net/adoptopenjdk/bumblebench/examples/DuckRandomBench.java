
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
 * DuckRandomBench score: 1059840640.000000 (1.060G 2078.1%)
 *             uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DuckRandomBench score: 1297522048.000000 (1.298G 2098.4%)
 *             uncertainty:   2.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DuckRandomBench score: 1629735296.000000 (1.630G 2121.2%)
 *             uncertainty:   1.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DuckRandomBench score: 1676132480.000000 (1.676G 2124.0%)
 *             uncertainty:   2.0%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DuckRandomBench score: 1635419136.000000 (1.635G 2121.5%)
 *             uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * DuckRandomBench score: 640765568.000000 (640.8M 2027.8%)
 *             uncertainty:   1.1%
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
