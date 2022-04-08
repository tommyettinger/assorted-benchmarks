package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.lists;

import com.badlogic.gdx.utils.Array;
import de.heidelberg.pvs.container_bench.factories.GDXArrayFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class GDXArrayBench extends AbstractListBench<Object> {

	Array<Object> fullList;
	Object[] values;
	
	@Param
	public GDXArrayFact impl;
	
	@Param
	public GDXArrayWorkload workload;
	
	protected Array<Object> getNewList() {
		return impl.maker.get();
	}

	protected Array<Object> copyList(Array<Object> original) {
		return impl.copyMaker.apply(original);
	}
	
	@Override
	public void testSetup() {
		fullList = this.getNewList();
		values = this.generator.generateArray(size);
		for (int i = 0; i < size; i++) {
			fullList.add(values[i]);
		}

	}
	
	@Benchmark
	public void bench() {
		workload.run(this);
		blackhole.consume(fullList);
	}
	
	public enum GDXArrayWorkload {

		ITERATE {
			@Override
			public void run(GDXArrayBench self) {
				for (Object element : self.fullList) {
					self.blackhole.consume(element);
				}
			}
		}, //

		GET_INDEX {

			@Override
			public void run(GDXArrayBench self) {
				int index = self.generator.generateIndex(self.size);
				self.blackhole.consume(self.fullList.get(index));
			}
		}, //

		CONTAINS {

			@Override
			public void run(GDXArrayBench self) {
				int index = self.generator.generateIndex(self.size);
				self.blackhole.consume(self.fullList.contains(self.values[index], false));

			}

		}, //

		POPULATE {

			@Override
			public <T> void run(GDXArrayBench self) {
				Array<Object> newList = self.getNewList();
				for (int i = 0; i < self.size; i++) {
					newList.add(self.values[i]);
				}
				self.blackhole.consume(newList);
			}

		}, //
		
		COPY {

			@Override
			public void run(GDXArrayBench self) {
				Array<Object> newList = self.copyList(self.fullList);
				self.blackhole.consume(newList);
			}
		}, 
		
		// TODO: Add more scenarios for single operation
		;
		
		abstract public <T> void run(GDXArrayBench self);
		
	}

}
