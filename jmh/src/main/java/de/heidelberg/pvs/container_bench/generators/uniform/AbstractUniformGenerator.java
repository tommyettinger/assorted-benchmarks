package de.heidelberg.pvs.container_bench.generators.uniform;

import de.heidelberg.pvs.container_bench.generators.ElementGenerator;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;

/**
 * Abstract class for elements with uniform random distribution 
 * 
 * @author diego.costa
 *
 * @param <T>
 */
public abstract class AbstractUniformGenerator<T> implements ElementGenerator<T> {

	TangleRNG generator;
	
	@Override
	public void init(int size, int seed) {
		// FIXME: size has no influence here so we should think in a better
		// way of handling this common initialization
		generator = new TangleRNG(seed);
	}
	
	@Override
	public int generateIndex(int range) {
		return generator.nextInt(range);
	}

		

}
