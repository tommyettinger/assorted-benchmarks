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

import java.util.Random;

/**
 * This tests int generation instead of long generation, because Random is handicapped on longs.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * JavaRandomBench score: 123475912.000000 (123.5M 1863.2%)
 *             uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * JavaRandomBench score: 123655352.000000 (123.7M 1863.3%)
 *             uncertainty:   1.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * JavaRandomBench score: 127783576.000000 (127.8M 1866.6%)
 *             uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * JavaRandomBench score: 127887904.000000 (127.9M 1866.7%)
 *             uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * JavaRandomBench score: 847075584.000000 (847.1M 2055.7%)
 *             uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * JavaRandomBench score: 123434768.000000 (123.4M 1863.1%)
 *             uncertainty:   0.4%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * JavaRandomBench score: 127339232.000000 (127.3M 1866.2%)
 *             uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * JavaRandomBench score: 128287792.000000 (128.3M 1867.0%)
 *             uncertainty:   0.6%
 */
public final class JavaRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Random rng = new Random(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}

// Old results
/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * Java 8, HotSpot:
 * <br>
 * JavaRandomBench score: 117254840.000000 (117.3M 1858.0%)
 *             uncertainty:   0.4%
 * <br>
 * With Java 15, OpenJ9, same hardware:
 * <br>
 * JavaRandomBench score: 119695936.000000 (119.7M 1860.0%)
 *             uncertainty:   0.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * JavaRandomBench score: 123277880.000000 (123.3M 1863.0%)
 *             uncertainty:   0.4%
 */
