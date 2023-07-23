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

import com.github.tommyettinger.digital.BitConversion;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RandomExclusiveFloatUniformBench score: 622112320.000000 (622.1M 2024.9%)
 *                              uncertainty:   5.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomExclusiveFloatUniformBench score: 827187840.000000 (827.2M 2053.4%)
 *                              uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomExclusiveFloatUniformBench score: 832965696.000000 (833.0M 2054.1%)
 *                              uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomExclusiveFloatUniformBench score: 729590848.000000 (729.6M 2040.8%)
 *                              uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomExclusiveFloatUniformBench score: 843629824.000000 (843.6M 2055.3%)
 *                              uncertainty:   2.1%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomExclusiveFloatUniformBench score: 759848256.000000 (759.8M 2044.9%)
 *                              uncertainty:   0.3%
 */
public final class RandomExclusiveFloatUniformBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	private float nextExclusiveFloat() {
		final long bits = rng.nextLong();
		return BitConversion.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23 | (int)(bits >>> 41));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0f;
		for (long i = 0; i < numIterations; i++)
			sum += nextExclusiveFloat() - 0.5f;
		return numIterations;
	}
}
//old
/*
 * (This uses WhiskerRandom, so a large part of its performance is related to that generator.)
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
