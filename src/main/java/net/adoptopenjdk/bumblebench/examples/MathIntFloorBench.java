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
 * MathIntFloorBench score: 14610.790039 (14.61K 959.0%)
 *               uncertainty:   6.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathIntFloorBench score: 15122.193359 (15.12K 962.4%)
 *               uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MathIntFloorBench score: 147297.156250 (147.3K 1190.0%)
 *               uncertainty:   3.9%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MathIntFloorBench score: 141774.203125 (141.8K 1186.2%)
 *               uncertainty:   1.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * MathIntFloorBench score: 33877.140625 (33.88K 1043.0%)
 *  *               uncertainty:   1.0%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * MathIntFloorBench score: 85292.898438 (85.29K 1135.4%)
 *               uncertainty:   0.7%
 */
public final class MathIntFloorBench extends MicroBench {

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
				sum += (int)Math.floor(floats[j]);
			}
		}
		return numIterations;
	}
}
