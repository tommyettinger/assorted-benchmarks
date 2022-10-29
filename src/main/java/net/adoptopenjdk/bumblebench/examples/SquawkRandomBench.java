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
 * SquawkRandomBench score: 891395904.000000 (891.4M 2060.8%)
 *               uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SquawkRandomBench score: 1226241152.000000 (1.226G 2092.7%)
 *               uncertainty:   3.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SquawkRandomBench score: 1513758976.000000 (1.514G 2113.8%)
 *               uncertainty:   0.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SquawkRandomBench score: 1507451520.000000 (1.507G 2113.4%)
 *               uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SquawkRandomBench score: 1516716544.000000 (1.517G 2114.0%)
 *               uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SquawkRandomBench score: 647534464.000000 (647.5M 2028.9%)
 *               uncertainty:   5.0%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SquawkRandomBench score: 1505017984.000000 (1.505G 2113.2%)
 *               uncertainty:   1.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SquawkRandomBench score: 1493098624.000000 (1.493G 2112.4%)
 *               uncertainty:   0.3%
 */
public final class SquawkRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SquawkRandom rng = new SquawkRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
