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
 * RandomInclusiveDoubleScalingBench score: 214323072.000000 (214.3M 1918.3%)
 *                               uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * RandomInclusiveDoubleScalingBench score: 353081856.000000 (353.1M 1968.2%)
 *                               uncertainty:   5.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * RandomInclusiveDoubleScalingBench score: 206904560.000000 (206.9M 1914.8%)
 *                               uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * RandomInclusiveDoubleScalingBench score: 209648480.000000 (209.6M 1916.1%)
 *                               uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * RandomInclusiveDoubleScalingBench score: 213794032.000000 (213.8M 1918.1%)
 *                               uncertainty:   2.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * RandomInclusiveDoubleScalingBench score: 207257632.000000 (207.3M 1914.9%)
 *                               uncertainty:   1.7%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * RandomInclusiveDoubleScalingBench score: 209437952.000000 (209.4M 1916.0%)
 *                               uncertainty:   0.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * RandomInclusiveDoubleScalingBench score: 208392288.000000 (208.4M 1915.5%)
 *                               uncertainty:   1.0%
 */
public final class RandomInclusiveDoubleScalingBench extends MicroBench {

	private final WhiskerRandom rng = new WhiskerRandom(0x12345678);

	/**
	 * Based loosely on <a href="https://github.com/godotengine/godot/blob/926429392a73a0c2261bc4ed4503c99025842d7c/core/math/random_pcg.h#L90-L126">this Godot engine code</a>.
	 * This version gets a reliable count of random numbers from the generator (one long, always) instead of how Godot
	 * usually gets 3 ints, rarely gets one int, and can get two ints if CLZ32 is not available. There's probably some
	 * tiny amount of bias here from reusing {@code bits} for both the significand, and the exponent (based on its
	 * trailing rather than leading zeroes).
	 * @return a double between 0 and 1, both inclusive
	 */
	public double nextInclusiveDouble () {
		final long bits = rng.nextLong();
		return 1.0 - Math.scalb(bits + 0x1p63, -64 - Long.numberOfTrailingZeros(bits));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextInclusiveDouble() - 0.5;
		return numIterations;
	}
}
