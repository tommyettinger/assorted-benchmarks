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

import com.github.tommyettinger.ds.FloatList;
import com.github.tommyettinger.random.LaserRandom;
import com.github.tommyettinger.ds.support.sort.FloatComparators;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * With Java 8, HotSpot, on an 8th-gen i7 hexacore mobile processor running Manjaro Linux:
 * <br>
 * FloatSortRevBench score: 385.928955 (385.9 595.6%)
 *               uncertainty:   0.1%
 * <br>
 * With Java 14, OpenJ9, same hardware:
 * <br>
 *
 */
public final class FloatSortRevBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		LaserRandom rng = new LaserRandom(0x12345678);
		FloatList list = new FloatList(len);
		float sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			list.clear();
			for (int j = 0; j < len; j++) {
				list.add(rng.nextFloat());
			}
			startTimer();
			list.sort(FloatComparators.OPPOSITE_COMPARATOR);
			sum += list.peek();
		}
		return numIterations;
	}
}
