package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.*;
import de.heidelberg.pvs.container_bench.ObjectSetAlt;
import de.heidelberg.pvs.container_bench.ObjectSetBare;
import de.heidelberg.pvs.container_bench.OrderedSetBare;
import squidpony.squidmath.UnorderedSet;

import java.util.Set;
import java.util.function.Supplier;

public enum JDKSetFact {
	//JDK_HASH,JDKGDXDS_HASH,SQUID_HASH,KOLOBOKE_HASH,KOLOBOKE_QHASH,FASTUTIL_HASH,ECLIPSE_HASH,AGRONA_HASH
	//JDK_LINKEDHASH,JDKGDXDS_INDEXED,SQUID_INDEXED,ATLANTIS_INDEXED,FASTUTIL_LINKEDHASH,APACHE_LINKEDHASH
	//JDK_HASH,SQUID_HASH,JDKGDXDS_HASH,FASTUTIL_HASH
	//JDKGDXDSALT_HASH,JDKGDXDS_HASH,JDK_HASH
	//JDK_LINKEDHASH,SQUID_INDEXED,JDKGDXDS_INDEXED,FASTUTIL_LINKEDHASH,APACHE_LINKEDHASH
	//JDK_TREE,FASTUTIL_AVL,FASTUTIL_RB,ECLIPSE_TREE
	JDK_HASH(() -> new java.util.HashSet<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_LINKEDHASH(() -> new java.util.LinkedHashSet<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_TREE(() -> new java.util.TreeSet<>()), //

	KOLOBOKE_HASH(com.koloboke.collect.set.hash.HashObjSets::newMutableSet), //
	KOLOBOKE_QHASH(KolobokeQHash.ObjSets::newMutableSet), //

	FASTUTIL_HASH(() -> new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_AVL(() -> new it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet<>()), //
	FASTUTIL_RB(() -> new it.unimi.dsi.fastutil.objects.ObjectRBTreeSet<>()), //
	FASTUTIL_ARRAY(() -> new it.unimi.dsi.fastutil.objects.ObjectArraySet<>()), //

	TROVE_HASH(() -> new gnu.trove.set.hash.THashSet<>(16, LoadFactor.LOAD_FACTOR)), //

	ECLIPSE_HASH(() -> new org.eclipse.collections.impl.set.mutable.UnifiedSet<>(16, LoadFactor.LOAD_FACTOR)), //
	ECLIPSE_TREE(() -> new org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet<>()), //

	APACHE_HASH(() -> org.apache.commons.collections4.set.MapBackedSet.mapBackedSet(//
			new org.apache.commons.collections4.map.HashedMap<>())), //
	APACHE_LINKEDHASH(() -> new org.apache.commons.collections4.set.ListOrderedSet<>()), //
	MAHOUT_HASH(() -> new org.apache.mahout.math.set.OpenHashSet<>()), //

	JAVOLUTION_HASH(() -> new javolution.util.FastSet<>()), //
	JAVOLUTION_SORTED(() -> new javolution.util.FastSortedSet<>()), //

	AGRONA_HASH(() -> new org.agrona.collections.ObjectHashSet<>(16, LoadFactor.LOAD_FACTOR)), //

	CORENLP_ARRAY(() -> new edu.stanford.nlp.util.ArraySet<>(16, LoadFactor.LOAD_FACTOR)), //

	SQUID_HASH(() -> new UnorderedSet<>(16, LoadFactor.LOAD_FACTOR)),
	SQUID_INDEXED(() -> new squidpony.squidmath.OrderedSet<>(16, LoadFactor.LOAD_FACTOR)),
	ATLANTIS_INDEXED(() -> new IndexedSet<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_HASH(() -> new ObjectSet<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSQ_HASH(() -> new ObjectQuadSet<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_INDEXED(() -> new ObjectOrderedSet<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSALT_HASH(() -> new ObjectSetAlt<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSBARE_HASH(() -> new ObjectSetBare<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDSBARE_INDEXED(() -> new OrderedSetBare<>(16, LoadFactor.LOAD_FACTOR))

	;

	public Supplier<Set<?>> maker;

	JDKSetFact(Supplier<Set<?>> maker) {
		this.maker = maker;
	}
}
