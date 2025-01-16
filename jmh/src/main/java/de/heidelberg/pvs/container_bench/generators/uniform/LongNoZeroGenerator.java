package de.heidelberg.pvs.container_bench.generators.uniform;

import de.heidelberg.pvs.container_bench.generators.LongElementGenerator;

public class LongNoZeroGenerator extends AbstractUniformGenerator<Long> implements LongElementGenerator {

	@Override
	public long[] generateLongArray(int arraySize) {
		long[] longs = new long[arraySize];
		long seed = generator.nextLong();
		if(seed == 0) seed = 12345;
		for (int i = 0; i < arraySize; i++) {
			seed <<= 9;
			seed >>>= 7;
			longs[i] = seed;
		}
		return longs;
	}

	@Override
	public Long[] generateArray(int arraySize) {
		Long[] longs = new Long[arraySize];
		long seed = generator.nextLong();
		if(seed == 0) seed = 12345;
		for (int i = 0; i < arraySize; i++) {
			seed <<= 9;
			seed >>>= 7;
			longs[i] = seed;
		}
		return longs;
	}

}
