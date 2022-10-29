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
 * ScreechRandomBench score: 810018560.000000 (810.0M 2051.3%)
 *                uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * ScreechRandomBench score: 1394626560.000000 (1.395G 2105.6%)
 *                uncertainty:   1.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ScreechRandomBench score: 1548654592.000000 (1.549G 2116.1%)
 *                uncertainty:   2.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * ScreechRandomBench score: 1952306176.000000 (1.952G 2139.2%)
 *                uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * ScreechRandomBench score: 1536485248.000000 (1.536G 2115.3%)
 *                uncertainty:   0.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * ScreechRandomBench score: 1879403648.000000 (1.879G 2135.4%)
 *                uncertainty:   0.9%
 */
public final class ScreechRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ScreechRandom rng = new ScreechRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
