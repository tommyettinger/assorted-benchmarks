package de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations.maps;

import de.heidelberg.pvs.container_bench.factories.FastutilMapLong2ObjFact;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.openjdk.jmh.annotations.Param;

public class FastutilMapLong2ObjBench extends AbstractMapLong2ObjBench {

	@Param
	FastutilMapLong2ObjFact impl;
	
	Long2ObjectMap<Object> fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		Long2ObjectMap<Object> newMap = impl.maker.get();
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
		Long2ObjectMap<Object> newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		LongSet keySet = fullMap.keySet();
		for(Object key : keySet) {
			blackhole.consume(key);
		}
	}

	@Override
	protected void iterateKeyValueBench() {
		ObjectSet<Entry<Object>> long2ObjectEntrySet = fullMap.long2ObjectEntrySet();
		for(Entry<Object> e : long2ObjectEntrySet) {
			blackhole.consume(e.getLongKey());
			blackhole.consume(e.getValue());
		}
		
	}

}
