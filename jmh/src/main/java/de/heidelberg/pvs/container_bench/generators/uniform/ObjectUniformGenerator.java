package de.heidelberg.pvs.container_bench.generators.uniform;

import java.util.List;

import static de.heidelberg.pvs.container_bench.generators.uniform.ClassUniformGenerator.generateClasses;

public class ObjectUniformGenerator extends AbstractUniformGenerator<Object> {

	public static class Dummy {
		private final String name;
		public Dummy(int i){
			name = String.valueOf(i);
		}
		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public Object[] generateArray(int arraySize) {
		Object[] objects = new Object[arraySize];
		int i = 0;
        List<Class<?>> classes = generateClasses();
        for(Class<?> cl : classes){
            objects[i++] = cl;
            if(i == arraySize) return objects;
        }
        for (; i < arraySize; i++) {
			objects[i] = new Dummy(i);
		}
		return objects;
	}
}
