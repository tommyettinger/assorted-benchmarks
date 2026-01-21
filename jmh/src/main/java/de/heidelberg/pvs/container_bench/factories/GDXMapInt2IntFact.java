package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.IntIntMap;

import java.util.function.Supplier;

public enum GDXMapInt2IntFact {
	GDX_I2I_HASH(() -> new IntIntMap(16, LoadFactor.LOAD_FACTOR)), //
	;

	public final Supplier<IntIntMap> maker;

	GDXMapInt2IntFact(Supplier<IntIntMap> maker) {
		this.maker = maker;
	}
}