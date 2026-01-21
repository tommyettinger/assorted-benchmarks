package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import com.badlogic.gdx.utils.IntIntMap;
import de.heidelberg.pvs.container_bench.factories.FastutilMapInt2IntFact;
import de.heidelberg.pvs.container_bench.factories.GDXMapInt2IntFact;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.openjdk.jmh.annotations.Param;

import java.util.Set;

public class GDXMapInt2IntBench extends AbstractMapInt2IntBench {

	@Param
	GDXMapInt2IntFact impl;
	
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
		IntIntMap.Keys it = fullMap.keys();
		while (it.hasNext)
			blackhole.consume(it.next());
	}

	@Override
	protected void iterateKeyValueBench() {
		IntIntMap.Entries int2IntEntrySet = fullMap.entries();
		for(IntIntMap.Entry e : int2IntEntrySet) {
			blackhole.consume(e.key);
			blackhole.consume(e.value);
		}
	}

}
