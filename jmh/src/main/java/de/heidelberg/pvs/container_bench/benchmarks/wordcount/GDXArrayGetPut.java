package de.heidelberg.pvs.container_bench.benchmarks.wordcount;

import com.badlogic.gdx.utils.ArrayMap;
import de.heidelberg.pvs.container_bench.factories.GDXArrayMapFact;
import org.openjdk.jmh.annotations.Param;

/**
 * Adapter using libGDX ArrayMap
 * 
 * @author Erich Schubert
 */
public class GDXArrayGetPut extends AbstractWordcountBenchmark<ArrayMap<Object, Integer>> {
	@Param
	public GDXArrayMapFact impl;

	@Override
	protected ArrayMap<Object, Integer> makeMap() {
		return impl.maker.get();
	}

	@Override
	protected void count(ArrayMap<Object, Integer> map, String object) {
		Integer old = map.get(object);
		map.put(object, old != null ? old + 1 : 1);
	}

	@Override
	protected int size(ArrayMap<Object, Integer> map) {
		return map.size;
	}
}