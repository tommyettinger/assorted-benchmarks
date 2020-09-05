package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IndexedSet;

import java.util.function.Function;
import java.util.function.Supplier;

public enum AtlantisIndexedSetFact {
	ATLANTIS_O_INDEXED(() -> new IndexedSet(16, 0.8f), IndexedSet::new),
	;

	public final Supplier<IndexedSet<?>> maker;
	public final Function<IndexedSet<?>, IndexedSet<?>> copyMaker;

	private AtlantisIndexedSetFact(Supplier<IndexedSet<?>> maker,
								   Function<IndexedSet<?>, IndexedSet<?>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
	}
}
