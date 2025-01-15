package de.heidelberg.pvs.container_bench.generators;

import java.io.IOException;

public interface LongElementGenerator {
	
	void init(int poolSize, int seed) throws IOException;
	
	int generateIndex(int range);
	
	long[] generateLongArray(int arraySize);


}
