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

import java.util.HashSet;

/**
 * At load factor 0.5f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13+33, mixed mode, sharing)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * HashSet_Victor2_Bench score: 4641652.500000 (4.642M 1535.1%)
 *                   uncertainty:  40.0%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build master-99e396a57, JRE 13 Windows 7 amd64-64-Bit Compressed References 20191030_96 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * HashSet_Victor2_Bench score: 5891258.000000 (5.891M 1558.9%)
 *                   uncertainty:  14.1%
 */
public final class HashSet_Victor2_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final HashSet<Victor2> coll = new HashSet<>(16, 0.5f);
		for (long i = 0; i < numIterations; i++) {
			coll.add(new Victor2(i & 0xAAAAAAAAAAAAAAAAL, i & 0x5555555555555555L));
		}
		return numIterations;
	}
}

