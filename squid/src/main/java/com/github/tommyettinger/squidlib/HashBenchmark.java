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
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.FakeLanguageGen;
import squidpony.squidmath.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark results for the competitive hashes; (len) is the number of ints hashed per op.
 * <pre>
 * Benchmark                      (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doIntCurlup32        5  avgt    3   10.410 ±  0.235  ns/op
 * HashBenchmark.doIntCurlup32       10  avgt    3   12.926 ±  0.137  ns/op
 * HashBenchmark.doIntCurlup32       20  avgt    3   20.346 ±  2.662  ns/op
 * HashBenchmark.doIntCurlup32       40  avgt    3   31.253 ±  0.744  ns/op
 * HashBenchmark.doIntCurlup32       80  avgt    3   53.240 ±  6.534  ns/op
 * HashBenchmark.doIntCurlup32      160  avgt    3   99.011 ±  0.975  ns/op
 * HashBenchmark.doIntCurlup64        5  avgt    3   11.157 ±  0.117  ns/op
 * HashBenchmark.doIntCurlup64       10  avgt    3   13.203 ±  0.101  ns/op
 * HashBenchmark.doIntCurlup64       20  avgt    3   19.823 ±  0.122  ns/op
 * HashBenchmark.doIntCurlup64       40  avgt    3   27.836 ±  0.757  ns/op
 * HashBenchmark.doIntCurlup64       80  avgt    3   46.816 ±  1.154  ns/op
 * HashBenchmark.doIntCurlup64      160  avgt    3   87.071 ±  0.785  ns/op
 * HashBenchmark.doIntHive32          5  avgt    3   14.193 ±  0.185  ns/op
 * HashBenchmark.doIntHive32         10  avgt    3   20.856 ±  2.633  ns/op
 * HashBenchmark.doIntHive32         20  avgt    3   27.403 ±  1.202  ns/op
 * HashBenchmark.doIntHive32         40  avgt    3   50.747 ±  1.236  ns/op
 * HashBenchmark.doIntHive32         80  avgt    3   95.800 ±  0.511  ns/op
 * HashBenchmark.doIntHive32        160  avgt    3  218.991 ±  1.624  ns/op
 * HashBenchmark.doIntHive64          5  avgt    3   12.095 ±  0.060  ns/op
 * HashBenchmark.doIntHive64         10  avgt    3   17.121 ±  0.067  ns/op
 * HashBenchmark.doIntHive64         20  avgt    3   26.109 ±  3.191  ns/op
 * HashBenchmark.doIntHive64         40  avgt    3   38.862 ±  0.740  ns/op
 * HashBenchmark.doIntHive64         80  avgt    3   72.409 ±  1.109  ns/op
 * HashBenchmark.doIntHive64        160  avgt    3  131.376 ±  0.232  ns/op
 * HashBenchmark.doIntJDK32           5  avgt    3    7.305 ±  0.045  ns/op
 * HashBenchmark.doIntJDK32          10  avgt    3   10.619 ±  0.092  ns/op
 * HashBenchmark.doIntJDK32          20  avgt    3   19.018 ±  0.125  ns/op
 * HashBenchmark.doIntJDK32          40  avgt    3   33.440 ±  0.503  ns/op
 * HashBenchmark.doIntJDK32          80  avgt    3   61.018 ±  0.949  ns/op
 * HashBenchmark.doIntJDK32         160  avgt    3  123.563 ±  1.136  ns/op
 * HashBenchmark.doIntJDK32Mixed      5  avgt    3    9.047 ±  0.810  ns/op
 * HashBenchmark.doIntJDK32Mixed     10  avgt    3   11.374 ±  0.088  ns/op
 * HashBenchmark.doIntJDK32Mixed     20  avgt    3   18.380 ±  0.289  ns/op
 * HashBenchmark.doIntJDK32Mixed     40  avgt    3   35.397 ±  0.618  ns/op
 * HashBenchmark.doIntJDK32Mixed     80  avgt    3   63.677 ±  0.376  ns/op
 * HashBenchmark.doIntJDK32Mixed    160  avgt    3  125.909 ±  1.834  ns/op
 * HashBenchmark.doIntMist32          5  avgt    3   11.563 ±  0.046  ns/op
 * HashBenchmark.doIntMist32         10  avgt    3   19.236 ±  0.381  ns/op
 * HashBenchmark.doIntMist32         20  avgt    3   25.740 ±  0.892  ns/op
 * HashBenchmark.doIntMist32         40  avgt    3   42.987 ±  0.848  ns/op
 * HashBenchmark.doIntMist32         80  avgt    3   74.014 ±  0.319  ns/op
 * HashBenchmark.doIntMist32        160  avgt    3  141.042 ±  4.097  ns/op
 * HashBenchmark.doIntMist64          5  avgt    3   11.682 ±  0.178  ns/op
 * HashBenchmark.doIntMist64         10  avgt    3   19.638 ±  0.136  ns/op
 * HashBenchmark.doIntMist64         20  avgt    3   28.129 ±  2.834  ns/op
 * HashBenchmark.doIntMist64         40  avgt    3   46.732 ±  0.808  ns/op
 * HashBenchmark.doIntMist64         80  avgt    3   72.067 ±  1.171  ns/op
 * HashBenchmark.doIntMist64        160  avgt    3  140.166 ±  0.218  ns/op
 * HashBenchmark.doIntWater32         5  avgt    3   13.064 ±  0.257  ns/op
 * HashBenchmark.doIntWater32        10  avgt    3   17.203 ±  0.277  ns/op
 * HashBenchmark.doIntWater32        20  avgt    3   24.044 ±  0.165  ns/op
 * HashBenchmark.doIntWater32        40  avgt    3   37.323 ±  0.317  ns/op
 * HashBenchmark.doIntWater32        80  avgt    3   63.500 ±  1.442  ns/op
 * HashBenchmark.doIntWater32       160  avgt    3  119.901 ±  0.734  ns/op
 * HashBenchmark.doIntWater64         5  avgt    3   15.180 ±  1.386  ns/op
 * HashBenchmark.doIntWater64        10  avgt    3   17.619 ±  0.268  ns/op
 * HashBenchmark.doIntWater64        20  avgt    3   24.904 ±  0.035  ns/op
 * HashBenchmark.doIntWater64        40  avgt    3   35.126 ±  1.256  ns/op
 * HashBenchmark.doIntWater64        80  avgt    3   58.549 ± 18.751  ns/op
 * HashBenchmark.doIntWater64       160  avgt    3  109.082 ±  4.224  ns/op
 * HashBenchmark.doIntWisp32          5  avgt    3    8.624 ±  0.207  ns/op
 * HashBenchmark.doIntWisp32         10  avgt    3   12.757 ±  0.245  ns/op
 * HashBenchmark.doIntWisp32         20  avgt    3   19.153 ±  0.512  ns/op
 * HashBenchmark.doIntWisp32         40  avgt    3   32.001 ±  4.718  ns/op
 * HashBenchmark.doIntWisp32         80  avgt    3   47.737 ±  0.062  ns/op
 * HashBenchmark.doIntWisp32        160  avgt    3   86.610 ±  0.679  ns/op
 * HashBenchmark.doIntWisp64          5  avgt    3    9.038 ±  0.201  ns/op
 * HashBenchmark.doIntWisp64         10  avgt    3   12.187 ±  0.050  ns/op
 * HashBenchmark.doIntWisp64         20  avgt    3   19.110 ±  0.036  ns/op
 * HashBenchmark.doIntWisp64         40  avgt    3   30.891 ±  0.696  ns/op
 * HashBenchmark.doIntWisp64         80  avgt    3   46.939 ±  0.777  ns/op
 * HashBenchmark.doIntWisp64        160  avgt    3   82.321 ±  1.395  ns/op
 * HashBenchmark.doIntYolk32          5  avgt    3   14.332 ±  0.572  ns/op
 * HashBenchmark.doIntYolk32         10  avgt    3   17.965 ±  0.370  ns/op
 * HashBenchmark.doIntYolk32         20  avgt    3   24.719 ±  9.866  ns/op
 * HashBenchmark.doIntYolk32         40  avgt    3   37.274 ±  2.063  ns/op
 * HashBenchmark.doIntYolk32         80  avgt    3   62.619 ±  0.303  ns/op
 * HashBenchmark.doIntYolk32        160  avgt    3  120.233 ±  2.963  ns/op
 * HashBenchmark.doIntYolk64          5  avgt    3   15.026 ±  0.546  ns/op
 * HashBenchmark.doIntYolk64         10  avgt    3   17.716 ±  0.111  ns/op
 * HashBenchmark.doIntYolk64         20  avgt    3   25.100 ±  1.029  ns/op
 * HashBenchmark.doIntYolk64         40  avgt    3   37.374 ±  1.111  ns/op
 * HashBenchmark.doIntYolk64         80  avgt    3   59.446 ±  0.483  ns/op
 * HashBenchmark.doIntYolk64        160  avgt    3  114.024 ±  2.423  ns/op
 * </pre>
 * Of these, Curlup, Water, and Yolk pass the latest SMHasher test suite. Hive comes closer than the others, but still
 * fails quite a few tests. Wisp fails about as many tests as the JDK's basic multiplicative hashing algorithm, but at
 * least doesn't have visual artifacts, which the JDK hashes absolutely do. Curlup and Yolk allow 64 bits of salt for
 * the hash, but Curlup also uses much less time per input than Water and Yolk while still passing tests.
 * <br>
 * So the gist of it is, we just need to use Curlup more for int arrays, especially when the inputs may be large.
 * It doesn't perform as well on long arrays or especially on CharSequences.
 * <br>
 * Long array results:
 * <pre>
 * Benchmark                       (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doLongCurlup32        5  avgt    3   18.694 ±  0.222  ns/op
 * HashBenchmark.doLongCurlup32       10  avgt    3   22.333 ±  5.216  ns/op
 * HashBenchmark.doLongCurlup32       20  avgt    3   28.693 ±  0.300  ns/op
 * HashBenchmark.doLongCurlup32       40  avgt    3   40.775 ±  1.826  ns/op
 * HashBenchmark.doLongCurlup32       80  avgt    3   64.048 ±  3.346  ns/op
 * HashBenchmark.doLongCurlup32      160  avgt    3  132.503 ±  5.074  ns/op
 * HashBenchmark.doLongCurlup64        5  avgt    3   17.427 ±  0.213  ns/op
 * HashBenchmark.doLongCurlup64       10  avgt    3   19.928 ±  0.264  ns/op
 * HashBenchmark.doLongCurlup64       20  avgt    3   28.505 ±  2.483  ns/op
 * HashBenchmark.doLongCurlup64       40  avgt    3   39.902 ±  1.801  ns/op
 * HashBenchmark.doLongCurlup64       80  avgt    3   66.251 ±  7.010  ns/op
 * HashBenchmark.doLongCurlup64      160  avgt    3  123.253 ±  4.363  ns/op
 * HashBenchmark.doLongHive32          5  avgt    3   12.234 ±  0.191  ns/op
 * HashBenchmark.doLongHive32         10  avgt    3   18.545 ±  0.084  ns/op
 * HashBenchmark.doLongHive32         20  avgt    3   25.778 ±  2.348  ns/op
 * HashBenchmark.doLongHive32         40  avgt    3   40.216 ±  3.080  ns/op
 * HashBenchmark.doLongHive32         80  avgt    3   68.896 ±  0.645  ns/op
 * HashBenchmark.doLongHive32        160  avgt    3  140.878 ±  5.307  ns/op
 * HashBenchmark.doLongHive64          5  avgt    3   11.800 ±  0.243  ns/op
 * HashBenchmark.doLongHive64         10  avgt    3   18.921 ±  0.500  ns/op
 * HashBenchmark.doLongHive64         20  avgt    3   24.095 ±  1.752  ns/op
 * HashBenchmark.doLongHive64         40  avgt    3   39.381 ±  5.871  ns/op
 * HashBenchmark.doLongHive64         80  avgt    3   71.798 ±  0.493  ns/op
 * HashBenchmark.doLongHive64        160  avgt    3  143.421 ±  1.469  ns/op
 * HashBenchmark.doLongJDK32           5  avgt    3   10.380 ±  0.392  ns/op
 * HashBenchmark.doLongJDK32          10  avgt    3   17.558 ±  0.307  ns/op
 * HashBenchmark.doLongJDK32          20  avgt    3   24.730 ±  0.543  ns/op
 * HashBenchmark.doLongJDK32          40  avgt    3   38.566 ±  0.336  ns/op
 * HashBenchmark.doLongJDK32          80  avgt    3   73.082 ±  0.195  ns/op
 * HashBenchmark.doLongJDK32         160  avgt    3  143.428 ±  2.160  ns/op
 * HashBenchmark.doLongJDK32Mixed      5  avgt    3   10.025 ±  0.049  ns/op
 * HashBenchmark.doLongJDK32Mixed     10  avgt    3   18.726 ±  1.232  ns/op
 * HashBenchmark.doLongJDK32Mixed     20  avgt    3   25.842 ±  1.754  ns/op
 * HashBenchmark.doLongJDK32Mixed     40  avgt    3   40.925 ±  2.422  ns/op
 * HashBenchmark.doLongJDK32Mixed     80  avgt    3   75.287 ±  0.093  ns/op
 * HashBenchmark.doLongJDK32Mixed    160  avgt    3  142.904 ±  1.688  ns/op
 * HashBenchmark.doLongMist32          5  avgt    3   11.330 ±  0.124  ns/op
 * HashBenchmark.doLongMist32         10  avgt    3   19.212 ±  0.270  ns/op
 * HashBenchmark.doLongMist32         20  avgt    3   30.946 ±  3.066  ns/op
 * HashBenchmark.doLongMist32         40  avgt    3   53.447 ±  0.521  ns/op
 * HashBenchmark.doLongMist32         80  avgt    3   99.031 ±  7.741  ns/op
 * HashBenchmark.doLongMist32        160  avgt    3  201.561 ±  2.213  ns/op
 * HashBenchmark.doLongMist64          5  avgt    3   11.517 ±  0.215  ns/op
 * HashBenchmark.doLongMist64         10  avgt    3   20.747 ±  0.272  ns/op
 * HashBenchmark.doLongMist64         20  avgt    3   29.717 ±  0.468  ns/op
 * HashBenchmark.doLongMist64         40  avgt    3   42.417 ±  3.498  ns/op
 * HashBenchmark.doLongMist64         80  avgt    3   72.929 ±  1.613  ns/op
 * HashBenchmark.doLongMist64        160  avgt    3  141.127 ±  9.716  ns/op
 * HashBenchmark.doLongWater32         5  avgt    3   15.908 ±  0.444  ns/op
 * HashBenchmark.doLongWater32        10  avgt    3   20.040 ±  0.568  ns/op
 * HashBenchmark.doLongWater32        20  avgt    3   28.341 ±  0.233  ns/op
 * HashBenchmark.doLongWater32        40  avgt    3   40.796 ±  0.409  ns/op
 * HashBenchmark.doLongWater32        80  avgt    3   63.259 ±  1.037  ns/op
 * HashBenchmark.doLongWater32       160  avgt    3  123.069 ±  3.251  ns/op
 * HashBenchmark.doLongWater64         5  avgt    3   17.944 ±  0.953  ns/op
 * HashBenchmark.doLongWater64        10  avgt    3   21.508 ±  0.335  ns/op
 * HashBenchmark.doLongWater64        20  avgt    3   26.584 ±  1.881  ns/op
 * HashBenchmark.doLongWater64        40  avgt    3   40.209 ±  0.656  ns/op
 * HashBenchmark.doLongWater64        80  avgt    3   66.793 ±  1.138  ns/op
 * HashBenchmark.doLongWater64       160  avgt    3  129.456 ±  1.458  ns/op
 * HashBenchmark.doLongWisp32          5  avgt    3    9.418 ±  0.083  ns/op
 * HashBenchmark.doLongWisp32         10  avgt    3   13.137 ±  0.283  ns/op
 * HashBenchmark.doLongWisp32         20  avgt    3   21.888 ±  0.831  ns/op
 * HashBenchmark.doLongWisp32         40  avgt    3   32.850 ±  0.649  ns/op
 * HashBenchmark.doLongWisp32         80  avgt    3   51.705 ±  1.318  ns/op
 * HashBenchmark.doLongWisp32        160  avgt    3  110.984 ±  2.548  ns/op
 * HashBenchmark.doLongWisp64          5  avgt    3    9.115 ±  0.242  ns/op
 * HashBenchmark.doLongWisp64         10  avgt    3   12.887 ±  0.981  ns/op
 * HashBenchmark.doLongWisp64         20  avgt    3   24.156 ±  4.409  ns/op
 * HashBenchmark.doLongWisp64         40  avgt    3   32.635 ±  0.769  ns/op
 * HashBenchmark.doLongWisp64         80  avgt    3   53.166 ±  5.443  ns/op
 * HashBenchmark.doLongWisp64        160  avgt    3  108.002 ±  1.669  ns/op
 * HashBenchmark.doLongYolk32          5  avgt    3   16.814 ±  0.554  ns/op
 * HashBenchmark.doLongYolk32         10  avgt    3   22.042 ±  0.139  ns/op
 * HashBenchmark.doLongYolk32         20  avgt    3   25.179 ±  0.446  ns/op
 * HashBenchmark.doLongYolk32         40  avgt    3   40.728 ±  3.335  ns/op
 * HashBenchmark.doLongYolk32         80  avgt    3   66.073 ±  1.266  ns/op
 * HashBenchmark.doLongYolk32        160  avgt    3  128.780 ± 16.935  ns/op
 * HashBenchmark.doLongYolk64          5  avgt    3   18.229 ±  0.108  ns/op
 * HashBenchmark.doLongYolk64         10  avgt    3   20.539 ±  0.651  ns/op
 * HashBenchmark.doLongYolk64         20  avgt    3   28.568 ±  1.056  ns/op
 * HashBenchmark.doLongYolk64         40  avgt    3   41.424 ±  3.241  ns/op
 * HashBenchmark.doLongYolk64         80  avgt    3   66.346 ±  0.597  ns/op
 * HashBenchmark.doLongYolk64        160  avgt    3  131.340 ±  5.007  ns/op
 * </pre>
 * <br>
 * On String or CharSequence inputs, Water and Yolk do well on quality and speed, Wisp is best on speed
 * (but not on quality), and Curlup lags behind on speed but retains high quality. A hybrid approach may
 * be optimal for hashing different types of array, using Yolk for Strings and Curlup for int arrays.
 * <pre>
 * Benchmark                   Mode  Cnt    Score   Error  Units
 * HashBenchmark.doCurlup32    avgt    3   87.707 ± 1.047  ns/op
 * HashBenchmark.doCurlup64    avgt    3   86.399 ± 0.598  ns/op
 * HashBenchmark.doHive32      avgt    3  107.000 ± 1.661  ns/op
 * HashBenchmark.doHive64      avgt    3   79.802 ± 0.997  ns/op
 * HashBenchmark.doJDK32       avgt    3   73.314 ± 1.375  ns/op
 * HashBenchmark.doJDK32Mixed  avgt    3   73.626 ± 1.603  ns/op
 * HashBenchmark.doMist32      avgt    3   74.812 ± 0.596  ns/op
 * HashBenchmark.doMist64      avgt    3   75.283 ± 0.913  ns/op
 * HashBenchmark.doWater32     avgt    3   68.136 ± 1.300  ns/op
 * HashBenchmark.doWater64     avgt    3   64.533 ± 0.272  ns/op
 * HashBenchmark.doWisp32      avgt    3   57.387 ± 0.351  ns/op
 * HashBenchmark.doWisp64      avgt    3   54.624 ± 0.675  ns/op
 * HashBenchmark.doYolk32      avgt    3   64.287 ± 2.183  ns/op
 * HashBenchmark.doYolk64      avgt    3   64.611 ± 0.688  ns/op
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class HashBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {

        @Param({ "5", "10", "20", "40", "80", "160" })
        public int len;
        public CharSequence[] words;
        public char[][] chars;
        public long[][] longs;
        public int[][] ints;
        public int idx;
        private final int[] intInputs = new int[65536];
        private final long[] longInputs = new long[65536];

//        @Benchmark
//        public long measurePointHash2D()
//        {
//            return PointHash.hashAll(longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measurePointHash3D()
//        {
//            return PointHash.hashAll(longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measurePointHash4D()
//        {
//            return PointHash.hashAll(longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measurePointHash6D()
//        {
//            return PointHash.hashAll(longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)], longInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measureHastyPointHash2D()
//        {
//            return HastyPointHash.hashAll(intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measureHastyPointHash3D()
//        {
//            return HastyPointHash.hashAll(intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measureHastyPointHash4D()
//        {
//            return HastyPointHash.hashAll(intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)]);
//        }
//
//        @Benchmark
//        public long measureHastyPointHash6D()
//        {
//            return HastyPointHash.hashAll(intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)], intInputs[(idx++ & 0xFFFF)]);
//        }

        @Setup(Level.Trial)
        public void setup() {
            MiniMover64RNG random = new MiniMover64RNG(1000L);
            FakeLanguageGen[] languages = new FakeLanguageGen[16];
            for (int i = 0; i < 16; i++) {
                languages[i] = FakeLanguageGen.randomLanguage(random.nextLong()).addAccents(0.8, 0.6);
            }
            words = new CharSequence[4096];
            chars = new char[4096][];
            longs = new long[4096][];
            ints = new int[4096][];
            for (int i = 0; i < 65536; i++) {
                intInputs[i] = (int)(longInputs[i] = random.nextLong());
            }
            for (int i = 0; i < 4096; i++) {
                String w = languages[i & 15].sentence(random.nextLong(), random.next(3) + 1, random.next(6)+9);
                chars[i] = w.toCharArray();
                words[i] = new StringBuilder(w);
                //final int len = (random.next(8)+1);
                long[] lon = new long[len];
                int[] inn = new int[len];
                for (int j = 0; j < len; j++) {
                    inn[j] = (int)(lon[j] = random.nextLong());
                }
                longs[i] = lon;
                ints[i] = inn;
            }
            idx = 0;
        }

    }

    @Benchmark
    public long doWisp64(BenchmarkState state)
    {
        return CrossHash.Wisp.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doWisp32(BenchmarkState state)
    {
        return CrossHash.Wisp.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }
    
    @Benchmark
    public long doCharWisp64(BenchmarkState state)
    {
        return CrossHash.Wisp.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharWisp32(BenchmarkState state)
    {
        return CrossHash.Wisp.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntWisp64(BenchmarkState state)
    {
        return CrossHash.Wisp.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntWisp32(BenchmarkState state)
    {
        return CrossHash.Wisp.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongWisp64(BenchmarkState state)
    {
        return CrossHash.Wisp.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongWisp32(BenchmarkState state)
    {
        return CrossHash.Wisp.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

//    @Benchmark
//    public long doLightning64(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash64(state.words[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doLightning32(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash(state.words[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doCharLightning64(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doCharLightning32(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash(state.chars[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doIntLightning64(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doIntLightning32(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash(state.ints[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doLongLightning64(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doLongLightning32(BenchmarkState state)
//    {
//        return CrossHash.Lightning.hash(state.longs[state.idx = state.idx + 1 & 4095]);
//    }
     
    @Benchmark
    public long doMist64(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doMist32(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharMist64(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharMist32(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntMist64(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntMist32(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongMist64(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongMist32(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }


    @Benchmark
    public long doHive64(BenchmarkState state)
    {
        return CrossHash.Hive.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doHive32(BenchmarkState state)
    {
        return CrossHash.Hive.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntHive64(BenchmarkState state)
    {
        return CrossHash.Hive.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntHive32(BenchmarkState state)
    {
        return CrossHash.Hive.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharHive64(BenchmarkState state)
    {
        return CrossHash.Hive.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharHive32(BenchmarkState state)
    {
        return CrossHash.Hive.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongHive64(BenchmarkState state)
    {
        return CrossHash.Hive.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongHive32(BenchmarkState state)
    {
        return CrossHash.Hive.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doWater64(BenchmarkState state)
    {
        return CrossHash.Water.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doWater32(BenchmarkState state)
    {
        return CrossHash.Water.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntWater64(BenchmarkState state)
    {
        return CrossHash.Water.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntWater32(BenchmarkState state)
    {
        return CrossHash.Water.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharWater64(BenchmarkState state)
    {
        return CrossHash.Water.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharWater32(BenchmarkState state)
    {
        return CrossHash.Water.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongWater64(BenchmarkState state)
    {
        return CrossHash.Water.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongWater32(BenchmarkState state)
    {
        return CrossHash.Water.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }


    @Benchmark
    public long doYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }


    @Benchmark
    public long doCurlup64(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCurlup32(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharCurlup64(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharCurlup32(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntCurlup64(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntCurlup32(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongCurlup64(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongCurlup32(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doJDK32(BenchmarkState state)
    {
        return hashCode(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doJDK32Mixed(BenchmarkState state)
    {
        return HashCommon.mix(hashCode(state.words[state.idx = state.idx + 1 & 4095]));
    }

    @Benchmark
    public int doCharJDK32(BenchmarkState state)
    {
        return Arrays.hashCode(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharJDK32Mixed(BenchmarkState state)
    {
        return HashCommon.mix(Arrays.hashCode(state.chars[state.idx = state.idx + 1 & 4095]));
    }
    @Benchmark
    public int doIntJDK32(BenchmarkState state)
    {
        return Arrays.hashCode(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntJDK32Mixed(BenchmarkState state)
    {
        return HashCommon.mix(Arrays.hashCode(state.ints[state.idx = state.idx + 1 & 4095]));
    }

    @Benchmark
    public int doLongJDK32(BenchmarkState state)
    {
        return Arrays.hashCode(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongJDK32Mixed(BenchmarkState state)
    {
        return HashCommon.mix(Arrays.hashCode(state.longs[state.idx = state.idx + 1 & 4095]));
    }




//    @Benchmark
//    public long doMetro64(BenchmarkState state)
//    {
//        return CrossHash.Metro.hash64(state.words[state.idx = state.idx + 1 & 4095]);
//    }
//    @Benchmark
//    public long doCharMetro64(BenchmarkState state)
//    {
//        return CrossHash.Metro.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
//    }
//    @Benchmark
//    public long doLongMetro64(BenchmarkState state)
//    {
//        return CrossHash.Metro.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
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
     *    $ java -jar target/benchmarks.jar HashBenchmark -wi 5 -i 4 -f 1
     *
     *    (we requested 8 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(8)
                .measurementIterations(8)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    /**
     * Acts like {@link Arrays#hashCode(char[])} but works on any CharSequence, including StringBuilder (which doesn't
     * have a hashCode() implementation of its own).
     * @param chars any CharSequence
     * @return a 32-bit hash of {@code chars}
     */
    public static int hashCode(CharSequence chars) {
        if (chars == null) return 0;
        int result = 1;
        final int len = chars.length();
        for (int i = 0; i < len; i++)
            result = 31 * result + chars.charAt(i);
        return result;
    }

}