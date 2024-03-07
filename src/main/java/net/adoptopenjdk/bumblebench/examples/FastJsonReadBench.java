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
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.ds.ObjectObjectMap;
import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.util.ArrayList;
import java.util.Arrays;
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
		String data = new HeadlessFiles().local("fastjson.json").readString();
		HashMap<String, ArrayList<Vector2>> big;
		ParserConfig config = new ParserConfig();

		long counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				big = JSON.parseObject(data, HashMap.class);
				counter += big.size();
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}

	public static void main(String[] args) {
		ArrayList<ArrayList<HashMap<Vector2, String>>> deep = new ArrayList<>(8), after;
		HashMap<Vector2, String> hm0 = new HashMap<>(1);
		HashMap<Vector2, String> hm1 = new HashMap<>(ObjectObjectMap.with(new Vector2(1, 2), "1 2"));
		HashMap<Vector2, String> hm2 = new HashMap<>(ObjectObjectMap.with(new Vector2(3, 4), "3 4", new Vector2(5, 6), "5 6"));
		HashMap<Vector2, String> hm3 = new HashMap<>(ObjectObjectMap.with(new Vector2(7, 8), "7 8", new Vector2(9, 0), "9 0"));
		deep.add(new ArrayList<>(Arrays.asList(hm0, hm1)));
		deep.add(new ArrayList<>(Arrays.asList(hm2, hm3)));
		deep.add(new ArrayList<>(Arrays.asList(hm0, hm1, hm2, hm3)));

		String data = JSON.toJSONString(deep);
		System.out.println(data);
		after = JSON.parseObject(data, ArrayList.class);
		System.out.println(after);
	}
}

