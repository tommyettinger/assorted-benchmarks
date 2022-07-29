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
 * MathInverseSqrtFloatBench score: 140330192.000000 (140.3M 1876.0%)
 *                       uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * (loop is eliminated, claims impossibly high throughput)
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MathInverseSqrtFloatBench score: 140850144.000000 (140.9M 1876.3%)
 *                       uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MathInverseSqrtFloatBench score: 140524192.000000 (140.5M 1876.1%)
 *                       uncertainty:   1.1%
 * <br>
 * GraalVM Java 17:
 * <br>
 * MathInverseSqrtFloatBench score: 94047536.000000 (94.05M 1835.9%)
 *                       uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * (loop is eliminated, claims impossibly high throughput)
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * MathInverseSqrtFloatBench score: 141482400.000000 (141.5M 1876.8%)
 *                       uncertainty:   1.6%
 */
public final class MathInverseSqrtFloatBench extends MicroBench {
	 protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.01f, inc = 0.01f;
		  final float shrink = 1.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
			  sum = 1f/(float) Math.sqrt(sum + sum + (inc += shrink));
		  return numIterations;
	 }
}
