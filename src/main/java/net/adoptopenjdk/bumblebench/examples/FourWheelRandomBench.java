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

import com.github.tommyettinger.random.FourWheelRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * FourWheelRandomBench score: 903197952.000000 (903.2M 2062.1%)
                 uncertainty:   2.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FourWheelRandomBench score: 1241804288.000000 (1.242G 2094.0%)
                 uncertainty:   1.0%
 * <br>
 * HotSpot Java 16:
 * <br>
 * FourWheelRandomBench score: 1607683840.000000 (1.608G 2119.8%)
 *                  uncertainty:   0.9%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * FourWheelRandomBench score: 1646670080.000000 (1.647G 2122.2%)
 *                  uncertainty:   1.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * FourWheelRandomBench score: 1642684544.000000 (1.643G 2122.0%)
 *                  uncertainty:   0.6%
 */
public final class FourWheelRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		FourWheelRandom rng = new FourWheelRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
