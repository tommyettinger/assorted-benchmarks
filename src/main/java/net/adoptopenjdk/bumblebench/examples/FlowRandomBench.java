
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
 * FlowRandomBench score: 701092288.000000 (701.1M 2036.8%)
 *             uncertainty:   1.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * FlowRandomBench score: 1876861312.000000 (1.877G 2135.3%)
 *             uncertainty:   1.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * FlowRandomBench score: 607363840.000000 (607.4M 2022.5%)
 *             uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * FlowRandomBench score: 659508288.000000 (659.5M 2030.7%)
 *             uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * FlowRandomBench score: 1029935616.000000 (1.030G 2075.3%)
 *             uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * FlowRandomBench score: 774334784.000000 (774.3M 2046.8%)
 *             uncertainty:   0.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * FlowRandomBench score: 651484736.000000 (651.5M 2029.5%)
 *             uncertainty:   1.3%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * FlowRandomBench score: 605812352.000000 (605.8M 2022.2%)
 *             uncertainty:   0.7%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * FlowRandomBench score: 628266240.000000 (628.3M 2025.8%)
 *             uncertainty:   8.9%
 * <br>
 * GraalVM Java 20:
 * <br>
 * FlowRandomBench score: 4275203072.000000 (4.275G 2217.6%)
 *             uncertainty:   0.4%
 */
public final class FlowRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		FlowRandom rng = new FlowRandom(0x12345678);
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
