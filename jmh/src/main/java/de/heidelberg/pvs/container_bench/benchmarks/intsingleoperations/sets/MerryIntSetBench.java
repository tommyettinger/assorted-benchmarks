package de.heidelberg.pvs.container_bench.benchmarks.intsingleoperations.sets;

import ds.merry.IntSet;
import de.heidelberg.pvs.container_bench.factories.MerryIntSetFact;
import org.openjdk.jmh.annotations.Param;

public class MerryIntSetBench extends AbstractIntSetBench {
	
	@Param MerryIntSetFact impl;
	
	IntSet fullSet;
	
	@Override
	public void testSetup() {
		fullSet = impl.maker.get();
		for (int i = 0; i < values.length; i++) {
			fullSet.add(values[i]);
		}
		
	}


	@Override
	protected void populateBench() {
		
		IntSet newSet = impl.maker.get();
		for (int i = 0; i < values.length; i++) {
			newSet.add(values[i]);
		}
		blackhole.consume(newSet);
		
	}

	@Override
	protected void containsBench() {
		int index = generator.generateIndex(size);
		blackhole.consume(fullSet.contains(values[index]));
	}

	@Override
	protected void copyBench() {
		IntSet newSet = impl.maker.get();
		newSet.addAll(fullSet);
		blackhole.consume(newSet);
	}

	@Override
	protected void iterateBench() {
		IntSet.IntSetIterator it = fullSet.iterator();
		while (it.hasNext)
		{
			blackhole.consume(it.next());
		}
//		fullSet.forEach(new Consumer<IntCursor>() {
//			@Override
//			public void accept(IntCursor t) {
//				blackhole.consume(t.value);
//			}
//		});
	}
}
