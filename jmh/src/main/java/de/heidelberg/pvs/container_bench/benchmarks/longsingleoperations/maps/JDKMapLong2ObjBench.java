package de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations.maps;

import de.heidelberg.pvs.container_bench.factories.JDKMapLong2ObjFact;
import org.openjdk.jmh.annotations.Param;

import java.util.Map;
import java.util.Set;

public class JDKMapLong2ObjBench extends AbstractMapLong2ObjBench {

	@Param
	JDKMapLong2ObjFact impl;
	
	Map<Long, Object> fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		Map<Long, Object> newMap = impl.maker.get();
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
		Map<Long, Object> newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		Set<Long> keySet = fullMap.keySet();
		for (Long key : keySet){
			blackhole.consume(key);
		}
	}

	@Override
	protected void iterateKeyValueBench() {
		Set<Map.Entry<Long, Object>> long2objEntrySet = fullMap.entrySet();
		for(Map.Entry<Long, Object> e : long2objEntrySet) {
			blackhole.consume(e.getKey());
			blackhole.consume(e.getValue());
		}
		
	}

}
