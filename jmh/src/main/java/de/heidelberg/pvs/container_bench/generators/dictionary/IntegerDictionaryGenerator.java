package de.heidelberg.pvs.container_bench.generators.dictionary;

import de.heidelberg.pvs.container_bench.generators.IntElementGenerator;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import de.heidelberg.pvs.container_bench.generators.Wordlist;

import java.io.IOException;
import java.util.Arrays;

public class IntegerDictionaryGenerator implements IntElementGenerator{

	int[] words;

	protected TangleRNG randomGenerator;
	
	public void init(int size, int seed) throws IOException {
		
		words = Wordlist.loadInts(size, seed, -1);
		randomGenerator = new TangleRNG(seed);
		
	}

	@Override
	public int generateIndex(int range) {
		return randomGenerator.nextInt(range);
	}

	@Override
	public int[] generateIntArray(int arraySize) {
		return Arrays.copyOfRange(words, 0, arraySize);
	}
	
	public Integer[] generateIntegerArray(final int arraySize, final int shift) {
		Integer[] arr = new Integer[arraySize];
		int state = 12345;
		for (int i = 0; i < arraySize; i++) {
			arr[i] = (state = state * 0xcf019d85 + 0x87654321) << shift;
		}
		return arr;
	}

}
