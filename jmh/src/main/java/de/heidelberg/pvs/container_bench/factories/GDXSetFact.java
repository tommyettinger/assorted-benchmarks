package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectSet;

import java.util.function.Function;
import java.util.function.Supplier;

public enum GDXSetFact {
	GDX_O_HASH(ObjectSet::new, ObjectSet::new), //
	;

	public final Supplier<ObjectSet<?>> maker;
	public final Function<ObjectSet<?>, ObjectSet<?>> copyMaker;

	private GDXSetFact (Supplier<ObjectSet<?>> maker, 
			Function<ObjectSet<?>, ObjectSet<?>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}
}