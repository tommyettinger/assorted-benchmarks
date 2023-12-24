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
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalCosSmootherBench score: 452259168.000000 (452.3M 1993.0%)
 *                     uncertainty:   2.1%
 * <br>
 * HotSpot Java 20 (BellSoft), 0.4.2:
 * <br>
 * DigitalCosSmootherBench score: 482780736.000000 (482.8M 1999.5%)
 *                     uncertainty:   1.0%
 * <br>
 * HotSpot Java 21 (BellSoft), 0.4.7:
 * <br>
 * DigitalCosSmootherBench score: 470207360.000000 (470.2M 1996.9%)
 *                     uncertainty:   1.4%
 * <br>
 * GraalVM Java 17, 0.4.7:
 * <br>
 * DigitalCosSmootherBench score: 435732992.000000 (435.7M 1989.3%)
 *                     uncertainty:   1.1%
 * <br>
 * GraalVM Java 21, 0.4.7:
 * <br>
 * DigitalCosSmootherBench score: 432730624.000000 (432.7M 1988.6%)
 *                     uncertainty:   1.6%
 */
public final class DigitalCosSmootherBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		for (long i = 0L, bits = 123L; i < numIterations; i++, bits += 0x9E3779B97F4A7C15L) {
			sum -= TrigTools.cosSmoother(
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
 * DigitalSinSmootherBench score: 84239928.000000 (84.24M 1824.9%)
 *                     uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalSinSmootherBench score: 84092720.000000 (84.09M 1824.7%)
 *                     uncertainty:   1.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalSinSmootherBench score: 84961760.000000 (84.96M 1825.8%)
 *                     uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalSinSmootherBench score: 84208928.000000 (84.21M 1824.9%)
 *                     uncertainty:   0.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalSinSmootherBench score: 84736304.000000 (84.74M 1825.5%)
 *                     uncertainty:   0.2%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalSinSmootherBench score: 84507312.000000 (84.51M 1825.2%)
 *                     uncertainty:   0.8%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalSinSmootherBench score: 84595008.000000 (84.60M 1825.3%)
 *                     uncertainty:   0.1%
 */