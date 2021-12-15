package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.maps;

import com.esotericsoftware.kryo.kryo5.util.CuckooObjectMap;
import de.heidelberg.pvs.container_bench.factories.KryoMapFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class KryoMapBench extends AbstractMapBench<Object, Integer> {

	private CuckooObjectMap<Object, Integer> fullMap;
	private Object[] keys;
	private Integer[] values;

	@Param
	public KryoMapFact impl;

	@Param
	public KryoMapWorkload workload;

	protected CuckooObjectMap<Object, Integer> getNewMap() {
		return impl.maker.get();
	}

	protected CuckooObjectMap<Object, Integer> copyMap(CuckooObjectMap<Object, Integer> fullMap2) {
		CuckooObjectMap<Object, Integer> map = this.getNewMap();
		//map.putAll(fullMap2);
		map.ensureCapacity(fullMap2.size);
		for (CuckooObjectMap.Entry<Object, Integer> kv : fullMap2.entries()) {
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

	public enum KryoMapWorkload {

		POPULATE {
			@Override
			void run(KryoMapBench self) {
				CuckooObjectMap<Object, Integer> newMap = self.getNewMap();
				for (int i = 0; i < self.size; i++) {
					newMap.put(self.keys[i], self.values[i]);
				}
				self.blackhole.consume(newMap);
			}
		},

		CONTAINS {
			@Override
			void run(KryoMapBench self) {
				int index = self.keyGenerator.generateIndex(self.size);
				self.blackhole.consume(self.fullMap.containsKey(self.keys[index]));
			}
		}, 
		
		COPY {
			@Override
			void run(KryoMapBench self) {
				CuckooObjectMap<Object, Integer> newMap = self.copyMap(self.fullMap);
				self.blackhole.consume(newMap);
			}
		}, 
		
		ITERATE {
			@Override
			void run(KryoMapBench self) {
				for (CuckooObjectMap.Entry<Object, Integer> c : self.fullMap.entries()) {
					self.blackhole.consume(c.key);
					self.blackhole.consume(c.value);
				}
			}
		};

		abstract void run(KryoMapBench self);

	}

}
