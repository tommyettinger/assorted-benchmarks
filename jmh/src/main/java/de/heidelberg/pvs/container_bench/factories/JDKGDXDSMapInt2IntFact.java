package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IntIntMap;

import java.util.function.Supplier;

public enum JDKGDXDSMapInt2IntFact {
	GDX_O2I_HASH(() -> new IntIntMap(16, LoadFactor.LOAD_FACTOR)), //
	;

	public final Supplier<IntIntMap> maker;

	JDKGDXDSMapInt2IntFact(Supplier<IntIntMap> maker) {
		this.maker = maker;
	}
}