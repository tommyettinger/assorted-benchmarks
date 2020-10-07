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

import com.badlogic.gdx.math.RandomXS128;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * With Java 8, HotSpot, on an 8th-gen i7 hexacore mobile processor running Manjaro Linux:
 * <br>
 * RandomXS128Bench score: 709502592.000000 (709.5M 2038.0%)
 *              uncertainty:   0.0%
 * <br>
 * With Java 14, OpenJ9, same hardware:
 * <br>
 * RandomXS128Bench score: 909541824.000000 (909.5M 2062.8%)
 *              uncertainty:   0.2%
 */
public final class RandomXS128Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		RandomXS128 rng = new RandomXS128(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
