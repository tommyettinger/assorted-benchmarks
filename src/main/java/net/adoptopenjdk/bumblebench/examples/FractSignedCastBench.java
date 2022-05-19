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
 * FractSignedCastBench score: 40440004.000000 (40.44M 1751.5%)
 *                  uncertainty:   0.0%
 * <br>
 * With Java 14, OpenJ9, same hardware:
 * <br>
 * FractSignedCastBench score: 46243044.000000 (46.24M 1764.9%)
 *                  uncertainty:   0.2%
 */
public final class FractSignedCastBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		LaserRandom rng = new LaserRandom(0x12345678);
		float f;
		float sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			f = rng.nextFloat() - 0.5f;
			startTimer();
			sum += f - (int)f;
		}
		return numIterations;
	}
}
