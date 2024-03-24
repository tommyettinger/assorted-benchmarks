package de.heidelberg.pvs.container_bench.generators.uniform;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.List;

public class ClassUniformGenerator extends AbstractUniformGenerator<Class<?>> {
	public static List<Class<?>> obtained = null;
	public static List<Class<?>> generateClasses() {
		if(obtained != null) return obtained;
        Reflections reflections = new Reflections("com", new SubTypesScanner(false));
        return obtained = new ArrayList<>(reflections.getSubTypesOf(Object.class));
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
