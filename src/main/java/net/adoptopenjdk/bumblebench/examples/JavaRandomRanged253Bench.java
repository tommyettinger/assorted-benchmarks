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

import java.util.Random;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * JavaRandomRanged253Bench score: 123079216.000000 (123.1M 1862.8%)
 *                      uncertainty:   1.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * JavaRandomRanged253Bench score: 122315224.000000 (122.3M 1862.2%)
 *                      uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * JavaRandomRanged253Bench score: 120335456.000000 (120.3M 1860.6%)
 *                      uncertainty:   0.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * OpenJ9 Java 17 (Semeru):
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
 */
public final class JavaRandomRanged253Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Random rng = new Random(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt(253);
		return numIterations;
	}
}
