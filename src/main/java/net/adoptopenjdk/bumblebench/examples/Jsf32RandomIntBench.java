
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
 * Jsf32RandomIntBench score: 704051904.000000 (704.1M 2037.2%)
 *                 uncertainty:   1.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * Jsf32RandomIntBench score: 1269566336.000000 (1.270G 2096.2%)
 *                 uncertainty:   0.9%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * Jsf32RandomIntBench score: 1027135872.000000 (1.027G 2075.0%)
 *                 uncertainty:   1.0%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * Jsf32RandomIntBench score: 1027140288.000000 (1.027G 2075.0%)
 *                 uncertainty:   4.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * Jsf32RandomIntBench score: 992534080.000000 (992.5M 2071.6%)
 *                 uncertainty:   1.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * Jsf32RandomIntBench score: 1270715904.000000 (1.271G 2096.3%)
 *                 uncertainty:   0.7%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * Jsf32RandomIntBench score: 1026421760.000000 (1.026G 2074.9%)
 *                 uncertainty:   1.1%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * Jsf32RandomIntBench score: 1112799232.000000 (1.113G 2083.0%)
 *                 uncertainty:   0.8%
 */
public final class Jsf32RandomIntBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Jsf32Random rng = new Jsf32Random(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}
