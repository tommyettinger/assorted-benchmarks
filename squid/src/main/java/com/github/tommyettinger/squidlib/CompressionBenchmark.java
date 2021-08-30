package com.github.tommyettinger.squidlib;

import com.github.yellowstonegames.text.Language;
import org.openjdk.jmh.annotations.*;
import squidpony.LZSEncoding;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class CompressionBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {
        @Param({"16", "256", "4096", "65536"})
        public int len;
        public String[] texts;
        public int idx;

        @Setup(Level.Trial)
        public void setup() {
            idx = 0;
            texts = new String[Language.registered.length];
            for (int i = 0; i < texts.length; i++) {
                texts[i] = Language.registered[i].sentence(i, len, len);
            }
        }
    }
    @Benchmark
    public int doBlazingChainLZSUTF(BenchmarkState state)
    {
        return blazing.chain.LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }
    @Benchmark
    public int doSquidLibLZSUTF(BenchmarkState state)
    {
        return LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }
    @Benchmark
    public int doSquidSquadLZSUTF(BenchmarkState state)
    {
        return com.github.yellowstonegames.core.LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }
    @Benchmark
    public int doFastLZSUTF(BenchmarkState state)
    {
        return com.github.tommyettinger.squidlib.LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }

}
