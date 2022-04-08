package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.Array;

import java.util.function.Function;
import java.util.function.Supplier;

public enum GDXArrayFact {

	GDX_ARRAY(Array<Object>::new, Array<Object>::new); //

	public final Supplier<Array<Object>> maker;
	public final Function<Array<Object>, Array<Object>> copyMaker;

	private GDXArrayFact(Supplier<Array<Object>> maker,
                         Function<Array<Object>, Array<Object>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}

}
