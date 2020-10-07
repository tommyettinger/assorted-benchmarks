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
import ds.merry.IntMap;
import net.adoptopenjdk.bumblebench.core.MiniBench;

/**
 * NOTE: this version does not use a pairing function on its keys; oddly, that makes it faster here.
 * <br>
 * At load factor 0.5f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13+33, mixed mode, sharing)} (HotSpot)
 * This gets these results (higher is better):
 * With Rosenberg-Strong pairing function (not recommended here):
 * <br>
 * Merry_IntMap_Bench score: 29490994.000000 (29.49M 1720.0%)
 *               uncertainty:   1.7%
 * <br>
 * Without using any pairing function (just inserting {@code ((x & 0xFFFF) | (y & 0xFFFF) << 16)}) (recommended):
 * <br>
 * Merry_IntMap_Bench score: 48819764.000000 (48.82M 1770.4%)
 *               uncertainty:   2.0%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build master-99e396a57, JRE 13 Windows 7 amd64-64-Bit Compressed References 20191030_96 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 */
public final class Merry_IntMap_Bench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		final int halfIterations = MathUtils.nextPowerOfTwo((int)Math.sqrt(numIterationsPerLoop)) - 1;
		for (long i = 0; i < numLoops; i++) {
			final IntMap<Object> coll = new IntMap<>(numIterationsPerLoop, 0.5f);
			int x = -halfIterations, y = -halfIterations;
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				//// Rosenberg-Strong pairing function, not recommended here:
//				final int xx = (x << 1 ^ x >> 31);
//				final int yy = (y << 1 ^ y >> 31);
//				coll.put((xx + (xx > yy ? xx * xx + xx - yy : yy * yy)), null);
				//// basic bitwise packing, recommended:
				coll.put(((x & 0xFFFF) | (y & 0xFFFF) << 16), null);
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

