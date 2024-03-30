package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.maps;

import de.heidelberg.pvs.container_bench.factories.JDKMapIdentityFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

import java.util.Map;
import java.util.Map.Entry;

public class JDKIdentityMapBench extends AbstractMapBench<Object, Object> {

	private Map<Object, Object> fullMap;
	protected Object[] keys;

	@Param
	public JDKMapIdentityFact impl;

	@Param
	public JDKMapWorkload workload;
	
	
	protected Map<Object, Object> getNewMap() {
		return impl.maker.get();
	}
	
	protected Map<Object, Object> copyMap(Map<Object, Object> fullMap2) {
		Map<Object, Object> map = this.getNewMap();
		map.putAll(fullMap2);
		return map;
	}
	
	@Override
	public void testSetup() {
		int varietyOfKeys = (int) (size * ((double)percentageRangeKeys / 100));
		fullMap = this.getNewMap();

		keys = keyGenerator.generateArray(size);

		for(int i = 0; i < size; i++) {
			fullMap.put(keys[i], keys[i]);
		}
		
	}
	
	@Benchmark
	public void bench() throws InterruptedException {
		workload.run(this);
		blackhole.consume(fullMap);
	}
	
	public enum JDKMapWorkload {
		
		POPULATE {

			@Override
			public void run(JDKIdentityMapBench self) throws InterruptedException {
				Map<Object, Object> newMap = self.getNewMap();
				for(int i = 0; i < self.size; i++) {
					self.blackhole.consume(newMap.put(self.keys[i], self.keys[i]));
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
				}
				self.blackhole.consume(newMap);
			}
			
		}, // 
		
		
		CONTAINS {

			@Override
			public void run(JDKIdentityMapBench self) throws InterruptedException {
				for (int i = 0; i < 64; i++) {
					self.blackhole.consume(self.fullMap.containsKey(self.keys[self.keyGenerator.generateIndex(self.size)]));
				}
			}
			
		}, //
		
		COPY {

			@Override
			public void run(JDKIdentityMapBench self) throws InterruptedException {
				Map<Object, Object> newMap = self.copyMap(self.fullMap);
				self.blackhole.consume(newMap);
			}
		}, //
		
		ITERATE {

			@Override
			public void run(JDKIdentityMapBench self) throws InterruptedException {
				for(Entry<Object, Object> entry : self.fullMap.entrySet()) {
					self.blackhole.consume(entry.getKey());
					self.blackhole.consume(entry.getValue());
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
				}
				
			}
			
			
		}
		;
		
		public abstract void run(JDKIdentityMapBench self) throws InterruptedException;
		
	}
	
}
