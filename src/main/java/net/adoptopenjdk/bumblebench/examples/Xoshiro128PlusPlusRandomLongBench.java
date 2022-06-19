
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
 * Xoshiro128PlusPlusRandomLongBench score: 510008736.000000 (510.0M 2005.0%)
 *                               uncertainty:   0.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Xoshiro128PlusPlusRandomLongBench score: 1318554240.000000 (1.319G 2100.0%)
 *                               uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Xoshiro128PlusPlusRandomLongBench score: 690813952.000000 (690.8M 2035.3%)
 *                               uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Xoshiro128PlusPlusRandomLongBench score: 687187520.000000 (687.2M 2034.8%)
 *                               uncertainty:   2.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Xoshiro128PlusPlusRandomLongBench score: 665298432.000000 (665.3M 2031.6%)
 *                               uncertainty:   0.5%
 */
public final class Xoshiro128PlusPlusRandomLongBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Xoshiro128PlusPlusRandom rng = new Xoshiro128PlusPlusRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
