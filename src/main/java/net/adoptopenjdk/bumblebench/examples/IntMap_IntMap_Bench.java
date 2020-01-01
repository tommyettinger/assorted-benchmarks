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
import com.badlogic.gdx.utils.IntMap;
import net.adoptopenjdk.bumblebench.core.MiniBench;

/**
 * At load factor 0.5f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13+33, mixed mode, sharing)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * IntMap_IntMap_Bench score: 77520496.000000 (77.52M 1816.6%)
 *                 uncertainty:   0.3%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build master-99e396a57, JRE 13 Windows 7 amd64-64-Bit Compressed References 20191030_96 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * IntMap_IntMap_Bench score: 24961720.000000 (24.96M 1703.3%)
 *                 uncertainty:   0.3%
 */
public final class IntMap_IntMap_Bench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		final int halfIterations = MathUtils.nextPowerOfTwo((int)Math.sqrt(numIterationsPerLoop)) - 1;
		IntMap<Object> current = new IntMap<Object>(16, 0.5f);
		for (long i = 0; i < numLoops; i++) {
			final IntMap<IntMap<Object>> coll = new IntMap<>(16, 0.5f);
			int x = -halfIterations, y = -halfIterations;
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				current.put(y, null);
				pauseTimer();
				if(++x > halfIterations)
				{
					startTimer();
					coll.put(x, current);
					current = new IntMap<Object>(16, 0.5f);
					pauseTimer();
					x = -halfIterations;
					y++;
				}
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}

