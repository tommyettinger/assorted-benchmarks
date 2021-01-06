package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.IntSet;

import java.util.function.Supplier;

public enum GDXIntSetFact {
	GDX_I_HASH(() -> new IntSet(16, LoadFactor.LOAD_FACTOR)), //
	;

	public final Supplier<IntSet> maker;
	
	GDXIntSetFact (Supplier<IntSet> maker) {
		this.maker = maker;
	}
}