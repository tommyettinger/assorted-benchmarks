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

import com.badlogic.gdx.math.MathUtils;
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
 * GDXSinBench score: 817569408.000000 (817.6M 2052.2%)
 *         uncertainty:   0.4%
 * <br>
 * GraalVM Java 20:
 * <br>
 *
 */
public final class GDXSinBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		for (long i = 0L, bits = 123L; i < numIterations; i++, bits += 0x9E3779B97F4A7C15L) {
			sum -= MathUtils.sin(
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
 * GDXSinBench score: 150555696.000000 (150.6M 1883.0%)
 *         uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GDXSinBench score: 151019840.000000 (151.0M 1883.3%)
 *         uncertainty:   0.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * GDXSinBench score: 150672832.000000 (150.7M 1883.1%)
 *         uncertainty:   1.2%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * GDXSinBench score: 149349824.000000 (149.3M 1882.2%)
 *         uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * GDXSinBench score: 148779184.000000 (148.8M 1881.8%)
 *         uncertainty:   0.6%
 */
