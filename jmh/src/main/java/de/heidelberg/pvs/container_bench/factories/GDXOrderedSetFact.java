package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.OrderedSet;

import java.util.function.Function;
import java.util.function.Supplier;

public enum GDXOrderedSetFact {
	GDX_O_ORDERED(() -> new OrderedSet<>(16, LoadFactor.LOAD_FACTOR), OrderedSet::new),
	;

	public final Supplier<OrderedSet<?>> maker;
	public final Function<OrderedSet<?>, OrderedSet<?>> copyMaker;

	private GDXOrderedSetFact(Supplier<OrderedSet<?>> maker,
                              Function<OrderedSet<?>, OrderedSet<?>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}
}