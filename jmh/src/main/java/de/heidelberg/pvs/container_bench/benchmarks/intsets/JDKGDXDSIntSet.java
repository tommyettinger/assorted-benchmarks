package de.heidelberg.pvs.container_bench.benchmarks.intsets;

import com.github.tommyettinger.ds.IntSet;
import de.heidelberg.pvs.container_bench.factories.JDKGDXDSIntSetFact;
import org.openjdk.jmh.annotations.Param;

public class JDKGDXDSIntSet extends AbstractIntSetBenchmark<IntSet> {
	@Param
	public JDKGDXDSIntSetFact impl;

	@Override
	protected IntSet makeSet() {
		return impl.maker.get();
	}

	@Override
	protected void add(int object) {
		set.add(object);
	}

	@Override
	protected boolean contains(int object) {
		return set.contains(object);
	}

	@Override
	protected void remove(int object) {
		set.remove(object);
	}
}
