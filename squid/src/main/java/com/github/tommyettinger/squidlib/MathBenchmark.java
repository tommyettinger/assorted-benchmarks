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

import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.digital.BitConversion;
import com.github.tommyettinger.digital.TrigTools;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.squidmath.DiverRNG;
import squidpony.squidmath.HashCommon;
import squidpony.squidmath.NumberTools;

import java.util.concurrent.TimeUnit;

/**
 * This doesn't include two benchmarks for atan2 in degrees, yet, but it has the rest.
 * <pre>
 * Benchmark                                Mode  Cnt    Score   Error  Units
 * MathBenchmark.measureAtan2Baseline       avgt    5    4.654 ± 0.120  ns/op
 * MathBenchmark.measureAtan2BaselineFloat  avgt    5    4.588 ± 0.082  ns/op
 * MathBenchmark.measureBaseline            avgt    5    3.822 ± 0.061  ns/op
 * MathBenchmark.measureBitCos              avgt    5   15.861 ± 0.081  ns/op
 * MathBenchmark.measureBitCosF             avgt    5   15.446 ± 0.325  ns/op
 * MathBenchmark.measureBitSin              avgt    5   16.130 ± 0.332  ns/op
 * MathBenchmark.measureBitSinF             avgt    5   15.081 ± 0.208  ns/op
 * MathBenchmark.measureChristensenASin     avgt    5    6.913 ± 0.093  ns/op
 * MathBenchmark.measureGdxAtan2            avgt    5   20.564 ± 0.397  ns/op
 * MathBenchmark.measureGdxCos              avgt    5    5.722 ± 0.265  ns/op
 * MathBenchmark.measureGdxCosDeg           avgt    5    5.712 ± 0.033  ns/op
 * MathBenchmark.measureGdxSin              avgt    5    5.853 ± 0.218  ns/op
 * MathBenchmark.measureGdxSinDeg           avgt    5    5.517 ± 0.650  ns/op
 * MathBenchmark.measureMathASin            avgt    5  350.587 ± 9.219  ns/op
 * MathBenchmark.measureMathAtan2           avgt    5   89.549 ± 1.727  ns/op
 * MathBenchmark.measureMathCos             avgt    5   18.843 ± 0.202  ns/op
 * MathBenchmark.measureMathCosDeg          avgt    5   19.445 ± 0.631  ns/op
 * MathBenchmark.measureMathSin             avgt    5   18.911 ± 0.303  ns/op
 * MathBenchmark.measureMathSinDeg          avgt    5   19.759 ± 0.333  ns/op
 * MathBenchmark.measureSquidASin           avgt    5   18.478 ± 0.270  ns/op
 * MathBenchmark.measureSquidAtan2          avgt    5   24.930 ± 0.832  ns/op
 * MathBenchmark.measureSquidAtan2Float     avgt    5   22.373 ± 0.323  ns/op
 * MathBenchmark.measureSquidCos            avgt    5    9.148 ± 0.104  ns/op
 * MathBenchmark.measureSquidCosDeg         avgt    5    9.085 ± 0.102  ns/op
 * MathBenchmark.measureSquidCosF           avgt    5    9.199 ± 0.399  ns/op
 * MathBenchmark.measureSquidSin            avgt    5    8.588 ± 0.164  ns/op
 * MathBenchmark.measureSquidSinDeg         avgt    5    8.352 ± 0.168  ns/op
 * MathBenchmark.measureSquidSinF           avgt    5    8.327 ± 0.083  ns/op
 * </pre>
 * <br>
 * And here's just the atan2() methods, two measured in degrees and marked with a *:
 * <pre>
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureAtan2Baseline       avgt    5   4.414 ± 0.019  ns/op
 * MathBenchmark.measureAtan2BaselineFloat  avgt    5   4.380 ± 0.537  ns/op
 * MathBenchmark.measureGdxAtan2            avgt    5  20.574 ± 0.084  ns/op
 * MathBenchmark.measureGdxAtan2Deg         avgt    5  36.738 ± 0.204  ns/op *
 * MathBenchmark.measureGtAtan2             avgt    5  21.665 ± 0.086  ns/op
 * MathBenchmark.measureImuli2Atan2         avgt    5  18.459 ± 1.160  ns/op
 * MathBenchmark.measureImuliAtan2          avgt    5  18.588 ± 0.055  ns/op
 * MathBenchmark.measureMathAtan2           avgt    5  84.168 ± 1.465  ns/op
 * MathBenchmark.measureNtAtan2             avgt    5  19.847 ± 1.796  ns/op
 * MathBenchmark.measureSquidAtan2          avgt    5  23.232 ± 0.856  ns/op
 * MathBenchmark.measureSquidAtan2DegFloat  avgt    5  23.690 ± 2.294  ns/op *
 * MathBenchmark.measureSquidAtan2Float     avgt    5  24.059 ± 2.169  ns/op
 * </pre>
 * Here, Imuli has the best speed without sacrificing quality, but Gt has slightly better
 * quality and slightly worse speed. Imuli doesn't use a LUT, Gt does.
 * <br>
 * Another benchmark of atan2() methods, on newer hardware with Java 8 Hotspot:
 * <pre>
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureAtan2Baseline       avgt   10   3.351 ± 0.110  ns/op
 * MathBenchmark.measureAtan2BaselineFloat  avgt   10   3.263 ± 0.051  ns/op
 * MathBenchmark.measureFunkyAtan2          avgt   10  12.506 ± 0.371  ns/op
 * MathBenchmark.measureFunkyAtan2_         avgt   10  12.314 ± 0.140  ns/op
 * MathBenchmark.measureGdxAtan2            avgt   10  15.109 ± 0.400  ns/op
 * MathBenchmark.measureGdxAtan2Deg         avgt   10  27.244 ± 0.237  ns/op
 * MathBenchmark.measureGeneralAtan2        avgt   10  14.801 ± 0.225  ns/op
 * MathBenchmark.measureGtAtan2             avgt   10  15.867 ± 0.172  ns/op
 * MathBenchmark.measureImuliAtan2          avgt   10  14.259 ± 0.357  ns/op
 * MathBenchmark.measureImuliAtan2_         avgt   10  16.900 ± 0.268  ns/op
 * MathBenchmark.measureMathAtan2           avgt   10  61.638 ± 0.844  ns/op
 * MathBenchmark.measureMathAtan2_          avgt   10  64.977 ± 1.369  ns/op
 * MathBenchmark.measureNtAtan2             avgt   10  14.288 ± 0.202  ns/op
 * MathBenchmark.measureSimpleAtan2         avgt   10  14.613 ± 0.266  ns/op
 * MathBenchmark.measureSimpleAtan2_        avgt   10  14.207 ± 0.211  ns/op
 * MathBenchmark.measureSquidAtan2          avgt   10  14.326 ± 0.173  ns/op
 * MathBenchmark.measureSquidAtan2DegFloat  avgt   10  16.884 ± 0.272  ns/op
 * MathBenchmark.measureSquidAtan2Float     avgt   10  14.845 ± 1.343  ns/op
 * MathBenchmark.measureSquidAtan2_         avgt   10  16.842 ± 0.242  ns/op
 * </pre>
 * Here, most but {@link Math#atan2(double, double)} are very close in speed,
 * but {@link NumberTools2#atan2Simple(float, float)} is unusually fast.
 * Gt has the lowest speed of the others, but is the most precise as an
 * approximation. The next-best precision is also the fastest, "Funky" (tied
 * with "Simple" on quality, but faster), so it may be the best mix of high
 * precision and speed. The Funky version works by calling an approximation
 * of atan() as one would expect for atan2 from Wikipedia's description, and
 * this turns out to optimize well. The Simple version is very similar, but
 * does slightly more work in the atan() call, and this slows it down.
 * <br>
 * The "Funky" atan2_(), using turns instead of radians, is also fastest.
 * It uses a fairly strange atn_() function that doesn't use the same kind
 * of turn measurement, but it's also the most precise approximation.
 * <br>
 * Benchmarking fastFloor() methods; the comment in GustavsonSimplexNoise:
 * {@code // This method is a *lot* faster than using (int)Math.floor(x)}
 * is incorrect for this benchmark result with Java 15. Any of these can,
 * at most, say they are a *little* faster than casting Math.floor(), and
 * in many of the trials I did, Gustavson's fastfloor() was the slowest.
 * The implementations used by SquidLib (FN and Noise) are, by sheer dumb
 * luck, the fastest, but only by about 5% on an already-fast method.
 * <pre>
 * Benchmark                        Mode  Cnt  Score   Error  Units
 * MathBenchmark.measureFloorFN     avgt    6  3.207 ± 0.090  ns/op
 * MathBenchmark.measureFloorGust   avgt    6  3.330 ± 0.154  ns/op
 * MathBenchmark.measureFloorMath   avgt    6  3.353 ± 0.019  ns/op
 * MathBenchmark.measureFloorNoise  avgt    6  3.226 ± 0.125  ns/op
 * </pre>
 * <br>
 * Benchmarking just some competitive atan2() approximations, plus Math's
 * non-approximate version to compare. On Java 8, HotSpot:
 * <pre>
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureGdxAtan2            avgt    5  14.689 ± 0.686  ns/op
 * MathBenchmark.measureHighPrecisionAtan2  avgt    5  14.673 ± 0.030  ns/op
 * MathBenchmark.measureMathAtan2           avgt    5  60.481 ± 1.998  ns/op
 * MathBenchmark.measureSquidAtan2          avgt    5  12.423 ± 0.973  ns/op
 * </pre>
 * <br>
 * And on Java 16, HotSpot (load may be a little higher this time):
 * <pre>
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureGdxAtan2            avgt    5  15.715 ± 0.656  ns/op
 * MathBenchmark.measureHighPrecisionAtan2  avgt    5  14.843 ± 0.426  ns/op
 * MathBenchmark.measureMathAtan2           avgt    5  63.574 ± 2.227  ns/op
 * MathBenchmark.measureSquidAtan2          avgt    5  12.395 ± 0.453  ns/op
 * </pre>
 * <br>
 * Measuring tan() on Java 18:
 * <br>
 * <pre>
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDivideTan  avgt    5   5.217 ± 0.566  ns/op
 * MathBenchmark.measureMathTan    avgt    5  17.659 ± 1.176  ns/op
 * MathBenchmark.measureSoontsTan  avgt    5   7.824 ± 0.646  ns/op
 * </pre>
 * <br>
 * And on Java 8:
 * <pre>
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDivideTan  avgt    5   4.605 ± 0.219  ns/op
 * MathBenchmark.measureMathTan    avgt    5  51.494 ± 2.440  ns/op
 * MathBenchmark.measureSoontsTan  avgt    5  13.219 ± 0.858  ns/op
 * </pre>
 * Measuring sine approximations; there were some issues during testing that might be resolvable.
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF  avgt   32  15.087 ± 1.335  ns/op
 * MathBenchmark.measureBitSinF       avgt   32  13.332 ± 2.200  ns/op
 * MathBenchmark.measureGdxSinF       avgt   32   3.796 ± 0.088  ns/op
 * MathBenchmark.measureSquidSinF     avgt   32   8.623 ± 1.084  ns/op
 * </pre>
 * The previous results seem especially suspicious when compared with this tiny change, run on Java 17 instead
 * of 18 and with one function changed from an approximation to Math.ceil().
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF  avgt   16   7.151 ± 0.148  ns/op
 * MathBenchmark.measureBitSinF       avgt   16  10.737 ± 0.139  ns/op
 * MathBenchmark.measureGdxSinF       avgt   16   3.795 ± 0.073  ns/op
 * MathBenchmark.measureSquidSinF     avgt   16  10.366 ± 0.098  ns/op
 * </pre>
 * Testing the embedded-systems sin() approximation by Andrew Steadman, it looks like floating-point math is comparably
 * faster on PCs relative to tiny systems, enough so that the almost-all-int-math approximation is a bit slower than
 * the Bhaskara I-based approximation. Steadman's is also a tiny bit less accurate, sadly, so no win for it here.
 * <pre>
 * Benchmark                          Mode  Cnt  Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF  avgt    5  7.226 ± 0.156  ns/op
 * MathBenchmark.measureGdxSinF       avgt    5  3.749 ± 0.249  ns/op
 * MathBenchmark.measureSteadmanSinF  avgt    5  8.633 ± 0.151  ns/op
 * </pre>
 * With a simpler baseline, the benchmarks changed how often they deoptimize (it seems less frequent now).
 * This tests just the newest methods, including the very precise (and a little slow) sinLerp. Java 8:
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF  avgt    5  17.773 ± 0.414  ns/op
 * MathBenchmark.measureGdxSinF       avgt    5   2.889 ± 0.140  ns/op
 * MathBenchmark.measureLerpSinF      avgt    5  10.246 ± 0.153  ns/op
 * MathBenchmark.measureMathSinF      avgt    5  41.414 ± 2.016  ns/op
 * MathBenchmark.measureSquidSinF     avgt    5   6.519 ± 0.235  ns/op
 * </pre>
 * Java 19:
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF  avgt    5   7.016 ± 0.168  ns/op
 * MathBenchmark.measureGdxSinF       avgt    5   3.288 ± 0.091  ns/op
 * MathBenchmark.measureLerpSinF      avgt    5  10.315 ± 0.268  ns/op
 * MathBenchmark.measureMathSinF      avgt    5  19.825 ± 0.584  ns/op
 * MathBenchmark.measureSquidSinF     avgt    5   6.896 ± 0.919  ns/op
 * </pre>
 * tan() benchmarks, on Java 8:
 * <pre>
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDivideTan  avgt    5   4.052 ± 0.108  ns/op
 * MathBenchmark.measureLerpTan    avgt    5  13.621 ± 0.158  ns/op
 * MathBenchmark.measureMathTan    avgt    5  59.677 ± 1.474  ns/op
 * MathBenchmark.measureSoontsTan  avgt    5   9.273 ± 0.286  ns/op
 * MathBenchmark.measureTableTan   avgt    5   3.674 ± 0.183  ns/op
 * </pre>
 * and on Java 19:
 * <pre>
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDivideTan  avgt    5   4.473 ± 0.964  ns/op
 * MathBenchmark.measureLerpTan    avgt    5   6.499 ± 0.105  ns/op
 * MathBenchmark.measureMathTan    avgt    5  21.985 ± 1.417  ns/op
 * MathBenchmark.measureSoontsTan  avgt    5   7.391 ± 0.364  ns/op
 * MathBenchmark.measureTableTan   avgt    5   3.892 ± 0.345  ns/op
 * </pre>
 * <br>
 * Benchmarking various float to int floor implementations on Java 8:
 * <pre>
 * Benchmark                                Mode  Cnt  Score   Error  Units
 * MathBenchmark.measureFloorBitAbs         avgt    5  3.068 ± 0.027  ns/op
 * MathBenchmark.measureFloorBitArithmetic  avgt    5  5.801 ± 0.124  ns/op
 * MathBenchmark.measureFloorBitCorrect     avgt    5  4.146 ± 0.122  ns/op
 * MathBenchmark.measureFloorFloatAbs       avgt    5  3.979 ± 0.189  ns/op
 * MathBenchmark.measureFloorGdx            avgt    5  2.776 ± 0.153  ns/op
 * MathBenchmark.measureFloorGust           avgt    5  2.990 ± 0.087  ns/op
 * MathBenchmark.measureFloorIncorrect      avgt    5  2.569 ± 0.156  ns/op
 * MathBenchmark.measureFloorMath           avgt    5  6.623 ± 0.321  ns/op
 * </pre>
 * <br>
 * And on Java 19:
 * <pre>
 * Benchmark                                Mode  Cnt  Score   Error  Units
 * MathBenchmark.measureFloorBitAbs         avgt    5  3.210 ± 0.132  ns/op
 * MathBenchmark.measureFloorBitArithmetic  avgt    5  5.955 ± 0.072  ns/op
 * MathBenchmark.measureFloorBitCorrect     avgt    5  3.797 ± 0.135  ns/op
 * MathBenchmark.measureFloorFloatAbs       avgt    5  4.163 ± 0.149  ns/op
 * MathBenchmark.measureFloorGdx            avgt    5  2.925 ± 0.123  ns/op
 * MathBenchmark.measureFloorGust           avgt    5  3.179 ± 0.179  ns/op
 * MathBenchmark.measureFloorIncorrect      avgt    5  2.845 ± 0.262  ns/op
 * MathBenchmark.measureFloorMath           avgt    5  5.155 ± 0.039  ns/op
 * </pre>
 * Testing just sine approximations on Java 20:
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF  avgt    5   7.499 ± 0.041  ns/op
 * MathBenchmark.measureBitSinF       avgt    5  13.616 ± 0.081  ns/op
 * MathBenchmark.measureGdxSinF       avgt    5   3.123 ± 0.025  ns/op
 * MathBenchmark.measureHastingsSinF  avgt    5  20.384 ± 0.101  ns/op
 * MathBenchmark.measureLerpSinF      avgt    5  10.615 ± 0.070  ns/op
 * MathBenchmark.measureMathSinF      avgt    5  20.021 ± 0.277  ns/op
 * MathBenchmark.measureSquidSinF     avgt    5  14.379 ± 0.181  ns/op
 * MathBenchmark.measureSteadmanSinF  avgt    5   8.538 ± 0.049  ns/op
 * </pre>
 * Several of these started off faster, but deoptimized or slowed down due to machine temperature.
 * <br>
 * More sine approximations on Java 20:
 * <pre>
 * Benchmark                           Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureBhaskaraSinF   avgt    5   7.340 ± 0.112  ns/op
 * MathBenchmark.measureBitSinF        avgt    5  13.473 ± 0.127  ns/op
 * MathBenchmark.measureBonusSinF      avgt    5   3.790 ± 0.096  ns/op
 * MathBenchmark.measureDigitalSinF    avgt    5   6.081 ± 0.046  ns/op
 * MathBenchmark.measureGdxSinF        avgt    5   3.146 ± 0.172  ns/op
 * MathBenchmark.measureLerpSinF       avgt    5  10.625 ± 0.355  ns/op
 * MathBenchmark.measureMathSinF       avgt    5  19.823 ± 0.168  ns/op
 * MathBenchmark.measureRoundSinF      avgt    5   5.000 ± 0.208  ns/op
 * MathBenchmark.measureShiftySinF     avgt    5   3.796 ± 0.134  ns/op
 * MathBenchmark.measureSquidSinF      avgt    5  14.357 ± 0.133  ns/op
 * MathBenchmark.measureUnroundedSinF  avgt    5   3.167 ± 0.148  ns/op
 * </pre>
 * Shifty and Bonus need to be evaluated for how much more accurate they are than Unrounded.
 * (Bonus is terrible, Shifty isn't bad at all).
 * <br>
 * Just the fastest sin and cos approximations:
 * <pre>
 * Benchmark                         Mode  Cnt  Score   Error  Units
 * MathBenchmark.measureCToolsCosF   avgt    5  3.506 ± 0.092  ns/op
 * MathBenchmark.measureCToolsSinF   avgt    5  7.281 ± 0.053  ns/op
 * MathBenchmark.measureDigitalCosF  avgt    5  6.708 ± 0.109  ns/op
 * MathBenchmark.measureDigitalSinF  avgt    5  3.786 ± 0.140  ns/op
 * MathBenchmark.measureGdxCosF      avgt    5  6.131 ± 0.155  ns/op
 * MathBenchmark.measureGdxSinF      avgt    5  3.138 ± 0.144  ns/op
 * </pre>
 * These next results don't make any sense; the BumbleBench ones are much more reasonable.
 * For digital, sin() is faster than cos() but sinSmoother() is slower than cosSmoother().
 * For CosTools, cos() is faster than sin() and cosSmoother() is slower than sinSmoother().
 * What.
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureCToolsCosF           avgt    5   3.538 ± 0.088  ns/op
 * MathBenchmark.measureCToolsSinF           avgt    5   7.241 ± 0.138  ns/op
 * MathBenchmark.measureCToolsSmootherCosF   avgt    5   8.595 ± 0.164  ns/op
 * MathBenchmark.measureCToolsSmootherSinF   avgt    5   4.703 ± 0.106  ns/op
 * MathBenchmark.measureDigitalCosF          avgt    5   6.786 ± 0.340  ns/op
 * MathBenchmark.measureDigitalSinF          avgt    5   3.761 ± 0.218  ns/op
 * MathBenchmark.measureDigitalSmootherCosF  avgt    5   5.335 ± 0.166  ns/op
 * MathBenchmark.measureDigitalSmootherSinF  avgt    5  10.568 ± 0.183  ns/op
 * MathBenchmark.measureGdxCosF              avgt    5   6.104 ± 0.065  ns/op
 * MathBenchmark.measureGdxSinF              avgt    5   3.059 ± 0.079  ns/op
 * </pre>
 * More nonsense results...
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDigitalSinF          avgt   10   7.287 ± 0.195  ns/op
 * MathBenchmark.measureDigitalSmootherSinF  avgt   10   4.440 ± 0.042  ns/op
 * MathBenchmark.measureFFSinF               avgt   10   6.459 ± 0.012  ns/op
 * MathBenchmark.measureFFSmootherSinF       avgt   10  10.625 ± 0.049  ns/op
 * MathBenchmark.measureGdxSinF              avgt   10   3.057 ± 0.038  ns/op
 * MathBenchmark.measureMathSinF             avgt   10  19.836 ± 0.140  ns/op
 * </pre>
 * Testing a select few sine methods with Graal 17...
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDigitalSinF          avgt    5   7.412 ± 0.070  ns/op
 * MathBenchmark.measureDigitalSmootherSinF  avgt    5   9.936 ± 0.243  ns/op
 * MathBenchmark.measureFloatySinF           avgt    5   6.205 ± 0.116  ns/op
 * MathBenchmark.measureGdxSinF              avgt    5   5.058 ± 0.138  ns/op
 * MathBenchmark.measureMathSinF             avgt    5  19.896 ± 0.157  ns/op
 * MathBenchmark.measureRoundSinF            avgt    5   6.121 ± 0.127  ns/op
 * </pre>
 * And now testing a select few sine methods with Graal 20 (wow, fast!)...
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDigitalSinF          avgt    5   3.887 ± 0.117  ns/op
 * MathBenchmark.measureDigitalSmootherSinF  avgt    5   5.106 ± 0.139  ns/op
 * MathBenchmark.measureFloatySinF           avgt    5   3.811 ± 0.232  ns/op
 * MathBenchmark.measureGdxSinF              avgt    5   3.427 ± 0.094  ns/op
 * MathBenchmark.measureMathSinF             avgt    5  13.572 ± 0.718  ns/op
 * MathBenchmark.measureRoundSinF            avgt    5   5.000 ± 0.210  ns/op
 * </pre>
 * Testing a select few cosine methods with Graal 17...
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDigitalCosF          avgt    5   6.509 ± 0.167  ns/op
 * MathBenchmark.measureDigitalSmootherCosF  avgt    5   8.578 ± 0.067  ns/op
 * MathBenchmark.measureFloatyCosF           avgt    5   6.252 ± 0.091  ns/op
 * MathBenchmark.measureGdxCosF              avgt    5   6.292 ± 0.788  ns/op
 * MathBenchmark.measureMathCosF             avgt    5  20.245 ± 1.938  ns/op
 * MathBenchmark.measureRoundCosF            avgt    5   6.160 ± 0.173  ns/o
 * </pre>
 * And now testing a select few cosine methods with Graal 20 (wow, fast again!)...
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureDigitalCosF          avgt    5   3.843 ± 0.159  ns/op
 * MathBenchmark.measureDigitalSmootherCosF  avgt    5   4.600 ± 0.045  ns/op
 * MathBenchmark.measureFloatyCosF           avgt    5   3.581 ± 0.090  ns/op
 * MathBenchmark.measureGdxCosF              avgt    5   3.616 ± 0.064  ns/op
 * MathBenchmark.measureMathCosF             avgt    5  13.108 ± 0.347  ns/op
 * MathBenchmark.measureRoundCosF            avgt    5   4.999 ± 0.367  ns/op
 * </pre>
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class MathBenchmark {

    private final FastSinCos fastSinCos = FastSinCos.getTable();
    private final double[] inputs = new double[65536];
    private final float[] floatInputs = new float[65536];
    private final int[] intInputs = new int[65536];
    private final double[] arcInputs = new double[65536];
    {
        for (int i = 0; i < 65536; i++) {
            intInputs[i] = (int)(floatInputs[i] = (float) (inputs[i] =
                    (DiverRNG.determine(i) >> 10) * 0x1p-41
            ));
            arcInputs[i] = (DiverRNG.determine(~i) >> 10) * 0x1p-53;
        }
    }
    public static double asin(double a)
    {
        return (a * (1.0 + (a *= a) * (-0.141514171442891431 + a * -0.719110791477959357)))
                / (1.0 + a * (-0.439110389941411144 + a * -0.471306172023844527));
    }

    private int mathCos = -0x8000;
    private int mathSin = -0x8000;
    private int mathCosF = -0x8000;
    private int mathSinF = -0x8000;
    private int mathTan = -0x8000;
    private int mathASin = -0x8000;
    private int asinChristensen = -0x8000;
    private int asinSquid = -0x8000;
    private int cosOld = -0x8000;
    private int sinOld = -0x8000;
    private int sinNick = -0x8000;
    private int cosNick = -0x8000;
    private int sinBit = -0x8000;
    private int cosBit = -0x8000;
    private int sinBitF = -0x8000;
    private int cosBitF = -0x8000;
    private int cosFloat = -0x8000;
    private int sinFloat = -0x8000;
    private int cosGdx = -0x8000;
    private int sinGdx = -0x8000;
    private int cosWallace = -0x8000;
    private int sinWallace = -0x8000;
    private int sinBhaskara = -0x8000;
    private int sinBonus = -0x8000;
    private int sinSteadman = -0x8000;
    private int sinHastings = -0x8000;
    private int sinRound = -0x8000;
    private int cosRound = -0x8000;
    private int sinUnrounded = -0x8000;
    private int sinSign = -0x8000;
    private int sinShifty = -0x8000;
    private int sinDigital = -0x8000;
    private int sinSmootherDigital = -0x8000;
    private int sinCTools = -0x8000;
    private int sinSmootherCTools = -0x8000;
    private int sinFF = -0x8000;
    private int sinFloaty = -0x8000;
    private int cosFloaty = -0x8000;
    private int sinSmootherFF = -0x8000;
    private int cosDigital = -0x8000;
    private int cosSmootherDigital = -0x8000;
    private int cosCTools = -0x8000;
    private int cosSmootherCTools = -0x8000;
    private int sinSplit = -0x8000;
    private int sinTT2 = -0x8000;
    private int sinLerp = -0x8000;
    private int mathCosDeg = -0x8000;
    private int mathSinDeg = -0x8000;
    private int sinNickDeg = -0x8000;
    private int cosNickDeg = -0x8000;
    private int cosGdxDeg = -0x8000;
    private int sinGdxDeg = -0x8000;
    private int cosDigitalDeg = -0x8000;
    private int sinDigitalDeg = -0x8000;
    private int cosFSCDeg = -0x8000;
    private int sinFSCDeg = -0x8000;
    private int tanDiv = -0x8000;
    private int tanSoo = -0x8000;
    private int tanLer = -0x8000;
    private int tanTab = -0x8000;
    private int baseline = -0x8000;
    private int mathAtan2X = -0x4000;
    private int mathAtan2Y = -0x8000;
    private int mathAtan2_X = -0x4000;
    private int mathAtan2_Y = -0x8000;
    private int atan2SquidX = -0x4000;
    private int atan2SquidY = -0x8000;
    private int atan2SquidXF = -0x4000;
    private int atan2SquidYF = -0x8000;
    private int atan2GdxX = -0x4000;
    private int atan2GdxY = -0x8000;
    private int atan2GtX = -0x4000;
    private int atan2GtY = -0x8000;
    private int atan2NtX = -0x4000;
    private int atan2NtY = -0x8000;
    private int atan2ImX = -0x4000;
    private int atan2ImY = -0x8000;
    private int atan2HPX = -0x4000;
    private int atan2HPY = -0x8000;
    private int atan2RmX = -0x4000;
    private int atan2RmY = -0x8000;
    private int atan2Im_X = -0x4000;
    private int atan2Im_Y = -0x8000;
    private int atan2Si_X = -0x4000;
    private int atan2Si_Y = -0x8000;
    private int atan2Fn_X = -0x4000;
    private int atan2Fn_Y = -0x8000;
    private int atan2GeX = -0x4000;
    private int atan2GeY = -0x8000;
    private int atan2SiX = -0x4000;
    private int atan2SiY = -0x8000;
    private int atan2FnX = -0x4000;
    private int atan2FnY = -0x8000;
    private int atan2_SquidXF = -0x4000;
    private int atan2_SquidYF = -0x8000;
    private int atan2DegSquidXF = -0x4000;
    private int atan2DegSquidYF = -0x8000;
    private int atan2DegGdxX = -0x4000;
    private int atan2DegGdxY = -0x8000;
    private int atan2BaselineX = -0x4000;
    private int atan2BaselineY = -0x8000;
    private int atan2BaselineXF = -0x4000;
    private int atan2BaselineYF = -0x8000;
    
    private int npotHC = 0;
    private int npotM = 0;
    private int npotCLZ = 0;

    private int floorMath = 0;
    private int floorGust = 0;
    private int floorNoise = 0;
    private int floorGdx = 0;
    private int floorBfc = 0;
    private int floorBfr = 0;
    private int floorBfa = 0;
    private int floorFfa = 0;

    @Benchmark
    public double measureBaseline()
    {
        return ((baseline += 0x9E3779B9) >> 24);
    }



    @Benchmark
    public double measureMathCos()
    {
        return Math.cos(((mathCos += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureMathSin()
    {
        return Math.sin(((mathSin += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureMathCosF()
    {
        return (float)Math.cos(((mathCos += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureMathSinF()
    {
        return (float)Math.sin(((mathSin += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureMathTan()
    {
        return Math.tan(((mathTan += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureMathASin()
    {
        return Math.asin(arcInputs[mathASin++ & 0xFFFF]);
    }

    @Benchmark
    public double measureChristensenASin()
    {
        return asin(arcInputs[asinChristensen++ & 0xFFFF]);
    }

    @Benchmark
    public double measureSquidASin()
    {
        return NumberTools.asin(arcInputs[asinSquid++ & 0xFFFF]);
    }


    //@Benchmark
    public double measureCosApproxOld() {
        return cosOld(((cosOld += 0x9E3779B9) >> 24));
//        cosOld += 0.0625;
//        final long s = Double.doubleToLongBits(cosOld * 0.3183098861837907 + (cosOld < 0.0 ? -2.0 : 2.0)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
//        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L) >> 51)) & 0xfffffffffffffL) | 0x4000000000000000L) - 2.0);
//        return a * a * (3.0 - 2.0 * a) * -2.0 + 1.0;
    }

    //@Benchmark
    public double measureSinApproxOld() {
        return sinOld(((sinOld += 0x9E3779B9) >> 24));
//        sinOld += 0.0625;
//        final long s = Double.doubleToLongBits(sinOld * 0.3183098861837907 + (sinOld < -1.5707963267948966 ? -1.5 : 2.5)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
//        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L) >> 51)) & 0xfffffffffffffL) | 0x4000000000000000L) - 2.0);
//        return a * a * (3.0 - 2.0 * a) * 2.0 - 1.0;
    }

    private static double sinOld(final double radians)
    {
        final long s = Double.doubleToLongBits(radians * 0.3183098861837907 + (radians < -1.5707963267948966 ? -1.5 : 2.5)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L) >> 51)) & 0xfffffffffffffL) | 0x4000000000000000L) - 2.0);
        return a * a * (3.0 - 2.0 * a) * 2.0 - 1.0;
    }

    private static float sinOld(final float radians)
    {
        final int s = Float.floatToIntBits(radians * 0.3183098861837907f + (radians < -1.5707963267948966f ? -1.5f : 2.5f)), m = (s >>> 23 & 0xFF) - 0x80, sm = s << m;
        final float a = (Float.intBitsToFloat(((sm ^ -((sm & 0x00400000)>>22)) & 0x007fffff) | 0x40000000) - 2f);
        return a * a * (3f - 2f * a) * 2f - 1f;
    }

    private static double cosOld(final double radians)
    {
        final long s = Double.doubleToLongBits(radians * 0.3183098861837907 + (radians < 0.0 ? -2.0 : 2.0)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L) >> 51)) & 0xfffffffffffffL) | 0x4000000000000000L) - 2.0);
        return a * a * (3.0 - 2.0 * a) * -2.0 + 1.0;
    }

    private static float cosOld(final float radians)
    {
        final int s = Float.floatToIntBits(radians * 0.3183098861837907f + (radians < 0f ? -2f : 2f)), m = (s >>> 23 & 0xFF) - 0x80, sm = s << m;
        final float a = (Float.intBitsToFloat(((sm ^ -((sm & 0x00400000)>>22)) & 0x007fffff) | 0x40000000) - 2f);
        return a * a * (3f - 2f * a) * -2f + 1f;
    }

//    @Benchmark
    public float measureSquidCosF() {
        return NumberTools.cos(((cosFloat += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureSquidSinF() {
        return NumberTools.sin(((sinFloat += 0x9E3779B9) >> 24));
    }
//    private double sinNick = 1.0;
//    @Benchmark
//    public double measureSinApproxNick()
//    {
//        double a = Math.abs(sinNick += 0.0625), n = (a % 3.141592653589793);
//        n *= 1.2732395447351628 - 0.4052847345693511 * n;
//        return n * (0.775 + 0.225 * n) * Math.signum(((a + 3.141592653589793) % 6.283185307179586) - 3.141592653589793) * Math.signum(sinNick);
//    }


    /**
     * Sine approximation code from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick".
     * @return a close approximation of the sine of an internal variable this changes by 0.0625 each time
     */
    @Benchmark
    public double measureSquidSin()
    {
        return NumberTools.sin(((sinNick += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureSquidCos()
    {
        return  NumberTools.cos(((cosNick += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureBitSin()
    {
        return sinBit(((sinBit += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureBitCos()
    {
        return cosBit(((cosBit += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureBitSinF()
    {
        return sinBit(((sinBitF += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureBitCosF()
    {
        return cosBit(((cosBitF += 0x9E3779B9) >> 24));
    }
    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 4x and 40x
     * faster sin() calls in benchmarking, depending on whether HotSpot deoptimizes Math.sin() for its own inscrutable
     * reasons), and both takes and returns doubles. Takes the same arguments Math.sin() does, so one angle in radians,
     * which may technically be any double (but this will lose precision on fairly large doubles, such as those that
     * are larger than about 65536.0). This is closely related to {@link NumberTools#sway(float)}, but the shape of the output when
     * graphed is almost identical to sin().  The difference between the result of this method and
     * {@link Math#sin(double)} should be under 0.001 at all points between -pi and pi, with an average difference of
     * about 0.0005; not all points have been checked for potentially higher errors, though. Coercion between float and
     * double takes about as long as this method normally takes to run, so if you have floats you should usually use
     * methods that take floats (or return floats, if assigning the result to a float), and likewise for doubles.
     * <br>
     * If you call this frequently, consider giving it either all positive numbers, i.e. 0 to PI * 2 instead of -PI to
     * PI; this can help the performance of this particular approximation by making its one branch easier to predict.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range,
     * using code extremely similar to {@link NumberTools#zigzag(double)}.
     * @param radians an angle in radians as a double, often from 0 to pi * 2, though not required to be.
     * @return the sine of the given angle, as a double between -1.0 and 1.0 (probably exclusive on -1.0, but not 1.0)
     */
    public static double sinBit(final double radians)
    {
        long sign, s;
        if(radians < 0.0) {
            s = Double.doubleToLongBits(radians * 0.3183098861837907 - 2.0);
            sign = 1L;
        }
        else {
            s = Double.doubleToLongBits(radians * 0.3183098861837907 + 2.0);
            sign = -1L;
        }
        final long m = (s >>> 52 & 0x7FFL) - 0x400L, sm = s << m, sn = -((sm & 0x8000000000000L) >> 51);
        double n = (Double.longBitsToDouble(((sm ^ sn) & 0xfffffffffffffL) | 0x4010000000000000L) - 4.0);
        n *= 2.0 - n;
        return n * (-0.775 - 0.225 * n) * ((sn ^ sign) | 1L);
    }

    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 4x and 40x
     * faster sin() calls in benchmarking, depending on whether HotSpot deoptimizes Math.sin() for its own inscrutable
     * reasons), and both takes and returns floats. Takes the same arguments Math.sin() does, so one angle in radians,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than about 4096f). This is closely related to {@link NumberTools#sway(float)}, but the shape of the output when
     * graphed is almost identical to sin(). The difference between the result of this method and
     * {@link Math#sin(double)} should be under 0.001 at all points between -pi and pi, with an average difference of
     * about 0.0005; not all points have been checked for potentially higher errors, though. The error for this float
     * version is extremely close to the double version, {@link NumberTools#sin(double)}, so you should choose based on what type
     * you have as input and/or want to return rather than on quality concerns. Coercion between float and double takes
     * about as long as this method normally takes to run, so if you have floats you should usually use methods that
     * take floats (or return floats, if assigning the result to a float), and likewise for doubles.
     * <br>
     * If you call this frequently, consider giving it either all positive numbers, i.e. 0 to PI * 2 instead of -PI to
     * PI; this can help the performance of this particular approximation by making its one branch easier to predict.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range,
     * using code extremely similar to {@link NumberTools#zigzag(float)}.
     * @param radians an angle in radians as a float, often from 0 to pi * 2, though not required to be.
     * @return the sine of the given angle, as a float between -1f and 1f (probably exclusive on -1f, but not 1f)
     */
    public static float sinBit(final float radians)
    {
        int sign, s;
        if(radians < 0.0f) {
            s = Float.floatToIntBits(radians * 0.3183098861837907f - 2f);
            sign = 1;
        }
        else {
            s = Float.floatToIntBits(radians * 0.3183098861837907f + 2f);
            sign = -1;
        }
        final int m = (s >>> 23 & 0xFF) - 0x80, sm = s << m, sn = -((sm & 0x00400000) >> 22);
        float n = (Float.intBitsToFloat(((sm ^ sn) & 0x007fffff) | 0x40800000) - 4f);
        n *= 2f - n;
        return n * (-0.775f - 0.225f * n) * ((sn ^ sign) | 1);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 4x and 40x
     * faster cos() calls in benchmarking, depending on whether HotSpot deoptimizes Math.cos() for its own inscrutable
     * reasons), and both takes and returns doubles. Takes the same arguments Math.cos() does, so one angle in radians,
     * which may technically be any double (but this will lose precision on fairly large doubles, such as those that
     * are larger than about 65536.0). This is closely related to {@link NumberTools#sway(float)}, but the shape of the output when
     * graphed is almost identical to cos(). The difference between the result of this method and
     * {@link Math#cos(double)} should be under 0.001 at all points between -pi and pi, with an average difference of
     * about 0.0005; not all points have been checked for potentially higher errors, though.Coercion between float and
     * double takes about as long as this method normally takes to run, so if you have floats you should usually use
     * methods that take floats (or return floats, if assigning the result to a float), and likewise for doubles.
     * <br>
     * If you call this frequently, consider giving it either all positive numbers, i.e. 0 to PI * 2 instead of -PI to
     * PI; this can help the performance of this particular approximation by making its one branch easier to predict.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range,
     * using code extremely similar to {@link NumberTools#zigzag(double)}.
     * @param radians an angle in radians as a double, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a double between -1.0 and 1.0 (probably exclusive on 1.0, but not -1.0)
     */
    public static double cosBit(final double radians)
    {
        long sign, s;
        if(radians < -1.5707963267948966) {
            s = Double.doubleToLongBits(radians * 0.3183098861837907 - 1.5);
            sign = 1L;
        }
        else {
            s = Double.doubleToLongBits(radians * 0.3183098861837907 + 2.5);
            sign = -1L;
        }
        final long m = (s >>> 52 & 0x7FFL) - 0x400L, sm = s << m, sn = -((sm & 0x8000000000000L) >> 51);
        double n = (Double.longBitsToDouble(((sm ^ sn) & 0xfffffffffffffL) | 0x4010000000000000L) - 4.0);
        n *= 2.0 - n;
        return n * (-0.775 - 0.225 * n) * ((sn ^ sign) | 1L);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 4x and 40x
     * faster cos() calls in benchmarking, depending on whether HotSpot deoptimizes Math.cos() for its own inscrutable
     * reasons), and both takes and returns floats. Takes the same arguments Math.cos() does, so one angle in radians,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than about 4096f). This is closely related to {@link NumberTools#sway(float)}, but the shape of the output when
     * graphed is almost identical to cos(). The difference between the result of this method and
     * {@link Math#cos(double)} should be under 0.001 at all points between -pi and pi, with an average difference of
     * about 0.0005; not all points have been checked for potentially higher errors, though. The error for this float
     * version is extremely close to the double version, {@link NumberTools#cos(double)}, so you should choose based on what type
     * you have as input and/or want to return rather than on quality concerns. Coercion between float and double takes
     * about as long as this method normally takes to run, so if you have floats you should usually use methods that
     * take floats (or return floats, if assigning the result to a float), and likewise for doubles.
     * <br>
     * If you call this frequently, consider giving it either all positive numbers, i.e. 0 to PI * 2 instead of -PI to
     * PI; this can help the performance of this particular approximation by making its one branch easier to predict.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range,
     * using code extremely similar to {@link NumberTools#zigzag(float)}.
     * @param radians an angle in radians as a float, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a float between -1f and 1f (probably exclusive on 1f, but not -1f)
     */
    public static float cosBit(final float radians)
    {
        int sign, s;
        if(radians < -1.5707963267948966f) {
            s = Float.floatToIntBits(radians * 0.3183098861837907f - 1.5f);
            sign = 1;
        }
        else {
            s = Float.floatToIntBits(radians * 0.3183098861837907f + 2.5f);
            sign = -1;
        }
        final int m = (s >>> 23 & 0xFF) - 0x80, sm = s << m, sn = -((sm & 0x00400000) >> 22);
        float n = (Float.intBitsToFloat(((sm ^ sn) & 0x007fffff) | 0x40800000) - 4f);
        n *= 2f - n;
        return n * (-0.775f - 0.225f * n) * ((sn ^ sign) | 1);
    }
    @Benchmark
    public float measureGdxSinF()
    {
        return MathUtils.sin(((sinGdx += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureGdxCosF() {
        return MathUtils.cos(((cosGdx += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureWallaceSinF()
    {
        return NumberTools2.sinWallaceN(((sinWallace += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureBhaskaraSinF()
    {
        return NumberTools2.sinBhaskaroid(((sinBhaskara += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureBonusSinF()
    {
        return NumberTools2.sinBonus(((sinBonus += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureHastingsSinF()
    {
        return NumberTools2.sinHastings(((sinHastings += 0x9E3779B9) >> 24));
    }
//    @Benchmark
    public float measureSignSinF()
    {
        return NumberTools2.sinSign(((sinSign += 0x9E3779B9) >> 24));
    }
//    @Benchmark
    public float measureShiftySinF()
    {
        return NumberTools2.sinShifty(((sinShifty += 0x9E3779B9) >> 24));
    }
//    @Benchmark
    public float measureTT2SinF()
    {
        return TrigTools2.sin(((sinTT2 += 0x9E3779B9) >> 24));
    }
    @Benchmark
    public float measureDigitalSinF()
    {
        return TrigTools.sin(((sinDigital += 0x9E3779B9) >> 24));
    }
    @Benchmark
    public float measureDigitalSmootherSinF()
    {
        return TrigTools.sinSmoother(((sinSmootherDigital += 0x9E3779B9) >> 24));
    }
//    @Benchmark
//    public float measureCToolsSinF()
//    {
//        return CosTools.sin(((sinCTools += 0x9E3779B9) >> 24));
//    }
//    @Benchmark
//    public float measureCToolsSmootherSinF()
//    {
//        return CosTools.sinSmoother(((sinSmootherCTools += 0x9E3779B9) >> 24));
//    }
//@Benchmark
//public float measureFFSinF()
//{
//    return NumberTools2.sinFF(((sinFF += 0x9E3779B9) >> 24));
//}
//    @Benchmark
//    public float measureFFSmootherSinF()
//    {
//        return NumberTools2.sinSmootherFF(((sinSmootherFF += 0x9E3779B9) >> 24));
//    }

    @Benchmark
    public float measureRoundSinF()
    {
        return NumberTools2.sinRound(((sinRound += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureFloatySinF() {
        return NumberTools2.sinFloaty(((sinFloaty += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureRoundCosF()
    {
        return NumberTools2.cosRound(((cosRound += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureFloatyCosF() {
        return NumberTools2.cosFloaty(((cosFloaty += 0x9E3779B9) >> 24));
    }

    //    @Benchmark
    public float measureUnroundedSinF()
    {
        return NumberTools2.sinUnrounded(((sinUnrounded += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureDigitalCosF()
    {
        return TrigTools.cos(((cosDigital += 0x9E3779B9) >> 24));
    }
    @Benchmark
    public float measureDigitalSmootherCosF()
    {
        return TrigTools.cosSmoother(((cosSmootherDigital += 0x9E3779B9) >> 24));
    }
//    @Benchmark
//    public float measureCToolsCosF()
//    {
//        return CosTools.cos(((cosCTools += 0x9E3779B9) >> 24));
//    }
//    @Benchmark
//    public float measureCToolsSmootherCosF()
//    {
//        return CosTools.cosSmoother(((cosSmootherCTools += 0x9E3779B9) >> 24));
//    }
//    @Benchmark
    public float measureSplitSinF()
    {
        return NumberTools2.sinSplit(((sinSplit += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureSteadmanSinF()
    {
        return NumberTools2.sinSteadman(((sinSteadman += 0x9E3779B9) >> 24));
    }

//    @Benchmark
    public float measureLerpSinF()
    {
        return NumberTools2.sinLerp(((sinLerp += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public double measureMathCosDeg()
    {
        return Math.cos(((mathCosDeg += 0x9E3779B9) >> 24) * 0.017453292519943295);
    }

    @Benchmark
    public double measureMathSinDeg()
    {
        return Math.sin(((mathSinDeg += 0x9E3779B9) >> 24) * 0.017453292519943295);
    }

    @Benchmark
    public float measureSquidCosDeg() {
        return NumberTools.cosDegrees(((cosNickDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureSquidSinDeg() {
        return NumberTools.sinDegrees(((sinNickDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureGdxSinDeg()
    {
        return MathUtils.sinDeg(((sinGdxDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureGdxCosDeg() {
        return MathUtils.cosDeg(((cosGdxDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureDigitalSinDeg()
    {
        return TrigTools.sinDeg(((sinDigitalDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureDigitalCosDeg() {
        return TrigTools.cosDeg(((cosDigitalDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureFSCSinDeg()
    {
        return fastSinCos.sinDeg(((sinFSCDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureFSCCosDeg() {
        return fastSinCos.cosDeg(((cosFSCDeg += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureDivideTan()
    {
        return NumberTools2.tanDivide(((tanDiv += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureSoontsTan()
    {
        return NumberTools2.tanSoonts(((tanSoo += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureLerpTan()
    {
        return NumberTools2.tanLerp(((tanLer += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureTableTan()
    {
        return NumberTools2.tanTable(((tanTab += 0x9E3779B9) >> 24));
    }


    @Benchmark
    public double measureMathAtan2()
    {
        return Math.atan2(((mathAtan2Y += 0x9E3779B9) >> 24), ((mathAtan2X += 0x9E3779B9) >> 24));
    }
    @Benchmark
    public double measureMathAtan2_()
    {
        final double z = Math.atan2(((mathAtan2_Y += 0x9E3779B9) >> 24), ((mathAtan2_X += 0x9E3779B9) >> 24)) * 0.15915494309189535 + 1.0;
        return z - (int)z;
    }

    @Benchmark
    public double measureSquidAtan2()
    {
        return NumberTools.atan2(((atan2SquidY += 0x9E3779B9) >> 24), ((atan2SquidX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureSquidAtan2Float()
    {
        return NumberTools.atan2(((atan2SquidYF += 0x9E3779B9) >> 24), ((atan2SquidXF += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureGdxAtan2()
    {
        return MathUtils.atan2(((atan2GdxY += 0x9E3779B9) >> 24), ((atan2GdxX += 0x9E3779B9) >> 24));
    }
 
    @Benchmark
    public float measureGtAtan2()
    {
        return GtMathUtils.atan2(((atan2GtY += 0x9E3779B9) >> 24), ((atan2GtX += 0x9E3779B9) >> 24));
    }
 
    @Benchmark
    public float measureNtAtan2()
    {
        return NumberTools2.atan2_nt(((atan2NtY += 0x9E3779B9) >> 24), ((atan2NtX += 0x9E3779B9) >> 24));
    }
 
    @Benchmark
    public float measureImuliAtan2()
    {
        return NumberTools2.atan2_quartic(((atan2ImY += 0x9E3779B9) >> 24), ((atan2ImX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureHighPrecisionAtan2()
    {
        return NumberTools2.atan2HP(((atan2HPY += 0x9E3779B9) >> 24), ((atan2HPX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureRemezAtan2()
    {
        return NumberTools2.atan2Remez(((atan2RmY += 0x9E3779B9) >> 24), ((atan2RmX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureGeneralAtan2()
    {
        return NumberTools2.atan2General(((atan2GeY += 0x9E3779B9) >> 24), ((atan2GeX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureSimpleAtan2()
    {
        return NumberTools2.atan2Simple(((atan2SiY += 0x9E3779B9) >> 24), ((atan2SiX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureFunkyAtan2()
    {
        return NumberTools2.atan2Funky(((atan2FnY += 0x9E3779B9) >> 24), ((atan2FnX += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureImuliAtan2_()
    {
        return NumberTools2.atan2_imuli_(((atan2Im_Y += 0x9E3779B9) >> 24), ((atan2Im_X += 0x9E3779B9) >> 24));
    }
 
    @Benchmark
    public float measureSquidAtan2_()
    {
        return NumberTools.atan2_(((atan2_SquidYF += 0x9E3779B9) >> 24), ((atan2_SquidXF += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureSimpleAtan2_()
    {
        return NumberTools2.atan2Simple_(((atan2Si_Y += 0x9E3779B9) >> 24), ((atan2Si_X += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureFunkyAtan2_()
    {
        return NumberTools2.atan2Funky_(((atan2Fn_Y += 0x9E3779B9) >> 24), ((atan2Fn_X += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureSquidAtan2DegFloat()
    {
        return NumberTools.atan2Degrees360(((atan2DegSquidYF += 0x9E3779B9) >> 24), ((atan2DegSquidXF += 0x9E3779B9) >> 24));
    }

    @Benchmark
    public float measureGdxAtan2Deg()
    {
        return (MathUtils.radiansToDegrees * MathUtils.atan2(((atan2DegGdxY += 0x9E3779B9) >> 24), ((atan2DegGdxX += 0x9E3779B9) >> 24)) + 360f) % 360f;
    }
    @Benchmark
    public double measureAtan2Baseline()
    {
        return ((atan2BaselineY += 0x9E3779B9) >> 24) + ((atan2BaselineX += 0x9E3779B9) >> 24);
    }
    @Benchmark
    public float measureAtan2BaselineFloat()
    {
        return ((atan2BaselineYF += 0x9E3779B9) >> 24) + ((atan2BaselineXF += 0x9E3779B9) >> 24);
    }
    
    @Benchmark
    public int measureNextPowerOfTwoHashCommon(){
        return HashCommon.nextPowerOfTwo(npotHC++ & 0x3FFFFFFF);
    }
    @Benchmark
    public int measureNextPowerOfTwoMath(){
        return Math.max(1, Integer.highestOneBit((npotM++ & 0x3FFFFFFF) - 1 << 1));
    }
    @Benchmark
    public int measureNextPowerOfTwoCLZ(){
        return 1 << -Integer.numberOfLeadingZeros((npotCLZ++ & 0x3FFFFFFF) - 1);
    }

    @Benchmark
    public int measureFloorMath(){
        return (int) Math.floor(((floorMath += 0x9E3779B9) * 0x1p-24f));
    }

    public static int fastFloorGust(float x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
    public static int floatFloorAbs(float x) {
        return (int)Math.copySign((int)Math.abs(x), x);
    }
    public static int bitFloorAbs(float x) {
        final int s = BitConversion.floatToIntBits(x) >> 31;
        return (int)Math.abs(x) + s ^ s;
    }
    public static int bitFloorCorrect(final float f) {
        final int i = (int)f;
        return i + (BitConversion.floatToIntBits(-i + f) >> 31);
    }
    public static int bitFloorArithmetic(final float f) {
        final int i = (int)f;
        return i + (BitConversion.floatToIntBits(f - i) >> 31);
    }
    public static int fastFloorNoiseIncorrect(float t) {
        return t >= 0f ? (int) t : (int) t - 1;
    }
    @Benchmark
    public int measureFloorGust(){
        return fastFloorGust(((floorGust += 0x9E3779B9) * 0x1p-24f));
    }
    @Benchmark
    public int measureFloorIncorrect(){
        return fastFloorNoiseIncorrect(((floorNoise += 0x9E3779B9)  * 0x1p-24f));
    }

    @Benchmark
    public int measureFloorGdx(){
        return MathUtils.floor(((floorGdx += 0x9E3779B9) * 0x1p-24f));
    }
    @Benchmark
    public int measureFloorFloatAbs(){
        return floatFloorAbs(((floorFfa += 0x9E3779B9) * 0x1p-24f));
    }
    @Benchmark
    public int measureFloorBitAbs(){
        return bitFloorAbs(((floorBfa += 0x9E3779B9) * 0x1p-24f));
    }
    @Benchmark
    public int measureFloorBitCorrect(){
        return bitFloorCorrect(((floorBfc += 0x9E3779B9) * 0x1p-24f));
    }
    @Benchmark
    public int measureFloorBitArithmetic(){
        return bitFloorArithmetic(((floorBfr += 0x9E3779B9) * 0x1p-24f));
    }

    /*
mvn clean install
java -jar benchmarks.jar MathBenchmark -wi 5 -i 5 -f 1
     */
    public static void main2(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MathBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
    public static void main(String[] args)
    {
        MathBenchmark u = new MathBenchmark();
        double  cosFError = 0.0, cosNickError = 0.0, cosBitError = 0.0, cosBitFError = 0.0, cosGdxError = 0.0,
                sinFError = 0.0, sinNickError = 0.0, sinBitError = 0.0, sinBitFError = 0.0, sinGdxError = 0.0,
                sinWallaceError = 0.0,
                precisionError = 0.0, cosDegNickError = 0.0, sinDegNickError = 0.0, cosDegGdxError = 0.0, sinDegGdxError = 0.0,
                asinChristensenError = 0.0, asinSquidError = 0.0;
        ;
        System.out.println("Math.sin()       : " + u.measureMathSin());
        System.out.println("Math.cos()       : " + u.measureMathCos());
        System.out.println("double sin approx: " + u.measureSquidSin());
        System.out.println("double cos approx: " + u.measureSquidCos());
        for (int r = 0; r < 0x1000; r++) {
            short i = (short) (DiverRNG.determine(r) & 0xFFFF);
            u.mathCos = i;
            u.mathSin = i;
            u.cosOld = i;
            u.sinOld = i;
            u.sinNick = i;
            u.cosNick = i;
            u.sinBit = i;
            u.cosBit = i;
            u.sinBitF = i;
            u.cosBitF = i;
            u.cosFloat = i;
            u.sinFloat = i;
            u.cosGdx = i;
            u.sinGdx = i;
            u.sinWallace = i;

            u.mathCosDeg = i;
            u.mathSinDeg = i;
            u.cosNickDeg = i;
            u.sinNickDeg = i;
            u.cosGdxDeg = i;
            u.sinGdxDeg = i;
            double c = u.measureMathCos(), s = u.measureMathSin(), cd = u.measureMathCosDeg(), sd = u.measureMathSinDeg(), as = u.measureMathASin();
            precisionError += Math.abs(c - (float)c);
            cosFError += Math.abs(u.measureSquidCosF() - c);
            cosGdxError += Math.abs(u.measureGdxCosF() - c);
            sinGdxError += Math.abs(u.measureGdxSinF() - s);
            sinWallaceError += Math.abs(u.measureWallaceSinF() - s);
            cosNickError += Math.abs(u.measureSquidCos() - c);
            sinNickError += Math.abs(u.measureSquidSin() - s);
            sinFError += Math.abs(u.measureSquidSinF() - s);
            cosBitError += Math.abs(u.measureBitCos() - c);
            sinBitError += Math.abs(u.measureBitSin() - s);
            cosBitFError += Math.abs(u.measureBitCosF() - c);
            sinBitFError += Math.abs(u.measureBitSinF() - s);
            cosDegNickError += Math.abs(u.measureSquidCosDeg() - cd);
            sinDegNickError += Math.abs(u.measureSquidSinDeg() - sd);
            cosDegGdxError += Math.abs(u.measureGdxCosDeg() - cd);
            sinDegGdxError += Math.abs(u.measureGdxSinDeg() - sd);
            asinChristensenError += Math.abs(u.measureChristensenASin() - as);
            asinSquidError += Math.abs(u.measureSquidASin() - as);
        }
        System.out.println("base float error : " + precisionError * 0x1p-16);
        System.out.println("cos GDX          : " + cosGdxError * 0x1p-16);
        System.out.println("sin GDX          : " + sinGdxError * 0x1p-16);
        System.out.println("sin Wallace      : " + sinWallaceError * 0x1p-16);
        System.out.println("cos Squid float  : " + cosFError * 0x1p-16);
        System.out.println("sin Squid float  : " + sinFError * 0x1p-16);
        System.out.println("sin Squid        : " + sinNickError * 0x1p-16);
        System.out.println("cos Squid        : " + cosNickError * 0x1p-16);
        System.out.println("sin Bit          : " + sinBitError * 0x1p-16);
        System.out.println("cos Bit          : " + cosBitError * 0x1p-16);
        System.out.println("sin BitF         : " + sinBitFError * 0x1p-16);
        System.out.println("cos BitF         : " + cosBitFError * 0x1p-16);
        System.out.println("sin Nick deg     : " + sinDegNickError * 0x1p-16);
        System.out.println("cos Nick deg     : " + cosDegNickError * 0x1p-16);
        System.out.println("sin GDX deg      : " + sinDegGdxError * 0x1p-16);
        System.out.println("cos GDX deg      : " + cosDegGdxError * 0x1p-16);
        System.out.println("asin Chr.        : " + asinChristensenError * 0x1p-16);
        System.out.println("asin Squid       : " + asinSquidError * 0x1p-16);
        double atan2SquidError = 0;
        double atan2GDXError = 0;
        double atan2GtError = 0;
        double atan2NtError = 0;
        double atan2ImError = 0;
        double atan2GeError = 0;
        double atan2SiError = 0;
        double atan2FnError = 0;
        double atan2HPError = 0;
        double atan2RmError = 0;

        double maxSquidError = 0.0;
        double maxGDXError = 0.0;
        double maxHPError = 0.0;
        double maxRmError = 0.0;

        double atan2_SquidError = 0;
        double atan2_ImError = 0;
        double atan2_SiError = 0;
        double atan2_FnError = 0;
        double at, at_;
        double temp = 0.0;
        for(int r = 0; r < 0x10000; r++)
        {
            short i = (short) (DiverRNG.determine(r) & 0xFFFF);
            short j = (short) (DiverRNG.determine(-0x20000 - r - i) & 0xFFFF);
            u.mathAtan2X = i;
            u.mathAtan2Y = j;
            u.atan2SquidXF = i;
            u.atan2SquidYF = j;
            u.atan2GdxX = i;
            u.atan2GdxY = j;
            u.atan2GtX = i;
            u.atan2GtY = j;
            u.atan2NtX = i;
            u.atan2NtY = j;
            u.atan2ImX = i;
            u.atan2ImY = j;
            u.atan2GeX = i;
            u.atan2GeY = j;
            u.atan2SiX = i;
            u.atan2SiY = j;
            u.atan2FnX = i;
            u.atan2FnY = j;
            u.atan2HPX = i;
            u.atan2HPY = j;
            u.atan2RmX = i;
            u.atan2RmY = j;
            u.mathAtan2_X = i;
            u.mathAtan2_Y = j;
            u.atan2_SquidXF = i;
            u.atan2_SquidYF = j;
            u.atan2Im_X = i;
            u.atan2Im_Y = j;
            u.atan2Si_X = i;
            u.atan2Si_Y = j;
            u.atan2Fn_X = i;
            u.atan2Fn_Y = j;
            at = u.measureMathAtan2();
            at_ = u.measureMathAtan2_();
            atan2SquidError += temp = Math.abs(u.measureSquidAtan2Float() - at);
            maxSquidError = Math.max(maxSquidError, temp);
            atan2GDXError += temp = Math.abs(u.measureGdxAtan2() - at);
            maxGDXError = Math.max(maxGDXError, temp);
            atan2GtError += Math.abs(u.measureGtAtan2() - at);
            atan2NtError += Math.abs(u.measureNtAtan2() - at);
            atan2ImError += Math.abs(u.measureImuliAtan2() - at);
            atan2GeError += Math.abs(u.measureGeneralAtan2() - at);
            atan2SiError += Math.abs(u.measureSimpleAtan2() - at);
            atan2FnError += Math.abs(u.measureFunkyAtan2() - at);
            atan2HPError += temp = Math.abs(u.measureHighPrecisionAtan2() - at);
            maxHPError = Math.max(maxHPError, temp);
            atan2RmError += temp = Math.abs(u.measureRemezAtan2() - at);
            maxRmError = Math.max(maxRmError, temp);

            atan2_SquidError += Math.abs(u.measureSquidAtan2_() - at_);
            atan2_ImError += Math.abs(u.measureImuliAtan2_() - at_);
            atan2_SiError += Math.abs(u.measureSimpleAtan2_() - at_);
            atan2_FnError += Math.abs(u.measureFunkyAtan2_() - at_);
        }
        System.out.println("atan2 Squid      : " + atan2SquidError * 0x1p-16);
        System.out.println("atan2 Squid Max  : " + maxSquidError);
        System.out.println();
        System.out.println("atan2 GDX        : " + atan2GDXError * 0x1p-16);
        System.out.println("atan2 GDX Max    : " + maxGDXError);
        System.out.println();
        System.out.println("atan2 Gt         : " + atan2GtError * 0x1p-16);
        System.out.println("atan2 Nt         : " + atan2NtError * 0x1p-16);
        System.out.println("atan2 Imuli      : " + atan2ImError * 0x1p-16);
        System.out.println("atan2 General    : " + atan2GeError * 0x1p-16);
        System.out.println("atan2 Simple     : " + atan2SiError * 0x1p-16);
        System.out.println("atan2 Funky      : " + atan2FnError * 0x1p-16);
        System.out.println("atan2 HP         : " + atan2HPError * 0x1p-16);
        System.out.println("atan2 HP Max     : " + maxHPError);
        System.out.println();
        System.out.println("atan2 Remez      : " + atan2RmError * 0x1p-16);
        System.out.println("atan2 Remez Max  : " + maxRmError);
        System.out.println();
        System.out.println("atan2_ Squid     : " + atan2_SquidError * 0x1p-16);
        System.out.println("atan2_ Imuli     : " + atan2_ImError * 0x1p-16);
        System.out.println("atan2_ Simple    : " + atan2_SiError * 0x1p-16);
        System.out.println("atan2_ Funky     : " + atan2_FnError * 0x1p-16);
    }
}
