package de.heidelberg.pvs.container_bench.benchmarks.singleoperations.sets;

import de.heidelberg.pvs.container_bench.factories.JDKCoordSetFact;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import squidpony.squidmath.Coord;

import java.util.Set;


public class JDKCoordSetBench extends AbstractSetBench<Coord> {
	
	private Set<Coord> fullSet;
	protected Coord[] values;
	
	@Param
	public JDKCoordSetFact impl;
	
	@Param
	public JDKSetWorkload workload;
	
	@SuppressWarnings("unchecked")
	protected Set<Coord> getNewSet() {
		// FIXME: Find a better way to avoid this casting
		return (Set<Coord>) impl.maker.get();
	}
	
	@Benchmark
	public void bench() {
		workload.run(this);
		blackhole.consume(fullSet);
	}
	
	protected Set<Coord> copySet(Set<Coord> fullSet2) {
		Set<Coord> set = this.getNewSet();
		set.addAll(fullSet2);
		return set;
	}
	
	public void testSetup() {
		fullSet = this.getNewSet();
		values = generator.generateArray(size);
		for(int i = 0; i < size; i++) {
			fullSet.add(values[i]);
		}
	}
	
	public enum JDKSetWorkload {
		
		POPULATE {
			@Override
			void run(JDKCoordSetBench self) {
				Set<Coord> newSet = self.getNewSet();
				for(int i = 0; i < self.size; i++) {
					newSet.add(self.values[i]);
				}
				self.blackhole.consume(newSet);
			}
			
		}, //
		
		ITERATE {
			@Override
			void run(JDKCoordSetBench self) {
				for(Object element : self.fullSet) {
					self.blackhole.consume(element);
				}
			}
			
		}, 
		
		COPY {
			@Override
			void run(JDKCoordSetBench self) {
				Set<Coord> newSet = self.copySet(self.fullSet);
				self.blackhole.consume(newSet);
			}
		}, 
		
		CONTAINS {

			@Override
			void run(JDKCoordSetBench self) {
				int index = self.generator.generateIndex(self.size);
				self.blackhole.consume(self.fullSet.contains(self.values[index]));
			}
		}
		
		;
		
		abstract void run(JDKCoordSetBench self);
		
	}
	
}