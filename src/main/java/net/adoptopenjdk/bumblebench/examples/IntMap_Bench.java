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
 * NOTE: this uses the Rosenberg-Strong pairing function to combine x and y of a grid point into one long. I'm not
 * totally sure that x and y can be extracted from a key with the way they are paired now, but it's likely they can be.
 * <br>
 * At load factor 0.5f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13+33, mixed mode, sharing)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * IntMap_Bench score: 46028572.000000 (46.03M 1764.5%)
 *          uncertainty:   1.4%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build master-99e396a57, JRE 13 Windows 7 amd64-64-Bit Compressed References 20191030_96 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * IntMap_Bench score: 19145394.000000 (19.15M 1676.8%)
 *          uncertainty:   2.9%
 */
public final class IntMap_Bench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		final IntMap<Object> coll = new IntMap<>(numIterationsPerLoop, 0.5f);
		final int halfIterations = MathUtils.nextPowerOfTwo((int)Math.sqrt(numIterationsPerLoop)) - 1;
		for (long i = 0; i < numLoops; i++) {
			int x = -halfIterations, y = -halfIterations;
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				final int xx = (x << 1 ^ x >> 31);
				final int yy = (y << 1 ^ y >> 31);
				coll.put((xx + (xx > yy ? xx * xx + xx - yy : yy * yy)), null);
//				coll.put(((x & 0xFFFF) | (y & 0xFFFF) << 16), null);
				//// this was bad, below; it had less than half the throughput.
//				coll.put((long)x << 32 | (y & 0xFFFFFFFFL), null);
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

