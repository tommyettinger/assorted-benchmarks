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

import com.badlogic.gdx.utils.ObjectSet;
import net.adoptopenjdk.bumblebench.core.MiniBench;
import squidpony.StringKit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * At load factor 0.5f:
 * When run with JVM:
 * {@code OpenJDK 64-Bit Server VM AdoptOpenJDK (build 13+33, mixed mode, sharing)} (HotSpot)
 * This gets these results (higher is better):
 * <br>
 * ObjectSet_String_Bench score: 25582462.000000 (25.58M 1705.7%)
 *                    uncertainty:   1.1%
 * <br>
 * When run with JVM:
 * {@code Eclipse OpenJ9 VM AdoptOpenJDK (build master-99e396a57, JRE 13 Windows 7 amd64-64-Bit Compressed References 20191030_96 (JIT enabled, AOT enabled)}
 * This gets different results:
 * <br>
 * 
 */
public final class ObjectSet_String_Bench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		final ObjectSet<String> coll = new ObjectSet<>(16, 0.5f);
		String book = "";
		try {
			book = new String(Files.readAllBytes(Paths.get("res/bible_only_words.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String[] words = StringKit.split(book, " ");
		final int length = words.length, mask = Integer.highestOneBit(length) - 1;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				coll.add(words[j & mask]);
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
}

