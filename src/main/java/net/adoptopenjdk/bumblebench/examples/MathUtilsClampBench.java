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

import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * New laptop; Windows 10, 10th generation i7, Java 8 HotSpot
 * <br>
 * MathUtilsClampBench score: 22799.572266 (22.80K 1003.4%)
 *                 uncertainty:   2.0%
 * <br>
 * New laptop; Windows 10, 10th generation i7, Java 15 HotSpot
 * <br>
 *  MathUtilsClampBench score: 22622.697266 (22.62K 1002.7%)
 *                 uncertainty:   0.5%
 * <br>
 * New laptop; Windows 10, 10th generation i7, Java 15 OpenJ9
 * <br>
 *  MathUtilsClampBench score: 21892.371094 (21.89K 999.4%)
 *                 uncertainty:   1.7%
 */
public final class MathUtilsClampBench extends MicroBench {

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
				sum += (floats[j] = MathUtils.clamp(floats[j], 0f, 1f));
			}
		}
		return numIterations;
	}
}
