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
 * BitInverseSqrtFloatBench score: 135936944.000000 (135.9M 1872.8%)
 *                      uncertainty:   0.7
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * BitInverseSqrtFloatBench score: 135179424.000000 (135.2M 1872.2%)
 *                      uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * BitInverseSqrtFloatBench score: 138432688.000000 (138.4M 1874.6%)
 *                      uncertainty:   0.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * BitInverseSqrtFloatBench score: 135659744.000000 (135.7M 1872.6%)
 *                      uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * BitInverseSqrtFloatBench score: 134399280.000000 (134.4M 1871.6%)
 *                      uncertainty:   0.4%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * BitInverseSqrtFloatBench score: 132363536.000000 (132.4M 1870.1%)
 *                      uncertainty:   1.9%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * BitInverseSqrtFloatBench score: 138950544.000000 (139.0M 1875.0%)
 *                      uncertainty:   1.0%
 */
public final class BitInverseSqrtFloatBench extends MicroBench {
	/**
	 * Fast inverse square root, best known for its implementation in Quake III Arena.
	 * This is an algorithm that estimates the {@code float} value of 1/sqrt(x).
	 * <br>
	 * It is often used for vector normalization, i.e. scaling it to a length of 1.
	 * For example, it can be used to compute angles of incidence and reflection for
	 * lighting and shading.
	 * <br>
	 * For more information, see <a href="https://en.wikipedia.org/wiki/Fast_inverse_square_root">Wikipedia</a>
	 *
	 * @param x any finite float to find the inverse square root of
	 * @return the inverse square root of x, approximated
	 */
	public static float invSqrt(float x) {
		float x2 = 0.5f * x;
		int i = Float.floatToIntBits(x);
		i = 0x5f3759df - (i >> 1);
		float y = Float.intBitsToFloat(i);
		y *= (1.5f - x2 * y * y);
		return y;
	}
	protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.01f, inc = 0.01f;
		  final float shrink = 1.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum = invSqrt(sum + sum + (inc += shrink));
		  return numIterations;
	 }
}
