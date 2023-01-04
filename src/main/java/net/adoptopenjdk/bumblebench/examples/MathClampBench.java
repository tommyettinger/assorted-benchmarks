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

import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MathClampBench score: 19798.328125 (19.80K 989.3%)
 *            uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathClampBench score: 17426.683594 (17.43K 976.6%)
 *            uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MathClampBench score: 97154.210938 (97.15K 1148.4%)
 *            uncertainty:   1.0%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MathClampBench score: 99384.656250 (99.38K 1150.7%)
 *            uncertainty:   0.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * MathClampBench score: 64611.007813 (64.61K 1107.6%)
 *            uncertainty:   1.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * MathClampBench score: 96823.250000 (96.82K 1148.1%)
 *            uncertainty:   1.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * MathClampBench score: 96342.562500 (96.34K 1147.6%)
 *            uncertainty:   0.4%
 */

public final class MathClampBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		LaserRandom rng = new LaserRandom(0x12345678);
		float[] floats = new float[len];
		float sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			for (int j = 0; j < len; j++) {
				floats[j] = rng.nextFloat() * 2f - 0.5f;
			}
			startTimer();
			for (int j = 0; j < len; j++) {
				sum += (Math.min(Math.max(floats[j], 0f), 1f));
			}
		}
		return numIterations;
	}
}
/* Old!
 * New laptop; Windows 10, 10th generation i7, Java 8 HotSpot
 * <br>
 * MathClampBench score: 19287.197266 (19.29K 986.7%)
 *            uncertainty:   0.4%
 * <br>
 * New laptop; Windows 10, 10th generation i7, Java 15 HotSpot
 * <br>
 *  MathClampBench score: 56204.199219 (56.20K 1093.7%)
 *            uncertainty:   1.3%
 * <br>
 * New laptop; Windows 10, 10th generation i7, Java 15 OpenJ9
 * <br>
 *  MathClampBench score: 16562.205078 (16.56K 971.5%)
 *            uncertainty:   2.3%
 */
