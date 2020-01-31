package de.heidelberg.pvs.container_bench.factories;

import ds.merry.OrderedSet;

import java.util.function.Function;
import java.util.function.Supplier;

public enum MerryOrderedSetFact {
	MERRY_O_ORDERED(OrderedSet::new, OrderedSet::new),
	;

	public final Supplier<OrderedSet<?>> maker;
	public final Function<OrderedSet<?>, OrderedSet<?>> copyMaker;

	private MerryOrderedSetFact(Supplier<OrderedSet<?>> maker,
                                Function<OrderedSet<?>, OrderedSet<?>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}
}