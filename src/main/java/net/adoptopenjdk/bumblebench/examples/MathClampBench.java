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
				sum += (floats[j] = Math.min(Math.max(floats[j], 0f), 1f));
			}
		}
		return numIterations;
	}
}
