package de.heidelberg.pvs.container_bench.factories;

import com.github.tommyettinger.ds.IntIntMap;
import com.github.tommyettinger.ds.IntIntTable;

import java.util.function.Supplier;

public enum RobinHoodMapInt2IntFact {
	GDX_O2I_HASH(() -> new IntIntTable(16)), //
	;

	public final Supplier<IntIntTable> maker;

	RobinHoodMapInt2IntFact(Supplier<IntIntTable> maker) {
		this.maker = maker;
	}
}