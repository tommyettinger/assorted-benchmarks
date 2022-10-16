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
 * RandomInclusiveDoubleRangedBench score: 349916640.000000 (349.9M 1967.3%)
 *                              uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomInclusiveDoubleRangedBench score: 487099904.000000 (487.1M 2000.4%)
 *                              uncertainty:   0.9%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomInclusiveDoubleRangedBench score: 825217024.000000 (825.2M 2053.1%)
 *                              uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomInclusiveDoubleRangedBench score: 806498304.000000 (806.5M 2050.8%)
 *                              uncertainty:   1.0%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomInclusiveDoubleRangedBench score: 337873632.000000 (337.9M 1963.8%)
 *                              uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * RandomInclusiveDoubleRangedBench score: 480166464.000000 (480.2M 1999.0%)
 *                              uncertainty:   0.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomInclusiveDoubleRangedBench score: 815040256.000000 (815.0M 2051.9%)
 *                              uncertainty:   0.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomInclusiveDoubleRangedBench score: 833790336.000000 (833.8M 2054.1%)
 *                              uncertainty:   1.7%
 */
public final class RandomInclusiveDoubleRangedBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	public double nextInclusiveDouble () {
//		return nextLong(0x20000000000001L) * 0x1p-53;
		final long rand = rng.nextLong();
		final long bound = 0x20000000000001L;
		final long randLow = rand & 0xFFFFFFFFL;
		final long randHigh = (rand >>> 32);
		final long boundHigh = (bound >>> 32);
		return ((randLow * boundHigh >>> 32) + randHigh * boundHigh) * 0x1p-53;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextInclusiveDouble() - 0.5;
		return numIterations;
	}
}
