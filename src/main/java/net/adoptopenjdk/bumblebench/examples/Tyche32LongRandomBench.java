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
 * Tyche32LongRandomBench score: 558440960.000000 (558.4M 2014.1%)
 *                   uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Tyche32LongRandomBench score: 612356544.000000 (612.4M 2023.3%)
 *                   uncertainty:   1.1%
 * <br>
 * HotSpot Java 16:
 * <br>
 * Tyche32LongRandomBench score: 621111424.000000 (621.1M 2024.7%)
 *                   uncertainty:   0.6%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * Tyche32LongRandomBench score: 549711168.000000 (549.7M 2012.5%)
 *                   uncertainty:   0.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Tyche32LongRandomBench score: 631144064.000000 (631.1M 2026.3%)
 *                   uncertainty:   2.3%
 */
public final class Tyche32LongRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Tyche32IntRandomBench.Tyche32Random rng = new Tyche32IntRandomBench.Tyche32Random(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
