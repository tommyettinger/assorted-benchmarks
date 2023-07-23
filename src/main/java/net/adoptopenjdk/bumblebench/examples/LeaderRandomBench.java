
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
 * LeaderRandomBench score: 579962688.000000 (580.0M 2017.8%)
 *               uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * LeaderRandomBench score: 855939456.000000 (855.9M 2056.8%)
 *               uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * LeaderRandomBench score: 600495808.000000 (600.5M 2021.3%)
 *               uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * LeaderRandomBench score: 594727552.000000 (594.7M 2020.4%)
 *               uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * LeaderRandomBench score: 649848832.000000 (649.8M 2029.2%)
 *               uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * LeaderRandomBench score: 722567552.000000 (722.6M 2039.8%)
 *               uncertainty:   1.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * LeaderRandomBench score: 600044160.000000 (600.0M 2021.3%)
 *               uncertainty:   0.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * LeaderRandomBench score: 599412544.000000 (599.4M 2021.1%)
 *               uncertainty:   0.2%
 */
public final class LeaderRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		LeaderRandom rng = new LeaderRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
