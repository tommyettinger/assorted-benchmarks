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
 * Java 8, HotSpot:
 * <br>
 * MathATanFloatBench score: 33210218.000000 (33.21M 1731.8%)
 *                uncertainty:   1.1%
 * <br>
 * Java 15, HotSpot:
 * <br>
 * MathATanFloatBench score: 37022468.000000 (37.02M 1742.7%)
 *                uncertainty:   1.6%
 */
public final class MathATanFloatBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.1f;
		  final float shrink = 0.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
				sum -= Math.atan((sum + i) * shrink);
		  return numIterations;
	 }
}
