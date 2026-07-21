package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IntIntMap;
import com.github.tommyettinger.ds.IntIntMapChanging;
import com.github.tommyettinger.ds.IntIntOrderedMap;

import java.util.function.Supplier;

public enum JDKGDXDSMapInt2IntFact {
	JDKGDXDS_I2I_HASH(() -> new IntIntMap(16, LoadFactor.LOAD_FACTOR)), //
	JDKGDXDS_I2I_CHANGING(() -> new IntIntMapChanging(16, LoadFactor.LOAD_FACTOR)), //
	JDKGDXDS_I2I_ORDERED(() -> new IntIntOrderedMap(16, LoadFactor.LOAD_FACTOR)), //
	;

	public final Supplier<IntIntMap> maker;

	JDKGDXDSMapInt2IntFact(Supplier<IntIntMap> maker) {
		this.maker = maker;
	}
}