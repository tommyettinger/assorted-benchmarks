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

import com.badlogic.gdx.math.MathUtils;
import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.util.HashSet;

/**
 * At load factor 0.5f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13+33, mixed mode, sharing)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * HashSet_Victor2_Bench score: 3561045.750000 (3.561M 1508.6%)
 *                   uncertainty:   8.1%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build master-99e396a57, JRE 13 Windows 7 amd64-64-Bit Compressed References 20191030_96 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * HashSet_Victor2_Bench score: 5245604.500000 (5.246M 1547.3%)
 *                   uncertainty:   8.4%
 */
public final class HashSet_Victor2_Bench extends MiniBench {

	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		final HashSet<Victor2> coll = new HashSet<>(16, 0.5f);
		final int halfIterations = MathUtils.nextPowerOfTwo((int)Math.sqrt(numIterationsPerLoop)) - 1;
		for (long i = 0; i < numLoops; i++) {
			int x = -halfIterations, y = -halfIterations;
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				coll.add(new Victor2(x, y));
				pauseTimer();
				if(++x > halfIterations)
				{
					x = -halfIterations;
					y++;
				}
			} 
		}
		return numLoops * numIterationsPerLoop;
	}
}

