
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
 * SplatterRandomBench score: 841989824.000000 (842.0M 2055.1%)
 *                 uncertainty:   0.6%
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
 * SplatterRandomBench score: 1229051008.000000 (1.229G 2093.0%)
 *                 uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SplatterRandomBench score: 1566795264.000000 (1.567G 2117.2%)
 *                 uncertainty:   0.8%
 */
public final class SplatterRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SplatterRandom rng = new SplatterRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}