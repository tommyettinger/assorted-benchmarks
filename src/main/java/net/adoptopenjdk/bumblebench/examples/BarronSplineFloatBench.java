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

import com.github.tommyettinger.digital.BitConversion;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * BarronSplineFloatBench score: 82619488.000000 (82.62M 1823.0%)
 *                    uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BarronSplineFloatBench score: 91600480.000000 (91.60M 1833.3%)
 *                    uncertainty:   1.9%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BarronSplineFloatBench score: 155986304.000000 (156.0M 1886.5%)
 *                    uncertainty:   0.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BarronSplineFloatBench score: 231113168.000000 (231.1M 1925.8%)
 *                    uncertainty:   1.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BarronSplineFloatBench score: 81484904.000000 (81.48M 1821.6%)
 *                    uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * BarronSplineFloatBench score: 91008136.000000 (91.01M 1832.6%)
 *                    uncertainty:   0.3%
 */
public final class BarronSplineFloatBench extends MicroBench {
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
	 * @param shape   must be greater than or equal to 0; values greater than 1 are "normal interpolations"
	 * @param turning a value between 0.0 and 1.0, inclusive, where the shape changes
	 * @return a float between 0 and 1, inclusive
	 */
	public static float barronSpline(final float x, final float shape, final float turning) {
		final float d = turning - x;
		final int f = BitConversion.floatToIntBits(d) >> 31, n = f | 1;
		return ((turning * n - f) * (x + f)) / (Float.MIN_NORMAL - f + (x + shape * d) * n) - f;
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 1f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum += barronSpline(i * shrink, 0.6f, 0.5f) - 0.5f;
		return numIterations;
	}
}
