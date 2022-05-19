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
 * With Java 8, HotSpot, on an 8th-gen i7 hexacore mobile processor running Manjaro Linux:
 * <br>
 * FractSignedModBench score: 40115368.000000 (40.12M 1750.7%)
 *                 uncertainty:   0.0%
 * <br>
 * With Java 14, OpenJ9, same hardware:
 * <br>
 * FractSignedModBench score: 43867724.000000 (43.87M 1759.7%)
 *                 uncertainty:   0.1%
 */
public final class FractSignedModBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		LaserRandom rng = new LaserRandom(0x12345678);
		float f;
		float sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			f = rng.nextFloat() - 0.5f;
			startTimer();
			sum += f % 1f;
		}
		return numIterations;
	}
}
