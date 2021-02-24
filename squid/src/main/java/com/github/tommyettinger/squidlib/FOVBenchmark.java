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

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks were run With Java 15, HotSpot, on a 10th-gen i7 hexacore mobile processor running Windows 10.
 * <br>
 * 64x64:
 * <pre>
 * Benchmark                        Mode  Cnt    Score     Error  Units
 * FOVBenchmark.doRipple4           avgt    4  178.428 ±  11.250  ms/op
 * FOVBenchmark.doRipple6           avgt    4  321.717 ±  37.487  ms/op
 * FOVBenchmark.doRipple8           avgt    4  447.333 ±  40.856  ms/op
 * FOVBenchmark.doRippleLoose4      avgt    4  190.591 ±  21.360  ms/op
 * FOVBenchmark.doRippleLoose6      avgt    4  357.926 ±  55.520  ms/op
 * FOVBenchmark.doRippleLoose8      avgt    4  544.752 ± 284.655  ms/op
 * FOVBenchmark.doRippleTight4      avgt    4  155.512 ±  12.243  ms/op
 * FOVBenchmark.doRippleTight6      avgt    4  257.555 ±  23.819  ms/op
 * FOVBenchmark.doRippleTight8      avgt    4  330.164 ±  48.733  ms/op
 * FOVBenchmark.doRippleVeryLoose4  avgt    4  192.560 ±   7.363  ms/op
 * FOVBenchmark.doRippleVeryLoose6  avgt    4  372.891 ±  39.487  ms/op
 * FOVBenchmark.doRippleVeryLoose8  avgt    4  569.839 ±  82.032  ms/op
 * FOVBenchmark.doShadow4           avgt    4    3.046 ±   0.088  ms/op
 * FOVBenchmark.doShadow6           avgt    4    4.167 ±   0.371  ms/op
 * FOVBenchmark.doShadow8           avgt    4    5.086 ±   0.746  ms/op
 * </pre>
 * Wow, Ripple is at least 50x slower than Shadow. And this is with the most recent GC opt...
 * Without that opt, the results are about the same:
 * <pre>
 * Benchmark                        Mode  Cnt    Score     Error  Units
 * FOVBenchmark.doRipple4           avgt    4  177.446 ±  25.251  ms/op
 * FOVBenchmark.doRipple6           avgt    4  321.845 ±  12.472  ms/op
 * FOVBenchmark.doRipple8           avgt    4  452.460 ±  65.996  ms/op
 * FOVBenchmark.doRippleLoose4      avgt    4  189.676 ±  13.986  ms/op
 * FOVBenchmark.doRippleLoose6      avgt    4  355.760 ±  32.303  ms/op
 * FOVBenchmark.doRippleLoose8      avgt    4  529.852 ± 155.911  ms/op
 * FOVBenchmark.doRippleTight4      avgt    4  154.605 ±   8.859  ms/op
 * FOVBenchmark.doRippleTight6      avgt    4  253.677 ±  19.818  ms/op
 * FOVBenchmark.doRippleTight8      avgt    4  326.990 ±   7.981  ms/op
 * FOVBenchmark.doRippleVeryLoose4  avgt    4  193.375 ±  13.003  ms/op
 * FOVBenchmark.doRippleVeryLoose6  avgt    4  370.524 ±  20.170  ms/op
 * FOVBenchmark.doRippleVeryLoose8  avgt    4  563.479 ±  58.760  ms/op
 * FOVBenchmark.doShadow4           avgt    4    3.010 ±   0.123  ms/op
 * FOVBenchmark.doShadow6           avgt    4    3.987 ±   0.908  ms/op
 * FOVBenchmark.doShadow8           avgt    4    4.906 ±   0.309  ms/op
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 4)
public class FOVBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        public int DIMENSION = 64;
        public DungeonGenerator dungeonGen = new DungeonGenerator(DIMENSION, DIMENSION, new StatefulRNG(0x1337BEEFDEAL));
        public char[][] map;
        public double[][] resMap;
        public double[][] lightMap;
        public GreasedRegion floors;
        public int floorCount;
        public Coord[] floorArray;

        @Setup(Level.Trial)
        public void setup() {
            Coord.expandPoolTo(DIMENSION, DIMENSION);
            map = dungeonGen.generate();
            floors = new GreasedRegion(map, '.');
            floorCount = floors.size();
            floorArray = floors.asCoords();
            System.out.println("Floors: " + floorCount);
            System.out.println("Percentage walkable: " + floorCount * 100.0 / (DIMENSION * DIMENSION) + "%");
            resMap = FOV.generateSimpleResistances(map);
            lightMap = new double[DIMENSION][DIMENSION];
        }

    }

    @Benchmark
    public long doShadow4(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseFOV(res, light, x, y, 4.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doShadow6(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseFOV(res, light, x, y, 6.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doShadow8(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseFOV(res, light, x, y, 8.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleTight4(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 1, x, y, 4.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleTight6(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 1, x, y, 6.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleTight8(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 1, x, y, 8.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRipple4(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 2, x, y, 4.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRipple6(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 2, x, y, 6.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRipple8(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 2, x, y, 8.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleLoose4(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 3, x, y, 4.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleLoose6(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 3, x, y, 6.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleLoose8(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 3, x, y, 8.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleVeryLoose4(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 6, x, y, 4.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleVeryLoose6(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 6, x, y, 6.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doRippleVeryLoose8(BenchmarkState state)
    {
        long scanned = 0;
        double[][] res = state.resMap, light = state.lightMap;
        for (int x = 1; x < state.DIMENSION - 1; x++) {
            for (int y = 1; y < state.DIMENSION - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                FOV.reuseRippleFOV(res, light, 6, x, y, 8.0, Radius.CIRCLE);
                scanned++;
            }
        }
        return scanned;
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
     *    $ java -jar target/benchmarks.jar FOVBenchmark -wi 3 -i 3 -f 1 -gc true -w 10s -r 10s
     *
     *    (we requested 3 warmup/measurement iterations, single fork, garbage collect between benchmarks)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FOVBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(3)
                .forks(1)
                .shouldDoGC(true)
                .build();
        new Runner(opt).run();
    }
}
