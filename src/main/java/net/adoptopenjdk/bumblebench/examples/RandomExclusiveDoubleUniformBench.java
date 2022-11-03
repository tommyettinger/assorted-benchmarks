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
 * RandomExclusiveDoubleUniformBench score: 622569280.000000 (622.6M 2024.9%)
 *                               uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomExclusiveDoubleUniformBench score: 816393152.000000 (816.4M 2052.0%)
 *                               uncertainty:   2.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomExclusiveDoubleUniformBench score: 807589568.000000 (807.6M 2051.0%)
 *                               uncertainty:   1.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomExclusiveDoubleUniformBench score: 729678336.000000 (729.7M 2040.8%)
 *                               uncertainty:   2.0%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomExclusiveDoubleUniformBench score: 820466112.000000 (820.5M 2052.5%)
 *                               uncertainty:   0.8%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomExclusiveDoubleUniformBench score: 863282688.000000 (863.3M 2057.6%)
 *                               uncertainty:   0.4%
 */
public final class RandomExclusiveDoubleUniformBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	private double nextExclusiveDouble() {
		final long bits = rng.nextLong();
		return BitConversion.longBitsToDouble(1022L - Long.numberOfTrailingZeros(bits) << 52 | bits >>> 12);
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextExclusiveDouble() - 0.5;
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
 * RandomExclusiveDoubleUniformBench score: 566665472.000000 (566.7M 2015.5%)
 *                               uncertainty:   1.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomExclusiveDoubleUniformBench score: 370206880.000000 (370.2M 1973.0%)
 *                               uncertainty:   0.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * RandomExclusiveDoubleUniformBench score: 608618560.000000 (608.6M 2022.7%)
 *                               uncertainty:   2.4%
 */
