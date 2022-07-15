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
 * FastIntFloorBench score: 180585.609375 (180.6K 1210.4%)
 *               uncertainty:   5.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FastIntFloorBench score: 118445.015625 (118.4K 1168.2%)
 *               uncertainty:   3.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * FastIntFloorBench score: 163093.734375 (163.1K 1200.2%)
 *               uncertainty:   1.0%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * FastIntFloorBench score: 165872.046875 (165.9K 1201.9%)
 *               uncertainty:   1.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * FastIntFloorBench score: 50037.355469 (50.04K 1082.1%)
 *               uncertainty:   0.8%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * FastIntFloorBench score: 164077.625000 (164.1K 1200.8%)
 *               uncertainty:   2.8%
 */
public final class FastIntFloorBench extends MicroBench {
	private static int fastFloor(final float f) {
		return ((int) (f + 0x1p14) - 0x4000);
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
