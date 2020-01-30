package de.heidelberg.pvs.container_bench;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class MemoryMBeanCheck {

    private static final long OFFSET = measureInternal(() -> { });

    /**
     * @return amount of memory allocated while executing provided {@link Runnable}
     */
    private static long measureInternal(final Runnable x) {
       final long now = getCurrentThreadAllocatedBytes();
       x.run();
       return getCurrentThreadAllocatedBytes() - now;
    }
    
    public static long measure(final Runnable x)
    {
        System.gc();
        final long mi = measureInternal(x);
        return mi - OFFSET;
    }

    @SuppressWarnings("restriction")
    private static long getCurrentThreadAllocatedBytes() {
        return ((com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean())
            .getThreadAllocatedBytes(Thread.currentThread().getId());
    }
    
    public static void main(String[] args) {
//        mainString();
        mainFloat();
    }
    public static void mainString() {
        StringDictionaryGenerator gen = new StringDictionaryGenerator();
        PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(".junk.txt#"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            if(i == 9)
                System.setOut(out);
            for (int size : new int[] {1, 10, 100, 1000, 10000, 100000, 1000000}) {
                try {
                    gen.init(size, -1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] words = gen.generateArray(size);
                List<String> wordList = gen.generateList(size);
                System.out.println("LIST");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK ArrayList", size,
                    measure(new Runnable() {
                        ArrayList<String> x;

                        @Override public void run () {
                            x = new ArrayList<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX Array", size, measure(new Runnable() {
                    Array<String> x;

                    @Override public void run () {
                        x = new Array<>(size);
                        for (int j = 0; j < size; j++) x.add(words[j]);
                    }
                }));

                System.out.println("UNORDERED");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK HashSet", size,
                    measure(new Runnable() {
                        HashSet<String> x;

                        @Override public void run () {
                            x = new HashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Set", size,
                    measure(new Runnable() {
                        ObjectOpenHashSet<String> x;

                        @Override public void run () {
                            x = new ObjectOpenHashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                    measure(new Runnable() {
                        ObjectSet<String> x;

                        @Override public void run () {
                            x = new ObjectSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Merry ObjectSet", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.merry.lp.ObjectSet<String> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.merry.lp.ObjectSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.println("INSERTION-ORDERED:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                    measure(new Runnable() {
                        LinkedHashSet<String> x;

                        @Override public void run () {
                            x = new LinkedHashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Linked Set", size,
                    measure(new Runnable() {
                        ObjectLinkedOpenHashSet<String> x;

                        @Override public void run () {
                            x = new ObjectLinkedOpenHashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                    measure(new Runnable() {
                        OrderedSet<String> x;

                        @Override public void run () {
                            x = new OrderedSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Merry OrderedSet", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.merry.lp.OrderedSet<String> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.merry.lp.OrderedSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
            }
        }
    }
    public static void mainFloat() {
        TangleRNG rng = new TangleRNG(-10000L, 1111111L);
        PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(".junk.txt#"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            if(i == 9)
                System.setOut(out);
            for (int size : new int[] {1, 10, 100, 1000, 10000, 100000, 1000000}) {
                Float[] words = new Float[size];
                rng.setStateA(size);
                rng.setStateB(~size);
                for (int j = 0; j < size; j++) {
                    words[j] = rng.next(24) * 0.6180339887498949f;
                }
                List<Float> wordList = Arrays.asList(words);
                System.out.println("LIST");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK ArrayList", size,
                    measure(new Runnable() {
                        ArrayList<Float> x;

                        @Override public void run () {
                            x = new ArrayList<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX Array", size, measure(new Runnable() {
                    Array<Float> x;

                    @Override public void run () {
                        x = new Array<>(size);
                        for (int j = 0; j < size; j++) x.add(words[j]);
                    }
                }));

                System.out.println("UNORDERED");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK HashSet", size,
                    measure(new Runnable() {
                        HashSet<Float> x;

                        @Override public void run () {
                            x = new HashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "FastUtil Set", size,
                    measure(new Runnable() {
                        ObjectOpenHashSet<Float> x;

                        @Override public void run () {
                            x = new ObjectOpenHashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                    measure(new Runnable() {
                        ObjectSet<Float> x;

                        @Override public void run () {
                            x = new ObjectSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Merry ObjectSet", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.merry.lp.ObjectSet<Float> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.merry.lp.ObjectSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.println("INSERTION-ORDERED:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                    measure(new Runnable() {
                        LinkedHashSet<Float> x;

                        @Override public void run () {
                            x = new LinkedHashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "FastUtil Linked Set", size,
                    measure(new Runnable() {
                        ObjectLinkedOpenHashSet<Float> x;

                        @Override public void run () {
                            x = new ObjectLinkedOpenHashSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                    measure(new Runnable() {
                        OrderedSet<Float> x;

                        @Override public void run () {
                            x = new OrderedSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Merry OrderedSet", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.merry.lp.OrderedSet<Float> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.merry.lp.OrderedSet<>(size);
                            for (int j = 0; j < size; j++) x.add(words[j]);
                        }
                    }));
            }
        }
    }
}