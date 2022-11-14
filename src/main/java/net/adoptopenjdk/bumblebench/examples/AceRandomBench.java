
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
 * AceRandomBench score: 695007488.000000 (695.0M 2035.9%)
 *            uncertainty:   3.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * AceRandomBench score: 1046176256.000000 (1.046G 2076.8%)
 *            uncertainty:   3.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * AceRandomBench score: 1596342272.000000 (1.596G 2119.1%)
 *            uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * AceRandomBench score: 1870299520.000000 (1.870G 2134.9%)
 *            uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * AceRandomBench score: 1585510784.000000 (1.586G 2118.4%)
 *            uncertainty:   1.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * AceRandomBench score: 1752449536.000000 (1.752G 2128.4%)
 *            uncertainty:   1.6%
 */
public final class AceRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		AceRandom rng = new AceRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
