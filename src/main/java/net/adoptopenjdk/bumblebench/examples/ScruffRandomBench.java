
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

/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * ScruffRandomBench score: 975726976.000000 (975.7M 2069.9%)
              uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ScruffRandomBench score: 1453985152.000000 (1.454G 2109.8%)
              uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * ScruffRandomBench score: 1577533568.000000 (1.578G 2117.9%)
              uncertainty:   1.9%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ScruffRandomBench score: 1495354752.000000 (1.495G 2112.6%)
              uncertainty:   1.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * ScruffRandomBench score: 1840755712.000000 (1.841G 2133.3%)
              uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * ScruffRandomBench score: 689638656.000000 (689.6M 2035.2%)
              uncertainty:   0.3%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * ScruffRandomBench score: 1506329600.000000 (1.506G 2113.3%)
              uncertainty:   2.3%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * ScruffRandomBench score: 1635361536.000000 (1.635G 2121.5%)
              uncertainty:   0.1%
 */
public final class ScruffRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ScruffRandom rng = new ScruffRandom(0x12345678);
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
