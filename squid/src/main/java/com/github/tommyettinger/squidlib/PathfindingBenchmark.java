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

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.grid.Region;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import space.earlygrey.simplegraphs.*;
import space.earlygrey.simplegraphs.algorithms.DirectedGraphAlgorithms;
import space.earlygrey.simplegraphs.algorithms.SearchStep;
import space.earlygrey.simplegraphs.algorithms.UndirectedGraphAlgorithms;
import squid.squad.BitDijkstraMap;
import squid.squad.CDijkstraMap;
import squidpony.ArrayTools;
import squidpony.squidai.CustomDijkstraMap;
import squid.lib.DijkstraMap;
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
 * PathfindingBenchmark.doPathAStarSearch         avgt    5   723401.656 ± 100143.566  us/op
 * PathfindingBenchmark.doPathBitDijkstra         avgt    5  1362471.735 ±  28827.452  us/op
 * PathfindingBenchmark.doPathCDijkstra           avgt    5  1376518.398 ±   9843.301  us/op
 * PathfindingBenchmark.doPathCustomDijkstra      avgt    5  2591001.615 ±  67352.760  us/op
 * PathfindingBenchmark.doPathDijkstra            avgt    5  1552855.477 ±  47374.772  us/op
 * PathfindingBenchmark.doPathGDXAStarCoord       avgt    5  1345276.688 ±  16473.037  us/op
 * PathfindingBenchmark.doPathGDXAStarGP          avgt    5    42759.357 ±   1932.409  us/op
 * PathfindingBenchmark.doPathSimpleD             avgt    5   616131.435 ±  10131.809  us/op
 * PathfindingBenchmark.doPathSimpleGPD           avgt    5     1174.820 ±      8.963  us/op
 * PathfindingBenchmark.doPathSimpleGPUD          avgt    5     1163.575 ±     15.182  us/op
 * PathfindingBenchmark.doPathSimpleUD            avgt    5   621103.825 ±  15513.861  us/op
 * PathfindingBenchmark.doPathSquadDijkstra       avgt    5  1398658.505 ±  55511.355  us/op
 * PathfindingBenchmark.doPathSquidCG             avgt    5   644088.093 ±  12692.813  us/op
 * PathfindingBenchmark.doPathSquidD              avgt    5   714277.825 ±  87217.539  us/op
 * PathfindingBenchmark.doPathSquidDG             avgt    5   634813.273 ±   8740.559  us/op
 * PathfindingBenchmark.doPathSquidUD             avgt    5   661954.041 ±  12062.135  us/op
 * PathfindingBenchmark.doTinyPathAStarSearch     avgt    5    10974.680 ±    263.024  us/op
 * PathfindingBenchmark.doTinyPathBitDijkstra     avgt    5    35430.111 ±    815.466  us/op
 * PathfindingBenchmark.doTinyPathCDijkstra       avgt    5    36364.889 ±   1740.616  us/op
 * PathfindingBenchmark.doTinyPathCustomDijkstra  avgt    5   201668.380 ±   7586.828  us/op
 * PathfindingBenchmark.doTinyPathDijkstra        avgt    5    46102.466 ±   4189.637  us/op
 * PathfindingBenchmark.doTinyPathGDXAStarCoord   avgt    5    18222.640 ±    227.460  us/op
 * PathfindingBenchmark.doTinyPathGDXAStarGP      avgt    5    31076.514 ±    625.452  us/op // anomalous?
 * PathfindingBenchmark.doTinyPathSimpleD         avgt    5     8501.443 ±    313.235  us/op
 * PathfindingBenchmark.doTinyPathSimpleGPD       avgt    5     1025.561 ±      6.237  us/op
 * PathfindingBenchmark.doTinyPathSimpleGPUD      avgt    5     1015.949 ±     14.985  us/op
 * PathfindingBenchmark.doTinyPathSimpleUD        avgt    5     8308.827 ±    132.115  us/op
 * PathfindingBenchmark.doTinyPathSquadDijkstra   avgt    5    36713.787 ±   1323.081  us/op
 * PathfindingBenchmark.doTinyPathSquidCG         avgt    5     7617.579 ±    165.523  us/op
 * PathfindingBenchmark.doTinyPathSquidD          avgt    5     8930.869 ±    123.616  us/op
 * PathfindingBenchmark.doTinyPathSquidDG         avgt    5     8376.287 ±    209.275  us/op
 * PathfindingBenchmark.doTinyPathSquidUD         avgt    5     8198.538 ±    160.099  us/op
 * </pre>
 *
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 4)
@Measurement(iterations = 3)
public class PathfindingBenchmark {
    private static final GridPoint2 start = new GridPoint2(), end = new GridPoint2();
    @State(Scope.Thread)
    public static class BenchmarkState {
        public static final int WIDTH = 100;
        public static final int HEIGHT = 100;
//        public static final GridPoint2[][] gridPool = new GridPoint2[WIDTH][HEIGHT];
        public DungeonGenerator dungeonGen = new DungeonGenerator(WIDTH, HEIGHT, new StatefulRNG(0x1337BEEFDEAL));
        public char[][] map;
        public double[][] astarMap;
        public GreasedRegion floors;
        public ArrayList<GridPoint2> gpFloors;
        public Region squadFloors;
        public int floorCount;
        public Coord[] floorArray;
        public Coord lowest, highest;
        public Coord[][] nearbyMap;
        public GridPoint2[][] gpNearbyMap;
        public com.github.yellowstonegames.grid.Coord[] squadFloorArray;
        public com.github.yellowstonegames.grid.Coord[][] squadNearbyMap;
        public int[] customNearbyMap;
        public Adjacency adj;
        public DijkstraMap dijkstra;
        public CustomDijkstraMap customDijkstra;
        public squid.squad.DijkstraMap squadDijkstra;
        public BitDijkstraMap bitDijkstra;
        public CDijkstraMap cDijkstra;
        public StatefulRNG srng;
        public GridGraphGP gg;
        public GridGraphCoord gg2;
        public IndexedAStarPathFinder<GridPoint2> astar;
        public IndexedAStarPathFinder<Coord> astar2;
        public AStarSearch as;
        public DefaultGraphPath<GridPoint2> dgpgp;
        public DefaultGraphPath<Coord> dgp;
        public ArrayList<Coord> path;
        public Path<Coord> simplePath;
        public Path<GridPoint2> sggpPath;
        public ObjectList<com.github.yellowstonegames.grid.Coord> squadPath;

