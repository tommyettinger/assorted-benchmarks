package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import com.github.tommyettinger.ds.IntIntMap;
import com.github.tommyettinger.ds.PrimitiveSet;
import com.github.tommyettinger.ds.support.util.IntIterator;
import de.heidelberg.pvs.container_bench.factories.JDKGDXDSMapInt2IntFact;
import org.openjdk.jmh.annotations.Param;

import java.util.Set;

public class JDKGDXDSMapInt2IntBench extends AbstractMapInt2IntBench {

	@Param
	JDKGDXDSMapInt2IntFact impl;
	
	IntIntMap fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		IntIntMap newMap = impl.maker.get();
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
		IntIntMap newMap = impl.maker.get();
		newMap.putAll(fullMap);
		blackhole.consume(newMap);
	}

	@Override
	protected void iterateKeyBench() {
		PrimitiveSet.SetOfInt keySet = fullMap.keySet();
		IntIterator it = keySet.iterator();
		while (it.hasNext())
			blackhole.consume(it.nextInt());
	}

	@Override
	protected void iterateKeyValueBench() {
		Set<IntIntMap.Entry> int2IntEntrySet = fullMap.entrySet();
		for(IntIntMap.Entry e : int2IntEntrySet) {
			blackhole.consume(e.getKey());
			blackhole.consume(e.getValue());
		}
		
	}

}
