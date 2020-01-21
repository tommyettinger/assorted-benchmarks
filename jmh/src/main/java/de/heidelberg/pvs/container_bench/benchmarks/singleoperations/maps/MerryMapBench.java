package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.maps;

import com.github.tommyettinger.merry.lp.ObjectMap;
import de.heidelberg.pvs.container_bench.factories.MerryMapFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class MerryMapBench extends AbstractMapBench<Object, Integer> {

	private ObjectMap<Object, Integer> fullMap;
	private Object[] keys;
	private Integer[] values;

	@Param
	public MerryMapFact impl;

	@Param
	public MerryMapWorkload workload;

	protected ObjectMap<Object, Integer> getNewMap() {
		return impl.maker.get();
	}

	protected ObjectMap<Object, Integer> copyMap(ObjectMap<Object, Integer> fullMap2) {
		ObjectMap<Object, Integer> map = this.getNewMap();
		map.putAll(fullMap2);
		return map;
	}

	@Override
	public void testSetup() {
		fullMap = this.getNewMap();

		keys = keyGenerator.generateArray(size);
		values = valueGenerator.generateArray(size);

		for (int i = 0; i < size; i++) {
			fullMap.put(keys[i], values[i]);
		}

	}
	
	@Benchmark
	public void bench() throws InterruptedException {
		workload.run(this);
		blackhole.consume(fullMap);
	}

	public enum MerryMapWorkload {

		POPULATE {
			@Override
			void run(MerryMapBench self) {
				ObjectMap<Object, Integer> newMap = self.getNewMap();
				for (int i = 0; i < self.size; i++) {
					newMap.put(self.keys[i], self.values[i]);
				}
				self.blackhole.consume(newMap);
			}
		},

		CONTAINS {
			@Override
			void run(MerryMapBench self) {
				int index = self.keyGenerator.generateIndex(self.size);
				self.blackhole.consume(self.fullMap.containsKey(self.keys[index]));
			}
		}, 
		
		COPY {
			@Override
			void run(MerryMapBench self) {
				ObjectMap<Object, Integer> newMap = self.copyMap(self.fullMap);
				self.blackhole.consume(newMap);
			}
		}, 
		
		ITERATE {
			@Override
			void run(MerryMapBench self) {
				for (ObjectMap.Entry<Object, Integer> c : self.fullMap) {
					self.blackhole.consume(c.key);
					self.blackhole.consume(c.value);
				}
			}
		};

		abstract void run(MerryMapBench self);

	}

}
