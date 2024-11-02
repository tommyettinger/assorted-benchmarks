package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IntOrderedSet;
import com.github.tommyettinger.ds.IntSet;

import java.util.function.Supplier;

public enum JDKGDXDSIntSetFact {
	DEFAULT(() -> new IntSet(12, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_I_HASH(() -> new IntSet(12, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_I_INDEXED(() -> new IntOrderedSet(12, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<IntSet> maker;

	JDKGDXDSIntSetFact(Supplier<IntSet> maker) {
		this.maker = maker;
	}
}