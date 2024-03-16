package de.heidelberg.pvs.container_bench.factories;

import com.esotericsoftware.kryo.util.CuckooObjectMap;

import java.util.function.Supplier;

public enum KryoMapFact {
	KRYO_O2O_HASH(() -> new CuckooObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<CuckooObjectMap<Object, Integer>> maker;

	KryoMapFact(Supplier<CuckooObjectMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}