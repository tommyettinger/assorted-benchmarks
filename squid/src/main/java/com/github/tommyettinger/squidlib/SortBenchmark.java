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

import com.badlogic.gdx.utils.Sort;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.support.sort.ObjectComparators;
import com.github.tommyettinger.random.WhiskerRandom;
import com.github.yellowstonegames.text.Language;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import sort.GrailSort;
import sort.SortingNetwork;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                          (len)  Mode  Cnt       Score       Error  Units
 * SortBenchmark.doEttingerSort          10  avgt    5      38.495 ±    10.539  ns/op
 * SortBenchmark.doEttingerSort          40  avgt    5     231.777 ±    28.068  ns/op
 * SortBenchmark.doEttingerSort         160  avgt    5    1161.651 ±   234.271  ns/op
 * SortBenchmark.doEttingerSort         640  avgt    5    5085.883 ±  1014.257  ns/op
 * SortBenchmark.doEttingerSort        2560  avgt    5   27641.252 ±  2234.745  ns/op
 * SortBenchmark.doFastUtilMergeSort     10  avgt    5      25.732 ±     4.912  ns/op
 * SortBenchmark.doFastUtilMergeSort     40  avgt    5     230.893 ±    11.383  ns/op
 * SortBenchmark.doFastUtilMergeSort    160  avgt    5    1052.910 ±    52.314  ns/op
 * SortBenchmark.doFastUtilMergeSort    640  avgt    5    5086.110 ±   589.738  ns/op
 * SortBenchmark.doFastUtilMergeSort   2560  avgt    5   23602.268 ±  1247.427  ns/op
 * SortBenchmark.doGrailSort             10  avgt    5      24.912 ±     6.410  ns/op
 * SortBenchmark.doGrailSort             40  avgt    5    1787.961 ±   132.503  ns/op
 * SortBenchmark.doGrailSort            160  avgt    5    9839.707 ±  1773.594  ns/op
 * SortBenchmark.doGrailSort            640  avgt    5   71840.278 ± 38351.597  ns/op
 * SortBenchmark.doGrailSort           2560  avgt    5  354386.805 ± 45258.285  ns/op
 * SortBenchmark.doJDKSort               10  avgt    5      39.073 ±     5.933  ns/op
 * SortBenchmark.doJDKSort               40  avgt    5     161.342 ±    12.756  ns/op
 * SortBenchmark.doJDKSort              160  avgt    5     778.645 ±    36.450  ns/op
 * SortBenchmark.doJDKSort              640  avgt    5    3024.694 ±   102.049  ns/op
 * SortBenchmark.doJDKSort             2560  avgt    5   20000.941 ±  2049.230  ns/op
 * </pre>
 * Round 2!
 * <pre>
 * Benchmark                          (len)  Mode  Cnt       Score        Error  Units
 * SortBenchmark.doDSSort                10  avgt    5      35.067 ±      8.069  ns/op
 * SortBenchmark.doDSSort                40  avgt    5     251.090 ±     23.483  ns/op
 * SortBenchmark.doDSSort               160  avgt    5    1176.802 ±    257.750  ns/op
 * SortBenchmark.doDSSort               640  avgt    5    5078.693 ±    974.685  ns/op
 * SortBenchmark.doDSSort              2560  avgt    5   23188.909 ±   2006.005  ns/op
 * SortBenchmark.doEttingerSort          10  avgt    5      38.436 ±      8.443  ns/op
 * SortBenchmark.doEttingerSort          40  avgt    5     238.194 ±     77.344  ns/op
 * SortBenchmark.doEttingerSort         160  avgt    5    1156.285 ±    266.105  ns/op
 * SortBenchmark.doEttingerSort         640  avgt    5    4931.089 ±    844.457  ns/op
 * SortBenchmark.doEttingerSort        2560  avgt    5   27701.279 ±   1484.862  ns/op
 * SortBenchmark.doFastUtilMergeSort     10  avgt    5      25.707 ±      5.537  ns/op
 * SortBenchmark.doFastUtilMergeSort     40  avgt    5     228.237 ±     19.607  ns/op
 * SortBenchmark.doFastUtilMergeSort    160  avgt    5    1051.412 ±     39.612  ns/op
 * SortBenchmark.doFastUtilMergeSort    640  avgt    5    5097.612 ±    842.413  ns/op
 * SortBenchmark.doFastUtilMergeSort   2560  avgt    5   22032.496 ±   1818.985  ns/op
 * SortBenchmark.doGrailSort             10  avgt    5      24.620 ±      8.177  ns/op
 * SortBenchmark.doGrailSort             40  avgt    5    1723.784 ±    127.902  ns/op
 * SortBenchmark.doGrailSort            160  avgt    5    9667.650 ±   1736.952  ns/op
 * SortBenchmark.doGrailSort            640  avgt    5   64374.902 ±  39739.745  ns/op
 * SortBenchmark.doGrailSort           2560  avgt    5  351262.539 ± 115183.625  ns/op
 * SortBenchmark.doJDKSort               10  avgt    5      37.426 ±      6.492  ns/op
 * SortBenchmark.doJDKSort               40  avgt    5     162.872 ±     10.075  ns/op
 * SortBenchmark.doJDKSort              160  avgt    5     767.921 ±     12.894  ns/op
 * SortBenchmark.doJDKSort              640  avgt    5    3020.724 ±    230.197  ns/op
 * SortBenchmark.doJDKSort             2560  avgt    5   19171.034 ±   3102.891  ns/op
 * </pre>
 * With the (apparently very important!) {@code -gc true} flag:
 * <pre>
 * Benchmark                          (len)  Mode  Cnt       Score       Error  Units
 * SortBenchmark.doDSSort                10  avgt    5      21.922 ±     2.851  ns/op
 * SortBenchmark.doDSSort                40  avgt    5     249.710 ±    41.668  ns/op
 * SortBenchmark.doDSSort               160  avgt    5    1186.223 ±    42.126  ns/op
 * SortBenchmark.doDSSort               640  avgt    5    4499.036 ±   784.507  ns/op
 * SortBenchmark.doDSSort              2560  avgt    5   30933.042 ±  3808.520  ns/op
 * SortBenchmark.doEttingerSort          10  avgt    5      37.495 ±     5.252  ns/op
 * SortBenchmark.doEttingerSort          40  avgt    5     228.285 ±    32.573  ns/op
 * SortBenchmark.doEttingerSort         160  avgt    5    1066.587 ±   191.717  ns/op
 * SortBenchmark.doEttingerSort         640  avgt    5    4391.841 ±   607.109  ns/op
 * SortBenchmark.doEttingerSort        2560  avgt    5   26143.211 ±  3494.311  ns/op
 * SortBenchmark.doFastUtilMergeSort     10  avgt    5      31.968 ±     6.953  ns/op
 * SortBenchmark.doFastUtilMergeSort     40  avgt    5     204.887 ±    35.929  ns/op
 * SortBenchmark.doFastUtilMergeSort    160  avgt    5     976.684 ±    56.646  ns/op
 * SortBenchmark.doFastUtilMergeSort    640  avgt    5    4096.085 ±   666.017  ns/op
 * SortBenchmark.doFastUtilMergeSort   2560  avgt    5   23343.831 ±  1642.210  ns/op
 * SortBenchmark.doGrailSort             10  avgt    5      22.187 ±     4.471  ns/op
 * SortBenchmark.doGrailSort             40  avgt    5    1520.930 ±   116.610  ns/op
 * SortBenchmark.doGrailSort            160  avgt    5    9918.198 ±   442.629  ns/op
 * SortBenchmark.doGrailSort            640  avgt    5   56788.304 ± 42225.641  ns/op
 * SortBenchmark.doGrailSort           2560  avgt    5  340139.654 ± 42754.690  ns/op
 * SortBenchmark.doJDKSort               10  avgt    5      31.293 ±     5.292  ns/op
 * SortBenchmark.doJDKSort               40  avgt    5     127.665 ±    16.792  ns/op
 * SortBenchmark.doJDKSort              160  avgt    5     538.126 ±    15.972  ns/op
 * SortBenchmark.doJDKSort              640  avgt    5    2869.476 ±    52.685  ns/op
 * SortBenchmark.doJDKSort             2560  avgt    5   16640.592 ±   983.970  ns/op
 * </pre>
 * Looks like this whole "parallel programming" is just a fad. (Or these sizes aren't even close to big enough for
 * FastUtil to benefit at all.)
 * <pre>
 * Benchmark                                  (len)  Mode  Cnt       Score       Error  Units
 * SortBenchmark.doDSSort                        10  avgt    5      21.823 ±     2.927  ns/op
 * SortBenchmark.doDSSort                        40  avgt    5     235.288 ±    25.148  ns/op
 * SortBenchmark.doDSSort                       160  avgt    5    1185.362 ±    48.895  ns/op
 * SortBenchmark.doDSSort                       640  avgt    5    4840.674 ±   848.497  ns/op
 * SortBenchmark.doDSSort                      2560  avgt    5   30775.256 ±  3576.319  ns/op
 * SortBenchmark.doEttingerSort                  10  avgt    5      36.960 ±     7.082  ns/op
 * SortBenchmark.doEttingerSort                  40  avgt    5     219.587 ±    36.388  ns/op
 * SortBenchmark.doEttingerSort                 160  avgt    5    1025.808 ±    68.589  ns/op
 * SortBenchmark.doEttingerSort                 640  avgt    5    4208.089 ±   833.560  ns/op
 * SortBenchmark.doEttingerSort                2560  avgt    5   22715.233 ±  3528.032  ns/op
 * SortBenchmark.doFastUtilMergeSort             10  avgt    5      30.296 ±     7.597  ns/op
 * SortBenchmark.doFastUtilMergeSort             40  avgt    5     204.066 ±    29.161  ns/op
 * SortBenchmark.doFastUtilMergeSort            160  avgt    5     967.457 ±    49.805  ns/op
 * SortBenchmark.doFastUtilMergeSort            640  avgt    5    4106.324 ±   732.560  ns/op
 * SortBenchmark.doFastUtilMergeSort           2560  avgt    5   22565.827 ±  2170.644  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort     10  avgt    5      31.416 ±     5.438  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort     40  avgt    5     505.660 ±    34.658  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort    160  avgt    5    3983.953 ±   114.515  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort    640  avgt    5   23547.037 ±  3607.127  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort   2560  avgt    5  185265.032 ± 11997.773  ns/op
 * SortBenchmark.doFastUtilQuickSort             10  avgt    5      31.946 ±     4.688  ns/op
 * SortBenchmark.doFastUtilQuickSort             40  avgt    5     515.543 ±    47.636  ns/op
 * SortBenchmark.doFastUtilQuickSort            160  avgt    5    3945.819 ±   204.179  ns/op
 * SortBenchmark.doFastUtilQuickSort            640  avgt    5   23015.601 ±  2378.468  ns/op
 * SortBenchmark.doFastUtilQuickSort           2560  avgt    5  187468.882 ± 10601.042  ns/op
 * SortBenchmark.doGrailSort                     10  avgt    5      21.033 ±     5.848  ns/op
 * SortBenchmark.doGrailSort                     40  avgt    5    1596.467 ±    39.295  ns/op
 * SortBenchmark.doGrailSort                    160  avgt    5    9953.656 ±   332.304  ns/op
 * SortBenchmark.doGrailSort                    640  avgt    5   54884.763 ± 42339.419  ns/op
 * SortBenchmark.doGrailSort                   2560  avgt    5  333789.830 ± 34319.123  ns/op
 * SortBenchmark.doJDKSort                       10  avgt    5      30.364 ±     4.454  ns/op
 * SortBenchmark.doJDKSort                       40  avgt    5     127.459 ±    16.661  ns/op
 * SortBenchmark.doJDKSort                      160  avgt    5     557.015 ±    15.137  ns/op
 * SortBenchmark.doJDKSort                      640  avgt    5    2934.185 ±    72.814  ns/op
 * SortBenchmark.doJDKSort                     2560  avgt    5   15243.943 ±  1581.802  ns/op
 * SortBenchmark.doParallelJDKSort               10  avgt    5      30.754 ±     7.376  ns/op
 * SortBenchmark.doParallelJDKSort               40  avgt    5     139.443 ±    16.228  ns/op
 * SortBenchmark.doParallelJDKSort              160  avgt    5     530.800 ±    19.289  ns/op
 * SortBenchmark.doParallelJDKSort              640  avgt    5    2278.514 ±    87.634  ns/op
 * SortBenchmark.doParallelJDKSort             2560  avgt    5   16919.460 ±  1163.633  ns/op
 * </pre>
 * Something about my sorting network code is either wrong or just slow. (Testing only on smallest and largest.)
 * <pre>
 * Benchmark                                  (len)  Mode  Cnt       Score       Error  Units
 * SortBenchmark.doDSSort                        10  avgt    5      22.218 ±     4.027  ns/op
 * SortBenchmark.doDSSort                      2560  avgt    5   26506.745 ±  2665.149  ns/op
 * SortBenchmark.doEttingerSort                  10  avgt    5      36.268 ±     3.476  ns/op
 * SortBenchmark.doEttingerSort                2560  avgt    5   32657.576 ±  1926.769  ns/op
 * SortBenchmark.doFastUtilMergeSort             10  avgt    5      30.977 ±     6.417  ns/op
 * SortBenchmark.doFastUtilMergeSort           2560  avgt    5   23022.791 ±  2496.802  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort     10  avgt    5      32.911 ±     7.789  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort   2560  avgt    5  187682.947 ±  6800.589  ns/op
 * SortBenchmark.doFastUtilQuickSort             10  avgt    5      32.399 ±     5.664  ns/op
 * SortBenchmark.doFastUtilQuickSort           2560  avgt    5  191213.166 ± 13027.216  ns/op
 * SortBenchmark.doGDXSort                       10  avgt    5      29.445 ±     5.965  ns/op
 * SortBenchmark.doGDXSort                     2560  avgt    5   16999.598 ±  1970.929  ns/op
 * SortBenchmark.doGrailSort                     10  avgt    5      21.514 ±     7.656  ns/op
 * SortBenchmark.doGrailSort                   2560  avgt    5  331082.220 ± 26329.563  ns/op
 * SortBenchmark.doJDKSort                       10  avgt    5      26.825 ±     4.176  ns/op
 * SortBenchmark.doJDKSort                     2560  avgt    5   17023.164 ±  1070.560  ns/op
 * SortBenchmark.doNetworkSort                   10  avgt    5     140.368 ±    11.190  ns/op
 * SortBenchmark.doNetworkSort                 2560  avgt    5   43850.717 ± 10284.215  ns/op
 * SortBenchmark.doParallelJDKSort               10  avgt    5      30.953 ±     8.260  ns/op
 * SortBenchmark.doParallelJDKSort             2560  avgt    5   16801.236 ±  1131.503  ns/op
 * </pre>
 * What on Earth is wrong with FastUtil's QuickSort?
 * <pre>
 * Benchmark                           (len)  Mode  Cnt          Score         Error  Units
 * SortBenchmark.doFastUtilMergeSort      10  avgt    5         29.595 ±       5.645  ns/op
 * SortBenchmark.doFastUtilMergeSort    2560  avgt    5      22486.352 ±    1095.960  ns/op
 * SortBenchmark.doFastUtilMergeSort  655360  avgt    5   40680600.033 ± 1703346.781  ns/op
 * SortBenchmark.doFastUtilQuickSort      10  avgt    5         32.102 ±       4.937  ns/op
 * SortBenchmark.doFastUtilQuickSort    2560  avgt    5     184383.374 ±    1730.757  ns/op
 * SortBenchmark.doFastUtilQuickSort  655360  avgt    5  201289079.138 ± 8404552.088  ns/op
 * </pre>
 * Even the parallel QuickSort is slow... This is on a hexacore laptop with hyperthreading; it has 6 cores/12 threads.
 * <pre>
 * Benchmark                                   (len)  Mode  Cnt         Score         Error  Units
 * SortBenchmark.doFastUtilMergeSort              10  avgt    5        29.694 ±       5.211  ns/op
 * SortBenchmark.doFastUtilMergeSort            2560  avgt    5     22699.187 ±    1097.819  ns/op
 * SortBenchmark.doFastUtilMergeSort          655360  avgt    5  41111132.339 ± 1315001.912  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort      10  avgt    5        31.488 ±       4.926  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort    2560  avgt    5    185895.213 ±    9924.799  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort  655360  avgt    5  72263998.569 ± 1616012.603  ns/op
 * </pre>
 * Raising the threshold for insertion sort in SortingNetwork (and making it otherwise identical to FastUtil's merge
 * sort) seems to hurt performance except on very small inputs:
 * <pre>
 * Benchmark                           (len)  Mode  Cnt         Score         Error  Units
 * SortBenchmark.doFastUtilMergeSort      10  avgt    5        30.053 ±       8.076  ns/op
 * SortBenchmark.doFastUtilMergeSort    2560  avgt    5     21162.854 ±    2698.161  ns/op
 * SortBenchmark.doFastUtilMergeSort  655360  avgt    5  42068922.926 ± 5203394.704  ns/op
 * SortBenchmark.doNetworkSort            10  avgt    5        24.024 ±       3.608  ns/op
 * SortBenchmark.doNetworkSort          2560  avgt    5     28068.623 ±     906.025  ns/op
 * SortBenchmark.doNetworkSort        655360  avgt    5  43686477.188 ± 1688989.429  ns/op
 * </pre>
 * Lowering the threshold doesn't help either:
 * <pre>
 * Benchmark                           (len)  Mode  Cnt         Score         Error  Units
 * SortBenchmark.doFastUtilMergeSort      10  avgt    5        29.787 ±       4.839  ns/op
 * SortBenchmark.doFastUtilMergeSort    2560  avgt    5     21853.372 ±    1858.570  ns/op
 * SortBenchmark.doFastUtilMergeSort  655360  avgt    5  41372693.283 ± 2943140.842  ns/op
 * SortBenchmark.doNetworkSort            10  avgt    5        35.618 ±       5.963  ns/op
 * SortBenchmark.doNetworkSort          2560  avgt    5     31365.447 ±    2933.582  ns/op
 * SortBenchmark.doNetworkSort        655360  avgt    5  43048194.273 ± 1232113.309  ns/op
 * </pre>
 * <br>
 * Testing on a newer laptop, 14 cores (6 high-power, 8 low-power), running Windows 11 and using Java 21:
 * <pre>
 * Benchmark                                   (len)  Mode  Cnt          Score          Error  Units
 * SortBenchmark.doDSSort                         10  avgt    5         13.667 ±        3.552  ns/op
 * SortBenchmark.doDSSort                       2560  avgt    5      12416.053 ±     9200.018  ns/op
 * SortBenchmark.doDSSort                     655360  avgt    5   31503884.972 ±  1162891.463  ns/op
 * SortBenchmark.doEttingerSort                   10  avgt    5         20.650 ±        5.524  ns/op
 * SortBenchmark.doEttingerSort                 2560  avgt    5      15392.274 ±     9318.182  ns/op
 * SortBenchmark.doEttingerSort               655360  avgt    5   28065454.009 ±  2489941.310  ns/op
 * SortBenchmark.doFastUtilMergeSort              10  avgt    5         16.736 ±        3.283  ns/op
 * SortBenchmark.doFastUtilMergeSort            2560  avgt    5      12244.880 ±     8092.382  ns/op
 * SortBenchmark.doFastUtilMergeSort          655360  avgt    5   29941269.771 ±  4899589.795  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort      10  avgt    5         17.556 ±        2.947  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort    2560  avgt    5     103691.461 ±    18230.677  ns/op
 * SortBenchmark.doFastUtilParallelQuickSort  655360  avgt    5   37127223.295 ±  6815492.132  ns/op
 * SortBenchmark.doFastUtilQuickSort              10  avgt    5         17.649 ±        2.547  ns/op
 * SortBenchmark.doFastUtilQuickSort            2560  avgt    5     102896.493 ±    19407.837  ns/op
 * SortBenchmark.doFastUtilQuickSort          655360  avgt    5  125416328.638 ± 18769105.048  ns/op
 * SortBenchmark.doGDXSort                        10  avgt    5         16.228 ±        1.614  ns/op
 * SortBenchmark.doGDXSort                      2560  avgt    5      11660.267 ±     8242.570  ns/op
 * SortBenchmark.doGDXSort                    655360  avgt    5   21300313.625 ±  2275226.806  ns/op
 * SortBenchmark.doGrailSort                      10  avgt    5         15.982 ±        3.151  ns/op
 * SortBenchmark.doGrailSort                    2560  avgt    5     266054.562 ±   194898.374  ns/op
 * SortBenchmark.doGrailSort                  655360  avgt    5  527479238.000 ± 56801550.348  ns/op
 * SortBenchmark.doJDKSort                        10  avgt    5         16.214 ±        2.199  ns/op
 * SortBenchmark.doJDKSort                      2560  avgt    5       7786.178 ±     4283.577  ns/op
 * SortBenchmark.doJDKSort                    655360  avgt    5   21939387.232 ±  2163434.786  ns/op
 * SortBenchmark.doNetworkSort                    10  avgt    5         13.529 ±        3.196  ns/op
 * SortBenchmark.doNetworkSort                  2560  avgt    5      13467.183 ±     8928.664  ns/op
 * SortBenchmark.doNetworkSort                655360  avgt    5   31520516.939 ±  1873418.682  ns/op
 * SortBenchmark.doParallelJDKSort                10  avgt    5         16.456 ±        1.910  ns/op
 * SortBenchmark.doParallelJDKSort              2560  avgt    5       7578.079 ±     2018.174  ns/op
 * SortBenchmark.doParallelJDKSort            655360  avgt    5    3838810.276 ±  4885968.328  ns/op
 * </pre>
 * I might try to update GrailSort if there's a newer one, because it seems impossibly slow...
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class SortBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {

        @Param({ "10", "2560", "655360" })
        public int len;
        public String[] words;
        public ObjectList<String> wordList;
        public int idx;
        public WhiskerRandom random = new WhiskerRandom(1000L);
        public Language[] languages = Language.romanizedLanguages;

        public Swapper wordsSwapper = (a, b) -> {
            String t = words[a];
            words[a] = words[b];
            words[b] = t;
        };

        public GrailSort<String> grail = new GrailSort<>(String::compareTo);
        public IntComparator getComp = (a, b) -> words[a].compareTo(words[b]);

        @Setup(Level.Iteration)
        public void setup() {
            words = new String[len];
            for (int i = 0; i < len; i++) {
                words[i] = languages[i & 31].word(random, random.nextBoolean(), random.next(3) + 1);
            }
            wordList = new ObjectList<>(words);
            idx = 0;
        }

    }

    @Benchmark
    public void doFastUtilMergeSort(BenchmarkState state)
    {
        it.unimi.dsi.fastutil.Arrays.mergeSort(0, state.words.length, state.getComp, state.wordsSwapper);
    }

    @Benchmark
    public void doFastUtilQuickSort(BenchmarkState state)
    {
        it.unimi.dsi.fastutil.Arrays.quickSort(0, state.words.length, state.getComp, state.wordsSwapper);
    }

    @Benchmark
    public void doFastUtilParallelQuickSort(BenchmarkState state)
    {
        it.unimi.dsi.fastutil.Arrays.parallelQuickSort(0, state.words.length, state.getComp, state.wordsSwapper);
    }

    @Benchmark
    public void doGrailSort(BenchmarkState state)
    {
        state.grail.grailSortInPlace(state.words, 0, state.words.length);
    }

    @Benchmark
    public void doEttingerSort(BenchmarkState state)
    {
        ObjectComparators.sort(state.wordList, 0, state.words.length, String::compareTo);
    }

    @Benchmark
    public void doDSSort(BenchmarkState state)
    {
        ObjectComparators.sort(state.words, 0, state.words.length, String::compareTo);
    }

    @Benchmark
    public void doNetworkSort(BenchmarkState state)
    {
        SortingNetwork.sort(state.words, 0, state.words.length, String::compareTo);
    }

    @Benchmark
    public void doGDXSort(BenchmarkState state)
    {
        Sort.instance().sort(state.words, String::compareTo, 0, state.words.length);
    }

    @Benchmark
    public void doJDKSort(BenchmarkState state)
    {
        Arrays.sort(state.words, 0, state.words.length, String::compareTo);
    }

    @Benchmark
    public void doParallelJDKSort(BenchmarkState state)
    {
        Arrays.parallelSort(state.words, 0, state.words.length, String::compareTo);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You are expected to see the different run modes for the same benchmark.
     * Note the units are different, scores are consistent with each other.
     *
     * You can run this test:
     *
     * a) Via the command line from the squidlib module's root folder:
     *    $ mvn clean install
     *    $ java -jar benchmarks.jar SortBenchmark
     *
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SortBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(5).warmupTime(TimeValue.seconds(5))
                .measurementIterations(5).measurementTime(TimeValue.seconds(5))
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(1)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }
}