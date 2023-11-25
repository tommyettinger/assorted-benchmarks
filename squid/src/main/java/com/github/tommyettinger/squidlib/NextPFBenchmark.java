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

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.TimeUtils;
import graph.earlygrey.simplegraphs.NextDirectedGraph;
import graph.earlygrey.simplegraphs.NextPath;
import graph.earlygrey.simplegraphs.NextUndirectedGraph;
import graph.earlygrey.simplegraphs.utils.NextHeuristic;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import space.earlygrey.simplegraphs.DirectedGraph;
import space.earlygrey.simplegraphs.Path;
import space.earlygrey.simplegraphs.UndirectedGraph;
import space.earlygrey.simplegraphs.algorithms.DirectedGraphAlgorithms;
import space.earlygrey.simplegraphs.algorithms.SearchStep;
import space.earlygrey.simplegraphs.algorithms.UndirectedGraphAlgorithms;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 4)
@Measurement(iterations = 3)
public class NextPFBenchmark {
    private static final GridPoint2 start = new GridPoint2(), end = new GridPoint2();

    @State(Scope.Thread)
    public static class BenchmarkState {
        public static final int WIDTH = 666;
        public static final int HEIGHT = 666;
        //        public static final GridPoint2[][] gridPool = new GridPoint2[WIDTH][HEIGHT];
        public DungeonGenerator dungeonGen = new DungeonGenerator(WIDTH, HEIGHT, new StatefulRNG(0x1337BEEFDEAL));
        public char[][] map;
        public double[][] astarMap;
        public float[][] squadAstarMap;
        public GreasedRegion floors;
        public ArrayList<GridPoint2> gpFloors;
        public int floorCount;
        public Coord[] floorArray;
        public GreasedRegion tmp;
        public GridPoint2 lowestGP, highestGP;
        public GridPoint2[][] gpNearbyMap;
        public StatefulRNG srng;
        public Path<GridPoint2> sggpPath;
        public DirectedGraph<GridPoint2> sggpDirectedGraph;
        public UndirectedGraph<GridPoint2> sggpUndirectedGraph;
        public space.earlygrey.simplegraphs.utils.Heuristic<GridPoint2> sggpHeu;

        public NextPath<GridPoint2> nsgPath;
        public NextDirectedGraph<GridPoint2> nsgDirectedGraph;
        public NextUndirectedGraph<GridPoint2> nsgUndirectedGraph;
        public NextHeuristic<GridPoint2> nsgHeu;

        public Coord rejectionSample(int x, int y) {
            Coord c;
            do {
                c = Coord.get(srng.nextInt(17) - 8 + x, srng.nextInt(17) - 8 + y);
            } while ((x == c.x && y == c.y) || c.x <= 0 || c.y <= 0 || c.x >= WIDTH - 1 || c.y >= HEIGHT - 1 || map[c.x][c.y] == '#');
            return c;
        }

        public Coord floodSample(int x, int y) {
            return tmp.empty().insert(x, y).flood(floors, 8).remove(x, y).singleRandom(srng);
        }

