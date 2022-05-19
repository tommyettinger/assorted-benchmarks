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

import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * TricycleRandomBench score: 1043965632.000000 (1.044G 2076.6%)
 *                 uncertainty:   1.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * TricycleRandomBench score: 1111736320.000000 (1.112G 2082.9%)
 *                 uncertainty:   2.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * TricycleRandomBench score: 1322780032.000000 (1.323G 2100.3%)
 *                 uncertainty:   2.5%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * TricycleRandomBench score: 1409696256.000000 (1.410G 2106.7%)
 *                 uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * TricycleRandomBench score: 1362413824.000000 (1.362G 2103.3%)
 *                 uncertainty:   0.7%
 */
public final class TricycleRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		TricycleRandom rng = new TricycleRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
