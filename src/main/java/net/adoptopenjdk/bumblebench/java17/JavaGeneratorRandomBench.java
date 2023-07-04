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
 *   JavaGeneratorRandomBench score: 61428652.000000 (61.43M 1793.3%)
 *                      uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * JavaGeneratorRandomBench score: 62129136.000000 (62.13M 1794.5%)
 *                     uncertainty:   0.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * JavaGeneratorRandomBench score: 60815752.000000 (60.82M 1792.3%)
 *                     uncertainty:   0.9%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * JavaGeneratorRandomBench score: 61231188.000000 (61.23M 1793.0%)
 *                     uncertainty:   0.6%
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * JavaGeneratorRandomBench score: 61817620.000000 (61.82M 1794.0%)
 *                      uncertainty:   0.2%
 */
public final class JavaGeneratorRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("Random");
		RandomGenerator rng = factory.create(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
