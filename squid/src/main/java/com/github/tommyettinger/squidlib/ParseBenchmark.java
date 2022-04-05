package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.ds.support.Base;
import com.github.tommyettinger.ds.support.FourWheelRandom;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 6, time = 5)
@Measurement(iterations = 6, time = 5)
public class ParseBenchmark {
    private static final String digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_$";
    private static final Base b64 = new Base(digits, false, ' ', '+', '-');
    private static final int[] fromEncoded = new int[128];
    static {
        Arrays.fill(fromEncoded, -1);
        char[] toEncoded = digits.toCharArray();
        for(int i = 0; i < 64; ++i) {
            char to = toEncoded[i];
            fromEncoded[to & 127] = i;
        }

    }
    public static int readInt (final CharSequence cs) {
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

    public static int readReversedInt (final CharSequence cs) {
        int sign, h, end;
        if ((end = cs.length()) <= 0)
            return 0;
        char c = cs.charAt(end-1);
        if (c == '-') {
            sign = -1;
            h = 0;
        } else if ((h = fromEncoded[c & 127]) < 0)
            return 0;
        else {
            sign = 1;
        }
        int data = h;
        for (int i = end - 2; i >= 0; i--) {
            if ((h = fromEncoded[cs.charAt(i) & 127]) < 0)
                return data * sign;
            data <<= 6;
            data += h;
        }
        return data * sign;
    }


    @State(Scope.Thread)
    public static class BenchmarkState {
        public String[] normal;
        public String[] intHex;
        public String[] revHex;
        public String[] int64;
        public String[] rev64;
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
            for (int i = 0; i < normal.length; i++) {
                float f = (random.nextExclusiveFloat() - 0.5f) * 2000f;
                if((i & 7) == 0) f = Math.round(f);
                normal[i] = Float.toString(f);
                intHex[i] = Integer.toHexString(Float.floatToRawIntBits(f));
                revHex[i] = Integer.toHexString(Integer.reverseBytes(Float.floatToRawIntBits(f)));
                int64[i] = b64.signed(Float.floatToRawIntBits(f));
                rev64[i] = b64.signed(Integer.reverseBytes(Float.floatToRawIntBits(f)));
            }
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
        return Float.intBitsToFloat(Integer.reverseBytes(b64.readInt(state.revHex[state.idx = state.idx + 1 & 4095])));
    }

    @Benchmark
    public float doCustom64(BenchmarkState state)
    {
        return Float.intBitsToFloat(readInt(state.int64[state.idx = state.idx + 1 & 4095]));
    }

    @Benchmark
    public float doRevCustom64(BenchmarkState state)
    {
        return Float.intBitsToFloat((readReversedInt(state.revHex[state.idx = state.idx + 1 & 4095])));
    }

}
