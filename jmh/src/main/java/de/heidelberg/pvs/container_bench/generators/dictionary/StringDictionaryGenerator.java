package de.heidelberg.pvs.container_bench.generators.dictionary;

import de.heidelberg.pvs.container_bench.generators.ElementGenerator;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import de.heidelberg.pvs.container_bench.generators.Wordlist;

import java.io.IOException;
import java.util.List;

public class StringDictionaryGenerator implements ElementGenerator<String> {
	
	protected List<String> words;
	
	protected TangleRNG generator;
	
	protected int seed; 
	protected int poolSize;
	
	@Override
	public void init(int size, int seed) throws IOException {
		this.seed = seed;
		this.poolSize = size;
		
		generator = new TangleRNG(seed);
		// Read all -> might be too expensive
		words = Wordlist.loadWords(size, seed);
	}

	@Override
	public int generateIndex(int range) {
		return generator.nextInt(range);
	}
	
	@Override
	public String[] generateArray(int arraySize) {
		String[] array = new String[arraySize];
		return words.subList(0, arraySize).toArray(array);
	}

}
