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

import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * LaserRandomBench score: 954058880.000000 (954.1M 2067.6%)
 *              uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * LaserRandomBench score: 4232752128.000000 (4.233G 2216.6%)
 *              uncertainty:   3.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * LaserRandomBench score: 919491520.000000 (919.5M 2063.9%)
 *              uncertainty:   2.1%
 * <br>
 * 8th-gen i7 hexacore mobile processor running Manjaro Linux:
 * <br>
 * With Java 8, HotSpot:
 * <br>
 * LaserRandomBench score: 858795712.000000 (858.8M 2057.1%)
 *              uncertainty:   0.0%
 * <br>
 * With Java 14, OpenJ9:
 * <br>
 * LaserRandomBench score: 3261202688.000000 (3.261G 2190.5%)
 *              uncertainty:   0.0%
 * <br>
 * It should be stressed that with OpenJ9, this generates 4.2 billion longs a second.
 * On a laptop.
 */
public final class LaserRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		LaserRandom rng = new LaserRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
