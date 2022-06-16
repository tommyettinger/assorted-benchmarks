package com.github.tommyettinger.squidlib;

import com.github.yellowstonegames.text.Language;
import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
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
 * Trying to verify that all the implementations are now the same and have comparable speed:
 * <pre>
 * Benchmark                                  (len)   Mode  Cnt      Score       Error  Units
 * CompressionBenchmark.doBlazingChainLZSUTF     16  thrpt    5  94120.125 ±  1240.833  ops/s
 * CompressionBenchmark.doBlazingChainLZSUTF    256  thrpt    5   7621.959 ±    83.592  ops/s
 * CompressionBenchmark.doBlazingChainLZSUTF   4096  thrpt    5    461.459 ±     5.373  ops/s
 * CompressionBenchmark.doFastLZSUTF             16  thrpt    5  90312.565 ± 22619.370  ops/s
 * CompressionBenchmark.doFastLZSUTF            256  thrpt    5   7529.014 ±   106.222  ops/s
 * CompressionBenchmark.doFastLZSUTF           4096  thrpt    5    460.864 ±    16.174  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF         16  thrpt    5  92841.255 ±  1772.350  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF        256  thrpt    5   7403.885 ±   162.004  ops/s
 * CompressionBenchmark.doSquidLibLZSUTF       4096  thrpt    5    453.121 ±     5.156  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF       16  thrpt    5  91092.692 ±  1802.423  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF      256  thrpt    5   7265.979 ±   147.258  ops/s
 * CompressionBenchmark.doSquidSquadLZSUTF     4096  thrpt    5    452.311 ±     8.338  ops/s
 * </pre>
 * The big important gain is that SquidSquad isn't so much slower anymore.
 * <br>
 * Testing LZByteEncoding:
 * <pre>
 * Benchmark                               (len)   Mode  Cnt      Score      Error  Units
 * CompressionBenchmark.doBlazingChainLZB     16  thrpt    8  94862.587 ± 1532.027  ops/s
 * CompressionBenchmark.doBlazingChainLZB    256  thrpt    8   7447.933 ±  246.443  ops/s
 * CompressionBenchmark.doSquidSquadLZB       16  thrpt    8  63811.520 ±  798.487  ops/s
 * CompressionBenchmark.doSquidSquadLZB      256  thrpt    8   4409.747 ±   40.498  ops/s
 * </pre>
 * Again, using HashMap and HashSet with String keys is a win here.
 * <br>
 * <pre>
 * Benchmark                               (len)   Mode  Cnt       Score     Error  Units
 * CompressionBenchmark.doByteCompress        16  thrpt    6   77381.815 ± 892.987  ops/s
 * CompressionBenchmark.doByteCompress       256  thrpt    6    7452.812 ± 441.111  ops/s
 * CompressionBenchmark.doByteCompress      4096  thrpt    6     509.880 ±  48.081  ops/s
 * CompressionBenchmark.doByteCompressOpt     16  thrpt    6  110121.018 ± 961.789  ops/s
 * CompressionBenchmark.doByteCompressOpt    256  thrpt    6    7432.277 ±  39.430  ops/s
 * CompressionBenchmark.doByteCompressOpt   4096  thrpt    6     456.446 ±   5.607  ops/s
 * </pre>
 * Opt doesn't use HashMap or HashSet in this version, but does avoid some allocation at startup, which might explain
 * why it's so much faster on small inputs.
 * <br>
 * <pre>
 * Benchmark                                (len)   Mode  Cnt       Score      Error  Units
 * CompressionBenchmark.doByteCompress         16  thrpt    6   84414.116 ± 1666.922  ops/s
 * CompressionBenchmark.doByteCompress        256  thrpt    6    7967.798 ±   85.545  ops/s
 * CompressionBenchmark.doByteCompress       4096  thrpt    6     511.472 ±    9.256  ops/s
 * CompressionBenchmark.doByteCompressOpt      16  thrpt    6  116634.440 ±  724.312  ops/s
 * CompressionBenchmark.doByteCompressOpt     256  thrpt    6    7108.033 ±   77.026  ops/s
 * CompressionBenchmark.doByteCompressOpt    4096  thrpt    6     431.773 ±    8.907  ops/s
 * CompressionBenchmark.doByteCompressOpt2     16  thrpt    6  101680.907 ±  278.561  ops/s
 * CompressionBenchmark.doByteCompressOpt2    256  thrpt    6    8431.531 ±  118.579  ops/s
 * CompressionBenchmark.doByteCompressOpt2   4096  thrpt    6     522.932 ±    5.352  ops/s
 * </pre>
 * Opt2 seems like a winner here, though there might be some tricks Opt can still do with ObjectIntMap to gain speed.
 * <br>
 * <pre>
 * Benchmark                                (len)   Mode  Cnt       Score      Error  Units
 * CompressionBenchmark.doByteCompress         16  thrpt    6   84571.417 ±  803.815  ops/s
 * CompressionBenchmark.doByteCompress        256  thrpt    6    8223.614 ±   71.457  ops/s
 * CompressionBenchmark.doByteCompress       4096  thrpt    6     535.110 ±    2.914  ops/s
 * CompressionBenchmark.doByteCompressOpt      16  thrpt    6  101101.452 ±  888.442  ops/s
 * CompressionBenchmark.doByteCompressOpt     256  thrpt    6    6174.722 ±   48.094  ops/s
 * CompressionBenchmark.doByteCompressOpt    4096  thrpt    6     404.296 ±    4.377  ops/s
 * CompressionBenchmark.doByteCompressOpt2     16  thrpt    6  102308.207 ± 1212.622  ops/s
 * CompressionBenchmark.doByteCompressOpt2    256  thrpt    6    8664.567 ±  113.650  ops/s
 * CompressionBenchmark.doByteCompressOpt2   4096  thrpt    6     538.306 ±    4.288  ops/s
 * </pre>
 * Using String-specialized Map and Set didn't really help Opt here. It looks like we will go with the boxed HashMap and
 * HashSet...
 * <br>
 * <pre>
 * Benchmark                                (len)   Mode  Cnt       Score      Error  Units
 * CompressionBenchmark.doByteCompress         16  thrpt    6   83946.658 ±  652.978  ops/s
 * CompressionBenchmark.doByteCompress        256  thrpt    6    6308.446 ±   85.102  ops/s
 * CompressionBenchmark.doByteCompress       4096  thrpt    6     522.166 ±    6.510  ops/s
 * CompressionBenchmark.doByteCompressOpt      16  thrpt    6  102603.753 ± 1070.536  ops/s
 * CompressionBenchmark.doByteCompressOpt     256  thrpt    6    8281.024 ±   97.827  ops/s
 * CompressionBenchmark.doByteCompressOpt    4096  thrpt    6     524.618 ±    7.416  ops/s
 * CompressionBenchmark.doByteCompressOpt2     16  thrpt    6  110666.286 ± 1604.567  ops/s
 * CompressionBenchmark.doByteCompressOpt2    256  thrpt    6   10224.805 ±   94.131  ops/s
 * CompressionBenchmark.doByteCompressOpt2   4096  thrpt    6     610.817 ±    9.558  ops/s
 * </pre>
 * By avoiding frequent recreation of temporary collections, Opt2 gets a very significant speed boost (about 35% faster
 * than the non-Opt version for mid-size inputs, and faster all around by some factor).
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 6, time = 6)
@Measurement(iterations = 6, time = 6)
public class CompressionBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {
        @Param({"16", "256", "4096"})
        public int len;
        public String[] texts;
        public byte[][] bytes;
        public int idx;

