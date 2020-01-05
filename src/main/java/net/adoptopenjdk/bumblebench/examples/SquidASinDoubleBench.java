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
 * SquidASinDoubleBench score: 33241824.000000 (33.24M 1731.9%)
 *                  uncertainty:   0.3%
 */
public final class SquidASinDoubleBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  double sum = 0.1;
		  final double shrink = 0.6180339887498949 / numIterations;
		  for (long i = 0; i < numIterations; i++)
				sum -= NumberTools.asin((sum + i) * shrink);
		  return numIterations;
	 }
}
