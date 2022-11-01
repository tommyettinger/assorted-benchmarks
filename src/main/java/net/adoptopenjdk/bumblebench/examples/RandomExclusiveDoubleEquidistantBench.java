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
 * RandomExclusiveDoubleEquidistantBench score: 348328544.000000 (348.3M 1966.9%)
 *                                   uncertainty:   3.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 465511584.000000 (465.5M 1995.9%)
 *                                   uncertainty:   6.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 462927232.000000 (462.9M 1995.3%)
 *                                   uncertainty:   3.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 340691328.000000 (340.7M 1964.6%)
 *                                   uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 462320288.000000 (462.3M 1995.2%)
 *                                   uncertainty:   2.0%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomExclusiveDoubleEquidistantBench score: 458766592.000000 (458.8M 1994.4%)
 *                                   uncertainty:   2.3%
 */
public final class RandomExclusiveDoubleEquidistantBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	private double nextExclusiveDouble() {
		final long rand = rng.nextLong();
		final long bound = 0x1FFFFFFFFFFFFFL;
		final long randLow = rand & 0xFFFFFFFFL;
		final long boundLow = bound & 0xFFFFFFFFL;
		final long randHigh = (rand >>> 32);
		final long boundHigh = (bound >>> 32);
		return (1L + (randHigh * boundLow >>> 32) + (randLow * boundHigh >>> 32) + randHigh * boundHigh) * 0x1p-53;
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
