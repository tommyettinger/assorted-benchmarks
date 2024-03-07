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

import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.flopon.Flopon;
import com.github.tommyettinger.flopon.FloponWriter;
import net.adoptopenjdk.bumblebench.core.MiniBench;

/**
 * Java 17:
 * <br>
 * FloponReadBench score: 8.617563 (8.618 215.4%)
 *             uncertainty:   4.0%
 */
public final class FloponReadBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		String data = new HeadlessFiles().local("flopon.json").readString();
		ObjectMap<String, Array<Vector2>> big;

		Flopon flopon = new Flopon(FloponWriter.OutputType.minimal);

		long counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				big = flopon.fromFlopon(ObjectMap.class, Array.class, data);
				counter += big.size;
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}

