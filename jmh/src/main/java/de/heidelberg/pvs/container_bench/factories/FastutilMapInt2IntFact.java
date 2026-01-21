package de.heidelberg.pvs.container_bench.factories;

import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.function.Supplier;

public enum FastutilMapInt2IntFact {
	FASTUTIL_I2I_HASH(it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap::new), //
	FASTUTIL_I2I_LINKEDHASH(it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap::new), //
	FASTUTIL_I2I_AVL(it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap::new), //
	FASTUTIL_I2I_RB(it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap::new), //
	FASTUTIL_I2I_ARRAY(it.unimi.dsi.fastutil.ints.Int2IntArrayMap::new, 1000000), //
	;

	public final Supplier<Int2IntMap> maker;

	public final int maxsize;

	private FastutilMapInt2IntFact(Supplier<Int2IntMap> maker) {
		this(maker, Integer.MAX_VALUE);
	}

	private FastutilMapInt2IntFact(Supplier<Int2IntMap> maker, int maxsize) {
		this.maker = maker;
		this.maxsize = maxsize;
	}
}