package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.IntSet;

import java.util.function.Supplier;

public enum GDXIntSetFact {
	GDX_I_HASH(IntSet::new), //
	;

	public final Supplier<IntSet> maker;
	
	private GDXIntSetFact (Supplier<IntSet> maker) {
		this.maker = maker;
	}
}