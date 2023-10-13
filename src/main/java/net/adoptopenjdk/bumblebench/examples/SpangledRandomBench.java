
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
 * SpangledRandomBench score: 200848464.000000 (200.8M 1911.8%)
 *                 uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SpangledRandomBench score: 155634976.000000 (155.6M 1886.3%)
 *                 uncertainty:   3.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SpangledRandomBench score: 168964448.000000 (169.0M 1894.5%)
 *                 uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SpangledRandomBench score: 171592192.000000 (171.6M 1896.1%)
 *                 uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SpangledRandomBench score: 306976864.000000 (307.0M 1954.2%)
 *                 uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SpangledRandomBench score: 211730448.000000 (211.7M 1917.1%)
 *                 uncertainty:   2.0%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * SpangledRandomBench score: 169761328.000000 (169.8M 1895.0%)
 *                 uncertainty:   0.6%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * SpangledRandomBench score: 183720944.000000 (183.7M 1902.9%)
 *                 uncertainty:   0.6%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * SpangledRandomBench score: 184335680.000000 (184.3M 1903.2%)
 *                 uncertainty:   1.0%
 * <br>
 * GraalVM Java 20:
 * <br>
 * SpangledRandomBench score: 4216506880.000000 (4.217G 2216.2%)
 *                 uncertainty:   0.3%
 */
public final class SpangledRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SpangledRandom rng = new SpangledRandom(0x12345678);
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
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 *
 * <br>
 * GraalVM Java 20:
 * <br>
 *
 */
