package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import com.badlogic.gdx.utils.ObjectIntMap;
import de.heidelberg.pvs.container_bench.factories.GDXMap2IntFact;
import org.openjdk.jmh.annotations.Param;

import java.util.function.Consumer;

public class GDXMap2IntBench extends AbstractMap2IntBench {

	@Param GDXMap2IntFact impl;
	
	ObjectIntMap<Object> fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		ObjectIntMap<Object> newMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			newMap.put(keys[i], values[i]);
		}
		blackhole.consume(newMap);
	}

	@Override
	protected void containsBench() {
		int index = keyGenerator.generateIndex(size);
		fullMap.containsKey(keys[index]);
	}

	@Override
	protected void copyBench() {
		ObjectIntMap<Object> newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		ObjectIntMap.Keys<Object> keys2 = fullMap.keys();
		for(Object e : keys2) {
			blackhole.consume(e);
		}
		blackhole.consume(keys2);
	}

	@Override
	protected void iterateKeyValueBench() {
		
		fullMap.forEach(new Consumer<ObjectIntMap.Entry<Object>>() {
			@Override
			public void accept(ObjectIntMap.Entry<Object> t) {
				blackhole.consume(t.key);
				blackhole.consume(t.value);
			}
		});
	}

}
