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

import java.util.LinkedHashMap;

/**
 * At load factor 0.25f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * LinkedHashMap_String_String_Bench score: 11614581.000000 (11.61M 1626.8%)
 *                               uncertainty:  14.4%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * LinkedHashMap_String_String_Bench score: 9044734.000000 (9.045M 1601.8%)
 *                               uncertainty:  14.4%
 * <br>
 * At load factor 0.5f on HotSpot:
 * <br>
 * LinkedHashMap_String_String_Bench score: 10201033.000000 (10.20M 1613.8%)
 *                               uncertainty:  40.0%
 */
public final class LinkedHashMap_String_String_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final LinkedHashMap<String, String> coll = new LinkedHashMap<String, String>(16, 0.5f);
		for (long i = 0; i < numIterations; i++) {
			final String s = String.valueOf(i);
			coll.put(s, s);
		}
		return numIterations;
	}
}

