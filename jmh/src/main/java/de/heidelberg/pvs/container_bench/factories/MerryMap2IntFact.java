package de.heidelberg.pvs.container_bench.factories;

import ds.merry.ObjectIntMap;

import java.util.function.Supplier;

public enum MerryMap2IntFact {
	MERRY_O2I_HASH(ObjectIntMap::new), //
	;

	public final Supplier<ObjectIntMap<Object>> maker;

	MerryMap2IntFact (Supplier<ObjectIntMap<Object>> maker) {
		this.maker = maker;
	}
}