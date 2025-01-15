package de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations.maps;

import com.github.tommyettinger.ds.*;
import com.github.tommyettinger.ds.support.util.LongIterator;
import de.heidelberg.pvs.container_bench.factories.JDKGDXDSMapLong2ObjFact;
import org.openjdk.jmh.annotations.Param;

public class JDKGDXDSMapLong2ObjBench extends AbstractMapLong2ObjBench {

	@Param
	JDKGDXDSMapLong2ObjFact impl;
	
	LongObjectMap<Object> fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		LongObjectMap<Object> newMap = impl.maker.get();
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
		LongObjectMap<Object> newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		LongIterator keySet = fullMap.keySet().iterator();
		while (keySet.hasNext()){
			blackhole.consume(keySet.nextLong());
		}
	}

	@Override
	protected void iterateKeyValueBench() {
		LongObjectMap.Entries<Object> long2ObjEntrySet = fullMap.entrySet();
		for(LongObjectMap.Entry<Object> e : long2ObjEntrySet) {
			blackhole.consume(e.getKey());
			blackhole.consume(e.getValue());
		}
		
	}

}
