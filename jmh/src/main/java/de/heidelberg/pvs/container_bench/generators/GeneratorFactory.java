package de.heidelberg.pvs.container_bench.generators;

import de.heidelberg.pvs.container_bench.generators.dictionary.IntegerDictionaryGenerator;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator;
import de.heidelberg.pvs.container_bench.generators.uniform.*;

public class GeneratorFactory {

	public static ElementGenerator<?> buildRandomGenerator(PayloadType payloadType) {

		switch (payloadType) {

			case INTEGER_UNIFORM:
				return new IntegerUniformGenerator();

			case STRING_UNIFORM:
				return new StringUniformGenerator();

			case STRING_DICTIONARY:
				return new StringDictionaryGenerator();

			case OBJECT_UNIFORM:
				return new ObjectUniformGenerator();

			case CLASS_UNIFORM:
				return new ClassUniformGenerator();

			case VECTOR2_UNIFORM:
				return new Vector2UniformGenerator();

			case GRIDPOINT2_UNIFORM:
				return new GridPoint2UniformGenerator();

			case COORD_UNIFORM:
				return new CoordUniformGenerator();

			default:
				throw new IllegalArgumentException("Payload type not specified");
		}

	}
	
	public static IntElementGenerator buildRandomGenerator(IntPayloadType payloadType) {

		switch (payloadType) {

			case INTEGER_UNIFORM:
				return new IntegerUniformGenerator();

			case INTEGER_DICTIONARY:
				return new IntegerDictionaryGenerator();

			default:
				throw new IllegalArgumentException("Payload type not specified");
		}
	}

}
