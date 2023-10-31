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
 * MathCosBench score: 93463272.000000 (93.46M 1835.3%)
 *          uncertainty:   0.9%
 * <br>
 * GraalVM Java 20:
 * <br>
 *
 */
public final class MathCosBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.1;
		for (long i = 0L, bits = 123L; i < numIterations; i++, bits += 0x9E3779B97F4A7C15L) {
			sum -= Math.cos(
					Float.intBitsToFloat(129 - Long.numberOfLeadingZeros(bits) << 23 | ((int) bits & 0x807FFFFF))
			);
		}
		return numIterations;
	}
}

/* OLD
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8 (AdoptOpenJDK):
 * <br>
 * MathSinBench score: 32765174.000000 (32.77M 1730.5%)
 *          uncertainty:   2.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathSinBench score: 33481438.000000 (33.48M 1732.7%)
 *          uncertainty:   1.0%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MathSinBench score: 45923824.000000 (45.92M 1764.2%)
 *          uncertainty:   3.7%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * MathSinBench score: 49647548.000000 (49.65M 1772.0%)
 *          uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * MathSinBench score: 44931508.000000 (44.93M 1762.1%)
 *          uncertainty:   4.7%
 */