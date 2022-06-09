
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
 * SlashRandomBench score: 941464896.000000 (941.5M 2066.3%)
 *              uncertainty:   1.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SlashRandomBench score: 1120781056.000000 (1.121G 2083.7%)
 *              uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SlashRandomBench score: 1714335104.000000 (1.714G 2126.2%)
 *              uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SlashRandomBench score: 1659698688.000000 (1.660G 2123.0%)
 *              uncertainty:   1.2%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SlashRandomBench score: 1937868288.000000 (1.938G 2138.5%)
 *              uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SlashRandomBench score: 674274624.000000 (674.3M 2032.9%)
 *              uncertainty:   0.8%
 */
public final class SlashRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SlashRandom rng = new SlashRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}

/* Old results
 *
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SlashRandomBench score: 1429009664.000000 (1.429G 2108.0%)
 *              uncertainty:   6.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 *
 * <br>
 * GraalVM Java 16:
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SlashRandomBench score: 1841557376.000000 (1.842G 2133.4%)
 *              uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SlashRandomBench score: 2583982592.000000 (2.584G 2167.3%)
 *              uncertainty:   1.0%
 */
