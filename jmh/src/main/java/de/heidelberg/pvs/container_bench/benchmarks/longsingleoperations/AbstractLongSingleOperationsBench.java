package de.heidelberg.pvs.container_bench.benchmarks.longsingleoperations;

import de.heidelberg.pvs.container_bench.generators.IntPayloadType;
import de.heidelberg.pvs.container_bench.generators.PayloadType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class of Single Operation benchmarks with primitives.
 * Each workload is composed by one or multiple calls of a single collection
 * operation.
 * 
 * @author diego.costa
 *
 */
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Timeout(time = 20, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 40, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(2)
@State(Scope.Thread)
public abstract class AbstractLongSingleOperationsBench {
	
	/**
	 * From 100 - 1M
	 */
	@Param({ "100", "1000", "10000", "100000", "1000000" })
	public int size;

	/**
	 * Random seed generated using https://www.random.org/ Number between 1 - 1M
	 */
	@Param({ "467505" })
	public int seed;

	@Param()
	public PayloadType payloadType = PayloadType.LONG_UNIFORM;

	/**
	 * Blackhole object responsible for consuming any return from our tested methods
	 */
	protected Blackhole blackhole;

	public abstract void generatorSetup() throws IOException;

	public abstract void testSetup();

	@Setup
	public void initializeSetup(Blackhole blackhole) throws IOException {
		this.blackhole = blackhole;
		// Initialize the seed
		this.generatorSetup();
		// Test Setup
		this.testSetup();

	}

}
