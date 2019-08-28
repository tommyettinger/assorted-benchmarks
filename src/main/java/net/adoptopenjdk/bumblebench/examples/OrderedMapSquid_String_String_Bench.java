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
import squidpony.squidmath.OrderedMap;

/**
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * OrderedMapSquid_String_String_Bench score: 9580815.000000 (9.581M 1607.5%)
 *                                 uncertainty:  17.4%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * OrderedMapSquid_String_String_Bench score: 9152753.000000 (9.153M 1603.0%)
 *                                 uncertainty:  40.0%
 */
public final class OrderedMapSquid_String_String_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final OrderedMap<String, String> coll = new OrderedMap<String, String>(16, 0.25f);
		for (long i = 0; i < numIterations; i++) {
			final String s = String.valueOf(i);
			coll.put(s, s);
		}
		return numIterations;
	}
}

