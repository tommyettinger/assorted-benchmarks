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
 *  JavaL128X1024MixRandomBench score: 156872864.000000 (156.9M 1887.1%)
 *                         uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * JavaL128X1024MixRandomBench score: 161720608.000000 (161.7M 1890.1%)
 *                        uncertainty:   1.5%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * JavaL128X1024MixRandomBench score: 169710896.000000 (169.7M 1895.0%)
 *                        uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * JavaL128X1024MixRandomBench score: 162129952.000000 (162.1M 1890.4%)
 *                        uncertainty:   0.5%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * JavaL128X1024MixRandomBench score: 162356464.000000 (162.4M 1890.5%)
 *                         uncertainty:   0.4%
 */
public final class JavaL128X1024MixRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("L128X1024MixRandom");
		RandomGenerator rng = factory.create(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
