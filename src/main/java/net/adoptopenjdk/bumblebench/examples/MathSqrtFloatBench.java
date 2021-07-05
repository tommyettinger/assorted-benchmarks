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
 * MathSqrtFloatBench score: 222139840.000000 (222.1M 1921.9%)
 *               uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * (loop is eliminated, claims impossibly high throughput)
 * <br>
 * HotSpot Java 16:
 * <br>
 * MathSqrtFloatBench score: 221765040.000000 (221.8M 1921.7%)
 *               uncertainty:   0.4%
 */
public final class MathSqrtFloatBench extends MicroBench {
	 protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.01f, inc = 0.01f;
		  final float shrink = 1.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum = (float) Math.sqrt(sum + sum + (inc += shrink));
		  return numIterations;
	 }
}
