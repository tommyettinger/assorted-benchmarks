
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
 * WeaselRandomBench score: 606718592.000000 (606.7M 2022.4%)
 *               uncertainty:   1.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * WeaselRandomBench score: 767715648.000000 (767.7M 2045.9%)
 *               uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * WeaselRandomBench score: 789666368.000000 (789.7M 2048.7%)
 *               uncertainty:   0.5%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * WeaselRandomBench score: 799417024.000000 (799.4M 2049.9%)
 *               uncertainty:   0.4%
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 * WeaselRandomBench score: 792935424.000000 (792.9M 2049.1%)
 *               uncertainty:   0.5%
 * <br>
 * GraalVM Java 21:
 * <br>
 * WeaselRandomBench score: 795907072.000000 (795.9M 2049.5%)
 *               uncertainty:   0.7%
 */
public final class WeaselRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		WeaselRandom rng = new WeaselRandom(0x12345678);
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
