package de.heidelberg.pvs.container_bench.benchmarks.sets;

import com.github.tommyettinger.ds.IndexedSet;
import de.heidelberg.pvs.container_bench.factories.AtlantisIndexedSetFact;
import org.openjdk.jmh.annotations.Param;

public class AtlantisIndexedSet extends AbstractWordSetBenchmark<IndexedSet<String>> {
	@Param
	public AtlantisIndexedSetFact impl;

	@SuppressWarnings("unchecked")
	@Override
	protected IndexedSet<String> makeSet() {
		return (IndexedSet<String>) impl.maker.get();
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
