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
import org.nustaq.serialization.FSTConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Java 17:
 * <br>
 * FSTReadBench score: 205.444519 (205.4 532.5%)
 *          uncertainty:   1.2%
 */
public final class FSTReadBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		byte[] data = new Lwjgl3Files().local("fst.dat").readBytes();
		HashMap<String, ArrayList<Vector2>> big;
		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

		int counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				big = (HashMap<String, ArrayList<Vector2>>) conf.asObject(data);
				counter += big.size();
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}

