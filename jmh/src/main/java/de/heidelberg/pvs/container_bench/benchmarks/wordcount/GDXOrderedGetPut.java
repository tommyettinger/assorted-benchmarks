package de.heidelberg.pvs.container_bench.benchmarks.wordcount;

import com.badlogic.gdx.utils.OrderedMap;
import de.heidelberg.pvs.container_bench.factories.GDXOrderedMapFact;
import org.openjdk.jmh.annotations.Param;

/**
 * Adapter using libGDX OrderedMap
 * 
 * @author Erich Schubert
 */
public class GDXOrderedGetPut extends AbstractWordcountBenchmark<OrderedMap<Object, Integer>> {
	@Param
	public GDXOrderedMapFact impl;

	@Override
	protected OrderedMap<Object, Integer> makeMap() {
		return impl.maker.get();
	}

	@Override
	protected void count(OrderedMap<Object, Integer> map, String object) {
		Integer old = map.get(object);
		map.put(object, old != null ? old + 1 : 1);
	}

	@Override
	protected int size(OrderedMap<Object, Integer> map) {
		return map.size;
	}
}