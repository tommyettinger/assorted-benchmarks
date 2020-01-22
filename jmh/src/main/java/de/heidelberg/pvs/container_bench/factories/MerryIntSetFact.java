package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.merry.lp.IntSet;

import java.util.function.Supplier;

public enum MerryIntSetFact {
	MERRY_I_HASH(IntSet::new), //
	;

	public final Supplier<IntSet> maker;
	
	private MerryIntSetFact (Supplier<IntSet> maker) {
		this.maker = maker;
	}
}