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
import org.javolution.util.FastTable;

/**
 * FastMap is from jaunt, a fork of Javolution, and is (as used here) insertion-ordered
 * like LinkedHashMap. This uses the code from Javolution 7, with {@code linked()} on.
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * FastTableJaunt7_Long_Bench score: 23530374.000000 (23.53M 1697.4%)
 *                        uncertainty:   5.2%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * FastTableJaunt7_Long_Bench score: 21676308.000000 (21.68M 1689.2%)
 *                        uncertainty:   4.8%
 */
public final class FastTableJaunt7_Long_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final FastTable<Long> coll = new FastTable<Long>();
		for (long i = 0; i < numIterations; i++) {
			coll.add(i);
		}
		return numIterations;
	}
}

