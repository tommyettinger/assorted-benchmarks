
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
 * SplatterRandomBench score: 845915136.000000 (845.9M 2055.6%)
 *                 uncertainty:   1.2%
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
 * SplatterRandomBench score: 1311018240.000000 (1.311G 2099.4%)
 *                 uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SplatterRandomBench score: 1389131776.000000 (1.389G 2105.2%)
 *                 uncertainty:   1.2%
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