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
import squidpony.squidmath.IntPointHash;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * PointHashIPHBench score: 241808048.000000 (241.8M 1930.4%)
 *               uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PointHashIPHBench score: 239952256.000000 (240.0M 1929.6%)
 *               uncertainty:   0.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * PointHashIPHBench score: 239156208.000000 (239.2M 1929.3%)
 *               uncertainty:   0.9%
 */
public final class PointHashIPHBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + IntPointHash.hashAll((int)sum, (int)(sum>>>32), 0x1337BEEF);
		}
		return numIterations;
	}
}
