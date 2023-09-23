
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

import com.github.tommyettinger.random.Crand64Random;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * Crand64RandomBench score: 807621504.000000 (807.6M 2051.0%)
 *                uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Crand64RandomBench score: 1103328256.000000 (1.103G 2082.2%)
 *                uncertainty:   1.1%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Crand64RandomBench score: 1168047872.000000 (1.168G 2087.9%)
 *                uncertainty:   1.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Crand64RandomBench score: 1156338944.000000 (1.156G 2086.9%)
 *                uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Crand64RandomBench score: 1130963968.000000 (1.131G 2084.6%)
 *                uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * Crand64RandomBench score: 632106240.000000 (632.1M 2026.5%)
 *                uncertainty:   1.9%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * Crand64RandomBench score: 1151480960.000000 (1.151G 2086.4%)
 *                uncertainty:   0.6%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * Crand64RandomBench score: 1286079872.000000 (1.286G 2097.5%)
 *                uncertainty:   0.3%
 */
public final class Crand64RandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Crand64Random rng = new Crand64Random(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
