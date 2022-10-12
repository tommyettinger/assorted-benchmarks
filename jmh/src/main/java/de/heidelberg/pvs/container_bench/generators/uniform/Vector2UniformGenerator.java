package de.heidelberg.pvs.container_bench.generators.uniform;

import com.badlogic.gdx.math.Vector2;

public class Vector2UniformGenerator extends AbstractUniformGenerator<Vector2> {
	@Override
	public Vector2[] generateArray(int arraySize) {
		Vector2[] vectors = new Vector2[arraySize];
		int half = arraySize >>> 1;
		for (int i = 0; i < arraySize; i++) {
			vectors[i] = new Vector2(generator.next(16) - half, i - half);
		}
		return vectors;
	}

}
