/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.ds.support.TrimRandom;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.squidmath.HastyPointHash;
import squidpony.squidmath.HushPointHash;
import squidpony.squidmath.IntPointHash;
import squidpony.squidmath.PointHash;

import java.util.concurrent.TimeUnit;

/**
 * Measuring point hashes for 3D points:
 * <pre>
 *
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class HashPointBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {

        public int idx = 0;
        private final int[] intInputs = new int[1 << 19];
        private final long[] longInputs = new long[1 << 19];

        @Setup(Level.Trial)
        public void setup() {
            TrimRandom random = new TrimRandom(1000L);
            for (int i = 0; i < (1 << 19); i++) {
                intInputs[i] = (int)(longInputs[i] = random.nextLong());
            }
            idx = 0;
        }

    }

    @Benchmark
    public long measurePointHash2D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return PointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2]);
    }

    @Benchmark
    public long measurePointHash3D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return PointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3]);
    }

    @Benchmark
    public long measurePointHash4D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return PointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4]);
    }

    @Benchmark
    public long measurePointHash5D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return PointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4], longInputs[idx + 5]);
    }

    @Benchmark
    public long measurePointHash6D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return PointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4], longInputs[idx + 5], longInputs[idx + 6]);
    }

    @Benchmark
    public long measureHastyPointHash2D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HastyPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2]);
    }

    @Benchmark
    public long measureHastyPointHash3D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HastyPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3]);
    }

    @Benchmark
    public long measureHastyPointHash4D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HastyPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4]);
    }

    @Benchmark
    public long measureHastyPointHash5D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HastyPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4], longInputs[idx + 5]);
    }

    @Benchmark
    public long measureHastyPointHash6D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HastyPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4], longInputs[idx + 5], longInputs[idx + 6]);
    }

    @Benchmark
    public long measureHushPointHash2D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HushPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2]);
    }

    @Benchmark
    public long measureHushPointHash3D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HushPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3]);
    }

    @Benchmark
    public long measureHushPointHash4D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HushPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4]);
    }

    @Benchmark
    public long measureHushPointHash5D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HushPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4], longInputs[idx + 5]);
    }

    @Benchmark
    public long measureHushPointHash6D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        long[] longInputs = s.longInputs;
        return HushPointHash.hashAll(longInputs[idx], longInputs[idx + 1], longInputs[idx + 2], longInputs[idx + 3],
                longInputs[idx + 4], longInputs[idx + 5], longInputs[idx + 6]);
    }

    @Benchmark
    public int measureIntPointHash2D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        int[] intInputs = s.intInputs;
        return IntPointHash.hashAll(intInputs[idx], intInputs[idx + 1], intInputs[idx + 2]);
    }

    @Benchmark
    public int measureIntPointHash3D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        int[] intInputs = s.intInputs;
        return IntPointHash.hashAll(intInputs[idx], intInputs[idx + 1], intInputs[idx + 2], intInputs[idx + 3]);
    }

    @Benchmark
    public int measureIntPointHash4D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        int[] intInputs = s.intInputs;
        return IntPointHash.hashAll(intInputs[idx], intInputs[idx + 1], intInputs[idx + 2], intInputs[idx + 3],
                intInputs[idx + 4]);
    }

    @Benchmark
    public int measureIntPointHash5D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        int[] intInputs = s.intInputs;
        return IntPointHash.hashAll(intInputs[idx], intInputs[idx + 1], intInputs[idx + 2], intInputs[idx + 3],
                intInputs[idx + 4], intInputs[idx + 5]);
    }

    @Benchmark
    public int measureIntPointHash6D(BenchmarkState s)
    {
        int idx = s.idx = s.idx + 8 & 0x7FFF8;
        int[] intInputs = s.intInputs;
        return IntPointHash.hashAll(intInputs[idx], intInputs[idx + 1], intInputs[idx + 2], intInputs[idx + 3],
                intInputs[idx + 4], intInputs[idx + 5], intInputs[idx + 6]);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You are expected to see the different run modes for the same benchmark.
     * Note the units are different, scores are consistent with each other.
     *
     * You can run this test:
     *
     * a) Via the command line from the squidlib-performance module's root folder:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar HashPointBenchmark -wi 4 -i 5 -f 1
     *
     *    (we requested 8 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashPointBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(8)
                .measurementIterations(8)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}