        public DirectedGraph<Coord> simpleDirectedGraph;
        public UndirectedGraph<Coord> simpleUndirectedGraph;
        public space.earlygrey.simplegraphs.utils.Heuristic<Coord> simpleHeu;

        public DirectedGraph<GridPoint2> sggpDirectedGraph;
        public UndirectedGraph<GridPoint2> sggpUndirectedGraph;
        public space.earlygrey.simplegraphs.utils.Heuristic<GridPoint2> sggpHeu;

        public squidpony.squidai.graph.DirectedGraph<Coord> squidDirectedGraph;
        public squidpony.squidai.graph.UndirectedGraph<Coord> squidUndirectedGraph;
        public squidpony.squidai.graph.DefaultGraph squidDefaultGraph;
        public squidpony.squidai.graph.CostlyGraph squidCostlyGraph;
        
        @Setup(Level.Trial)
        public void setup() {
            map = dungeonGen.generate();
            ArrayTools.reverse(map);
            Coord.expandPoolTo(WIDTH, HEIGHT);
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

            squadFloors = new Region(map, '.');
            squadFloorArray = squadFloors.asCoords();

            lowest = floors.first();
            highest = floors.last();
            System.out.println("Floors: " + floorCount);
            System.out.println("Percentage walkable: " + floorCount * 100.0 / (WIDTH * HEIGHT) + "%");
            astarMap = DungeonUtility.generateAStarCostMap(map, Collections.<Character, Double>emptyMap(), 1);
            as = new AStarSearch(astarMap, AStarSearch.SearchType.CHEBYSHEV);
            nearbyMap = new Coord[WIDTH][HEIGHT];
            gpNearbyMap = new GridPoint2[WIDTH][HEIGHT];
            squadNearbyMap = new com.github.yellowstonegames.grid.Coord[WIDTH][HEIGHT];
            customNearbyMap = new int[WIDTH * HEIGHT];
            GreasedRegion tmp = new GreasedRegion(WIDTH, HEIGHT);
            adj = new Adjacency.BasicAdjacency(WIDTH, HEIGHT, CHEBYSHEV);
            adj.blockingRule = 0;
            srng = new StatefulRNG(0x1337BEEF1337CA77L);
            Coord c;
            for (int i = 1; i < WIDTH - 1; i++) {
                for (int j = 1; j < HEIGHT - 1; j++) {
                    if(map[i][j] == '#')
                        continue;
                    c = tmp.empty().insert(i, j).flood(floors, 8).remove(i, j).singleRandom(srng);
                    nearbyMap[i][j] = c;
                    gpNearbyMap[i][j] = new GridPoint2(c.x, c.y);
                    squadNearbyMap[i][j] = com.github.yellowstonegames.grid.Coord.get(c.x, c.y);
                    customNearbyMap[adj.composite(i, j, 0, 0)] = adj.composite(c.x, c.y, 0, 0);
                }
            }
            dijkstra = new DijkstraMap(map, CHEBYSHEV, new StatefulRNG(0x1337BEEF));
            dijkstra.setBlockingRequirement(0);
            squadDijkstra = new squid.squad.DijkstraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
            squadDijkstra.setBlockingRequirement(0);
            bitDijkstra = new BitDijkstraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
            bitDijkstra.setBlockingRequirement(0);
            cDijkstra = new CDijkstraMap(map, com.github.yellowstonegames.grid.Measurement.CHEBYSHEV);
            cDijkstra.setBlockingRequirement(0);
            customDijkstra = new CustomDijkstraMap(map, adj, new StatefulRNG(0x1337BEEF));
            gg = new GridGraphGP(floors, map);
            gg2 = new GridGraphCoord(floors, map);
            astar = new IndexedAStarPathFinder<>(gg, false, GridPoint2::equals);
            astar2 = new IndexedAStarPathFinder<>(gg2, false);
            dgp = new DefaultGraphPath<>(WIDTH + HEIGHT << 1);
            dgpgp = new DefaultGraphPath<>(WIDTH + HEIGHT << 1);
            path = new ArrayList<>(WIDTH + HEIGHT << 1);
            squadPath = new ObjectList<>(WIDTH + HEIGHT << 1);

            simplePath = new Path<>(WIDTH + HEIGHT << 1);
            simpleDirectedGraph = new DirectedGraph<>(floors);
            simpleUndirectedGraph = new UndirectedGraph<>(floors);
            simpleHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            sggpPath = new Path<>(WIDTH + HEIGHT << 1);
            sggpDirectedGraph = new DirectedGraph<>(gpFloors);
            sggpUndirectedGraph = new UndirectedGraph<>(gpFloors);
            sggpHeu = (currentNode, targetNode) ->
                    Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

            squidDirectedGraph   = new squidpony.squidai.graph.DirectedGraph<>(floors);
            squidUndirectedGraph = new squidpony.squidai.graph.UndirectedGraph<>(floors);
            squidDefaultGraph = new squidpony.squidai.graph.DefaultGraph(map, true);
            squidCostlyGraph = new squidpony.squidai.graph.CostlyGraph(astarMap, true);

            Coord center;
            GridPoint2 gpCenter;
            Direction[] outer = Direction.CLOCKWISE;
            Direction dir;
            for (int i = floorCount - 1; i >= 0; i--) {
                center = floorArray[i];
                gpCenter = gpFloors.get(i);
                for (int j = 0; j < 8; j++) {
                    dir = outer[j];
                    if(floors.contains(center.x + dir.deltaX, center.y + dir.deltaY))
                    {
                        GridPoint2 gpMoved = new GridPoint2(gpCenter).add(dir.deltaX, dir.deltaY);
                        simpleDirectedGraph.addEdge(center, center.translate(dir));
                        sggpDirectedGraph.addEdge(gpCenter, gpMoved);
                        squidDirectedGraph.addEdge(center, center.translate(dir));
                        if(!simpleUndirectedGraph.edgeExists(center, center.translate(dir)))
                        {
                            simpleUndirectedGraph.addEdge(center, center.translate(dir));
                            squidUndirectedGraph.addEdge(center, center.translate(dir));
                            sggpUndirectedGraph.addEdge(gpCenter, gpMoved);
                        }
                    }
                }
            }
        }

    }

