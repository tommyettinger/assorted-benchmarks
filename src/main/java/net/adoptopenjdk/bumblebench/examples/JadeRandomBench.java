
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
 * JadeRandomBench score: 447084352.000000 (447.1M 1991.8%)
 *             uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * JadeRandomBench score: 2158154496.000000 (2.158G 2149.3%)
 *             uncertainty:   0.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * JadeRandomBench score: 468492192.000000 (468.5M 1996.5%)
 *             uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * JadeRandomBench score: 441885472.000000 (441.9M 1990.7%)
 *             uncertainty:   1.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * JadeRandomBench score: 674969664.000000 (675.0M 2033.0%)
 *             uncertainty:   0.1%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * JadeRandomBench score: 777750592.000000 (777.8M 2047.2%)
 *             uncertainty:   0.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * JadeRandomBench score: 449393312.000000 (449.4M 1992.3%)
 *             uncertainty:   1.0%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * JadeRandomBench score: 482977984.000000 (483.0M 1999.5%)
 *             uncertainty:   1.0%
 */
public final class JadeRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		JadeRandom rng = new JadeRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
