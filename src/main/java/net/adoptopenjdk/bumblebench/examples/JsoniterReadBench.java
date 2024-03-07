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
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.TypeLiteral;
import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Java 8:
 * <br>
 * JsoniterReadBench score: 145.798828 (145.8 498.2%)
 *               uncertainty:   0.5%
 * <br>
 * Java 17:
 * <br>
 * JsoniterReadBench score: 178.906281 (178.9 518.7%)
 *               uncertainty:   2.1%
 */
public final class JsoniterReadBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		String data = new HeadlessFiles().local("jsoniter.json").readString();
		HashMap<String, ArrayList<Vector2>> big;
		Config cfg = new Config.Builder()
				.omitDefaultValue(true)
				.build();
		JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
		TypeLiteral<HashMap<String, ArrayList<Vector2>>> tl = new TypeLiteral<HashMap<String, ArrayList<Vector2>>>(){};
		long counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				big = JsonIterator.deserialize(cfg, data, tl);
				counter += big.size();
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
	public static void main(String[] args) {
		byte[] data = new HeadlessFiles().local("jsoniter.json").readBytes();
		Config cfg = new Config.Builder()
				.omitDefaultValue(true)
				.build();
		JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);

		HashMap<String, ArrayList<Vector2>> big = JsonIterator.deserialize(cfg, data,
				new TypeLiteral<HashMap<String, ArrayList<Vector2>>>(){});
		System.out.println(big.get("whoremongers")); // big pimpin' King James
	}
}

