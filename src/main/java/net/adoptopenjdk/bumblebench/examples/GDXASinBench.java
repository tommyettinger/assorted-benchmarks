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

import com.badlogic.gdx.math.MathUtils;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * GDXASinBench score: 60559452.000000 (60.56M 1791.9%)
 *          uncertainty:   0.3%
 */
public final class GDXASinBench extends MicroBench {

	 protected long doBatch (long numIterations) throws InterruptedException {
		  float sum = 0.1f;
		  final float shrink = 0.6180339887498949f / numIterations;
		  for (long i = 0; i < numIterations; i++)
				sum -= MathUtils.asin((sum + i) * shrink);
		  return numIterations;
	 }
}
