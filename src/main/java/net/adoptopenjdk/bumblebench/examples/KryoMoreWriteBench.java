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
import com.badlogic.gdx.utils.OrderedSet;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.tommyettinger.ds.Utilities;
import com.github.tommyettinger.random.FourWheelRandom;
import net.adoptopenjdk.bumblebench.core.MiniBench;
import squidpony.StringKit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * KryoMoreWriteBench score: 466.033508 (466.0 614.4%)
 *                uncertainty:   2.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * KryoMoreWriteBench score: 355.941071 (355.9 587.5%)
 *                uncertainty:   1.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * KryoMoreWriteBench score: 440.114502 (440.1 608.7%)
 *                uncertainty:   2.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * KryoMoreWriteBench score: 422.688416 (422.7 604.7%)
 *                uncertainty:   8.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * KryoMoreWriteBench score: 435.889465 (435.9 607.7%)
 *                uncertainty:   2.6%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * KryoMoreWriteBench score: 409.629669 (409.6 601.5%)
 *                uncertainty:   4.5%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * KryoMoreWriteBench score: 416.224670 (416.2 603.1%)
 *                uncertainty:   0.6%
 */
public final class KryoMoreWriteBench extends MiniBench {
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
		OrderedSet<String> unique = OrderedSet.with(words);
		ObjectObjectMap<String, ObjectList<Vector2>> big = new ObjectObjectMap<>(unique.size);
		FourWheelRandom random = new FourWheelRandom(12345);
		for(String u : unique){
			big.put(u, ObjectList.with(
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f)
			));
		}

		Kryo kryo = new Kryo();
		kryo.register(Vector2.class);
		kryo.register(ObjectList.class, new CollectionSerializer(){
			@Override
			protected ObjectList create(Kryo kryo, Input input, Class type, int size) {
				return new ObjectList(size);
			}
		});
		kryo.register(ObjectObjectMap.class, new MapSerializer(){
			@Override
			protected ObjectObjectMap create(Kryo kryo, Input input, Class type, int size) {
				return new ObjectObjectMap((int)(size / Utilities.getDefaultLoadFactor()+1), Utilities.getDefaultLoadFactor());
			}
		});

//		Kryo kryo = new Kryo();
//		kryo.register(Vector2.class);
//		CollectionSerializer<ObjectList<Vector2>> cs = new CollectionSerializer<>();
//		cs.setElementClass(Vector2.class);
//		kryo.register(ObjectList.class, cs);
//		MapSerializer<ObjectObjectMap<String, ObjectList<Vector2>>> ms = new MapSerializer<>();
//		ms.setKeyClass(String.class);
//		ms.setValueClass(ObjectList.class, cs);
//		kryo.register(ObjectObjectMap.class, ms);

		int counter = 0;
		for (long i = 0; i < numLoops; i++) {
			for (int j = 0; j < numIterationsPerLoop; j++) {
				startTimer();
				Output output = new Output(65536, -1);
				kryo.writeObject(output, big);
				counter += output.total();
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
		ObjectObjectMap<String, ObjectList<Vector2>> big = new ObjectObjectMap<>(unique.size);
		FourWheelRandom random = new FourWheelRandom(12345);
		for(String u : unique){
			big.put(u, ObjectList.with(
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f),
					new Vector2(random.nextExclusiveFloat() - 0.5f, random.nextExclusiveFloat() - 0.5f)
			));
		}

		System.out.println("There are " + big.size() + " keys in the Map.");

		Kryo kryo = new Kryo();
		kryo.register(Vector2.class);
		kryo.register(ObjectList.class, new CollectionSerializer(){
			@Override
			protected ObjectList create(Kryo kryo, Input input, Class type, int size) {
				return new ObjectList(size);
			}
		});
		kryo.register(ObjectObjectMap.class, new MapSerializer(){
			@Override
			protected ObjectObjectMap create(Kryo kryo, Input input, Class type, int size) {
				return new ObjectObjectMap((int)(size / Utilities.getDefaultLoadFactor()+1), Utilities.getDefaultLoadFactor());
			}
		});

//		kryo.register(Vector2.class);
//		kryo.register(ObjectList.class, new CollectionSerializer<ObjectList<Vector2>>(){
//			@Override
//			protected ObjectList<Vector2> create(Kryo kryo, Input input, Class type, int size) {
//				return new ObjectList<>(size);
//			}
//		});
//		kryo.register(ObjectObjectMap.class, new MapSerializer<ObjectObjectMap<String, ObjectList<Vector2>>>(){
//			@Override
//			protected ObjectObjectMap<String, ObjectList<Vector2>> create(Kryo kryo, Input input, Class type, int size) {
//				return new ObjectObjectMap<>((int)(size / Utilities.getDefaultLoadFactor()+1), Utilities.getDefaultLoadFactor());
//			}
//		});

//		Kryo kryo = new Kryo();
//		kryo.register(Vector2.class);
//		CollectionSerializer<ObjectList<Vector2>> cs = new CollectionSerializer<>();
//		cs.setElementClass(Vector2.class);
//		kryo.register(ObjectList.class, cs);
//		MapSerializer<ObjectObjectMap<String, ObjectList<Vector2>>> ms = new MapSerializer<>();
//		ms.setKeyClass(String.class);
//		ms.setValueClass(ObjectList.class, cs);
//		kryo.register(ObjectObjectMap.class, ms);

		try {
			Output output = new Output(new FileOutputStream("kryomore.dat"));
			kryo.writeObject(output, big);
			output.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}

