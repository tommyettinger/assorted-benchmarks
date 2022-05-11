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
 *
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 *
 * <br>
 * GraalVM Java 16:
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * TroubleRandomBench score: 494590496.000000 (494.6M 2001.9%)
 *                uncertainty:   1.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * TroubleRandomBench score: 721080704.000000 (721.1M 2039.6%)
 *                uncertainty:   0.4%
 * <br>
 * Not fast enough on two major JDKs to warrant further testing.
 */
public final class TroubleRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		TroubleRandom rng = new TroubleRandom(0x12345678, 0x87654321);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}