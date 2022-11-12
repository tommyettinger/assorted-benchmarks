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
 * WhiskerRandomBench score: 932268352.000000 (932.3M 2065.3%)
 *                uncertainty:   1.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * WhiskerRandomBench score: 1476923520.000000 (1.477G 2111.3%)
 *                uncertainty:   1.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * WhiskerRandomBench score: 1918452096.000000 (1.918G 2137.5%)
 *                uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * WhiskerRandomBench score: 1851629952.000000 (1.852G 2133.9%)
 *                uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * WhiskerRandomBench score: 1891515776.000000 (1.892G 2136.1%)
 *                uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * WhiskerRandomBench score: 647251008.000000 (647.3M 2028.8%)
 *                uncertainty:   1.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 */
public final class WhiskerRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		WhiskerRandom rng = new WhiskerRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
