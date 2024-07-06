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
import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * With deserializeJavaObject():
 * <br>
 * Java 17:
 * <br>
 * FuryReadBench score: 640.986328 (641.0 646.3%)
 *           uncertainty:   1.2%
 * <br>
 * With deserialize():
 * <br>
 * Java 17:
 * <br>
 * FuryReadBench score: 632.360962 (632.4 644.9%)
 *           uncertainty:   2.1%
 */
public final class FuryReadBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		byte[] data = new HeadlessFiles().local("fury.dat").readBytes();
		HashMap<String, ArrayList<Vector2>> big;
		Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
		fury.register(HashMap.class);
		fury.register(ArrayList.class);
		fury.register(Vector2.class);

		long counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				big = fury.deserializeJavaObject(data, HashMap.class);
				counter += big.size();
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}

