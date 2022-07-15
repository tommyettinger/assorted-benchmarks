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
 * BranchLongFloorBench score: 102043.625000 (102.0K 1153.3%)
 *                  uncertainty:   4.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BranchLongFloorBench score: 16933.257813 (16.93K 973.7%)
 *                  uncertainty:   0.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BranchLongFloorBench score: 102450.460938 (102.5K 1153.7%)
 *                  uncertainty:   3.9%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BranchLongFloorBench score: 90206.835938 (90.21K 1141.0%)
 *                  uncertainty:   2.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BranchLongFloorBench score: 18808.496094 (18.81K 984.2%)
 *                  uncertainty:   3.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * BranchLongFloorBench score: 99630.851563 (99.63K 1150.9%)
 *                  uncertainty:   1.6%
 */
public final class BranchLongFloorBench extends MicroBench {

	private static long longFloor(final double f) {
		final long t = (long) f;
		return f < t ? t - 1L : t;
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
