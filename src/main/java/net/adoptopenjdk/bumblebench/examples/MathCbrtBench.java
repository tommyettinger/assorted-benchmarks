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
 * New laptop; Windows 10, 10th generation i7, Java 8
 * <br>
 * MathCbrtBench score: 16701729.000000 (16.70M 1663.1%)
 *          uncertainty:   1.3%
 * <br>
 * New laptop; Windows 10, 10th generation i7, Java 15
 * <br>
 *  MathCbrtBench score: 26720632.000000 (26.72M 1710.1%)
 *          uncertainty:   0.1%
 */
public final class MathCbrtBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		 float sum = 0.1f;
		 final float shrink = 1.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
				sum -= Math.cbrt(sum + i * shrink);
		  return numIterations;
	 }
}
