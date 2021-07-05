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
 * MathSqrtDoubleBench score: 170852880.000000 (170.9M 1895.6%)
 *                uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * (loop is eliminated, claims impossibly high throughput)
 * <br>
 * HotSpot Java 16:
 * <br>
 * MathSqrtDoubleBench score: 170725520.000000 (170.7M 1895.6%)
 *                uncertainty:   0.6%
 */
public final class MathSqrtDoubleBench extends MicroBench {
	 protected long doBatch (long numIterations) throws InterruptedException {
		  double sum = 0.01, inc = 0.01;
		  final double shrink = 1.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum = Math.sqrt(sum + sum + (inc += shrink));
		  return numIterations;
	 }
}
