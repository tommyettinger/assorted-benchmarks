package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.LongObjectMap;
import com.github.tommyettinger.ds.LongObjectOrderedMap;

import java.util.function.Supplier;

public enum JDKGDXDSMapLong2ObjFact {
	JDKGDXDS_L2O_PRIMITIVE_HASH(() -> new LongObjectMap<>(8, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_L2O_PRIMITIVE_ORDERED(() -> new LongObjectOrderedMap<>(8, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_L2O_PRIMITIVE_UNMIXED(() -> new LongObjectMap<Object>(8, LoadFactor.LOAD_FACTOR) {
		@Override
		protected int place(long item) {
			return (int)(item) & mask;
		}
	}),
	;

	public final Supplier<LongObjectMap<Object>> maker;

	JDKGDXDSMapLong2ObjFact(Supplier<LongObjectMap<Object>> maker) {
		this.maker = maker;
	}
}