package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.*;
import de.heidelberg.pvs.container_bench.*;

import java.util.Map;
import java.util.function.Supplier;

public enum JDKClassMapFact {
//JDKGDXDS_HASH,CUCKOO_IDENTITY,JDKGDXDS_IDENTITY,JDKGDXDS_CLASS,JDKGDXDS_SPEC_CLASS,JDK_O2O_HASH,JDK_O2O_IDENTITY,FASTUTIL_O2O_HASH

	JDK_O2O_HASH(() -> new java.util.HashMap<>(16, LoadFactor.LOAD_FACTOR)), //
	JDK_O2O_IDENTITY(() -> new java.util.IdentityHashMap<>(16)), //

	KOLOBOKE_O2O_HASH(com.koloboke.collect.map.hash.HashObjObjMaps::newMutableMap), //
	KOLOBOKE_O2O_QHASH(KolobokeQHash.ObjObjMaps::newMutableMap), //

	FASTUTIL_O2O_HASH(() -> new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(16, LoadFactor.LOAD_FACTOR)), //

	ECLIPSE_O2O_HASH(() -> new org.eclipse.collections.impl.map.mutable.UnifiedMap<>(16, LoadFactor.LOAD_FACTOR)), //

	APACHE_O2O_HASH(() -> new org.apache.commons.collections4.map.HashedMap<>(16, LoadFactor.LOAD_FACTOR)), //

	AGRONA_O2O_HASH(() -> new org.agrona.collections.Object2ObjectHashMap<>(16, LoadFactor.LOAD_FACTOR)), //

	JDKGDXDS_HASH(() -> new ObjectObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_TOSTR(() -> new ObjectMapToString<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_IDENTITY(() -> new IdentityObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_CLASS(() -> new ClassObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	JDKGDXDS_SPEC_CLASS(() -> new ClassSpecializedMap<>(16, LoadFactor.LOAD_FACTOR)),

	CUCKOO_IDENTITY(() -> new IdentityCuckooMap<>(16, LoadFactor.LOAD_FACTOR)),
	;

	public final Supplier<Map<Class<?>, Object>> maker;

	public final int maxsize;

	private JDKClassMapFact(Supplier<Map<Class<?>, Object>> maker) {
		this(maker, Integer.MAX_VALUE);
	}

	private JDKClassMapFact(Supplier<Map<Class<?>, Object>> maker, int maxsize) {
		this.maker = maker;
		this.maxsize = maxsize;
	}
}
