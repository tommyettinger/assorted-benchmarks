package de.heidelberg.pvs.container_bench.benchmarks.sets;

import com.badlogic.gdx.utils.ObjectSet;
import de.heidelberg.pvs.container_bench.factories.GDXSetFact;
import org.openjdk.jmh.annotations.Param;

public class GDXObjectSet extends AbstractWordSetBenchmark<ObjectSet<String>> {
	@Param
	public GDXSetFact impl;

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
