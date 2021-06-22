package de.heidelberg.pvs.container_bench.benchmarks.wordcount;

import de.heidelberg.pvs.container_bench.factories.JDKMapString2ObjFact;
import org.openjdk.jmh.annotations.Param;

import java.util.Map;

public class MutableJDK8ComputeString extends AbstractWordcountBenchmark<Map<String, MutableInteger>> {
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
		map.compute(object, (key, value) -> {
			if (value == null) {
				value = new MutableInteger(1);
			} else {
				value.v += 1;
			}
			return value;
		});
	}

	@Override
	protected int size(Map<String, MutableInteger> map) {
		return map.size();
	}
}
