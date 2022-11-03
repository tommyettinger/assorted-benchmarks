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
 * RandomDoubleBench score: 343553152.000000 (343.6M 1965.5%)
 *               uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomDoubleBench score: 479217792.000000 (479.2M 1998.8%)
 *               uncertainty:   1.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomDoubleBench score: 691251392.000000 (691.3M 2035.4%)
 *               uncertainty:   1.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomDoubleBench score: 681904832.000000 (681.9M 2034.0%)
 *               uncertainty:   1.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomDoubleBench score: 336601504.000000 (336.6M 1963.4%)
 *               uncertainty:   1.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * RandomDoubleBench score: 492883744.000000 (492.9M 2001.6%)
 *               uncertainty:   0.5%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomDoubleBench score: 677875008.000000 (677.9M 2033.4%)
 *               uncertainty:   1.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomDoubleBench score: 686081664.000000 (686.1M 2034.7%)
 *               uncertainty:   0.2%
 */
public final class RandomDoubleBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	private double nextDouble() {
		return (rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextDouble() - 0.5;
		return numIterations;
	}
}
