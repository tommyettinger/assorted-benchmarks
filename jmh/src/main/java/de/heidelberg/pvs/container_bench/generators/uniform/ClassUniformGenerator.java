package de.heidelberg.pvs.container_bench.generators.uniform;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClassUniformGenerator extends AbstractUniformGenerator<Class<?>> {
	public static List<Class<?>> generateClasses() {
        try {
            return ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream()
                    .filter(clazz -> clazz.getPackageName()
                            .startsWith("java."))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

	@Override
	public Class<?>[] generateArray(int arraySize) {
		Class<?>[] classes = new Class<?>[arraySize], generated = generateClasses().toArray(new Class<?>[0]);
		int placed = 0;
		while (placed < arraySize){
			final int amt = Math.min(arraySize - placed, generated.length);
			System.arraycopy(generated, 0, classes, placed, amt);
			placed += amt;
		}
		return classes;
	}
}
