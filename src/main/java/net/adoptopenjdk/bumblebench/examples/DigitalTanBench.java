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
 * DigitalTanBench score: 92821080.000000 (92.82M 1834.6%)
 *             uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalTanBench score: 106281304.000000 (106.3M 1848.2%)
 *             uncertainty:   0.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalTanBench score: 89935536.000000 (89.94M 1831.5%)
 *             uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalTanBench score: 89870032.000000 (89.87M 1831.4%)
 *             uncertainty:   2.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalTanBench score: 56178444.000000 (56.18M 1784.4%)
 *             uncertainty:   0.1%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalTanBench score: 89900152.000000 (89.90M 1831.4%)
 *             uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalTanBench score: 88908928.000000 (88.91M 1830.3%)
 *             uncertainty:   0.4%
 */
public final class DigitalTanBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		long counter = 1L;
		for (long i = 0; i < numIterations; i++)
			sum += TrigTools.tan((counter += 0x9E3779B97F4A7C15L) * 0x1.8p-63f);
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
