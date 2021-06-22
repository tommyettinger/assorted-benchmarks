package de.heidelberg.pvs.container_bench.benchmarks.wordcount;

import de.heidelberg.pvs.container_bench.factories.JDKMapString2ObjFact;
import org.openjdk.jmh.annotations.Param;

import java.util.Map;

public class MutableJDKGetPutString extends AbstractWordcountBenchmark<Map<String, MutableInteger>> {
	@Param
	public JDKMapString2ObjFact impl;

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, MutableInteger> makeMap() {
		if (size > impl.maxsize) {
			throw new RuntimeException("Skipping because size > maxsize.");
		}
		return (Map<String, MutableInteger>) (Map<?, ?>) impl.maker.get();
	}

	@Override
	protected void count(Map<String, MutableInteger> map, String object) {
		final MutableInteger old = map.get(object);
		if (old != null) {
			old.v++;
		} else {
			map.put(object, new MutableInteger(1));
		}
	}

	@Override
	protected int size(Map<String, MutableInteger> map) {
		return map.size();
	}
}
