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
 * MathCbrtBench score: 17421624.000000 (17.42M 1667.3%)
 *          uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathCbrtBench score: 27825858.000000 (27.83M 1714.1%)
 *          uncertainty:   1.6%
 * <br>
 * HotSpot Java 16:
 * <br>
 * MathCbrtBench score: 29086090.000000 (29.09M 1718.6%)
 *          uncertainty:   0.1%
 */
public final class MathCbrtBench extends MicroBench {
	 protected long doBatch (long numIterations) throws InterruptedException {
		  double sum = 0.01, inc = -0.5;
		  final double shrink = 1.6180339887498949 / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum = Math.cbrt(sum + sum + (inc += shrink));
		  return numIterations;
	 }
}
