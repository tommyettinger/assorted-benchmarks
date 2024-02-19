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
 * RandomBoundedIntTephriumBench score: 801106432.000000 (801.1M 2050.2%)
 *                           uncertainty:   1.5%
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
 * RandomBoundedIntTephriumBench score: 1164489088.000000 (1.164G 2087.6%)
 *                           uncertainty:   1.6%
 * <br>
 * GraalVM Java 21:
 * <br>
 * RandomBoundedIntTephriumBench score: 848035840.000000 (848.0M 2055.8%)
 *                           uncertainty:   1.5%
 */
public final class RandomBoundedIntTephriumBench extends MicroBench {

	private final PouchRandom rng = new PouchRandom(0x12345678);

	private int nextInt(int inner, int outer) {
		// Same as nextLong(long, long) but for integers
		final int rDiff = outer - inner;
		final int rLow = rDiff & 0xFFFF;
		final int rHigh = (rDiff >>> 16);

		final int x = rng.nextInt();
		final int xLow = x & 0xFFFF;
		final int xHigh = (x >>> 16);

		final int xtimesrange = xHigh * rHigh +    // HH
				(xHigh * rLow >>> 16) +          // HL
				(xLow * rHigh >>> 16);           // LH
		//        (xLow * rLow >>> 32)             // LL (ignored since its always 0)
		return inner + xtimesrange;
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
