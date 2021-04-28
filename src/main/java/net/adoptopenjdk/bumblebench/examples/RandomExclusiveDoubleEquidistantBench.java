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

import com.github.tommyettinger.ds.support.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * (This uses LaserRandom, so a large part of its performance is related to that generator.)
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 343178304.000000 (343.2M 1965.4%)
 *                                   uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 301639488.000000 (301.6M 1952.5%)
 *                                   uncertainty:   1.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 347644384.000000 (347.6M 1966.7%)
 *                                   uncertainty:   3.3%
 */
public final class RandomExclusiveDoubleEquidistantBench extends MicroBench {

	private final LaserRandom rng = new LaserRandom(0x12345678);

	private double nextExclusiveDouble() {
		return (rng.nextLong(0x1FFFFFFFFFFFFFL) + 1L) * 0x1p-53;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextExclusiveDouble() - 0.5;
		return numIterations;
	}
}
