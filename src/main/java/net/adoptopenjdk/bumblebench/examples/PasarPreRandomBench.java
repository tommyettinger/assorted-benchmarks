
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
 * PasarPreRandomBench score: 725203008.000000 (725.2M 2040.2%)
 *              uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PasarPreRandomBench score: 1274963584.000000 (1.275G 2096.6%)
 *              uncertainty:   3.7%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * PasarPreRandomBench score: 1609099904.000000 (1.609G 2119.9%)
 *              uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * PasarPreRandomBench score: 1556022400.000000 (1.556G 2116.5%)
 *              uncertainty:   1.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * PasarPreRandomBench score: 1709806464.000000 (1.710G 2126.0%)
 *              uncertainty:   1.7%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * PasarPreRandomBench score: 628034240.000000 (628.0M 2025.8%)
 *              uncertainty:   3.0%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * PasarPreRandomBench score: 1556091264.000000 (1.556G 2116.5%)
 *              uncertainty:   1.9%
 */
public final class PasarPreRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		PasarPreRandom rng = new PasarPreRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
