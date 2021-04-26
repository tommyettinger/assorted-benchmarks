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
import squidpony.squidmath.Coord;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * PointHashRoStBench score: 150855968.000000 (150.9M 1883.2%)
 *                uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PointHashRoStBench score: 147709376.000000 (147.7M 1881.1%)
 *                uncertainty:   2.6%
 * <br>
 * HotSpot Java 16:
 * <br>
 * PointHashRoStBench score: 150656624.000000 (150.7M 1883.1%)
 *                uncertainty:   0.7%
 */
public final class PointHashRoStBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + Coord.rosenbergStrongHashCode((int)sum, (int)(sum>>>32));
		}
		return numIterations;
	}
}
