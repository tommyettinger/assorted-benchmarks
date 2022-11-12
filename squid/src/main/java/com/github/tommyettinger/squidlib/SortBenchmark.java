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
import it.unimi.dsi.fastutil.ints.IntComparator;
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
 * SortBenchmark.doEttingerSort          10  avgt    5      38.374 ±     8.379  ns/op
 * SortBenchmark.doEttingerSort          40  avgt    5     232.439 ±    17.530  ns/op
 * SortBenchmark.doEttingerSort         160  avgt    5    1117.275 ±   154.614  ns/op
 * SortBenchmark.doEttingerSort         640  avgt    5    5075.120 ±  1262.704  ns/op
 * SortBenchmark.doEttingerSort        2560  avgt    5   25815.348 ±  1508.187  ns/op
 * SortBenchmark.doFastUtilMergeSort     10  avgt    5       4.160 ±     0.278  ns/op
 * SortBenchmark.doFastUtilMergeSort     40  avgt    5      33.168 ±     0.195  ns/op
 * SortBenchmark.doFastUtilMergeSort    160  avgt    5     141.816 ±     1.808  ns/op
 * SortBenchmark.doFastUtilMergeSort    640  avgt    5     582.644 ±    89.238  ns/op
 * SortBenchmark.doFastUtilMergeSort   2560  avgt    5    2104.164 ±   129.792  ns/op
 * SortBenchmark.doGrailSort             10  avgt    5      24.669 ±     7.966  ns/op
 * SortBenchmark.doGrailSort             40  avgt    5    1723.824 ±    75.031  ns/op
 * SortBenchmark.doGrailSort            160  avgt    5   10493.553 ±  2773.331  ns/op
 * SortBenchmark.doGrailSort            640  avgt    5   66975.016 ± 39108.912  ns/op
 * SortBenchmark.doGrailSort           2560  avgt    5  318195.080 ±  7437.860  ns/op
 * SortBenchmark.doJDKSort               10  avgt    5      38.705 ±     4.284  ns/op
 * SortBenchmark.doJDKSort               40  avgt    5     159.985 ±    11.280  ns/op
 * SortBenchmark.doJDKSort              160  avgt    5     780.382 ±    28.775  ns/op
 * SortBenchmark.doJDKSort              640  avgt    5    3000.739 ±   127.574  ns/op
 * SortBenchmark.doJDKSort             2560  avgt    5   19924.747 ±  1567.535  ns/op
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
        it.unimi.dsi.fastutil.Arrays.mergeSort(0, state.words.length, Integer::compare, state.wordsSwapper);
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