        @Setup(Level.Trial)
        public void setup() {
            idx = 0;
            texts = new String[Language.registered.length];
            bytes = new byte[Language.registered.length][];
            for (int i = 0; i < texts.length; i++) {
                bytes[i] = (texts[i] = Language.registered[i].sentence(i, len, len)).getBytes(StandardCharsets.UTF_8);
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
        return squid.lib.LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }
    @Benchmark
    public int doSquidSquadLZSUTF(BenchmarkState state)
    {
        return squid.squad.LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }
    @Benchmark
    public int doFastLZSUTF(BenchmarkState state)
    {
        return com.github.tommyettinger.squidlib.LZSEncoding.compressToUTF16(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length();
    }

    @Benchmark
    public int doBlazingChainLZB(BenchmarkState state)
    {
        return blazing.chain.redux.LZByteEncoding.compressToBytes(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length;
    }

    @Benchmark
    public int doSquidSquadLZB(BenchmarkState state)
    {
        return com.github.yellowstonegames.core.LZByteEncoding.compressToBytes(state.texts[state.idx = (state.idx + 1) % state.texts.length]).length;
    }

    @Benchmark
    public int doByteCompress(BenchmarkState state)
    {
        return ByteStringEncoding.compress(state.bytes[state.idx = (state.idx + 1) % state.bytes.length]).length();
    }

    @Benchmark
    public int doByteCompressOpt(BenchmarkState state)
    {
        return ByteStringEncoding.Opt.compress(state.bytes[state.idx = (state.idx + 1) % state.bytes.length]).length();
    }

    @Benchmark
    public int doByteCompressOpt2(BenchmarkState state)
    {
        return ByteStringEncoding.Opt2.compress(state.bytes[state.idx = (state.idx + 1) % state.bytes.length]).length();
    }

}
