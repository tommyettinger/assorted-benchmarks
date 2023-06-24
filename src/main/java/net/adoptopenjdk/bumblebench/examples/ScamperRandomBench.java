
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
 * ScamperRandomBench score: 249187072.000000 (249.2M 1933.4%)
 *                uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ScamperRandomBench score: 1537481344.000000 (1.537G 2115.3%)
 *                uncertainty:   1.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * ScamperRandomBench score: 280850592.000000 (280.9M 1945.3%)
 *                uncertainty:   0.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ScamperRandomBench score: 300880448.000000 (300.9M 1952.2%)
 *                uncertainty:   2.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * ScamperRandomBench score: 625543680.000000 (625.5M 2025.4%)
 *                uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * ScamperRandomBench score: 786560832.000000 (786.6M 2048.3%)
 *                uncertainty:   3.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * ScamperRandomBench score: 303764064.000000 (303.8M 1953.2%)
 *                uncertainty:   0.7%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * ScamperRandomBench score: 314405216.000000 (314.4M 1956.6%)
 *                uncertainty:   0.5%
 */
public final class ScamperRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ScamperRandom rng = new ScamperRandom(0x12345678);
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
