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

import javolution.util.FastMap;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * FastMap is from jaunt, a fork of Javolution, and is (as used here) insertion-ordered
 * like LinkedHashMap. This uses the code from Javolution 6.
 * <br>
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * Javolution 6.0.0 gets these results (higher is better):
 * <br>
 * FastMapJaunt6_String_String_Bench score: 12660406.000000 (12.66M 1635.4%)
 *                               uncertainty:  40.0%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * Javolution 6.0.0 gets different results:
 * <br>
 * FastMapJaunt6_String_String_Bench score: 9913282.000000 (9.913M 1610.9%)
 *                               uncertainty:  40.0%
 * <br>
 * Yeah, that's about twice as fast as HashMap, to say nothing of LinkedHashMap.
 */
public final class FastMapJaunt6_String_String_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final FastMap<String, String> coll = new FastMap<String, String>();
		for (long i = 0; i < numIterations; i++) {
			final String s = String.valueOf(i);
			coll.put(s, s);
		}
		return numIterations;
	}
}

