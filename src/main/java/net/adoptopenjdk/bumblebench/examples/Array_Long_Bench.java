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

import com.badlogic.gdx.utils.Array;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Array_Long_Bench score: 29198858.000000 (29.20M 1719.0%)
 *              uncertainty:  40.0%
 *
 * RUN WITH {@code -DBumbleBench.batchTargetDuration=100 } !!!
 */
public final class Array_Long_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Array<Long> coll = new Array<Long>();
		for (long i = 0; i < numIterations; i++) {
			coll.add(i);
		}
		return numIterations;
	}
}

