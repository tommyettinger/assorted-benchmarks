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
import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Java 17:
 * <br>
 * SerializableReadBench score: 83.329773 (83.33 442.3%)
 *                   uncertainty:   1.1%
 */
public final class SerializableReadBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		byte[] data = new HeadlessFiles().local("ser.dat").readBytes();
		HashMap<String, ArrayList<Vector2>> big;

		long counter = 0;
		try {
			for (long i = 0; i < numLoops; i++) {
				for (int j = 0; j < numIterationsPerLoop; j++) {
					startTimer();
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					ObjectInputStream input = null;
					input = new ObjectInputStream(bais);
					big = (HashMap<String, ArrayList<Vector2>>) input.readObject();
					counter += big.size();
					pauseTimer();
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return numLoops * numIterationsPerLoop;
	}
}

