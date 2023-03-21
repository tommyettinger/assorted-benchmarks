
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
 * WrangleRandomBench score: 192793104.000000 (192.8M 1907.7%)
 *                uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * WrangleRandomBench score: 2153297152.000000 (2.153G 2149.0%)
 *                uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * WrangleRandomBench score: 191379744.000000 (191.4M 1907.0%)
 *                uncertainty:   0.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * WrangleRandomBench score: 184038128.000000 (184.0M 1903.1%)
 *                uncertainty:   1.0%
 * <br>
 * GraalVM Java 17:
 * <br>
 * WrangleRandomBench score: 500041024.000000 (500.0M 2003.0%)
 *                uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * WrangleRandomBench score: 779648512.000000 (779.6M 2047.4%)
 *                uncertainty:   0.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * WrangleRandomBench score: 185156720.000000 (185.2M 1903.7%)
 *                uncertainty:   0.5%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * WrangleRandomBench score: 169645552.000000 (169.6M 1894.9%)
 *                uncertainty:   0.4%
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
