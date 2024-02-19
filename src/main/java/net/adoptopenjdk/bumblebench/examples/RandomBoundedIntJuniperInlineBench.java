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

import com.github.tommyettinger.random.PouchRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * RandomBoundedIntJuniperInlineBench score: 804720768.000000 (804.7M 2050.6%)
 *                                uncertainty:   2.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 *
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 * RandomBoundedIntJuniperInlineBench score: 1171187712.000000 (1.171G 2088.1%)
 *                                uncertainty:   1.7%
 * <br>
 * GraalVM Java 21:
 * <br>
 * RandomBoundedIntJuniperInlineBench score: 852286464.000000 (852.3M 2056.3%)
 *                                uncertainty:   4.0%
 */
public final class RandomBoundedIntJuniperInlineBench extends MicroBench {

	private final PouchRandom rng = new PouchRandom(0x12345678);

	public int nextInt (int innerBound, int outerBound) {
		return innerBound + (int)((outerBound - innerBound & 0xFFFFFFFFL) * (rng.nextLong() & 0xFFFFFFFFL) >>> 32);
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += nextInt(-10000, 10001);
		return numIterations;
	}
}

// TEMPLATE
/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 *
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 *
 * <br>
 * GraalVM Java 21:
 * <br>
 *
 */