    @Benchmark
    public long doScanDijkstra(BenchmarkState state)
    {
        long scanned = 0;
        final DijkstraMap dijkstra = state.dijkstra;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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
    public long doScanCustomDijkstra(BenchmarkState state)
    {
        CustomDijkstraMap dijkstra = state.customDijkstra;
        long scanned = 0;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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
    public long doPathDijkstra(BenchmarkState state)
    {
        Coord r;
        final Coord[] tgts = new Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DijkstraMap dijkstra = state.dijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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
    public long doTinyPathDijkstra(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final Coord[] tgts = new Coord[1];
        final DijkstraMap dijkstra = state.dijkstra;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
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
    public long doPathSquadDijkstra(BenchmarkState state)
    {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squid.squad.DijkstraMap dijkstra = state.squadDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.squadFloorArray);
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                state.path.clear();
                dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquadDijkstra(BenchmarkState state)
    {
            com.github.yellowstonegames.grid.Coord r;
            final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
            long scanned = 0;
            final squid.squad.DijkstraMap dijkstra = state.squadDijkstra;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.squadNearbyMap[x][y];
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                //dijkstra.partialScan(r,9, null);
                state.path.clear();
                dijkstra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneSquadDijkstra(BenchmarkState state) {
            final squid.squad.DijkstraMap dijkstra = state.squadDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
        state.path.clear();
        dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
        dijkstra.clearGoals();
        dijkstra.resetMap();
        return state.path.size();
    }

    @Benchmark
    public long doPathBitDijkstra(BenchmarkState state)
    {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final BitDijkstraMap dijkstra = state.bitDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.squadFloorArray);
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                state.path.clear();
                dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathBitDijkstra(BenchmarkState state)
    {
            com.github.yellowstonegames.grid.Coord r;
            final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
            long scanned = 0;
            final BitDijkstraMap dijkstra = state.bitDijkstra;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.squadNearbyMap[x][y];
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                //dijkstra.partialScan(r,9, null);
                state.path.clear();
                dijkstra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneBitDijkstra(BenchmarkState state) {
            final BitDijkstraMap dijkstra = state.bitDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
        state.path.clear();
        dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
        dijkstra.clearGoals();
        dijkstra.resetMap();
        return state.path.size();
    }


    @Benchmark
    public long doPathCDijkstra(BenchmarkState state)
    {
        com.github.yellowstonegames.grid.Coord r;
        final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
        long scanned = 0;
        state.srng.setState(1234567890L);
        final CDijkstraMap dijkstra = state.cDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.srng.getRandomElement(state.squadFloorArray);
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                state.path.clear();
                dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathCDijkstra(BenchmarkState state)
    {
            com.github.yellowstonegames.grid.Coord r;
            final com.github.yellowstonegames.grid.Coord[] tgts = new com.github.yellowstonegames.grid.Coord[1];
            long scanned = 0;
            final CDijkstraMap dijkstra = state.cDijkstra;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.squadNearbyMap[x][y];
                tgts[0] = com.github.yellowstonegames.grid.Coord.get(x, y);
                //dijkstra.partialScan(r,9, null);
                state.path.clear();
                dijkstra.findPath(state.squadPath, 9, 9, null, null, r, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doOneCDijkstra(BenchmarkState state) {
            final CDijkstraMap dijkstra = state.cDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        com.github.yellowstonegames.grid.Coord tgt = com.github.yellowstonegames.grid.Coord.get(state.highest.x, state.highest.y);
        state.srng.setState(state.highest.hashCode());
        com.github.yellowstonegames.grid.Coord r = com.github.yellowstonegames.grid.Coord.get(state.lowest.x, state.lowest.y);
        state.path.clear();
        dijkstra.findPath(state.squadPath, PATH_LENGTH, -1, null, null, r, tgt);
        dijkstra.clearGoals();
        dijkstra.resetMap();
        return state.path.size();
    }

    @Benchmark
    public long doPathCustomDijkstra(BenchmarkState state)
    {
        Coord r;
        int[] tgts = new int[1];
        long scanned = 0;
        int p;
        state.srng.setState(1234567890L);
        CustomDijkstraMap dijkstra = state.customDijkstra;
        final int PATH_LENGTH = state.WIDTH * state.HEIGHT;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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
    @Benchmark
    public long doTinyPathCustomDijkstra(BenchmarkState state)
    {
        Coord r;
        int[] tgts = new int[1];
        long scanned = 0;
        int p;
        CustomDijkstraMap dijkstra = state.customDijkstra;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                //((StatefulRNG) dijkstra.rng).setState(((x << 20) | (y << 14)) ^ (x * y));
                r = state.nearbyMap[x][y];
                p = state.adj.composite(r.x, r.y, 0, 0);
                tgts[0] = state.adj.composite(x, y, 0, 0);
                dijkstra.findPath(9,  9,null, null, p, tgts);
                dijkstra.clearGoals();
                dijkstra.resetMap();
                scanned += dijkstra.path.size;
            }
        }
        return scanned;
    }





    @Benchmark
    public long doPathAStarSearch(BenchmarkState state)
    {
        Coord r;
        Coord tgt;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final AStarSearch aStarSearch = state.as;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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
    public long doTinyPathAStarSearch(BenchmarkState state)
    {
        Coord r;
        Coord tgt;
        long scanned = 0;
        final AStarSearch aStarSearch = state.as;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
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

    static class GridGraphGP implements IndexedGraph<GridPoint2>
    {
        public ObjectIntMap<GridPoint2> points = new ObjectIntMap<>(128 * 128);
        public char[][] map;
//        public Heuristic<Coord> heu = new Heuristic<Coord>() {
//            @Override
//            public float estimate(Coord node, Coord endNode) {
//                return (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));
//            }
//        };
        public Heuristic<GridPoint2> heu = (node, endNode) -> {
//                return node.dst(endNode);
            return (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));
        };

        public GridGraphGP(Iterable<Coord> floors, char[][] map)
        {
            this.map = map;
            int i = 0;
            for(Coord c : floors){
                points.put(new GridPoint2(c.x, c.y), i++);
            }
        }
        // use this if using only libGDX classes.
//        public GridGraphGP(Iterable<GridPoint2> floors, char[][] map)
//        {
//            this.map = map;
//            int i = 0;
//            for(GridPoint2 c : floors){
//                points.put(c, i++);
//            }
//        }
        @Override
        public int getIndex(GridPoint2 node) {
            return points.get(node, -1);
        }

        @Override
        public int getNodeCount() {
            return points.size;
        }

        @Override
        public Array<Connection<GridPoint2>> getConnections(GridPoint2 fromNode) {
            Array<Connection<GridPoint2>> conn = new Array<>(false, 8, Connection.class);
            if(map[fromNode.x][fromNode.y] != '.')
                return conn;
            for (int i = 0; i < 8; i++) {
                int x = fromNode.x+Direction.OUTWARDS[i].deltaX, y = fromNode.y+Direction.OUTWARDS[i].deltaY;
                if (x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] == '.')
                    conn.add(new DefaultConnection<>(fromNode, new GridPoint2(x, y)));
            }
            return conn;
        }
    }


    static class GridGraphCoord implements IndexedGraph<Coord>
    {
        public ObjectIntMap<Coord> points = new ObjectIntMap<>(128 * 128);
        public char[][] map;
        public Heuristic<Coord> heu = (node, endNode) ->
                Math.max(Math.abs(node.x - endNode.x), Math.abs(node.y - endNode.y));

        public GridGraphCoord(Iterable<Coord> floors, char[][] map)
        {
            this.map = map;
            int i = 0;
            for(Coord c : floors){
                points.put(c, i++);
            }
        }

        @Override
        public int getIndex(Coord node) {
            return points.get(node, -1);
        }

        @Override
        public int getNodeCount() {
            return points.size;
        }

        @Override
        public Array<Connection<Coord>> getConnections(Coord fromNode) {
            Array<Connection<Coord>> conn = new Array<>(false, 8, Connection.class);
            if(map[fromNode.x][fromNode.y] != '.')
                return conn;
            for (int i = 0; i < 8; i++) {
                int x = fromNode.x+Direction.OUTWARDS[i].deltaX, y = fromNode.y+Direction.OUTWARDS[i].deltaY;
                if (x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] == '.')
                    conn.add(new DefaultConnection<>(fromNode, Coord.get(x, y)));
            }
            return conn;
        }
    }

    @Benchmark
    public long doPathGDXAStarGP(BenchmarkState state)
    {
        long scanned = 0;
        state.srng.setState(1234567890L);
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.srng.getRandomElement(state.gpFloors));
                state.dgpgp.clear();
                end.y = y;
                if(state.astar.searchNodePath(start, end, state.gg.heu, state.dgpgp))
                    scanned += state.dgpgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathGDXAStarGP(BenchmarkState state)
    {
        long scanned = 0;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            end.x = x;
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                start.set(state.gpNearbyMap[x][y]);
                state.dgpgp.clear();
                end.y = y;
                if(state.astar.searchNodePath(start, end, state.gg.heu, state.dgpgp))
                    scanned += state.dgpgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doOneGDXAStarGP(BenchmarkState state) {
        Coord tgt = state.highest;
        state.srng.setState(state.highest.hashCode());
        state.dgpgp.clear();
        start.set(state.lowest.x, state.lowest.y);
        end.set(tgt.x, tgt.y);
        state.astar.searchNodePath(start, end, state.gg.heu, state.dgpgp);
        return state.dgpgp.getCount();
    }

    @Benchmark
    public long doPathGDXAStarCoord(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.dgp.clear();
                if(state.astar2.searchNodePath(r, Coord.get(x, y), state.gg2.heu, state.dgp))
                    scanned+= state.dgp.getCount();
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathGDXAStarCoord(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.dgp.clear();
                if(state.astar2.searchNodePath(r, Coord.get(x, y), state.gg2.heu, state.dgp))
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
    public long doPathSimpleGPD(BenchmarkState state)
    {
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
                if(state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSimpleGPUD(BenchmarkState state)
    {
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
                if(state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleGPD(BenchmarkState state)
    {
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
                if(state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleGPUD(BenchmarkState state)
    {
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
                if(state.sggpPath.size != 0)
                    scanned += state.sggpPath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSimpleD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final DirectedGraphAlgorithms<Coord> algo = state.simpleDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if(state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
//        if(scanned == 0) throw new RuntimeException("No paths found!");
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final DirectedGraphAlgorithms<Coord> algo = state.simpleDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.nearbyMap[x][y];
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if(state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSimpleUD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final UndirectedGraphAlgorithms<Coord> algo = state.simpleUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                r = state.srng.getRandomElement(state.floorArray);
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if(state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSimpleUD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final UndirectedGraphAlgorithms<Coord> algo = state.simpleUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.simplePath.clear();
                state.simplePath.addAll(algo.findShortestPath(r, Coord.get(x, y), state.simpleHeu, SearchStep::vertex));
                if(state.simplePath.size != 0)
                    scanned += state.simplePath.size;
            }
        }
        return scanned;
    }

    @Benchmark
    public long doPathSquidD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidDirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }


    @Benchmark
    public long doPathSquidUD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidUD(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidUndirectedGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
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
    public long doPathSquidDG(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidDefaultGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidDG(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.UndirectedGraphAlgorithms<Coord> algo = state.squidDefaultGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }


    @Benchmark
    public long doPathSquidCG(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        state.srng.setState(1234567890L);
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidCostlyGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                // state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.srng.getRandomElement(state.floorArray);
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
            }
        }
        return scanned;
    }

    @Benchmark
    public long doTinyPathSquidCG(BenchmarkState state)
    {
        Coord r;
        long scanned = 0;
        final squidpony.squidai.graph.DirectedGraphAlgorithms<Coord> algo = state.squidCostlyGraph.algorithms();
        for (int x = 1; x < state.WIDTH - 1; x++) {
            for (int y = 1; y < state.HEIGHT - 1; y++) {
                if (state.map[x][y] == '#')
                    continue;
                // this should ensure no blatant correlation between R and W
                //state.srng.setState(x * 0xD1342543DE82EF95L + y * 0xC6BC279692B5C323L);
                r = state.nearbyMap[x][y];
                state.path.clear();
                if(algo.findShortestPath(r, Coord.get(x, y), state.path, squidpony.squidai.graph.Heuristic.CHEBYSHEV))
                    scanned += state.path.size();
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
