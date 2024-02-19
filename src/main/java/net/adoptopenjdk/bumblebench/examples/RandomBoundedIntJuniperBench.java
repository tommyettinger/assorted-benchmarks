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
 * RandomBoundedIntJuniperBench score: 805329600.000000 (805.3M 2050.7%)
 *                          uncertainty:   2.1%
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
 * RandomBoundedIntJuniperBench score: 1160987776.000000 (1.161G 2087.3%)
 *                          uncertainty:   2.7%
 * <br>
 * GraalVM Java 21:
 * <br>
 * RandomBoundedIntJuniperBench score: 860149440.000000 (860.1M 2057.3%)
 *                          uncertainty:   1.1%
 */
public final class RandomBoundedIntJuniperBench extends MicroBench {

	private final PouchRandom rng = new PouchRandom(0x12345678);
	public int nextUnsignedInt (int bound) {
		return (int)((bound & 0xFFFFFFFFL) * (rng.nextLong() & 0xFFFFFFFFL) >>> 32);
	}

	public int nextInt (int innerBound, int outerBound) {
		return innerBound + nextUnsignedInt(outerBound - innerBound);
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
