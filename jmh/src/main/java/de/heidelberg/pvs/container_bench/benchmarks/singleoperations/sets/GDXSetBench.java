package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.sets;

import com.badlogic.gdx.utils.ObjectSet;
import de.heidelberg.pvs.container_bench.factories.GDXSetFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

/**
 * Abstract class for every test with GDX ObjectSet implementation
 * 
 * @author Diego
 *
 */
public class GDXSetBench extends AbstractSetBench<Object> {

	private ObjectSet<Object> fullSet;
	private Object[] values;

	@Param
	public GDXSetFact impl;
	
	@Param
	public GDXSetWorkload workload;

	protected ObjectSet<Object> getNewSet() {
		return impl.maker.get();
	}

	protected ObjectSet<Object> copySet(ObjectSet<Object> original) {
		return impl.copyMaker.apply(original);
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

	public enum GDXSetWorkload {

		POPULATE {
			@Override
			void run(GDXSetBench self) {
				ObjectSet<Object> newSet = self.getNewSet();
				for (int i = 0; i < self.size; i++) {
					newSet.add(self.values[i]);
				}
				self.blackhole.consume(newSet);
			}
		}, //

		ITERATE {
			@Override
			void run(GDXSetBench self) {
				for (Object element : self.fullSet) {
					self.blackhole.consume(element);
				}
			}
		},

		COPY {
			@Override
			void run(GDXSetBench self) {
				ObjectSet<Object> newSet = self.copySet(self.fullSet);
				self.blackhole.consume(newSet);
			}
		},

		CONTAINS {

			@Override
			void run(GDXSetBench self) {
				int index = self.generator.generateIndex(self.size);
				self.blackhole.consume(self.fullSet.contains(self.values[index]));
			}
		};

		abstract void run(GDXSetBench self);

	}

}