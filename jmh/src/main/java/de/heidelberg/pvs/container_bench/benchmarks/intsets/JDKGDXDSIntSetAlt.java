package de.heidelberg.pvs.container_bench.benchmarks.intsets;

import de.heidelberg.pvs.container_bench.IntSetAlt;
import de.heidelberg.pvs.container_bench.factories.JDKGDXDSIntSetAltFact;
import org.openjdk.jmh.annotations.Param;

public class JDKGDXDSIntSetAlt extends AbstractIntSetBenchmark<IntSetAlt> {
	@Param
	public JDKGDXDSIntSetAltFact impl;

	@Override
	protected IntSetAlt makeSet() {
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
