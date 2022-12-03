package de.heidelberg.pvs.container_bench.generators.uniform;

import com.github.tommyettinger.random.WhiskerRandom;
import squidpony.squidmath.Coord;

public class CoordUniformGenerator extends AbstractUniformGenerator<Coord> {
	@Override
	public Coord[] generateArray(int size) {
		int edge = Integer.highestOneBit((int)Math.ceil(Math.sqrt(size))) << 1;
		Coord.expandPoolTo(edge, edge);
		WhiskerRandom random = new WhiskerRandom(size);
		Coord[] result = new Coord[size];
		for (int root = 0, index = 0; ; ++root) {
			for (int limit = index + root + root; index <= limit; ) {
				final int r = index - root * root;
				final int x = (r < root) ? root : root + root - r;
				final int y = Math.min(r, root);
				result[index] = Coord.get(x-3, y-3);
				if (++index >= size) {
					random.shuffle(result);
					return result;
				}
			}
		}
	}
}
