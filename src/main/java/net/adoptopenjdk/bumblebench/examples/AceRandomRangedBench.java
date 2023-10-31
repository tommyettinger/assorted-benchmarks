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
 * AceRandomRangedBench score: 624813504.000000 (624.8M 2025.3%)
 *                  uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * AceRandomRangedBench score: 1206266752.000000 (1.206G 2091.1%)
 *                  uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * AceRandomRangedBench score: 1143668480.000000 (1.144G 2085.8%)
 *                  uncertainty:   1.0%
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
public final class AceRandomRangedBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		AceRandom rng = new AceRandom(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt(256);
		return numIterations;
	}
}
