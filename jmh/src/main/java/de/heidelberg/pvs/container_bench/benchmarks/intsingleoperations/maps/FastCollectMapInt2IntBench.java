package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import de.heidelberg.pvs.container_bench.factories.FastCollectMapInt2IntFact;
import io.github.sooniln.fastcollect.MutableFastIterator;
import io.github.sooniln.fastcollect.ints.MutableInt2IntMap;
import io.github.sooniln.fastcollect.ints.MutableIntIterator;
import org.openjdk.jmh.annotations.Param;

public class FastCollectMapInt2IntBench extends AbstractMapInt2IntBench {

	@Param
	FastCollectMapInt2IntFact impl;
	
	MutableInt2IntMap fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		MutableInt2IntMap newMap = impl.maker.get();
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
		MutableInt2IntMap newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		MutableIntIterator it = fullMap.getKeys().iterator();
		while (it.hasNext())
			blackhole.consume(it.next());
	}

	@Override
	protected void iterateKeyValueBench() {
		MutableFastIterator<MutableInt2IntMap.MutableEntry> int2IntEntrySet = fullMap.iterator();
		while (int2IntEntrySet.hasNext()) {
			MutableInt2IntMap.MutableEntry e =int2IntEntrySet.next();
			blackhole.consume(e.getKey());
			blackhole.consume(e.getValue());
		}
	}

}
