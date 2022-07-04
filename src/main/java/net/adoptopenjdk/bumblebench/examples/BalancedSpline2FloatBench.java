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
 * BalancedSpline2FloatBench score: 131220584.000000 (131.2M 1869.2%)
 *                       uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BalancedSpline2FloatBench score: 218413344.000000 (218.4M 1920.2%)
 *                       uncertainty:   0.8%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BalancedSpline2FloatBench score: 251884688.000000 (251.9M 1934.4%)
 *                       uncertainty:   1.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BalancedSpline2FloatBench score: 251463904.000000 (251.5M 1934.3%)
 *                       uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BalancedSpline2FloatBench score: 132223048.000000 (132.2M 1870.0%)
 *                       uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * BalancedSpline2FloatBench score: 217580016.000000 (217.6M 1919.8%)
 *                       uncertainty:   0.7%
 */
public final class BalancedSpline2FloatBench extends MicroBench {
	/**
	 * A generalization on bias and gain functions that can represent both; this version is branch-less.
	 * This is based on <a href="https://arxiv.org/abs/2010.09714">this micro-paper</a> by Jon Barron, which
	 * generalizes the earlier bias and gain rational functions by Schlick. The second and final page of the
	 * paper has useful graphs of what the s (shape) and t (turning point) parameters do; shape should be 0
	 * or greater, while turning must be between 0 and 1, inclusive. This effectively combines two different
	 * curving functions so they continue into each other when x equals turning. The shape parameter will
	 * cause this to imitate "smoothstep-like" splines when greater than 1 (where the values ease into their
	 * starting and ending levels), or to be the inverse when less than 1 (where values start like square
	 * root does, taking off very quickly, but also end like square does, landing abruptly at the ending
	 * level). You should only give x values between 0 and 1, inclusive.
	 *
	 * @param x       progress through the spline, from 0 to 1, inclusive
	 * @param shape   must be greater than 0; values less than 1 are "normal interpolations"
	 * @return a float between 0 and 1, inclusive
	 */
	public static float balancedSpline(final float x, final float shape) {
		return x / (((shape - 1) * (1 - Math.abs(x))) + 1.0000001f);
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 2f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum += balancedSpline(i * shrink - 1f, 0.3f);
		return numIterations;
	}
}
