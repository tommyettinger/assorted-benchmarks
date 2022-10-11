
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
 * FinchRandomBench score: 734768064.000000 (734.8M 2041.5%)
 *              uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FinchRandomBench score: 1105062656.000000 (1.105G 2082.3%)
 *              uncertainty:   3.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * FinchRandomBench score: 1549425152.000000 (1.549G 2116.1%)
 *              uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * FinchRandomBench score: 1543856256.000000 (1.544G 2115.8%)
 *              uncertainty:   1.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * FinchRandomBench score: 1742463104.000000 (1.742G 2127.9%)
 *              uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * FinchRandomBench score: 620322176.000000 (620.3M 2024.6%)
 *              uncertainty:   1.5%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * FinchRandomBench score: 1531705088.000000 (1.532G 2115.0%)
 *              uncertainty:   0.5%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * FinchRandomBench score: 1652381696.000000 (1.652G 2122.5%)
 *              uncertainty:   1.5%
 */
public final class FinchRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		FinchRandom rng = new FinchRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
