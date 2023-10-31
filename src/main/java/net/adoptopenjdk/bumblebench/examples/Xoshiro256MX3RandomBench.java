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

import com.github.tommyettinger.random.Xoshiro256MX3Random;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * Xoshiro256MX3RandomBench score: 512868960.000000 (512.9M 2005.6%)
 *                      uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Xoshiro256MX3RandomBench score: 1311993472.000000 (1.312G 2099.5%)
 *                      uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Xoshiro256MX3RandomBench score: 552611264.000000 (552.6M 2013.0%)
 *                      uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Xoshiro256MX3RandomBench score: 553841664.000000 (553.8M 2013.2%)
 *                      uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Xoshiro256MX3RandomBench score: 567142656.000000 (567.1M 2015.6%)
 *                      uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * Xoshiro256MX3RandomBench score: 1320362496.000000 (1.320G 2100.1%)
 *                      uncertainty:   0.9%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * Xoshiro256MX3RandomBench score: 550733952.000000 (550.7M 2012.7%)
 *                      uncertainty:   0.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * Xoshiro256MX3RandomBench score: 554309760.000000 (554.3M 2013.3%)
 *                      uncertainty:   0.4%
 */
public final class Xoshiro256MX3RandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Xoshiro256MX3Random rng = new Xoshiro256MX3Random(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
