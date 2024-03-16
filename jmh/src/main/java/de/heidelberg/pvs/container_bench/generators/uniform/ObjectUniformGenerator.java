package de.heidelberg.pvs.container_bench.generators.uniform;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            List<Class<?>> classes = ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream()
					.filter(clazz -> clazz.getPackageName()
							.startsWith("java."))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toList());
			for(Class<?> cl : classes){
				objects[i++] = cl;
				if(i == arraySize) return objects;
			}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (; i < arraySize; i++) {
			objects[i] = new Dummy(i);
		}
		return objects;
	}
}
