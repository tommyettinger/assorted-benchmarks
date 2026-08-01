package de.heidelberg.pvs.container_bench.factories;

import io.github.sooniln.fastcollect.ints.Int2IntHashMap;
import io.github.sooniln.fastcollect.ints.MutableInt2IntMap;

import java.util.function.Supplier;

public enum FastCollectMapInt2IntFact {
	FC_I2I_HASH(() -> new Int2IntHashMap(16,0)), //
	;

	public final Supplier<MutableInt2IntMap> maker;

	FastCollectMapInt2IntFact(Supplier<MutableInt2IntMap> maker) {
		this.maker = maker;
	}
}