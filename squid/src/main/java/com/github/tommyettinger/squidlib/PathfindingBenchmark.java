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

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.gand.GradientGridI2;
import com.github.tommyettinger.gdcrux.PointF2;
import com.github.tommyettinger.gdcrux.PointI2;
import com.github.tommyettinger.gand.utils.GridMetric;
import com.github.yellowstonegames.grid.Region;
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
import squid.lib.DijkstraMap;
import squidpony.ArrayTools;
import squidpony.squidai.CustomDijkstraMap;
import squidpony.squidgrid.Adjacency;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.AStarSearch;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static squidpony.squidgrid.Measurement.CHEBYSHEV;

/**
 * Benchmarks were run With Java 8, HotSpot, on an 8th-gen i7 hexacore mobile processor running Manjaro Linux.
 * <br>
 * 64x64:
 * <pre>
 * Benchmark                                      Mode  Cnt    Score   Error  Units
 * PathfindingBenchmark.doPathAStarSearch         avgt    5   84.281 ± 0.411  ms/op
 * PathfindingBenchmark.doPathCustomDijkstra      avgt    5  412.251 ± 0.656  ms/op
 * PathfindingBenchmark.doPathDijkstra            avgt    5  258.391 ± 0.421  ms/op
 * PathfindingBenchmark.doPathGDXAStar            avgt    5  187.551 ± 3.784  ms/op
 * PathfindingBenchmark.doPathSimpleD             avgt    5   80.645 ± 3.796  ms/op
 * PathfindingBenchmark.doPathSimpleUD            avgt    5   82.061 ± 1.842  ms/op
 * PathfindingBenchmark.doPathSquidCG             avgt    5   84.827 ± 0.112  ms/op
 * PathfindingBenchmark.doPathSquidD              avgt    5   91.574 ± 0.197  ms/op
 * PathfindingBenchmark.doPathSquidDG             avgt    5   85.401 ± 0.494  ms/op
 * PathfindingBenchmark.doPathSquidUD             avgt    5   89.837 ± 0.137  ms/op
 * PathfindingBenchmark.doScanCustomDijkstra      avgt    5  799.660 ± 3.717  ms/op
 * PathfindingBenchmark.doScanDijkstra            avgt    5  497.609 ± 1.237  ms/op
 * PathfindingBenchmark.doTinyPathAStarSearch     avgt    5    2.776 ± 0.006  ms/op
 * PathfindingBenchmark.doTinyPathCustomDijkstra  avgt    5   23.381 ± 0.228  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra        avgt    5    9.916 ± 0.379  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar        avgt    5    5.586 ± 0.049  ms/op
 * PathfindingBenchmark.doTinyPathSimpleD         avgt    5    2.545 ± 0.004  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD        avgt    5    2.598 ± 0.017  ms/op
 * PathfindingBenchmark.doTinyPathSquidCG         avgt    5    2.581 ± 0.008  ms/op
 * PathfindingBenchmark.doTinyPathSquidD          avgt    5    2.741 ± 0.005  ms/op
 * PathfindingBenchmark.doTinyPathSquidDG         avgt    5    2.590 ± 0.004  ms/op
 * PathfindingBenchmark.doTinyPathSquidUD         avgt    5    2.841 ± 0.002  ms/op
 * </pre>
 * <br>
 * 128x128:
 * <pre>
 * Benchmark                                      Mode  Cnt      Score    Error  Units
 * PathfindingBenchmark.doPathAStarSearch         avgt    5   1399.509 ± 20.982  ms/op
 * PathfindingBenchmark.doPathCustomDijkstra      avgt    5   7488.397 ±  1.530  ms/op
 * PathfindingBenchmark.doPathDijkstra            avgt    5   4491.251 ± 10.695  ms/op
 * PathfindingBenchmark.doPathGDXAStar            avgt    5   3277.896 ± 74.574  ms/op
 * PathfindingBenchmark.doPathSimpleD             avgt    5   1329.156 ±  2.543  ms/op
 * PathfindingBenchmark.doPathSimpleUD            avgt    5   1320.179 ±  3.360  ms/op
 * PathfindingBenchmark.doPathSquidCG             avgt    5   1516.920 ±  4.625  ms/op
 * PathfindingBenchmark.doPathSquidD              avgt    5   1925.184 ± 10.930  ms/op
 * PathfindingBenchmark.doPathSquidDG             avgt    5   1458.154 ±  3.250  ms/op
 * PathfindingBenchmark.doPathSquidUD             avgt    5   1718.223 ±  4.270  ms/op
 * PathfindingBenchmark.doScanCustomDijkstra      avgt    5  13699.966 ± 13.198  ms/op
 * PathfindingBenchmark.doScanDijkstra            avgt    5   8339.163 ± 12.729  ms/op
 * PathfindingBenchmark.doTinyPathAStarSearch     avgt    5     11.655 ±  0.194  ms/op
 * PathfindingBenchmark.doTinyPathCustomDijkstra  avgt    5    522.405 ±  5.606  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra        avgt    5    104.484 ±  7.574  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar        avgt    5     25.837 ±  0.422  ms/op
 * PathfindingBenchmark.doTinyPathSimpleD         avgt    5     10.988 ±  0.322  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD        avgt    5     11.510 ±  0.253  ms/op
 * PathfindingBenchmark.doTinyPathSquidCG         avgt    5     11.649 ±  0.249  ms/op
 * PathfindingBenchmark.doTinyPathSquidD          avgt    5     13.118 ±  0.024  ms/op
 * PathfindingBenchmark.doTinyPathSquidDG         avgt    5     11.201 ±  0.043  ms/op
 * PathfindingBenchmark.doTinyPathSquidUD         avgt    5     13.633 ±  0.126  ms/op
 * </pre>
 * <br>
 * 256x256:
 * <pre>
 * Benchmark                                      Mode  Cnt       Score       Error  Units
 * PathfindingBenchmark.doPathAStarSearch         avgt    4   27789.960 ±    55.497  ms/op
 * PathfindingBenchmark.doPathCustomDijkstra      avgt    4  128859.344 ± 18245.219  ms/op
 * PathfindingBenchmark.doPathDijkstra            avgt    4   75654.535 ±  4743.716  ms/op
 * PathfindingBenchmark.doPathGDXAStar            avgt    4   56627.572 ±    84.007  ms/op
 * PathfindingBenchmark.doPathSimpleD             avgt    4   25402.243 ±   104.579  ms/op
 * PathfindingBenchmark.doPathSimpleUD            avgt    4   24576.159 ±    42.049  ms/op
 * PathfindingBenchmark.doPathSquidCG             avgt    4   27954.778 ±    17.596  ms/op
 * PathfindingBenchmark.doPathSquidD              avgt    4   38832.866 ±   105.486  ms/op
 * PathfindingBenchmark.doPathSquidDG             avgt    4   26647.978 ±    34.000  ms/op
 * PathfindingBenchmark.doPathSquidUD             avgt    4   37546.416 ±   118.943  ms/op
 * PathfindingBenchmark.doScanCustomDijkstra      avgt    4  252647.413 ±   364.018  ms/op
 * PathfindingBenchmark.doScanDijkstra            avgt    4  146685.721 ±   213.054  ms/op
 * PathfindingBenchmark.doTinyPathAStarSearch     avgt    4      52.356 ±     0.180  ms/op
 * PathfindingBenchmark.doTinyPathCustomDijkstra  avgt    4   10545.529 ±  1139.687  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra        avgt    4    1748.233 ±   142.468  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar        avgt    4     123.132 ±     0.118  ms/op
 * PathfindingBenchmark.doTinyPathSimpleD         avgt    4      50.458 ±     0.198  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD        avgt    4      54.254 ±     0.159  ms/op
 * PathfindingBenchmark.doTinyPathSquidCG         avgt    4      54.680 ±     0.292  ms/op
 * PathfindingBenchmark.doTinyPathSquidD          avgt    4      58.929 ±     0.104  ms/op
 * PathfindingBenchmark.doTinyPathSquidDG         avgt    4      54.120 ±     0.222  ms/op
 * PathfindingBenchmark.doTinyPathSquidUD         avgt    4      56.321 ±     1.657  ms/op
 * </pre>
 * <br>
 * And just a quick comparison on newer hardware of simple-graphs 3.0.0 with gdx-ai 1.8.2, with a 64x64 map:
 * <pre>
 * Benchmark                                Mode  Cnt    Score   Error  Units
 * PathfindingBenchmark.doPathGDXAStar      avgt    4  180.276 ± 6.968  ms/op
 * PathfindingBenchmark.doPathSimpleD       avgt    4   93.790 ± 1.938  ms/op
 * PathfindingBenchmark.doPathSimpleUD      avgt    4   91.334 ± 2.428  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar  avgt    4    5.043 ± 0.113  ms/op
 * PathfindingBenchmark.doTinyPathSimpleD   avgt    4    2.728 ± 0.354  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD  avgt    4    2.863 ± 0.527  ms/op
 * </pre>
 * <br>
 * Quick comparison of single-path times from random point to random point on a 200x200 map:
 * <pre>
 * Benchmark                           Mode  Cnt  Score   Error  Units
 * PathfindingBenchmark.doOneDijkstra  avgt    3  1.166 ± 0.059  ms/op
 * PathfindingBenchmark.doOneGDXAStar  avgt    3  0.921 ± 2.716  ms/op
 * PathfindingBenchmark.doOneSquidUD   avgt    3  0.418 ± 0.077  ms/op
 * </pre>
 * <br>
 * On a 100x100 map, Simple-Graphs is close to twice as fast as gdx-ai 1.8.2.
 * <pre>
 * Benchmark                                 Mode  Cnt     Score    Error  Units
 * PathfindingBenchmark.doPathGDXAStar       avgt    5  1083.838 � 28.082  ms/op
 * PathfindingBenchmark.doPathGDXAStar2      avgt    5   928.181 � 21.481  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    5   504.841 �  5.979  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar   avgt    5    12.620 �  0.296  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar2  avgt    5    10.910 �  0.225  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD   avgt    5     6.914 �  0.221  ms/op
 * </pre>
 * <br>
 * With gdx-ai 1.8.3-SNAPSHOT and its StopCondition, there's no measurable difference.
 * All differences are inside the margin of error between the gdx-ai tests here and above.
 * <pre>
 * Benchmark                                 Mode  Cnt     Score    Error  Units
 * PathfindingBenchmark.doPathGDXAStar       avgt    5  1082.626 � 21.162  ms/op
 * PathfindingBenchmark.doPathGDXAStar2      avgt    5   937.659 � 19.265  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    5   494.665 � 22.942  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar   avgt    5    12.829 �  0.294  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar2  avgt    5    10.865 �  0.160  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD   avgt    5     6.397 �  0.072  ms/op
 * </pre>
 * <br>
 * Testing on a 240x400 BSP dungeon map provided by FudgeFiddle for comparison purposes, with one path from the lowest
 * x and lowest y to approximately the highest x and highest y:
 * <pre>
 * Benchmark                            Mode  Cnt  Score   Error  Units
 * PathfindingBenchmark.doOneDijkstra   avgt    5  4.501 ± 0.052  ms/op
 * PathfindingBenchmark.doOneGDXAStar   avgt    5  7.574 ± 0.054  ms/op
 * PathfindingBenchmark.doOneGDXAStar2  avgt    5  5.625 ± 0.089  ms/op
 * PathfindingBenchmark.doOneSquidUD    avgt    5  4.364 ± 0.075  ms/op
 * </pre>
 * I have no idea why DijkstraMap performs so well here, though SquidLib and simple-graphs' UndirectedGraph is faster
 * by a small margin.
 * <br>
 * On a different path through the same BSP dungeon map:
 * <pre>
 * Benchmark                            Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doOneDijkstra   avgt    5   2.744 ± 0.120  ms/op
 * PathfindingBenchmark.doOneGDXAStar   avgt    5  16.792 ± 0.864  ms/op
 * PathfindingBenchmark.doOneGDXAStar2  avgt    5  10.715 ± 0.164  ms/op
 * PathfindingBenchmark.doOneSquidUD    avgt    5  12.694 ± 0.133  ms/op
 * </pre>
 * Tiny paths through the same BSP dungeon map:
 * <pre>
 * Benchmark                                      Mode  Cnt      Score     Error  Units
 * PathfindingBenchmark.doTinyPathAStarSearch     avgt    5    100.884 ±   3.059  ms/op
 * PathfindingBenchmark.doTinyPathCustomDijkstra  avgt    5  13948.186 ± 101.136  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra        avgt    5   3083.407 ± 119.990  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar        avgt    5    162.983 ±   3.056  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStar2       avgt    5    111.234 ±   1.315  ms/op
 * PathfindingBenchmark.doTinyPathSimpleD         avgt    5     73.213 ±   0.568  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD        avgt    5     74.787 ±   0.981  ms/op
 * PathfindingBenchmark.doTinyPathSquidCG         avgt    5     85.488 ±  12.577  ms/op
 * PathfindingBenchmark.doTinyPathSquidD          avgt    5     89.521 ±   1.044  ms/op
 * PathfindingBenchmark.doTinyPathSquidDG         avgt    5     73.333 ±   0.860  ms/op
 * PathfindingBenchmark.doTinyPathSquidUD         avgt    5     90.911 ±   1.072  ms/op
 * </pre>
 * Here, it's much more what I would expect, with DijkstraMap taking much more time to find 51716 paths than any of the
 * others except CustomDijkstra. Still, gdx-ai never beats simple-graphs or its derivatives in SquidLib.
 * <br>
 * A quick test of SquidLib vs. SquidSquad; this time, Squad is faster. The BitDijkstra optimization for Squad doesn't
 * sem to change much.
 * <pre>
 * Benchmark                                     Mode  Cnt     Score     Error  Units
 * PathfindingBenchmark.doOneBitDijkstra         avgt    3     0.483 ±   0.085  ms/op
 * PathfindingBenchmark.doOneDijkstra            avgt    3     0.573 ±   0.053  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra       avgt    3     0.487 ±   0.029  ms/op
 * PathfindingBenchmark.doPathBitDijkstra        avgt    3  1448.529 ± 888.940  ms/op
 * PathfindingBenchmark.doPathDijkstra           avgt    3  1620.672 ± 137.465  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra      avgt    3  1475.510 ± 237.581  ms/op
 * PathfindingBenchmark.doTinyPathBitDijkstra    avgt    3    36.850 ±   5.918  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra       avgt    3    46.594 ±   0.725  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra  avgt    3    37.817 ±   2.320  ms/op
 * </pre>
 * With CoordSet from SquidSquad used in CDijkstra, this seems to be fastest:
 * <pre>
 * Benchmark                                     Mode  Cnt     Score    Error  Units
 * PathfindingBenchmark.doOneBitDijkstra         avgt    5     0.478 ±  0.013  ms/op
 * PathfindingBenchmark.doOneCDijkstra           avgt    5     0.494 ±  0.029  ms/op
 * PathfindingBenchmark.doOneDijkstra            avgt    5     0.606 ±  0.032  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra       avgt    5     0.495 ±  0.021  ms/op
 * PathfindingBenchmark.doPathBitDijkstra        avgt    5  1467.971 ± 37.951  ms/op
 * PathfindingBenchmark.doPathCDijkstra          avgt    5  1454.166 ± 57.630  ms/op
 * PathfindingBenchmark.doPathDijkstra           avgt    5  1641.669 ± 36.791  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra      avgt    5  1462.148 ± 33.725  ms/op
 * PathfindingBenchmark.doTinyPathBitDijkstra    avgt    5    38.642 ±  2.567  ms/op
 * PathfindingBenchmark.doTinyPathCDijkstra      avgt    5    35.342 ±  2.372  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra       avgt    5    48.323 ±  2.193  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra  avgt    5    36.847 ±  1.245  ms/op
 * </pre>
 * Testing a mix of GridPoint2 and Coord:
 * <pre>
 * Benchmark                                      Mode  Cnt        Score        Error  Units
 * PathfindingBenchmark.doPathAStarSearch         avgt    5   694973.053 ±  76781.573  us/op
 * PathfindingBenchmark.doPathBitDijkstra         avgt    5  1347203.565 ±  30430.976  us/op
 * PathfindingBenchmark.doPathCDijkstra           avgt    5  1372245.507 ±  43683.971  us/op
 * PathfindingBenchmark.doPathCustomDijkstra      avgt    5  2541415.135 ±  42205.445  us/op
 * PathfindingBenchmark.doPathDijkstra            avgt    5  1561208.149 ±  54350.308  us/op
 * PathfindingBenchmark.doPathGDXAStarCoord       avgt    5  1505324.541 ± 401152.476  us/op
 * PathfindingBenchmark.doPathGDXAStarGP          avgt    5    42891.605 ±    231.301  us/op
 * PathfindingBenchmark.doPathSimpleD             avgt    5   630883.974 ±  28020.950  us/op
 * PathfindingBenchmark.doPathSimpleGPD           avgt    5   629837.469 ±  37200.920  us/op
 * PathfindingBenchmark.doPathSimpleGPUD          avgt    5   619220.421 ±   6467.766  us/op
 * PathfindingBenchmark.doPathSimpleUD            avgt    5   649818.634 ±  12379.327  us/op
 * PathfindingBenchmark.doPathSquadDijkstra       avgt    5  1382012.700 ±  31907.690  us/op
 * PathfindingBenchmark.doPathSquidCG             avgt    5   608523.373 ±  22797.481  us/op
 * PathfindingBenchmark.doPathSquidD              avgt    5   716529.512 ±  19173.656  us/op
 * PathfindingBenchmark.doPathSquidDG             avgt    5   598800.799 ±  11845.345  us/op
 * PathfindingBenchmark.doPathSquidUD             avgt    5   681378.256 ±  17573.780  us/op
 * PathfindingBenchmark.doTinyPathAStarSearch     avgt    5    11020.876 ±    526.867  us/op
 * PathfindingBenchmark.doTinyPathBitDijkstra     avgt    5    38062.678 ±   1929.482  us/op
 * PathfindingBenchmark.doTinyPathCDijkstra       avgt    5    36248.857 ±   1448.693  us/op
 * PathfindingBenchmark.doTinyPathCustomDijkstra  avgt    5   201641.240 ±   3923.762  us/op
 * PathfindingBenchmark.doTinyPathDijkstra        avgt    5    47662.265 ±   1852.761  us/op
 * PathfindingBenchmark.doTinyPathGDXAStarCoord   avgt    5    18515.330 ±    348.738  us/op
 * PathfindingBenchmark.doTinyPathGDXAStarGP      avgt    5    31057.291 ±    127.932  us/op
 * PathfindingBenchmark.doTinyPathSimpleD         avgt    5     9473.978 ±    343.797  us/op
 * PathfindingBenchmark.doTinyPathSimpleGPD       avgt    5     9631.573 ±    396.015  us/op
 * PathfindingBenchmark.doTinyPathSimpleGPUD      avgt    5     9674.871 ±    154.607  us/op
 * PathfindingBenchmark.doTinyPathSimpleUD        avgt    5     9670.675 ±    181.448  us/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra   avgt    5    37475.435 ±   5866.468  us/op
 * PathfindingBenchmark.doTinyPathSquidCG         avgt    5     7795.309 ±    208.715  us/op
 * PathfindingBenchmark.doTinyPathSquidD          avgt    5     9624.182 ±    486.599  us/op
 * PathfindingBenchmark.doTinyPathSquidDG         avgt    5     8056.931 ±    267.515  us/op
 * PathfindingBenchmark.doTinyPathSquidUD         avgt    5     9435.427 ±    747.011  us/op
 * </pre>
 * <br>
 * On Graal 19, there's pretty much no difference between SquidLib and SquidSquad pathfinding.
 * There are some odd outliers occasionally, but these may be related to how hot the laptop was getting...
 * <pre>
 * Benchmark                                     Mode  Cnt     Score     Error  Units
 * PathfindingBenchmark.doOneSquadDijkstra       avgt    5     0.465 ±   0.016  ms/op
 * PathfindingBenchmark.doOneSquadUD             avgt    5     0.538 ±   0.037  ms/op
 * PathfindingBenchmark.doOneSquidUD             avgt    5     0.532 ±   0.044  ms/op
 * PathfindingBenchmark.doPathSquadCG            avgt    5   585.296 ±  36.236  ms/op
 * PathfindingBenchmark.doPathSquadD             avgt    5   826.279 ± 271.676  ms/op
 * PathfindingBenchmark.doPathSquadDG            avgt    5   683.611 ± 112.950  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra      avgt    5  1337.169 ±  67.748  ms/op
 * PathfindingBenchmark.doPathSquadUD            avgt    5   656.321 ±  15.116  ms/op
 * PathfindingBenchmark.doPathSquidCG            avgt    5   583.824 ±  25.852  ms/op
 * PathfindingBenchmark.doPathSquidD             avgt    5   705.798 ±  30.227  ms/op
 * PathfindingBenchmark.doPathSquidDG            avgt    5   660.100 ± 506.954  ms/op
 * PathfindingBenchmark.doPathSquidUD            avgt    5   655.595 ±  80.861  ms/op
 * PathfindingBenchmark.doTinyPathSquadCG        avgt    5     7.259 ±   0.287  ms/op
 * PathfindingBenchmark.doTinyPathSquadD         avgt    5    10.105 ±   4.701  ms/op
 * PathfindingBenchmark.doTinyPathSquadDG        avgt    5     7.529 ±   0.809  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra  avgt    5    35.507 ±   2.236  ms/op
 * PathfindingBenchmark.doTinyPathSquadUD        avgt    5     8.514 ±   0.392  ms/op
 * PathfindingBenchmark.doTinyPathSquidCG        avgt    5     7.776 ±   0.496  ms/op
 * PathfindingBenchmark.doTinyPathSquidD         avgt    5     9.705 ±   0.307  ms/op
 * PathfindingBenchmark.doTinyPathSquidDG        avgt    5     8.092 ±   0.592  ms/op
 * PathfindingBenchmark.doTinyPathSquidUD        avgt    5     9.611 ±   0.391  ms/op
 * </pre>
 * <br>
 * Testing on HotSpot Java 19, with all the varieties of the broadest test, doPath:
 * <pre>
 * Benchmark                                  Mode  Cnt     Score    Error  Units
 * PathfindingBenchmark.doPathAStarSearch     avgt    5   652.217 ± 22.756  ms/op
 * PathfindingBenchmark.doPathBitDijkstra     avgt    5  1344.788 ± 32.642  ms/op
 * PathfindingBenchmark.doPathCDijkstra       avgt    5  1347.899 ± 47.326  ms/op
 * PathfindingBenchmark.doPathCustomDijkstra  avgt    5  2541.292 ± 49.725  ms/op
 * PathfindingBenchmark.doPathDijkstra        avgt    5  1517.510 ± 63.571  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord   avgt    5  1376.808 ± 37.770  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP      avgt    5   531.745 ±  7.271  ms/op
 * PathfindingBenchmark.doPathNate            avgt    5  2456.673 ± 85.702  ms/op
 * PathfindingBenchmark.doPathSimpleD         avgt    5   614.864 ± 43.617  ms/op
 * PathfindingBenchmark.doPathSimpleGPD       avgt    5   622.291 ±  7.097  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD      avgt    5   618.404 ± 39.350  ms/op
 * PathfindingBenchmark.doPathSimpleUD        avgt    5   603.095 ±  5.638  ms/op
 * PathfindingBenchmark.doPathSquadCG         avgt    5   608.257 ±  8.455  ms/op
 * PathfindingBenchmark.doPathSquadD          avgt    5   738.030 ± 15.758  ms/op
 * PathfindingBenchmark.doPathSquadDG         avgt    5   604.759 ±  9.936  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra   avgt    5  1341.546 ± 25.508  ms/op
 * PathfindingBenchmark.doPathSquadUD         avgt    5   659.084 ± 12.445  ms/op
 * PathfindingBenchmark.doPathSquidCG         avgt    5   625.508 ±  5.220  ms/op
 * PathfindingBenchmark.doPathSquidD          avgt    5   748.627 ± 12.934  ms/op
 * PathfindingBenchmark.doPathSquidDG         avgt    5   619.013 ± 11.420  ms/op
 * PathfindingBenchmark.doPathSquidUD         avgt    5   690.944 ±  6.758  ms/op
 * </pre>
 * The slowest two here are CustomDijkstra, which was never especially fast because it does so much extra work,
 * and Nate (using the new/old NateStar class here from a 2014 Gist by Nathan Sweet). I haven't really picked apart
 * NateStar to see why it's so slow, but it takes about 4x as much time as Simple-Graphs or SquidSquad. The fastest one
 * here is GDXAStarGP, interestingly, even after it was fixed so that it finds paths as often as anything else does. I
 * think this is because the gdx-ai implementation using GridPoint2 doesn't use any hashed lookups, only arrays. The
 * gdx-ai implementation using Coord does use hashed lookups, and is much slower.
 * <br>
 * I don't know what happened. GDXAStarCoord is almost the same code as GDXAStarGP, but it's much slower...
 * <pre>
 * Benchmark                                  Mode  Cnt     Score     Error  Units
 * PathfindingBenchmark.doPathAStarSearch     avgt    5   643.035 ±  43.603  ms/op
 * PathfindingBenchmark.doPathBitDijkstra     avgt    5  1338.887 ±  37.311  ms/op
 * PathfindingBenchmark.doPathCDijkstra       avgt    5  1351.684 ±  34.891  ms/op
 * PathfindingBenchmark.doPathCustomDijkstra  avgt    5  2529.270 ±  90.358  ms/op
 * PathfindingBenchmark.doPathDijkstra        avgt    5  1520.068 ±  47.062  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord   avgt    5  1131.681 ±  15.601  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP      avgt    5   550.697 ±  11.223  ms/op
 * PathfindingBenchmark.doPathNate            avgt    5  2223.303 ±  60.252  ms/op
 * PathfindingBenchmark.doPathSimpleD         avgt    5   639.887 ±  81.174  ms/op
 * PathfindingBenchmark.doPathSimpleGPD       avgt    5   614.273 ±   7.684  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD      avgt    5   609.471 ±  10.010  ms/op
 * PathfindingBenchmark.doPathSimpleUD        avgt    5   625.789 ±   8.055  ms/op
 * PathfindingBenchmark.doPathSquadCG         avgt    5   577.080 ±   5.190  ms/op
 * PathfindingBenchmark.doPathSquadD          avgt    5   727.053 ±  15.023  ms/op
 * PathfindingBenchmark.doPathSquadDG         avgt    5   603.581 ± 113.664  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra   avgt    5  1363.334 ±  20.328  ms/op
 * PathfindingBenchmark.doPathSquadUD         avgt    5   698.364 ±   7.639  ms/op
 * PathfindingBenchmark.doPathSquidCG         avgt    5   627.614 ±   6.351  ms/op
 * PathfindingBenchmark.doPathSquidD          avgt    5   752.059 ±   7.853  ms/op
 * PathfindingBenchmark.doPathSquidDG         avgt    5   595.272 ±  32.003  ms/op
 * PathfindingBenchmark.doPathSquidUD         avgt    5   698.687 ±  14.065  ms/op
 * </pre>
 * <br>
 * THAT'S what happened. GDXAStarGP uses the Manhattan distance metric (effectively 4-way), while all other times here
 * used the Chebyshev metric (8-way, all equally weighted). That seems to speed up GDXAStarGP unfairly, so I switched it
 * to match the rest and use Chebyshev. Lo and behold, it is now just a tiny bit slower than GDXAStarCoord, instead of
 * about twice as fast or faster.
 * <pre>
 * Benchmark                                  Mode  Cnt     Score     Error  Units
 * PathfindingBenchmark.doPathAStarSearch     avgt    4   688.900 ±  90.273  ms/op
 * PathfindingBenchmark.doPathBitDijkstra     avgt    4  1333.449 ± 136.421  ms/op
 * PathfindingBenchmark.doPathCDijkstra       avgt    4  1374.697 ± 156.989  ms/op
 * PathfindingBenchmark.doPathCustomDijkstra  avgt    4  2468.169 ± 110.896  ms/op
 * PathfindingBenchmark.doPathDijkstra        avgt    4  1543.327 ±  54.205  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord   avgt    4   978.116 ±  26.467  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP      avgt    4   998.174 ±  16.103  ms/op
 * PathfindingBenchmark.doPathNate            avgt    4  2385.063 ± 109.239  ms/op
 * PathfindingBenchmark.doPathSimpleD         avgt    4   540.575 ±  18.275  ms/op
 * PathfindingBenchmark.doPathSimpleGPD       avgt    4   547.958 ±   7.451  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD      avgt    4   538.080 ±  39.487  ms/op
 * PathfindingBenchmark.doPathSimpleUD        avgt    4   542.024 ±  17.646  ms/op
 * PathfindingBenchmark.doPathSquadCG         avgt    4   613.782 ±   9.437  ms/op
 * PathfindingBenchmark.doPathSquadD          avgt    4   799.459 ±  11.113  ms/op
 * PathfindingBenchmark.doPathSquadDG         avgt    4   575.314 ±  59.555  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra   avgt    4  1307.063 ±  50.400  ms/op
 * PathfindingBenchmark.doPathSquadUD         avgt    4   637.927 ±  14.603  ms/op
 * PathfindingBenchmark.doPathSquidCG         avgt    4   646.996 ±  28.062  ms/op
 * PathfindingBenchmark.doPathSquidD          avgt    4   728.152 ±  17.170  ms/op
 * PathfindingBenchmark.doPathSquidDG         avgt    4   628.153 ±  14.555  ms/op
 * PathfindingBenchmark.doPathSquidUD         avgt    4   707.871 ±  58.645  ms/op
 * </pre>
 * <br>
 * Just testing "One" path benchmarks...
 * <pre>
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doOneGDXAStarCoord  avgt    5  45.167 ± 2.560  ms/op
 * PathfindingBenchmark.doOneGDXAStarGP     avgt    5  41.186 ± 2.570  ms/op
 * PathfindingBenchmark.doOneNate           avgt    5  50.798 ± 0.431  ms/op
 * PathfindingBenchmark.doOneSimpleGPD      avgt    5  23.082 ± 0.317  ms/op
 * PathfindingBenchmark.doOneSimpleGPUD     avgt    5  22.112 ± 0.278  ms/op
 * PathfindingBenchmark.doOneSquadUD        avgt    5  46.873 ± 1.155  ms/op
 * PathfindingBenchmark.doOneSquidUD        avgt    5  46.210 ± 1.541  ms/op
 * </pre>
 * Comparing SquidLib's DijkstraMap, SquidSquad's current DijkstraMap, and the newly-optimized "DextraMap"
 * on a 200x200 map:
 * <pre>
 * Benchmark                                     Mode  Cnt      Score      Error  Units
 * PathfindingBenchmark.doOneDijkstra            avgt    3      2.428 ±    1.318  ms/op
 * PathfindingBenchmark.doOneSquadDextra         avgt    3      1.250 ±    0.464  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra       avgt    3      1.885 ±    0.146  ms/op
 *
 * PathfindingBenchmark.doPathDijkstra           avgt    3  27807.447 ± 4358.417  ms/op
 * PathfindingBenchmark.doPathSquadDextra        avgt    3  20689.887 ± 3692.350  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra      avgt    3  25391.540 ±  263.612  ms/op
 *
 * PathfindingBenchmark.doTinyPathDijkstra       avgt    3    753.097 ±   87.077  ms/op
 * PathfindingBenchmark.doTinyPathSquadDextra    avgt    3    437.437 ±   44.791  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra  avgt    3    473.089 ±  116.007  ms/op
 *
 * PathfindingBenchmark.doScanDijkstra           avgt    3  52352.006 ± 2272.243  ms/op
 * </pre>
 * Testing scan() on DijkstraMap variants on a 60x60 map:
 * <pre>
 * Benchmark                                 Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doScanDijkstra       avgt    3  359.114 ±  6.562  ms/op
 * PathfindingBenchmark.doScanSquadDextra    avgt    3  203.085 ± 11.306  ms/op
 * PathfindingBenchmark.doScanSquadDijkstra  avgt    3  352.705 ± 49.611  ms/op
 * </pre>
 * Comparing doPath() across all pathfinders, on a 60x60 map:
 * <pre>
 * Benchmark                                 Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathAStarSearch    avgt    3   99.126 ± 14.026  ms/op
 * PathfindingBenchmark.doPathSquidD         avgt    3  104.002 ± 11.520  ms/op
 * PathfindingBenchmark.doPathSquidUD        avgt    3   98.026 ±  8.747  ms/op
 * PathfindingBenchmark.doPathSquidCG        avgt    3   85.525 ±  3.519  ms/op
 * PathfindingBenchmark.doPathSquidDG        avgt    3   85.213 ± 20.176  ms/op
 *
 * PathfindingBenchmark.doPathSquadD         avgt    3  104.229 ±  7.172  ms/op
 * PathfindingBenchmark.doPathSquadUD        avgt    3  101.110 ±  4.301  ms/op
 * PathfindingBenchmark.doPathSquadCG        avgt    3   91.927 ± 17.840  ms/op
 * PathfindingBenchmark.doPathSquadDG        avgt    3   83.227 ± 17.788  ms/op
 *
 * PathfindingBenchmark.doPathSimpleD        avgt    3   85.420 ±  7.541  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    3   87.731 ± 10.085  ms/op
 * PathfindingBenchmark.doPathSimpleGPD      avgt    3   85.494 ± 30.486  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD     avgt    3   84.426 ± 19.388  ms/op
 *
 * PathfindingBenchmark.doPathUpdateD        avgt    3  107.068 ± 25.616  ms/op
 * PathfindingBenchmark.doPathUpdateGPD      avgt    3  107.284 ±  5.546  ms/op
 * PathfindingBenchmark.doPathUpdateGPUD     avgt    3  123.873 ± 15.302  ms/op
 * PathfindingBenchmark.doPathUpdateUD       avgt    3  108.048 ± 21.104  ms/op
 *
 * PathfindingBenchmark.doPathGDXAStarCoord  avgt    3  174.016 ± 17.006  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP     avgt    3  158.030 ± 21.410  ms/op
 *
 * PathfindingBenchmark.doPathNate           avgt    3  269.572 ± 29.253  ms/op
 *
 * PathfindingBenchmark.doPathDijkstra       avgt    3  213.346 ± 75.960  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra  avgt    3  196.333 ±  7.116  ms/op
 * PathfindingBenchmark.doPathSquadDextra    avgt    3  129.962 ± 22.164  ms/op
 * </pre>
 * <br>
 * Add Gand, it's decent...
 * <pre>
 * Benchmark                                 Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathAStarSearch    avgt    5  100.794 ±  4.561  ms/op
 * PathfindingBenchmark.doPathDijkstra       avgt    5  205.146 ± 15.489  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord  avgt    5  177.497 ±  2.733  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP     avgt    5  179.028 ±  8.004  ms/op
 * PathfindingBenchmark.doPathGandD          avgt    5  111.594 ± 16.191  ms/op
 * PathfindingBenchmark.doPathGandUD         avgt    5  107.475 ±  5.470  ms/op
 * PathfindingBenchmark.doPathNate           avgt    5  287.877 ±  6.429  ms/op
 * PathfindingBenchmark.doPathSimpleD        avgt    5   91.588 ±  4.530  ms/op
 * PathfindingBenchmark.doPathSimpleGPD      avgt    5   90.307 ±  3.450  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD     avgt    5   88.294 ±  4.110  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    5   87.892 ±  1.099  ms/op
 * PathfindingBenchmark.doPathSquadCG        avgt    5   89.742 ±  2.452  ms/op
 * PathfindingBenchmark.doPathSquadD         avgt    5  117.780 ±  5.825  ms/op
 * PathfindingBenchmark.doPathSquadDG        avgt    5   87.349 ±  5.901  ms/op
 * PathfindingBenchmark.doPathSquadDextra    avgt    5  129.603 ±  5.012  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra  avgt    5  177.503 ±  7.424  ms/op
 * PathfindingBenchmark.doPathSquadUD        avgt    5  101.663 ±  6.197  ms/op
 * PathfindingBenchmark.doPathSquidCG        avgt    5   90.188 ±  4.941  ms/op
 * PathfindingBenchmark.doPathSquidD         avgt    5  108.224 ±  4.419  ms/op
 * PathfindingBenchmark.doPathSquidDG        avgt    5   91.629 ±  2.485  ms/op
 * PathfindingBenchmark.doPathSquidUD        avgt    5  104.708 ± 12.265  ms/op
 * PathfindingBenchmark.doPathUpdateD        avgt    5  116.903 ±  7.765  ms/op
 * PathfindingBenchmark.doPathUpdateUD       avgt    5  123.612 ± 12.652  ms/op
 * </pre>
 * <br>
 * That's more like it.
 * <pre>
 * Benchmark                                 Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathAStarSearch    avgt    5  102.008 ±  2.544  ms/op
 * PathfindingBenchmark.doPathDijkstra       avgt    5  200.748 ± 14.854  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord  avgt    5  152.216 ±  5.499  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP     avgt    5  148.781 ±  2.076  ms/op
 * PathfindingBenchmark.doPathGandD          avgt    5   86.486 ±  4.249  ms/op
 * PathfindingBenchmark.doPathGandUD         avgt    5   88.571 ±  6.369  ms/op
 * PathfindingBenchmark.doPathNate           avgt    5  284.124 ± 11.564  ms/op
 * PathfindingBenchmark.doPathSimpleD        avgt    5   90.866 ±  2.983  ms/op
 * PathfindingBenchmark.doPathSimpleGPD      avgt    5   86.018 ±  1.451  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD     avgt    5   89.248 ±  6.616  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    5   90.672 ±  3.649  ms/op
 * PathfindingBenchmark.doPathSquadCG        avgt    5   84.807 ±  3.530  ms/op
 * PathfindingBenchmark.doPathSquadD         avgt    5  111.338 ±  2.752  ms/op
 * PathfindingBenchmark.doPathSquadDG        avgt    5   87.315 ±  3.192  ms/op
 * PathfindingBenchmark.doPathSquadDextra    avgt    5  118.472 ±  5.174  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra  avgt    5  178.991 ± 16.849  ms/op
 * PathfindingBenchmark.doPathSquadUD        avgt    5  103.877 ±  3.920  ms/op
 * PathfindingBenchmark.doPathSquidCG        avgt    5   89.737 ±  4.900  ms/op
 * PathfindingBenchmark.doPathSquidD         avgt    5  106.923 ±  2.238  ms/op
 * PathfindingBenchmark.doPathSquidDG        avgt    5   91.189 ±  5.682  ms/op
 * PathfindingBenchmark.doPathSquidUD        avgt    5  104.971 ±  3.627  ms/op
 * PathfindingBenchmark.doPathUpdateD        avgt    5  111.345 ±  4.609  ms/op
 * PathfindingBenchmark.doPathUpdateUD       avgt    5  109.997 ±  1.495  ms/op
 * </pre>
 * <br>
 * But, Gand 0.0.1 is slower again (not by as much, but, not great...):
 * <pre>
 * Benchmark                                 Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathAStarSearch    avgt    5  105.333 ±  7.152  ms/op
 * PathfindingBenchmark.doPathDijkstra       avgt    5  205.476 ± 17.046  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord  avgt    5  158.551 ±  9.655  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP     avgt    5  155.233 ±  8.881  ms/op
 * PathfindingBenchmark.doPathGandD          avgt    5  105.141 ±  5.062  ms/op
 * PathfindingBenchmark.doPathGandUD         avgt    5  106.147 ±  7.762  ms/op
 * PathfindingBenchmark.doPathNate           avgt    5  282.226 ± 14.515  ms/op
 * PathfindingBenchmark.doPathSimpleD        avgt    5   91.728 ±  3.115  ms/op
 * PathfindingBenchmark.doPathSimpleGPD      avgt    5   87.272 ±  5.114  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD     avgt    5   88.252 ±  4.025  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    5   92.048 ±  2.985  ms/op
 * PathfindingBenchmark.doPathSquadCG        avgt    5   92.794 ±  7.276  ms/op
 * PathfindingBenchmark.doPathSquadD         avgt    5  113.659 ±  5.616  ms/op
 * PathfindingBenchmark.doPathSquadDG        avgt    5   86.924 ±  3.866  ms/op
 * PathfindingBenchmark.doPathSquadDextra    avgt    5  125.753 ±  7.059  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra  avgt    5  171.804 ±  5.855  ms/op
 * PathfindingBenchmark.doPathSquadUD        avgt    5  109.523 ±  5.481  ms/op
 * PathfindingBenchmark.doPathSquidCG        avgt    5   86.101 ±  3.490  ms/op
 * PathfindingBenchmark.doPathSquidD         avgt    5  106.215 ±  2.592  ms/op
 * PathfindingBenchmark.doPathSquidDG        avgt    5   87.898 ±  2.326  ms/op
 * PathfindingBenchmark.doPathSquidUD        avgt    5  106.141 ±  4.122  ms/op
 * PathfindingBenchmark.doPathUpdateD        avgt    5  118.877 ±  6.141  ms/op
 * PathfindingBenchmark.doPathUpdateUD       avgt    5  113.374 ±  5.286  ms/op
 * </pre>
 * <br>
 * Here it looks like Gand is back to being competitive with Simple-Graphs, and is faster than SquidSquad when it isn't
 * using its type-specialized pathfinding.
 * <pre>
 * Benchmark                                 Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathGandD          avgt    5   91.680 ±  4.727  ms/op
 * PathfindingBenchmark.doPathGandGPD        avgt    5   89.341 ±  3.798  ms/op
 * PathfindingBenchmark.doPathGandGPUD       avgt    5   95.574 ±  5.352  ms/op
 * PathfindingBenchmark.doPathGandUD         avgt    5   98.291 ±  8.897  ms/op
 * PathfindingBenchmark.doPathSimpleD        avgt    5   92.906 ±  7.193  ms/op
 * PathfindingBenchmark.doPathSimpleGPD      avgt    5   88.475 ±  5.272  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD     avgt    5   86.679 ±  7.398  ms/op
 * PathfindingBenchmark.doPathSimpleUD       avgt    5   98.021 ±  9.149  ms/op
 * PathfindingBenchmark.doPathSquadCG        avgt    5   89.798 ±  4.549  ms/op
 * PathfindingBenchmark.doPathSquadD         avgt    5  106.164 ±  7.183  ms/op
 * PathfindingBenchmark.doPathSquadDG        avgt    5   86.703 ±  7.860  ms/op
 * PathfindingBenchmark.doPathSquadDextra    avgt    5  127.364 ± 10.186  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra  avgt    5  176.347 ± 15.541  ms/op
 * PathfindingBenchmark.doPathSquadUD        avgt    5   99.600 ±  3.707  ms/op
 * </pre>
 * <br>
 * Gand does quite well with the long-multiply-and-shift based hash() method.
 * This benchmark was done on Java 21 (HotSpot), which may explain the faster times all around.
 * <pre>
 * Benchmark                              Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathGandD       avgt    5   88.065 ±  5.976  ms/op
 * PathfindingBenchmark.doPathGandGPD     avgt    5   84.130 ±  1.429  ms/op
 * PathfindingBenchmark.doPathGandGPUD    avgt    5   91.360 ±  8.520  ms/op
 * PathfindingBenchmark.doPathGandUD      avgt    5   91.981 ±  6.990  ms/op
 * PathfindingBenchmark.doPathGandVD      avgt    5   89.918 ±  7.754  ms/op
 * PathfindingBenchmark.doPathGandVUD     avgt    5   89.740 ±  9.491  ms/op
 * PathfindingBenchmark.doPathSimpleD     avgt    5   91.593 ±  6.388  ms/op
 * PathfindingBenchmark.doPathSimpleGPD   avgt    5   87.722 ±  7.374  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD  avgt    5   86.635 ±  5.672  ms/op
 * PathfindingBenchmark.doPathSimpleUD    avgt    5   88.345 ±  4.861  ms/op
 * PathfindingBenchmark.doPathSimpleVD    avgt    5   91.605 ± 10.770  ms/op
 * PathfindingBenchmark.doPathSimpleVUD   avgt    5   97.987 ± 10.791  ms/op
 * PathfindingBenchmark.doPathSquadD      avgt    5   94.244 ±  3.036  ms/op
 * PathfindingBenchmark.doPathSquadUD     avgt    5   90.522 ±  3.184  ms/op
 * PathfindingBenchmark.doPathSquidD      avgt    5  100.839 ±  3.748  ms/op
 * PathfindingBenchmark.doPathSquidUD     avgt    5  105.825 ±  2.997  ms/op
 * PathfindingBenchmark.doPathUpdateD     avgt    5  112.493 ± 11.546  ms/op
 * PathfindingBenchmark.doPathUpdateUD    avgt    5  114.295 ±  4.411  ms/op
 * </pre>
 * <br>
 * These benchmarks used Graal 21, and are even faster.
 * <pre>
 * Benchmark                              Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathGandD       avgt    5   85.348 ±  2.918  ms/op
 * PathfindingBenchmark.doPathGandGPD     avgt    5   86.529 ±  6.207  ms/op
 * PathfindingBenchmark.doPathGandGPUD    avgt    5   82.921 ±  5.128  ms/op
 * PathfindingBenchmark.doPathGandUD      avgt    5   85.252 ±  8.089  ms/op
 * PathfindingBenchmark.doPathGandVD      avgt    5   84.046 ±  5.013  ms/op
 * PathfindingBenchmark.doPathGandVUD     avgt    5   84.325 ±  6.253  ms/op
 * PathfindingBenchmark.doPathSimpleD     avgt    5   85.145 ± 10.070  ms/op
 * PathfindingBenchmark.doPathSimpleGPD   avgt    5   83.547 ±  9.850  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD  avgt    5   85.983 ±  7.908  ms/op
 * PathfindingBenchmark.doPathSimpleUD    avgt    5   85.089 ±  6.965  ms/op
 * PathfindingBenchmark.doPathSimpleVD    avgt    5   84.012 ±  7.179  ms/op
 * PathfindingBenchmark.doPathSimpleVUD   avgt    5   82.364 ±  4.729  ms/op
 * PathfindingBenchmark.doPathSquadD      avgt    5   99.211 ±  2.938  ms/op
 * PathfindingBenchmark.doPathSquadUD     avgt    5   94.450 ±  3.282  ms/op
 * PathfindingBenchmark.doPathSquidD      avgt    5   91.733 ±  6.331  ms/op
 * PathfindingBenchmark.doPathSquidUD     avgt    5  102.381 ±  8.808  ms/op
 * PathfindingBenchmark.doPathUpdateD     avgt    5  109.875 ±  4.879  ms/op
 * PathfindingBenchmark.doPathUpdateUD    avgt    5  109.953 ±  6.181  ms/op
 * </pre>
 * <br>
 * These benchmarks use Graal 21 but as a native-image, and are all a little slower.
 * <pre>
 * Benchmark                              Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathGandD       avgt    5  110.861 ┬▒  5.333  ms/op
 * PathfindingBenchmark.doPathGandGPD     avgt    5  111.335 ┬▒  4.575  ms/op
 * PathfindingBenchmark.doPathGandGPUD    avgt    5  112.075 ┬▒ 10.512  ms/op
 * PathfindingBenchmark.doPathGandUD      avgt    5  117.298 ┬▒ 10.469  ms/op
 * PathfindingBenchmark.doPathGandVD      avgt    5  110.249 ┬▒ 11.113  ms/op
 * PathfindingBenchmark.doPathGandVUD     avgt    5  112.560 ┬▒  5.155  ms/op
 * PathfindingBenchmark.doPathSimpleD     avgt    5  109.391 ┬▒  8.257  ms/op
 * PathfindingBenchmark.doPathSimpleGPD   avgt    5  101.312 ┬▒ 11.169  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD  avgt    5  100.855 ┬▒  7.589  ms/op
 * PathfindingBenchmark.doPathSimpleUD    avgt    5  113.416 ┬▒ 16.365  ms/op
 * PathfindingBenchmark.doPathSimpleVD    avgt    5  104.496 ┬▒  6.021  ms/op
 * PathfindingBenchmark.doPathSimpleVUD   avgt    5  107.814 ┬▒ 11.690  ms/op
 * PathfindingBenchmark.doPathSquadD      avgt    5  107.717 ┬▒  4.724  ms/op
 * PathfindingBenchmark.doPathSquadUD     avgt    5  111.091 ┬▒  7.319  ms/op
 * PathfindingBenchmark.doPathSquidD      avgt    5  109.249 ┬▒  8.252  ms/op
 * PathfindingBenchmark.doPathSquidUD     avgt    5  108.593 ┬▒  5.739  ms/op
 * PathfindingBenchmark.doPathUpdateD     avgt    5  137.515 ┬▒  4.414  ms/op
 * PathfindingBenchmark.doPathUpdateUD    avgt    5  144.917 ┬▒  7.125  ms/op
 * </pre>
 * <br>
 * Then I wanted to figure out if the custom hash() in Gand's Grid2DDirectedGraph mattered. It doesn't, really.
 * Tested with a Graal 21 JVM, but many more iterations than usual (and 3 forks):
 * <pre>
 * C:\d\jvm\graal21\bin\java -jar benchmarks.jar "PathfindingBenchmark.doPath.*Gand(D|G2D|GPD|VD)$" -wi 8 -i 8 -f 3 -w 10 -r 10
 *
 * Benchmark                           Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doPathGandD    avgt   24  84.131 ± 2.524  ms/op
 * PathfindingBenchmark.doPathGandG2D  avgt   24  81.051 ± 1.101  ms/op
 * PathfindingBenchmark.doPathGandGPD  avgt   24  81.817 ± 0.875  ms/op
 * PathfindingBenchmark.doPathGandVD   avgt   24  85.488 ± 1.459  ms/op
 * </pre>
 * <br>
 * Testing all directed or undirected (so, some type of A-Star) graph pathfinders.
 * Gand, Simple-Graphs, SquidSquad, SquidLib, and Update. No AStarSearch, no gdx-ai, no DijkstraMap.
 * <pre>
 * C:\d\jvm\graal21\bin\java -jar benchmarks.jar "PathfindingBenchmark.doPath.*[DG]$" -wi 5 -i 5 -f 1 -w 5 -r 5
 *
 * Benchmark                              Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doPathGandD       avgt    5   82.697 ±  5.243  ms/op
 * PathfindingBenchmark.doPathGandG2D     avgt    5   80.084 ±  3.932  ms/op
 * PathfindingBenchmark.doPathGandG2UD    avgt    5   83.713 ±  7.932  ms/op
 * PathfindingBenchmark.doPathGandGPD     avgt    5   79.641 ±  4.886  ms/op
 * PathfindingBenchmark.doPathGandGPUD    avgt    5   83.336 ± 10.908  ms/op
 * PathfindingBenchmark.doPathGandUD      avgt    5   84.486 ±  4.271  ms/op
 * PathfindingBenchmark.doPathGandVD      avgt    5   84.763 ±  7.348  ms/op
 * PathfindingBenchmark.doPathGandVUD     avgt    5   86.996 ±  5.695  ms/op
 * PathfindingBenchmark.doPathSimpleD     avgt    5   85.160 ±  9.966  ms/op
 * PathfindingBenchmark.doPathSimpleGPD   avgt    5   82.463 ± 11.302  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD  avgt    5   85.261 ±  4.678  ms/op
 * PathfindingBenchmark.doPathSimpleUD    avgt    5   88.030 ± 11.681  ms/op
 * PathfindingBenchmark.doPathSimpleVD    avgt    5   83.728 ± 10.329  ms/op
 * PathfindingBenchmark.doPathSimpleVUD   avgt    5   81.958 ±  5.237  ms/op
 * PathfindingBenchmark.doPathSquadCG     avgt    5   85.878 ±  2.749  ms/op
 * PathfindingBenchmark.doPathSquadD      avgt    5   93.153 ±  5.530  ms/op
 * PathfindingBenchmark.doPathSquadDG     avgt    5   85.914 ±  4.664  ms/op
 * PathfindingBenchmark.doPathSquadUD     avgt    5   96.076 ±  9.565  ms/op
 * PathfindingBenchmark.doPathSquidCG     avgt    5   89.396 ±  9.290  ms/op
 * PathfindingBenchmark.doPathSquidD      avgt    5  100.374 ±  3.301  ms/op
 * PathfindingBenchmark.doPathSquidDG     avgt    5   86.724 ±  5.434  ms/op
 * PathfindingBenchmark.doPathSquidUD     avgt    5   90.471 ±  3.840  ms/op
 * PathfindingBenchmark.doPathUpdateD     avgt    5  112.092 ± 10.465  ms/op
 * PathfindingBenchmark.doPathUpdateUD    avgt    5  107.864 ± 15.506  ms/op
 * </pre>
 * <br>
 * Testing with HotSpot 21, gand vs. simple-graphs:
 * <pre>
 * Benchmark                              Mode  Cnt   Score    Error  Units
 * PathfindingBenchmark.doPathGandD       avgt    6  90.185 ±  3.859  ms/op
 * PathfindingBenchmark.doPathGandF2D     avgt    6  91.301 ±  2.814  ms/op
 * PathfindingBenchmark.doPathGandF2UD    avgt    6  97.420 ± 17.997  ms/op
 * PathfindingBenchmark.doPathGandGPD     avgt    6  86.968 ±  3.936  ms/op
 * PathfindingBenchmark.doPathGandGPUD    avgt    6  90.276 ±  3.554  ms/op
 * PathfindingBenchmark.doPathGandI2D     avgt    6  87.024 ±  1.841  ms/op
 * PathfindingBenchmark.doPathGandI2UD    avgt    6  91.188 ±  2.119  ms/op
 * PathfindingBenchmark.doPathGandUD      avgt    6  89.594 ±  3.466  ms/op
 * PathfindingBenchmark.doPathGandVD      avgt    6  86.989 ±  1.969  ms/op
 * PathfindingBenchmark.doPathGandVUD     avgt    6  90.665 ±  2.551  ms/op
 * PathfindingBenchmark.doPathSimpleD     avgt    6  86.347 ±  1.440  ms/op
 * PathfindingBenchmark.doPathSimpleGPD   avgt    6  86.724 ±  6.734  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD  avgt    6  86.327 ±  7.636  ms/op
 * PathfindingBenchmark.doPathSimpleUD    avgt    6  86.465 ±  1.625  ms/op
 * PathfindingBenchmark.doPathSimpleVD    avgt    6  89.704 ±  3.446  ms/op
 * PathfindingBenchmark.doPathSimpleVUD   avgt    6  87.182 ±  5.677  ms/op
 * </pre>
 * <br>
 * Testing with GraalVM 21, gand vs. simple-graphs, with some more tests:
 * <pre>
 * Benchmark                                   Mode  Cnt   Score    Error  Units
 * PathfindingBenchmark.doPathGandD            avgt    6  83.944 ±  3.680  ms/op
 * PathfindingBenchmark.doPathGandF2D          avgt    6  85.413 ±  4.384  ms/op
 * PathfindingBenchmark.doPathGandF2UD         avgt    6  85.223 ±  3.012  ms/op
 * PathfindingBenchmark.doPathGandGPD          avgt    6  85.223 ± 15.170  ms/op
 * PathfindingBenchmark.doPathGandGPUD         avgt    6  85.251 ±  4.396  ms/op
 * PathfindingBenchmark.doPathGandGenericF2D   avgt    6  84.996 ±  7.195  ms/op
 * PathfindingBenchmark.doPathGandGenericF2UD  avgt    6  85.356 ±  4.442  ms/op
 * PathfindingBenchmark.doPathGandGenericI2D   avgt    6  82.332 ±  5.332  ms/op
 * PathfindingBenchmark.doPathGandGenericI2UD  avgt    6  83.437 ±  3.359  ms/op
 * PathfindingBenchmark.doPathGandI2D          avgt    6  82.958 ±  2.692  ms/op
 * PathfindingBenchmark.doPathGandI2UD         avgt    6  86.136 ±  3.773  ms/op
 * PathfindingBenchmark.doPathGandUD           avgt    6  85.454 ±  4.622  ms/op
 * PathfindingBenchmark.doPathGandVD           avgt    6  84.587 ±  2.381  ms/op
 * PathfindingBenchmark.doPathGandVUD          avgt    6  89.802 ±  4.564  ms/op
 * PathfindingBenchmark.doPathSimpleD          avgt    6  85.582 ±  2.713  ms/op
 * PathfindingBenchmark.doPathSimpleGPD        avgt    6  84.040 ±  3.175  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD       avgt    6  84.308 ±  3.026  ms/op
 * PathfindingBenchmark.doPathSimpleUD         avgt    6  85.452 ±  5.027  ms/op
 * PathfindingBenchmark.doPathSimpleVD         avgt    6  84.966 ±  3.899  ms/op
 * PathfindingBenchmark.doPathSimpleVUD        avgt    6  84.552 ±  3.014  ms/op
 * </pre>
 * <br>
 * On a new laptop, Windows 11, 14-core (asymmetrical), 64GB RAM, using Graal 22 (not native)...
 * <br>
 * Comparing Gand to SquidSquad using only the One-path benchmark:
 * <pre>
 * Benchmark                                  Mode  Cnt  Score   Error  Units
 * PathfindingBenchmark.doOneGandF2D          avgt    5  0.173 ± 0.005  ms/op
 * PathfindingBenchmark.doOneGandF2UD         avgt    5  0.180 ± 0.006  ms/op
 * PathfindingBenchmark.doOneGandGPD          avgt    5  0.139 ± 0.004  ms/op
 * PathfindingBenchmark.doOneGandGPUD         avgt    5  0.137 ± 0.001  ms/op
 * PathfindingBenchmark.doOneGandGenericF2D   avgt    5  0.133 ± 0.003  ms/op
 * PathfindingBenchmark.doOneGandGenericF2UD  avgt    5  0.149 ± 0.002  ms/op
 * PathfindingBenchmark.doOneGandGenericI2D   avgt    5  0.130 ± 0.003  ms/op
 * PathfindingBenchmark.doOneGandGenericI2UD  avgt    5  0.136 ± 0.007  ms/op
 * PathfindingBenchmark.doOneGandI2D          avgt    5  0.135 ± 0.001  ms/op
 * PathfindingBenchmark.doOneGandI2UD         avgt    5  0.152 ± 0.002  ms/op
 * PathfindingBenchmark.doOneGandVD           avgt    5  0.126 ± 0.002  ms/op
 * PathfindingBenchmark.doOneGandVUD          avgt    5  0.138 ± 0.003  ms/op
 * PathfindingBenchmark.doOneSquadCG          avgt    5  0.119 ± 0.001  ms/op
 * PathfindingBenchmark.doOneSquadD           avgt    5  0.163 ± 0.002  ms/op
 * PathfindingBenchmark.doOneSquadDG          avgt    5  0.125 ± 0.001  ms/op
 * PathfindingBenchmark.doOneSquadDextra      avgt    5  0.225 ± 0.002  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra    avgt    5  0.287 ± 0.018  ms/op
 * PathfindingBenchmark.doOneSquadUD          avgt    5  0.138 ± 0.003  ms/op
 * </pre>
 * (Some surprises here; Gand's generic pathfinders seem better in general than its non-generic ones, including the
 * "V" tests, which test Vector2 and actually are also generic. Dextra also is quite a bit better than Dijkstra...)
 * <br>
 * Testing DextraMap vs. the current DijkstraMap code in SquidSquad, they end up quite close, with two results close
 * to the margin of error, and a tie for the other results (one win each, and never by much).
 * <pre>
 * Benchmark                                     Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doOneSquadDextra         avgt    5    0.200 ±  0.006  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra       avgt    5    0.208 ±  0.002  ms/op
 * PathfindingBenchmark.doPathSquadDextra        avgt    5  549.934 ±  6.447  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra      avgt    5  534.721 ±  4.372  ms/op
 * PathfindingBenchmark.doScanSquadDextra        avgt    5  900.927 ±  3.606  ms/op
 * PathfindingBenchmark.doScanSquadDijkstra      avgt    5  901.640 ± 11.716  ms/op
 * PathfindingBenchmark.doTinyPathSquadDextra    avgt    5   24.208 ±  0.201  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra  avgt    5   27.494 ±  0.277  ms/op
 * </pre>
 * <br>
 * Oddly, GradientGrid seems a little faster than DijkstraMap in SquidSquad, despite being very similar.
 * <pre>
 * Benchmark                                        Mode  Cnt    Score   Error  Units
 * PathfindingBenchmark.doOneGandGradientGrid       avgt    5    0.141 ± 0.003  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra          avgt    5    0.207 ± 0.002  ms/op
 * PathfindingBenchmark.doPathGandGradientGrid      avgt    5  501.545 ± 6.320  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra         avgt    5  533.430 ± 6.036  ms/op
 * PathfindingBenchmark.doScanGandGradientGrid      avgt    5  824.753 ± 6.801  ms/op
 * PathfindingBenchmark.doScanSquadDijkstra         avgt    5  898.153 ± 8.283  ms/op
 * PathfindingBenchmark.doTinyPathGandGradientGrid  avgt    5   26.621 ± 0.384  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra     avgt    5   24.954 ± 0.761  ms/op
 * </pre>
 * <br>
 * Run for 8 hours on everything, using
 * {@code ~\.jdks\graalvm-jdk-22.0.1\bin\java.exe -jar benchmarks.jar "PathfindingBench" -wi 8 -i 8 -f 3 -w 5 -r 5}
 * <pre>
 * Benchmark                                        Mode  Cnt     Score    Error  Units
 * PathfindingBenchmark.doOneDijkstra               avgt   24     0.421 ±  0.033  ms/op
 * PathfindingBenchmark.doOneGDXAStarCoord          avgt   24     0.227 ±  0.010  ms/op
 * PathfindingBenchmark.doOneGDXAStarGP             avgt   24     0.219 ±  0.006  ms/op
 * PathfindingBenchmark.doOneGandF2D                avgt   24     0.192 ±  0.008  ms/op
 * PathfindingBenchmark.doOneGandF2UD               avgt   24     0.205 ±  0.008  ms/op
 * PathfindingBenchmark.doOneGandGPD                avgt   24     0.178 ±  0.005  ms/op
 * PathfindingBenchmark.doOneGandGPUD               avgt   24     0.185 ±  0.007  ms/op
 * PathfindingBenchmark.doOneGandGenericF2D         avgt   24     0.197 ±  0.008  ms/op
 * PathfindingBenchmark.doOneGandGenericF2UD        avgt   24     0.208 ±  0.008  ms/op
 * PathfindingBenchmark.doOneGandGenericI2D         avgt   24     0.196 ±  0.009  ms/op
 * PathfindingBenchmark.doOneGandGenericI2UD        avgt   24     0.204 ±  0.008  ms/op
 * PathfindingBenchmark.doOneGandGradientGrid       avgt   24     0.148 ±  0.009  ms/op
 * PathfindingBenchmark.doOneGandI2D                avgt   24     0.143 ±  0.005  ms/op
 * PathfindingBenchmark.doOneGandI2UD               avgt   24     0.148 ±  0.005  ms/op
 * PathfindingBenchmark.doOneGandVD                 avgt   24     0.127 ±  0.003  ms/op
 * PathfindingBenchmark.doOneGandVUD                avgt   24     0.136 ±  0.006  ms/op
 * PathfindingBenchmark.doOneNate                   avgt   24     0.444 ±  0.020  ms/op
 * PathfindingBenchmark.doOneSimpleGPD              avgt   24     0.138 ±  0.005  ms/op
 * PathfindingBenchmark.doOneSimpleGPUD             avgt   24     0.137 ±  0.001  ms/op
 * PathfindingBenchmark.doOneSimpleVD               avgt   24     0.135 ±  0.001  ms/op
 * PathfindingBenchmark.doOneSimpleVUD              avgt   24     0.142 ±  0.003  ms/op
 * PathfindingBenchmark.doOneSquadCG                avgt   24     0.109 ±  0.004  ms/op
 * PathfindingBenchmark.doOneSquadD                 avgt   24     0.148 ±  0.003  ms/op
 * PathfindingBenchmark.doOneSquadDG                avgt   24     0.111 ±  0.003  ms/op
 * PathfindingBenchmark.doOneSquadDextra            avgt   24     0.196 ±  0.002  ms/op
 * PathfindingBenchmark.doOneSquadDijkstra          avgt   24     0.212 ±  0.009  ms/op
 * PathfindingBenchmark.doOneSquadUD                avgt   24     0.139 ±  0.006  ms/op
 * PathfindingBenchmark.doOneSquidCG                avgt   24     0.116 ±  0.009  ms/op
 * PathfindingBenchmark.doOneSquidD                 avgt   24     0.147 ±  0.004  ms/op
 * PathfindingBenchmark.doOneSquidDG                avgt   24     0.109 ±  0.001  ms/op
 * PathfindingBenchmark.doOneSquidUD                avgt   24     0.132 ±  0.002  ms/op
 * PathfindingBenchmark.doPathAStarSearch           avgt   24   413.773 ± 22.647  ms/op
 * PathfindingBenchmark.doPathDijkstra              avgt   24  1046.723 ± 13.607  ms/op
 * PathfindingBenchmark.doPathGDXAStarCoord         avgt   24   570.724 ±  6.981  ms/op
 * PathfindingBenchmark.doPathGDXAStarGP            avgt   24   535.192 ± 32.858  ms/op
 * PathfindingBenchmark.doPathGandD                 avgt   24   396.468 ±  5.599  ms/op
 * PathfindingBenchmark.doPathGandF2D               avgt   24   404.510 ±  2.324  ms/op
 * PathfindingBenchmark.doPathGandF2UD              avgt   24   412.631 ±  4.078  ms/op
 * PathfindingBenchmark.doPathGandGPD               avgt   24   370.374 ±  4.485  ms/op
 * PathfindingBenchmark.doPathGandGPUD              avgt   24   380.983 ±  4.503  ms/op
 * PathfindingBenchmark.doPathGandGenericF2D        avgt   24   392.977 ±  2.376  ms/op
 * PathfindingBenchmark.doPathGandGenericF2UD       avgt   24   389.593 ±  3.823  ms/op
 * PathfindingBenchmark.doPathGandGenericI2D        avgt   24   388.924 ±  3.150  ms/op
 * PathfindingBenchmark.doPathGandGenericI2UD       avgt   24   391.541 ±  6.556  ms/op
 * PathfindingBenchmark.doPathGandGradientGrid      avgt   24   479.409 ±  7.728  ms/op
 * PathfindingBenchmark.doPathGandI2D               avgt   24   392.450 ±  4.862  ms/op
 * PathfindingBenchmark.doPathGandI2UD              avgt   24   392.392 ±  4.415  ms/op
 * PathfindingBenchmark.doPathGandUD                avgt   24   396.536 ±  5.801  ms/op
 * PathfindingBenchmark.doPathGandVD                avgt   24   370.076 ±  3.204  ms/op
 * PathfindingBenchmark.doPathGandVUD               avgt   24   372.792 ±  4.295  ms/op
 * PathfindingBenchmark.doPathNate                  avgt   24  1258.861 ± 58.152  ms/op
 * PathfindingBenchmark.doPathSimpleD               avgt   24   383.484 ±  3.056  ms/op
 * PathfindingBenchmark.doPathSimpleGPD             avgt   24   376.424 ±  2.755  ms/op
 * PathfindingBenchmark.doPathSimpleGPUD            avgt   24   377.493 ±  4.344  ms/op
 * PathfindingBenchmark.doPathSimpleUD              avgt   24   378.644 ±  2.937  ms/op
 * PathfindingBenchmark.doPathSimpleVD              avgt   24   380.612 ±  2.183  ms/op
 * PathfindingBenchmark.doPathSimpleVUD             avgt   24   374.181 ±  2.579  ms/op
 * PathfindingBenchmark.doPathSquadCG               avgt   24   397.399 ± 11.676  ms/op
 * PathfindingBenchmark.doPathSquadD                avgt   24   441.698 ±  6.440  ms/op
 * PathfindingBenchmark.doPathSquadDG               avgt   24   377.684 ±  3.102  ms/op
 * PathfindingBenchmark.doPathSquadDextra           avgt   24   555.164 ± 41.317  ms/op
 * PathfindingBenchmark.doPathSquadDijkstra         avgt   24   531.723 ±  8.635  ms/op
 * PathfindingBenchmark.doPathSquadUD               avgt   24   429.440 ±  2.586  ms/op
 * PathfindingBenchmark.doPathSquidCG               avgt   24   382.096 ±  2.697  ms/op
 * PathfindingBenchmark.doPathSquidD                avgt   24   427.501 ±  5.630  ms/op
 * PathfindingBenchmark.doPathSquidDG               avgt   24   377.755 ±  3.523  ms/op
 * PathfindingBenchmark.doPathSquidUD               avgt   24   430.916 ±  2.795  ms/op
 * PathfindingBenchmark.doPathUpdateD               avgt   24   451.950 ±  5.405  ms/op
 * PathfindingBenchmark.doPathUpdateUD              avgt   24   462.008 ±  3.327  ms/op
 * PathfindingBenchmark.doScanDijkstra              avgt   24  1636.448 ±  7.607  ms/op
 * PathfindingBenchmark.doScanGandGradientGrid      avgt   24   808.388 ± 15.476  ms/op
 * PathfindingBenchmark.doScanSquadDextra           avgt   24   891.470 ±  9.228  ms/op
 * PathfindingBenchmark.doScanSquadDijkstra         avgt   24   884.114 ±  6.007  ms/op
 * PathfindingBenchmark.doTinyPathAStarSearch       avgt   24    23.468 ±  0.970  ms/op
 * PathfindingBenchmark.doTinyPathDijkstra          avgt   24    40.871 ±  0.343  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStarCoord     avgt   24    29.935 ±  0.237  ms/op
 * PathfindingBenchmark.doTinyPathGDXAStarGP        avgt   24    30.321 ±  0.735  ms/op
 * PathfindingBenchmark.doTinyPathGandD             avgt   24    21.394 ±  0.172  ms/op
 * PathfindingBenchmark.doTinyPathGandF2D           avgt   24    31.537 ±  0.523  ms/op
 * PathfindingBenchmark.doTinyPathGandF2UD          avgt   24    33.081 ±  0.330  ms/op
 * PathfindingBenchmark.doTinyPathGandGPD           avgt   24    20.650 ±  0.178  ms/op
 * PathfindingBenchmark.doTinyPathGandGPUD          avgt   24    21.237 ±  0.101  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2D    avgt   24    21.463 ±  0.292  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2UD   avgt   24    21.739 ±  0.161  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2D    avgt   24    21.224 ±  0.125  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2UD   avgt   24    21.853 ±  0.267  ms/op
 * PathfindingBenchmark.doTinyPathGandGradientGrid  avgt   24    24.797 ±  0.920  ms/op
 * PathfindingBenchmark.doTinyPathGandI2D           avgt   24    21.555 ±  0.190  ms/op
 * PathfindingBenchmark.doTinyPathGandI2UD          avgt   24    21.994 ±  0.201  ms/op
 * PathfindingBenchmark.doTinyPathGandUD            avgt   24    21.893 ±  0.231  ms/op
 * PathfindingBenchmark.doTinyPathGandVD            avgt   24    21.422 ±  0.375  ms/op
 * PathfindingBenchmark.doTinyPathGandVUD           avgt   24    21.438 ±  0.135  ms/op
 * PathfindingBenchmark.doTinyPathNate              avgt   24    51.112 ±  0.719  ms/op
 * PathfindingBenchmark.doTinyPathSimpleD           avgt   24    21.558 ±  0.127  ms/op
 * PathfindingBenchmark.doTinyPathSimpleGPD         avgt   24    21.282 ±  0.255  ms/op
 * PathfindingBenchmark.doTinyPathSimpleGPUD        avgt   24    21.562 ±  0.381  ms/op
 * PathfindingBenchmark.doTinyPathSimpleUD          avgt   24    21.877 ±  0.198  ms/op
 * PathfindingBenchmark.doTinyPathSimpleVD          avgt   24    21.753 ±  0.251  ms/op
 * PathfindingBenchmark.doTinyPathSimpleVUD         avgt   24    21.662 ±  0.162  ms/op
 * PathfindingBenchmark.doTinyPathSquadCG           avgt   24    20.604 ±  0.073  ms/op
 * PathfindingBenchmark.doTinyPathSquadD            avgt   24    22.660 ±  0.206  ms/op
 * PathfindingBenchmark.doTinyPathSquadDG           avgt   24    20.382 ±  0.199  ms/op
 * PathfindingBenchmark.doTinyPathSquadDextra       avgt   24    26.417 ±  0.281  ms/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra     avgt   24    25.988 ±  1.044  ms/op
 * PathfindingBenchmark.doTinyPathSquadUD           avgt   24    23.051 ±  0.276  ms/op
 * PathfindingBenchmark.doTinyPathSquidCG           avgt   24    20.839 ±  0.212  ms/op
 * PathfindingBenchmark.doTinyPathSquidD            avgt   24    22.769 ±  0.324  ms/op
 * PathfindingBenchmark.doTinyPathSquidDG           avgt   24    20.662 ±  0.127  ms/op
 * PathfindingBenchmark.doTinyPathSquidUD           avgt   24    23.243 ±  0.154  ms/op
 * PathfindingBenchmark.doTinyPathUpdateD           avgt   24    23.789 ±  0.215  ms/op
 * PathfindingBenchmark.doTinyPathUpdateUD          avgt   24    25.068 ±  0.219  ms/op
 * </pre>
 * <br>
 * A smaller comparison, just tiny paths with Gand -- F2 pathfinding takes about 40-50% more time
 * than generic PointF2 items, which suggests the mixing done on generic hash codes is important.
 * This uses commit hash 86c5773143 .
 * <pre>
 * Benchmark                                       Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doTinyPathGandF2D          avgt    8  30.678 ± 0.689  ms/op
 * PathfindingBenchmark.doTinyPathGandF2UD         avgt    8  31.654 ± 0.804  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2D   avgt    8  21.340 ± 0.081  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2UD  avgt    8  21.574 ± 0.111  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2D   avgt    8  21.614 ± 0.147  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2UD  avgt    8  22.385 ± 0.067  ms/op
 * PathfindingBenchmark.doTinyPathGandI2D          avgt    8  22.035 ± 0.490  ms/op
 * PathfindingBenchmark.doTinyPathGandI2UD         avgt    8  22.532 ± 0.099  ms/op
 * </pre>
 * <br>
 * Much better. This uses some multiplication in PointF2.hashCode(), which helps the mixing.
 * <pre>
 * Benchmark                                       Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doTinyPathGandF2D          avgt    8  21.660 ± 0.173  ms/op
 * PathfindingBenchmark.doTinyPathGandF2UD         avgt    8  22.223 ± 0.103  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2D   avgt    8  22.120 ± 0.131  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2UD  avgt    8  22.225 ± 0.097  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2D   avgt    8  21.508 ± 0.063  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2UD  avgt    8  21.635 ± 0.167  ms/op
 * PathfindingBenchmark.doTinyPathGandI2D          avgt    8  21.930 ± 0.142  ms/op
 * PathfindingBenchmark.doTinyPathGandI2UD         avgt    8  21.746 ± 0.091  ms/op
 * </pre>
 * <br>
 * Maybe I should roll some changes back to the 0.2.0 Gand release... The performance is very close, though.
 * <pre>
 * Benchmark                                       Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doTinyPathGandF2D          avgt    8  21.742 ± 1.539  ms/op
 * PathfindingBenchmark.doTinyPathGandF2UD         avgt    8  22.200 ± 0.418  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2D   avgt    8  21.647 ± 0.130  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericF2UD  avgt    8  21.820 ± 0.109  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2D   avgt    8  21.337 ± 0.129  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2UD  avgt    8  21.647 ± 0.061  ms/op
 * PathfindingBenchmark.doTinyPathGandI2D          avgt    8  21.672 ± 0.079  ms/op
 * PathfindingBenchmark.doTinyPathGandI2UD         avgt    8  21.970 ± 0.158  ms/op
 * </pre>
 * <br>
 * Comparing Gand 0.2.0 PointI2 vs. SquidSquad Coord, they are very close:
 * <pre>
 * Benchmark                                          Mode  Cnt    Score    Error  Units
 * PathfindingBenchmark.doOneGandGenericCoordD        avgt    8    0.165 ±  0.026  ms/op
 * PathfindingBenchmark.doOneGandGenericCoordUD       avgt    8    0.170 ±  0.027  ms/op
 * PathfindingBenchmark.doOneGandGenericI2D           avgt    8    0.153 ±  0.027  ms/op
 * PathfindingBenchmark.doOneGandGenericI2UD          avgt    8    0.162 ±  0.032  ms/op
 * PathfindingBenchmark.doPathGandGenericCoordD       avgt    8  423.537 ± 67.643  ms/op
 * PathfindingBenchmark.doPathGandGenericCoordUD      avgt    8  419.356 ± 66.939  ms/op
 * PathfindingBenchmark.doPathGandGenericI2D          avgt    8  442.204 ± 69.822  ms/op
 * PathfindingBenchmark.doPathGandGenericI2UD         avgt    8  425.530 ± 63.016  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericCoordD   avgt    8   21.557 ±  0.651  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericCoordUD  avgt    8   22.290 ±  0.148  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2D      avgt    8   21.605 ±  0.074  ms/op
 * PathfindingBenchmark.doTinyPathGandGenericI2UD     avgt    8   22.006 ±  0.086  ms/op
 * </pre>
 * Just testing tiny paths with Vector2, there's not much difference at all:
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * PathfindingBenchmark.doTinyPathGandVD     avgt    5  44.759 ± 2.162  ms/op
 * PathfindingBenchmark.doTinyPathGandVUD    avgt    5  45.374 ± 0.760  ms/op
 * PathfindingBenchmark.doTinyPathSimpleVD   avgt    5  45.570 ± 0.429  ms/op
 * PathfindingBenchmark.doTinyPathSimpleVUD  avgt    5  46.589 ± 1.886  ms/op
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 4)
@Measurement(iterations = 3)
public class PathfindingBenchmark {
    private static final GridPoint2 start = new GridPoint2(), end = new GridPoint2();
    private static final Vector2 startV = new Vector2(), endV = new Vector2();
    private static final PointI2 startI = new PointI2(), endI = new PointI2();
    private static final PointF2 startF = new PointF2(), endF = new PointF2();

    @State(Scope.Thread)
    public static class BenchmarkState {
        public static final int WIDTH = 128;
        public static final int HEIGHT = 128;
        public DungeonGenerator dungeonGen = new DungeonGenerator(WIDTH, HEIGHT, new StatefulRNG(0x1337BEEFDEAL));
        public char[][] map;
        public double[][] astarMap;
        public float[][] squadAstarMap;
        public GreasedRegion floors;
        public ArrayList<GridPoint2> gpFloors;
        public ArrayList<Vector2> vFloors;
        public ArrayList<PointI2> iFloors;
        public ArrayList<PointF2> fFloors;
        public Region squadFloors;
        public int floorCount;
        public Coord[] floorArray;
        public GreasedRegion tmp;
        public Coord lowest, highest;
        public GridPoint2 lowestGP, highestGP;
        public Vector2 lowestV, highestV;
        public PointI2 lowestI, highestI;
        public PointF2 lowestF, highestF;
        public Coord[][] nearbyMap;
        public com.github.yellowstonegames.grid.Coord squadLowest, squadHighest;
        public com.github.yellowstonegames.grid.Coord[][] squadNearbyMap;
        public GridPoint2[][] gpNearbyMap;
        public Vector2[][] vNearbyMap;
        public PointI2[][] iNearbyMap;
        public PointF2[][] fNearbyMap;
        public com.github.yellowstonegames.grid.Coord[] squadFloorArray;
        public int[] customNearbyMap;
        public Adjacency adj;
        public DijkstraMap dijkstra;
        public CustomDijkstraMap customDijkstra;
        public com.github.yellowstonegames.path.DijkstraMap squadDijkstra;
        public squid.squad.DextraMap squadDextra;
        public GradientGridI2 gradientGrid;
//        public BitDijkstraMap bitDijkstra;
//        public CDijkstraMap cDijkstra;
        public StatefulRNG srng;
        public GridGraphGP gg;
        public GridGraphCoord gg2;
        public IndexedAStarPathFinder<GridPoint2> astar;
        public IndexedAStarPathFinder<Coord> astar2;
        public AStarSearch as;
        public DefaultGraphPath<GridPoint2> dgpgp;
        public DefaultGraphPath<Coord> dgp;
        public ArrayList<Coord> path;
        public Path<com.github.yellowstonegames.grid.Coord> simplePath;
        public graph.sg.Path<com.github.yellowstonegames.grid.Coord> updatePath;
        public com.github.tommyettinger.gand.Path<com.github.yellowstonegames.grid.Coord> gandPath;
        public ObjectDeque<com.github.yellowstonegames.grid.Coord> squadPath;
        public Path<GridPoint2> sggpPath;
        public com.github.tommyettinger.gand.Path<GridPoint2> ggpPath;
        public Path<Vector2> sgvPath;
        public com.github.tommyettinger.gand.Path<Vector2> gvPath;
        public com.github.tommyettinger.gand.Path<PointI2> giPath;
        public com.github.tommyettinger.gand.Path<PointF2> gfPath;
        public com.github.tommyettinger.gand.Path<com.github.yellowstonegames.grid.Coord> gCoordPath;

        //        public graph.sg.Path<GridPoint2> upgpPath;

        public DirectedGraph<com.github.yellowstonegames.grid.Coord> simpleDirectedGraph;
        public UndirectedGraph<com.github.yellowstonegames.grid.Coord> simpleUndirectedGraph;
        public space.earlygrey.simplegraphs.utils.Heuristic<com.github.yellowstonegames.grid.Coord> simpleHeu;

        public DirectedGraph<GridPoint2> sggpDirectedGraph;
        public UndirectedGraph<GridPoint2> sggpUndirectedGraph;
        public space.earlygrey.simplegraphs.utils.Heuristic<GridPoint2> sggpHeu;

        public DirectedGraph<Vector2> sgvDirectedGraph;
        public UndirectedGraph<Vector2> sgvUndirectedGraph;
        public space.earlygrey.simplegraphs.utils.Heuristic<Vector2> sgvHeu;

        public graph.sg.DirectedGraph<com.github.yellowstonegames.grid.Coord> updateDirectedGraph;
        public graph.sg.UndirectedGraph<com.github.yellowstonegames.grid.Coord> updateUndirectedGraph;
        public graph.sg.Heuristic<com.github.yellowstonegames.grid.Coord> updateHeu;

        public com.github.tommyettinger.gand.DirectedGraph<com.github.yellowstonegames.grid.Coord> gandDirectedGraph;
        public com.github.tommyettinger.gand.UndirectedGraph<com.github.yellowstonegames.grid.Coord> gandUndirectedGraph;
        public com.github.tommyettinger.gand.utils.Heuristic<com.github.yellowstonegames.grid.Coord> gandHeu;

        public com.github.tommyettinger.gand.DirectedGraph<GridPoint2> ggpDirectedGraph;
        public com.github.tommyettinger.gand.UndirectedGraph<GridPoint2> ggpUndirectedGraph;
        public com.github.tommyettinger.gand.utils.Heuristic<GridPoint2> ggpHeu;

        public com.github.tommyettinger.gand.DirectedGraph<Vector2> gvDirectedGraph;
        public com.github.tommyettinger.gand.UndirectedGraph<Vector2> gvUndirectedGraph;
        public com.github.tommyettinger.gand.utils.Heuristic<Vector2> gvHeu;

        public com.github.tommyettinger.gand.Int2DirectedGraph gi2DirectedGraph;
        public com.github.tommyettinger.gand.Int2UndirectedGraph gi2UndirectedGraph;
        public com.github.tommyettinger.gand.DirectedGraph<PointI2> ggeni2DirectedGraph;
        public com.github.tommyettinger.gand.UndirectedGraph<PointI2> ggeni2UndirectedGraph;
        public com.github.tommyettinger.gand.utils.Heuristic<PointI2> giHeu;

        public com.github.tommyettinger.gand.Float2DirectedGraph gf2DirectedGraph;
        public com.github.tommyettinger.gand.Float2UndirectedGraph gf2UndirectedGraph;
        public com.github.tommyettinger.gand.DirectedGraph<PointF2> ggenf2DirectedGraph;
        public com.github.tommyettinger.gand.UndirectedGraph<PointF2> ggenf2UndirectedGraph;
        public com.github.tommyettinger.gand.utils.Heuristic<PointF2> gfHeu;

        public com.github.tommyettinger.gand.DirectedGraph<com.github.yellowstonegames.grid.Coord> ggenCoordDirectedGraph;
        public com.github.tommyettinger.gand.UndirectedGraph<com.github.yellowstonegames.grid.Coord> ggenCoordUndirectedGraph;
        public com.github.tommyettinger.gand.utils.Heuristic<com.github.yellowstonegames.grid.Coord> gCoordHeu;


//        public graph.sg.DirectedGraph<GridPoint2> upgpDirectedGraph;
//        public graph.sg.UndirectedGraph<GridPoint2> upgpUndirectedGraph;
//        public graph.sg.Heuristic<GridPoint2> upgpHeu;

        public squidpony.squidai.graph.DirectedGraph<Coord> squidDirectedGraph;
        public squidpony.squidai.graph.UndirectedGraph<Coord> squidUndirectedGraph;
        public squidpony.squidai.graph.DefaultGraph squidDefaultGraph;
        public squidpony.squidai.graph.CostlyGraph squidCostlyGraph;

        public com.github.yellowstonegames.path.DirectedGraph<com.github.yellowstonegames.grid.Coord> squadDirectedGraph;
        public com.github.yellowstonegames.path.UndirectedGraph<com.github.yellowstonegames.grid.Coord> squadUndirectedGraph;
        public com.github.yellowstonegames.path.DefaultGraph squadDefaultGraph;
        public com.github.yellowstonegames.path.CostlyGraph squadCostlyGraph;

        public NateStar nate;

        public Coord rejectionSample(int x, int y) {
            int cx, cy;
            do {
                cx = srng.nextInt(17) - 8 + x;
                cy = srng.nextInt(17) - 8 + y;
            } while (cx <= 0 || cy <= 0 || cx >= WIDTH - 1 || cy >= HEIGHT - 1 || (x == cx && y == cy) || map[cx][cy] == '#');
            return Coord.get(cx, cy);
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
            long previousTime = System.nanoTime();
            System.out.printf("Starting took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            Coord.expandPoolTo(WIDTH, HEIGHT);
            com.github.yellowstonegames.grid.Coord.expandPoolTo(WIDTH, HEIGHT);
            System.out.printf("Coord pools filled took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            map = dungeonGen.generate();
//            System.out.println();
//            DungeonUtility.debugPrint(map);
//            System.out.println();
            System.out.printf("Generated map took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            floors = new GreasedRegion(map, '.');
            floorCount = floors.size();
            floorArray = floors.asCoords();

            gpFloors = new ArrayList<>(floorCount);
            vFloors = new ArrayList<>(floorCount);
            iFloors = new ArrayList<>(floorCount);
            fFloors = new ArrayList<>(floorCount);
            for (int i = 0; i < floorCount; i++) {
                gpFloors.add(new GridPoint2(floorArray[i].x, floorArray[i].y));
                vFloors.add(new Vector2(floorArray[i].x, floorArray[i].y));
                iFloors.add(new PointI2(floorArray[i].x, floorArray[i].y));
                fFloors.add(new PointF2(floorArray[i].x, floorArray[i].y));
            }

            squadFloors = new Region(map, '.');
            squadFloorArray = squadFloors.asCoords();
            System.out.printf("Region stuff took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));

            lowest = floors.first();
            highest = floors.last();
            squadLowest = com.github.yellowstonegames.grid.Coord.get(lowest.x, lowest.y);
            squadHighest = com.github.yellowstonegames.grid.Coord.get(highest.x, highest.y);
            lowestGP = new GridPoint2(lowest.x, lowest.y);
            highestGP = new GridPoint2(highest.x, highest.y);
            lowestV = new Vector2(lowest.x, lowest.y);
            highestV = new Vector2(highest.x, highest.y);
            lowestI = new PointI2(lowest.x, lowest.y);
            highestI = new PointI2(highest.x, highest.y);
            lowestF = new PointF2(lowest.x, lowest.y);
            highestF = new PointF2(highest.x, highest.y);
            System.out.println("[INFO] Floors: " + floorCount);
            System.out.println("[INFO] Percentage walkable: " + floorCount * 100.0 / (WIDTH * HEIGHT) + "%");
            astarMap = DungeonUtility.generateAStarCostMap(map, Collections.emptyMap(), 1);
            squadAstarMap = new float[WIDTH][HEIGHT];
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    squadAstarMap[x][y] = (float) astarMap[x][y];
                }
            }
            System.out.printf("AStar took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            as = new AStarSearch(astarMap, AStarSearch.SearchType.CHEBYSHEV);
            nearbyMap = new Coord[WIDTH][HEIGHT];
            gpNearbyMap = new GridPoint2[WIDTH][HEIGHT];
            squadNearbyMap = new com.github.yellowstonegames.grid.Coord[WIDTH][HEIGHT];
            customNearbyMap = new int[WIDTH * HEIGHT];
            vNearbyMap = new Vector2[WIDTH][HEIGHT];
            iNearbyMap = new PointI2[WIDTH][HEIGHT];
            fNearbyMap = new PointF2[WIDTH][HEIGHT];
            tmp = new GreasedRegion(WIDTH, HEIGHT);
            adj = new Adjacency.BasicAdjacency(WIDTH, HEIGHT, CHEBYSHEV);
            adj.blockingRule = 0;
            srng = new StatefulRNG(0x1337BEEF1337CA77L);
            Coord c;
            for (int i = 1; i < WIDTH - 1; i++) {
                for (int j = 1; j < HEIGHT - 1; j++) {
                    if (map[i][j] == '#')
                        continue;
                    c = rejectionSample(i, j);
//                    c = floodSample(i, j);
                    nearbyMap[i][j] = c;
                    gpNearbyMap[i][j] = new GridPoint2(c.x, c.y);
                    squadNearbyMap[i][j] = com.github.yellowstonegames.grid.Coord.get(c.x, c.y);
                    vNearbyMap[i][j] = new Vector2(c.x, c.y);
                    iNearbyMap[i][j] = new PointI2(c.x, c.y);
                    fNearbyMap[i][j] = new PointF2(c.x, c.y);
                    customNearbyMap[adj.composite(i, j, 0, 0)] = adj.composite(c.x, c.y, 0, 0);
                }
            }
            System.out.printf("Tiny paths took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            dijkstra = new DijkstraMap(map, CHEBYSHEV, new StatefulRNG(0x1337BEEF));
            dijkstra.setBlockingRequirement(0);
            squadDijkstra = new com.github.yellowstonegames.path.DijkstraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
            squadDijkstra.setBlockingRequirement(0);
            squadDextra = new squid.squad.DextraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
            squadDextra.setBlockingRequirement(0);
//            bitDijkstra = new BitDijkstraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
//            bitDijkstra.setBlockingRequirement(0);
//            cDijkstra = new CDijkstraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
//            cDijkstra.setBlockingRequirement(0);
            customDijkstra = new CustomDijkstraMap(map, adj, new StatefulRNG(0x1337BEEF));
            gradientGrid = new GradientGridI2(map, GridMetric.CHEBYSHEV);
            gradientGrid.setBlockingRequirement(0);
            System.out.printf("Dijkstra maps took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            gg = new GridGraphGP(floors, WIDTH, HEIGHT);
            gg2 = new GridGraphCoord(floors, WIDTH, HEIGHT);
            astar = new IndexedAStarPathFinder<>(gg, false, GridPoint2::equals);
            astar2 = new IndexedAStarPathFinder<>(gg2, false, Coord::equals);
            System.out.printf("AStar finders made took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            dgp = new DefaultGraphPath<>(WIDTH + HEIGHT << 1);
            dgpgp = new DefaultGraphPath<>(WIDTH + HEIGHT << 1);
            path = new ArrayList<>(WIDTH + HEIGHT << 1);
            squadPath = new ObjectDeque<>(WIDTH + HEIGHT << 1);
            simplePath = new Path<>(WIDTH + HEIGHT << 1);
            updatePath = new graph.sg.Path<>(WIDTH + HEIGHT << 1);
            gandPath = new com.github.tommyettinger.gand.Path<>(WIDTH + HEIGHT << 1);
            ggpPath = new com.github.tommyettinger.gand.Path<>(WIDTH + HEIGHT << 1);
            gvPath = new com.github.tommyettinger.gand.Path<>(WIDTH + HEIGHT << 1);
            giPath = new com.github.tommyettinger.gand.Path<>(WIDTH + HEIGHT << 1);
            gfPath = new com.github.tommyettinger.gand.Path<>(WIDTH + HEIGHT << 1);
            gCoordPath = new com.github.tommyettinger.gand.Path<>(WIDTH + HEIGHT << 1);
//            upgpPath = new graph.sg.Path<>(WIDTH + HEIGHT << 1);
            sggpPath = new Path<>(WIDTH + HEIGHT << 1);
            sgvPath = new Path<>(WIDTH + HEIGHT << 1);
            System.out.printf("Paths made took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            simpleDirectedGraph = new DirectedGraph<>(squadFloors);
            simpleUndirectedGraph = new UndirectedGraph<>(squadFloors);
            simpleHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            sggpDirectedGraph = new DirectedGraph<>(gpFloors);
            sggpUndirectedGraph = new UndirectedGraph<>(gpFloors);
            sggpHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            sgvDirectedGraph = new DirectedGraph<>(vFloors);
            sgvUndirectedGraph = new UndirectedGraph<>(vFloors);
            sgvHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            System.out.printf("Simple finders took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));

            updateDirectedGraph = new graph.sg.DirectedGraph<>(squadFloors);
            updateUndirectedGraph = new graph.sg.UndirectedGraph<>(squadFloors);
            updateHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));


//            upgpPath = new graph.sg.Path<>(WIDTH + HEIGHT << 1);
//            upgpDirectedGraph = new graph.sg.DirectedGraph<>(gpFloors);
//            upgpUndirectedGraph = new graph.sg.UndirectedGraph<>(gpFloors);
//            upgpHeu = (currentNode, targetNode) ->
//                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));
            System.out.printf("Update finders took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));

            gandDirectedGraph = new com.github.tommyettinger.gand.DirectedGraph<>(squadFloors);
            gandUndirectedGraph = new com.github.tommyettinger.gand.UndirectedGraph<>(squadFloors);
            gandHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            ggpDirectedGraph = new com.github.tommyettinger.gand.DirectedGraph<>(gpFloors);
            ggpUndirectedGraph = new com.github.tommyettinger.gand.UndirectedGraph<>(gpFloors);
            ggpHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            gvDirectedGraph = new com.github.tommyettinger.gand.DirectedGraph<>(vFloors);
            gvUndirectedGraph = new com.github.tommyettinger.gand.UndirectedGraph<>(vFloors);
            gvHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            gi2DirectedGraph = new com.github.tommyettinger.gand.Int2DirectedGraph(iFloors);
            gi2UndirectedGraph = new com.github.tommyettinger.gand.Int2UndirectedGraph(iFloors);
            ggeni2DirectedGraph = new com.github.tommyettinger.gand.DirectedGraph<>(iFloors);
            ggeni2UndirectedGraph = new com.github.tommyettinger.gand.UndirectedGraph<>(iFloors);
            giHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));
            gf2DirectedGraph = new com.github.tommyettinger.gand.Float2DirectedGraph(fFloors);
            gf2UndirectedGraph = new com.github.tommyettinger.gand.Float2UndirectedGraph(fFloors);
            ggenf2DirectedGraph = new com.github.tommyettinger.gand.DirectedGraph<>(fFloors);
            ggenf2UndirectedGraph = new com.github.tommyettinger.gand.UndirectedGraph<>(fFloors);

            gfHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            ggenCoordDirectedGraph = new com.github.tommyettinger.gand.DirectedGraph<>(squadFloors);
            ggenCoordUndirectedGraph = new com.github.tommyettinger.gand.UndirectedGraph<>(squadFloors);
            gCoordHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            System.out.printf("Gand finders took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));

            squidDirectedGraph = new squidpony.squidai.graph.DirectedGraph<>(floors);
            squidUndirectedGraph = new squidpony.squidai.graph.UndirectedGraph<>(floors);
            squidDefaultGraph = new squidpony.squidai.graph.DefaultGraph(map, true);
            squidCostlyGraph = new squidpony.squidai.graph.CostlyGraph(astarMap, true);

            System.out.printf("Squid finders took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));

            squadDirectedGraph = new com.github.yellowstonegames.path.DirectedGraph<>(squadFloors);
            squadUndirectedGraph = new com.github.yellowstonegames.path.UndirectedGraph<>(squadFloors);
            squadDefaultGraph = new com.github.yellowstonegames.path.DefaultGraph(map, true);
            squadCostlyGraph = new com.github.yellowstonegames.path.CostlyGraph(squadAstarMap, true);
            System.out.printf("Squad finders took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));

            Coord center;
            GridPoint2 gpCenter;
            Vector2 vCenter;
            PointI2 iCenter;
            PointF2 fCenter;
            com.github.yellowstonegames.grid.Coord squadCenter, squadMoved;
            Direction[] outer = Direction.CLOCKWISE;
            Direction dir;
            for (int i = floorCount - 1; i >= 0; i--) {
                center = floorArray[i];
                squadCenter = com.github.yellowstonegames.grid.Coord.get(center.x, center.y);
                gpCenter = gpFloors.get(i);
                vCenter = vFloors.get(i);
                iCenter = iFloors.get(i);
                fCenter = fFloors.get(i);
                for (int j = 0; j < 8; j++) {
                    dir = outer[j];
                    if (floors.contains(center.x + dir.deltaX, center.y + dir.deltaY)) {
                        GridPoint2 gpMoved = new GridPoint2(gpCenter).add(dir.deltaX, dir.deltaY);
                        Vector2 vMoved = new Vector2(vCenter).add(dir.deltaX, dir.deltaY);
                        PointI2 iMoved = new PointI2(iCenter);
                        iMoved.add(dir.deltaX, dir.deltaY);
                        PointF2 fMoved = new PointF2(vCenter);
                        fMoved.add(dir.deltaX, dir.deltaY);
                        squadMoved = com.github.yellowstonegames.grid.Coord.get(center.x + dir.deltaX, center.y + dir.deltaY);
                        simpleDirectedGraph.addEdge(squadCenter, squadMoved);
                        sggpDirectedGraph.addEdge(gpCenter, gpMoved);
                        ggpDirectedGraph.addEdge(gpCenter, gpMoved);
                        sgvDirectedGraph.addEdge(vCenter, vMoved);
                        gvDirectedGraph.addEdge(vCenter, vMoved);
                        gi2DirectedGraph.addEdge(iCenter, iMoved);
                        gf2DirectedGraph.addEdge(fCenter, fMoved);
                        ggeni2DirectedGraph.addEdge(iCenter, iMoved);
                        ggenf2DirectedGraph.addEdge(fCenter, fMoved);
                        ggenCoordDirectedGraph.addEdge(squadCenter, squadMoved);
                        updateDirectedGraph.addEdge(squadCenter, squadMoved);
                        gandDirectedGraph.addEdge(squadCenter, squadMoved);
//                        upgpDirectedGraph.addEdge(gpCenter, gpMoved);
                        squidDirectedGraph.addEdge(center, center.translate(dir));
                        squadDirectedGraph.addEdge(squadCenter, squadMoved);
                        if (!simpleUndirectedGraph.edgeExists(squadCenter, squadMoved)) {
                            simpleUndirectedGraph.addEdge(squadCenter, squadMoved);
                            squidUndirectedGraph.addEdge(center, center.translate(dir));
                            updateUndirectedGraph.addEdge(squadCenter, squadMoved);
                            gandUndirectedGraph.addEdge(squadCenter, squadMoved);
//                            upgpUndirectedGraph.addEdge(gpCenter, gpMoved);
                            squadUndirectedGraph.addEdge(squadCenter, squadMoved);
                            sggpUndirectedGraph.addEdge(gpCenter, gpMoved);
                            ggpUndirectedGraph.addEdge(gpCenter, gpMoved);
                            sgvUndirectedGraph.addEdge(vCenter, vMoved);
                            gvUndirectedGraph.addEdge(vCenter, vMoved);
                            gi2UndirectedGraph.addEdge(iCenter, iMoved);
                            gf2UndirectedGraph.addEdge(fCenter, fMoved);
                            ggeni2UndirectedGraph.addEdge(iCenter, iMoved);
                            ggenf2UndirectedGraph.addEdge(fCenter, fMoved);
                            ggenCoordUndirectedGraph.addEdge(squadCenter, squadMoved);
                        }
                    }
                }
            }
            System.out.printf("Edges took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
            nate = new NateStar(WIDTH, HEIGHT) {
                @Override
                protected boolean isValid(int x, int y) {
                    return floors.contains(x, y);
                }
            };
            System.out.printf("Nate took %g\n", (double)(-previousTime + (previousTime = System.nanoTime())));
        }

    }

    @Benchmark
    public long doScanDijkstra(BenchmarkState state) {
        long scanned = 0;
        final DijkstraMap dijkstra = state.dijkstra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                dijkstra.setGoal(x, y);
                dijkstra.scan(null, null);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned++;
            }
        }
        return scanned;
    }


//    @Benchmark
    public long doScanCustomDijkstra(BenchmarkState state) {
        CustomDijkstraMap dijkstra = state.customDijkstra;
        long scanned = 0;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                dijkstra.setGoal(state.adj.composite(x, y, 0, 0));
                dijkstra.scan(null);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathDijkstra(BenchmarkState state) {
        Coord r;
        final Coord[] tgts = new Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DijkstraMap dijkstra = state.dijkstra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.floorArray);
                tgts[0] = Coord.get(x, y);
                state.path.clear();
                dijkstra.findPath(state.path, PATH_LENGTH, -1, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathDijkstra(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        final Coord[] tgts = new Coord[1];
        final DijkstraMap dijkstra = state.dijkstra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.nearbyMap[x][y];
                tgts[0] = Coord.get(x, y);
                //dijkstra.partialScan(r,9, null);
                state.path.clear();
                dijkstra.findPath(state.path, 9, 9, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneDijkstra(BenchmarkState state) {
        final DijkstraMap dijkstra = state.dijkstra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        Coord r = state.lowest;
        state.path.clear();
        dijkstra.findPath(state.path, PATH_LENGTH, -1, null, null, r, tgt);
        dijkstra.clearGoals();
        dijkstra.resetMap();
        return state.path.size();
    }

    @Benchmark
    public long doPathSquadDijkstra(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.yellowstonegames.path.DijkstraMap dijkstra = state.squadDijkstra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.squadFloorArray);
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                state.squadPath.clear();
                dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadDijkstra(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        final com.github.yellowstonegames.path.DijkstraMap dijkstra = state.squadDijkstra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.squadNearbyMap[x][y];
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                //dijkstra.partialScan(r,9, null);
                state.squadPath.clear();
                dijkstra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadDijkstra(BenchmarkState state) {
        final com.github.yellowstonegames.path.DijkstraMap dijkstra = state.squadDijkstra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
        state.squadPath.clear();
        dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
        dijkstra.clearGoals();
        dijkstra.resetMap();
        return state.squadPath.size();
    }

    @Benchmark
    public long doScanSquadDijkstra(BenchmarkState state) {
        long scanned = 0;
        final com.github.yellowstonegames.path.DijkstraMap dijkstra = state.squadDijkstra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                dijkstra.setGoal(x, y);
                dijkstra.scan(null, null);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned++;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSquadDextra(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squid.squad.DextraMap dextra = state.squadDextra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dextra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.squadFloorArray);
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                state.squadPath.clear();
                dextra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
                dextra.clearGoals();
                dextra.resetMap();
                scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadDextra(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        final squid.squad.DextraMap dextra = state.squadDextra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dextra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.squadNearbyMap[x][y];
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                //dextra.partialScan(r,9, null);
                state.squadPath.clear();
                dextra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
                dextra.clearGoals();
                dextra.resetMap();
                scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadDextra(BenchmarkState state) {
        final squid.squad.DextraMap dextra = state.squadDextra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
        state.squadPath.clear();
        dextra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
        dextra.clearGoals();
        dextra.resetMap();
        return state.squadPath.size();
    }

    @Benchmark
    public long doScanSquadDextra(BenchmarkState state) {
        long scanned = 0;
        final squid.squad.DextraMap dijkstra = state.squadDextra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                dijkstra.setGoal(x, y);
                dijkstra.scan(null, null);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned++;
            }
        }
        return scanned;
    }

//
//    @Benchmark
//    public long doPathBitDijkstra(BenchmarkState state) {
//        com.github.yellowstonegames.grid.Coord r;
//        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
//        long scanned = 0;
//        state.srng.setState(1234567890L);
//        final BitDijkstraMap dijkstra = state.bitDijkstra;
//        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
//                r = state.srng.getRandomElement(state.squadFloorArray);
//                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
//                state.path.clear();
//                dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
//                dijkstra.clearGoals();
//                dijkstra.resetMap();
//                scanned += state.path.size();
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doTinyPathBitDijkstra(BenchmarkState state) {
//        com.github.yellowstonegames.grid.Coord r;
//        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
//        long scanned = 0;
//        final BitDijkstraMap dijkstra = state.bitDijkstra;
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
//                r = state.squadNearbyMap[x][y];
//                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
//                //dijkstra.partialScan(r,9, null);
//                state.path.clear();
//                dijkstra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
//                dijkstra.clearGoals();
//                dijkstra.resetMap();
//                scanned += state.path.size();
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doOneBitDijkstra(BenchmarkState state) {
//        final BitDijkstraMap dijkstra = state.bitDijkstra;
//        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
//        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
//        state.srng.setState(state.highest.hashCode());
//        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
//        state.path.clear();
//        dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
//        dijkstra.clearGoals();
//        dijkstra.resetMap();
//        return state.path.size();
//    }
//
//
//    @Benchmark
//    public long doPathCDijkstra(BenchmarkState state) {
//        com.github.yellowstonegames.grid.Coord r;
//        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
//        long scanned = 0;
//        state.srng.setState(1234567890L);
//        final CDijkstraMap dijkstra = state.cDijkstra;
//        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
//                r = state.srng.getRandomElement(state.squadFloorArray);
//                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
//                state.path.clear();
//                dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
//                dijkstra.clearGoals();
//                dijkstra.resetMap();
//                scanned += state.path.size();
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doTinyPathCDijkstra(BenchmarkState state) {
//        com.github.yellowstonegames.grid.Coord r;
//        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
//        long scanned = 0;
//        final CDijkstraMap dijkstra = state.cDijkstra;
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
//                r = state.squadNearbyMap[x][y];
//                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
//                //dijkstra.partialScan(r,9, null);
//                state.path.clear();
//                dijkstra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
//                dijkstra.clearGoals();
//                dijkstra.resetMap();
//                scanned += state.path.size();
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doOneCDijkstra(BenchmarkState state) {
//        final CDijkstraMap dijkstra = state.cDijkstra;
//        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
//        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
//        state.srng.setState(state.highest.hashCode());
//        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
//        state.squadPath.clear();
//        dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
//        dijkstra.clearGoals();
//        dijkstra.resetMap();
//        return state.squadPath.size();
//    }

//    @Benchmark
    public long doPathCustomDijkstra(BenchmarkState state) {
        Coord r;
        int[] tgts = new int[1];
        long scanned = 0;
        int p;
        state.srng.setState(1234567890L);
        CustomDijkstraMap dijkstra = state.customDijkstra;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.floorArray);
                p = state.adj.composite(r.x, r.y, 0, 0);
                tgts[0] = state.adj.composite(x, y, 0, 0);
                dijkstra.findPath(PATH_LENGTH, null, null, p, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += dijkstra.path.size;
            }
        }
        return scanned;
    }

//    @Benchmark
    public long doTinyPathCustomDijkstra(BenchmarkState state) {
        Coord r;
        int[] tgts = new int[1];
        long scanned = 0;
        int p;
        CustomDijkstraMap dijkstra = state.customDijkstra;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.nearbyMap[x][y];
                p = state.adj.composite(r.x, r.y, 0, 0);
                tgts[0] = state.adj.composite(x, y, 0, 0);
                dijkstra.findPath(9, 9, null, null, p, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += dijkstra.path.size;
            }
        }
        return scanned;
    }


    @Benchmark
    public long doPathAStarSearch(BenchmarkState state) {
        Coord r;
        Coord tgt;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final AStarSearch aStarSearch = state.as;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.floorArray);
                tgt = Coord.get(x, y);
                state.path.clear();
                state.path.addAll(aStarSearch.path(r, tgt));
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathAStarSearch(BenchmarkState state) {
        Coord r;
        Coord tgt;
        long scanned = 0;
        final AStarSearch aStarSearch = state.as;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.nearbyMap[x][y];
                tgt = Coord.get(x, y);
                state.path.clear();
                state.path.addAll(aStarSearch.path(r, tgt));
                scanned += state.path.size();
            }
        }
        return scanned;
    }


//    public long doPathAStar()
//    {
//        AStarSearch astar = new AStarSearch(astarMap, AStarSearch.SearchType.CHEBYSHEV);
//        Coord r;
//        long scanned = 0;
//        DungeonUtility utility = new DungeonUtility(new StatefulRNG(0x1337BEEFDEAL));
//        Queue<Coord> latestPath;
//        for (int x = 1; x < DIMENSION - 1; x++) {
//            for (int y = 1; y < DIMENSION - 1; y++) {
//                if (map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                utility.rng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                r = floors.singleRandom(utility.rng);
//                latestPath = astar.path(r, Coord.get(x, y));
//                scanned+= latestPath.size();
//            }
//        }
//        return scanned;
//    }
//
//    //@Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void measurePathAStar() throws InterruptedException {
//        System.out.println(doPathAStar() / floorCount);
//        doPathAStar();
//    }
//
//    public long doPathAStar2()
//    {
//        AStarSearch astar = new AStarSearch(astarMap, AStarSearch.SearchType.CHEBYSHEV);
//        Coord r;
//        long scanned = 0;
//        DungeonUtility utility = new DungeonUtility(new StatefulRNG(0x1337BEEFDEAL));
//        Queue<Coord> latestPath;
//        for (int x = 1; x < DIMENSION - 1; x++) {
//            for (int y = 1; y < DIMENSION - 1; y++) {
//                if (map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                utility.rng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                r = floors.singleRandom(utility.rng);
//                latestPath = astar.path(r, Coord.get(x, y));
//                scanned+= latestPath.size();
//            }
//        }
//        return scanned;
//    }
//
//    //@Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void measurePathAStar2() throws InterruptedException {
//        //System.out.println(doPathAStar2());
//        doPathAStar2();
//    }
//
//    public long doTinyPathAStar2()
//    {
//        AStarSearch astar = new AStarSearch(astarMap, AStarSearch.SearchType.CHEBYSHEV);
//        Coord r;
//        long scanned = 0;
//        DungeonUtility utility = new DungeonUtility(new StatefulRNG(0x1337BEEFDEAL));
//        Queue<Coord> latestPath;
//        for (int x = 1; x < DIMENSION - 1; x++) {
//            for (int y = 1; y < DIMENSION - 1; y++) {
//                if (map[x][y] == '#')
//                    continue;
//                // this should ensure no blatant correlation between R and W
//                utility.rng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
//                r = nearbyMap[x][y];
//                latestPath = astar.path(r, Coord.get(x, y));
//                scanned += latestPath.size();
//            }
//        }
//        return scanned;
//    }
//
//    //@Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void measureTinyPathAStar2() throws InterruptedException {
//        System.out.println(doTinyPathAStar2() / floorCount);
//        doTinyPathAStar2();
//    }

    public static class GridGraphGP implements IndexedGraph<GridPoint2> {
        public int[][] indices;
        public GridPoint2[][] grid;
        public int nodeCount;
        public Heuristic<GridPoint2> heu = (node, endNode) ->
                Math.max(Math.abs(node.x - endNode.x), Math.abs(node.y - endNode.y));


        public GridGraphGP(Iterable<Coord> floors, int width, int height) {
            grid = new GridPoint2[width][height];
            indices = new int[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    indices[x][y] = -1;
                }
            }
            int i = 0;
            for (Coord c : floors) {
                GridPoint2 pt = new GridPoint2(c.x, c.y);
                grid[c.x][c.y] = pt;
                indices[c.x][c.y] = i++;
            }
            nodeCount = i;
        }

        // use this if using only libGDX classes.
//        public GridGraphGP(Iterable<GridPoint2> floors, int width, int height) {
//            grid = new GridPoint2[width][height];
//            indices = new int[width][height];
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    indices[x][y] = -1;
//                }
//            }
//            int i = 0;
//            for (GridPoint2 c : floors) {
//                GridPoint2 pt = new GridPoint2(c.x, c.y);
//                grid[c.x][c.y] = pt;
//                indices[c.x][c.y] = i++;
//            }
//            nodeCount = i;
//        }
        @Override
        public int getIndex(GridPoint2 node) {
            return indices[node.x][node.y];
        }

        @Override
        public int getNodeCount() {
            return nodeCount;
        }

        @Override
        public Array<Connection<GridPoint2>> getConnections(GridPoint2 fromNode) {
            Array<Connection<GridPoint2>> conn = new Array<>(false, 8);
            if (indices[fromNode.x][fromNode.y] == -1)
                return conn;
            for (int i = 0; i < 8; i++) {
                int x = fromNode.x + Direction.OUTWARDS[i].deltaX, y = fromNode.y + Direction.OUTWARDS[i].deltaY;
                if (x >= 0 && y >= 0 && x < indices.length && y < indices[0].length && indices[x][y] != -1)
                    conn.add(new DefaultConnection<>(fromNode, new GridPoint2(x, y)));
            }
            return conn;
        }
    }


    public static class GridGraphCoord implements IndexedGraph<Coord> {
        public int[][] map;
        int nodeCount;
        public Heuristic<Coord> heu = (node, endNode) ->
                Math.max(Math.abs(node.x - endNode.x), Math.abs(node.y - endNode.y));

        public GridGraphCoord(Iterable<Coord> floors, int width, int height) {
            this.map = ArrayTools.fill(-1, width, height);
            int i = 0;
            for (Coord c : floors) {
                map[c.x][c.y] = i++;
            }
            nodeCount = i;
        }

        @Override
        public int getIndex(Coord node) {
            return map[node.x][node.y];
        }

        @Override
        public int getNodeCount() {
            return nodeCount;
        }

        @Override
        public Array<Connection<Coord>> getConnections(Coord fromNode) {
            Array<Connection<Coord>> conn = new Array<>(false, 8);
            if (map[fromNode.x][fromNode.y] == -1)
                return conn;
            for (int i = 0; i < 8; i++) {
                int x = fromNode.x + Direction.OUTWARDS[i].deltaX, y = fromNode.y + Direction.OUTWARDS[i].deltaY;
                if (x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] != -1)
                    conn.add(new DefaultConnection<>(fromNode, Coord.get(x, y)));
            }
            return conn;
        }
    }

    @Benchmark
    public long doPathGDXAStarGP(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.dgpgp.clear();
                if (state.astar.searchNodePath(state.gg.grid[start.x][start.y], state.gg.grid[x][y], state.gg.heu, state.dgpgp))
                    scanned += state.dgpgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathGDXAStarGP(BenchmarkState state) {
        long scanned = 0;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.dgpgp.clear();
                if (state.astar.searchNodePath(state.gg.grid[start.x][start.y], state.gg.grid[x][y], state.gg.heu, state.dgpgp))
                    scanned += state.dgpgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doOneGDXAStarGP(BenchmarkState state) {
        Coord tgt = state.highest;
        state.dgpgp.clear();
        start.set(state.lowest.x, state.lowest.y);
        state.astar.searchNodePath(state.gg.grid[start.x][start.y], state.gg.grid[tgt.x][tgt.y], state.gg.heu, state.dgpgp);
        return state.dgpgp.getCount();
    }

    @Benchmark
    public long doPathGDXAStarCoord(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.dgp.clear();
                if (state.astar2.searchNodePath(r, Coord.get(x, y), state.gg2.heu, state.dgp))
                    scanned += state.dgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathGDXAStarCoord(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.dgp.clear();
                if (state.astar2.searchNodePath(r, Coord.get(x, y), state.gg2.heu, state.dgp))
                    scanned += state.dgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doOneGDXAStarCoord(BenchmarkState state) {
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        Coord r = state.lowest;
        state.dgp.clear();
        state.astar2.searchNodePath(r, tgt, state.gg2.heu, state.dgp);
        return state.dgp.getCount();
    }

    @Benchmark
    public long doPathSimpleGPD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DirectedGraphAlgorithms<GridPoint2> algo = state.sggpDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
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
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
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
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
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
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
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
    public long doPathSimpleD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.simpleDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if (state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.simpleDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.squadNearbyMap[x][y];
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if (state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSimpleUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.simpleUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if (state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.simpleUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if (state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSimpleVD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DirectedGraphAlgorithms<Vector2> algo = state.sgvDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.srng.getRandomElement(state.vFloors));
                state.sgvPath.clear();
                endV.y = y;
                state.sgvPath.addAll(algo.findShortestPath(startV, endV, state.sgvHeu, SearchStep::vertex));
                if (state.sgvPath.size != 0)
                    scanned += state.sgvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleVD(BenchmarkState state) {
        long scanned = 0;
        final DirectedGraphAlgorithms<Vector2> algo = state.sgvDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.vNearbyMap[x][y]);
                state.sgvPath.clear();
                endV.y = y;
                state.sgvPath.addAll(algo.findShortestPath(startV, endV, state.sgvHeu, SearchStep::vertex));
                if (state.sgvPath.size != 0)
                    scanned += state.sgvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSimpleVD(BenchmarkState state) {
        final DirectedGraphAlgorithms<Vector2> algo = state.sgvDirectedGraph.algorithms();
        Vector2 startV = state.highestV;
        Vector2 endV = state.lowestV;
        state.sgvPath.clear();
        state.sgvPath.addAll(algo.findShortestPath(startV, endV, state.sgvHeu, SearchStep::vertex));
        return state.sgvPath.size();
    }

    @Benchmark
    public long doPathSimpleVUD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final UndirectedGraphAlgorithms<Vector2> algo = state.sgvUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.srng.getRandomElement(state.vFloors));
                state.sgvPath.clear();
                endV.y = y;
                state.sgvPath.addAll(algo.findShortestPath(startV, endV, state.sgvHeu, SearchStep::vertex));
                if (state.sgvPath.size != 0)
                    scanned += state.sgvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleVUD(BenchmarkState state) {
        long scanned = 0;
        final UndirectedGraphAlgorithms<Vector2> algo = state.sgvUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.vNearbyMap[x][y]);
                state.sgvPath.clear();
                endV.y = y;
                state.sgvPath.addAll(algo.findShortestPath(startV, endV, state.sgvHeu, SearchStep::vertex));
                if (state.sgvPath.size != 0)
                    scanned += state.sgvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSimpleVUD(BenchmarkState state) {
        final UndirectedGraphAlgorithms<Vector2> algo = state.sgvUndirectedGraph.algorithms();
        Vector2 startV = state.highestV;
        Vector2 endV = state.lowestV;
        state.sgvPath.clear();
        state.sgvPath.addAll(algo.findShortestPath(startV, endV, state.sgvHeu, SearchStep::vertex));
        return state.sgvPath.size();
    }

    // SQUIDLIB

    @Benchmark
    public long doPathSquidD(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidD(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquidD(BenchmarkState state) {
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidDirectedGraph.algorithms();
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        Coord r = state.lowest;
        state.path.clear();
        algo.findShortestPath(r, tgt, state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV);
        return state.path.size();
    }

    @Benchmark
    public long doPathSquidUD(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidUD(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquidUD(BenchmarkState state) {
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidUndirectedGraph.algorithms();
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        Coord r = state.lowest;
        state.path.clear();
        algo.findShortestPath(r, tgt, state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV);
        return state.path.size();
    }

    @Benchmark
    public long doPathSquidDG(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidDefaultGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidDG(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidDefaultGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquidDG(BenchmarkState state) {
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidDefaultGraph.algorithms();
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        Coord r = state.lowest;
        state.path.clear();
        algo.findShortestPath(r, tgt, state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV);
        return state.path.size();
    }

    @Benchmark
    public long doPathSquidCG(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidCostlyGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidCG(BenchmarkState state) {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidCostlyGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if (algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquidCG(BenchmarkState state) {
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidCostlyGraph.algorithms();
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        Coord r = state.lowest;
        state.path.clear();
        algo.findShortestPath(r, tgt, state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV);
        return state.path.size();
    }

    // SQUAD

    @Benchmark
    public long doPathSquadD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.yellowstonegames.path.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final com.github.yellowstonegames.path.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadD(BenchmarkState state) {
        final com.github.yellowstonegames.path.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadDirectedGraph.algorithms();
        com.github.yellowstonegames.grid.Coord tgt = state.squadHighest;
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = state.squadLowest;
        state.squadPath.clear();
        algo.findShortestPath(r, tgt, state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV);
        return state.squadPath.size();
    }

    @Benchmark
    public long doPathSquadUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.yellowstonegames.path.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final com.github.yellowstonegames.path.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadUD(BenchmarkState state) {
        final com.github.yellowstonegames.path.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadUndirectedGraph.algorithms();
        com.github.yellowstonegames.grid.Coord tgt = state.squadHighest;
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = state.squadLowest;
        state.squadPath.clear();
        algo.findShortestPath(r, tgt, state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV);
        return state.squadPath.size();
    }


    @Benchmark
    public long doPathSquadDG(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.yellowstonegames.path.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadDefaultGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadDG(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final com.github.yellowstonegames.path.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadDefaultGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadDG(BenchmarkState state) {
        final com.github.yellowstonegames.path.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadDefaultGraph.algorithms();
        com.github.yellowstonegames.grid.Coord tgt = state.squadHighest;
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = state.squadLowest;
        state.squadPath.clear();
        algo.findShortestPath(r, tgt, state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV);
        return state.squadPath.size();
    }

    @Benchmark
    public long doPathSquadCG(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.yellowstonegames.path.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadCostlyGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadCG(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final com.github.yellowstonegames.path.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadCostlyGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.squadPath.clear();
                if (algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV))
                    scanned += state.squadPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadCG(BenchmarkState state) {
        final com.github.yellowstonegames.path.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.squadCostlyGraph.algorithms();
        com.github.yellowstonegames.grid.Coord tgt = state.squadHighest;
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = state.squadLowest;
        state.squadPath.clear();
        algo.findShortestPath(r, tgt, state.squadPath, com.github.yellowstonegames.path.Heuristic.CHEBYSHEV);
        return state.squadPath.size();
    }

    // UPDATE
    
//    @Benchmark
//    public long doPathUpdateGPD(BenchmarkState state) {
//        long scanned = 0;
//        state.srng.setState(1234567890L);
//        final graph.sg.algorithms.DirectedGraphAlgorithms<GridPoint2> algo = state.upgpDirectedGraph.algorithms();
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            end.x = x;
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                start.set(state.srng.getRandomElement(state.gpFloors));
//                state.upgpPath.clear();
//                end.y = y;
//                state.upgpPath.addAll(algo.findShortestPath(start, end, state.upgpHeu, graph.sg.algorithms.SearchStep::vertex));
//                if (state.upgpPath.size != 0)
//                    scanned += state.upgpPath.size;
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doTinyPathUpdateGPD(BenchmarkState state) {
//        long scanned = 0;
//        final graph.sg.algorithms.DirectedGraphAlgorithms<GridPoint2> algo = state.upgpDirectedGraph.algorithms();
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            end.x = x;
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                start.set(state.gpNearbyMap[x][y]);
//                state.upgpPath.clear();
//                end.y = y;
//                state.upgpPath.addAll(algo.findShortestPath(start, end, state.upgpHeu, graph.sg.algorithms.SearchStep::vertex));
//                if (state.upgpPath.size != 0)
//                    scanned += state.upgpPath.size;
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doOneUpdateGPD(BenchmarkState state) {
//        final graph.sg.algorithms.DirectedGraphAlgorithms<GridPoint2> algo = state.upgpDirectedGraph.algorithms();
//        GridPoint2 start = state.highestGP;
//        GridPoint2 end = state.lowestGP;
//        state.upgpPath.clear();
//        state.upgpPath.addAll(algo.findShortestPath(start, end, state.upgpHeu, graph.sg.algorithms.SearchStep::vertex));
//        return state.upgpPath.size();
//    }
//
//    @Benchmark
//    public long doPathUpdateGPUD(BenchmarkState state) {
//        long scanned = 0;
//        state.srng.setState(1234567890L);
//        final graph.sg.algorithms.UndirectedGraphAlgorithms<GridPoint2> algo = state.upgpUndirectedGraph.algorithms();
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            end.x = x;
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                start.set(state.srng.getRandomElement(state.gpFloors));
//                state.upgpPath.clear();
//                end.y = y;
//                state.upgpPath.addAll(algo.findShortestPath(start, end, state.upgpHeu, graph.sg.algorithms.SearchStep::vertex));
//                if (state.upgpPath.size != 0)
//                    scanned += state.upgpPath.size;
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doTinyPathUpdateGPUD(BenchmarkState state) {
//        long scanned = 0;
//        final graph.sg.algorithms.UndirectedGraphAlgorithms<GridPoint2> algo = state.upgpUndirectedGraph.algorithms();
//        for (int x = 1; x < state.WIDTH - 1; x++) {
//            end.x = x;
//            for (int y = 1; y < state.HEIGHT - 1; y++) {
//                if (state.map[x][y] == '#')
//                    continue;
//                start.set(state.gpNearbyMap[x][y]);
//                state.upgpPath.clear();
//                end.y = y;
//                state.upgpPath.addAll(algo.findShortestPath(start, end, state.upgpHeu, graph.sg.algorithms.SearchStep::vertex));
//                if (state.upgpPath.size != 0)
//                    scanned += state.upgpPath.size;
//            }
//        }
//        return scanned;
//    }
//
//    @Benchmark
//    public long doOneUpdateGPUD(BenchmarkState state) {
//        final graph.sg.algorithms.UndirectedGraphAlgorithms<GridPoint2> algo = state.upgpUndirectedGraph.algorithms();
//        GridPoint2 start = state.highestGP;
//        GridPoint2 end = state.lowestGP;
//        state.upgpPath.clear();
//        state.upgpPath.addAll(algo.findShortestPath(start, end, state.upgpHeu, graph.sg.algorithms.SearchStep::vertex));
//        return state.upgpPath.size();
//    }

    @Benchmark
    public long doPathUpdateD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final graph.sg.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.updateDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.updatePath.clear();
                state.updatePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.updateHeu, graph.sg.algorithms.SearchStep::vertex));
                if (state.updatePath.size != 0)
                    scanned += state.updatePath.size;
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathUpdateD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final graph.sg.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.updateDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.squadNearbyMap[x][y];
                state.updatePath.clear();
                state.updatePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.updateHeu, graph.sg.algorithms.SearchStep::vertex));
                if (state.updatePath.size != 0)
                    scanned += state.updatePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathUpdateUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final graph.sg.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.updateUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.updatePath.clear();
                state.updatePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.updateHeu, graph.sg.algorithms.SearchStep::vertex));
                if (state.updatePath.size != 0)
                    scanned += state.updatePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathUpdateUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final graph.sg.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.updateUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.updatePath.clear();
                state.updatePath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.updateHeu, graph.sg.algorithms.SearchStep::vertex));
                if (state.updatePath.size != 0)
                    scanned += state.updatePath.size;
            }
        }
        return scanned;
    }

    // GAND
    
    @Benchmark
    public long doPathGandD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.gandDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.gandPath.clear();
                state.gandPath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.gandHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gandPath.size != 0)
                    scanned += state.gandPath.size;
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.gandDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.squadNearbyMap[x][y];
                state.gandPath.clear();
                state.gandPath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.gandHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gandPath.size != 0)
                    scanned += state.gandPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathGandUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.gandUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.srng.getRandomElement(state.squadFloorArray);
                state.gandPath.clear();
                state.gandPath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.gandHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gandPath.size != 0)
                    scanned += state.gandPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandUD(BenchmarkState state) {
        com.github.yellowstonegames.grid.Coord r;
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.gandUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.squadNearbyMap[x][y];
                state.gandPath.clear();
                state.gandPath.addAll(algo.findShortestPath(r, com.github.yellowstonegames.grid.Coord.get(x, y), state.gandHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gandPath.size != 0)
                    scanned += state.gandPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathGandGPD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<GridPoint2> algo = state.ggpDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.ggpPath.clear();
                end.y = y;
                state.ggpPath.addAll(algo.findShortestPath(start, end, state.ggpHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.ggpPath.size != 0)
                    scanned += state.ggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGPD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<GridPoint2> algo = state.ggpDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.ggpPath.clear();
                end.y = y;
                state.ggpPath.addAll(algo.findShortestPath(start, end, state.ggpHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.ggpPath.size != 0)
                    scanned += state.ggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGPD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<GridPoint2> algo = state.ggpDirectedGraph.algorithms();
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        state.ggpPath.clear();
        state.ggpPath.addAll(algo.findShortestPath(start, end, state.ggpHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.ggpPath.size();
    }

    @Benchmark
    public long doPathGandGPUD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<GridPoint2> algo = state.ggpUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.ggpPath.clear();
                end.y = y;
                state.ggpPath.addAll(algo.findShortestPath(start, end, state.ggpHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.ggpPath.size != 0)
                    scanned += state.ggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGPUD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<GridPoint2> algo = state.ggpUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.ggpPath.clear();
                end.y = y;
                state.ggpPath.addAll(algo.findShortestPath(start, end, state.ggpHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.ggpPath.size != 0)
                    scanned += state.ggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGPUD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<GridPoint2> algo = state.ggpUndirectedGraph.algorithms();
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        state.ggpPath.clear();
        state.ggpPath.addAll(algo.findShortestPath(start, end, state.ggpHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.ggpPath.size();
    }
    
    @Benchmark
    public long doPathGandVD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<Vector2> algo = state.gvDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.srng.getRandomElement(state.vFloors));
                state.gvPath.clear();
                endV.y = y;
                state.gvPath.addAll(algo.findShortestPath(startV, endV, state.gvHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gvPath.size != 0)
                    scanned += state.gvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandVD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<Vector2> algo = state.gvDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.vNearbyMap[x][y]);
                state.gvPath.clear();
                endV.y = y;
                state.gvPath.addAll(algo.findShortestPath(startV, endV, state.gvHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gvPath.size != 0)
                    scanned += state.gvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandVD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<Vector2> algo = state.gvDirectedGraph.algorithms();
        Vector2 startV = state.highestV;
        Vector2 endV = state.lowestV;
        state.gvPath.clear();
        state.gvPath.addAll(algo.findShortestPath(startV, endV, state.gvHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gvPath.size();
    }

    @Benchmark
    public long doPathGandVUD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<Vector2> algo = state.gvUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.srng.getRandomElement(state.vFloors));
                state.gvPath.clear();
                endV.y = y;
                state.gvPath.addAll(algo.findShortestPath(startV, endV, state.gvHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gvPath.size != 0)
                    scanned += state.gvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandVUD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<Vector2> algo = state.gvUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endV.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startV.set(state.vNearbyMap[x][y]);
                state.gvPath.clear();
                endV.y = y;
                state.gvPath.addAll(algo.findShortestPath(startV, endV, state.gvHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gvPath.size != 0)
                    scanned += state.gvPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandVUD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<Vector2> algo = state.gvUndirectedGraph.algorithms();
        Vector2 startV = state.highestV;
        Vector2 endV = state.lowestV;
        state.gvPath.clear();
        state.gvPath.addAll(algo.findShortestPath(startV, endV, state.gvHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gvPath.size();
    }

    @Benchmark
    public long doPathGandI2D(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointI2> algo = state.gi2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.srng.getRandomElement(state.iFloors));
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandI2D(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointI2> algo = state.gi2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.iNearbyMap[x][y]);
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandI2D(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointI2> algo = state.gi2DirectedGraph.algorithms();
        PointI2 startI = state.highestI;
        PointI2 endI = state.lowestI;
        state.giPath.clear();
        state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.giPath.size();
    }
    @Benchmark
    public long doPathGandI2UD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointI2> algo = state.gi2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.srng.getRandomElement(state.iFloors));
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandI2UD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointI2> algo = state.gi2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.iNearbyMap[x][y]);
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandI2UD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointI2> algo = state.gi2UndirectedGraph.algorithms();
        PointI2 startI = state.highestI;
        PointI2 endI = state.lowestI;
        state.giPath.clear();
        state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.giPath.size();
    }


    @Benchmark
    public long doPathGandF2D(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointF2> algo = state.gf2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.srng.getRandomElement(state.fFloors));
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandF2D(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointF2> algo = state.gf2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.fNearbyMap[x][y]);
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandF2D(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointF2> algo = state.gf2DirectedGraph.algorithms();
        PointF2 startF = state.highestF;
        PointF2 endF = state.lowestF;
        state.gfPath.clear();
        state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gfPath.size();
    }
    @Benchmark
    public long doPathGandF2UD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointF2> algo = state.gf2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.srng.getRandomElement(state.fFloors));
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandF2UD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointF2> algo = state.gf2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.fNearbyMap[x][y]);
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandF2UD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointF2> algo = state.gf2UndirectedGraph.algorithms();
        PointF2 startF = state.highestF;
        PointF2 endF = state.lowestF;
        state.gfPath.clear();
        state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gfPath.size();
    }

    @Benchmark
    public long doPathGandGenericI2D(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointI2> algo = state.ggeni2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.srng.getRandomElement(state.iFloors));
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGenericI2D(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointI2> algo = state.ggeni2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.iNearbyMap[x][y]);
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGenericI2D(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointI2> algo = state.ggeni2DirectedGraph.algorithms();
        PointI2 startI = state.highestI;
        PointI2 endI = state.lowestI;
        state.giPath.clear();
        state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.giPath.size();
    }
    @Benchmark
    public long doPathGandGenericI2UD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointI2> algo = state.ggeni2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.srng.getRandomElement(state.iFloors));
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGenericI2UD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointI2> algo = state.ggeni2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startI.set(state.iNearbyMap[x][y]);
                state.giPath.clear();
                endI.y = y;
                state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.giPath.size != 0)
                    scanned += state.giPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGenericI2UD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointI2> algo = state.ggeni2UndirectedGraph.algorithms();
        PointI2 startI = state.highestI;
        PointI2 endI = state.lowestI;
        state.giPath.clear();
        state.giPath.addAll(algo.findShortestPath(startI, endI, state.giHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.giPath.size();
    }
    
    
    @Benchmark
    public long doPathGandGenericF2D(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointF2> algo = state.ggenf2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.srng.getRandomElement(state.fFloors));
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGenericF2D(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointF2> algo = state.ggenf2DirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.fNearbyMap[x][y]);
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGenericF2D(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<PointF2> algo = state.ggenf2DirectedGraph.algorithms();
        PointF2 startF = state.highestF;
        PointF2 endF = state.lowestF;
        state.gfPath.clear();
        state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gfPath.size();
    }
    @Benchmark
    public long doPathGandGenericF2UD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointF2> algo = state.ggenf2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.srng.getRandomElement(state.fFloors));
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGenericF2UD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointF2> algo = state.ggenf2UndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endF.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                startF.set(state.fNearbyMap[x][y]);
                state.gfPath.clear();
                endF.y = y;
                state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gfPath.size != 0)
                    scanned += state.gfPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGenericF2UD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<PointF2> algo = state.ggenf2UndirectedGraph.algorithms();
        PointF2 startF = state.highestF;
        PointF2 endF = state.lowestF;
        state.gfPath.clear();
        state.gfPath.addAll(algo.findShortestPath(startF, endF, state.gfHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gfPath.size();
    }


    @Benchmark
    public long doPathGandGenericCoordD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.ggenCoordDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                state.gCoordPath.clear();
                state.gCoordPath.addAll(algo.findShortestPath(state.srng.getRandomElement(state.squadFloorArray), com.github.yellowstonegames.grid.Coord.get(x, y), state.gCoordHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gCoordPath.size != 0)
                    scanned += state.gCoordPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGenericCoordD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.ggenCoordDirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                state.gCoordPath.clear();
                state.gCoordPath.addAll(algo.findShortestPath(state.squadNearbyMap[x][y], com.github.yellowstonegames.grid.Coord.get(x, y), state.gCoordHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gCoordPath.size != 0)
                    scanned += state.gCoordPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGenericCoordD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.DirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.ggenCoordDirectedGraph.algorithms();
        com.github.yellowstonegames.grid.Coord startI = state.squadHighest;
        com.github.yellowstonegames.grid.Coord endI = state.squadLowest;
        state.gCoordPath.clear();
        state.gCoordPath.addAll(algo.findShortestPath(startI, endI, state.gCoordHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gCoordPath.size();
    }
    @Benchmark
    public long doPathGandGenericCoordUD(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.ggenCoordUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                state.gCoordPath.clear();
                state.gCoordPath.addAll(algo.findShortestPath(state.srng.getRandomElement(state.squadFloorArray), com.github.yellowstonegames.grid.Coord.get(x, y), state.gCoordHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gCoordPath.size != 0)
                    scanned += state.gCoordPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGenericCoordUD(BenchmarkState state) {
        long scanned = 0;
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.ggenCoordUndirectedGraph.algorithms();
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                state.gCoordPath.clear();
                state.gCoordPath.addAll(algo.findShortestPath(state.squadNearbyMap[x][y], com.github.yellowstonegames.grid.Coord.get(x, y), state.gCoordHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
                if (state.gCoordPath.size != 0)
                    scanned += state.gCoordPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGenericCoordUD(BenchmarkState state) {
        final com.github.tommyettinger.gand.algorithms.UndirectedGraphAlgorithms<com.github.yellowstonegames.grid.Coord> algo = state.ggenCoordUndirectedGraph.algorithms();
        com.github.yellowstonegames.grid.Coord startI = state.squadHighest;
        com.github.yellowstonegames.grid.Coord endI = state.squadLowest;
        state.gCoordPath.clear();
        state.gCoordPath.addAll(algo.findShortestPath(startI, endI, state.gCoordHeu, com.github.tommyettinger.gand.algorithms.SearchStep::vertex));
        return state.gCoordPath.size();
    }


    @Benchmark
    public long doPathGandGradientGrid(BenchmarkState state) {
        PointI2 r;
        final ArrayList<PointI2> tgts = new ArrayList<>(1);
        tgts.add(endI);
        long scanned = 0;
        state.srng.setState(1234567890L);
        final GradientGridI2 gragri = state.gradientGrid;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                endI.y = y;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) gragri.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.iFloors);
                state.giPath.clear();
                gragri.findPath(state.giPath, PATH_LENGTH, -1, null, null, r, tgts);
                gragri.clearGoals();
                gragri.resetMap();
                scanned += state.giPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathGandGradientGrid(BenchmarkState state) {
        PointI2 r;
        final ArrayList<PointI2> tgts = new ArrayList<>(1);
        tgts.add(endI);
        long scanned = 0;
        final GradientGridI2 gragri = state.gradientGrid;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            endI.x = x;
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                endI.y = y;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dextra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.iNearbyMap[x][y];
                state.giPath.clear();
                gragri.findPath(state.giPath, 9, 9, null, null, r, tgts);
                gragri.clearGoals();
                gragri.resetMap();
                scanned += state.giPath.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneGandGradientGrid(BenchmarkState state) {
        final GradientGridI2 gragri = state.gradientGrid;
        final int PATH_LENGTH = BenchmarkState.WIDTH * BenchmarkState.HEIGHT;
        state.srng.setState(state.highest.hashCode());
        List<PointI2> end = Collections.singletonList(state.lowestI);
        state.giPath.clear();
        gragri.findPath(state.giPath, PATH_LENGTH, -1, null, null, state.highestI, end);
        gragri.clearGoals();
        gragri.resetMap();
        return state.giPath.size();
    }

    @Benchmark
    public long doScanGandGradientGrid(BenchmarkState state) {
        long scanned = 0;
        final GradientGridI2 gragri = state.gradientGrid;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                gragri.setGoal(x, y);
                gragri.scan(null, null);
                gragri.clearGoals();
                gragri.resetMap();
                scanned++;
            }
        }
        return scanned;
    }


    // NATE


    @Benchmark
    public long doPathNate(BenchmarkState state) {
        long scanned = 0;
        state.srng.setState(1234567890L);
        final NateStar nate = state.nate;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                int len = nate.getPath(start.x, start.y, x, y).size;
                if (len != 0)
                    scanned += len;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathNate(BenchmarkState state) {
        long scanned = 0;
        final NateStar nate = state.nate;
        for (int x = 1; x < BenchmarkState.WIDTH - 1; x++) {
            for (int y = 1; y < BenchmarkState.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                int len = nate.getPath(start.x, start.y, x, y).size;
                if (len != 0)
                    scanned += len;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneNate(BenchmarkState state) {
        final NateStar nate = state.nate;
        GridPoint2 start = state.highestGP;
        GridPoint2 end = state.lowestGP;
        return nate.getPath(start.x, start.y, end.x, end.y).size;
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
                .include(PathfindingBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(3)
                .forks(1)
                .shouldDoGC(true)
                .build();
        new Runner(opt).run();
    }
}
