
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

/** // These benchmarks are out-of-date; it has slowed down since these were measured.
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SplurgeRandomBench score: 147213936.000000 (147.2M 1880.7%)
 *                uncertainty:   1.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SplurgeRandomBench score: 1547223296.000000 (1.547G 2116.0%)
 *                uncertainty:   0.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SplurgeRandomBench score: 147162496.000000 (147.2M 1880.7%)
 *                uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SplurgeRandomBench score: 138004480.000000 (138.0M 1874.3%)
 *                uncertainty:   0.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SplurgeRandomBench score: 443369152.000000 (443.4M 1991.0%)
 *                uncertainty:   1.7%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SplurgeRandomBench score: 775082112.000000 (775.1M 2046.8%)
 *                uncertainty:   0.7%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SplurgeRandomBench score: 133806416.000000 (133.8M 1871.2%)
 *                uncertainty:   0.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SplurgeRandomBench score: 111295680.000000 (111.3M 1852.8%)
 *                uncertainty:   0.5%
 */
public final class SplurgeRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SplurgeRandom rng = new SplurgeRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
// TEMPLATE
/*
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
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
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
