
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
 * Rawr32RandomIntBench score: 499279872.000000 (499.3M 2002.9%)
 *                  uncertainty:   2.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Rawr32RandomIntBench score: 447392800.000000 (447.4M 1991.9%)
 *                  uncertainty:   0.9%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Rawr32RandomIntBench score: 789812672.000000 (789.8M 2048.7%)
 *                  uncertainty:   1.0%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * Rawr32RandomIntBench score: 450368064.000000 (450.4M 1992.6%)
 *                  uncertainty:   0.3%
 * <br>
 * HotSpot Java 21 (BellSoft):
 * <br>
 * Rawr32RandomIntBench score: 491983136.000000 (492.0M 2001.4%)
 *                  uncertainty:   1.6%
 * <br>
 * GraalVM Java 21:
 * <br>
 * Rawr32RandomIntBench score: 789923776.000000 (789.9M 2048.7%)
 *                  uncertainty:   1.1%
 */
public final class Rawr32RandomIntBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Rawr32Random rng = new Rawr32Random(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
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
