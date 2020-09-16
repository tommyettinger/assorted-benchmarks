package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.function.Supplier;

public enum GDXXMapFact {
	GDXX_O2O_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		@Override
		protected int place(Object item) {
			int h = item.hashCode() * 0x9E377;
			return (h ^ h >>> shift) & mask;
		}
	}),
	;

	public final Supplier<ObjectMap<Object, Integer>> maker;
	
	GDXXMapFact(Supplier<ObjectMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}