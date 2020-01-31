package de.heidelberg.pvs.container_bench.benchmarks.sets;

import ds.merry.ObjectSet;
import de.heidelberg.pvs.container_bench.factories.MerrySetFact;
import org.openjdk.jmh.annotations.Param;

public class MerryObjectSet extends AbstractWordSetBenchmark<ObjectSet<String>> {
	@Param
	public MerrySetFact impl;

	@SuppressWarnings("unchecked")
	@Override
	protected ObjectSet<String> makeSet() {
		return (ObjectSet<String>) impl.maker.get();
	}

	@Override
	protected void add(String object) {
		set.add(object);
	}

	@Override
	protected boolean contains(String object) {
		return set.contains(object);
	}

	@Override
	protected void remove(String object) {
		set.remove(object);
	}
}
