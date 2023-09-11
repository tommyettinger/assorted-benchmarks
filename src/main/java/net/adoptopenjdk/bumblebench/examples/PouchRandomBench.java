
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
 * PouchRandomBench score: 948625472.000000 (948.6M 2067.1%)
 *              uncertainty:   2.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PouchRandomBench score: 1271890304.000000 (1.272G 2096.4%)
 *              uncertainty:   0.8%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * PouchRandomBench score: 1984443648.000000 (1.984G 2140.9%)
 *              uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * PouchRandomBench score: 1867580800.000000 (1.868G 2134.8%)
 *              uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * PouchRandomBench score: 1982399744.000000 (1.982G 2140.8%)
 *              uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * PouchRandomBench score: 632478848.000000 (632.5M 2026.5%)
 *              uncertainty:   1.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * PouchRandomBench score: 1868254464.000000 (1.868G 2134.8%)
 *              uncertainty:   0.9%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * PouchRandomBench score: 1929308800.000000 (1.929G 2138.0%)
 *              uncertainty:   0.7%
 */
public final class PouchRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		PouchRandom rng = new PouchRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
