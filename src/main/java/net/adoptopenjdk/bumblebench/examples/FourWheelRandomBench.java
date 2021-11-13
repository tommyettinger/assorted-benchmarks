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

import com.github.tommyettinger.ds.support.FourWheelRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * FourWheelRandomBench score: 919712256.000000 (919.7M 2064.0%)
 *                  uncertainty:   1.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FourWheelRandomBench score: 1646497664.000000 (1.646G 2122.2%)
 *                  uncertainty:   2.1%
 * <br>
 * HotSpot Java 16:
 * <br>
 * FourWheelRandomBench score: 1618691712.000000 (1.619G 2120.5%)
 *                  uncertainty:   2.3%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * FourWheelRandomBench score: 1630737408.000000 (1.631G 2121.2%)
 *                  uncertainty:   0.7%
 * <br>
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
