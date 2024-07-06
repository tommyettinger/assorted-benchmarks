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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.github.tommyettinger.random.FourWheelRandom;
import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.apache.fury.memory.MemoryBuffer;
import net.adoptopenjdk.bumblebench.core.MiniBench;
import squidpony.StringKit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * With serializeJavaObject():
 * <br>
 * Java 17:
 * <br>
 * FuryWriteBench score: 526.263489 (526.3 626.6%)
 *            uncertainty:   8.4%
 * <br>
 * With serialize():
 * <br>
 * Java 17:
 * <br>
 * FuryWriteBench score: 521.859558 (521.9 625.7%)
 *            uncertainty:  19.0%
 */
public final class FuryWriteBench extends MiniBench {
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
		Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
		fury.register(HashMap.class);
		fury.register(ArrayList.class);
		fury.register(Vector2.class);

		MemoryBuffer mem = MemoryBuffer.newHeapBuffer(65536);
		long counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				fury.serializeJavaObject(mem, big);
				counter += mem.size();
				pauseTimer();
			}
		}
		return numLoops * numIterationsPerLoop;
	}

	public static void main(String[] args) {
		String book = "";
		try {
			book = new String(Files.readAllBytes(Paths.get("res/bible_only_words.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String[] words = StringKit.split(book, " ");
		OrderedSet<String> unique = OrderedSet.with(words);
		HashMap<String, ArrayList<Vector2>> big = new HashMap<>(unique.size);
		FourWheelRandom random = new FourWheelRandom(12345);
		for(String u : unique){
			big.put(u, new ArrayList<>(Arrays.asList(
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f)
			)));
		}

		System.out.println("There are " + big.size() + " keys in the Map.");

		Fury fury = Fury.builder().withLanguage(Language.JAVA).build();
		fury.register(HashMap.class);
		fury.register(ArrayList.class);
		fury.register(Vector2.class);

		try {
			FileOutputStream stream = new FileOutputStream("fury.dat");
			byte[] bytes = fury.serializeJavaObject(big);
			System.out.println("Fury serialized data is " + bytes.length + " bytes in size.");
			stream.write(bytes);
			stream.flush();
			stream.close();
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

