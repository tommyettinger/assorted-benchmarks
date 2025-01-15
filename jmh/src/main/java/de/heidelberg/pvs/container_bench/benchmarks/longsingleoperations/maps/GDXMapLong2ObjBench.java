package de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations.maps;

import com.badlogic.gdx.utils.LongMap;
import de.heidelberg.pvs.container_bench.factories.GDXMapLong2ObjFact;
import org.openjdk.jmh.annotations.Param;

import java.util.function.Consumer;

public class GDXMapLong2ObjBench extends AbstractMapLong2ObjBench {

	@Param GDXMapLong2ObjFact impl;
	
	LongMap<Object> fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		LongMap<Object> newMap = impl.maker.get();
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
		LongMap<Object> newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		LongMap.Keys keys2 = fullMap.keys();
		while(keys2.hasNext) {
			blackhole.consume(keys2.next());
		}
		blackhole.consume(keys2);
	}

	@Override
	protected void iterateKeyValueBench() {
		
		fullMap.forEach(new Consumer<LongMap.Entry<Object>>() {
			@Override
			public void accept(LongMap.Entry<Object> t) {
				blackhole.consume(t.key);
				blackhole.consume(t.value);
			}
		});
	}

}
