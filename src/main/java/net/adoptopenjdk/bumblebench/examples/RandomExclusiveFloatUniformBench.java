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

import com.github.tommyettinger.ds.support.BitConversion;
import com.github.tommyettinger.ds.support.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * (This uses LaserRandom, so a large part of its performance is related to that generator.)
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RandomExclusiveFloatUniformBench score: 585160448.000000 (585.2M 2018.7%)
 *                              uncertainty:   2.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomExclusiveFloatUniformBench score: 397202208.000000 (397.2M 1980.0%)
 *                              uncertainty:   0.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * RandomExclusiveFloatUniformBench score: 617061312.000000 (617.1M 2024.0%)
 *                              uncertainty:   0.6%
 */
public final class RandomExclusiveFloatUniformBench extends MicroBench {

	private final LaserRandom rng = new LaserRandom(0x12345678);

	private float nextExclusiveFloat() {
		final long bits = rng.nextLong();
		return BitConversion.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
				| (int)(bits >>> 41));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0f;
		for (long i = 0; i < numIterations; i++)
			sum += nextExclusiveFloat() - 0.5f;
		return numIterations;
	}
}
