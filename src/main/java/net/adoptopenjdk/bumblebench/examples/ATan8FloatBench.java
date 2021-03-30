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
 * This implements the atan() approximation from sheet 8 of RAND Corporation's 1955 research study,
 * Approximations for Digital Computers. The copy used was https://www.researchgate.net/publication/318310473_Hastings%27_Approximations_for_Digital_Computers_Hastings_1955
 * <br>
 * Accuracy: absolute error 0.000382348, relative error -0.000000297, max error 0.000609356
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * Java 15 Hotspot:
 * <br>
 * ATan8FloatBench score: 68488872.000000 (68.49M 1804.2%)
 *             uncertainty:   0.3%
 */
public final class ATan8FloatBench extends MicroBench {
	public static float atan(final float v) {
		final float n = Math.abs(v);
		final float x = (n - 1f) / (n + 1f);
		final float x2 = x * x;
		final float x3 = x * x2;
		final float x5 = x3 * x2;
		return Math.copySign(0.7853981633974483f +
				(0.995354f * x - 0.288679f * x3 + 0.079331f * x5), v);
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 0.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= atan((sum + i) * shrink);
		return numIterations;
	}

	public static void main(String[] args) {
		double absolute = 0.0, relative = 0.0, max = 0.0;
		float ctr = -1f;
		for (int i = 0; i < 2048; i++) {
			final double error = Math.atan(ctr) - atan(ctr);
			relative += error;
			max = Math.max(max, Math.abs(error));
			absolute += Math.abs(error);
			ctr += 0x1p-10f;
		}
		System.out.printf("absolute error %2.9f, relative error %2.9f, max error %2.9f",
				absolute * 0x1p-11f, relative * 0x1p-11f, max);
	}
}
