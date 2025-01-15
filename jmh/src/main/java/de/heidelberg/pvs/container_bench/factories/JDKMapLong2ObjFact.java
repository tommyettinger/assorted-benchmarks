package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.tommyettinger.ds.ObjectObjectOrderedMap;

import java.util.Map;
import java.util.function.Supplier;

public enum JDKMapLong2ObjFact {
//JDK_O2O_HASH,SQUID_HASH,JDKGDXDS_HASH,JDKGDXDSBARE_HASH
//JDK_O2O_LINKEDHASH,SQUID_INDEXED,JDKGDXDS_INDEXED,JDKGDXDSBARE_INDEXED
	JDK_O2O_HASH(() -> new java.util.HashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_CONCURRENTHASH(() -> new java.util.concurrent.ConcurrentHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_LINKEDHASH(() -> new java.util.LinkedHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_TREE(() -> new java.util.TreeMap<>()), //

	FASTUTIL_O2O_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2O_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2O_AVL(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap<>()), //
	FASTUTIL_O2O_RB(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap<>()), //
	FASTUTIL_O2O_ARRAY(it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap::new, 1000000), //

	// Fastutil primitive-valued
	FASTUTIL_L2O_HASH(() -> new it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_L2O_LINKEDHASH(() -> new it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_L2O_AVL(() -> new it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap<>()), //
	FASTUTIL_L2O_RB(() -> new it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap<>()), //
	FASTUTIL_L2O_ARRAY(it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap::new, 1000000), //

	JDKGDXDS_HASH(() -> new ObjectObjectMap<>(8, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_ORDERED(() -> new ObjectObjectOrderedMap<>(8, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<Map<Long, Object>> maker;

	public final int maxsize;

	private JDKMapLong2ObjFact(Supplier<Map<Long, Object>> maker) {
		this(maker, Integer.MAX_VALUE);
	}

	private JDKMapLong2ObjFact(Supplier<Map<Long, Object>> maker, int maxsize) {
		this.maker = maker;
		this.maxsize = maxsize;
	}

}
