package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.function.Supplier;

public enum GDXOrderedMapFact {
	GDX_O2O_ORDERED(() -> new OrderedMap<>(16, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<OrderedMap<Object, Integer>> maker;

	GDXOrderedMapFact(Supplier<OrderedMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}