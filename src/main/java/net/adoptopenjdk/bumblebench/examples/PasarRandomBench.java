
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
 * PasarRandomBench score: 719005696.000000 (719.0M 2039.3%)
 *              uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PasarRandomBench score: 1200240512.000000 (1.200G 2090.6%)
 *              uncertainty:   1.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * PasarRandomBench score: 1594572544.000000 (1.595G 2119.0%)
 *              uncertainty:   1.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * PasarRandomBench score: 1584360576.000000 (1.584G 2118.3%)
 *              uncertainty:   1.0%
 * <br>
 * GraalVM Java 17:
 * <br>
 * PasarRandomBench score: 1710134528.000000 (1.710G 2126.0%)
 *              uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * PasarRandomBench score: 632945600.000000 (632.9M 2026.6%)
 *              uncertainty:   2.9%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * PasarRandomBench score: 1579731200.000000 (1.580G 2118.1%)
 *              uncertainty:   1.6%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * PasarRandomBench score: 1590802560.000000 (1.591G 2118.8%)
 *              uncertainty:   2.9%
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
