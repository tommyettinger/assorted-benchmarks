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
 * New laptop; Windows 10, 10th generation i7, Java 15
 * <br>
 * MathCbrtPositiveBench score: 27221158.000000 (27.22M 1712.0%)
 *                  uncertainty:   0.2%
 */
public final class MathCbrtPositiveBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.1f;
		  final float shrink = 1.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum += Math.cbrt(sum + i * shrink);
		  return numIterations;
	 }
}
