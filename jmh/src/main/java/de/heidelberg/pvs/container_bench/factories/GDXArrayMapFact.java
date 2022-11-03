package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ArrayMap;

import java.util.function.Supplier;

public enum GDXArrayMapFact {
	GDX_O2O_ARRAY(() -> new ArrayMap<>(true, 16, Object.class, Integer.class)),
	GDX_O2O_ARRAY_UNORDERED(() -> new ArrayMap<>(false, 16, Object.class, Integer.class)),
	;

	public final Supplier<ArrayMap<Object, Integer>> maker;

	GDXArrayMapFact(Supplier<ArrayMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}