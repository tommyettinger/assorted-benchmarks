package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.ds.support.Base;
import com.github.tommyettinger.ds.support.FourWheelRandom;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Higher is better, Java 17:
 * <pre>
 * Benchmark                         Mode  Cnt         Score         Error  Units
 * ParseBenchmark.doBase64          thrpt    5  53426908.429 ± 2018618.898  ops/s
 * ParseBenchmark.doCustom64        thrpt    5  52057837.495 ± 1371258.539  ops/s
 * ParseBenchmark.doHex             thrpt    5  40035210.134 ±  816840.522  ops/s
 * ParseBenchmark.doNormal          thrpt    5  11591452.124 ±  368493.139  ops/s
 * ParseBenchmark.doNumericBase87   thrpt    5  53513220.845 ± 9145363.847  ops/s
 * ParseBenchmark.doNumericBaseURI  thrpt    5  48378738.615 ± 1636725.214  ops/s
 * ParseBenchmark.doRevBase64       thrpt    5  47778204.747 ±  628849.316  ops/s
 * ParseBenchmark.doRevHex          thrpt    5  39542626.086 ±  715652.111  ops/s
 * </pre>
 * And without reversing the bytes in NumericBase:
 * <pre>
 * Benchmark                         Mode  Cnt         Score         Error  Units
 * ParseBenchmark.doBase64          thrpt    6  52924384.667 ±  591774.325  ops/s
 * ParseBenchmark.doCustom64        thrpt    6  52768262.268 ± 1284668.357  ops/s
 * ParseBenchmark.doHex             thrpt    6  40775066.539 ±  296950.173  ops/s
 * ParseBenchmark.doNormal          thrpt    6  11277459.587 ±  318875.778  ops/s
 * ParseBenchmark.doNumericBase87   thrpt    6  55891644.057 ±  926604.962  ops/s
 * ParseBenchmark.doNumericBaseURI  thrpt    6  52942334.141 ±  615677.929  ops/s
 * ParseBenchmark.doRevBase64       thrpt    6  49159453.065 ±  390308.681  ops/s
 * ParseBenchmark.doRevHex          thrpt    6  40487363.630 ±  613115.909  ops/s
 * </pre>
 *
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 6, time = 5)
@Measurement(iterations = 6, time = 5)
public class ParseBenchmark {
    private static final String digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_$";
    private static final Base b64 = new Base(digits, false, ' ', '+', '-');
    private static final NumericBase b87 = new NumericBase("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz`~!@#$%^&*()[]{}<>.?;'|_=", false, ' ', '+', '-');

    @State(Scope.Thread)
    public static class BenchmarkState {
        public String[] normal;
        public String[] intHex;
        public String[] revHex;
        public String[] int64;
        public String[] rev64;
        public String[] uri64;
        public String[] int87;
        public int idx;

        @Setup(Level.Trial)
        public void setup() {
            idx = 0;
            FourWheelRandom random = new FourWheelRandom(1234);
            normal = new String[4096];
            intHex = new String[4096];
            revHex = new String[4096];
            int64 = new String[4096];
            rev64 = new String[4096];
            uri64 = new String[4096];
            int87 = new String[4096];
            for (int i = 0; i < normal.length; i++) {
                float f = (random.nextExclusiveFloat() - 0.5f) * 2000f;
                if((i & 7) == 0) f = Math.round(f);
                normal[i] = Float.toString(f);
                intHex[i] = Integer.toHexString(Float.floatToRawIntBits(f));
                revHex[i] = Integer.toHexString(Integer.reverseBytes(Float.floatToRawIntBits(f)));
                int64[i] = b64.signed(Float.floatToRawIntBits(f));
                rev64[i] = b64.signed(Integer.reverseBytes(Float.floatToRawIntBits(f)));
                uri64[i] = NumericBase.URI_SAFE.signed(f);
                int87[i] = NumericBase.BASE86.signed(f);
            }
        }
        private final int[] fromEncoded = new int[128];
        {
            Arrays.fill(fromEncoded, -1);
            char[] toEncoded = digits.toCharArray();
            for(int i = 0; i < 64; ++i) {
                char to = toEncoded[i];
                fromEncoded[to & 127] = i;
            }

        }
        public int readInt (final CharSequence cs) {
            int sign, h, end;
            if ((end = cs.length()) <= 0)
                return 0;
            char c = cs.charAt(0);
            if (c == '-') {
                sign = -1;
                h = 0;
            } else if ((h = fromEncoded[c & 127]) < 0)
                return 0;
            else {
                sign = 1;
            }
            int data = h;
            for (int i = 1; i < end; i++) {
                if ((h = fromEncoded[cs.charAt(i) & 127]) < 0)
                    return data * sign;
                data <<= 6;
                data += h;
            }
            return data * sign;
        }

    }
    @Benchmark
    public float doNormal(BenchmarkState state)
    {
        return Float.parseFloat(state.normal[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public float doHex(BenchmarkState state)
    {
        return Float.intBitsToFloat(Integer.parseUnsignedInt(state.intHex[state.idx = state.idx + 1 & 4095], 16));
    }

    @Benchmark
    public float doRevHex(BenchmarkState state)
    {
        return Float.intBitsToFloat(Integer.reverseBytes(Integer.parseUnsignedInt(state.revHex[state.idx = state.idx + 1 & 4095], 16)));
    }

    @Benchmark
    public float doBase64(BenchmarkState state)
    {
        return Float.intBitsToFloat(b64.readInt(state.int64[state.idx = state.idx + 1 & 4095]));
    }

    @Benchmark
    public float doRevBase64(BenchmarkState state)
    {
        return Float.intBitsToFloat(Integer.reverseBytes(b64.readInt(state.rev64[state.idx = state.idx + 1 & 4095])));
    }

    @Benchmark
    public float doCustom64(BenchmarkState state)
    {
        return Float.intBitsToFloat(state.readInt(state.int64[state.idx = state.idx + 1 & 4095]));
    }

    @Benchmark
    public float doNumericBase87(BenchmarkState state)
    {
        return b87.readFloat(state.int87[state.idx = state.idx + 1 & 4095]);
    }

    @Benchmark
    public float doNumericBaseURI(BenchmarkState state)
    {
        return NumericBase.URI_SAFE.readFloat(state.uri64[state.idx = state.idx + 1 & 4095]);
    }

}
