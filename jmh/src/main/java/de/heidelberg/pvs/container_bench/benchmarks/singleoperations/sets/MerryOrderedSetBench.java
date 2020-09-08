package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.sets;

import ds.merry.OrderedSet;
import de.heidelberg.pvs.container_bench.factories.MerryOrderedSetFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

/**
 * Abstract class for every test with Merry OrderedSet implementation
 * 
 * @author Diego
 *
 */
public class MerryOrderedSetBench extends AbstractSetBench<Object> {

	private OrderedSet<Object> fullSet;
	private Object[] values;

	@Param
	public MerryOrderedSetFact impl;
	
	@Param
	public MerryOrderedSetWorkload workload;

	protected OrderedSet<Object> getNewSet() {
		return (OrderedSet<Object>)impl.maker.get();
	}

	protected OrderedSet<Object> copySet(OrderedSet<Object> original) {
		return (OrderedSet<Object>)impl.copyMaker.apply(original);
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

	public enum MerryOrderedSetWorkload {

		POPULATE {
			@Override
			void run(MerryOrderedSetBench self) {
				OrderedSet<Object> newSet = self.getNewSet();
				for (int i = 0; i < self.size; i++) {
					newSet.add(self.values[i]);
				}
				self.blackhole.consume(newSet);
			}
		}, //

		ITERATE {
			@Override
			void run(MerryOrderedSetBench self) {
				for (Object element : self.fullSet) {
					self.blackhole.consume(element);
				}
			}
		},

		COPY {
			@Override
			void run(MerryOrderedSetBench self) {
				OrderedSet<Object> newSet = self.copySet(self.fullSet);
				self.blackhole.consume(newSet);
			}
		},

		CONTAINS {

			@Override
			void run(MerryOrderedSetBench self) {
				int index = self.generator.generateIndex(self.size);
				self.blackhole.consume(self.fullSet.contains(self.values[index]));
			}
		};

		abstract void run(MerryOrderedSetBench self);

	}

}