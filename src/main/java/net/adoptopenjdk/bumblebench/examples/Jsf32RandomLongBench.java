
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

import com.github.tommyettinger.random.Jsf32Random;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * Jsf32RandomLongBench score: 482270336.000000 (482.3M 1999.4%)
 *                  uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Jsf32RandomLongBench score: 633274240.000000 (633.3M 2026.6%)
 *                  uncertainty:   0.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Jsf32RandomLongBench score: 581192448.000000 (581.2M 2018.1%)
 *                  uncertainty:   0.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Jsf32RandomLongBench score: 587701440.000000 (587.7M 2019.2%)
 *                  uncertainty:   0.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Jsf32RandomLongBench score: 562360192.000000 (562.4M 2014.8%)
 *                  uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * Jsf32RandomLongBench score: 632982784.000000 (633.0M 2026.6%)
 *                  uncertainty:   0.3%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * Jsf32RandomLongBench score: 587845120.000000 (587.8M 2019.2%)
 *                  uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * Jsf32RandomLongBench score: 582462656.000000 (582.5M 2018.3%)
 *                  uncertainty:   0.5%
 */
public final class Jsf32RandomLongBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Jsf32Random rng = new Jsf32Random(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
