package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.*;
import de.heidelberg.pvs.container_bench.FlipMap0;
import io.timeandspace.smoothie.OptimizationObjective;
import io.timeandspace.smoothie.SmoothieMap;
import io.timeandspace.smoothie.SwissTable;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.UnorderedMap;

import java.util.Map;
import java.util.function.Supplier;

public enum JDKMap2IntFact {
//JDK_O2O_HASH,SQUID_HASH,JDKGDXDS_HASH,JDKGDXDSBARE_HASH
//JDK_O2O_LINKEDHASH,SQUID_INDEXED,JDKGDXDS_INDEXED,JDKGDXDSBARE_INDEXED
	JDK_O2O_HASH(() -> new java.util.HashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_LINKEDHASH(() -> new java.util.LinkedHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_TREE(() -> new java.util.TreeMap<>()), //
	JDK_O2O_HASHTABLE(() -> new java.util.Hashtable<>(16, LoadFactor.LOAD_FACTOR)), //
	
	KOLOBOKE_O2O_HASH(com.koloboke.collect.map.hash.HashObjObjMaps::newMutableMap), //
	KOLOBOKE_O2I_HASH(com.koloboke.collect.map.hash.HashObjIntMaps::newMutableMap), //
	KOLOBOKE_O2O_QHASH(KolobokeQHash.ObjObjMaps::newMutableMap), //
	KOLOBOKE_O2I_QHASH(KolobokeQHash.ObjIntMaps::newMutableMap), //


	FASTUTIL_O2O_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2O_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2O_AVL(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap<>()), //
	FASTUTIL_O2O_RB(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap<>()), //
	FASTUTIL_O2O_ARRAY(it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap::new, 1000000), //

	// Fastutil primitive-valued
	FASTUTIL_O2I_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2I_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2I_AVL(() -> new it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap<>()), //
	FASTUTIL_O2I_RB(() -> new it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap<>()), //
	FASTUTIL_O2I_ARRAY(it.unimi.dsi.fastutil.objects.Object2IntArrayMap::new, 1000000), //

	TROVE_O2O_HASH(() -> new gnu.trove.map.hash.THashMap<>(16, LoadFactor.LOAD_FACTOR)), //

	ECLIPSE_O2O_HASH(() -> new org.eclipse.collections.impl.map.mutable.UnifiedMap<>(16, LoadFactor.LOAD_FACTOR)), //
	ECLIPSE_O2O_TREE(() -> new org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap<>()), //

	APACHE_O2O_HASH(() -> new org.apache.commons.collections4.map.HashedMap<>(16, LoadFactor.LOAD_FACTOR)), //
	APACHE_O2O_LINKEDHASH(() -> new org.apache.commons.collections4.map.LinkedMap<>(16, LoadFactor.LOAD_FACTOR)), //

	AGRONA_O2O_HASH(() -> new org.agrona.collections.Object2ObjectHashMap<>(16, LoadFactor.LOAD_FACTOR)),

	SMOOTHIE_LG_HASH(() -> SmoothieMap.<Object, Integer>newBuilder().optimizeFor(OptimizationObjective.LOW_GARBAGE).build()),
	SMOOTHIE_MX_HASH(() -> SmoothieMap.<Object, Integer>newBuilder().defaultOptimizationConfiguration().build()),
	SMOOTHIE_FP_HASH(() -> SmoothieMap.<Object, Integer>newBuilder().optimizeFor(OptimizationObjective.FOOTPRINT).build()),
	SWISS_TABLE(() -> new SwissTable<>(16)),

	SQUID_HASH(() -> new UnorderedMap<>(16, LoadFactor.LOAD_FACTOR)),
	SQUID_INDEXED(() -> new squidpony.squidmath.OrderedMap<>(16, LoadFactor.LOAD_FACTOR)),
	ATLANTIS_INDEXED(() -> new IndexedMap<>(16, LoadFactor.LOAD_FACTOR)),

	JDKGDXDS_HASH(() -> new com.github.tommyettinger.ds.ObjectObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSQ_HASH(() -> new ObjectObjectQuadMap<>(16, LoadFactor.LOAD_FACTOR)),
	CUCKOO_HASH(() -> new ObjectObjectCuckooMap<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_INDEXED(() -> new OrderedMap<>(16, LoadFactor.LOAD_FACTOR)),

	FLIP_HASH0(() -> new FlipMap0<>(16, LoadFactor.LOAD_FACTOR)),
	FLIP_HASH(() -> new de.heidelberg.pvs.container_bench.flip.ObjectObjectMap<>(16, LoadFactor.LOAD_FACTOR)),

	GOOGLE_O2O_ARRAY(com.google.api.client.util.ArrayMap::new, 1000000),
	;

	public final Supplier<Map<Object, Integer>> maker;

	public final int maxsize;

	private JDKMap2IntFact(Supplier<Map<Object, Integer>> maker) {
		this(maker, Integer.MAX_VALUE);
	}

	private JDKMap2IntFact(Supplier<Map<Object, Integer>> maker, int maxsize) {
		this.maker = maker;
		this.maxsize = maxsize;
	}

}
