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

import net.adoptopenjdk.bumblebench.core.MiniBench;
import squidpony.squidmath.LightRNG;

/**
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * Matrix4RotArcBench score: 58535096.000000 (58.54M 1788.5%)
 *                uncertainty:   0.8%
 */
public final class Matrix4RotArcBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		final float[] values = new float[16], v2 = new float[16];
		LightRNG rng = new LightRNG(123456789L);
		for (long i = 0; i < numLoops; i++) {
			for (int r = 0; r < 16; r++) {
				v2[r] = rng.nextFloat();
			}
			for (int j = 0; j < numIterationsPerLoop; j++) {
				for (int r = 0; r < 16; r++) {
					values[r] = rng.nextFloat();
				}
				startTimer();
				Matrix4.rot(values, v2);
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}
