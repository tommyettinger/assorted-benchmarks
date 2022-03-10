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
import net.adoptopenjdk.bumblebench.examples.ChopIntRandomBench.ChopRandom;

/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * (Generating two ints and combining them, this we know works.)
 * <br>
 * HotSpot Java 8:
 * <br>
 * ChopLongRandomBench score: 550529920.000000 (550.5M 2012.6%)
 *                uncertainty:   2.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ChopLongRandomBench score: 657001536.000000 (657.0M 2030.3%)
 *                uncertainty:   2.9%
 * <br>
 * HotSpot Java 16:
 * <br>
 * ChopLongRandomBench score: 483681376.000000 (483.7M 1999.7%)
 *                uncertainty:   1.2%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * ChopLongRandomBench score: 567077888.000000 (567.1M 2015.6%)
 *                uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ChopLongRandomBench score: 508210464.000000 (508.2M 2004.6%)
 *                uncertainty:   1.2%
 */

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * (With single-generation optimization, this could pass tests?)
 * <br>
 * HotSpot Java 8:
 * <br>
 * ChopLongRandomBench score: 724925760.000000 (724.9M 2040.2%)
 *                uncertainty:   9.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * ChopLongRandomBench score: 1131402240.000000 (1.131G 2084.7%)
                uncertainty:   2.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * ChopLongRandomBench score: 726020992.000000 (726.0M 2040.3%)
 *                uncertainty:   1.4%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * ChopLongRandomBench score: 850065920.000000 (850.1M 2056.1%)
 *                uncertainty:   0.8% 
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * ChopLongRandomBench score: 732182016.000000 (732.2M 2041.2%)
 *                uncertainty:   0.3%
 */
public final class ChopLongRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ChopRandom rng = new ChopRandom(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
