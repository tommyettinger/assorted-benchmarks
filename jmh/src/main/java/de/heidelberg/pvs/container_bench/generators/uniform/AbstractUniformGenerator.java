package de.heidelberg.pvs.container_bench.generators.uniform;

import com.github.tommyettinger.random.WhiskerRandom;
import de.heidelberg.pvs.container_bench.generators.ElementGenerator;

/**
 * Abstract class for elements with uniform random distribution 
 * 
 * @author diego.costa
 *
 * @param <T>
 */
public abstract class AbstractUniformGenerator<T> implements ElementGenerator<T> {

	WhiskerRandom generator;
	
	@Override
	public void init(int size, int seed) {
		// FIXME: size has no influence here so we should think in a better
		// way of handling this common initialization
		generator = new WhiskerRandom(seed);
	}
	
	@Override
	public int generateIndex(int range) {
		return generator.nextInt(range);
	}

		

}
