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

import com.github.yellowstonegames.grid.Noise;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.squidmath.ClassicNoise;
import squidpony.squidmath.FastNoise;
import squidpony.squidmath.FoamNoise;
import squidpony.squidmath.SeededNoise;
import squidpony.squidmath.VastNoise;

import java.util.concurrent.TimeUnit;

/**
 * Various different noise functions, most variants on Simplex noise. These measurements are per-call, in nanoseconds.
 * The Score column is the most relevant; the score is how much time it takes to complete one call, so lower is better.
 * The Error column can be ignored if it is relatively small, but large Error values may show a measurement inaccuracy.
 * OSF is OpenSimplex2F, OSS is OpenSimplex2S, OpenSimplex refers to the first version.
 * <br>
 * All 2D and higher noise benchmarks, run with Java 14, Manjaro Linux, 8th generation i7 mobile processor, on July 18,
 * 2020:
 * <pre>
 * Benchmark                            Mode  Cnt     Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D    avgt    5    25.040 ±  0.023  ns/op
 * NoiseBenchmark.measureFastNoise3D    avgt    5    29.250 ±  0.042  ns/op
 * NoiseBenchmark.measureFastNoise4D    avgt    5    66.120 ±  0.094  ns/op
 * NoiseBenchmark.measureFastNoise6D    avgt    5   124.677 ±  0.084  ns/op
 * NoiseBenchmark.measureOSFNoise2D     avgt    5    25.261 ±  0.018  ns/op
 * NoiseBenchmark.measureOSFNoise3D     avgt    5    43.317 ±  0.127  ns/op
 * NoiseBenchmark.measureOSFNoise4D     avgt    5    42.937 ±  0.075  ns/op
 * NoiseBenchmark.measureOSSNoise2D     avgt    5    30.246 ±  0.029  ns/op
 * NoiseBenchmark.measureOSSNoise3D     avgt    5    54.604 ±  0.054  ns/op
 * NoiseBenchmark.measureOSSNoise4D     avgt    5   105.006 ±  0.324  ns/op
 * NoiseBenchmark.measureOpenSimplex2D  avgt    5    35.855 ±  0.234  ns/op
 * NoiseBenchmark.measureOpenSimplex3D  avgt    5    75.841 ±  0.087  ns/op
 * NoiseBenchmark.measureOpenSimplex4D  avgt    5  1562.718 ± 12.655  ns/op
 * NoiseBenchmark.measureSeeded2D       avgt    5    29.052 ±  0.060  ns/op
 * NoiseBenchmark.measureSeeded3D       avgt    5    40.755 ±  0.159  ns/op
 * NoiseBenchmark.measureSeeded4D       avgt    5    60.508 ±  0.064  ns/op
 * NoiseBenchmark.measureSeeded6D       avgt    5   134.823 ±  0.246  ns/op
 * </pre>
 * <br>
 * Added in OpenSimplex2S as OSSNoise. It isn't too bad in low dimensions, but it loses
 * the impressive advantage OpenSimplex2F has in 4D, and OSS 4D is slower than OSS 3D
 * (which is what you would expect, but it isn't the case for OSF) and FastNoise 4D.
 * <br>
 * These benchmarks are from an older Windows 7 machine; Java 14, 6th generation i7 mobile processor:
 * <pre>
 * Benchmark                          Mode  Cnt    Score   Error  Units
 * NoiseBenchmark.measureFastNoise2D  avgt    5   35.277 ± 0.116  ns/op
 * NoiseBenchmark.measureFastNoise3D  avgt    5   41.478 ± 0.077  ns/op
 * NoiseBenchmark.measureFastNoise4D  avgt    5   82.395 ± 0.985  ns/op
 * NoiseBenchmark.measureFastNoise6D  avgt    5  184.830 ± 0.621  ns/op
 * NoiseBenchmark.measureOSFNoise2D   avgt    5   36.729 ± 0.123  ns/op
 * NoiseBenchmark.measureOSFNoise3D   avgt    5   62.726 ± 0.405  ns/op
 * NoiseBenchmark.measureOSFNoise4D   avgt    5   60.681 ± 0.350  ns/op
 * NoiseBenchmark.measureOSSNoise2D   avgt    5   43.932 ± 2.871  ns/op
 * NoiseBenchmark.measureOSSNoise3D   avgt    5   78.379 ± 0.329  ns/op
 * NoiseBenchmark.measureOSSNoise4D   avgt    5  134.307 ± 0.414  ns/op
 * </pre>
 * And briefly testing hashed OpenSimplex2, avoiding 28KB of permutation tables per seed:
 * <pre>
 * Benchmark                          Mode  Cnt    Score   Error  Units
 * NoiseBenchmark.measureFastNoise2D  avgt    5   35.204 ± 0.245  ns/op
 * NoiseBenchmark.measureFastNoise3D  avgt    5   41.534 ± 0.556  ns/op
 * NoiseBenchmark.measureFastNoise4D  avgt    5   80.002 ± 0.444  ns/op
 * NoiseBenchmark.measureFastNoise6D  avgt    5  176.253 ± 0.517  ns/op
 * NoiseBenchmark.measureOSFNoise2D   avgt    5   36.774 ± 0.175  ns/op
 * NoiseBenchmark.measureOSFNoise3D   avgt    5   62.686 ± 0.302  ns/op
 * NoiseBenchmark.measureOSFNoise4D   avgt    5   63.983 ± 0.476  ns/op
 * NoiseBenchmark.measureOSHNoise2D   avgt    5   40.309 ± 0.556  ns/op
 * NoiseBenchmark.measureOSHNoise3D   avgt    5   64.572 ± 0.148  ns/op
 * NoiseBenchmark.measureOSHNoise4D   avgt    5   73.817 ± 0.650  ns/op
 * </pre>
 * <br>
 * With an experimental mix of FastNoise' Simplex code and OpenSimplex2F's perm code,
 * called Fnospiral here:
 * <pre>
 * Benchmark                            Mode  Cnt     Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D    avgt    5    25.028 ±  0.048  ns/op
 * NoiseBenchmark.measureFastNoise3D    avgt    5    29.224 ±  0.112  ns/op
 * NoiseBenchmark.measureFastNoise4D    avgt    5    66.217 ±  0.109  ns/op
 * NoiseBenchmark.measureFastNoise6D    avgt    5   127.115 ±  0.444  ns/op
 * NoiseBenchmark.measureFnospiral2D    avgt    5    24.940 ±  0.026  ns/op
 * NoiseBenchmark.measureFnospiral3D    avgt    5    26.940 ±  0.023  ns/op
 * NoiseBenchmark.measureFnospiral4D    avgt    5    48.949 ±  0.375  ns/op
 * NoiseBenchmark.measureFnospiral6D    avgt    5   130.221 ±  0.242  ns/op
 * NoiseBenchmark.measureOSFNoise2D     avgt    5    30.382 ±  0.080  ns/op
 * NoiseBenchmark.measureOSFNoise3D     avgt    5    47.526 ±  0.236  ns/op
 * NoiseBenchmark.measureOSFNoise4D     avgt    5    52.697 ±  0.101  ns/op
 * NoiseBenchmark.measureOSHNoise2D     avgt    5    36.132 ±  2.219  ns/op
 * NoiseBenchmark.measureOSHNoise3D     avgt    5    49.253 ±  0.153  ns/op
 * NoiseBenchmark.measureOSHNoise4D     avgt    5    52.676 ±  0.174  ns/op
 * NoiseBenchmark.measureOSSNoise2D     avgt    5    37.889 ±  0.154  ns/op
 * NoiseBenchmark.measureOSSNoise3D     avgt    5    59.187 ±  0.047  ns/op
 * NoiseBenchmark.measureOSSNoise4D     avgt    5    89.812 ±  0.309  ns/op
 * NoiseBenchmark.measureOpenSimplex2D  avgt    5    35.963 ±  0.028  ns/op
 * NoiseBenchmark.measureOpenSimplex3D  avgt    5    75.904 ±  0.116  ns/op
 * NoiseBenchmark.measureOpenSimplex4D  avgt    5  1577.699 ± 37.611  ns/op
 * NoiseBenchmark.measureSeeded2D       avgt    5    29.071 ±  0.054  ns/op
 * NoiseBenchmark.measureSeeded3D       avgt    5    40.740 ±  0.045  ns/op
 * NoiseBenchmark.measureSeeded4D       avgt    5    60.504 ±  0.037  ns/op
 * NoiseBenchmark.measureSeeded6D       avgt    5   136.941 ±  0.128  ns/op
 * </pre>
 * <br>
 * Fnospiral is fastest in almost all cases, but also doesn't allow reseeding per-call.
 * <br>
 * Temporary data testing VastNoise...
 * <pre>
 * Benchmark                          Mode  Cnt    Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D  avgt    5   24.098 ±  0.242  ns/op
 * NoiseBenchmark.measureFastNoise3D  avgt    5   29.199 ±  0.495  ns/op
 * NoiseBenchmark.measureFastNoise4D  avgt    5   55.786 ±  0.430  ns/op
 * NoiseBenchmark.measureFastNoise5D  avgt    5   68.737 ±  0.652  ns/op
 * NoiseBenchmark.measureFastNoise6D  avgt    5  128.331 ± 16.751  ns/op
 * NoiseBenchmark.measureFnospiral2D  avgt    5   24.463 ±  0.424  ns/op
 * NoiseBenchmark.measureFnospiral3D  avgt    5   27.527 ±  0.846  ns/op
 * NoiseBenchmark.measureFnospiral4D  avgt    5   50.042 ±  1.071  ns/op
 * NoiseBenchmark.measureFnospiral6D  avgt    5  126.597 ±  2.157  ns/op
 * NoiseBenchmark.measureVastNoise2D  avgt    5   24.153 ±  0.184  ns/op
 * NoiseBenchmark.measureVastNoise3D  avgt    5   26.709 ±  0.558  ns/op
 * NoiseBenchmark.measureVastNoise4D  avgt    5   51.246 ±  0.994  ns/op
 * NoiseBenchmark.measureVastNoise5D  avgt    5   60.696 ±  1.852  ns/op
 * NoiseBenchmark.measureVastNoise6D  avgt    5  111.982 ±  2.432  ns/op
 * </pre>
 * <br>
 * More temporary data testing "XastNoise" to see if it improves on VastNoise...
 * <pre> 
 * Benchmark                          Mode  Cnt    Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D  avgt    5   23.990 ±  0.046  ns/op
 * NoiseBenchmark.measureFastNoise3D  avgt    5   29.285 ±  0.269  ns/op
 * NoiseBenchmark.measureFastNoise4D  avgt    5   55.672 ±  0.666  ns/op
 * NoiseBenchmark.measureFastNoise5D  avgt    5   69.411 ±  1.510  ns/op
 * NoiseBenchmark.measureFastNoise6D  avgt    5  129.823 ± 20.843  ns/op
 * NoiseBenchmark.measureVastNoise2D  avgt    5   24.273 ±  0.219  ns/op
 * NoiseBenchmark.measureVastNoise3D  avgt    5   26.912 ±  0.192  ns/op
 * NoiseBenchmark.measureVastNoise4D  avgt    5   51.326 ±  1.190  ns/op
 * NoiseBenchmark.measureVastNoise5D  avgt    5   67.340 ±  0.846  ns/op
 * NoiseBenchmark.measureVastNoise6D  avgt    5  128.921 ± 23.366  ns/op
 * NoiseBenchmark.measureXastNoise2D  avgt    5   22.726 ±  0.442  ns/op
 * NoiseBenchmark.measureXastNoise3D  avgt    5   26.979 ±  0.523  ns/op
 * NoiseBenchmark.measureXastNoise4D  avgt    5   52.824 ±  0.396  ns/op
 * NoiseBenchmark.measureXastNoise5D  avgt    5   63.334 ±  0.718  ns/op
 * NoiseBenchmark.measureXastNoise6D  avgt    5  115.846 ±  1.359  ns/op
 * </pre>
 * <br>
 * Just testing BookNoise, which Talos uses... Not good. Java 15 here, faster
 * hardware, but Windows 10. Our 5D noise in FastNoise is faster than that 2D noise,
 * plus it actually allows seeding...
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * NoiseBenchmark.measureBookNoise2D  avgt    5  88.478 ± 2.633  ns/op
 * NoiseBenchmark.measureFastNoise2D  avgt    5  24.159 ± 1.864  ns/op
 * NoiseBenchmark.measureFnospiral2D  avgt    5  24.210 ± 1.068  ns/op
 * </pre>
 * <br>
 * Comparing some 2D noise...
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * NoiseBenchmark.measureFastNoise2D  avgt    4  24.726 ± 0.593  ns/op
 * NoiseBenchmark.measureGustNoise2D  avgt    4  29.621 ± 1.872  ns/op
 * NoiseBenchmark.measureOSFNoise2D   avgt    4  29.460 ± 1.119  ns/op
 * NoiseBenchmark.measureOSHNoise2D   avgt    4  35.113 ± 1.625  ns/op
 * NoiseBenchmark.measureOSSNoise2D   avgt    4  35.952 ± 4.857  ns/op
 * </pre>
 * <br>
 * And comparing some 4D noise.
 * <pre>
 * Benchmark                          Mode  Cnt   Score    Error  Units
 * NoiseBenchmark.measureFastNoise4D  avgt    4  56.286 ±  0.860  ns/op
 * NoiseBenchmark.measureGustNoise4D  avgt    4  75.847 ±  3.272  ns/op
 * NoiseBenchmark.measureOSFNoise4D   avgt    4  50.964 ±  1.110  ns/op
 * NoiseBenchmark.measureOSHNoise4D   avgt    4  53.050 ±  3.261  ns/op
 * NoiseBenchmark.measureOSSNoise4D   avgt    4  84.870 ± 15.040  ns/op
 * </pre>
 * <br>
 * Comparing SquidLib FastNoise and SquidSquad Noise:
 * <pre>
 * Benchmark                                 Mode  Cnt     Score     Error  Units
 * NoiseBenchmark.measureFastFoamNoise2D     avgt    4    72.462 ±   1.733  ns/op
 * NoiseBenchmark.measureFastFoamNoise3D     avgt    4   126.036 ±   8.649  ns/op
 * NoiseBenchmark.measureFastFoamNoise4D     avgt    4   223.895 ±   1.349  ns/op
 * NoiseBenchmark.measureFastFoamNoise5D     avgt    4   560.161 ±  38.470  ns/op
 * NoiseBenchmark.measureFastFoamNoise6D     avgt    4  1731.005 ± 182.149  ns/op
 * NoiseBenchmark.measureFastNoise2D         avgt    4    24.631 ±   2.069  ns/op
 * NoiseBenchmark.measureFastNoise3D         avgt    4    29.945 ±   1.591  ns/op
 * NoiseBenchmark.measureFastNoise4D         avgt    4    56.687 ±   5.469  ns/op
 * NoiseBenchmark.measureFastNoise5D         avgt    4    67.145 ±   5.030  ns/op
 * NoiseBenchmark.measureFastNoise6D         avgt    4   124.176 ±  13.703  ns/op
 * NoiseBenchmark.measureFastPerlinNoise2D   avgt    4    21.925 ±   2.756  ns/op
 * NoiseBenchmark.measureFastPerlinNoise3D   avgt    4    31.190 ±   1.038  ns/op
 * NoiseBenchmark.measureFastPerlinNoise4D   avgt    4    61.523 ±   6.399  ns/op
 * NoiseBenchmark.measureFastPerlinNoise5D   avgt    4   140.238 ±   9.163  ns/op
 * NoiseBenchmark.measureFastPerlinNoise6D   avgt    4   457.842 ±  31.494  ns/op
 * NoiseBenchmark.measureSquadFoamNoise2D    avgt    4    72.843 ±   2.247  ns/op
 * NoiseBenchmark.measureSquadFoamNoise3D    avgt    4   129.210 ±   5.135  ns/op
 * NoiseBenchmark.measureSquadFoamNoise4D    avgt    4   229.869 ±  19.327  ns/op
 * NoiseBenchmark.measureSquadFoamNoise5D    avgt    4   496.394 ±  54.108  ns/op
 * NoiseBenchmark.measureSquadFoamNoise6D    avgt    4  1698.018 ±  63.552  ns/op
 * NoiseBenchmark.measureSquadNoise2D        avgt    4    24.120 ±   0.625  ns/op
 * NoiseBenchmark.measureSquadNoise3D        avgt    4    29.838 ±   1.743  ns/op
 * NoiseBenchmark.measureSquadNoise4D        avgt    4    56.720 ±   5.021  ns/op
 * NoiseBenchmark.measureSquadNoise5D        avgt    4    65.538 ±   3.677  ns/op
 * NoiseBenchmark.measureSquadNoise6D        avgt    4   120.508 ±   5.988  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise2D  avgt    4    21.667 ±   0.393  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise3D  avgt    4    29.462 ±   0.877  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise4D  avgt    4    65.082 ±   3.969  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise5D  avgt    4   141.615 ±  17.138  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise6D  avgt    4   461.854 ±  34.164  ns/op
 * </pre>
 * About the same! Foam has a slight advantage for Squad in higher dimensions,
 * but that could just be the very high error in the measurement.
 * <br>
 * Comparing VastNoise with OpenSimplex2F (OSF) and OpenSimplex2S (OSS):
 * <pre>
 * Benchmark                          Mode  Cnt   Score   Error  Units
 * NoiseBenchmark.measureOSFNoise2D   avgt    5  28.936 ± 0.275  ns/op
 * NoiseBenchmark.measureOSFNoise3D   avgt    5  45.348 ± 1.833  ns/op
 * NoiseBenchmark.measureOSFNoise4D   avgt    5  50.646 ± 1.675  ns/op
 * NoiseBenchmark.measureOSSNoise2D   avgt    5  35.538 ± 0.472  ns/op
 * NoiseBenchmark.measureOSSNoise3D   avgt    5  56.448 ± 5.389  ns/op
 * NoiseBenchmark.measureOSSNoise4D   avgt    5  81.002 ± 2.330  ns/op
 * NoiseBenchmark.measureVastNoise2D  avgt    5  23.856 ± 0.485  ns/op
 * NoiseBenchmark.measureVastNoise3D  avgt    5  26.033 ± 0.569  ns/op
 * NoiseBenchmark.measureVastNoise4D  avgt    5  50.313 ± 0.621  ns/op
 * </pre>
 * Here it should be noted that OSF in 3D and especially 4D loses quality,
 * so it's great that VastNoise can produce high-quality 4D noise at the
 * same speed as OSF's low-quality 4D noise. In 2D and 3D, VastNoise is
 * quite a bit faster, and has comparable quality to OSS. This has VastNoise
 * using the SIMPLEX noise mode.
 *
 * <pre>
 * Benchmark                                 Mode  Cnt     Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D         avgt    5    23.840 ±  0.613  ns/op
 * NoiseBenchmark.measureFastNoise3D         avgt    5    30.107 ±  0.458  ns/op
 * NoiseBenchmark.measureFastNoise4D         avgt    5    56.201 ±  0.968  ns/op
 * NoiseBenchmark.measureFastNoise5D         avgt    5    67.865 ±  1.033  ns/op
 * NoiseBenchmark.measureFastNoise6D         avgt    5    88.664 ±  5.168  ns/op
 * NoiseBenchmark.measureOSFNoise2D          avgt    5    29.653 ±  0.922  ns/op
 * NoiseBenchmark.measureOSFNoise3D          avgt    5    46.563 ±  0.634  ns/op
 * NoiseBenchmark.measureOSFNoise4D          avgt    5    51.756 ±  1.247  ns/op
 * NoiseBenchmark.measureOSHNoise2D          avgt    5    38.163 ±  4.114  ns/op
 * NoiseBenchmark.measureOSHNoise3D          avgt    5    53.993 ±  4.381  ns/op
 * NoiseBenchmark.measureOSHNoise4D          avgt    5    52.967 ±  1.327  ns/op
 * NoiseBenchmark.measureOSN2F2D             avgt    5    31.819 ±  5.080  ns/op
 * NoiseBenchmark.measureOSN2F3D             avgt    5    42.753 ±  9.842  ns/op
 * NoiseBenchmark.measureOSN2F4D             avgt    5    61.373 ±  0.979  ns/op
 * NoiseBenchmark.measureOSN2S2D             avgt    5    28.354 ±  0.351  ns/op
 * NoiseBenchmark.measureOSN2S3D             avgt    5    50.334 ±  1.086  ns/op
 * NoiseBenchmark.measureOSN2S4D             avgt    5    77.143 ±  1.349  ns/op
 * NoiseBenchmark.measureOSSNoise2D          avgt    5    36.348 ±  1.162  ns/op
 * NoiseBenchmark.measureOSSNoise3D          avgt    5    57.927 ±  7.773  ns/op
 * NoiseBenchmark.measureOSSNoise4D          avgt    5    82.572 ±  1.258  ns/op
 * NoiseBenchmark.measureSquadFoamNoise2D    avgt    5    67.636 ±  0.193  ns/op
 * NoiseBenchmark.measureSquadFoamNoise3D    avgt    5   128.329 ±  0.341  ns/op
 * NoiseBenchmark.measureSquadFoamNoise4D    avgt    5   211.229 ±  0.869  ns/op
 * NoiseBenchmark.measureSquadFoamNoise5D    avgt    5   545.180 ±  6.835  ns/op
 * NoiseBenchmark.measureSquadFoamNoise6D    avgt    5  1159.013 ± 15.768  ns/op
 * NoiseBenchmark.measureSquadNoise2D        avgt    5    29.576 ±  0.300  ns/op
 * NoiseBenchmark.measureSquadNoise3D        avgt    5    31.008 ±  0.301  ns/op
 * NoiseBenchmark.measureSquadNoise4D        avgt    5    51.327 ±  0.744  ns/op
 * NoiseBenchmark.measureSquadNoise5D        avgt    5    68.544 ±  0.554  ns/op
 * NoiseBenchmark.measureSquadNoise6D        avgt    5    85.151 ±  3.020  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise2D  avgt    5    19.908 ±  0.085  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise3D  avgt    5    24.361 ±  0.759  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise4D  avgt    5    63.485 ±  0.969  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise5D  avgt    5   119.337 ±  1.809  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise6D  avgt    5   377.451 ± 11.406  ns/op
 * NoiseBenchmark.measureVastNoise2D         avgt    5    22.558 ±  0.049  ns/op
 * NoiseBenchmark.measureVastNoise3D         avgt    5    26.817 ±  0.564  ns/op
 * NoiseBenchmark.measureVastNoise4D         avgt    5    47.651 ±  0.789  ns/op
 * NoiseBenchmark.measureVastNoise5D         avgt    5    64.988 ±  1.078  ns/op
 * NoiseBenchmark.measureVastNoise6D         avgt    5    84.217 ±  1.114  ns/op
 * </pre>
 * OSN2F is an improved version of OSFNoise, OSN2S is an improved version of OSSNoise.
 * They are from OpenSimplex2, and are in the public domain.
 * <br>
 * <pre>
 * Benchmark                                 Mode  Cnt     Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D         avgt    5    24.046 ±  2.426  ns/op
 * NoiseBenchmark.measureFastNoise3D         avgt    5    29.583 ±  1.030  ns/op
 * NoiseBenchmark.measureFastNoise4D         avgt    5    52.038 ±  1.723  ns/op
 * NoiseBenchmark.measureFastNoise5D         avgt    5    67.417 ±  0.688  ns/op
 * NoiseBenchmark.measureFastNoise6D         avgt    5    88.530 ±  6.921  ns/op
 * NoiseBenchmark.measureSquadFlanNoise2D    avgt    5    90.800 ±  1.298  ns/op
 * NoiseBenchmark.measureSquadFlanNoise3D    avgt    5   121.304 ±  1.447  ns/op
 * NoiseBenchmark.measureSquadFlanNoise4D    avgt    5   160.347 ±  1.308  ns/op
 * NoiseBenchmark.measureSquadFlanNoise5D    avgt    5   210.493 ±  6.253  ns/op
 * NoiseBenchmark.measureSquadFlanNoise6D    avgt    5   264.086 ±  5.100  ns/op
 * NoiseBenchmark.measureSquadFoamNoise2D    avgt    5    69.061 ±  1.490  ns/op
 * NoiseBenchmark.measureSquadFoamNoise3D    avgt    5   131.698 ±  1.836  ns/op
 * NoiseBenchmark.measureSquadFoamNoise4D    avgt    5   214.047 ±  5.314  ns/op
 * NoiseBenchmark.measureSquadFoamNoise5D    avgt    5   555.655 ±  7.260  ns/op
 * NoiseBenchmark.measureSquadFoamNoise6D    avgt    5  1157.547 ± 34.010  ns/op
 * NoiseBenchmark.measureSquadNoise2D        avgt    5    27.965 ±  0.525  ns/op
 * NoiseBenchmark.measureSquadNoise3D        avgt    5    32.209 ±  0.588  ns/op
 * NoiseBenchmark.measureSquadNoise4D        avgt    5    50.939 ±  1.076  ns/op
 * NoiseBenchmark.measureSquadNoise5D        avgt    5    68.705 ±  3.475  ns/op
 * NoiseBenchmark.measureSquadNoise6D        avgt    5   109.809 ±  1.222  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise2D  avgt    5    21.487 ±  0.534  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise3D  avgt    5    26.928 ±  0.361  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise4D  avgt    5    69.663 ±  2.664  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise5D  avgt    5   120.914 ±  7.604  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise6D  avgt    5   369.962 ± 56.187  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise2D   avgt    5    68.787 ±  1.894  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise3D   avgt    5   105.236 ±  1.724  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise4D   avgt    5   115.571 ±  1.400  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise5D   avgt    5   167.289 ±  3.872  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise6D   avgt    5   236.699 ± 15.247  ns/op
 * </pre>
 * Flan does seem to scale about linearly with dimension. Taffy's results don't make any sense. Foam is still slow.
 * <br>
 * Trying the new and improved Flan, it really does well compared to the similar Taffy or Foam.
 * <pre>
 * Benchmark                                 Mode  Cnt     Score    Error  Units
 * NoiseBenchmark.measureFastNoise2D         avgt    5    26.555 ±  0.557  ns/op
 * NoiseBenchmark.measureFastNoise3D         avgt    5    30.157 ±  1.470  ns/op
 * NoiseBenchmark.measureFastNoise4D         avgt    5    56.956 ±  1.933  ns/op
 * NoiseBenchmark.measureFastNoise5D         avgt    5    68.155 ±  3.825  ns/op
 * NoiseBenchmark.measureFastNoise6D         avgt    5    97.186 ±  9.296  ns/op
 * NoiseBenchmark.measureSquadFlanNoise2D    avgt    5    58.561 ±  5.025  ns/op
 * NoiseBenchmark.measureSquadFlanNoise3D    avgt    5    68.766 ±  1.818  ns/op
 * NoiseBenchmark.measureSquadFlanNoise4D    avgt    5   109.089 ±  1.846  ns/op
 * NoiseBenchmark.measureSquadFlanNoise5D    avgt    5   137.515 ±  1.562  ns/op
 * NoiseBenchmark.measureSquadFlanNoise6D    avgt    5   170.066 ±  1.898  ns/op
 * NoiseBenchmark.measureSquadFoamNoise2D    avgt    5    69.718 ±  2.410  ns/op
 * NoiseBenchmark.measureSquadFoamNoise3D    avgt    5   132.367 ±  1.633  ns/op
 * NoiseBenchmark.measureSquadFoamNoise4D    avgt    5   214.160 ±  8.997  ns/op
 * NoiseBenchmark.measureSquadFoamNoise5D    avgt    5   559.390 ±  6.754  ns/op
 * NoiseBenchmark.measureSquadFoamNoise6D    avgt    5  1164.401 ± 40.330  ns/op
 * NoiseBenchmark.measureSquadNoise2D        avgt    5    28.046 ±  1.083  ns/op
 * NoiseBenchmark.measureSquadNoise3D        avgt    5    31.337 ±  1.258  ns/op
 * NoiseBenchmark.measureSquadNoise4D        avgt    5    62.138 ±  1.282  ns/op
 * NoiseBenchmark.measureSquadNoise5D        avgt    5    68.058 ±  3.838  ns/op
 * NoiseBenchmark.measureSquadNoise6D        avgt    5    96.758 ±  1.209  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise2D  avgt    5    21.841 ±  0.888  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise3D  avgt    5    27.819 ±  1.936  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise4D  avgt    5    70.197 ±  3.314  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise5D  avgt    5   125.411 ±  8.564  ns/op
 * NoiseBenchmark.measureSquadPerlinNoise6D  avgt    5   372.430 ± 22.836  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise2D   avgt    5    68.992 ±  1.215  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise3D   avgt    5   106.786 ±  6.722  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise4D   avgt    5   119.707 ±  6.737  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise5D   avgt    5   169.887 ±  7.254  ns/op
 * NoiseBenchmark.measureSquadTaffyNoise6D   avgt    5   240.895 ± 15.128  ns/op
 * </pre>
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class NoiseBenchmark {

    private short x, y, z, w, u, v;
    private final FastNoise fast = new FastNoise(12345),
            fast3 = new FastNoise(12345),
            fast5 = new FastNoise(12345),
    fastFoam = new FastNoise(12345), fastPerlin = new FastNoise(12345);
    private final Noise squad = new Noise(12345),
            squad3 = new Noise(12345), squad5 = new Noise(12345),
            squadFoam = new Noise(12345), squadPerlin = new Noise(12345),
            squadTaffy = new Noise(12345);
    private final VastNoise vast = new VastNoise(12345);
    private final XastNoise xast = new XastNoise(12345);
    private final FoamNoise foam = new FoamNoise(12345);
    private final ClassicNoise perlin = new ClassicNoise(12345);
    private final OpenSimplex2F osf = new OpenSimplex2F(12345L);
    private final OpenSimplex2S oss = new OpenSimplex2S(12345L);
    private final OpenSimplex2H osh = new OpenSimplex2H(12345L);
    private final Fnospiral fno = new Fnospiral(12345);
    private final BookNoise book = new BookNoise();

    @Setup(Level.Trial)
    public void setup() {
        fast.setFractalOctaves(1);
        fast.setNoiseType(FastNoise.SIMPLEX);
        squad.setFractalOctaves(1);
        squad.setNoiseType(FastNoise.SIMPLEX);

        fastFoam.setFractalOctaves(1);
        fastFoam.setNoiseType(FastNoise.FOAM);
        fastPerlin.setFractalOctaves(1);
        fastPerlin.setNoiseType(FastNoise.PERLIN);

        squadFoam.setFractalOctaves(1);
        squadFoam.setNoiseType(Noise.FOAM);
        squadPerlin.setFractalOctaves(1);
        squadPerlin.setNoiseType(Noise.PERLIN);
        squadTaffy.setFractalOctaves(1);
        squadTaffy.setNoiseType(Noise.TAFFY);

        vast.setFractalOctaves(1);
        vast.setNoiseType(FastNoise.SIMPLEX);

        xast.setFractalOctaves(1);
        xast.setNoiseType(FastNoise.SIMPLEX);

        fast3.setNoiseType(FastNoise.SIMPLEX_FRACTAL);
        fast3.setFractalOctaves(3);
        fast5.setNoiseType(FastNoise.SIMPLEX_FRACTAL);
        fast5.setFractalOctaves(5);

        squad3.setNoiseType(FastNoise.SIMPLEX_FRACTAL);
        squad3.setFractalOctaves(3);
        squad5.setNoiseType(FastNoise.SIMPLEX_FRACTAL);
        squad5.setFractalOctaves(5);

        fno.setFractalOctaves(1);
        fno.setNoiseType(FastNoise.SIMPLEX);

    }
//    public static double swayTightBit(final double value)
//    {
//        final long s = Double.doubleToLongBits(value + (value < 0.0 ? -2.0 : 2.0)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
//        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L)>>51)) & 0xfffffffffffffL)
//                | 0x4000000000000000L) - 2.0);
//        return a * a * a * (a * (a * 6.0 - 15.0) + 10.0);
//    }
//
//    public static float swayTightBit(final float value)
//    {
//        final int s = Float.floatToIntBits(value + (value < 0f ? -2f : 2f)), m = (s >>> 23 & 0xFF) - 0x80, sm = s << m;
//        final float a = (Float.intBitsToFloat(((sm ^ -((sm & 0x00400000)>>22)) & 0x007fffff) | 0x40000000) - 2f);
//        return a * a * a * (a * (a * 6f - 15f) + 10f);
//    }
//
//
//    public static double swayBit(final double value)
//    {
//        final long s = Double.doubleToLongBits(value + (value < 0.0 ? -2.0 : 2.0)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
//        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L)>>51)) & 0xfffffffffffffL)
//                | 0x4000000000000000L) - 2.0);
//        return a * a * a * (a * (a * 6.0 - 15.0) + 10.0) * 2.0 - 1.0;
//    }
//
//    public static float swayBit(final float value)
//    {
//        final int s = Float.floatToIntBits(value + (value < 0f ? -2f : 2f)), m = (s >>> 23 & 0xFF) - 0x80, sm = s << m;
//        final float a = (Float.intBitsToFloat(((sm ^ -((sm & 0x00400000)>>22)) & 0x007fffff) | 0x40000000) - 2f);
//        return a * a * a * (a * (a * 6f - 15f) + 10f) * 2f - 1f;
//    }
//    @Benchmark
//    public double measureSwayRandomizedDouble() {
//        return NumberTools.swayRandomized(1024L, d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureSwayRandomizedFloat() {
//        return NumberTools.swayRandomized(1024L, f[x++ & 1023]);
//    }
//
//    @Benchmark
//    public double measureSwayDouble() {
//        return NumberTools.sway(d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureSwayFloat() {
//        return NumberTools.sway(f[x++ & 1023]);
//    }
//
//    @Benchmark
//    public double measureSwayBitDouble() {
//        return swayBit(d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureSwayBitFloat() {
//        return swayBit(f[x++ & 1023]);
//    }
//
//    @Benchmark
//    public double measureSwayDoubleTight() {
//        return swayTight(d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureSwayFloatTight() {
//        return swayTight(f[x++ & 1023]);
//    }
//
//    @Benchmark
//    public double measureSwayBitDoubleTight() {
//        return swayTightBit(d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureSwayBitFloatTight() {
//        return swayTightBit(f[x++ & 1023]);
//    }
//
//    public static double zigzagBit(final double value)
//    {
//        final long s = Double.doubleToLongBits(value + (value < 0f ? -2.0 : 2.0)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
//        return (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L)>>51)) & 0xfffffffffffffL)
//                | 0x4010000000000000L) - 5.0);
//    }
//
//    public static float zigzagBit(final float value)
//    {
//        final int s = Float.floatToIntBits(value + (value < 0f ? -2f : 2f)), m = (s >>> 23 & 0xFF) - 0x80, sm = s << m;
//        return (Float.intBitsToFloat(((sm ^ -((sm & 0x00400000)>>22)) & 0x007fffff)
//                | 0x40800000) - 5f);
//    }
//
//    @Benchmark
//    public double measureZigzagDouble() {
//        return zigzag(d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureZigzagFloat() {
//        return zigzag(f[x++ & 1023]);
//    }
//
//    @Benchmark
//    public double measureZigzagBitDouble() {
//        return zigzagBit(d[x++ & 1023]);
//    }
//
//    @Benchmark
//    public float measureZigzagBitFloat() {
//        return zigzagBit(f[x++ & 1023]);
//    }

//    @Benchmark
//    public double measurePerlin2D() {
//        return PerlinNoise.noise(++x * 0.03125, --y * 0.03125);
//    }
//
//    @Benchmark
//    public double measurePerlin3D() {
//        return PerlinNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
//    }
//
//    @Benchmark
//    public double measurePerlin4D() {
//        return PerlinNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
//    }

//    @Benchmark
//    public double measureWhirling2D() {
//        return WhirlingNoise.noise(++x * 0.03125, --y * 0.03125);
//    }
//
//    @Benchmark
//    public double measureWhirling3D() {
//        return WhirlingNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
//    }
//
//    @Benchmark
//    public double measureWhirling4D() {
//        return WhirlingNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
//    }
//
//    @Benchmark
//    public float measureWhirlingAlt2D() {
//        return WhirlingNoise.noiseAlt(++x * 0.03125, --y * 0.03125);
//    }
//
//    @Benchmark
//    public float measureWhirlingAlt3D() {
//        return WhirlingNoise.noiseAlt(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
//    }

//    @Benchmark
//    public long measureMerlin2D() {
//        return MerlinNoise.noise2D(++x, --y, 1024L, 16, 8);
//    }
//
//    @Benchmark
//    public long measureMerlin3D() {
//        return MerlinNoise.noise3D(++x, --y, z++, 1024L, 16, 8);
//    }
//

//    @Benchmark
//    public double measureClassicNoise2D() {
//    return ClassicNoise.instance.getNoise(++x * 0.03125, --y * 0.03125);
//}
//    @Benchmark
//    public double measureClassicNoise3D() {
//        return ClassicNoise.instance.getNoise(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
//    }
//    @Benchmark
//    public double measureClassicNoise4D() {
//        return ClassicNoise.instance.getNoise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
//    }
    
//    @Benchmark
//    public double measureJitterNoise2D() {
//        return JitterNoise.instance.getNoise(++x * 0.03125, --y * 0.03125);
//    }
//    @Benchmark
//    public double measureJitterNoise3D() {
//        return JitterNoise.instance.getNoise(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
//    }
//    @Benchmark
//    public double measureJitterNoise4D() {
//        return JitterNoise.instance.getNoise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
//    }

    @Benchmark
    public double measureSeeded2D() {
        return SeededNoise.noise(++x * 0.03125, --y * 0.03125, 1024L);
    }

    @Benchmark
    public double measureSeeded3D() {
        return SeededNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, 1024L);
    }

    @Benchmark
    public double measureSeeded4D() {
        return SeededNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125, 1024L);
    }

    @Benchmark
    public double measureSeeded5D() {
        return SeededNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125, u++ * 0.03125, 1024L);
    }

    @Benchmark
    public double measureSeeded6D() {
        return SeededNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125, ++u * 0.03125, ++v * 0.03125, 1024L);
    }

    @Benchmark
    public float measureFastNoise2D() { return fast.getSimplex(++x, --y); }

    @Benchmark
    public float measureFastNoise3D() {
        return fast.getSimplex(++x, --y, z++);
    }

    @Benchmark
    public float measureFastNoise4D() {
        return fast.getSimplex(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureFastNoise5D() {
        return fast.getSimplex(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public double measureFastNoise6D() { return fast.getSimplex(++x, --y, z++, w--, ++u, ++v); }

    @Benchmark
    public float measureVastNoise2D() { return vast.getSimplex(++x, --y); }

    @Benchmark
    public float measureVastNoise3D() {
        return vast.getSimplex(++x, --y, z++);
    }

    @Benchmark
    public float measureVastNoise4D() {
        return vast.getSimplex(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureVastNoise5D() {
        return vast.getSimplex(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public double measureVastNoise6D() { return vast.getSimplex(++x, --y, z++, w--, ++u, ++v); }


    @Benchmark
    public float measureXastNoise2D() { return xast.getSimplex(++x, --y); }

    @Benchmark
    public float measureXastNoise3D() {
        return xast.getSimplex(++x, --y, z++);
    }

    @Benchmark
    public float measureXastNoise4D() {
        return xast.getSimplex(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureXastNoise5D() {
        return xast.getSimplex(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public double measureXastNoise6D() { return xast.getSimplex(++x, --y, z++, w--, ++u, ++v); }


    @Benchmark
    public float measureFastFoamNoise2D() { return fastFoam.getFoam(++x, --y); }

    @Benchmark
    public float measureFastFoamNoise3D() { return fastFoam.getFoam(++x, --y, z++);
    }

    @Benchmark
    public float measureFastFoamNoise4D() { return fastFoam.getFoam(++x, --y, z++, w--);
    }

    @Benchmark
    public float measureFastFoamNoise5D() { return fastFoam.getFoam(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public float measureFastFoamNoise6D() { return fastFoam.getFoam(++x, --y, z++, w--, ++u, ++v); }

    @Benchmark
    public double measureFoamNoise2D() { return foam.getNoise(++x, --y); }

    @Benchmark
    public double measureFoamNoise3D() { return foam.getNoise(++x, --y, z++);
    }

    @Benchmark
    public double measureFoamNoise4D() { return foam.getNoise(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureFoamNoise5D() { return foam.getNoise(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public double measureFoamNoise6D() { return foam.getNoise(++x, --y, z++, w--, ++u, ++v); }

    @Benchmark
    public float measureFastPerlinNoise2D() { return fastPerlin.getPerlin(++x, --y); }

    @Benchmark
    public float measureFastPerlinNoise3D() { return fastPerlin.getPerlin(++x, --y, z++);
    }

    @Benchmark
    public float measureFastPerlinNoise4D() { return fastPerlin.getPerlin(++x, --y, z++, w--);
    }

    @Benchmark
    public float measureFastPerlinNoise5D() { return fastPerlin.getPerlin(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public float measureFastPerlinNoise6D() { return fastPerlin.getPerlin(++x, --y, z++, w--, ++u, ++v); }

    @Benchmark
    public double measurePerlinNoise2D() { return perlin.getNoise(++x, --y); }

    @Benchmark
    public double measurePerlinNoise3D() { return perlin.getNoise(++x, --y, z++);
    }

    @Benchmark
    public double measurePerlinNoise4D() { return perlin.getNoise(++x, --y, z++, w--);
    }

    @Benchmark
    public double measurePerlinNoise5D() { return perlin.getNoise(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public double measurePerlinNoise6D() { return perlin.getNoise(++x, --y, z++, w--, ++u, ++v); }

    @Benchmark
    public float measureFnospiral2D() {
        return fno.getSimplex(++x, --y);
    }

    @Benchmark
    public float measureFnospiral3D() {
        return fno.getSimplex(++x, --y, z++);
    }

    @Benchmark
    public float measureFnospiral4D() {
        return fno.getSimplex(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureFnospiral6D() {
        return fno.getSimplex(++x, --y, z++, w--, ++u, ++v);
    }



    @Benchmark
    public double measureOpenSimplex2D() {
        return OpenSimplexNoise.instance.getNoise(++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureOpenSimplex3D() {
        return OpenSimplexNoise.instance.getNoise(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureOpenSimplex4D() {
        return OpenSimplexNoise.instance.getNoise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public double measureOSFNoise2D() {
        return osf.noise2(++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureOSFNoise3D() {
        return osf.noise3_Classic(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureOSFNoise4D() {
        return osf.noise4_Classic(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public double measureOSSNoise2D() {
        return oss.noise2(++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureOSSNoise3D() {
        return oss.noise3_Classic(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureOSSNoise4D() {
        return oss.noise4_Classic(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public double measureOSHNoise2D() {
        return osh.noise2(++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureOSHNoise3D() {
        return osh.noise3_Classic(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureOSHNoise4D() {
        return osh.noise4_Classic(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public float measureBookNoise2D() { return book.query(++x, --y, 0.03125f); }

    @Benchmark
    public double measureGustNoise2D() {
        return GustavsonSimplexNoise.noise(++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureGustNoise3D() {
        return GustavsonSimplexNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureGustNoise4D() {
        return GustavsonSimplexNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public double measureOSN2F2D() {
        return OSN2F.noise2(12345L, ++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureOSN2F3D() {
        return OSN2F.noise3_ImproveXY(12345L, ++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureOSN2F4D() {
        return OSN2F.noise4_ImproveXY_ImproveZW(12345L, ++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public double measureOSN2S2D() {
        return OSN2S.noise2(12345L, ++x * 0.03125, --y * 0.03125);
    }

    @Benchmark
    public double measureOSN2S3D() {
        return OSN2S.noise3_ImproveXY(12345L, ++x * 0.03125, --y * 0.03125, z++ * 0.03125);
    }

    @Benchmark
    public double measureOSN2S4D() {
        return OSN2S.noise4_ImproveXY_ImproveZW(12345L, ++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125);
    }

    @Benchmark
    public float measureSquadNoise2D() { return squad.getSimplex(++x, --y); }

    @Benchmark
    public float measureSquadNoise3D() {
        return squad.getSimplex(++x, --y, z++);
    }

    @Benchmark
    public float measureSquadNoise4D() {
        return squad.getSimplex(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureSquadNoise5D() {
        return squad.getSimplex(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public double measureSquadNoise6D() { return squad.getSimplex(++x, --y, z++, w--, ++u, ++v); }

    @Benchmark
    public float measureSquadFoamNoise2D() { return squadFoam.getFoam(++x, --y); }

    @Benchmark
    public float measureSquadFoamNoise3D() { return squadFoam.getFoam(++x, --y, z++);
    }

    @Benchmark
    public float measureSquadFoamNoise4D() { return squadFoam.getFoam(++x, --y, z++, w--);
    }

    @Benchmark
    public float measureSquadFoamNoise5D() { return squadFoam.getFoam(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public float measureSquadFoamNoise6D() { return squadFoam.getFoam(++x, --y, z++, w--, ++u, ++v); }


    @Benchmark
    public float measureSquadPerlinNoise2D() { return squadPerlin.getPerlin(++x, --y); }

    @Benchmark
    public float measureSquadPerlinNoise3D() { return squadPerlin.getPerlin(++x, --y, z++);
    }

    @Benchmark
    public float measureSquadPerlinNoise4D() { return squadPerlin.getPerlin(++x, --y, z++, w--);
    }

    @Benchmark
    public float measureSquadPerlinNoise5D() { return squadPerlin.getPerlin(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public float measureSquadPerlinNoise6D() { return squadPerlin.getPerlin(++x, --y, z++, w--, ++u, ++v); }


    @Benchmark
    public float measureSquadTaffyNoise2D() { return squadTaffy.getTaffy(++x, --y); }

    @Benchmark
    public float measureSquadTaffyNoise3D() { return squadTaffy.getTaffy(++x, --y, z++);
    }

    @Benchmark
    public float measureSquadTaffyNoise4D() { return squadTaffy.getTaffy(++x, --y, z++, w--);
    }

    @Benchmark
    public float measureSquadTaffyNoise5D() { return squadTaffy.getTaffy(++x, --y, z++, w--, ++u);
    }

    @Benchmark
    public float measureSquadTaffyNoise6D() { return squadTaffy.getTaffy(++x, --y, z++, w--, ++u, ++v); }


//    @Benchmark
//    public float measureFast3Octave3D() {
//        return fast3.getSimplexFractal(++x, --y, z++);
//    }
//
//    @Benchmark
//    public float measureFast5Octave3D() {
//        return fast5.getSimplexFractal(++x, --y, z++);
//    }
//
//    @Benchmark
//    public float measureFast3Octave4D() {
//        return fast3.getSimplexFractal(++x, --y, z++, w--);
//    }
//
//    @Benchmark
//    public float measureFast5Octave4D() {
//        return fast5.getSimplexFractal(++x, --y, z++, w--);
//    }
//
//    @Benchmark
//    public double measureWhirling3Octave3D() {
//        return whirling3.getNoise(++x, --y, z++);
//    }
//
//    @Benchmark
//    public double measureWhirling5Octave3D() {
//        return whirling5.getNoise(++x, --y, z++);
//    }
//
//    @Benchmark
//    public double measureWhirling3Octave4D() {
//        return whirling3_4.getNoise(++x, --y, z++, w--);
//    }
//
//    @Benchmark
//    public double measureWhirling5Octave4D() {
//        return whirling5_4.getNoise(++x, --y, z++, w--);
//    }

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
     *    $ java -jar benchmarks.jar NoiseBenchmark -wi 4 -i 4 -f 1
     *
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NoiseBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(30))
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
//    public static void main2(String[] args){
//        for (float i = -16f; i <= 16f; i+= 0.0625f) {
//            System.out.printf("Float %f : NumberTools %f , Bit %f\n", i, NumberTools.zigzag(i), zigzagBit(i));
//        }
//        for (double i = -16.0; i <= 16.0; i+= 0.0625) {
//            System.out.printf("Double %f : NumberTools %f , Bit %f\n", i, NumberTools.zigzag(i), zigzagBit(i));
//        }
//    }
}
