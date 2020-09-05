package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IndexedSet;

import java.util.Set;
import java.util.function.Supplier;

public enum JDKSetFact {
	//JDK_HASH,KOLOBOKE_HASH,FASTUTIL_HASH,ECLIPSE_HASH,TROVE_HASH,AGRONA_HASH
	//JDK_LINKEDHASH,FASTUTIL_LINKEDHASH,APACHE_LINKEDHASH,ATLANTIS_INDEXED
	JDK_HASH(() -> new java.util.HashSet(16, 0.8f)), //
	JDK_LINKEDHASH(() -> new java.util.LinkedHashSet(16, 0.8f)), //
	JDK_TREE(() -> new java.util.TreeSet()), //

	KOLOBOKE_HASH(com.koloboke.collect.set.hash.HashObjSets::newMutableSet), //
	KOLOBOKE_QHASH(KolobokeQHash.ObjSets::newMutableSet), //

	FASTUTIL_HASH(() -> new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(16, 0.8f)), //
	FASTUTIL_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet(16, 0.8f)), //
	FASTUTIL_AVL(() -> new it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet()), //
	FASTUTIL_RB(() -> new it.unimi.dsi.fastutil.objects.ObjectRBTreeSet()), //
	FASTUTIL_ARRAY(() -> new it.unimi.dsi.fastutil.objects.ObjectArraySet()), //

	TROVE_HASH(() -> new gnu.trove.set.hash.THashSet(16, 0.8f)), //

	ECLIPSE_HASH(() -> new org.eclipse.collections.impl.set.mutable.UnifiedSet(16, 0.8f)), //
	ECLIPSE_TREE(() -> new org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet()), //

	APACHE_HASH(() -> org.apache.commons.collections4.set.MapBackedSet.mapBackedSet(//
			new org.apache.commons.collections4.map.HashedMap<>())), //
	APACHE_LINKEDHASH(() -> new org.apache.commons.collections4.set.ListOrderedSet()), //
	MAHOUT_HASH(() -> new org.apache.mahout.math.set.OpenHashSet()), //

	JAVOLUTION_HASH(() -> new javolution.util.FastSet()), //
	JAVOLUTION_SORTED(() -> new javolution.util.FastSortedSet()), //

	AGRONA_HASH(() -> new org.agrona.collections.ObjectHashSet(16, 0.8f)), //

	CORENLP_ARRAY(() -> new edu.stanford.nlp.util.ArraySet(16, 0.8f)), //
	ATLANTIS_INDEXED(() -> new IndexedSet(16, 0.8f))
	;

	public Supplier<Set<?>> maker;

	JDKSetFact(Supplier<Set<?>> maker) {
		this.maker = maker;
	}
}
