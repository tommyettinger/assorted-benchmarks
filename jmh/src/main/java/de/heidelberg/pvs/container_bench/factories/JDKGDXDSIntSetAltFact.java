package de.heidelberg.pvs.container_bench.factories;

import de.heidelberg.pvs.container_bench.IntSetAlt;

import java.util.function.Supplier;

public enum JDKGDXDSIntSetAltFact {
	DEFAULT(() -> new IntSetAlt(12, LoadFactor.LOAD_FACTOR))
	;

	public final Supplier<IntSetAlt> maker;

	JDKGDXDSIntSetAltFact(Supplier<IntSetAlt> maker) {
		this.maker = maker;
	}
}