
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
 * SpritzRandomBench score: 235707360.000000 (235.7M 1927.8%)
 *               uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SpritzRandomBench score: 1540772480.000000 (1.541G 2115.6%)
 *               uncertainty:   0.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SpritzRandomBench score: 280917248.000000 (280.9M 1945.4%)
 *               uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SpritzRandomBench score: 234626880.000000 (234.6M 1927.4%)
 *               uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SpritzRandomBench score: 569773952.000000 (569.8M 2016.1%)
 *               uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SpritzRandomBench score: 787404928.000000 (787.4M 2048.4%)
 *               uncertainty:   1.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SpritzRandomBench score: 234956672.000000 (235.0M 1927.5%)
 *               uncertainty:   6.8%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SpritzRandomBench score: 249663904.000000 (249.7M 1933.6%)
 *               uncertainty:   0.3%
 */
public final class SpritzRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SpritzRandom rng = new SpritzRandom(0x12345678);
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
