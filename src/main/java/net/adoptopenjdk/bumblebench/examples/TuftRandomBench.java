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
 * TuftRandomBench score: 802520128.000000 (802.5M 2050.3%)
 *             uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * TuftRandomBench score: 1122373504.000000 (1.122G 2083.9%)
 *             uncertainty:   1.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * TuftRandomBench score: 1664916864.000000 (1.665G 2123.3%)
 *             uncertainty:   0.7%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * TuftRandomBench score: 1705876864.000000 (1.706G 2125.7%)
 *             uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * TuftRandomBench score: 1868446464.000000 (1.868G 2134.8%)
 *             uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * TuftRandomBench score: 697465856.000000 (697.5M 2036.3%)
 *             uncertainty:   1.8%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * TuftRandomBench score: 1710880384.000000 (1.711G 2126.0%)
 *             uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * TuftRandomBench score: 1786245376.000000 (1.786G 2130.3%)
 *             uncertainty:   0.4%
 */
public final class TuftRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		TuftRandom rng = new TuftRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
