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
 * PointHashXoRoBench score: 314788576.000000 (314.8M 1956.7%)
 *                uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PointHashXoRoBench score: 317144928.000000 (317.1M 1957.5%)
 *                uncertainty:   0.6%
 * <br>
 * HotSpot Java 16:
 * <br>
 * PointHashXoRoBench score: 320862784.000000 (320.9M 1958.7%)
 *                uncertainty:   0.2%
 */
public final class PointHashXoRoBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + Coord.xoroHashCode((int)sum, (int)(sum>>>32));
		}
		return numIterations;
	}
}
