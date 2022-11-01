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
 * RandomFloatBench score: 476011712.000000 (476.0M 1998.1%)
 *              uncertainty:   1.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomFloatBench score: 755564288.000000 (755.6M 2044.3%)
 *              uncertainty:   2.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomFloatBench score: 748368320.000000 (748.4M 2043.3%)
 *              uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomFloatBench score: 474476256.000000 (474.5M 1997.8%)
 *              uncertainty:   1.8
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomFloatBench score: 755192640.000000 (755.2M 2044.2%)
 *              uncertainty:   3.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomFloatBench score: 547822656.000000 (547.8M 2012.1%)
 *              uncertainty:   1.5%
 */
public final class RandomFloatBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	private float nextFloat() {
		return (rng.nextLong() >>> 40) * 0x1p-24f;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0f;
		for (long i = 0; i < numIterations; i++)
			sum += nextFloat() - 0.5f;
		return numIterations;
	}
}
