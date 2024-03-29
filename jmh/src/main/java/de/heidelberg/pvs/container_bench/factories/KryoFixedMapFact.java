package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.CuckooObjectMap;

import java.util.function.Supplier;

public enum KryoFixedMapFact {
	KRYO_O2O_HASH(() -> new CuckooObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<CuckooObjectMap<Object, Integer>> maker;

	KryoFixedMapFact(Supplier<CuckooObjectMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}