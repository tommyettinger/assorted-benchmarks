
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
 * Xoshiro128PlusPlusRandomIntBench score: 668230656.000000 (668.2M 2032.0%)
 *                              uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Xoshiro128PlusPlusRandomIntBench score: 1321271168.000000 (1.321G 2100.2%)
 *                              uncertainty:   1.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Xoshiro128PlusPlusRandomIntBench score: 1020347584.000000 (1.020G 2074.3%)
 *                              uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Xoshiro128PlusPlusRandomIntBench score: 1028572096.000000 (1.029G 2075.1%)
 *                              uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Xoshiro128PlusPlusRandomIntBench score: 964453952.000000 (964.5M 2068.7%)
 *                              uncertainty:   0.6%
 */
public final class Xoshiro128PlusPlusRandomIntBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Xoshiro128PlusPlusRandom rng = new Xoshiro128PlusPlusRandom(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}
