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

import com.badlogic.gdx.utils.LongArray;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * LongArray_Bench score: 178327744.000000 (178.3M 1899.9%)
 *             uncertainty:  21.5%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build openj9-0.10.0, JRE 11 Windows 7 amd64-64-Bit Compressed References 20181003_41 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * LongArray_Bench score: 168945104.000000 (168.9M 1894.5%)
 *             uncertainty:  15.2%
 * <br>
 * RUN WITH {@code -DBumbleBench.batchTargetDuration=100 } !!!
 */
public final class LongArray_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		LongArray coll = new LongArray();
		for (long i = 0; i < numIterations; i++) {
			coll.add(i);
		}
		return numIterations;
	}
}

