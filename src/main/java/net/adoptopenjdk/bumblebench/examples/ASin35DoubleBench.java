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
 * ASin35DoubleBench score: 85164104.000000 (85.16M 1826.0%)
 *               uncertainty:   2.2%
 */
public final class ASin35DoubleBench extends MicroBench {
	public static double asin(final double v) {
		final double x = Math.abs(v);
		final double x2 = x * x;
		final double x3 = x * x2;
		return Math.copySign(1.5707963267948966 - Math.sqrt(1.0 - x) *
				(1.5707288 - 0.2121144 * x + 0.0742610 * x2 - 0.0187293 * x3), v);
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		double sum = 0.1;
		final double shrink = 0.6180339887498949 / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= asin((sum + i) * shrink);
		return numIterations;
	}

	public static void main(String[] args) {
		double absolute = 0.0, relative = 0.0, max = 0.0;
		double ctr = -1.0;
		for (int i = 0; i < 2048; i++) {
			final double error = Math.asin(ctr) - asin(ctr);
			relative += error;
			max = Math.max(max, Math.abs(error));
			absolute += Math.abs(error);
			ctr += 0x1p-10;
		}
		System.out.printf("absolute error %2.9f, relative error %2.9f, max error %2.9f",
				absolute * 0x1p-11f, relative * 0x1p-11f, max);
	}
}
