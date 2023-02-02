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
 * DigitalTanSmootherBench score: 79953352.000000 (79.95M 1819.7%)
 *                     uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalTanSmootherBench score: 81293056.000000 (81.29M 1821.4%)
 *                     uncertainty:   0.2%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalTanSmootherBench score: 151615536.000000 (151.6M 1883.7%)
 *                     uncertainty:   1.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalTanSmootherBench score: 151029584.000000 (151.0M 1883.3%)
 *                     uncertainty:   1.2%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalTanSmootherBench score: 67108312.000000 (67.11M 1802.2%)
 *                     uncertainty:   0.5%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalTanSmootherBench score: 151708816.000000 (151.7M 1883.7%)
 *                     uncertainty:   0.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalTanSmootherBench score: 149108368.000000 (149.1M 1882.0%)
 *                     uncertainty:   0.4%
 */
public final class DigitalTanSmootherBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		long counter = 1L;
		for (long i = 0; i < numIterations; i++)
			sum += TrigTools.tanSmoother((counter += 0x9E3779B97F4A7C15L) * 0x1.8p-63f);
		return numIterations;
	}
}

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
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 */
