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
 * HotSpot Java 17 (SAP Machine JDK):
 * <br>
 *   JavaXoroshiro128PlusPlusBench score: 918806592.000000 (918.8M 2063.9%)
 *                           uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * JavaXoroshiro128PlusPlusBench score: 885177920.000000 (885.2M 2060.1%)
 *                           uncertainty:   0.6%
 */
public final class JavaXoroshiro128PlusPlusBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("Xoroshiro128PlusPlus");
		RandomGenerator rng = factory.create(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
