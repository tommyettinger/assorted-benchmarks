
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
 * FleetRandomBench score: 709094720.000000 (709.1M 2037.9%)
 *              uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FleetRandomBench score: 769138944.000000 (769.1M 2046.1%)
 *              uncertainty:   0.8%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * FleetRandomBench score: 776237120.000000 (776.2M 2047.0%)
 *              uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * FleetRandomBench score: 795069376.000000 (795.1M 2049.4%)
 *              uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * FleetRandomBench score: 836903936.000000 (836.9M 2054.5%)
 *              uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * FleetRandomBench score: 671085504.000000 (671.1M 2032.4%)
 *              uncertainty:   1.8%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * FleetRandomBench score: 802345792.000000 (802.3M 2050.3%)
 *              uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * FleetRandomBench score: 781356864.000000 (781.4M 2047.7%)
 *              uncertainty:   0.2%
 */
public final class FleetRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		FleetRandom rng = new FleetRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
