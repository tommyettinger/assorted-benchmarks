package de.heidelberg.pvs.container_bench.generators.uniform;

import de.heidelberg.pvs.container_bench.generators.LongElementGenerator;

public class LongUniformGenerator extends AbstractUniformGenerator<Long> implements LongElementGenerator {

	@Override
	public long[] generateLongArray(int arraySize) {
		long[] longs = new long[arraySize];
		for (int i = 0; i < arraySize; i++) {
			longs[i] = generator.nextLong();
		}
		return longs;
	}

	@Override
	public Long[] generateArray(int arraySize) {
		Long[] longs = new Long[arraySize];
		for (int i = 0; i < arraySize; i++) {
			longs[i] = generator.nextLong();
		}
		return longs;
	}

}
