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
 * HotSpot Java 8:
 * <br>
 * BalancedSplineFloatBench score: 16119286.000000 (16.12M 1659.6%)
 *                      uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BalancedSplineFloatBench score: 12093820.000000 (12.09M 1630.8%)
 *                      uncertainty:   1.5%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BalancedSplineFloatBench score: 61981864.000000 (61.98M 1794.2%)
 *                      uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BalancedSplineFloatBench score: 61542888.000000 (61.54M 1793.5%)
 *                      uncertainty:   2.7%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BalancedSplineFloatBench score: 33518964.000000 (33.52M 1732.8%)
 *                      uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * BalancedSplineFloatBench score: 12140618.000000 (12.14M 1631.2%)
 *                      uncertainty:   0.8%
 */
public final class BalancedSplineFloatBench extends MicroBench {

	public static float balancedSpline(final float x, final float shape) {
		return Math.copySign((float)Math.pow(Math.abs(x), shape), x);
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 2f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum += balancedSpline(i * shrink - 1f, 0.3f);
		return numIterations;
	}
}
