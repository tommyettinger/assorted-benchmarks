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
 * HotSpot Java 8 (AdoptOpenJDK):
 * <br>
 * StrictMathSinBench score: 34722412.000000 (34.72M 1736.3%)
 *                uncertainty:   3.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * StrictMathSinBench score: 32636938.000000 (32.64M 1730.1%)
 *                uncertainty:   2.8%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * StrictMathSinBench score: 33137208.000000 (33.14M 1731.6%)
 *                uncertainty:   2.3%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * StrictMathSinBench score: 33357860.000000 (33.36M 1732.3%)
 *                uncertainty:   2.2%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * StrictMathSinBench score: 31586566.000000 (31.59M 1726.8%)
 *                uncertainty:   4.5%
 */
public final class StrictMathSinBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.1;
		final double shrink = Math.PI * 8.0 / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= StrictMath.sin((sum + i) * shrink);
		return numIterations;
	}
}
