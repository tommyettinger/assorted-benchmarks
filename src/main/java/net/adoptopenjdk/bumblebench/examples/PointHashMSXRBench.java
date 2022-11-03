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
 * Just speed-testing this; it may  or may not be random enough.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * PointHashMSXRBench score: 312618304.000000 (312.6M 1956.0%)
 *                uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * PointHashMSXRBench score: 317254208.000000 (317.3M 1957.5%)
 *                uncertainty:   0.7%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * PointHashMSXRBench score: 317102144.000000 (317.1M 1957.5%)
 *                uncertainty:   1.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * PointHashMSXRBench score: 301953600.000000 (302.0M 1952.6%)
 *                uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * PointHashMSXRBench score: 311047296.000000 (311.0M 1955.5%)
 *                uncertainty:   0.9%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * PointHashMSXRBench score: 329143584.000000 (329.1M 1961.2%)
 *                uncertainty:   0.6%
 */
public final class PointHashMSXRBench extends MicroBench {

	public static int msxrHashCode(final int x, final int y) {
		final int r = (int)((x + y) * 0x9E3779B97F4A7C15L >>> 32);
		return r ^ (r << 11 | r >>> 21) ^ (r << 19 | r >>> 13);
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + msxrHashCode((int)sum, (int)(sum>>>32));
		}
		return numIterations;
	}
}
