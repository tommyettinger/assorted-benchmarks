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
 * BitLongFloorBench score: 81227.546875 (81.23K 1130.5%)
 *               uncertainty:   3.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BitLongFloorBench score: 60118.578125 (60.12K 1100.4%)
 *               uncertainty:   2.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BitLongFloorBench score: 86939.789063 (86.94K 1137.3%)
 *               uncertainty:   6.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BitLongFloorBench score: 74884.953125 (74.88K 1122.4%)
 *               uncertainty:   5.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BitLongFloorBench score: 101252.601563 (101.3K 1152.5%)
 *               uncertainty:   2.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * BitLongFloorBench score: 70282.835938 (70.28K 1116.0%)
 *               uncertainty:   0.4%
 */
public final class BitLongFloorBench extends MicroBench {

	private static long longFloor(final double f) {
		return (long)f + (Double.doubleToLongBits(f + 0.0) >> 63);
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		WhiskerRandom rng = new WhiskerRandom(0x12345678);
		double[] doubles = new double[len];
		long sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			for (int j = 0; j < len; j++) {
				doubles[j] = (rng.nextExclusiveDouble() - 0.5) * 0x1p10;
			}
			startTimer();
			for (int j = 0; j < len; j++) {
				sum += longFloor(doubles[j]);
			}
		}
		return numIterations;
	}
}
