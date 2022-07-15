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
 * FastBranchIntFloorBench score: 26005.845703 (26.01K 1016.6%)
 *                     uncertainty:   1.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FastBranchIntFloorBench score: 24146.093750 (24.15K 1009.2%)
 *                     uncertainty:   3.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * FastBranchIntFloorBench score: 26833.888672 (26.83K 1019.7%)
 *                     uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * FastBranchIntFloorBench score: 24349.373047 (24.35K 1010.0%)
 *                     uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * FastBranchIntFloorBench score: 24141.306641 (24.14K 1009.2%)
 *                     uncertainty:   2.5%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * FastBranchIntFloorBench score: 26090.660156 (26.09K 1016.9%)
 *                     uncertainty:   0.9%
 */
public final class FastBranchIntFloorBench extends MicroBench {
	private static int fastFloor(final float f) {
		return f < 0 ? ((int) (f - 0x1p14) + 0x4000) : ((int) (f + 0x1p14) - 0x4000);
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
				sum += fastFloor(floats[j]);
			}
		}
		return numIterations;
	}
}
