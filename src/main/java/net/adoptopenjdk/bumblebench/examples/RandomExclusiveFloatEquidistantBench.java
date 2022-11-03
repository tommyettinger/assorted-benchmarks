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
 * RandomExclusiveFloatEquidistantBench score: 344052384.000000 (344.1M 1965.6%)
 *                                  uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 411823456.000000 (411.8M 1983.6%)
 *                                  uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 408421600.000000 (408.4M 1982.8%)
 *                                  uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 337100736.000000 (337.1M 1963.6%)
 *                                  uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 411116704.000000 (411.1M 1983.4%)
 *                                  uncertainty:   0.6%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 415448512.000000 (415.4M 1984.5%)
 *                                  uncertainty:   0.3
 */
public final class RandomExclusiveFloatEquidistantBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	private float nextExclusiveFloat() {
		return ((int)(0xFFFFFF * (rng.nextLong() & 0xFFFFFFFFL) >> 32) + 1) * 0x1p-24f;
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
 * RandomExclusiveFloatEquidistantBench score: 345743712.000000 (345.7M 1966.1%)
 *                                  uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 465666560.000000 (465.7M 1995.9%)
 *                                  uncertainty:   1.6%
 * <br>
 * HotSpot Java 16:
 * <br>
 * RandomExclusiveFloatEquidistantBench score: 509840832.000000 (509.8M 2005.0%)
 *                                  uncertainty:   2.0%
 */
