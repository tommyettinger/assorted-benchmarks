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
 * This implements the asin() approximation from sheet 35 of RAND Corporation's 1955 research study,
 * Approximations for Digital Computers. The copy used was https://www.researchgate.net/publication/318310473_Hastings%27_Approximations_for_Digital_Computers_Hastings_1955
 * <br>
 * Accuracy: absolute error 0.000028447, relative error -0.000000033, max error 0.000067592
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * ASin35FloatBench score: 94179176.000000 (94.18M 1836.1%)
 *                 uncertainty:   0.6%
 */
public final class ASin35FloatBench extends MicroBench {
	public static float asin(final float x) {
		final float x2 = x * x;
		final float x3 = x * x2;
		if (x >= 0f) {
			return 1.5707963267948966f - (float) Math.sqrt(1f - x) *
					(1.5707288f - 0.2121144f * x + 0.0742610f * x2 - 0.0187293f * x3);
		}
		else {
			return -1.5707963267948966f + (float) Math.sqrt(1f + x) *
					(1.5707288f + 0.2121144f * x + 0.0742610f * x2 + 0.0187293f * x3);
		}
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 0.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= asin((sum + i) * shrink);
		return numIterations;
	}

	public static void main(String[] args) {
		double absolute = 0.0, relative = 0.0, max = 0.0;
		for (float f : new float[]{-1f, -0.9999f, -0.999f, -0.99f, -0.5f -0.01f, -0.001f, -0.0001f, 0f, 0.0001f, 0.001f, 0.01f, 0.5f, 0.99f, 0.999f, 0.9999f, 1f}) {
			float math = (float) Math.asin(f);
			float mu = asin(f);
			float error = mu - math;
			relative = error;
			max = Math.max(max, Math.abs(error));
			absolute = Math.abs(error);
			System.out.printf("% 2.9f: Math gives % 2.9f, MathUtils gives % 2.9f ; off by % 2.9f\n",
					f, math, mu, relative);
		}
	}
//	public static void main(String[] args) {
//		double absolute = 0.0, relative = 0.0, max = 0.0;
//		float ctr = -1f;
//		for (int i = 0; i < 2048; i++) {
//			final double error = Math.asin(ctr) - asin(ctr);
//			relative += error;
//			max = Math.max(max, Math.abs(error));
//			absolute += Math.abs(error);
//			ctr += 0x1p-10f;
//		}
//		System.out.printf("absolute error %2.9f, relative error %2.9f, max error %2.9f",
//				absolute * 0x1p-11f, relative * 0x1p-11f, max);
//	}
}
