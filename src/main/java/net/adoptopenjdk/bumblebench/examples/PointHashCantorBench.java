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
 * PointHashCantorBench score: 178415184.000000 (178.4M 1900.0%)
 *                  uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PointHashCantorBench score: 175123152.000000 (175.1M 1898.1%)
 *                  uncertainty:   0.7%
 * <br>
 * HotSpot Java 16:
 * <br>
 * PointHashCantorBench score: 179857088.000000 (179.9M 1900.8%)
 *                  uncertainty:   2.6%
 */
public final class PointHashCantorBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + Coord.cantorHashCode((int)sum, (int)(sum>>>32));
		}
		return numIterations;
	}
}
