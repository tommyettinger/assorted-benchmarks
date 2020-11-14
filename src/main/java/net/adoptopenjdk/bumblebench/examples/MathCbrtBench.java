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
 * MathCbrtBench score: 27468688.000000 (27.47M 1712.9%)
 *           uncertainty:   0.5%
 */
public final class MathCbrtBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  double sum = 0.1;
		  final double shrink = 1.6180339887498949 / numIterations;
		  for (long i = 0; i < numIterations; i++)
				sum -= Math.cbrt(sum + i * shrink);
		  return numIterations;
	 }
}
