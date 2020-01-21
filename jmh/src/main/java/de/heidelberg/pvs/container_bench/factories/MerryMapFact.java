package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.merry.lp.ObjectMap;

import java.util.function.Supplier;

public enum MerryMapFact {
	MERRY_O2O_HASH(ObjectMap::new), //
	;

	public final Supplier<ObjectMap<Object, Integer>> maker;
	
	MerryMapFact (Supplier<ObjectMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}