package com.github.tommyettinger.squidlib;

import com.github.yellowstonegames.text.Language;
import org.openjdk.jmh.annotations.*;
import squidpony.LZSEncoding;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                                  (len)   Mode  Cnt      Score      Error  Units
 * CompressionBenchmark.doBlazingChainLZSUTF     16  thrpt    5  77318.748 ± 1039.049  ops/s
 * CompressionBenchmark.doBlazingChainLZSUTF    256  thrpt    5   7173.859 ±  187.882  ops/s
 * CompressionBenchmark.doBlazingChainLZSUTF   4096  thrpt    5    459.572 ±   16.271  ops/s
 * CompressionBenchmark.doBlazingChainLZSUTF  65536  thrpt    5     17.853 ±    2.125  ops/s
 * CompressionBenchmark.doFastLZSUTF             16  thrpt    5  94421.588 ± 3052.643  ops/s
 * CompressionBenchmark.doFastLZSUTF            256  thrpt    5   7602.584 ±  127.237  ops/s
 * CompressionBenchmark.doFastLZSUTF           4096  thrpt    5    473.684 ±    9.522  ops/s
 * CompressionBenchmark.doFastLZSUTF          65536  thrpt    5     17.015 ±    2.764  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF         16  thrpt    5  76093.203 ± 1197.082  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF        256  thrpt    5   7049.806 ±   96.669  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF       4096  thrpt    5    461.141 ±   11.680  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF      65536  thrpt    5     17.399 ±    2.943  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF       16  thrpt    5  55543.346 ± 1618.040  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF      256  thrpt    5   4368.717 ±  141.911  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF     4096  thrpt    5    296.431 ±    1.967  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF    65536  thrpt    5     13.382 ±    1.259  ops/s
 * </pre>
 */
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
