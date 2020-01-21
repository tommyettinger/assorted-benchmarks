package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectIntMap;

import java.util.function.Supplier;

public enum GDXMap2IntFact {
	GDX_O2I_HASH(ObjectIntMap::new), //
	;

	public final Supplier<ObjectIntMap<Object>> maker;

	GDXMap2IntFact (Supplier<ObjectIntMap<Object>> maker) {
		this.maker = maker;
	}
}