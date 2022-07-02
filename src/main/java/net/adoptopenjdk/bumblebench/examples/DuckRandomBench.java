
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
 * DuckRandomBench score: 1114239744.000000 (1.114G 2083.1%)
 *             uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DuckRandomBench score: 1146913536.000000 (1.147G 2086.0%)
 *             uncertainty:   0.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DuckRandomBench score: 1608280576.000000 (1.608G 2119.8%)
 *             uncertainty:   0.6%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DuckRandomBench score: 1668125312.000000 (1.668G 2123.5%)
 *             uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DuckRandomBench score: 1644705024.000000 (1.645G 2122.1%)
 *             uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * DuckRandomBench score: 602653248.000000 (602.7M 2021.7%)
 *             uncertainty:   1.1%
 */
public final class DuckRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		DuckRandom rng = new DuckRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
