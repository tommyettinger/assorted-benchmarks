package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.function.Supplier;

public enum GDXMapFact {
	GDX_O2O_HASH(() -> new ObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<ObjectMap<Object, Integer>> maker;
	
	GDXMapFact (Supplier<ObjectMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}