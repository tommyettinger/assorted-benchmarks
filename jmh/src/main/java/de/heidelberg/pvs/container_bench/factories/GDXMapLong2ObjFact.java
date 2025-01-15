package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.LongMap;

import java.util.function.Supplier;

public enum GDXMapLong2ObjFact {
	GDX_L2O_HASH(() -> new LongMap<>(8, LoadFactor.LOAD_FACTOR)), //
	;

	public final Supplier<LongMap<Object>> maker;

	GDXMapLong2ObjFact(Supplier<LongMap<Object>> maker) {
		this.maker = maker;
	}
}