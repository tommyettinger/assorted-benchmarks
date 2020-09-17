package de.heidelberg.pvs.container_bench.benchmarks.wordcount;

import com.badlogic.gdx.utils.ObjectMap;
import de.heidelberg.pvs.container_bench.factories.GDXXMapFact;
import org.openjdk.jmh.annotations.Param;

/**
 * Adapter using libGDX ObjectSet... but x-ier.
 * 
 * @author Erich Schubert
 */
public class GDXXGetPut extends AbstractWordcountBenchmark<ObjectMap<Object, Integer>> {
	@Param
	public GDXXMapFact impl;

	@Override
	protected ObjectMap<Object, Integer> makeMap() {
		return impl.maker.get();
	}

	@Override
	protected void count(ObjectMap<Object, Integer> map, String object) {
		Integer old = map.get(object);
		map.put(object, old != null ? old + 1 : 1);
	}

	@Override
	protected int size(ObjectMap<Object, Integer> map) {
		return map.size;
	}
}