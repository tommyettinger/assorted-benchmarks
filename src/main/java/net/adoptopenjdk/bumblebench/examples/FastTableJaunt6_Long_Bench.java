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

import javolution.util.FastTable;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * FastMap is from jaunt, a fork of Javolution, and is (as used here) insertion-ordered
 * like LinkedHashMap. This uses the code from Javolution 6.
 * <br>
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * Javolution 6.0.0 gets these results (higher is better):
 * <br>
 * FastTableJaunt6_Long_Bench score: 58528488.000000 (58.53M 1788.5%)
 *                        uncertainty:   9.6%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * Javolution 6.0.0 gets different results:
 * <br>
 * FastTableJaunt6_Long_Bench score: 50933464.000000 (50.93M 1774.6%)
 *                        uncertainty:   1.4%
 * <br>
 */
public final class FastTableJaunt6_Long_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final FastTable<Long> coll = new FastTable<Long>();
		for (long i = 0; i < numIterations; i++) {
			coll.add(i);
		}
		return numIterations;
	}
}

