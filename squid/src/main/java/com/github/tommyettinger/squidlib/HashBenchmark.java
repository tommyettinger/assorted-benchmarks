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

import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.random.WhiskerRandom;
import de.heidelberg.pvs.container_bench.generators.Wordlist;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.FakeLanguageGen;
import squidpony.squidmath.HashCommon;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
 * Doubles are... wild. There's a lot of unpredictable JIT behavior here.
 * <pre>
 * Benchmark                         (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doDoubleCurlup32        5  avgt    3   17.862 ±  0.155  ns/op
 * HashBenchmark.doDoubleCurlup32       10  avgt    3   23.878 ±  1.410  ns/op
 * HashBenchmark.doDoubleCurlup32       20  avgt    3   40.004 ±  0.772  ns/op
 * HashBenchmark.doDoubleCurlup32       40  avgt    3   67.521 ±  0.821  ns/op
 * HashBenchmark.doDoubleCurlup32       80  avgt    3  127.750 ±  1.898  ns/op
 * HashBenchmark.doDoubleCurlup32      160  avgt    3  235.422 ±  4.396  ns/op
 * HashBenchmark.doDoubleCurlup64        5  avgt    3   18.673 ±  0.563  ns/op
 * HashBenchmark.doDoubleCurlup64       10  avgt    3   25.145 ±  0.424  ns/op
 * HashBenchmark.doDoubleCurlup64       20  avgt    3   38.549 ±  0.670  ns/op
 * HashBenchmark.doDoubleCurlup64       40  avgt    3   70.016 ±  0.840  ns/op
 * HashBenchmark.doDoubleCurlup64       80  avgt    3  126.239 ±  0.250  ns/op
 * HashBenchmark.doDoubleCurlup64      160  avgt    3  251.886 ±  4.348  ns/op
 * HashBenchmark.doDoubleHive32          5  avgt    3   17.560 ±  0.122  ns/op
 * HashBenchmark.doDoubleHive32         10  avgt    3   23.555 ±  0.477  ns/op
 * HashBenchmark.doDoubleHive32         20  avgt    3   32.617 ±  0.241  ns/op
 * HashBenchmark.doDoubleHive32         40  avgt    3   56.100 ±  0.673  ns/op
 * HashBenchmark.doDoubleHive32         80  avgt    3  109.511 ±  0.646  ns/op
 * HashBenchmark.doDoubleHive32        160  avgt    3  247.982 ±  3.684  ns/op
 * HashBenchmark.doDoubleHive64          5  avgt    3   16.740 ±  0.232  ns/op
 * HashBenchmark.doDoubleHive64         10  avgt    3   22.964 ±  1.399  ns/op
 * HashBenchmark.doDoubleHive64         20  avgt    3   36.947 ±  0.605  ns/op
 * HashBenchmark.doDoubleHive64         40  avgt    3   62.399 ±  0.529  ns/op
 * HashBenchmark.doDoubleHive64         80  avgt    3  111.120 ±  0.722  ns/op
 * HashBenchmark.doDoubleHive64        160  avgt    3  219.099 ±  0.879  ns/op
 * HashBenchmark.doDoubleJDK32           5  avgt    3   16.805 ±  0.135  ns/op
 * HashBenchmark.doDoubleJDK32          10  avgt    3   22.139 ±  0.145  ns/op
 * HashBenchmark.doDoubleJDK32          20  avgt    3   33.618 ±  0.636  ns/op
 * HashBenchmark.doDoubleJDK32          40  avgt    3   58.112 ±  1.017  ns/op
 * HashBenchmark.doDoubleJDK32          80  avgt    3  107.664 ± 12.333  ns/op
 * HashBenchmark.doDoubleJDK32         160  avgt    3  234.287 ±  3.634  ns/op
 * HashBenchmark.doDoubleJDK32Mixed      5  avgt    3   15.507 ±  0.123  ns/op
 * HashBenchmark.doDoubleJDK32Mixed     10  avgt    3   23.212 ±  0.156  ns/op
 * HashBenchmark.doDoubleJDK32Mixed     20  avgt    3   32.366 ±  0.209  ns/op
 * HashBenchmark.doDoubleJDK32Mixed     40  avgt    3   55.055 ±  0.325  ns/op
 * HashBenchmark.doDoubleJDK32Mixed     80  avgt    3  105.745 ± 17.240  ns/op
 * HashBenchmark.doDoubleJDK32Mixed    160  avgt    3  211.812 ±  3.011  ns/op
 * HashBenchmark.doDoubleMist32          5  avgt    3   19.798 ±  0.087  ns/op
 * HashBenchmark.doDoubleMist32         10  avgt    3   27.990 ±  0.470  ns/op
 * HashBenchmark.doDoubleMist32         20  avgt    3   46.865 ±  0.329  ns/op
 * HashBenchmark.doDoubleMist32         40  avgt    3   77.725 ±  0.453  ns/op
 * HashBenchmark.doDoubleMist32         80  avgt    3  149.085 ±  3.330  ns/op
 * HashBenchmark.doDoubleMist32        160  avgt    3  280.914 ±  4.037  ns/op
 * HashBenchmark.doDoubleMist64          5  avgt    3   19.685 ±  0.317  ns/op
 * HashBenchmark.doDoubleMist64         10  avgt    3   27.463 ±  0.776  ns/op
 * HashBenchmark.doDoubleMist64         20  avgt    3   45.702 ±  5.291  ns/op
 * HashBenchmark.doDoubleMist64         40  avgt    3   75.578 ±  0.769  ns/op
 * HashBenchmark.doDoubleMist64         80  avgt    3  144.378 ±  0.186  ns/op
 * HashBenchmark.doDoubleMist64        160  avgt    3  278.566 ±  6.112  ns/op
 * HashBenchmark.doDoubleWater32         5  avgt    3   20.581 ±  0.235  ns/op
 * HashBenchmark.doDoubleWater32        10  avgt    3   26.075 ±  0.255  ns/op
 * HashBenchmark.doDoubleWater32        20  avgt    3   41.856 ±  0.335  ns/op
 * HashBenchmark.doDoubleWater32        40  avgt    3   70.633 ±  0.754  ns/op
 * HashBenchmark.doDoubleWater32        80  avgt    3  132.384 ±  0.392  ns/op
 * HashBenchmark.doDoubleWater32       160  avgt    3  252.140 ±  0.857  ns/op
 * HashBenchmark.doDoubleWater64         5  avgt    3   20.103 ±  0.358  ns/op
 * HashBenchmark.doDoubleWater64        10  avgt    3   26.784 ±  0.153  ns/op
 * HashBenchmark.doDoubleWater64        20  avgt    3   44.208 ±  0.844  ns/op
 * HashBenchmark.doDoubleWater64        40  avgt    3   70.892 ±  0.466  ns/op
 * HashBenchmark.doDoubleWater64        80  avgt    3  133.423 ±  0.491  ns/op
 * HashBenchmark.doDoubleWater64       160  avgt    3  276.185 ±  4.176  ns/op
 * HashBenchmark.doDoubleWisp32          5  avgt    3   17.280 ±  0.406  ns/op
 * HashBenchmark.doDoubleWisp32         10  avgt    3   23.205 ±  0.245  ns/op
 * HashBenchmark.doDoubleWisp32         20  avgt    3   33.029 ±  0.353  ns/op
 * HashBenchmark.doDoubleWisp32         40  avgt    3   61.604 ±  1.712  ns/op
 * HashBenchmark.doDoubleWisp32         80  avgt    3  112.749 ±  0.677  ns/op
 * HashBenchmark.doDoubleWisp32        160  avgt    3  213.211 ±  2.001  ns/op
 * HashBenchmark.doDoubleWisp64          5  avgt    3   16.309 ±  0.138  ns/op
 * HashBenchmark.doDoubleWisp64         10  avgt    3   23.518 ±  0.262  ns/op
 * HashBenchmark.doDoubleWisp64         20  avgt    3   33.805 ±  2.741  ns/op
 * HashBenchmark.doDoubleWisp64         40  avgt    3   61.404 ±  8.952  ns/op
 * HashBenchmark.doDoubleWisp64         80  avgt    3  120.249 ±  0.625  ns/op
 * HashBenchmark.doDoubleWisp64        160  avgt    3  237.270 ±  4.311  ns/op
 * HashBenchmark.doDoubleYolk32          5  avgt    3   19.736 ±  0.306  ns/op
 * HashBenchmark.doDoubleYolk32         10  avgt    3   26.752 ±  1.257  ns/op
 * HashBenchmark.doDoubleYolk32         20  avgt    3   41.777 ±  0.733  ns/op
 * HashBenchmark.doDoubleYolk32         40  avgt    3   71.514 ±  4.097  ns/op
 * HashBenchmark.doDoubleYolk32         80  avgt    3  135.047 ±  1.478  ns/op
 * HashBenchmark.doDoubleYolk32        160  avgt    3  251.952 ±  5.212  ns/op
 * HashBenchmark.doDoubleYolk64          5  avgt    3   20.144 ±  0.396  ns/op
 * HashBenchmark.doDoubleYolk64         10  avgt    3   25.593 ±  0.180  ns/op
 * HashBenchmark.doDoubleYolk64         20  avgt    3   40.327 ±  1.947  ns/op
 * HashBenchmark.doDoubleYolk64         40  avgt    3   65.072 ±  0.907  ns/op
 * HashBenchmark.doDoubleYolk64         80  avgt    3  119.306 ±  0.126  ns/op
 * HashBenchmark.doDoubleYolk64        160  avgt    3  227.780 ±  5.605  ns/op
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
 * <br>
 * When those same Strings are converted to char arrays, Curlup does well again, comparably to
 * Yolk or Water. Yolk has some JIT quirk with 32-bit output that slows it down
 * significantly after a few runs. Water may have the same with 64-bit output.
 * <pre>
 * Benchmark                       (len)  Mode  Cnt    Score   Error  Units
 * HashBenchmark.doCharCurlup32        5  avgt    3   63.182 ± 0.586  ns/op
 * HashBenchmark.doCharCurlup64        5  avgt    3   61.863 ± 1.378  ns/op
 * HashBenchmark.doCharHive32          5  avgt    3  101.530 ± 1.097  ns/op
 * HashBenchmark.doCharHive64          5  avgt    3   67.584 ± 1.711  ns/op
 * HashBenchmark.doCharJDK32           5  avgt    3   69.711 ± 0.038  ns/op
 * HashBenchmark.doCharJDK32Mixed      5  avgt    3   68.932 ± 0.335  ns/op
 * HashBenchmark.doCharMist32          5  avgt    3   74.295 ± 3.046  ns/op
 * HashBenchmark.doCharMist64          5  avgt    3   72.579 ± 6.445  ns/op
 * HashBenchmark.doCharWater32         5  avgt    3   60.102 ± 2.549  ns/op
 * HashBenchmark.doCharWater64         5  avgt    3   72.465 ± 0.368  ns/op
 * HashBenchmark.doCharWisp32          5  avgt    3   53.399 ± 1.678  ns/op
 * HashBenchmark.doCharWisp64          5  avgt    3   50.898 ± 0.198  ns/op
 * HashBenchmark.doCharYolk32          5  avgt    3   79.310 ± 1.171  ns/op
 * HashBenchmark.doCharYolk64          5  avgt    3   60.510 ± 0.373  ns/op
 * </pre>
 * <br>
 * Trying out Frost, the results are quite bad...
 * <pre>
 * Benchmark                     (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doIntCurlup32       5  avgt    3   10.601 ±  0.298  ns/op
 * HashBenchmark.doIntCurlup32      25  avgt    3   24.212 ±  0.626  ns/op
 * HashBenchmark.doIntCurlup32     125  avgt    3   80.642 ±  4.457  ns/op
 * HashBenchmark.doIntCurlup64       5  avgt    3   10.888 ±  0.367  ns/op
 * HashBenchmark.doIntCurlup64      25  avgt    3   24.435 ±  0.947  ns/op
 * HashBenchmark.doIntCurlup64     125  avgt    3   68.260 ±  5.660  ns/op
 * HashBenchmark.doIntFrost32        5  avgt    3   14.281 ±  0.534  ns/op
 * HashBenchmark.doIntFrost32       25  avgt    3   43.926 ±  0.538  ns/op
 * HashBenchmark.doIntFrost32      125  avgt    3  189.267 ± 12.588  ns/op
 * HashBenchmark.doIntFrost64        5  avgt    3   15.897 ±  0.831  ns/op
 * HashBenchmark.doIntFrost64       25  avgt    3   42.853 ±  2.788  ns/op
 * HashBenchmark.doIntFrost64      125  avgt    3  193.168 ±  1.953  ns/op
 * HashBenchmark.doIntYolk32         5  avgt    3   13.743 ±  0.089  ns/op
 * HashBenchmark.doIntYolk32        25  avgt    3   26.972 ±  1.224  ns/op
 * HashBenchmark.doIntYolk32       125  avgt    3   95.805 ±  7.528  ns/op
 * HashBenchmark.doIntYolk64         5  avgt    3   14.056 ±  0.216  ns/op
 * HashBenchmark.doIntYolk64        25  avgt    3   28.648 ±  0.744  ns/op
 * HashBenchmark.doIntYolk64       125  avgt    3   92.012 ±  3.671  ns/op
 * HashBenchmark.doLongCurlup32      5  avgt    3   17.275 ±  0.997  ns/op
 * HashBenchmark.doLongCurlup32     25  avgt    3   28.826 ±  1.453  ns/op
 * HashBenchmark.doLongCurlup32    125  avgt    3   94.744 ±  7.336  ns/op
 * HashBenchmark.doLongCurlup64      5  avgt    3   18.412 ±  2.236  ns/op
 * HashBenchmark.doLongCurlup64     25  avgt    3   30.625 ±  0.760  ns/op
 * HashBenchmark.doLongCurlup64    125  avgt    3   94.464 ±  8.340  ns/op
 * HashBenchmark.doLongFrost32       5  avgt    3   17.750 ±  0.342  ns/op
 * HashBenchmark.doLongFrost32      25  avgt    3   51.827 ±  1.589  ns/op
 * HashBenchmark.doLongFrost32     125  avgt    3  232.881 ± 15.873  ns/op
 * HashBenchmark.doLongFrost64       5  avgt    3   17.941 ±  0.761  ns/op
 * HashBenchmark.doLongFrost64      25  avgt    3   49.939 ±  1.720  ns/op
 * HashBenchmark.doLongFrost64     125  avgt    3  232.361 ± 17.619  ns/op
 * HashBenchmark.doLongYolk32        5  avgt    3   17.076 ±  1.345  ns/op
 * HashBenchmark.doLongYolk32       25  avgt    3   29.851 ±  2.386  ns/op
 * HashBenchmark.doLongYolk32      125  avgt    3   93.546 ±  2.094  ns/op
 * HashBenchmark.doLongYolk64        5  avgt    3   17.948 ±  0.309  ns/op
 * HashBenchmark.doLongYolk64       25  avgt    3   30.836 ±  3.374  ns/op
 * HashBenchmark.doLongYolk64      125  avgt    3  105.815 ± 16.118  ns/op
 * </pre>
 * <br>
 * Measuring point hashes for 3D points:
 * <pre>
 * Benchmark                              (len)  Mode  Cnt  Score   Error  Units
 * HashBenchmark.measurePointHashBitwise      1  avgt    6  4.280 ± 0.159  ns/op
 * HashBenchmark.measurePointHashCantor       1  avgt    6  3.797 ± 0.062  ns/op
 * HashBenchmark.measurePointHashObject       1  avgt    6  2.898 ± 0.048  ns/op
 * HashBenchmark.measurePointHashPeloton      1  avgt    6  3.524 ± 0.300  ns/op
 * </pre>
 * Of course, the one with the fewest collisions is slowest, and the most collisions is fastest...
 * <br>
 * Quickly comparing Crease (using a poor approximation of AHash's folded multiplication) with Yolk...
 * <br>
 * Strings or CharSequences:
 * <pre>
 * Benchmark                 (len)  Mode  Cnt    Score   Error  Units
 * HashBenchmark.doCrease32      5  avgt    5  112.920 ± 3.535  ns/op
 * HashBenchmark.doCrease64      5  avgt    5  114.416 ± 0.346  ns/op
 * HashBenchmark.doYolk32        5  avgt    5   59.264 ± 1.346  ns/op
 * HashBenchmark.doYolk64        5  avgt    5   59.485 ± 2.426  ns/op
 * </pre>
 * <br>
 * Low-length int arrays:
 * <pre>
 * Benchmark                    (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doIntCrease32      5  avgt    5  11.529 ± 1.199  ns/op
 * HashBenchmark.doIntCrease32     10  avgt    5  15.718 ± 0.465  ns/op
 * HashBenchmark.doIntCrease32     20  avgt    5  26.772 ± 0.826  ns/op
 * HashBenchmark.doIntCrease64      5  avgt    5  10.961 ± 0.281  ns/op
 * HashBenchmark.doIntCrease64     10  avgt    5  16.070 ± 6.156  ns/op
 * HashBenchmark.doIntCrease64     20  avgt    5  26.014 ± 0.466  ns/op
 * HashBenchmark.doIntYolk32        5  avgt    5   9.930 ± 0.285  ns/op
 * HashBenchmark.doIntYolk32       10  avgt    5  11.690 ± 0.208  ns/op
 * HashBenchmark.doIntYolk32       20  avgt    5  17.404 ± 0.600  ns/op
 * HashBenchmark.doIntYolk64        5  avgt    5   9.965 ± 0.246  ns/op
 * HashBenchmark.doIntYolk64       10  avgt    5  12.140 ± 0.224  ns/op
 * HashBenchmark.doIntYolk64       20  avgt    5  17.515 ± 0.362  ns/op
 * </pre>
 * <br>
 * 1024-length int arrays:
 * <pre>
 * Benchmark                    (len)  Mode  Cnt     Score    Error  Units
 * HashBenchmark.doIntCrease32   1024  avgt    5  1535.474 ± 46.971  ns/op
 * HashBenchmark.doIntCrease64   1024  avgt    5  1539.585 ± 10.398  ns/op
 * HashBenchmark.doIntYolk32     1024  avgt    5   632.176 ± 12.081  ns/op
 * HashBenchmark.doIntYolk64     1024  avgt    5   679.969 ± 14.169  ns/op
 * </pre>
 * After this, Crease changed to be more like Curlup.
 * <br>
 * Testing all 64-bit int hashes:
 * <pre>
 * Benchmark                    (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doIntCrease64      5  avgt    5    9.329 ±  0.099  ns/op
 * HashBenchmark.doIntCrease64     10  avgt    5   11.462 ±  0.619  ns/op
 * HashBenchmark.doIntCrease64     20  avgt    5   15.756 ±  0.290  ns/op
 * HashBenchmark.doIntCrease64     40  avgt    5   22.974 ±  0.982  ns/op
 * HashBenchmark.doIntCrease64     80  avgt    5   41.785 ±  0.894  ns/op
 * HashBenchmark.doIntCrease64    160  avgt    5   81.165 ± 12.493  ns/op
 * HashBenchmark.doIntCurlup64      5  avgt    5    9.358 ±  0.090  ns/op
 * HashBenchmark.doIntCurlup64     10  avgt    5   11.745 ±  0.337  ns/op
 * HashBenchmark.doIntCurlup64     20  avgt    5   16.996 ±  1.042  ns/op
 * HashBenchmark.doIntCurlup64     40  avgt    5   24.843 ±  1.067  ns/op
 * HashBenchmark.doIntCurlup64     80  avgt    5   42.271 ±  1.402  ns/op
 * HashBenchmark.doIntCurlup64    160  avgt    5   76.630 ±  3.170  ns/op
 * HashBenchmark.doIntHive64        5  avgt    5   10.071 ±  0.368  ns/op
 * HashBenchmark.doIntHive64       10  avgt    5   12.967 ±  0.137  ns/op
 * HashBenchmark.doIntHive64       20  avgt    5   19.342 ±  0.271  ns/op
 * HashBenchmark.doIntHive64       40  avgt    5   31.414 ±  0.422  ns/op
 * HashBenchmark.doIntHive64       80  avgt    5   54.334 ±  0.970  ns/op
 * HashBenchmark.doIntHive64      160  avgt    5  100.937 ±  1.811  ns/op
 * HashBenchmark.doIntMist64        5  avgt    5   10.944 ±  0.374  ns/op
 * HashBenchmark.doIntMist64       10  avgt    5   15.388 ±  0.192  ns/op
 * HashBenchmark.doIntMist64       20  avgt    5   21.810 ±  0.460  ns/op
 * HashBenchmark.doIntMist64       40  avgt    5   34.516 ±  0.945  ns/op
 * HashBenchmark.doIntMist64       80  avgt    5   58.437 ±  0.586  ns/op
 * HashBenchmark.doIntMist64      160  avgt    5  109.873 ±  1.362  ns/op
 * HashBenchmark.doIntWater64       5  avgt    5   10.421 ±  0.344  ns/op
 * HashBenchmark.doIntWater64      10  avgt    5   12.005 ±  0.296  ns/op
 * HashBenchmark.doIntWater64      20  avgt    5   17.640 ±  0.652  ns/op
 * HashBenchmark.doIntWater64      40  avgt    5   27.280 ±  0.205  ns/op
 * HashBenchmark.doIntWater64      80  avgt    5   50.075 ±  1.305  ns/op
 * HashBenchmark.doIntWater64     160  avgt    5   99.524 ±  1.029  ns/op
 * HashBenchmark.doIntWisp64        5  avgt    5    7.843 ±  0.052  ns/op
 * HashBenchmark.doIntWisp64       10  avgt    5   11.702 ±  0.194  ns/op
 * HashBenchmark.doIntWisp64       20  avgt    5   16.439 ±  0.071  ns/op
 * HashBenchmark.doIntWisp64       40  avgt    5   24.404 ±  0.395  ns/op
 * HashBenchmark.doIntWisp64       80  avgt    5   39.105 ±  1.247  ns/op
 * HashBenchmark.doIntWisp64      160  avgt    5   68.759 ±  1.622  ns/op
 * HashBenchmark.doIntYolk64        5  avgt    5   10.157 ±  0.212  ns/op
 * HashBenchmark.doIntYolk64       10  avgt    5   11.658 ±  0.289  ns/op
 * HashBenchmark.doIntYolk64       20  avgt    5   17.697 ±  0.158  ns/op
 * HashBenchmark.doIntYolk64       40  avgt    5   27.905 ±  0.574  ns/op
 * HashBenchmark.doIntYolk64       80  avgt    5   47.435 ±  1.533  ns/op
 * HashBenchmark.doIntYolk64      160  avgt    5   91.696 ±  1.629  ns/op
 * </pre>
 * <br>
 * With a Trim-inspired version of Crease, it does much better on speed
 * for large inputs:
 * <pre>
 * Benchmark                    (len)  Mode  Cnt   Score    Error  Units
 * HashBenchmark.doIntCrease64      5  avgt    5  11.995 ±  0.397  ns/op
 * HashBenchmark.doIntCrease64     10  avgt    5  14.091 ±  0.233  ns/op
 * HashBenchmark.doIntCrease64     20  avgt    5  17.320 ±  0.374  ns/op
 * HashBenchmark.doIntCrease64     40  avgt    5  24.602 ±  0.381  ns/op
 * HashBenchmark.doIntCrease64     80  avgt    5  40.198 ±  0.513  ns/op
 * HashBenchmark.doIntCrease64    160  avgt    5  74.267 ± 24.697  ns/op
 * HashBenchmark.doIntCurlup64      5  avgt    5   9.432 ±  0.249  ns/op
 * HashBenchmark.doIntCurlup64     10  avgt    5  11.352 ±  1.061  ns/op
 * HashBenchmark.doIntCurlup64     20  avgt    5  16.538 ±  0.558  ns/op
 * HashBenchmark.doIntCurlup64     40  avgt    5  24.143 ±  0.652  ns/op
 * HashBenchmark.doIntCurlup64     80  avgt    5  41.796 ±  0.939  ns/op
 * HashBenchmark.doIntCurlup64    160  avgt    5  74.678 ±  2.471  ns/op
 * HashBenchmark.doIntWisp64        5  avgt    5   7.783 ±  0.295  ns/op
 * HashBenchmark.doIntWisp64       10  avgt    5  11.058 ±  0.615  ns/op
 * HashBenchmark.doIntWisp64       20  avgt    5  16.208 ±  0.579  ns/op
 * HashBenchmark.doIntWisp64       40  avgt    5  24.422 ±  0.209  ns/op
 * HashBenchmark.doIntWisp64       80  avgt    5  39.137 ±  0.327  ns/op
 * HashBenchmark.doIntWisp64      160  avgt    5  68.757 ±  0.918  ns/op
 * HashBenchmark.doIntYolk64        5  avgt    5   9.929 ±  0.136  ns/op
 * HashBenchmark.doIntYolk64       10  avgt    5  11.982 ±  1.230  ns/op
 * HashBenchmark.doIntYolk64       20  avgt    5  17.072 ±  0.257  ns/op
 * HashBenchmark.doIntYolk64       40  avgt    5  28.090 ±  0.240  ns/op
 * HashBenchmark.doIntYolk64       80  avgt    5  46.720 ±  0.642  ns/op
 * HashBenchmark.doIntYolk64      160  avgt    5  91.580 ±  1.723  ns/op
 * </pre>
 * <br>
 * Using the same Crease as above (Trim-based) does well for large inputs
 * with 32-bit output:
 * <pre>
 * Benchmark                      (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doIntCrease32        5  avgt    5   12.402 ±  0.383  ns/op
 * HashBenchmark.doIntCrease32       10  avgt    5   14.435 ±  0.229  ns/op
 * HashBenchmark.doIntCrease32       20  avgt    5   17.758 ±  0.163  ns/op
 * HashBenchmark.doIntCrease32       40  avgt    5   25.079 ±  0.527  ns/op
 * HashBenchmark.doIntCrease32       80  avgt    5   40.912 ±  0.301  ns/op
 * HashBenchmark.doIntCrease32      160  avgt    5   74.710 ± 10.448  ns/op
 * HashBenchmark.doIntCurlup32        5  avgt    5    9.161 ±  0.362  ns/op
 * HashBenchmark.doIntCurlup32       10  avgt    5   11.440 ±  0.316  ns/op
 * HashBenchmark.doIntCurlup32       20  avgt    5   17.334 ±  0.569  ns/op
 * HashBenchmark.doIntCurlup32       40  avgt    5   24.875 ±  0.454  ns/op
 * HashBenchmark.doIntCurlup32       80  avgt    5   44.724 ±  1.112  ns/op
 * HashBenchmark.doIntCurlup32      160  avgt    5   86.016 ±  2.150  ns/op
 * HashBenchmark.doIntJDK32           5  avgt    5    6.695 ±  0.114  ns/op
 * HashBenchmark.doIntJDK32          10  avgt    5    9.321 ±  0.168  ns/op
 * HashBenchmark.doIntJDK32          20  avgt    5   14.589 ±  0.526  ns/op
 * HashBenchmark.doIntJDK32          40  avgt    5   29.128 ±  0.314  ns/op
 * HashBenchmark.doIntJDK32          80  avgt    5   58.201 ±  0.852  ns/op
 * HashBenchmark.doIntJDK32         160  avgt    5  117.358 ±  2.208  ns/op
 * HashBenchmark.doIntJDK32Mixed      5  avgt    5    7.176 ±  0.112  ns/op
 * HashBenchmark.doIntJDK32Mixed     10  avgt    5   10.102 ±  0.123  ns/op
 * HashBenchmark.doIntJDK32Mixed     20  avgt    5   15.283 ±  0.229  ns/op
 * HashBenchmark.doIntJDK32Mixed     40  avgt    5   29.323 ±  0.265  ns/op
 * HashBenchmark.doIntJDK32Mixed     80  avgt    5   59.234 ±  0.845  ns/op
 * HashBenchmark.doIntJDK32Mixed    160  avgt    5  117.147 ±  1.460  ns/op
 * HashBenchmark.doIntWisp32          5  avgt    5    7.822 ±  0.110  ns/op
 * HashBenchmark.doIntWisp32         10  avgt    5   11.591 ±  0.116  ns/op
 * HashBenchmark.doIntWisp32         20  avgt    5   15.445 ±  0.124  ns/op
 * HashBenchmark.doIntWisp32         40  avgt    5   24.411 ±  0.745  ns/op
 * HashBenchmark.doIntWisp32         80  avgt    5   38.421 ±  0.285  ns/op
 * HashBenchmark.doIntWisp32        160  avgt    5   68.980 ±  0.491  ns/op
 * HashBenchmark.doIntYolk32          5  avgt    5   10.173 ±  0.102  ns/op
 * HashBenchmark.doIntYolk32         10  avgt    5   11.809 ±  0.213  ns/op
 * HashBenchmark.doIntYolk32         20  avgt    5   16.848 ±  0.190  ns/op
 * HashBenchmark.doIntYolk32         40  avgt    5   26.170 ±  0.656  ns/op
 * HashBenchmark.doIntYolk32         80  avgt    5   46.361 ±  1.255  ns/op
 * HashBenchmark.doIntYolk32        160  avgt    5   91.341 ±  1.210  ns/op
 * </pre>
 * <br>
 * <pre>
 * Benchmark                     (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doCharMx32         50  avgt    5   95.498 ±  7.367  ns/op
 * HashBenchmark.doCharMx64         50  avgt    5   94.898 ±  7.613  ns/op
 * HashBenchmark.doCharYolk32       50  avgt    5   53.361 ±  4.232  ns/op
 * HashBenchmark.doCharYolk64       50  avgt    5   54.592 ±  3.615  ns/op
 * HashBenchmark.doDoubleMx32       50  avgt    5   84.159 ±  9.978  ns/op
 * HashBenchmark.doDoubleMx64       50  avgt    5   81.552 ±  4.842  ns/op
 * HashBenchmark.doDoubleYolk32     50  avgt    5   70.996 ±  6.078  ns/op
 * HashBenchmark.doDoubleYolk64     50  avgt    5   71.380 ±  5.775  ns/op
 * HashBenchmark.doIntMx32          50  avgt    5   54.722 ±  2.088  ns/op
 * HashBenchmark.doIntMx64          50  avgt    5   56.583 ±  4.273  ns/op
 * HashBenchmark.doIntYolk32        50  avgt    5   32.303 ±  1.621  ns/op
 * HashBenchmark.doIntYolk64        50  avgt    5   32.116 ±  2.947  ns/op
 * HashBenchmark.doLongMx32         50  avgt    5   56.161 ±  1.969  ns/op
 * HashBenchmark.doLongMx64         50  avgt    5   57.265 ±  4.169  ns/op
 * HashBenchmark.doLongYolk32       50  avgt    5   43.983 ±  2.911  ns/op
 * HashBenchmark.doLongYolk64       50  avgt    5   44.094 ±  2.524  ns/op
 * HashBenchmark.doMx32             50  avgt    5  111.704 ± 11.757  ns/op
 * HashBenchmark.doMx64             50  avgt    5  109.397 ±  8.175  ns/op
 * HashBenchmark.doYolk32           50  avgt    5   59.630 ±  5.269  ns/op
 * HashBenchmark.doYolk64           50  avgt    5   62.821 ±  3.784  ns/op
 * </pre>
 * Mx (which is Maiga's MX3 hash) does not seem to do well in Java at all...
 * <br>
 * Notably, all hashes here outperform the JDK version (Arrays.hashCode())
 * when inputs are 80 items or larger.
 * <br>
 * Everything:
 * <pre>
 * Benchmark                         (len)  Mode  Cnt    Score     Error  Units
 * HashBenchmark.doCharCurlup32        500  avgt    5   53.569 ±   2.441  ns/op
 * HashBenchmark.doCharCurlup64        500  avgt    5   51.410 ±   0.805  ns/op
 * HashBenchmark.doCharHive32          500  avgt    5   93.369 ±   1.003  ns/op
 * HashBenchmark.doCharHive64          500  avgt    5   64.140 ±   0.931  ns/op
 * HashBenchmark.doCharJDK32           500  avgt    5   63.810 ±   2.613  ns/op
 * HashBenchmark.doCharJDK32Mixed      500  avgt    5   63.410 ±   1.574  ns/op
 * HashBenchmark.doCharMist32          500  avgt    5   62.919 ±   4.392  ns/op
 * HashBenchmark.doCharMist64          500  avgt    5   65.306 ±   7.147  ns/op
 * HashBenchmark.doCharMx32            500  avgt    5   87.051 ±   6.178  ns/op
 * HashBenchmark.doCharMx64            500  avgt    5   88.603 ±   5.285  ns/op
 * HashBenchmark.doCharPurple32        500  avgt    5   71.863 ±   2.963  ns/op
 * HashBenchmark.doCharPurple64        500  avgt    5   70.219 ±   1.500  ns/op
 * HashBenchmark.doCharWater32         500  avgt    5   49.144 ±   5.848  ns/op
 * HashBenchmark.doCharWater64         500  avgt    5   52.068 ±   4.070  ns/op
 * HashBenchmark.doCharWisp32          500  avgt    5   46.047 ±   3.112  ns/op
 * HashBenchmark.doCharWisp64          500  avgt    5   45.212 ±   6.072  ns/op
 * HashBenchmark.doCharYolk32          500  avgt    5   49.803 ±   2.762  ns/op
 * HashBenchmark.doCharYolk64          500  avgt    5   50.615 ±   4.725  ns/op
 * HashBenchmark.doCurlup32            500  avgt    5   92.865 ±   7.526  ns/op
 * HashBenchmark.doCurlup64            500  avgt    5  104.441 ±   7.848  ns/op
 * HashBenchmark.doDoubleCurlup32      500  avgt    5  830.444 ±  51.212  ns/op
 * HashBenchmark.doDoubleCurlup64      500  avgt    5  817.250 ±  60.470  ns/op
 * HashBenchmark.doDoubleHive32        500  avgt    5  673.765 ±  42.413  ns/op
 * HashBenchmark.doDoubleHive64        500  avgt    5  689.056 ±  58.073  ns/op
 * HashBenchmark.doDoubleJDK32         500  avgt    5  732.442 ±  22.580  ns/op
 * HashBenchmark.doDoubleJDK32Mixed    500  avgt    5  740.965 ±  68.918  ns/op
 * HashBenchmark.doDoubleMist32        500  avgt    5  850.835 ±  45.014  ns/op
 * HashBenchmark.doDoubleMist64        500  avgt    5  886.087 ±  59.515  ns/op
 * HashBenchmark.doDoubleMx32          500  avgt    5  778.458 ±  79.874  ns/op
 * HashBenchmark.doDoubleMx64          500  avgt    5  796.256 ±  50.307  ns/op
 * HashBenchmark.doDoublePurple32      500  avgt    5  661.129 ±  70.118  ns/op
 * HashBenchmark.doDoublePurple64      500  avgt    5  640.118 ±  22.399  ns/op
 * HashBenchmark.doDoubleWater32       500  avgt    5  848.566 ±  77.674  ns/op
 * HashBenchmark.doDoubleWater64       500  avgt    5  747.092 ±  38.413  ns/op
 * HashBenchmark.doDoubleWisp32        500  avgt    5  721.903 ±  50.461  ns/op
 * HashBenchmark.doDoubleWisp64        500  avgt    5  756.245 ±  33.715  ns/op
 * HashBenchmark.doDoubleYolk32        500  avgt    5  766.837 ±  59.003  ns/op
 * HashBenchmark.doDoubleYolk64        500  avgt    5  744.790 ±  19.786  ns/op
 * HashBenchmark.doHive32              500  avgt    5   95.729 ±   4.527  ns/op
 * HashBenchmark.doHive64              500  avgt    5   68.553 ±   1.413  ns/op
 * HashBenchmark.doIntCurlup32         500  avgt    5  283.246 ±  17.758  ns/op
 * HashBenchmark.doIntCurlup64         500  avgt    5  260.325 ±  35.476  ns/op
 * HashBenchmark.doIntHive32           500  avgt    5  567.789 ±  10.466  ns/op
 * HashBenchmark.doIntHive64           500  avgt    5  352.225 ±   7.265  ns/op
 * HashBenchmark.doIntJDK32            500  avgt    5  374.801 ±  18.898  ns/op
 * HashBenchmark.doIntJDK32Mixed       500  avgt    5  381.772 ±   7.161  ns/op
 * HashBenchmark.doIntMist32           500  avgt    5  350.934 ±  29.626  ns/op
 * HashBenchmark.doIntMist64           500  avgt    5  347.589 ±  42.551  ns/op
 * HashBenchmark.doIntMx32             500  avgt    5  527.497 ±  31.719  ns/op
 * HashBenchmark.doIntMx64             500  avgt    5  524.242 ±   8.713  ns/op
 * HashBenchmark.doIntPurple32         500  avgt    5  449.365 ±  13.748  ns/op
 * HashBenchmark.doIntPurple64         500  avgt    5  516.276 ± 150.713  ns/op
 * HashBenchmark.doIntWater32          500  avgt    5  276.873 ±  19.073  ns/op
 * HashBenchmark.doIntWater64          500  avgt    5  277.801 ±  19.443  ns/op
 * HashBenchmark.doIntWisp32           500  avgt    5  209.391 ±   9.146  ns/op
 * HashBenchmark.doIntWisp64           500  avgt    5  207.087 ±  17.810  ns/op
 * HashBenchmark.doIntYolk32           500  avgt    5  316.624 ±  14.420  ns/op
 * HashBenchmark.doIntYolk64           500  avgt    5  276.916 ±  26.692  ns/op
 * HashBenchmark.doJDK32               500  avgt    5   64.711 ±   1.109  ns/op
 * HashBenchmark.doJDK32Mixed          500  avgt    5   66.012 ±   3.059  ns/op
 * HashBenchmark.doLongCurlup32        500  avgt    5  452.401 ±   8.351  ns/op
 * HashBenchmark.doLongCurlup64        500  avgt    5  446.142 ±  10.350  ns/op
 * HashBenchmark.doLongHive32          500  avgt    5  414.171 ±  10.874  ns/op
 * HashBenchmark.doLongHive64          500  avgt    5  413.451 ±   9.580  ns/op
 * HashBenchmark.doLongJDK32           500  avgt    5  523.832 ±  13.048  ns/op
 * HashBenchmark.doLongJDK32Mixed      500  avgt    5  501.879 ±  10.190  ns/op
 * HashBenchmark.doLongMist32          500  avgt    5  441.755 ±   4.699  ns/op
 * HashBenchmark.doLongMist64          500  avgt    5  442.286 ±  80.363  ns/op
 * HashBenchmark.doLongMx32            500  avgt    5  583.084 ±  13.290  ns/op
 * HashBenchmark.doLongMx64            500  avgt    5  589.285 ±  34.104  ns/op
 * HashBenchmark.doLongPurple32        500  avgt    5  517.487 ±   8.826  ns/op
 * HashBenchmark.doLongPurple64        500  avgt    5  510.932 ±  17.692  ns/op
 * HashBenchmark.doLongWater32         500  avgt    5  456.439 ±  27.478  ns/op
 * HashBenchmark.doLongWater64         500  avgt    5  398.698 ±  16.502  ns/op
 * HashBenchmark.doLongWisp32          500  avgt    5  364.526 ±  12.196  ns/op
 * HashBenchmark.doLongWisp64          500  avgt    5  372.354 ±  25.042  ns/op
 * HashBenchmark.doLongYolk32          500  avgt    5  483.083 ±  74.655  ns/op
 * HashBenchmark.doLongYolk64          500  avgt    5  505.018 ±  18.513  ns/op
 * HashBenchmark.doMist32              500  avgt    5   73.085 ±   3.755  ns/op
 * HashBenchmark.doMist64              500  avgt    5   72.576 ±   5.851  ns/op
 * HashBenchmark.doMx32                500  avgt    5  108.612 ±  11.653  ns/op
 * HashBenchmark.doMx64                500  avgt    5  105.063 ±   8.336  ns/op
 * HashBenchmark.doPurple32            500  avgt    5   75.293 ±   2.018  ns/op
 * HashBenchmark.doPurple64            500  avgt    5   74.368 ±   2.818  ns/op
 * HashBenchmark.doWater32             500  avgt    5   58.803 ±   6.988  ns/op
 * HashBenchmark.doWater64             500  avgt    5   55.530 ±   2.478  ns/op
 * HashBenchmark.doWisp32              500  avgt    5   49.771 ±   4.750  ns/op
 * HashBenchmark.doWisp64              500  avgt    5   46.358 ±   5.182  ns/op
 * HashBenchmark.doYolk32              500  avgt    5   58.854 ±   1.512  ns/op
 * HashBenchmark.doYolk64              500  avgt    5   59.665 ±   6.241  ns/op
 * </pre>
 * <br>
 * There is no beating a precomputed hashCode() on speed.
 * <pre>
 * Benchmark                         (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doStringsJDK32         32  avgt    5   36.194 ±  3.742  ns/op
 * HashBenchmark.doStringsLevartA32     32  avgt    5  702.059 ± 22.347  ns/op
 * HashBenchmark.doStringsLevartB32     32  avgt    5  697.498 ± 28.563  ns/op
 * HashBenchmark.doStringsYolk32        32  avgt    5  970.109 ± 74.922  ns/op
 * </pre>
 * <br>
 * Weird results comparing Yolk (based on wyhash), Tern (operating on four numbers at a time, regardless of bits), and
 * Terra (operating on 256 bits at a time whenever possible):
 * <pre>
 * Benchmark                      (len)  Mode  Cnt     Score    Error  Units
 * HashBenchmark.doCharTern32       256  avgt    5    71.889 ±  4.003  ns/op
 * HashBenchmark.doCharTern64       256  avgt    5    65.695 ±  1.258  ns/op
 * HashBenchmark.doCharTerra32      256  avgt    5    52.228 ±  3.288  ns/op
 * HashBenchmark.doCharTerra64      256  avgt    5    52.197 ±  2.984  ns/op
 * HashBenchmark.doCharYolk32       256  avgt    5    61.915 ± 24.721  ns/op
 * HashBenchmark.doCharYolk64       256  avgt    5    55.960 ±  2.815  ns/op
 * HashBenchmark.doDoubleTern32     256  avgt    5   323.236 ± 26.403  ns/op
 * HashBenchmark.doDoubleTern64     256  avgt    5   310.274 ±  5.884  ns/op
 * HashBenchmark.doDoubleTerra32    256  avgt    5   330.666 ± 55.986  ns/op
 * HashBenchmark.doDoubleTerra64    256  avgt    5   344.461 ± 45.305  ns/op
 * HashBenchmark.doDoubleYolk32     256  avgt    5   346.685 ± 13.308  ns/op
 * HashBenchmark.doDoubleYolk64     256  avgt    5   346.187 ±  7.256  ns/op
 * HashBenchmark.doIntTern32        256  avgt    5   136.491 ±  5.374  ns/op
 * HashBenchmark.doIntTern64        256  avgt    5   133.608 ±  4.923  ns/op
 * HashBenchmark.doIntTerra32       256  avgt    5   110.698 ±  4.236  ns/op
 * HashBenchmark.doIntTerra64       256  avgt    5   109.301 ±  3.413  ns/op
 * HashBenchmark.doIntYolk32        256  avgt    5   146.865 ±  1.455  ns/op
 * HashBenchmark.doIntYolk64        256  avgt    5   132.945 ±  0.904  ns/op
 * HashBenchmark.doLongTern32       256  avgt    5   165.666 ±  3.096  ns/op
 * HashBenchmark.doLongTern64       256  avgt    5   154.530 ±  1.420  ns/op
 * HashBenchmark.doLongTerra32      256  avgt    5   154.267 ±  1.579  ns/op
 * HashBenchmark.doLongTerra64      256  avgt    5   165.239 ±  6.178  ns/op
 * HashBenchmark.doLongYolk32       256  avgt    5   223.519 ±  2.258  ns/op
 * HashBenchmark.doLongYolk64       256  avgt    5   185.184 ±  3.170  ns/op
 * HashBenchmark.doStringsYolk32    256  avgt    5  3114.771 ± 91.328  ns/op
 * HashBenchmark.doTern32           256  avgt    5    78.926 ±  2.399  ns/op
 * HashBenchmark.doTern64           256  avgt    5    80.511 ±  1.120  ns/op
 * HashBenchmark.doTerra32          256  avgt    5   100.669 ±  3.508  ns/op
 * HashBenchmark.doTerra64          256  avgt    5   101.690 ±  1.668  ns/op
 * HashBenchmark.doYolk32           256  avgt    5    62.452 ±  0.931  ns/op
 * HashBenchmark.doYolk64           256  avgt    5    60.434 ±  0.520  ns/op
 * </pre>
 * <br>
 * Just a quick check for the latest change, using long casts instead of masks with longs where possible.
 * Terra is looking quite a bit better; it's just still not very fast at hashing CharSequence s. This is probably
 * because the CharSequence s are words or sentences with random lengths, as opposed to length-256 arrays...
 * <pre>
 * Benchmark                      (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doCharTerra64      256  avgt    5   53.870 ±  1.229  ns/op
 * HashBenchmark.doCharYolk64       256  avgt    5   51.747 ±  0.730  ns/op
 * HashBenchmark.doDoubleTerra64    256  avgt    5  246.348 ±  3.501  ns/op
 * HashBenchmark.doDoubleYolk64     256  avgt    5  322.763 ±  2.934  ns/op
 * HashBenchmark.doIntTerra64       256  avgt    5  120.792 ± 38.499  ns/op
 * HashBenchmark.doIntYolk64        256  avgt    5  153.787 ±  5.374  ns/op
 * HashBenchmark.doLongTerra64      256  avgt    5  178.570 ± 21.178  ns/op
 * HashBenchmark.doLongYolk64       256  avgt    5  223.294 ± 27.653  ns/op
 * HashBenchmark.doTerra64          256  avgt    5   64.109 ±  2.717  ns/op
 * HashBenchmark.doYolk64           256  avgt    5   55.007 ±  1.011  ns/op
 * </pre>
 * <br>
 * An even quicker check to make sure the benchmarks for CharSequence s operate on a little over 256 chars at a time.
 * This seems to be shorter than they were before, which is somewhat of a surprise.
 * <pre>
 * Benchmark                (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doTern64     256  avgt    5  27.111 ± 0.517  ns/op
 * HashBenchmark.doTerra64    256  avgt    5  36.971 ± 1.109  ns/op
 * HashBenchmark.doYolk64     256  avgt    5  29.444 ± 0.277  ns/op
 * </pre>
 * It looks like Hound isn't very good, much of the time. It also looks like Terra performs way better when the length
 * is always a multiple of 16, if it's hashing chars, but doCharTerra64 and doTerra64 only have that happen 1/16 of the
 * time... Hm.
 * <pre>
 * Benchmark                      (len)  Mode  Cnt    Score     Error  Units
 * HashBenchmark.doCharHound64      256  avgt    5   25.201 ±   0.890  ns/op
 * HashBenchmark.doCharTern64       256  avgt    5   32.385 ±   0.394  ns/op
 * HashBenchmark.doCharTerra64      256  avgt    5   32.833 ±   1.957  ns/op
 * HashBenchmark.doCharYolk64       256  avgt    5   26.628 ±   3.276  ns/op
 * HashBenchmark.doDoubleHound64    256  avgt    5  470.156 ±  54.139  ns/op
 * HashBenchmark.doDoubleTern64     256  avgt    5  343.367 ±  31.062  ns/op
 * HashBenchmark.doDoubleTerra64    256  avgt    5  350.679 ±  22.595  ns/op
 * HashBenchmark.doDoubleYolk64     256  avgt    5  469.640 ± 101.738  ns/op
 * HashBenchmark.doHound64          256  avgt    5   31.838 ±   1.493  ns/op
 * HashBenchmark.doIntHound64       256  avgt    5  205.368 ±   9.026  ns/op
 * HashBenchmark.doIntTern64        256  avgt    5  209.487 ±   4.583  ns/op
 * HashBenchmark.doIntTerra64       256  avgt    5  148.991 ±  18.546  ns/op
 * HashBenchmark.doIntYolk64        256  avgt    5  184.977 ±   4.125  ns/op
 * HashBenchmark.doLongHound64      256  avgt    5  307.076 ±  11.117  ns/op
 * HashBenchmark.doLongTern64       256  avgt    5  271.079 ±  29.057  ns/op
 * HashBenchmark.doLongTerra64      256  avgt    5  224.664 ±   4.318  ns/op
 * HashBenchmark.doLongYolk64       256  avgt    5  245.319 ±   7.200  ns/op
 * HashBenchmark.doTern64           256  avgt    5   35.794 ±   0.847  ns/op
 * HashBenchmark.doTerra64          256  avgt    5   36.947 ±   0.473  ns/op
 * HashBenchmark.doYolk64           256  avgt    5   21.718 ±   0.204  ns/op
 * </pre>
 * <br>
 * Checking Tempo vs. Terra...
 * <pre>
 * Benchmark                      (len)  Mode  Cnt    Score     Error  Units
 * HashBenchmark.doCharTempo64      256  avgt    5   30.051 ±   3.816  ns/op
 * HashBenchmark.doCharTern64       256  avgt    5   31.810 ±   1.727  ns/op
 * HashBenchmark.doCharTerra64      256  avgt    5   32.457 ±   1.665  ns/op
 * HashBenchmark.doCharYolk64       256  avgt    5   25.114 ±   0.278  ns/op
 * HashBenchmark.doDoubleTempo64    256  avgt    5  303.735 ±  38.026  ns/op
 * HashBenchmark.doDoubleTern64     256  avgt    5  271.593 ±  98.179  ns/op
 * HashBenchmark.doDoubleTerra64    256  avgt    5  254.267 ±   9.608  ns/op
 * HashBenchmark.doDoubleYolk64     256  avgt    5  347.034 ±   6.320  ns/op
 * HashBenchmark.doIntTempo64       256  avgt    5  127.269 ±  24.712  ns/op
 * HashBenchmark.doIntTern64        256  avgt    5  158.629 ±   5.060  ns/op
 * HashBenchmark.doIntTerra64       256  avgt    5  126.739 ±  10.546  ns/op
 * HashBenchmark.doIntYolk64        256  avgt    5  161.366 ±  20.127  ns/op
 * HashBenchmark.doLongTempo64      256  avgt    5  239.744 ±  83.437  ns/op
 * HashBenchmark.doLongTern64       256  avgt    5  258.770 ±  91.291  ns/op
 * HashBenchmark.doLongTerra64      256  avgt    5  201.565 ±  83.148  ns/op
 * HashBenchmark.doLongYolk64       256  avgt    5  254.663 ± 100.583  ns/op
 * HashBenchmark.doTempo64          256  avgt    5   37.085 ±   9.953  ns/op
 * HashBenchmark.doTern64           256  avgt    5   35.896 ±   3.684  ns/op
 * HashBenchmark.doTerra64          256  avgt    5   37.653 ±   3.254  ns/op
 * HashBenchmark.doYolk64           256  avgt    5   22.512 ±   2.108  ns/op
 * </pre>
 * Just a quick try of Mx again after some changes... no, still slower.
 * <pre>
 * Benchmark                   (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doIntMx32        80  avgt   10  73.038 ± 0.189  ns/op
 * HashBenchmark.doIntMx64        80  avgt   10  72.835 ± 0.165  ns/op
 * HashBenchmark.doIntTerra32     80  avgt   10  42.820 ± 0.134  ns/op
 * HashBenchmark.doIntTerra64     80  avgt   10  41.410 ± 0.516  ns/op
 * HashBenchmark.doIntWater32     80  avgt   10  42.589 ± 0.109  ns/op
 * HashBenchmark.doIntWater64     80  avgt   10  42.704 ± 0.153  ns/op
 * </pre>
 * Maybe it's worth looking into Terra again... It seems better on long keys.
 * <pre>
 * Benchmark                   (len)  Mode  Cnt    Score    Error  Units
 * HashBenchmark.doIntHound32     32  avgt   10   25.111 ±  0.766  ns/op
 * HashBenchmark.doIntHound32     64  avgt   10   41.071 ±  0.133  ns/op
 * HashBenchmark.doIntHound32    128  avgt   10   67.818 ±  0.185  ns/op
 * HashBenchmark.doIntHound32    256  avgt   10  112.350 ±  0.854  ns/op
 * HashBenchmark.doIntHound32    512  avgt   10  231.044 ±  1.665  ns/op
 * HashBenchmark.doIntHound32   1024  avgt   10  490.584 ±  4.056  ns/op
 * HashBenchmark.doIntHound64     32  avgt   10   25.184 ±  0.355  ns/op
 * HashBenchmark.doIntHound64     64  avgt   10   40.881 ±  0.459  ns/op
 * HashBenchmark.doIntHound64    128  avgt   10   68.274 ±  0.451  ns/op
 * HashBenchmark.doIntHound64    256  avgt   10  112.963 ±  2.153  ns/op
 * HashBenchmark.doIntHound64    512  avgt   10  227.676 ±  2.831  ns/op
 * HashBenchmark.doIntHound64   1024  avgt   10  477.152 ±  2.289  ns/op
 * HashBenchmark.doIntTempo32     32  avgt   10   20.792 ±  0.111  ns/op
 * HashBenchmark.doIntTempo32     64  avgt   10   36.799 ±  0.191  ns/op
 * HashBenchmark.doIntTempo32    128  avgt   10   58.562 ±  0.434  ns/op
 * HashBenchmark.doIntTempo32    256  avgt   10   92.132 ±  0.896  ns/op
 * HashBenchmark.doIntTempo32    512  avgt   10  170.060 ±  0.928  ns/op
 * HashBenchmark.doIntTempo32   1024  avgt   10  382.986 ±  9.601  ns/op
 * HashBenchmark.doIntTempo64     32  avgt   10   23.699 ±  0.552  ns/op
 * HashBenchmark.doIntTempo64     64  avgt   10   37.571 ±  0.275  ns/op
 * HashBenchmark.doIntTempo64    128  avgt   10   58.156 ±  0.361  ns/op
 * HashBenchmark.doIntTempo64    256  avgt   10   91.493 ±  0.444  ns/op
 * HashBenchmark.doIntTempo64    512  avgt   10  189.434 ±  1.901  ns/op
 * HashBenchmark.doIntTempo64   1024  avgt   10  378.290 ±  3.119  ns/op
 * HashBenchmark.doIntTerra32     32  avgt   10   22.498 ±  0.203  ns/op
 * HashBenchmark.doIntTerra32     64  avgt   10   34.702 ±  0.254  ns/op
 * HashBenchmark.doIntTerra32    128  avgt   10   58.120 ±  0.468  ns/op
 * HashBenchmark.doIntTerra32    256  avgt   10   92.664 ±  3.274  ns/op
 * HashBenchmark.doIntTerra32    512  avgt   10  186.205 ±  3.118  ns/op
 * HashBenchmark.doIntTerra32   1024  avgt   10  385.005 ±  2.259  ns/op
 * HashBenchmark.doIntTerra64     32  avgt   10   22.600 ±  0.214  ns/op
 * HashBenchmark.doIntTerra64     64  avgt   10   37.319 ±  0.217  ns/op
 * HashBenchmark.doIntTerra64    128  avgt   10   58.858 ±  0.334  ns/op
 * HashBenchmark.doIntTerra64    256  avgt   10   94.923 ±  0.850  ns/op
 * HashBenchmark.doIntTerra64    512  avgt   10  176.121 ±  1.610  ns/op
 * HashBenchmark.doIntTerra64   1024  avgt   10  378.560 ±  3.853  ns/op
 * HashBenchmark.doIntWater32     32  avgt   10   21.738 ±  0.806  ns/op
 * HashBenchmark.doIntWater32     64  avgt   10   36.081 ±  0.174  ns/op
 * HashBenchmark.doIntWater32    128  avgt   10   60.032 ±  0.475  ns/op
 * HashBenchmark.doIntWater32    256  avgt   10   98.978 ±  1.852  ns/op
 * HashBenchmark.doIntWater32    512  avgt   10  195.297 ±  1.079  ns/op
 * HashBenchmark.doIntWater32   1024  avgt   10  404.474 ±  5.538  ns/op
 * HashBenchmark.doIntWater64     32  avgt   10   22.040 ±  0.649  ns/op
 * HashBenchmark.doIntWater64     64  avgt   10   36.600 ±  0.251  ns/op
 * HashBenchmark.doIntWater64    128  avgt   10   60.831 ±  0.447  ns/op
 * HashBenchmark.doIntWater64    256  avgt   10   96.613 ±  2.308  ns/op
 * HashBenchmark.doIntWater64    512  avgt   10  189.429 ±  3.261  ns/op
 * HashBenchmark.doIntWater64   1024  avgt   10  415.133 ±  3.264  ns/op
 * HashBenchmark.doIntYolk32      32  avgt   10   21.522 ±  0.471  ns/op
 * HashBenchmark.doIntYolk32      64  avgt   10   36.243 ±  0.260  ns/op
 * HashBenchmark.doIntYolk32     128  avgt   10   60.892 ±  0.306  ns/op
 * HashBenchmark.doIntYolk32     256  avgt   10   97.006 ±  0.672  ns/op
 * HashBenchmark.doIntYolk32     512  avgt   10  204.748 ±  1.553  ns/op
 * HashBenchmark.doIntYolk32    1024  avgt   10  408.818 ±  3.247  ns/op
 * HashBenchmark.doIntYolk64      32  avgt   10   22.023 ±  0.446  ns/op
 * HashBenchmark.doIntYolk64      64  avgt   10   35.585 ±  0.105  ns/op
 * HashBenchmark.doIntYolk64     128  avgt   10   60.496 ±  0.418  ns/op
 * HashBenchmark.doIntYolk64     256  avgt   10   98.597 ±  0.635  ns/op
 * HashBenchmark.doIntYolk64     512  avgt   10  188.708 ±  3.535  ns/op
 * HashBenchmark.doIntYolk64    1024  avgt   10  420.261 ± 11.403  ns/op
 * </pre>
 * Testing on a larger size, there's definitely a difference:
 * <pre>
 * Benchmark                   (len)  Mode  Cnt     Score     Error  Units
 * HashBenchmark.doIntTerra32   8192  avgt   10  3719.946 ±  51.681  ns/op
 * HashBenchmark.doIntTerra64   8192  avgt   10  3700.687 ±  60.663  ns/op
 * HashBenchmark.doIntYolk32    8192  avgt   10  3909.681 ± 110.183  ns/op
 * HashBenchmark.doIntYolk64    8192  avgt   10  3962.901 ±  55.994  ns/op
 * </pre>
 * Oddly, Terra is still faster on long arrays...
 * <pre>
 * Benchmark                    (len)  Mode  Cnt     Score     Error  Units
 * HashBenchmark.doLongTerra32   8192  avgt   10  5606.300 ± 140.339  ns/op
 * HashBenchmark.doLongTerra64   8192  avgt   10  5635.817 ± 113.943  ns/op
 * HashBenchmark.doLongYolk32    8192  avgt   10  6093.706 ±  67.242  ns/op
 * HashBenchmark.doLongYolk64    8192  avgt   10  6039.756 ± 120.915  ns/op
 * </pre>
 * ...But much slower on short Strings. Tempo is only a little better.
 * <pre>
 * Benchmark                    (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doCharTempo32   8192  avgt   10   8.915 ± 0.091  ns/op
 * HashBenchmark.doCharTempo64   8192  avgt   10   9.548 ± 0.083  ns/op
 * HashBenchmark.doCharTerra32   8192  avgt   10  10.971 ± 0.055  ns/op
 * HashBenchmark.doCharTerra64   8192  avgt   10  10.430 ± 0.177  ns/op
 * HashBenchmark.doCharYolk32    8192  avgt   10   5.676 ± 0.028  ns/op
 * HashBenchmark.doCharYolk64    8192  avgt   10   5.967 ± 0.142  ns/op
 * </pre>
 * Testing against len=1000 long arrays:
 * <pre>
 * Benchmark                      (len)  Mode  Cnt     Score    Error  Units
 * HashBenchmark.doLongMx64        1000  avgt    5   775.863 ± 19.689  ns/op
 * HashBenchmark.doLongTritium64   1000  avgt    5  1092.887 ± 22.852  ns/op
 * HashBenchmark.doLongYolk64      1000  avgt    5   712.643 ± 11.291  ns/o
 * </pre>
 * OK, finally something that passes tests and is faster: Ax!
 * <pre>
 * Benchmark                      (len)  Mode  Cnt     Score    Error  Units
 * HashBenchmark.doLongAx64        1000  avgt    5   663.773 ± 22.097  ns/op
 * HashBenchmark.doLongMx64        1000  avgt    5   790.054 ± 40.655  ns/op
 * HashBenchmark.doLongTritium64   1000  avgt    5  1133.805 ± 36.454  ns/op
 * HashBenchmark.doLongYolk64      1000  avgt    5   708.943 ± 40.960  ns/op
 * </pre>
 * Well, mainly faster on longer input arrays, though it's already 10% faster on length 160.
 * <pre>
 * Benchmark                   (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doLongAx32        5  avgt    5  10.501 ± 0.411  ns/op
 * HashBenchmark.doLongAx32       10  avgt    5  21.054 ± 0.745  ns/op
 * HashBenchmark.doLongAx32       20  avgt    5  25.461 ± 0.601  ns/op
 * HashBenchmark.doLongAx32       40  avgt    5  35.803 ± 0.748  ns/op
 * HashBenchmark.doLongAx32       80  avgt    5  54.373 ± 1.907  ns/op
 * HashBenchmark.doLongAx32      160  avgt    5  89.720 ± 1.960  ns/op
 * HashBenchmark.doLongAx64        5  avgt    5  10.436 ± 0.136  ns/op
 * HashBenchmark.doLongAx64       10  avgt    5  20.711 ± 0.506  ns/op
 * HashBenchmark.doLongAx64       20  avgt    5  24.615 ± 0.579  ns/op
 * HashBenchmark.doLongAx64       40  avgt    5  34.799 ± 0.832  ns/op
 * HashBenchmark.doLongAx64       80  avgt    5  54.359 ± 3.131  ns/op
 * HashBenchmark.doLongAx64      160  avgt    5  88.908 ± 7.005  ns/op
 * HashBenchmark.doLongYolk32      5  avgt    5  12.020 ± 0.608  ns/op
 * HashBenchmark.doLongYolk32     10  avgt    5  15.387 ± 0.404  ns/op
 * HashBenchmark.doLongYolk32     20  avgt    5  22.800 ± 0.552  ns/op
 * HashBenchmark.doLongYolk32     40  avgt    5  38.852 ± 0.377  ns/op
 * HashBenchmark.doLongYolk32     80  avgt    5  57.894 ± 0.943  ns/op
 * HashBenchmark.doLongYolk32    160  avgt    5  99.317 ± 2.054  ns/op
 * HashBenchmark.doLongYolk64      5  avgt    5  12.099 ± 0.339  ns/op
 * HashBenchmark.doLongYolk64     10  avgt    5  15.899 ± 0.113  ns/op
 * HashBenchmark.doLongYolk64     20  avgt    5  23.180 ± 0.557  ns/op
 * HashBenchmark.doLongYolk64     40  avgt    5  39.407 ± 0.500  ns/op
 * HashBenchmark.doLongYolk64     80  avgt    5  58.520 ± 0.597  ns/op
 * HashBenchmark.doLongYolk64    160  avgt    5  99.497 ± 0.460  ns/op
 * </pre>
 * And running the above 64-bit tests again on Oracle GraalVM 22.0.1+8.1 :
 * <pre>
 * Benchmark                   (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doLongAx64        5  avgt    5  10.067 ± 0.452  ns/op
 * HashBenchmark.doLongAx64       10  avgt    5  13.333 ± 0.279  ns/op
 * HashBenchmark.doLongAx64       20  avgt    5  19.988 ± 0.599  ns/op
 * HashBenchmark.doLongAx64       40  avgt    5  30.885 ± 1.290  ns/op
 * HashBenchmark.doLongAx64       80  avgt    5  50.938 ± 0.895  ns/op
 * HashBenchmark.doLongAx64      160  avgt    5  86.614 ± 1.428  ns/op
 * HashBenchmark.doLongYolk64      5  avgt    5   8.653 ± 0.098  ns/op
 * HashBenchmark.doLongYolk64     10  avgt    5  12.263 ± 0.105  ns/op
 * HashBenchmark.doLongYolk64     20  avgt    5  19.370 ± 0.767  ns/op
 * HashBenchmark.doLongYolk64     40  avgt    5  32.786 ± 0.475  ns/op
 * HashBenchmark.doLongYolk64     80  avgt    5  56.549 ± 0.889  ns/op
 * HashBenchmark.doLongYolk64    160  avgt    5  99.217 ± 1.437  ns/op
 * </pre>
 * Benchmarking {@code byte[]} vs. {@link ByteBuffer} for the first time, using GraalVM as above:
 * <pre>
 * Benchmark                     (len)  Mode  Cnt   Score    Error  Units
 * HashBenchmark.doBufferAx64        5  avgt    5   7.474 ±  0.184  ns/op
 * HashBenchmark.doBufferAx64       10  avgt    5   8.571 ±  0.164  ns/op
 * HashBenchmark.doBufferAx64       20  avgt    5   7.774 ±  0.117  ns/op
 * HashBenchmark.doBufferAx64       40  avgt    5  13.544 ±  0.254  ns/op
 * HashBenchmark.doBufferAx64       80  avgt    5  16.381 ±  0.733  ns/op
 * HashBenchmark.doBufferAx64      160  avgt    5  25.379 ± 16.586  ns/op
 * HashBenchmark.doBufferYolk64      5  avgt    5   9.726 ±  4.236  ns/op
 * HashBenchmark.doBufferYolk64     10  avgt    5   6.863 ±  0.071  ns/op
 * HashBenchmark.doBufferYolk64     20  avgt    5   7.492 ±  0.068  ns/op
 * HashBenchmark.doBufferYolk64     40  avgt    5  11.459 ±  0.179  ns/op
 * HashBenchmark.doBufferYolk64     80  avgt    5  17.740 ±  0.772  ns/op
 * HashBenchmark.doBufferYolk64    160  avgt    5  22.742 ±  1.800  ns/op
 * HashBenchmark.doByteAx64          5  avgt    5   9.319 ±  0.324  ns/op
 * HashBenchmark.doByteAx64         10  avgt    5  11.449 ±  0.172  ns/op
 * HashBenchmark.doByteAx64         20  avgt    5  17.238 ±  0.063  ns/op
 * HashBenchmark.doByteAx64         40  avgt    5  24.645 ±  1.040  ns/op
 * HashBenchmark.doByteAx64         80  avgt    5  42.028 ±  1.115  ns/op
 * HashBenchmark.doByteAx64        160  avgt    5  62.338 ±  0.830  ns/op
 * HashBenchmark.doByteYolk64        5  avgt    5   6.961 ±  0.478  ns/op
 * HashBenchmark.doByteYolk64       10  avgt    5   9.670 ±  0.105  ns/op
 * HashBenchmark.doByteYolk64       20  avgt    5  15.467 ±  0.684  ns/op
 * HashBenchmark.doByteYolk64       40  avgt    5  21.484 ±  0.652  ns/op
 * HashBenchmark.doByteYolk64       80  avgt    5  35.650 ±  1.175  ns/op
 * HashBenchmark.doByteYolk64      160  avgt    5  65.361 ±  1.467  ns/op
 * </pre>
 * The same benchmark as above, but on HotSpot OpenJDK 22:
 * <pre>
 * Benchmark                     (len)  Mode  Cnt   Score   Error  Units
 * HashBenchmark.doBufferAx64        5  avgt    5  10.643 ± 0.230  ns/op
 * HashBenchmark.doBufferAx64       10  avgt    5  12.183 ± 0.165  ns/op
 * HashBenchmark.doBufferAx64       20  avgt    5  12.282 ± 0.096  ns/op
 * HashBenchmark.doBufferAx64       40  avgt    5  15.480 ± 1.583  ns/op
 * HashBenchmark.doBufferAx64       80  avgt    5  26.074 ± 0.443  ns/op
 * HashBenchmark.doBufferAx64      160  avgt    5  26.925 ± 2.616  ns/op
 * HashBenchmark.doBufferYolk64      5  avgt    5  10.451 ± 0.286  ns/op
 * HashBenchmark.doBufferYolk64     10  avgt    5  10.195 ± 0.109  ns/op
 * HashBenchmark.doBufferYolk64     20  avgt    5  11.117 ± 0.467  ns/op
 * HashBenchmark.doBufferYolk64     40  avgt    5  15.935 ± 0.620  ns/op
 * HashBenchmark.doBufferYolk64     80  avgt    5  23.620 ± 0.206  ns/op
 * HashBenchmark.doBufferYolk64    160  avgt    5  27.723 ± 2.154  ns/op
 * HashBenchmark.doByteAx64          5  avgt    5   9.745 ± 0.233  ns/op
 * HashBenchmark.doByteAx64         10  avgt    5  18.048 ± 0.206  ns/op
 * HashBenchmark.doByteAx64         20  avgt    5  19.394 ± 0.111  ns/op
 * HashBenchmark.doByteAx64         40  avgt    5  25.330 ± 0.567  ns/op
 * HashBenchmark.doByteAx64         80  avgt    5  42.681 ± 1.369  ns/op
 * HashBenchmark.doByteAx64        160  avgt    5  76.613 ± 5.676  ns/op
 * HashBenchmark.doByteYolk64        5  avgt    5   8.857 ± 0.110  ns/op
 * HashBenchmark.doByteYolk64       10  avgt    5  10.520 ± 0.096  ns/op
 * HashBenchmark.doByteYolk64       20  avgt    5  17.433 ± 1.281  ns/op
 * HashBenchmark.doByteYolk64       40  avgt    5  24.314 ± 4.854  ns/op
 * HashBenchmark.doByteYolk64       80  avgt    5  39.061 ± 1.601  ns/op
 * HashBenchmark.doByteYolk64      160  avgt    5  70.994 ± 0.790  ns/op
 * </pre>
 * With 1000 bytes on HotSpot OpenJDK 22:
 * <pre>
 * Benchmark                     (len)  Mode  Cnt    Score   Error  Units
 * HashBenchmark.doBufferAx64     1000  avgt    5   96.940 ± 3.098  ns/op
 * HashBenchmark.doBufferYolk64   1000  avgt    5  123.141 ± 3.773  ns/op
 * HashBenchmark.doByteAx64       1000  avgt    5  388.092 ± 6.084  ns/op
 * HashBenchmark.doByteYolk64     1000  avgt    5  359.372 ± 6.741  ns/op
 * </pre>
 * 1000 bytes on GraalVM:
 * <pre>
 * Benchmark                     (len)  Mode  Cnt    Score   Error  Units
 * HashBenchmark.doBufferAx64     1000  avgt    5  121.481 ± 1.223  ns/op
 * HashBenchmark.doBufferYolk64   1000  avgt    5  129.432 ± 3.756  ns/op
 * HashBenchmark.doByteAx64       1000  avgt    5  378.742 ± 5.824  ns/op
 * HashBenchmark.doByteYolk64     1000  avgt    5  356.265 ± 4.022  ns/op
 * </pre>
 * Yolk's byte array hash and its long array hash (which is used for the buffer) are different.
 * That explains why Yolk is slower on ByteBuffer hashing but faster on byte array hashing:
 * <pre>
 * Benchmark                     (len)  Mode  Cnt     Score     Error  Units
 * HashBenchmark.doBufferAx64    10000  avgt    5  1064.290 ±  34.429  ns/op
 * HashBenchmark.doBufferYolk64  10000  avgt    5  1269.956 ±  28.536  ns/op
 * HashBenchmark.doByteAx64      10000  avgt    5  4026.635 ±  99.830  ns/op
 * HashBenchmark.doByteYolk64    10000  avgt    5  3716.037 ± 106.306  ns/op
 * </pre>
 * The same is true for Graal, though surprisingly ByteBuffer doesn't perform as well there:
 * <pre>
 * Benchmark                     (len)  Mode  Cnt     Score    Error  Units
 * HashBenchmark.doBufferAx64    10000  avgt    5  1187.957 ± 35.496  ns/op
 * HashBenchmark.doBufferYolk64  10000  avgt    5  1338.758 ± 39.151  ns/op
 * HashBenchmark.doByteAx64      10000  avgt    5  3878.029 ± 93.453  ns/op
 * HashBenchmark.doByteYolk64    10000  avgt    5  3665.896 ± 79.322  ns/op
 * </pre>
 * Testing allocating a temporary ByteBuffer just to hash a large byte array:
 * <pre>
 * Benchmark                         (len)  Mode  Cnt     Score     Error  Units
 * HashBenchmark.doBufferAx64        10000  avgt    5  1042.729 ±  90.909  ns/op
 * HashBenchmark.doBufferWrapAx64    10000  avgt    5  1177.746 ±  57.708  ns/op
 * HashBenchmark.doBufferWrapYolk64  10000  avgt    5  1537.252 ±  51.707  ns/op
 * HashBenchmark.doBufferYolk64      10000  avgt    5  1263.996 ± 130.615  ns/op
 * HashBenchmark.doByteAx64          10000  avgt    5  4048.874 ± 149.810  ns/op
 * HashBenchmark.doByteYolk64        10000  avgt    5  3643.417 ± 147.210  ns/op
 * </pre>
 * Getting consistent results out of this is tricky, but the ByteBuffer approach is very good.
 * This is on OpenJDK HotSpot 22:
 * <pre>
 * Benchmark                         (len)  Mode  Cnt     Score     Error  Units
 * HashBenchmark.doBufferAx64        10000  avgt    5  1044.079 ±  78.031  ns/op
 * HashBenchmark.doBufferWrapAx64    10000  avgt    5  1298.438 ±  45.266  ns/op
 * HashBenchmark.doBufferWrapYolk64  10000  avgt    5  1481.765 ±  66.504  ns/op
 * HashBenchmark.doBufferYolk64      10000  avgt    5  1263.114 ±  50.253  ns/op
 * HashBenchmark.doByteAx64          10000  avgt    5  3560.461 ± 123.745  ns/op
 * HashBenchmark.doByteYolk64        10000  avgt    5  3699.877 ±  52.637  ns/op
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class HashBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {

        @Param({ "5", "10", "20", "40", "80", "160" })
        public int len;
        public String[][] strings;
        public CharSequence[] words;
        public char[][] chars;
        public long[][] longs;
        public int[][] ints;
        public byte[][] bytes;
        public double[][] doubles;
        public ByteBuffer[] buffers;
        public int idx;

        @Setup(Level.Trial)
        public void setup() {
            WhiskerRandom random = new WhiskerRandom(1000L);
            FakeLanguageGen[] languages = new FakeLanguageGen[16];
            for (int i = 0; i < 16; i++) {
                languages[i] = FakeLanguageGen.randomLanguage(random.nextLong()).addAccents(0.8, 0.6);
            }
            final String[] mid = {",", ",", ",", ";"}, end = {".", ".", ".", "!", "?", "..."};
            strings = new String[4096][len];
            words = new CharSequence[4096];
            chars = new char[4096][];
            longs = new long[4096][];
            ints = new int[4096][];
            doubles = new double[4096][];
            bytes = new byte[4096][];
            buffers = new ByteBuffer[4096];

            try {
                // 235971 is the number of words in the word list.
                ObjectList<String> wordSet = Wordlist.loadWordSet(4096 + len, len).order();
                for (int i = 0; i < 4096; i++) {
                    wordSet.subList(i, i+len).toArray(strings[i]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < 4096; i++) {
                String w = languages[i & 15].sentence(random.nextLong(), len+4>>1, len+6>>1, mid, end, 0.2, len + random.next(5));
                chars[i] = w.toCharArray();
                words[i] = new StringBuilder(w);
                long[] lon = new long[len];
                int[] inn = new int[len];
                double[] don = new double[len];
                ByteBuffer buf = ByteBuffer.allocate(len << 3).order(ByteOrder.LITTLE_ENDIAN);
                for (int j = 0; j < len; j++) {
                    long r = random.nextLong();
                    don[j] = inn[j] = (int)(lon[j] = r);
                    buf.putLong(r);
                }
                longs[i] = lon;
                ints[i] = inn;
                doubles[i] = don;
                buffers[i] = buf;
                bytes[i] = new byte[len];
                buf.rewind();
                buf.get(bytes[i], 0, len);
                buf.rewind();
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

    @Benchmark
    public long doDoubleWisp64(BenchmarkState state)
    {
        return CrossHash.Wisp.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleWisp32(BenchmarkState state)
    {
        return CrossHash.Wisp.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
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
    public long doDoubleMist64(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleMist32(BenchmarkState state)
    {
        return CrossHash.Mist.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
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
    public long doDoubleHive64(BenchmarkState state)
    {
        return CrossHash.Hive.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleHive32(BenchmarkState state)
    {
        return CrossHash.Hive.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
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
    public long doDoubleWater64(BenchmarkState state)
    {
        return CrossHash.Water.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleWater32(BenchmarkState state)
    {
        return CrossHash.Water.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
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
    public int doStringsYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.strings[state.idx = state.idx + 1 & 4095]);
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
    public long doDoubleYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doByteYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.bytes[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doByteYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.bytes[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doBufferYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64(state.buffers[state.idx = state.idx + 1 & 4095].rewind(), 0, state.len);
    }

    @Benchmark
    public int doBufferYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash(state.buffers[state.idx = state.idx + 1 & 4095].rewind(), 0, state.len);
    }

    @Benchmark
    public long doBufferWrapYolk64(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hash64Wrap(state.bytes[state.idx = state.idx + 1 & 4095], 0, state.len);
    }

    @Benchmark
    public long doBufferWrapYolk32(BenchmarkState state)
    {
        return CrossHash.Yolk.mu.hashWrap(state.bytes[state.idx = state.idx + 1 & 4095], 0, state.len);
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
    public long doDoubleCurlup64(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleCurlup32(BenchmarkState state)
    {
        return CrossHash.Curlup.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }



    @Benchmark
    public long doMx64(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doMx32(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharMx64(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharMx32(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntMx64(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntMx32(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongMx64(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongMx32(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleMx64(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleMx32(BenchmarkState state)
    {
        return CrossHash.Mx.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }


//    @Benchmark
//    public long doPurple64(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doPurple32(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doCharPurple64(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doCharPurple32(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doIntPurple64(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doIntPurple32(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doLongPurple64(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doLongPurple32(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public long doDoublePurple64(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
//    }
//
//    @Benchmark
//    public int doDoublePurple32(BenchmarkState state)
//    {
//        return CrossHash.Purple.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
//    }

    @Benchmark
    public long doTern64(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doTern32(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharTern64(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharTern32(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntTern64(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntTern32(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongTern64(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongTern32(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleTern64(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleTern32(BenchmarkState state)
    {
        return CrossHash.Tern.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doTerra64(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doTerra32(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharTerra64(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharTerra32(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntTerra64(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntTerra32(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongTerra64(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongTerra32(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleTerra64(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleTerra32(BenchmarkState state)
    {
        return CrossHash.Terra.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doTempo64(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doTempo32(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharTempo64(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharTempo32(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntTempo64(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntTempo32(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongTempo64(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongTempo32(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleTempo64(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleTempo32(BenchmarkState state)
    {
        return CrossHash.Tempo.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doHound64(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doHound32(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharHound64(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharHound32(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntHound64(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntHound32(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongHound64(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongHound32(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleHound64(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleHound32(BenchmarkState state)
    {
        return CrossHash.Hound.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doTritium64(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doTritium32(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharTritium64(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharTritium32(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntTritium64(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntTritium32(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongTritium64(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongTritium32(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleTritium64(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleTritium32(BenchmarkState state)
    {
        return CrossHash.Tritium.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doCharAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doCharAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.chars[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doIntAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doIntAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.ints[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doLongAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLongAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.longs[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doDoubleAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doByteAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.bytes[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doByteAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.bytes[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public long doBufferAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64(state.buffers[state.idx = state.idx + 1 & 4095].rewind(), 0, state.len);
    }

    @Benchmark
    public int doBufferAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash(state.buffers[state.idx = state.idx + 1 & 4095].rewind(), 0, state.len);
    }

    @Benchmark
    public long doBufferWrapAx64(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hash64Wrap(state.bytes[state.idx = state.idx + 1 & 4095], 0, state.len);
    }

    @Benchmark
    public long doBufferWrapAx32(BenchmarkState state)
    {
        return CrossHash.Ax.mu.hashWrap(state.bytes[state.idx = state.idx + 1 & 4095], 0, state.len);
    }

    @Benchmark
    public int doJDK32(BenchmarkState state)
    {
        return hashCode(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doStringsJDK32(BenchmarkState state)
    {
        return Arrays.hashCode(state.strings[state.idx = state.idx + 1 & 4095]);
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

    @Benchmark
    public int doDoubleJDK32(BenchmarkState state)
    {
        return Arrays.hashCode(state.doubles[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doDoubleJDK32Mixed(BenchmarkState state)
    {
        return HashCommon.mix(Arrays.hashCode(state.doubles[state.idx = state.idx + 1 & 4095]));
    }

    @Benchmark
    public int doLevartA32(BenchmarkState state)
    {
        return CrossHash.Levart.hash_31(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doStringsLevartA32(BenchmarkState state)
    {
        return CrossHash.Levart.hash_31(state.strings[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doLevartB32(BenchmarkState state)
    {
        return CrossHash.Levart.hash_109(state.words[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public int doStringsLevartB32(BenchmarkState state)
    {
        return CrossHash.Levart.hash_109(state.strings[state.idx = state.idx + 1 & 4095]);
    }

//    public int ixsHash(int x, int y, int z){
//        x = (x + (x << 16)) & 0x030000FF;
//        x = (x + (x <<  8)) & 0x0300F00F;
//        x = (x + (x <<  4)) & 0x030C30C3;
//        x = (x + (x <<  2)) & 0x09249249;
//        y = (y + (y << 16)) & 0x030000FF;
//        y = (y + (y <<  8)) & 0x0300F00F;
//        y = (y + (y <<  4)) & 0x030C30C3;
//        y = (y + (y <<  2)) & 0x09249249;
//        z = (z + (z << 16)) & 0x030000FF;
//        z = (z + (z <<  8)) & 0x0300F00F;
//        z = (z + (z <<  4)) & 0x030C30C3;
//        z = (z + (z <<  2)) & 0x09249249;
//        return (x |= y << 1 | z << 2) ^ x >>> 16;
//    }
//    @Benchmark
//    public int measurePointHashBitwise(BenchmarkState state) {
//        final int x, y, z;
//        x = y = z = state.intInputs[state.idx++ & 4095];
//        return ixsHash(x, y, z);
//    }
//    public static int pelotonHash(int x, int y, int z) {
//        final int n = (29 * (x << 1 ^ x >> 31) + 463 * (y << 1 ^ y >> 31) + 5867 * (z << 1 ^ z >> 31));
//        return n ^ n >>> 14;
//    }
//
//    @Benchmark
//    public int measurePointHashPeloton(BenchmarkState state) {
//        final int x, y, z;
//        x = y = z = state.intInputs[state.idx++ & 4095];
//        return pelotonHash(x, y, z);
//    }
//
//    public static int objectHash(int x, int y, int z) {
//        return  961 * x + 31 * y + z;
//    }
//
//    @Benchmark
//    public int measurePointHashObject(BenchmarkState state) {
//        final int x, y, z;
//        x = y = z = state.intInputs[state.idx++ & 4095];
//        return objectHash(x, y, z);
//    }
//
//    public static int cantorHash(int x, int y, int z){
//        x = x << 1 ^ x >> 31;
//        y = y << 1 ^ y >> 31;
//        z = z << 1 ^ z >> 31;
//        y += ((x+y) * (x+y+1) >> 1);
//        return z + ((z+y) * (z+y+1) >> 1);
//    }
//
//    @Benchmark
//    public int measurePointHashCantor(BenchmarkState state) {
//        final int x, y, z;
//        x = y = z = state.intInputs[state.idx++ & 4095];
//        return cantorHash(x, y, z);
//    }

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
     *    $ java -jar benchmarks.jar "HashBenchmark.doLong(Yolk|Tritium|Mx)"
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