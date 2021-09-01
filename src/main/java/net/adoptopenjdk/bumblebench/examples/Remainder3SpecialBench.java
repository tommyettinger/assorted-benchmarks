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
 * Remainder3SpecialBench score: 45552156.000000 (45.55M 1763.4%)
 *                    uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Remainder3SpecialBench score: 26935970.000000 (26.94M 1710.9%)
 *                    uncertainty:   2.6%
 * HotSpot Java 16:
 * <br>
 * Remainder3SpecialBench score: 46811364.000000 (46.81M 1766.2%)
 *                    uncertainty:   1.0%
 */
public final class Remainder3SpecialBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		int sum = 0;
		int state = 1234567;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			state = Integer.rotateLeft(state, 21) + 0x9E3779B9;
			startTimer();
			sum += (int)(state - ((state * 0x55555556L) >>> 32) * 3);
		}
		return numIterations;
	}
}
