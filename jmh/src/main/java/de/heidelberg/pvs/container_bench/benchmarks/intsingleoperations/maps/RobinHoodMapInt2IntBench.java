package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import com.github.tommyettinger.ds.IntIntTable;
import com.github.tommyettinger.ds.PrimitiveSet;
import com.github.tommyettinger.ds.support.util.IntIterator;
import de.heidelberg.pvs.container_bench.factories.RobinHoodMapInt2IntFact;
import org.openjdk.jmh.annotations.Param;

import java.util.Set;

public class RobinHoodMapInt2IntBench extends AbstractMapInt2IntBench {

	@Param
	RobinHoodMapInt2IntFact impl;
	
	IntIntTable fullMap;
	
	@Override
	public void testSetup() {
		fullMap = impl.maker.get();
		for (int i = 0; i < keys.length; i++) {
			fullMap.put(keys[i], values[i]);
		}
	}
	
	@Override
	protected void populateBench() {
		IntIntTable newMap = impl.maker.get();
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
		IntIntTable newMap = impl.maker.get();
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
		Set<IntIntTable.Entry> int2IntEntrySet = fullMap.entrySet();
		for(IntIntTable.Entry e : int2IntEntrySet) {
			blackhole.consume(e.getKey());
			blackhole.consume(e.getValue());
		}
		
	}

}
