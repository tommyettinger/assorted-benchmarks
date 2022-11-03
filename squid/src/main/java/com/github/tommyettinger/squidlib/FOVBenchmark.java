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
import org.openjdk.jmh.runner.RunnerException;
import rlforj.examples.DistanceBoard;
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
 * With some simple optimizations on RL4J, mostly reducing allocations, it does much better; it's still making an on/off
 * LOS map rather than a graded FOV one, but it should be possible to make it work with a float[][].
 * <pre>
 * Benchmark                    Mode  Cnt      Score       Error  Units
 * FOVBenchmark.doRlPrecise10   avgt    4     21.265 ±     0.257  us/op
 * FOVBenchmark.doRlPrecise20   avgt    4     35.882 ±     1.751  us/op
 * FOVBenchmark.doRlPrecise30   avgt    4     53.633 ±     0.975  us/op
 * FOVBenchmark.doRlPrecise5    avgt    4     15.586 ±     2.216  us/op
 * FOVBenchmark.doRlPreciseMax  avgt    4  26211.230 ± 11283.769  us/op
 * </pre>
 *
 * With some more attempts at optimization, it does... worse. There may be environmental differences.
 * <pre>
 * Benchmark                    Mode  Cnt      Score       Error  Units
 * FOVBenchmark.doRlPrecise10   avgt    4     22.755 ±     1.189  us/op
 * FOVBenchmark.doRlPrecise20   avgt    4     33.906 ±     0.430  us/op
 * FOVBenchmark.doRlPrecise30   avgt    4     54.040 ±     0.417  us/op
 * FOVBenchmark.doRlPrecise5    avgt    4     19.835 ±     1.780  us/op
 * FOVBenchmark.doRlPreciseMax  avgt    4  29181.089 ± 10391.568  us/op
 * </pre>
 *
 * With distances assigned as floats to a DistanceBoard, precise permissive is generally slower, sometimes by
 * a massive degree.
 * <pre>
 * Benchmark                    Mode  Cnt      Score       Error  Units
 * FOVBenchmark.doAdLOS         avgt    4     25.575 ±     3.954  us/op
 * FOVBenchmark.doAdShadow10    avgt    4     27.106 ±     2.292  us/op
 * FOVBenchmark.doAdShadow20    avgt    4     27.628 ±     1.665  us/op
 * FOVBenchmark.doAdShadow30    avgt    4     28.661 ±    10.455  us/op
 * FOVBenchmark.doAdShadow5     avgt    4     30.053 ±     4.181  us/op
 * FOVBenchmark.doAdShadowMax   avgt    4     51.394 ±    19.166  us/op
 * FOVBenchmark.doIdLOS         avgt    4     25.535 ±     3.246  us/op
 * FOVBenchmark.doIdShadow10    avgt    4     50.993 ±     5.999  us/op
 * FOVBenchmark.doIdShadow20    avgt    4     50.912 ±     5.573  us/op
 * FOVBenchmark.doIdShadow30    avgt    4     52.692 ±     6.532  us/op
 * FOVBenchmark.doIdShadow5     avgt    4     50.305 ±    10.544  us/op
 * FOVBenchmark.doIdShadowMax   avgt    4     73.795 ±    13.444  us/op
 * FOVBenchmark.doRlPrecise10   avgt    4     36.464 ±     0.247  us/op
 * FOVBenchmark.doRlPrecise20   avgt    4     50.357 ±     5.017  us/op
 * FOVBenchmark.doRlPrecise30   avgt    4     69.362 ±     0.983  us/op
 * FOVBenchmark.doRlPrecise5    avgt    4     31.279 ±     0.165  us/op
 * FOVBenchmark.doRlPreciseMax  avgt    4  28238.068 ± 10737.640  us/op
 * </pre>
 *
 * OK, symmetrical FOV likely needs to be reworked...
 * <pre>
 * Benchmark                        Mode  Cnt       Score        Error  Units
 * FOVBenchmark.doAdSymmetrical10   avgt    4      82.382 ±      5.428  us/op
 * FOVBenchmark.doAdSymmetrical20   avgt    4     292.131 ±     73.724  us/op
 * FOVBenchmark.doAdSymmetrical30   avgt    4     698.022 ±    145.319  us/op
 * FOVBenchmark.doAdSymmetrical5    avgt    4      37.656 ±      2.870  us/op
 * FOVBenchmark.doAdSymmetricalMax  avgt    4  489955.736 ± 206642.742  us/op
 * </pre>
 *
 * Drawing an OrthoLine (which is naturally symmetrical) to any point before we add light to it, and back to the start
 * to ensure symmetry. This is actually quite fast! It's assigning float distances and is faster than RL4J's PPFOV when
 * it does the same. Five hundred times faster, in the case of radius 1000. Unfortunately, it has many visual artifacts.
 * <pre>
 * Benchmark                  Mode  Cnt   Score   Error  Units
 * FOVBenchmark.doAdOrtho10   avgt    4  31.989 ± 2.739  us/op
 * FOVBenchmark.doAdOrtho20   avgt    4  37.189 ± 1.888  us/op
 * FOVBenchmark.doAdOrtho30   avgt    4  37.107 ± 3.812  us/op
 * FOVBenchmark.doAdOrtho5    avgt    4  28.302 ± 3.634  us/op
 * FOVBenchmark.doAdOrthoMax  avgt    4  55.788 ± 7.116  us/op
 * </pre>
 *
 * Comparing two different Symmetrical FOV options, the 2Symmetrical methods seem much faster with large radii.
 * <pre>
 * Benchmark                         Mode  Cnt       Score        Error  Units
 * FOVBenchmark.doAd2Symmetrical10   avgt    4      40.417 ±      3.631  us/op
 * FOVBenchmark.doAd2Symmetrical20   avgt    4      55.089 ±      3.692  us/op
 * FOVBenchmark.doAd2Symmetrical30   avgt    4      60.006 ±      1.985  us/op
 * FOVBenchmark.doAd2Symmetrical5    avgt    4      30.000 ±      5.531  us/op
 * FOVBenchmark.doAd2SymmetricalMax  avgt    4    1390.654 ±    476.750  us/op
 * FOVBenchmark.doAdSymmetrical10    avgt    4      81.413 ±      2.087  us/op
 * FOVBenchmark.doAdSymmetrical20    avgt    4     298.270 ±     92.780  us/op
 * FOVBenchmark.doAdSymmetrical30    avgt    4     647.953 ±     73.989  us/op
 * FOVBenchmark.doAdSymmetrical5     avgt    4      36.867 ±      3.495  us/op
 * FOVBenchmark.doAdSymmetricalMax   avgt    4  488830.869 ± 214074.002  us/op
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
        public DistanceBoard board;
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
            board = new DistanceBoard(blocking);
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




    @Benchmark
    public void doAdSymmetrical5(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOVSymmetrical(state.resF, state.lightF, point.x, point.y, 5f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdSymmetrical10(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOVSymmetrical(state.resF, state.lightF, point.x, point.y, 10f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdSymmetrical20(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOVSymmetrical(state.resF, state.lightF, point.x, point.y, 20f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdSymmetrical30(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOVSymmetrical(state.resF, state.lightF, point.x, point.y, 30f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdSymmetricalMax(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        com.github.yellowstonegames.grid.FOV.reuseFOVSymmetrical(state.resF, state.lightF, point.x, point.y, state.DIMENSION << 1, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }




    @Benchmark
    public void doAdOrtho5(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVOrtho(state.resF, state.lightF, point.x, point.y, 5f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdOrtho10(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVOrtho(state.resF, state.lightF, point.x, point.y, 10f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdOrtho20(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVOrtho(state.resF, state.lightF, point.x, point.y, 20f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdOrtho30(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVOrtho(state.resF, state.lightF, point.x, point.y, 30f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAdOrthoMax(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVOrtho(state.resF, state.lightF, point.x, point.y, state.DIMENSION << 1, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }


    @Benchmark
    public void doAd2Symmetrical5(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVSymmetrical2(state.resF, state.lightF, point.x, point.y, 5f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAd2Symmetrical10(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVSymmetrical2(state.resF, state.lightF, point.x, point.y, 10f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAd2Symmetrical20(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVSymmetrical2(state.resF, state.lightF, point.x, point.y, 20f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAd2Symmetrical30(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVSymmetrical2(state.resF, state.lightF, point.x, point.y, 30f, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
    }

    @Benchmark
    public void doAd2SymmetricalMax(BenchmarkState state, Blackhole blackhole)
    {
        Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
        FOV2.reuseFOVSymmetrical2(state.resF, state.lightF, point.x, point.y, state.DIMENSION << 1, com.github.yellowstonegames.grid.Radius.CIRCLE);
        blackhole.consume(state.lightF);
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
        BenchmarkState state = new BenchmarkState();
        state.setup();
        for (int i = 0; i < 10; i++) {
            Coord point = state.floorArray[state.idx = (state.idx + 1) % state.floorCount];
            state.board.resetVisited();
            state.pp.visitFieldOfView(state.board, point.x, point.y, 5);
        }

//        Options opt = new OptionsBuilder()
//                .include(FOVBenchmark.class.getSimpleName())
//                .warmupIterations(3)
//                .measurementIterations(3)
//                .forks(1)
//                .shouldDoGC(true)
//                .build();
//        new Runner(opt).run();
    }
}
