
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
 * LanternRandomBench score: 768771840.000000 (768.8M 2046.0%)
 *                uncertainty:   1.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * LanternRandomBench score: 1688146560.000000 (1.688G 2124.7%)
 *                uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * LanternRandomBench score: 1700290304.000000 (1.700G 2125.4%)
 *                uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * LanternRandomBench score: 2199323392.000000 (2.199G 2151.1%)
 *                uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * LanternRandomBench score: 1692184960.000000 (1.692G 2124.9%)
 *                uncertainty:   0.3%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * LanternRandomBench score: 1874360960.000000 (1.874G 2135.2%)
 *                uncertainty:   0.6%
 */
public final class LanternRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		LanternRandom rng = new LanternRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
