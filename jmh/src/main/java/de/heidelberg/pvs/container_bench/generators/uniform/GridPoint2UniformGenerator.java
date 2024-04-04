package de.heidelberg.pvs.container_bench.generators.uniform;

import com.badlogic.gdx.math.GridPoint2;

public class GridPoint2UniformGenerator extends AbstractUniformGenerator<GridPoint2> {
	@Override
	public GridPoint2[] generateArray(int arraySize) {
		GridPoint2[] vectors = new GridPoint2[arraySize];
		for (int i = 0; i < arraySize; i++) {
			final int root = (int) (Math.sqrt(i));
			final int sign = -(root & 1);
			final int big = (root * (root + 1)) - i << 1;
			final int y = ((root + 1 >> 1) + sign ^ sign) + ((sign ^ sign + Math.min(big, 0)) >> 1);
			final int x = ((root + 1 >> 1) + sign ^ sign) - ((sign ^ sign + Math.max(big, 0)) >> 1);
			vectors[i] = new GridPoint2(x, y);
		}
		return vectors;
	}
}
