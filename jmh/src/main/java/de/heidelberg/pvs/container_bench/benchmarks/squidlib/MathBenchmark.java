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

import com.badlogic.gdx.math.MathUtils;
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
 * And here's those two atan2() methods, each in the 0-360 range instead of -pi to pi:
 * <pre>
 * Benchmark                                Mode  Cnt   Score   Error  Units
 * MathBenchmark.measureGdxAtan2Deg         avgt    5  37.212 ± 0.479  ns/op
 * MathBenchmark.measureSquidAtan2DegFloat  avgt    5  22.696 ± 0.346  ns/op
 * </pre>
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class MathBenchmark {

    private final double[] inputs = new double[65536];
    private final float[] floatInputs = new float[65536];
    private final double[] arcInputs = new double[65536];
    {
        for (int i = 0; i < 65536; i++) {
            floatInputs[i] = (float) (inputs[i] =
                    //-2.0 + (i * 0.0625)
                    NumberTools.randomSignedDouble(i + 107) * 4096.0
            );
            arcInputs[i] = NumberTools.randomSignedDouble(i + 421L);
        }
    }
    public static double asin(double a)
    {
        return (a * (1.0 + (a *= a) * (-0.141514171442891431 + a * -0.719110791477959357)))
                / (1.0 + a * (-0.439110389941411144 + a * -0.471306172023844527));
    }

    private short mathCos = -0x8000;
    private short mathSin = -0x8000;
    private short mathASin = -0x8000;
    private short asinChristensen = -0x8000;
    private short asinSquid = -0x8000;
    private short cosOld = -0x8000;
    private short sinOld = -0x8000;
    private short sinNick = -0x8000;
    private short cosNick = -0x8000;
    private short sinBit = -0x8000;
    private short cosBit = -0x8000;
    private short sinBitF = -0x8000;
    private short cosBitF = -0x8000;
    private short cosFloat = -0x8000;
    private short sinFloat = -0x8000;
    private short cosGdx = -0x8000;
    private short sinGdx = -0x8000;
    private short mathCosDeg = -0x8000;
    private short mathSinDeg = -0x8000;
    private short sinNickDeg = -0x8000;
    private short cosNickDeg = -0x8000;
    private short cosGdxDeg = -0x8000;
    private short sinGdxDeg = -0x8000;
    private short baseline = -0x8000;
    private short mathAtan2X = -0x4000;
    private short mathAtan2Y = -0x8000;
    private short atan2ApproxX = -0x4000;
    private short atan2ApproxY = -0x8000;
    private short atan2ApproxXF = -0x4000;
    private short atan2ApproxYF = -0x8000;
    private short atan2GdxX = -0x4000;
    private short atan2GdxY = -0x8000;
    private short atan2DegSquidXF = -0x4000;
    private short atan2DegSquidYF = -0x8000;
    private short atan2DegGdxX = -0x4000;
    private short atan2DegGdxY = -0x8000;
    private short atan2ApproxAX = -0x4000;
    private short atan2ApproxAY = -0x8000;
    private short atan2ApproxAXF = -0x4000;
    private short atan2ApproxAYF = -0x8000;
    
    private int npotHC = 0;
    private int npotM = 0;
    private int npotCLZ = 0;


    @Benchmark
    public double measureBaseline()
    {
        return inputs[baseline++ & 0xFFFF];
    }



    @Benchmark
    public double measureMathCos()
    {
        return Math.cos(inputs[mathCos++ & 0xFFFF]);
    }

    @Benchmark
    public double measureMathSin()
    {
        return Math.sin(inputs[mathSin++ & 0xFFFF]);
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
        return cosOld(inputs[cosOld++ & 0xFFFF]);
//        cosOld += 0.0625;
//        final long s = Double.doubleToLongBits(cosOld * 0.3183098861837907 + (cosOld < 0.0 ? -2.0 : 2.0)), m = (s >>> 52 & 0x7FFL) - 0x400, sm = s << m;
//        final double a = (Double.longBitsToDouble(((sm ^ -((sm & 0x8000000000000L) >> 51)) & 0xfffffffffffffL) | 0x4000000000000000L) - 2.0);
//        return a * a * (3.0 - 2.0 * a) * -2.0 + 1.0;
    }

    //@Benchmark
    public double measureSinApproxOld() {
        return sinOld(inputs[sinOld++ & 0xFFFF]);
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

    @Benchmark
    public float measureSquidCosF() {
        return NumberTools.cos(floatInputs[cosFloat++ & 0xFFFF]);
    }

    @Benchmark
    public float measureSquidSinF() {
        return NumberTools.sin(floatInputs[sinFloat++ & 0xFFFF]);
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
        return NumberTools.sin(inputs[sinNick++ & 0xFFFF]);
    }

    @Benchmark
    public double measureSquidCos()
    {
        return  NumberTools.cos(inputs[cosNick++ & 0xFFFF]);
    }

    @Benchmark
    public double measureBitSin()
    {
        return sinBit(inputs[sinBit++ & 0xFFFF]);
    }

    @Benchmark
    public double measureBitCos()
    {
        return cosBit(inputs[cosBit++ & 0xFFFF]);
    }

    @Benchmark
    public float measureBitSinF()
    {
        return sinBit(floatInputs[sinBitF++ & 0xFFFF]);
    }

    @Benchmark
    public float measureBitCosF()
    {
        return cosBit(floatInputs[cosBitF++ & 0xFFFF]);
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
    public float measureGdxSin()
    {
        return MathUtils.sin(floatInputs[sinGdx++ & 0xFFFF]);
    }

    @Benchmark
    public float measureGdxCos() {
        return MathUtils.cos(floatInputs[cosGdx++ & 0xFFFF]);
    }

    @Benchmark
    public double measureMathCosDeg()
    {
        return Math.cos(inputs[mathCosDeg++ & 0xFFFF] * 0.017453292519943295);
    }

    @Benchmark
    public double measureMathSinDeg()
    {
        return Math.sin(inputs[mathSinDeg++ & 0xFFFF] * 0.017453292519943295);
    }

    @Benchmark
    public float measureSquidCosDeg() {
        return NumberTools.cosDegrees(floatInputs[cosNickDeg++ & 0xFFFF]);
    }

    @Benchmark
    public float measureSquidSinDeg() {
        return NumberTools.sinDegrees(floatInputs[sinNickDeg++ & 0xFFFF]);
    }

    @Benchmark
    public float measureGdxSinDeg()
    {
        return MathUtils.sinDeg(floatInputs[sinGdxDeg++ & 0xFFFF]);
    }

    @Benchmark
    public float measureGdxCosDeg() {
        return MathUtils.cosDeg(floatInputs[cosGdxDeg++ & 0xFFFF]);
    }
    @Benchmark
    public double measureMathAtan2()
    {
        return Math.atan2(inputs[mathAtan2Y++ & 0xFFFF], inputs[mathAtan2X++ & 0xFFFF]);
    }

    @Benchmark
    public double measureSquidAtan2()
    {
        return NumberTools.atan2(inputs[atan2ApproxY++ & 0xFFFF], inputs[atan2ApproxX++ & 0xFFFF]);
    }

    @Benchmark
    public float measureSquidAtan2Float()
    {
        return NumberTools.atan2(floatInputs[atan2ApproxYF++ & 0xFFFF], floatInputs[atan2ApproxXF++ & 0xFFFF]);
    }

    @Benchmark
    public float measureGdxAtan2()
    {
        return MathUtils.atan2(floatInputs[atan2GdxY++ & 0xFFFF], floatInputs[atan2GdxX++ & 0xFFFF]);
    }
 
    @Benchmark
    public float measureSquidAtan2DegFloat()
    {
        return NumberTools.atan2Degrees360(floatInputs[atan2DegSquidYF++ & 0xFFFF], floatInputs[atan2DegSquidXF++ & 0xFFFF]);
    }

    @Benchmark
    public float measureGdxAtan2Deg()
    {
        return (MathUtils.radiansToDegrees * MathUtils.atan2(floatInputs[atan2DegGdxY++ & 0xFFFF], floatInputs[atan2DegGdxX++ & 0xFFFF]) + 360f) % 360f;
    }
    @Benchmark
    public double measureAtan2Baseline()
    {
        return inputs[atan2ApproxAY++ & 0xFFFF] + inputs[atan2ApproxAX++ & 0xFFFF];
    }
    @Benchmark
    public float measureAtan2BaselineFloat()
    {
        return floatInputs[atan2ApproxAYF++ & 0xFFFF] + floatInputs[atan2ApproxAXF++ & 0xFFFF];
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
    
    
    /*
mvn clean install
java -jar target/benchmarks.jar UncommonBenchmark -wi 5 -i 5 -f 1 -gc true
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
                precisionError = 0.0, cosDegNickError = 0.0, sinDegNickError = 0.0, cosDegGdxError = 0.0, sinDegGdxError = 0.0,
                asinChristensenError = 0.0, asinSquidError = 0.0;
        ;
        System.out.println("Math.sin()       : " + u.measureMathSin());
        System.out.println("Math.sin()       : " + u.measureMathSin());
        System.out.println("Math.cos()       : " + u.measureMathCos());
        System.out.println("double sin approx: " + u.measureSquidSin());
        System.out.println("double cos approx: " + u.measureSquidCos());
//        System.out.println("float approx     : " + u.measureCosApproxFloat());
//        System.out.println("Climatiano       : " + u.measureCosApproxClimatiano());
//        System.out.println("ClimatianoLP     : " + u.measureCosApproxClimatianoLP());
        for (int r = 0; r < 0x1000; r++) {
            //margin += 0.0001;
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
            u.mathCosDeg = i;
            u.mathSinDeg = i;
            u.cosNickDeg = i;
            u.sinNickDeg = i;
            u.cosGdxDeg = i;
            u.sinGdxDeg = i;
            double c = u.measureMathCos(), s = u.measureMathSin(), cd = u.measureMathCosDeg(), sd = u.measureMathSinDeg(), as = u.measureMathASin();
            precisionError += Math.abs(c - (float)c);
            cosFError += Math.abs(u.measureSquidCosF() - c);
            cosGdxError += Math.abs(u.measureGdxCos() - c);
            sinGdxError += Math.abs(u.measureGdxSin() - s);
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
        //System.out.println("Margin allowed   : " + margin);
        System.out.println("base float error : " + precisionError);
        System.out.println("cos GDX          : " + cosGdxError);
        System.out.println("sin GDX          : " + sinGdxError);
        System.out.println("cos Squid float  : " + cosFError);
        System.out.println("sin Squid float  : " + sinFError);
        System.out.println("sin Squid        : " + sinNickError);
        System.out.println("cos Squid        : " + cosNickError);
        System.out.println("sin Bit          : " + sinBitError);
        System.out.println("cos Bit          : " + cosBitError);
        System.out.println("sin BitF         : " + sinBitFError);
        System.out.println("cos BitF         : " + cosBitFError);
        System.out.println("sin Nick deg     : " + sinDegNickError);
        System.out.println("cos Nick deg     : " + cosDegNickError);
        System.out.println("sin GDX deg      : " + sinDegGdxError);
        System.out.println("cos GDX deg      : " + cosDegGdxError);
        System.out.println("asin Chr.        : " + asinChristensenError);
        System.out.println("asin Squid       : " + asinSquidError);
        double atan2ApproxError = 0;
        double atan2GDXError = 0;
        double at;
        for(int r = 0; r < 0x10000; r++)
        {
            short i = (short) (DiverRNG.determine(r) & 0xFFFF);
            short j = (short) (DiverRNG.determine(-0x20000 - r - i) & 0xFFFF);
            u.mathAtan2X = i;
            u.mathAtan2Y = j;
            u.atan2ApproxX = i;
            u.atan2ApproxY = j;
            u.atan2ApproxAX = i;
            u.atan2ApproxAY = j;
            u.atan2GdxX = i;
            u.atan2GdxY = j;
            at = u.measureMathAtan2();
            atan2ApproxError += Math.abs(u.measureSquidAtan2() - at);
            atan2GDXError += Math.abs(u.measureGdxAtan2() - at);
        }
        System.out.println("atan2 Squid      : " + atan2ApproxError);
        System.out.println("atan2 GDX        : " + atan2GDXError);
    }
}
