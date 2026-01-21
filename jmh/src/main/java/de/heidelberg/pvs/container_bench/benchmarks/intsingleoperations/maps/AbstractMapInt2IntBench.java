package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.maps;

import de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.AbstractIntSingleOperationsBench;
import de.heidelberg.pvs.container_bench.generators.ElementGenerator;
import de.heidelberg.pvs.container_bench.generators.GeneratorFactory;
import de.heidelberg.pvs.container_bench.generators.IntElementGenerator;
import de.heidelberg.pvs.container_bench.generators.PayloadType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

import java.io.IOException;

/**
 * Abstract class for single operation benchmarks on map 2 primitives
 * 
 * @author diego.costa
 *
 */
public abstract class AbstractMapInt2IntBench extends AbstractIntSingleOperationsBench {

	protected IntElementGenerator valuesGenerator;
	protected IntElementGenerator keyGenerator;
	
	@Param
	public MapInt2IntWorload workload;
	
	protected int[] values;
	protected int[] keys;

	@SuppressWarnings("unchecked")
	public void generatorSetup() throws IOException {
		valuesGenerator = GeneratorFactory.buildRandomGenerator(payloadType);
		valuesGenerator.init(size, seed);

		keyGenerator = GeneratorFactory.buildRandomGenerator(payloadType);
		keyGenerator.init(size, seed);
		
		values = valuesGenerator.generateIntArray(size);
		keys = keyGenerator.generateIntArray(size);
	}

	
	@Benchmark
	public void bench() {
		workload.run(this);
	}
	
	public enum MapInt2IntWorload {
		
		POPULATE {
			@Override
			void run(AbstractMapInt2IntBench self) {
				self.populateBench();
			}
		}, 
		
		ITERATE_KEY {
			@Override
			void run(AbstractMapInt2IntBench self) {
				self.iterateKeyBench();
			}
			
		},
		
		ITERATE_KEYVALUE {
			@Override
			void run(AbstractMapInt2IntBench self) {
				self.iterateKeyValueBench();
			}
			
		},
		
		COPY {
			@Override
			void run(AbstractMapInt2IntBench self) {
				self.copyBench();
			}
			
		}, 
		
		CONTAINS {
			@Override
			void run(AbstractMapInt2IntBench self) {
				self.containsBench();
			}
		}
		;
		
		abstract void run(AbstractMapInt2IntBench self);
		
	}

	protected abstract void populateBench();
	protected abstract void containsBench();
	protected abstract void copyBench();
	protected abstract void iterateKeyBench();
	protected abstract void iterateKeyValueBench();
	
}
