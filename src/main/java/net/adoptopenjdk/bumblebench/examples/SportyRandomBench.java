
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
 * SportyRandomBench score: 601385984.000000 (601.4M 2021.5%)
 *               uncertainty:   1.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SportyRandomBench score: 1528944768.000000 (1.529G 2114.8%)
 *               uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SportyRandomBench score: 493411776.000000 (493.4M 2001.7%)
 *               uncertainty:   4.9%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SportyRandomBench score: 559604928.000000 (559.6M 2014.3%)
 *               uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SportyRandomBench score: 673767040.000000 (673.8M 2032.8%)
 *               uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SportyRandomBench score: 778007488.000000 (778.0M 2047.2%)
 *               uncertainty:   0.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SportyRandomBench score: 559621952.000000 (559.6M 2014.3%)
 *               uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SportyRandomBench score: 562094656.000000 (562.1M 2014.7%)
 *               uncertainty:   0.9%
 */
public final class SportyRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SportyRandom rng = new SportyRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
// EARLIER RESULTS
// These used 64-bit states B/C/D instead of always-odd states for those, and forced them to odd during generation.
/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SportyRandomBench score: 465400832.000000 (465.4M 1995.8%)
 *               uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SportyRandomBench score: 1535889664.000000 (1.536G 2115.2%)
 *               uncertainty:   4.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SportyRandomBench score: 465851872.000000 (465.9M 1995.9%)
 *               uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SportyRandomBench score: 506651616.000000 (506.7M 2004.3%)
 *               uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SportyRandomBench score: 585388352.000000 (585.4M 2018.8%)
 *               uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SportyRandomBench score: 781138944.000000 (781.1M 2047.6%)
 *               uncertainty:   1.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SportyRandomBench score: 506916384.000000 (506.9M 2004.4%)
 *               uncertainty:   0.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SportyRandomBench score: 507827200.000000 (507.8M 2004.6%)
 *               uncertainty:   2.6%
 */


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
