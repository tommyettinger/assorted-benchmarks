package de.heidelberg.pvs.container_bench.factories;

import ds.merry.ObjectSet;

import java.util.function.Function;
import java.util.function.Supplier;

public enum MerrySetFact {
	MERRY_O_HASH(ObjectSet::new, ObjectSet::new), //
	;

	public final Supplier<ObjectSet<?>> maker;
	public final Function<ObjectSet<?>, ObjectSet<?>> copyMaker;

	private MerrySetFact (Supplier<ObjectSet<?>> maker, 
			Function<ObjectSet<?>, ObjectSet<?>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}
}