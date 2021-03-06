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
 * NOTE: this uses Java 13, Hotspot; expect very different results on Java 8
 * <br>
 * MathSinBench score: 30795266.000000 (30.80M 1724.3%)
 *          uncertainty:   7.2%
 */
public final class MathSinBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		double sum = 0.1;
		final double shrink = Math.PI * 8.0 / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= Math.sin((sum + i) * shrink);
		return numIterations;
	}
}
