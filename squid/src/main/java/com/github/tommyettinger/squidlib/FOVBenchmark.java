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

import com.github.yellowstonegames.grid.Region;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import rlforj.examples.ExampleBoard;
import rlforj.los.PrecisePermissive;
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
 * <pre>
 * Benchmark                   Mode  Cnt   Score   Error  Units
 * FOVBenchmark.doAdLOS        avgt    4  25.756 ± 2.353  us/op
 * FOVBenchmark.doAdShadow10   avgt    4  27.399 ± 1.702  us/op
 * FOVBenchmark.doAdShadow20   avgt    4  27.570 ± 2.247  us/op
 * FOVBenchmark.doAdShadow30   avgt    4  28.290 ± 0.884  us/op
 * FOVBenchmark.doAdShadow5    avgt    4  26.901 ± 1.851  us/op
 * FOVBenchmark.doAdShadowMax  avgt    4  49.146 ± 3.643  us/op
 * FOVBenchmark.doIdLOS        avgt    4  25.767 ± 1.417  us/op
 * FOVBenchmark.doIdShadow10   avgt    4  51.652 ± 5.020  us/op
 * FOVBenchmark.doIdShadow20   avgt    4  51.813 ± 2.694  us/op
 * FOVBenchmark.doIdShadow30   avgt    4  51.419 ± 4.904  us/op
 * FOVBenchmark.doIdShadow5    avgt    4  50.219 ± 5.368  us/op
 * FOVBenchmark.doIdShadowMax  avgt    4  73.290 ± 5.870  us/op
 * </pre>
 * Testing RL4J's precise permissive FOV without optimizing it at all; it seems to slow down as the view radius
 * increases even if it has encountered obstacles in all directions before it reaches that radius.
 * <pre>
 * Benchmark                    Mode  Cnt      Score      Error  Units
 * FOVBenchmark.doRlPrecise10   avgt    4     65.303 ±    0.461  us/op
 * FOVBenchmark.doRlPrecise20   avgt    4     78.083 ±    0.846  us/op
 * FOVBenchmark.doRlPrecise30   avgt    4     97.025 ±    0.593  us/op
 * FOVBenchmark.doRlPrecise5    avgt    4     59.704 ±    5.301  us/op
 * FOVBenchmark.doRlPreciseMax  avgt    4  37562.747 ± 4087.306  us/op
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 4)
public class FOVBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        public int DIMENSION = 500;
        public DungeonGenerator dungeonGen = new DungeonGenerator(DIMENSION, DIMENSION, new StatefulRNG(0x1337BEEFDEAL));
        public char[][] map;
        public double[][] resD;
        public double[][] lightD;
        public float[][] resF;
        public float[][] lightF;
        public GreasedRegion floors;
        public GreasedRegion blocking;
        public GreasedRegion lit;
        public ExampleBoard board;
        public PrecisePermissive pp;
        public Region blockingR;
        public Region litR;
        public int floorCount;
        public Coord[] floorArray;
        public int idx;

        @Setup(Level.Trial)
        public void setup() {
            Coord.expandPoolTo(DIMENSION, DIMENSION);
            map = dungeonGen.generate();
            floors = new GreasedRegion(map, '.');
            blocking = floors.copy().not();
            lit = new GreasedRegion(DIMENSION, DIMENSION);
            blockingR = new Region(map, '.').not();
            litR = new Region(DIMENSION, DIMENSION);
            floorCount = floors.size();
            floorArray = floors.asCoords();
            System.out.println("Floors: " + floorCount);
            System.out.println("Percentage walkable: " + floorCount * 100.0 / (DIMENSION * DIMENSION) + "%");
            resD = FOV.generateSimpleResistances(map);
            resF = com.github.yellowstonegames.grid.FOV.generateSimpleResistances(map);
            lightD = new double[DIMENSION][DIMENSION];
            lightF = new float[DIMENSION][DIMENSION];
            pp = new PrecisePermissive();
            board = new ExampleBoard(blocking);
            idx = 0;
        }

    }

    @Benchmark
    public void doIdShadow5(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV.reuseFOV(state.resD, state.lightD, point.x, point.y, 5.0, Radius.CIRCLE);
        blackhole.consume(state.lightD);
    }

    @Benchmark
    public void doIdShadow10(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV.reuseFOV(state.resD, state.lightD, point.x, point.y, 10.0, Radius.CIRCLE);
        blackhole.consume(state.lightD);
    }

    @Benchmark
    public void doIdShadow20(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV.reuseFOV(state.resD, state.lightD, point.x, point.y, 20.0, Radius.CIRCLE);
        blackhole.consume(state.lightD);
    }

    @Benchmark
    public void doIdShadow30(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV.reuseFOV(state.resD, state.lightD, point.x, point.y, 30.0, Radius.CIRCLE);
        blackhole.consume(state.lightD);
    }

    @Benchmark
    public void doIdShadowMax(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV.reuseFOV(state.resD, state.lightD, point.x, point.y, state.DIMENSION << 1, Radius.CIRCLE);
        blackhole.consume(state.lightD);
    }

    @Benchmark
    public void doIdLOS(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV.reuseLOS(state.blocking, state.lit, point.x, point.y);
        blackhole.consume(state.lit);
    }

    @Benchmark
    public void doAdShadow5(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOV(state.resF, state.lightF, point.x, point.y, 5f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdShadow10(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOV(state.resF, state.lightF, point.x, point.y, 10f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdShadow20(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOV(state.resF, state.lightF, point.x, point.y, 20f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdShadow30(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOV(state.resF, state.lightF, point.x, point.y, 30f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdShadowMax(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOV(state.resF, state.lightF, point.x, point.y, state.DIMENSION << 1, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdLOS(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseLOS(state.blockingR, state.litR, point.x, point.y);
        blackhole.consume(state.litR);
    }



    @Benchmark
    public void doRlPrecise5(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        state.board.resetVisited();
        state.pp.visitFieldOfView(state.board, point.x, point.y, 5);
        blackhole.consume(state.board);
    }

    @Benchmark
    public void doRlPrecise10(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        state.board.resetVisited();
        state.pp.visitFieldOfView(state.board, point.x, point.y, 10);
        blackhole.consume(state.board);
    }

    @Benchmark
    public void doRlPrecise20(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        state.board.resetVisited();
        state.pp.visitFieldOfView(state.board, point.x, point.y, 20);
        blackhole.consume(state.board);
    }

    @Benchmark
    public void doRlPrecise30(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        state.board.resetVisited();
        state.pp.visitFieldOfView(state.board, point.x, point.y, 30);
        blackhole.consume(state.board);
    }

    @Benchmark
    public void doRlPreciseMax(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        state.board.resetVisited();
        state.pp.visitFieldOfView(state.board, point.x, point.y, state.DIMENSION << 1);
        blackhole.consume(state.board);
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
     *    $ java -jar benchmarks.jar FOVBenchmark -gc true
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
