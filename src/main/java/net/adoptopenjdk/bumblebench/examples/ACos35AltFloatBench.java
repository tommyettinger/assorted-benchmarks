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
 * Approximations for Digital Computers, and modified to calculate acos(). The copy used was
 * https://www.researchgate.net/publication/318310473_Hastings%27_Approximations_for_Digital_Computers_Hastings_1955
 * <br>
 * Accuracy: absolute error 0.000028448, relative error -0.000000012, max error 0.000067548
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * ACos35AltFloatBench score: 88942560.000000 (88.94M 1830.4%)
 *                 uncertainty:   0.2%
 */
public final class ACos35AltFloatBench extends MicroBench {
	public static float acos(final float v) {
		final float x = Math.abs(v);
		final float x2 = x * x;
		final float x3 = x * x2;
		return 1.5707963267948966f - Math.copySign(1.5707963267948966f - (float) Math.sqrt(1f - x) *
				(1.5707288f - 0.2121144f * x + 0.0742610f * x2 - 0.0187293f * x3), v);
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 0.3080339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= acos((sum + i) * shrink);
		return numIterations;
	}

	public static void main(String[] args) {
		double absolute = 0.0, relative = 0.0, max = 0.0;
		float ctr = -1f;
		for (int i = 0; i < 2048; i++) {
			final double error = Math.acos(ctr) - acos(ctr);
			relative += error;
			max = Math.max(max, Math.abs(error));
			absolute += Math.abs(error);
			ctr += 0x1p-10f;
		}
		System.out.printf("absolute error %2.9f, relative error %2.9f, max error %2.9f",
				absolute * 0x1p-11f, relative * 0x1p-11f, max);
	}
}
