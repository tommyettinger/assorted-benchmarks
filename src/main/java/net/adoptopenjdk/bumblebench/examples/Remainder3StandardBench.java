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
 * Remainder3StandardBench score: 43619412.000000 (43.62M 1759.1%)
 *                     uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Remainder3StandardBench score: 28100726.000000 (28.10M 1715.1%)
 *                     uncertainty:   0.6%
 * HotSpot Java 16:
 * <br>
 * Remainder3StandardBench score: 43392188.000000 (43.39M 1758.6%)
 *                     uncertainty:   0.3%
 */
public final class Remainder3StandardBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		int sum = 0;
		int state = 1234567;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			state = Integer.rotateLeft(state, 21) + 0x9E3779B9;
			startTimer();
			sum += ((state % 3) + 3) % 3;
		}
		return numIterations;
	}
}
