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
import squidpony.squidmath.DiverRNG;
import squidpony.squidmath.NumberTools;

import java.util.concurrent.TimeUnit;

/**
 * Using Java 17, HotSpot:
 * <pre>
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * WobbleBenchmark.measureBaseline           avgt    5   2.777 ± 0.213  ns/op
 * WobbleBenchmark.measureJuniperIntDouble   avgt    5   9.259 ± 0.159  ns/op
 * WobbleBenchmark.measureJuniperIntFloat    avgt    5  10.668 ± 0.249  ns/op
 * WobbleBenchmark.measureJuniperLongDouble  avgt    5   8.327 ± 0.281  ns/op
 * WobbleBenchmark.measureJuniperLongFloat   avgt    5   9.005 ± 0.291  ns/op
 * WobbleBenchmark.measureSquidIntDouble     avgt    5   8.911 ± 0.423  ns/op
 * WobbleBenchmark.measureSquidIntFloat      avgt    5  10.007 ± 0.438  ns/op
 * WobbleBenchmark.measureSquidLongDouble    avgt    5   7.633 ± 0.426  ns/op
 * WobbleBenchmark.measureSquidLongFloat     avgt    5   8.194 ± 0.072  ns/op
 * WobbleBenchmark.measureWiggleIntDouble    avgt    5   7.111 ± 0.307  ns/op
 * WobbleBenchmark.measureWiggleIntFloat     avgt    5   7.195 ± 0.403  ns/op
 * WobbleBenchmark.measureWiggleLongDouble   avgt    5   7.217 ± 0.353  ns/op
 * WobbleBenchmark.measureWiggleLongFloat    avgt    5   7.789 ± 0.328  ns/op
 * </pre>
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class WobbleBenchmark {

    private final double[] inputs = new double[65536];
    private final float[] floatInputs = new float[65536];
    {
        for (int i = 0; i < 65536; i++) {
            floatInputs[i] = (float) (inputs[i] =
                    (DiverRNG.determine(i) >> 10) * 0x1p-41
            );
        }
    }

    private int index = -0x8000;

    @Benchmark
    public double measureBaseline()
    {
        return inputs[index++ & 0xFFFF];
    }

    @Benchmark
    public double measureSquidIntDouble()
    {
        return NumberTools.swayRandomized(index ^ 0x23456789, inputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public float measureSquidIntFloat()
    {
        return NumberTools.swayRandomized(index ^ 0x23456789, floatInputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public double measureSquidLongDouble()
    {
        return NumberTools.swayRandomized(index ^ 0x2345678923456789L, inputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public float measureSquidLongFloat()
    {
        return NumberTools.swayRandomized(index ^ 0x2345678923456789L, floatInputs[index++ & 0xFFFF]);
    }

    @Benchmark
    public double measureJuniperIntDouble()
    {
        return LineWobble.wobble(index ^ 0x23456789, inputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public float measureJuniperIntFloat()
    {
        return LineWobble.wobble(index ^ 0x23456789, floatInputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public double measureJuniperLongDouble()
    {
        return LineWobble.wobble(index ^ 0x2345678923456789L, inputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public float measureJuniperLongFloat()
    {
        return LineWobble.wobble(index ^ 0x2345678923456789L, floatInputs[index++ & 0xFFFF]);
    }


    @Benchmark
    public double measureWiggleIntDouble()
    {
        return LineWobble.wiggle(index ^ 0x23456789, inputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public float measureWiggleIntFloat()
    {
        return LineWobble.wiggle(index ^ 0x23456789, floatInputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public double measureWiggleLongDouble()
    {
        return LineWobble.wiggle(index ^ 0x2345678923456789L, inputs[index++ & 0xFFFF]);
    }
    @Benchmark
    public float measureWiggleLongFloat()
    {
        return LineWobble.wiggle(index ^ 0x2345678923456789L, floatInputs[index++ & 0xFFFF]);
    }

    /*
mvn clean install
java -jar benchmarks.jar WobbleBenchmark -f 1
     */
    public static void main2(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(WobbleBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
