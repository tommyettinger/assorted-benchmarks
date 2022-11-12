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
import com.github.tommyettinger.ds.support.sort.ObjectComparators;
import com.github.tommyettinger.random.WhiskerRandom;
import com.github.yellowstonegames.text.Language;
import it.unimi.dsi.fastutil.Swapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import sort.GrailSort;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                          (len)  Mode  Cnt       Score       Error  Units
 * SortBenchmark.doEttingerSort          10  avgt    5      38.495 ±    10.539  ns/op
 * SortBenchmark.doEttingerSort          40  avgt    5     231.777 ±    28.068  ns/op
 * SortBenchmark.doEttingerSort         160  avgt    5    1161.651 ±   234.271  ns/op
 * SortBenchmark.doEttingerSort         640  avgt    5    5085.883 ±  1014.257  ns/op
 * SortBenchmark.doEttingerSort        2560  avgt    5   27641.252 ±  2234.745  ns/op
 * SortBenchmark.doFastUtilMergeSort     10  avgt    5      25.732 ±     4.912  ns/op
 * SortBenchmark.doFastUtilMergeSort     40  avgt    5     230.893 ±    11.383  ns/op
 * SortBenchmark.doFastUtilMergeSort    160  avgt    5    1052.910 ±    52.314  ns/op
 * SortBenchmark.doFastUtilMergeSort    640  avgt    5    5086.110 ±   589.738  ns/op
 * SortBenchmark.doFastUtilMergeSort   2560  avgt    5   23602.268 ±  1247.427  ns/op
 * SortBenchmark.doGrailSort             10  avgt    5      24.912 ±     6.410  ns/op
 * SortBenchmark.doGrailSort             40  avgt    5    1787.961 ±   132.503  ns/op
 * SortBenchmark.doGrailSort            160  avgt    5    9839.707 ±  1773.594  ns/op
 * SortBenchmark.doGrailSort            640  avgt    5   71840.278 ± 38351.597  ns/op
 * SortBenchmark.doGrailSort           2560  avgt    5  354386.805 ± 45258.285  ns/op
 * SortBenchmark.doJDKSort               10  avgt    5      39.073 ±     5.933  ns/op
 * SortBenchmark.doJDKSort               40  avgt    5     161.342 ±    12.756  ns/op
 * SortBenchmark.doJDKSort              160  avgt    5     778.645 ±    36.450  ns/op
 * SortBenchmark.doJDKSort              640  avgt    5    3024.694 ±   102.049  ns/op
 * SortBenchmark.doJDKSort             2560  avgt    5   20000.941 ±  2049.230  ns/op
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class SortBenchmark {
    @State(Scope.Thread)
    public static class BenchmarkState {

        @Param({ "10", "40", "160", "640", "2560" })
        public int len;
        public String[] words;
        public ObjectList<String> wordList;
        public int idx;
        public WhiskerRandom random = new WhiskerRandom(1000L);
        public Language[] languages = Language.romanizedLanguages;

        public Swapper wordsSwapper = (a, b) -> {
            String t = words[a];
            words[a] = words[b];
            words[b] = t;
        };

        public GrailSort<String> grail = new GrailSort<>(String::compareTo);

        @Setup(Level.Iteration)
        public void setup() {
            words = new String[len];
            for (int i = 0; i < len; i++) {
                words[i] = languages[i & 31].sentence(random.nextLong(), random.next(3) + 1, random.next(6)+9);
            }
            wordList = new ObjectList<>(words);
            idx = 0;
        }

    }

    @Benchmark
    public void doFastUtilMergeSort(BenchmarkState state)
    {
        it.unimi.dsi.fastutil.Arrays.mergeSort(0, state.words.length, (a, b) -> state.words[a].compareTo(state.words[b]), state.wordsSwapper);
    }

    @Benchmark
    public void doGrailSort(BenchmarkState state)
    {
        state.grail.grailSortInPlace(state.words, 0, state.words.length);
    }

    @Benchmark
    public void doEttingerSort(BenchmarkState state)
    {
        ObjectComparators.sort(state.wordList, 0, state.words.length, null);
    }

    @Benchmark
    public void doJDKSort(BenchmarkState state)
    {
        Arrays.sort(state.words, 0, state.words.length);
    }

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
     *    $ java -jar benchmarks.jar SortBenchmark
     *
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SortBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(5).warmupTime(TimeValue.seconds(5))
                .measurementIterations(5).measurementTime(TimeValue.seconds(5))
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}