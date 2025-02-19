package de.heidelberg.pvs.container_bench.generators.uniform;

import com.github.tommyettinger.random.AceRandom;
import de.heidelberg.pvs.container_bench.generators.ElementGenerator;

/**
 * Abstract class for elements with uniform random distribution 
 * 
 * @author diego.costa
 *
 * @param <T>
 */
public abstract class AbstractUniformGenerator<T> implements ElementGenerator<T> {

	public AceRandom generator;
	
	@Override
	public void init(int size, int seed) {
		// FIXME: size has no influence here so we should think in a better
		// way of handling this common initialization
		generator = new AceRandom(seed);
	}
	
	@Override
	public int generateIndex(int range) {
		return generator.nextInt(range);
	}

		

}
