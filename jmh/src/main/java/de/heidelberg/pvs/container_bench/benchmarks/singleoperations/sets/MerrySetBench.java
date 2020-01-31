package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.sets;

import ds.merry.ObjectSet;
import de.heidelberg.pvs.container_bench.factories.MerrySetFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

/**
 * Abstract class for every test with Merry ObjectSet implementation
 * 
 * @author Diego
 *
 */
public class MerrySetBench extends AbstractSetBench<Object> {

	private ObjectSet<Object> fullSet;
	private Object[] values;

	@Param
	public MerrySetFact impl;
	
	@Param
	public MerrySetWorkload workload;

	protected ObjectSet<Object> getNewSet() {
		return (ObjectSet<Object>)impl.maker.get();
	}

	protected ObjectSet<Object> copySet(ObjectSet<Object> original) {
		return (ObjectSet<Object>)impl.copyMaker.apply(original);
	}

	public void testSetup() {
		fullSet = this.getNewSet();
		values = generator.generateArray(size);
		for (int i = 0; i < size; i++) {
			fullSet.add(values[i]);
		}
	}
	
	@Benchmark
	public void bench() throws InterruptedException {
		workload.run(this);
		blackhole.consume(fullSet);
	}

	public enum MerrySetWorkload {

		POPULATE {
			@Override
			void run(MerrySetBench self) {
				ObjectSet<Object> newSet = self.getNewSet();
				for (int i = 0; i < self.size; i++) {
					newSet.add(self.values[i]);
				}
				self.blackhole.consume(newSet);
			}
		}, //

		ITERATE {
			@Override
			void run(MerrySetBench self) {
				for (Object element : self.fullSet) {
					self.blackhole.consume(element);
				}
			}
		},

		COPY {
			@Override
			void run(MerrySetBench self) {
				ObjectSet<Object> newSet = self.copySet(self.fullSet);
				self.blackhole.consume(newSet);
			}
		},

		CONTAINS {

			@Override
			void run(MerrySetBench self) {
				int index = self.generator.generateIndex(self.size);
				self.blackhole.consume(self.fullSet.contains(self.values[index]));
			}
		};

		abstract void run(MerrySetBench self);

	}

}