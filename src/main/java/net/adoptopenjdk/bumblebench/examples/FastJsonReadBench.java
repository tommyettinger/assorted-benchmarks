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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.math.Vector2;
import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Java 17:
 * <br>
 * FastJsonReadBench score: 57.170677 (57.17 404.6%)
 *               uncertainty:   2.6%
 */
public final class FastJsonReadBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		String data = new Lwjgl3Files().local("fastjson.json").readString();
		HashMap<String, ArrayList<Vector2>> big;
		ParserConfig config = new ParserConfig(true);

		int counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				big = JSON.parseObject(data, HashMap.class, config);
				counter += big.size();
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}

