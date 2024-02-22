
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

// TEMPLATE
/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * CongaRandomBench score: 732196224.000000 (732.2M 2041.2%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * CongaRandomBench score: 736506176.000000 (736.5M 2041.7%)
 *              uncertainty:   1.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * CongaRandomBench score: 687883648.000000 (687.9M 2034.9%)
 *              uncertainty:   0.5%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * CongaRandomBench score: 794892608.000000 (794.9M 2049.4%)
 *              uncertainty:   1.3%
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 * CongaRandomBench score: 807441920.000000 (807.4M 2050.9%)
 *              uncertainty:   0.8%
 * <br>
 * GraalVM Java 21:
 * <br>
 * CongaRandomBench score: 685942656.000000 (685.9M 2034.6%)
 *              uncertainty:   1.2%
 */
public final class CongaRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		CongaRandom rng = new CongaRandom(0x12345678);
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
