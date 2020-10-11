package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.*;
import de.heidelberg.pvs.container_bench.ObjectMapBare;
import de.heidelberg.pvs.container_bench.ObjectMapMulXor;
import de.heidelberg.pvs.container_bench.OrderedMapBare;
import de.heidelberg.pvs.container_bench.OrderedMapMulXor;
import io.timeandspace.smoothie.OptimizationObjective;
import io.timeandspace.smoothie.SmoothieMap;
import io.timeandspace.smoothie.SwissTable;
import squidpony.squidmath.UnorderedMap;

import java.util.Map;
import java.util.function.Supplier;

public enum JDKMap2ObjFact {
//JDK_O2O_HASH,KOLOBOKE_O2O_HASH,FASTUTIL_O2O_HASH,ECLIPSE_O2O_HASH,APACHE_O2O_HASH,MAHOUT_O2O_HASH,AGRONA_O2O_HASH,SQUID_HASH,JDKGDXDS_HASH
//JDK_O2O_LINKEDHASH,FASTUTIL_O2O_LINKEDHASH,APACHE_O2O_LINKEDHASH,SQUID_INDEXED,ATLANTIS_INDEXED,JDKGDXDS_INDEXED
//FASTUTIL_O2O_ARRAY,GOOGLE_O2O_ARRAY,CORENLP_ARRAY
	JDK_O2O_HASH(() -> new java.util.HashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_LINKEDHASH(() -> new java.util.LinkedHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_TREE(() -> new java.util.TreeMap<>()), //
	JDK_O2O_HASHTABLE(() -> new java.util.Hashtable<>(16, LoadFactor.LOAD_FACTOR)), //

	KOLOBOKE_O2O_HASH(com.koloboke.collect.map.hash.HashObjObjMaps::newMutableMap), //
	KOLOBOKE_O2O_QHASH(KolobokeQHash.ObjObjMaps::newMutableMap), //

	FASTUTIL_O2O_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2O_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_O2O_AVL(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap<>()), //
	FASTUTIL_O2O_RB(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap<>()), //
	FASTUTIL_O2O_ARRAY(it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap::new, 1000000), //

	TROVE_O2O_HASH(() -> new gnu.trove.map.hash.THashMap<>(16, LoadFactor.LOAD_FACTOR)), //

	ECLIPSE_O2O_HASH(() -> new org.eclipse.collections.impl.map.mutable.UnifiedMap<>(16, LoadFactor.LOAD_FACTOR)), //
	ECLIPSE_O2O_TREE(() -> new org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap<>()), //

	APACHE_O2O_HASH(() -> new org.apache.commons.collections4.map.HashedMap<>(16, LoadFactor.LOAD_FACTOR)), //
	APACHE_O2O_LINKEDHASH(() -> new org.apache.commons.collections4.map.LinkedMap<>(16, LoadFactor.LOAD_FACTOR)), //

	MAHOUT_O2O_HASH(() -> new org.apache.mahout.math.map.OpenHashMap<>(16, 0.5f, LoadFactor.LOAD_FACTOR)), //

	JAVOLUTION_HASH(() -> new javolution.util.FastMap<>()), //
	JAVOLUTION_SORTED(javolution.util.FastSortedMap::new, 1000000), //

	AGRONA_O2O_HASH(() -> new org.agrona.collections.Object2ObjectHashMap<>(16, LoadFactor.LOAD_FACTOR)), //

	SMOOTHIE_LG_HASH(() -> SmoothieMap.<Object, Object>newBuilder().optimizeFor(OptimizationObjective.LOW_GARBAGE).build()),
	SMOOTHIE_MX_HASH(() -> SmoothieMap.<Object, Object>newBuilder().defaultOptimizationConfiguration().build()),
	SMOOTHIE_FP_HASH(() -> SmoothieMap.<Object, Object>newBuilder().optimizeFor(OptimizationObjective.FOOTPRINT).build()),
	SWISS_TABLE(() -> new SwissTable<>(16)),

	SQUID_HASH(() -> new UnorderedMap<>(16, LoadFactor.LOAD_FACTOR)),
	SQUID_INDEXED(() -> new squidpony.squidmath.OrderedMap<>(16, LoadFactor.LOAD_FACTOR)),
	ATLANTIS_INDEXED(() -> new IndexedMap<>(16, LoadFactor.LOAD_FACTOR)),

	JDKGDXDS_HASH(() -> new ObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_INDEXED(() -> new OrderedMap<>(16, LoadFactor.LOAD_FACTOR)),

	JDKGDXDSFIB_HASH(() -> new ObjectMapFib<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSFIB2_HASH(() -> new ObjectMapFib2<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSFIBIN_HASH(() -> new ObjectMapFibIn<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSBIG_HASH(() -> new ObjectMapBigHash<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSSAME_HASH(() -> new ObjectMapSame<>(16, LoadFactor.LOAD_FACTOR)),

	JDKGDXDSBARE_HASH(() -> new ObjectMapBare<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSBARE_INDEXED(() -> new OrderedMapBare<>(16, LoadFactor.LOAD_FACTOR)),

	JDKGDXDSMX_HASH(() -> new ObjectMapMulXor<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSMX_INDEXED(() -> new OrderedMapMulXor<>(16, LoadFactor.LOAD_FACTOR)),

	GOOGLE_O2O_ARRAY(com.google.api.client.util.ArrayMap::new, 1000000), //
	CORENLP_ARRAY(edu.stanford.nlp.util.ArrayMap::new, 1000000); //
	;

	public final Supplier<Map<Object, Object>> maker;

	public final int maxsize;

	private JDKMap2ObjFact(Supplier<Map<Object, Object>> maker) {
		this(maker, Integer.MAX_VALUE);
	}

	private JDKMap2ObjFact(Supplier<Map<Object, Object>> maker, int maxsize) {
		this.maker = maker;
		this.maxsize = maxsize;
	}
}
