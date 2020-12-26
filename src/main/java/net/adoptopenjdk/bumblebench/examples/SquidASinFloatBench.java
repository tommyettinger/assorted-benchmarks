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
import squidpony.squidmath.NumberTools;

/**
 * Accuracy: absolute error 0.000068530, relative error 0.000000000, max error 0.000200667
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * SquidASinFloatBench score: 57799912.000000 (57.80M 1787.2%)
 *                 uncertainty:   1.2%
 */
public final class SquidASinFloatBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.1f;
		  final float shrink = 0.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
				sum -= NumberTools.asin((sum + i) * shrink);
		  return numIterations;
	 }

	public static void main(String[] args) {
		double absolute = 0.0, relative = 0.0, max = 0.0;
		float ctr = -1f;
		for (int i = 0; i < 2048; i++) {
			final double error = Math.asin(ctr) - NumberTools.asin(ctr);
			relative += error;
			max = Math.max(max, Math.abs(error));
			absolute += Math.abs(error);
			ctr += 0x1p-10f;
		}
		System.out.printf("absolute error %2.9f, relative error %2.9f, max error %2.9f",
				absolute * 0x1p-11f, relative * 0x1p-11f, max);
	}

}
