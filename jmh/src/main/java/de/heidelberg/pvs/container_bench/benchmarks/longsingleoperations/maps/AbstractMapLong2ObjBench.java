package de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations.maps;

import de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations.AbstractLongSingleOperationsBench;
import de.heidelberg.pvs.container_bench.generators.*;
import de.heidelberg.pvs.container_bench.generators.uniform.LongUniformGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

import java.io.IOException;

/**
 * Abstract class for single operation benchmarks on map 2 primitives
 * 
 * @author diego.costa
 *
 */
public abstract class AbstractMapLong2ObjBench extends AbstractLongSingleOperationsBench {

	protected ElementGenerator<String> valuesGenerator;
	protected LongElementGenerator keyGenerator;
	
	@Param
	public MapLong2ObjWorkload workload;
	
	protected Object[] values;
	protected long[] keys;

	@SuppressWarnings("unchecked")
	public void generatorSetup() throws IOException {
		valuesGenerator =
				(ElementGenerator<String>) GeneratorFactory.buildRandomGenerator(PayloadType.STRING_DICTIONARY);
		valuesGenerator.init(size, seed);

		keyGenerator = new LongUniformGenerator();
		keyGenerator.init(size, seed);
		
		values = valuesGenerator.generateArray(size);
		keys = keyGenerator.generateLongArray(size);
	}

	
	@Benchmark
	public void bench() {
		workload.run(this);
	}
	
	public enum MapLong2ObjWorkload {
		
		POPULATE {
			@Override
			void run(AbstractMapLong2ObjBench self) {
				self.populateBench();
			}
		}, 
		
		ITERATE_KEY {
			@Override
			void run(AbstractMapLong2ObjBench self) {
				self.iterateKeyBench();
			}
			
		},
		
		ITERATE_KEYVALUE {
			@Override
			void run(AbstractMapLong2ObjBench self) {
				self.iterateKeyValueBench();
			}
			
		},
		
		COPY {
			@Override
			void run(AbstractMapLong2ObjBench self) {
				self.copyBench();
			}
			
		}, 
		
		CONTAINS {
			@Override
			void run(AbstractMapLong2ObjBench self) {
				self.containsBench();
			}
		}
		;
		
		abstract void run(AbstractMapLong2ObjBench self);
		
	}

	protected abstract void populateBench();
	protected abstract void containsBench();
	protected abstract void copyBench();
	protected abstract void iterateKeyBench();
	protected abstract void iterateKeyValueBench();
	
}
