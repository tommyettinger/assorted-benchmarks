package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectSet;

import java.util.function.Function;
import java.util.function.Supplier;

public enum GDXSetFact {
	GDX_O_HASH(ObjectSet::new, ObjectSet::new), //
	;

	public final Supplier<ObjectSet<Object>> maker;
	public final Function<ObjectSet<Object>, ObjectSet<Object>> copyMaker;

	private GDXSetFact (Supplier<ObjectSet<Object>> maker, 
			Function<ObjectSet<Object>, ObjectSet<Object>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}
}