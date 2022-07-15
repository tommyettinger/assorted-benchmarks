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
 * BranchIntFloorBench score: 114047.609375 (114.0K 1164.4%)
 *                 uncertainty:   1.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BranchIntFloorBench score: 18886.599609 (18.89K 984.6%)
 *                 uncertainty:   1.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BranchIntFloorBench score: 103604.968750 (103.6K 1154.8%)
 *                 uncertainty:   2.0%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BranchIntFloorBench score: 101921.007813 (101.9K 1153.2%)
 *                 uncertainty:   0.2%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BranchIntFloorBench score: 19342.812500 (19.34K 987.0%)
 *                 uncertainty:   0.7%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * BranchIntFloorBench score: 100594.539063 (100.6K 1151.9%)
 *                 uncertainty:   0.8%
 */
public final class BranchIntFloorBench extends MicroBench {
	private static int intFloor(final float f) {
		final int t = (int) f;
		return f < t ? t - 1 : t;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		WhiskerRandom rng = new WhiskerRandom(0x12345678);
		float[] floats = new float[len];
		int sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			for (int j = 0; j < len; j++) {
				floats[j] = (rng.nextExclusiveFloat() - 0.5f) * 0x1p10f;
			}
			startTimer();
			for (int j = 0; j < len; j++) {
				sum += intFloor(floats[j]);
			}
		}
		return numIterations;
	}
}
