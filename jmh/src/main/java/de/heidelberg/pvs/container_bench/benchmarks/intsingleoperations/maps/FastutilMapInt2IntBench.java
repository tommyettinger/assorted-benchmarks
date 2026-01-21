package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import de.heidelberg.pvs.container_bench.factories.FastutilMapInt2IntFact;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.openjdk.jmh.annotations.Param;

import java.util.Set;

public class FastutilMapInt2IntBench extends AbstractMapInt2IntBench {

	@Param
	FastutilMapInt2IntFact impl;
	
	Int2IntMap fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		Int2IntMap newMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			newMap.put(keys[i], values[i]);
		}
		blackhole.consume(newMap);
	}


	@Override
	protected void containsBench() {
		int index = keyGenerator.generateIndex(size);
		blackhole.consume(fullMap.containsKey(keys[index]));
	}

	@Override
	protected void copyBench() {
		Int2IntMap newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		IntSet keySet = fullMap.keySet();
		IntIterator it = keySet.intIterator();
		while (it.hasNext())
			blackhole.consume(it.nextInt());
	}

	@Override
	protected void iterateKeyValueBench() {
		Set<Int2IntMap.Entry> int2IntEntrySet = fullMap.int2IntEntrySet();
		for(Int2IntMap.Entry e : int2IntEntrySet) {
			blackhole.consume(e.getIntKey());
			blackhole.consume(e.getIntValue());
		}
		
	}

}
