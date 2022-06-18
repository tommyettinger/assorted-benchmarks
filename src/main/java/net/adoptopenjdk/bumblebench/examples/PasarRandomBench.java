
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
 * PasarRandomBench score: 752126592.000000 (752.1M 2043.8%)
 *              uncertainty:   1.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PasarRandomBench score: 1346214528.000000 (1.346G 2102.1%)
 *              uncertainty:   1.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * PasarRandomBench score: 1129053568.000000 (1.129G 2084.5%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * PasarRandomBench score: 1683674496.000000 (1.684G 2124.4%)
 *              uncertainty:   2.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * PasarRandomBench score: 1846932224.000000 (1.847G 2133.7%)
 *              uncertainty:   2.1%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * PasarRandomBench score: 617233536.000000 (617.2M 2024.1%)
 *              uncertainty:   0.7%
 */
public final class PasarRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		PasarRandom rng = new PasarRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
// old benchmarks
/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * PassRandomBench score: 1058537216.000000 (1.059G 2078.0%)
 *             uncertainty:   1.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PassRandomBench score: 1231660032.000000 (1.232G 2093.2%)
 *             uncertainty:   2.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * PassRandomBench score: 1615123456.000000 (1.615G 2120.3%)
 *             uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * PassRandomBench score: 1619280256.000000 (1.619G 2120.5%)
 *             uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * PassRandomBench score: 1795062016.000000 (1.795G 2130.8%)
 *             uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * PassRandomBench score: 551812288.000000 (551.8M 2012.9%)
 *             uncertainty:   3.2%
 */