        /*
        OK, how fast is this now...
Generated map at 196279000
Coord pools filled at 242161600
Region stuff done at 310191200
Floors: 269830
Percentage walkable: 60.83335587840092%
AStar done at 324709600
Tiny paths made at 121244385400
Edges added at 248746725400
Nate sweetened at 248748408300

         Optimized!
Starting at 158700
Generated map at 191956600
Coord pools filled at 236418300
Region stuff done at 306619200
Floors: 269830
Percentage walkable: 60.83335587840092%
AStar done at 321422100
Tiny paths made at 1710092700
Dijkstra maps made at 1828516800
AStar finders made at 30106468700
Paths made at 30108881500
Simple finders made at 58843922800
Squid finders made at 89689895500
Squad finders made at 120669415500
Edges added at 129571434600
Nate sweetened at 129572932000

248748408300 (old)
129572932000 (new)
         */
        @Setup(Level.Trial)
        public void setup() {
            long startTime = System.nanoTime();
            System.out.println("Starting at " + TimeUtils.timeSinceNanos(startTime));
            map = dungeonGen.generate();
            System.out.println();
            DungeonUtility.debugPrint(map);
            System.out.println();
            System.out.println("Generated map at " + TimeUtils.timeSinceNanos(startTime));
            System.out.println();
//            ArrayTools.reverse(map);
            Coord.expandPoolTo(WIDTH, HEIGHT);
            com.github.yellowstonegames.grid.Coord.expandPoolTo(WIDTH, HEIGHT);
            System.out.println("Coord pools filled at " + TimeUtils.timeSinceNanos(startTime));
//            for (int x = 0; x < WIDTH; x++) {
//                for (int y = 0; y < HEIGHT; y++) {
//                    gridPool[x][y] = new GridPoint2(x, y);
//                }
//            }
            floors = new GreasedRegion(map, '.');
            floorCount = floors.size();
            floorArray = floors.asCoords();

            gpFloors = new ArrayList<>(floorCount);
            for (int i = 0; i < floorCount; i++) {
                gpFloors.add(new GridPoint2(floorArray[i].x, floorArray[i].y));
            }

            System.out.println("Region stuff done at " + TimeUtils.timeSinceNanos(startTime));

            Coord lowest = floors.first();
            Coord highest = floors.last();
            lowestGP = new GridPoint2(lowest.x, lowest.y);
            highestGP = new GridPoint2(highest.x, highest.y);
            System.out.println("Floors: " + floorCount);
            System.out.println("Percentage walkable: " + floorCount * 100.0 / (WIDTH * HEIGHT) + "%");
            gpNearbyMap = new GridPoint2[WIDTH][HEIGHT];
            tmp = new GreasedRegion(WIDTH, HEIGHT);
            srng = new StatefulRNG(0x1337BEEF1337CA77L);
            Coord c;
            for (int i = 1; i < WIDTH - 1; i++) {
                for (int j = 1; j < HEIGHT - 1; j++) {
                    if (map[i][j] == '#')
                        continue;
                    c = rejectionSample(i, j);
//                    c = floodSample(i, j);
                    gpNearbyMap[i][j] = new GridPoint2(c.x, c.y);
                }
            }
            System.out.println("Tiny paths made at " + TimeUtils.timeSinceNanos(startTime));

            sggpPath = new Path<>(WIDTH + HEIGHT << 1);
            sggpDirectedGraph = new DirectedGraph<>(gpFloors);
            sggpUndirectedGraph = new UndirectedGraph<>(gpFloors);
            sggpHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));
            nsgPath = new NextPath<>(WIDTH + HEIGHT << 1);
            nsgDirectedGraph = new NextDirectedGraph<>(gpFloors);
            nsgUndirectedGraph = new NextUndirectedGraph<>(gpFloors);
            nsgHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));
            System.out.println("Simple finders made at " + TimeUtils.timeSinceNanos(startTime));

            Coord center;
            GridPoint2 gpCenter;
            Direction[] outer = Direction.CLOCKWISE;
            Direction dir;
            for (int i = floorCount - 1; i >= 0; i--) {
                center = floorArray[i];
                gpCenter = gpFloors.get(i);
                for (int j = 0; j < 8; j++) {
                    dir = outer[j];
                    if (floors.contains(center.x + dir.deltaX, center.y + dir.deltaY)) {
                        GridPoint2 gpMoved = new GridPoint2(gpCenter).add(dir.deltaX, dir.deltaY);
                        sggpDirectedGraph.addEdge(gpCenter, gpMoved);
                        nsgDirectedGraph.addEdge(gpCenter, gpMoved);
                        if (!sggpUndirectedGraph.edgeExists(gpCenter, gpMoved)) {
                            sggpUndirectedGraph.addEdge(gpCenter, gpMoved);
                            nsgUndirectedGraph.addEdge(gpCenter, gpMoved);
                        }
                    }
                }
            }
            System.out.println("Edges added at " + TimeUtils.timeSinceNanos(startTime));
        }

    }

    @Benchmark
    public long doPathSimpleGPD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleGPD(BenchmarkState state) {
        long scanned = 0;
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSimpleGPD(BenchmarkState state) {
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        state.sggpPath.clear();
        state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
        return state.sggpPath.size();
    }

    @Benchmark
    public long doPathSimpleGPUD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final UndirectedGraphAlgorithms<GridPoint2> algo = state.sggpUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleGPUD(BenchmarkState state) {
        long scanned = 0;
        final UndirectedGraphAlgorithms<GridPoint2> algo = state.sggpUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSimpleGPUD(BenchmarkState state) {
        final UndirectedGraphAlgorithms<GridPoint2> algo = state.sggpUndirectedGraph.algorithms();
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        state.sggpPath.clear();
        state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
        return state.sggpPath.size();
    }





    @Benchmark
    public long doPathNextSimpleGPD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathNextSimpleGPD(BenchmarkState state) {
        long scanned = 0;
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneNextSimpleGPD(BenchmarkState state) {
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        state.sggpPath.clear();
        state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
        return state.sggpPath.size();
    }

    @Benchmark
    public long doPathNextSimpleGPUD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final UndirectedGraphAlgorithms<GridPoint2> algo = state.sggpUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathNextSimpleGPUD(BenchmarkState state) {
        long scanned = 0;
        final UndirectedGraphAlgorithms<GridPoint2> algo = state.sggpUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.sggpPath.clear();
                end.y = y;
                state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
                if (state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneNextSimpleGPUD(BenchmarkState state) {
        final UndirectedGraphAlgorithms<GridPoint2> algo = state.sggpUndirectedGraph.algorithms();
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        state.sggpPath.clear();
        state.sggpPath.addAll(algo.findShortestPath(start, end, state.sggpHeu, SearchStep::vertex));
        return state.sggpPath.size();
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
     *    $ java -jar target/benchmarks.jar PathfindingBenchmark -wi 3 -i 3 -f 1 -gc true -w 25 -r 25
     *
     *    (we requested 3 warmup/measurement iterations, single fork, garbage collect between benchmarks)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NextPFBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(3)
                .forks(1)
                .shouldDoGC(true)
                .build();
        new Runner(opt).run();
    }
}
