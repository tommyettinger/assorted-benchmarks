package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.maps;

import com.badlogic.gdx.utils.ObjectMap;
import de.heidelberg.pvs.container_bench.factories.GDXMapFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class GDXMapBench extends AbstractMapBench<Object, Integer> {

	private ObjectMap<Object, Integer> fullMap;
	private Object[] keys;
	private Integer[] values;

	@Param
	public GDXMapFact impl;

	@Param
	public GDXMapWorkload workload;

	protected ObjectMap<Object, Integer> getNewMap() {
		return impl.maker.get();
	}

	protected ObjectMap<Object, Integer> copyMap(ObjectMap<Object, Integer> fullMap2) {
		ObjectMap<Object, Integer> map = this.getNewMap();
		//map.putAll(fullMap2);
		map.ensureCapacity(fullMap2.size);
		for (ObjectMap.Entry<Object, Integer> kv : fullMap2.entries()) {
			map.put(kv.key, kv.value);
		}
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
			void run(GDXMapBench self) {
				ObjectMap<Object, Integer> newMap = self.getNewMap();
				for (int i = 0; i < self.size; i++) {
					newMap.put(self.keys[i], self.values[i]);
				}
				self.blackhole.consume(newMap);
			}
		},

		CONTAINS {
			@Override
			void run(GDXMapBench self) {
				for (int i = 0; i < 64; i++) {
					self.blackhole.consume(self.fullMap.containsKey(self.keys[self.keyGenerator.generateIndex(self.size)]));
				}
			}
		}, 
		
		COPY {
			@Override
			void run(GDXMapBench self) {
				ObjectMap<Object, Integer> newMap = self.copyMap(self.fullMap);
				self.blackhole.consume(newMap);
			}
		}, 
		
		ITERATE {
			@Override
			void run(GDXMapBench self) {
				for (ObjectMap.Entry<Object, Integer> c : self.fullMap) {
					self.blackhole.consume(c.key);
					self.blackhole.consume(c.value);
				}
			}
		};

		abstract void run(GDXMapBench self);

	}

}
