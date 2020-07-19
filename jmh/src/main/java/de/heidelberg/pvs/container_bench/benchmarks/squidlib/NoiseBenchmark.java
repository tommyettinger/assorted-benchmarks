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

package de.heidelberg.pvs.container_bench.benchmarks.squidlib;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.squidmath.*;

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
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class NoiseBenchmark {

    private short x, y, z, w, u, v;
    private final FastNoise fast = new FastNoise(12345),
            fast3 = new FastNoise(12345),
            fast5 = new FastNoise(12345);
    private final OpenSimplex2F osf = new OpenSimplex2F(12345L);
    private final OpenSimplex2S oss = new OpenSimplex2S(12345L);

    @Setup(Level.Trial)
    public void setup() {
        fast.setFractalOctaves(1);
        fast.setNoiseType(FastNoise.SIMPLEX);
        fast.setFrequency(0.03125f);
        fast3.setNoiseType(FastNoise.SIMPLEX_FRACTAL);
        fast3.setFractalOctaves(3);
        fast5.setNoiseType(FastNoise.SIMPLEX_FRACTAL);
        fast5.setFractalOctaves(5);
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
    public double measureSeeded6D() {
        return SeededNoise.noise(++x * 0.03125, --y * 0.03125, z++ * 0.03125, w-- * 0.03125, ++u * 0.03125, ++v * 0.03125, 1024L);
    }

    @Benchmark
    public float measureFastNoise2D() {
        return fast.getSimplex(++x, --y);
    }

    @Benchmark
    public float measureFastNoise3D() {
        return fast.getSimplex(++x, --y, z++);
    }

    @Benchmark
    public float measureFastNoise4D() {
        return fast.getSimplex(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureFastNoise6D() {
        return fast.getSimplex(++x, --y, z++, w--, ++u, ++v);
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
        return osf.noise2(++x, --y);
    }

    @Benchmark
    public double measureOSFNoise3D() {
        return osf.noise3_Classic(++x, --y, z++);
    }

    @Benchmark
    public double measureOSFNoise4D() {
        return osf.noise4_Classic(++x, --y, z++, w--);
    }

    @Benchmark
    public double measureOSSNoise2D() {
        return oss.noise2(++x, --y);
    }

    @Benchmark
    public double measureOSSNoise3D() {
        return oss.noise3_Classic(++x, --y, z++);
    }

    @Benchmark
    public double measureOSSNoise4D() {
        return oss.noise4_Classic(++x, --y, z++, w--);
    }


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
     *    $ java -jar target/benchmarks.jar NoiseBenchmark -wi 4 -i 4 -f 1
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
