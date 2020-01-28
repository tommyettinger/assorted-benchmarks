package de.heidelberg.pvs.container_bench.benchmarks.sets;

import com.badlogic.gdx.utils.OrderedSet;
import de.heidelberg.pvs.container_bench.factories.GDXOrderedSetFact;
import org.openjdk.jmh.annotations.Param;

public class GDXOrderedSet extends AbstractWordSetBenchmark<OrderedSet<String>> {
	@Param
	public GDXOrderedSetFact impl;

	@SuppressWarnings("unchecked")
	@Override
	protected OrderedSet<String> makeSet() {
		return (OrderedSet<String>) impl.maker.get();
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
