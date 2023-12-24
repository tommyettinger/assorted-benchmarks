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

import com.github.tommyettinger.digital.TrigTools;
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
 * DigitalCosBench score: 724534656.000000 (724.5M 2040.1%)
 *             uncertainty:   0.5%
 * <br>
 * HotSpot Java 20 (BellSoft), 0.4.2:
 * <br>
 * DigitalCosBench score: 672228928.000000 (672.2M 2032.6%)
 *             uncertainty:   0.4%
 * <br>
 * HotSpot Java 21 (BellSoft), 0.4.7:
 * <br>
 * DigitalCosBench score: 736185280.000000 (736.2M 2041.7%)
 *             uncertainty:   5.3%
 * <br>
 * GraalVM Java 17, 0.4.7:
 * <br>
 * DigitalCosBench score: 719542400.000000 (719.5M 2039.4%)
 *             uncertainty:   1.8%
 * <br>
 * GraalVM Java 21, 0.4.7:
 * <br>
 * DigitalCosBench score: 718207744.000000 (718.2M 2039.2%)
 *             uncertainty:   2.8%
 */
public final class DigitalCosBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		for (long i = 0L, bits = 123L; i < numIterations; i++, bits += 0x9E3779B97F4A7C15L) {
			sum -= TrigTools.cos(
					Float.intBitsToFloat(129 - Long.numberOfLeadingZeros(bits) << 23 | ((int) bits & 0x807FFFFF))
			);
		}
		return numIterations;
	}
}

/* OLD
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * DigitalSinBench score: 149810848.000000 (149.8M 1882.5%)
 *             uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalSinBench score: 149791632.000000 (149.8M 1882.5%)
 *             uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalSinBench score: 151432256.000000 (151.4M 1883.6%)
 *             uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalSinBench score: 149509440.000000 (149.5M 1882.3%)
 *             uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalSinBench score: 150604880.000000 (150.6M 1883.0%)
 *             uncertainty:   0.3%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalSinBench score: 149607872.000000 (149.6M 1882.4%)
 *             uncertainty:   0.3%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalSinBench score: 149943920.000000 (149.9M 1882.6%)
 *             uncertainty:   0.4%
 */
