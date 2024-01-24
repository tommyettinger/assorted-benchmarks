
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
 * Rawr32RandomLongBench score: 207547856.000000 (207.5M 1915.1%)
 *                   uncertainty:   2.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Rawr32RandomLongBench score: 256294064.000000 (256.3M 1936.2%)
 *                   uncertainty:   0.2%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Rawr32RandomLongBench score: 370989216.000000 (371.0M 1973.2%)
 *                   uncertainty:   1.6%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * Rawr32RandomLongBench score: 236154560.000000 (236.2M 1928.0%)
 *                   uncertainty:   0.8%
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 * Rawr32RandomLongBench score: 233988512.000000 (234.0M 1927.1%)
 *                   uncertainty:   0.8%
 * <br>
 * GraalVM Java 21:
 * <br>
 * Rawr32RandomLongBench score: 369222944.000000 (369.2M 1972.7%)
 *                   uncertainty:   0.4%
 */
public final class Rawr32RandomLongBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Rawr32Random rng = new Rawr32Random(0x12345678);
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
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 *
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 *
 * <br>
 * GraalVM Java 21:
 * <br>
 *
 */
