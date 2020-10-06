package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IndexedMap;
import com.github.tommyettinger.ds.ObjectMap;
import com.github.tommyettinger.ds.OrderedMap;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjObjMap;
import de.heidelberg.pvs.container_bench.ObjectMapBare;
import de.heidelberg.pvs.container_bench.ObjectMapMulXor;
import de.heidelberg.pvs.container_bench.OrderedMapBare;
import de.heidelberg.pvs.container_bench.OrderedMapMulXor;
import squidpony.squidmath.UnorderedMap;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public enum JDKMap2IntFact {
//JDK_O2O_HASH,SQUID_HASH,JDKGDXDS_HASH,JDKGDXDSBARE_HASH
//JDK_O2O_LINKEDHASH,SQUID_INDEXED,JDKGDXDS_INDEXED,JDKGDXDSBARE_INDEXED
	JDK_O2O_HASH(() -> new java.util.HashMap<>(16, LoadFactor.LOAD_FACTOR), java.util.HashMap::new), //
	JDK_O2O_LINKEDHASH(() -> new java.util.LinkedHashMap<>(16, LoadFactor.LOAD_FACTOR), java.util.LinkedHashMap::new), //
	JDK_O2O_TREE(() -> new java.util.TreeMap<>(), java.util.TreeMap::new), //
	JDK_O2O_HASHTABLE(() -> new java.util.Hashtable<>(16, LoadFactor.LOAD_FACTOR), java.util.Hashtable::new), //
	
	KOLOBOKE_O2O_HASH(com.koloboke.collect.map.hash.HashObjObjMaps::newMutableMap, com.koloboke.collect.map.hash.HashObjObjMaps::newMutableMap), //
	KOLOBOKE_O2I_HASH(com.koloboke.collect.map.hash.HashObjIntMaps::newMutableMap, com.koloboke.collect.map.hash.HashObjIntMaps::newMutableMap), //
	KOLOBOKE_O2O_QHASH(KolobokeQHash.ObjObjMaps::newMutableMap, (Map<Object, Integer> m) -> {HashObjObjMap<Object, Integer> c = KolobokeQHash.ObjObjMaps.newMutableMap(); c.putAll(m); return c;}),
	KOLOBOKE_O2I_QHASH(KolobokeQHash.ObjIntMaps::newMutableMap, (Map<Object, Integer> m) -> {HashObjIntMap<Object> c = KolobokeQHash.ObjIntMaps.newMutableMap(); c.putAll(m); return c;}),

	FASTUTIL_O2O_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(16, LoadFactor.LOAD_FACTOR), it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap::new), //
	FASTUTIL_O2O_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR), it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap::new), //
	FASTUTIL_O2O_AVL(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap<>(), it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap::new), //
	FASTUTIL_O2O_RB(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap<>(), it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap::new), //
	FASTUTIL_O2O_ARRAY(it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap::new, 1000000, it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap::new), //

	// Fastutil primitive-valued
	FASTUTIL_O2I_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>(16, LoadFactor.LOAD_FACTOR), it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap::new), //
	FASTUTIL_O2I_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap<>(16, LoadFactor.LOAD_FACTOR), it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap::new), //
	FASTUTIL_O2I_AVL(() -> new it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap<>(), it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap::new), //
	FASTUTIL_O2I_RB(() -> new it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap<>(), it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap::new), //
	FASTUTIL_O2I_ARRAY(it.unimi.dsi.fastutil.objects.Object2IntArrayMap::new, 1000000, it.unimi.dsi.fastutil.objects.Object2IntArrayMap::new), //

	TROVE_O2O_HASH(() -> new gnu.trove.map.hash.THashMap<>(16, LoadFactor.LOAD_FACTOR), gnu.trove.map.hash.THashMap::new), //

	ECLIPSE_O2O_HASH(() -> new org.eclipse.collections.impl.map.mutable.UnifiedMap<>(16, LoadFactor.LOAD_FACTOR), org.eclipse.collections.impl.map.mutable.UnifiedMap::new), //
	ECLIPSE_O2O_TREE(() -> new org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap<>(), org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap::new), //

	APACHE_O2O_HASH(() -> new org.apache.commons.collections4.map.HashedMap<>(16, LoadFactor.LOAD_FACTOR), org.apache.commons.collections4.map.HashedMap::new), //
	APACHE_O2O_LINKEDHASH(() -> new org.apache.commons.collections4.map.LinkedMap<>(16, LoadFactor.LOAD_FACTOR), org.apache.commons.collections4.map.LinkedMap::new), //

	MAHOUT_O2O_HASH(() -> new org.apache.mahout.math.map.OpenHashMap<>(16, 0.5f, LoadFactor.LOAD_FACTOR), (Map<Object, Integer> m) -> {org.apache.mahout.math.map.OpenHashMap<Object, Integer> c = new org.apache.mahout.math.map.OpenHashMap<>(m.size()); c.putAll(m); return c;}),

	JAVOLUTION_HASH(() -> new javolution.util.FastMap<>(), (Map<Object, Integer> m) -> {javolution.util.FastMap<Object, Integer> c = new javolution.util.FastMap<>(); c.putAll(m); return c;}),
	JAVOLUTION_SORTED(javolution.util.FastSortedMap::new, 1000000, (Map<Object, Integer> m) -> {javolution.util.FastSortedMap<Object, Integer> c = new javolution.util.FastSortedMap<>(); c.putAll(m); return c;}),

	AGRONA_O2O_HASH(() -> new org.agrona.collections.Object2ObjectHashMap<>(16, LoadFactor.LOAD_FACTOR), (Map<Object, Integer> m) -> {org.agrona.collections.Object2ObjectHashMap<Object, Integer> c = new org.agrona.collections.Object2ObjectHashMap<Object, Integer>(m.size(), LoadFactor.LOAD_FACTOR); c.putAll(m); return c;}),

	SQUID_HASH(() -> new UnorderedMap<>(16, LoadFactor.LOAD_FACTOR), UnorderedMap::new),
	SQUID_INDEXED(() -> new squidpony.squidmath.OrderedMap<>(16, LoadFactor.LOAD_FACTOR), squidpony.squidmath.OrderedMap::new),
	ATLANTIS_INDEXED(() -> new IndexedMap<>(16, LoadFactor.LOAD_FACTOR), IndexedMap::new),

	JDKGDXDS_HASH(() -> new ObjectMap<>(16, LoadFactor.LOAD_FACTOR), ObjectMap::new),
	JDKGDXDS_INDEXED(() -> new OrderedMap<>(16, LoadFactor.LOAD_FACTOR), OrderedMap::new),

	JDKGDXDSBARE_HASH(() -> new ObjectMapBare<>(16, LoadFactor.LOAD_FACTOR), ObjectMapBare::new),
	JDKGDXDSBARE_INDEXED(() -> new OrderedMapBare<>(16, LoadFactor.LOAD_FACTOR), OrderedMapBare::new),

	JDKGDXDSMULXOR_HASH(() -> new ObjectMapMulXor<>(16, LoadFactor.LOAD_FACTOR), ObjectMapMulXor::new),
	JDKGDXDSMULXOR_INDEXED(() -> new OrderedMapMulXor<>(16, LoadFactor.LOAD_FACTOR), OrderedMapMulXor::new),

	GOOGLE_O2O_ARRAY(com.google.api.client.util.ArrayMap::new, 1000000, (Map<Object, Integer> m) -> {com.google.api.client.util.ArrayMap<Object, Integer> c = new com.google.api.client.util.ArrayMap<>(); c.putAll(m); return c;}),
	CORENLP_ARRAY(edu.stanford.nlp.util.ArrayMap::new, 1000000, (Map<Object, Integer> m) -> {edu.stanford.nlp.util.ArrayMap<Object, Integer> c = new edu.stanford.nlp.util.ArrayMap<>(); c.putAll(m); return c;})
	;

	public final Supplier<Map<Object, Integer>> maker;
	public final Function<Map<Object, Integer>, Map<Object, Integer>> copyMaker;
	public final int maxsize;

	private JDKMap2IntFact(Supplier<Map<Object, Integer>> maker, Function<Map<Object, Integer>, Map<Object, Integer>> copyMaker) {
		this(maker, Integer.MAX_VALUE, copyMaker);
	}

	private JDKMap2IntFact(Supplier<Map<Object, Integer>> maker, int maxsize, Function<Map<Object, Integer>, Map<Object, Integer>> copyMaker) {
		this.maker = maker;
		this.copyMaker = copyMaker;
		this.maxsize = maxsize;
	}

}
