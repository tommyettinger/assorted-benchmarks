package de.heidelberg.pvs.container_bench.factories;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.util.function.Supplier;

public enum FastutilMapLong2ObjFact {
	FASTUTIL_L2O_PRIMITIVE_HASH(it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap::new), //
	FASTUTIL_L2O_PRIMITIVE_LINKEDHASH(it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap::new), //
	FASTUTIL_L2O_PRIMITIVE_AVL(it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap::new), //
	FASTUTIL_L2O_PRIMITIVE_RB(it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap::new), //
	FASTUTIL_L2O_PRIMITIVE_ARRAY(it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap::new, 1000000), //
	;

	public final Supplier<Long2ObjectMap<Object>> maker;

	public final int maxsize;

	private FastutilMapLong2ObjFact(Supplier<Long2ObjectMap<Object>> maker) {
		this(maker, Integer.MAX_VALUE);
	}

	private FastutilMapLong2ObjFact(Supplier<Long2ObjectMap<Object>> maker, int maxsize) {
		this.maker = maker;
		this.maxsize = maxsize;
	}
}