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

import java.util.HashMap;

/**
 * At load factor 0.25f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * HashMap_String_String_Bench score: 12257449.000000 (12.26M 1632.2%)
 *                         uncertainty:   5.2%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * HashMap_String_String_Bench score: 9618135.000000 (9.618M 1607.9%)
 *                         uncertainty:  24.0%
 * <br>
 * At load factor 0.5f on HotSpot:
 * <br>
 * HashMap_String_String_Bench score: 11737113.000000 (11.74M 1627.8%)
 *                         uncertainty:   4.4%
 */
public final class HashMap_String_String_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		HashMap<String, String> coll = new HashMap<String, String>(16, 0.5f);
		for (long i = 0; i < numIterations; i++) {
			String s = String.valueOf(i);
			coll.put(s, s);
		}
		return numIterations;
	}
}

