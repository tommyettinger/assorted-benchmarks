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

package net.adoptopenjdk.bumblebench.java17;

import net.adoptopenjdk.bumblebench.core.MicroBench;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * HotSpot Java 17 (Adoptium)
 * <br>
 * JavaL128X256MixRandomBench score: 145961440.000000 (146.0M 1879.9%)
 *                        uncertainty:   3.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium)
 * <br>
 * JavaL128X256MixRandomBench score: 189649856.000000 (189.6M 1906.1%)
 *                        uncertainty:   1.8%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * JavaL128X256MixRandomBench score: 180750352.000000 (180.8M 1901.3%)
 *                        uncertainty:   0.4%
 */
public final class JavaL128X256MixRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("L128X256MixRandom");
		RandomGenerator rng = factory.create(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
