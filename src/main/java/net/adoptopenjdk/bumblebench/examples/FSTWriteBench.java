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

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.tommyettinger.ds.support.FourWheelRandom;
import net.adoptopenjdk.bumblebench.core.MiniBench;
import org.nustaq.serialization.FSTConfiguration;
import squidpony.StringKit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Java 17:
 * <br>
 * FSTWriteBench score: 185.658020 (185.7 522.4%)
 *           uncertainty:   1.4%
 */
public final class FSTWriteBench extends MiniBench {
	@Override
	protected int maxIterationsPerLoop() {
		return 1000007;
	}

	@Override
	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		String book = "";
		try {
			book = new String(Files.readAllBytes(Paths.get("res/bible_only_words.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String[] words = StringKit.split(book, " ");
		ObjectSet<String> unique = ObjectSet.with(words);
		HashMap<String, ArrayList<Vector2>> big = new HashMap<>(unique.size);
		FourWheelRandom random = new FourWheelRandom(12345);
		for(String u : unique){
			big.put(u, new ArrayList<>(Arrays.asList(
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f)
			)));
		}
		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
		int counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				counter += conf.asByteArray(big).length;
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}
	//--add-modules jdk.incubator.foreign --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.sql/java.sql=ALL-UNNAMED
	public static void main(String[] args) {
		String book = "";
		try {
			book = new String(Files.readAllBytes(Paths.get("res/bible_only_words.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String[] words = StringKit.split(book, " ");
		ObjectSet<String> unique = ObjectSet.with(words);
		HashMap<String, ArrayList<Vector2>> big = new HashMap<>(unique.size);
		FourWheelRandom random = new FourWheelRandom(12345);
		for(String u : unique){
			big.put(u, new ArrayList<>(Arrays.asList(
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f)
			)));
		}
		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
		System.out.println("There are " + big.size() + " keys in the Map.");

		new Lwjgl3Files().local("fst.dat").writeBytes(conf.asByteArray(big), false);
	}

}

