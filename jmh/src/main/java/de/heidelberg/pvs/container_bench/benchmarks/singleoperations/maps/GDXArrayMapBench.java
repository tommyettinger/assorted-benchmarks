package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.maps;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import de.heidelberg.pvs.container_bench.factories.GDXArrayMapFact;
import de.heidelberg.pvs.container_bench.factories.GDXMapFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class GDXArrayMapBench extends AbstractMapBench<Object, Integer> {

	private ArrayMap<Object, Integer> fullMap;
	private Object[] keys;
	private Integer[] values;

	@Param
	public GDXArrayMapFact impl;

	@Param
	public GDXMapWorkload workload;

	protected ArrayMap<Object, Integer> getNewMap() {
		return impl.maker.get();
	}

	protected ArrayMap<Object, Integer> copyMap(ArrayMap<Object, Integer> fullMap2) {
		ArrayMap<Object, Integer> map = this.getNewMap();
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

	public enum GDXMapWorkload {

		POPULATE {
			@Override
			void run(GDXArrayMapBench self) {
				ArrayMap<Object, Integer> newMap = self.getNewMap();
				for (int i = 0; i < self.size; i++) {
					newMap.put(self.keys[i], self.values[i]);
				}
				self.blackhole.consume(newMap);
			}
		},

		CONTAINS {
			@Override
			void run(GDXArrayMapBench self) {
				int index = self.keyGenerator.generateIndex(self.size);
				self.blackhole.consume(self.fullMap.containsKey(self.keys[index]));
			}
		}, 
		
		COPY {
			@Override
			void run(GDXArrayMapBench self) {
				ArrayMap<Object, Integer> newMap = self.copyMap(self.fullMap);
				self.blackhole.consume(newMap);
			}
		}, 
		
		ITERATE {
			@Override
			void run(GDXArrayMapBench self) {
				for (ObjectMap.Entry<Object, Integer> c : self.fullMap) {
					self.blackhole.consume(c.key);
					self.blackhole.consume(c.value);
				}
			}
		};

		abstract void run(GDXArrayMapBench self);

	}

}
