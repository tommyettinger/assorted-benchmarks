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
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MathUtilsClampBench score: 22488.412109 (22.49K 1002.1%)
 *                 uncertainty:   1.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathUtilsClampBench score: 21218.283203 (21.22K 996.3%)
 *                 uncertainty:   1.1%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MathUtilsClampBench score: 22937.181641 (22.94K 1004.1%)
 *                 uncertainty:   1.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MathUtilsClampBench score: 22682.332031 (22.68K 1002.9%)
 *                 uncertainty:   2.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * MathUtilsClampBench score: 23164.152344 (23.16K 1005.0%)
 *                 uncertainty:   0.9%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * MathUtilsClampBench score: 22670.197266 (22.67K 1002.9%)
 *                 uncertainty:   0.9%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * MathUtilsClampBench score: 23669.816406 (23.67K 1007.2%)
 *                 uncertainty:   1.4%
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
/* Old!
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
