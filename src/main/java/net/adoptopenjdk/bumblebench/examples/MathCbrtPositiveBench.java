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
 * MathCbrtPositiveBench score: 17459108.000000 (17.46M 1667.5%)
 *                  uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathCbrtPositiveBench score: 27614680.000000 (27.61M 1713.4%)
 *                  uncertainty:   0.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * MathCbrtPositiveBench score: 29357254.000000 (29.36M 1719.5%)
 *                  uncertainty:   1.5%
 */
public final class MathCbrtPositiveBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  double sum = 0.01, inc = 0.01;
		  final double shrink = 1.6180339887498949 / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum = Math.cbrt(sum + sum + (inc += shrink));
		  return numIterations;
	 }
}
