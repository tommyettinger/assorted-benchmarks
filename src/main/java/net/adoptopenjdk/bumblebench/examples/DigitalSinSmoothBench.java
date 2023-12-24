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
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * DigitalSinSmoothBench score: 138973856.000000 (139.0M 1875.0%)
 *                   uncertainty:   0.9%
 * <br>
 * GraalVM Java 20:
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft), 0.4.7:
 * <br>
 *
 *
 */public final class DigitalSinSmoothBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		for (long i = 0L, bits = 123L; i < numIterations; i++, bits += 0x9E3779B97F4A7C15L) {
			sum -= TrigTools.sinSmooth(
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
 * DigitalSinSmoothBench score: 66395272.000000 (66.40M 1801.1%)
 *                   uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalSinSmoothBench score: 69715104.000000 (69.72M 1806.0%)
 *                   uncertainty:   0.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalSinSmoothBench score: 56202312.000000 (56.20M 1784.4%)
 *                   uncertainty:   1.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalSinSmoothBench score: 56206748.000000 (56.21M 1784.5%)
 *                   uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalSinSmoothBench score: 56655240.000000 (56.66M 1785.2%)
 *                   uncertainty:   0.3%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalSinSmoothBench score: 56011660.000000 (56.01M 1784.1%)
 *                   uncertainty:   0.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalSinSmoothBench score: 56127560.000000 (56.13M 1784.3%)
 *                   uncertainty:   0.1%
 */