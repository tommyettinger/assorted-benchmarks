package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.*;
import de.heidelberg.pvs.container_bench.ObjectSet32;
import de.heidelberg.pvs.container_bench.ObjectSetAlt;
import de.heidelberg.pvs.container_bench.ObjectSetBare;
import de.heidelberg.pvs.container_bench.OrderedSetBare;
import squidpony.squidmath.UnorderedSet;

import java.util.Set;
import java.util.function.Supplier;

public enum JDKCoordSetFact {
	//CANTOR_HASH,MUL_HASH,RS_HASH,JDKGDXDS_HASH,FASTUTIL_HASH,JDK_HASH
	JDK_HASH(() -> new java.util.HashSet<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_LINKEDHASH(() -> new java.util.LinkedHashSet<>(16, LoadFactor.LOAD_FACTOR)), //

	KOLOBOKE_HASH(com.koloboke.collect.set.hash.HashObjSets::newMutableSet), //
	KOLOBOKE_QHASH(KolobokeQHash.ObjSets::newMutableSet), //

	FASTUTIL_HASH(() -> new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(16, LoadFactor.LOAD_FACTOR)), //
	FASTUTIL_LINKEDHASH(() -> new it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet<>(16, LoadFactor.LOAD_FACTOR)), //

	ECLIPSE_HASH(() -> new org.eclipse.collections.impl.set.mutable.UnifiedSet<>(16, LoadFactor.LOAD_FACTOR)), //

	APACHE_HASH(() -> org.apache.commons.collections4.set.MapBackedSet.mapBackedSet(//
			new org.apache.commons.collections4.map.HashedMap<>())), //
	APACHE_LINKEDHASH(() -> new org.apache.commons.collections4.set.ListOrderedSet<>()), //

	JDKGDXDS_HASH(() -> new ObjectSet<>(16, LoadFactor.LOAD_FACTOR)),
	CANTOR_HASH(() -> new CantorCoordSet(16, LoadFactor.LOAD_FACTOR)),
	MUL_HASH(() -> new MulCoordSet(16, LoadFactor.LOAD_FACTOR)),
	RS_HASH(() -> new RSCoordSet(16, LoadFactor.LOAD_FACTOR)),
	;

	public Supplier<Set<?>> maker;

	JDKCoordSetFact(Supplier<Set<?>> maker) {
		this.maker = maker;
	}
}
