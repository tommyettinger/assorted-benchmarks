
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
 * WrangleRandomBench score: 120661600.000000 (120.7M 1860.9%)
 *                uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * WrangleRandomBench score: 2148079616.000000 (2.148G 2148.8%)
 *                uncertainty:   0.8%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * WrangleRandomBench score: 123785352.000000 (123.8M 1863.4%)
 *                uncertainty:   0.7%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * WrangleRandomBench score: 123263376.000000 (123.3M 1863.0%)
 *                uncertainty:   0.2%
 * <br>
 * GraalVM Java 17:
 * <br>
 * WrangleRandomBench score: 310838336.000000 (310.8M 1955.5%)
 *                uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * WrangleRandomBench score: 764025600.000000 (764.0M 2045.4%)
 *                uncertainty:   3.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * WrangleRandomBench score: 124318344.000000 (124.3M 1863.8%)
 *                uncertainty:   1.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * WrangleRandomBench score: 121676064.000000 (121.7M 1861.7%)
 *                uncertainty:   0.7%
 */
public final class WrangleRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		WrangleRandom rng = new WrangleRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
