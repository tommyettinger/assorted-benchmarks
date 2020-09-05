package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.utils.*;
import com.github.tommyettinger.ds.IndexedMap;
import com.github.tommyettinger.ds.IndexedSet;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import de.heidelberg.pvs.container_bench.generators.StringDictionaryGenerator;
import de.heidelberg.pvs.container_bench.generators.StringDictionaryGenerator.CustomString;
import squidpony.squidmath.UnorderedSet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.*;

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
        mainCharSeq();
        mainString();
        mainFloat();
        mainInteger();
//        mapStringInt();
    }
    public static void mainString () {
        StringDictionaryGenerator gen = new StringDictionaryGenerator();
        PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(".junk.txt#"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 6; i++) {
            if(i == 5)
                System.setOut(out);
            for (int size : new int[] {1, 10, 100, 1000, 10000, 100000, 1000000}) {
                try {
                    gen.init(size, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] items = gen.generateArray(size);
                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<String> x;

                            @Override public void run () {
                                x = new HashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<String> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<String> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<String> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<String> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<String> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<String> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<String, String> x;

                            @Override public void run () {
                                x = new HashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.UnorderedMap<String, String> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.UnorderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<String, String> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<String, String> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<String, String> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<String, String> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<String, String> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
            }
        }
    }
    public static void mainCharSeq () {
        StringDictionaryGenerator gen = new StringDictionaryGenerator();
        PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(".junk.txt#"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 6; i++) {
            if(i == 5)
                System.setOut(out);
            for (int size : new int[] {1, 10, 100, 1000, 10000, 100000, 1000000}) {
                try {
                    gen.init(size, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CustomString[] items = gen.generateCustomArray(size);
                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<CustomString> x;

                            @Override public void run () {
                                x = new HashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<CustomString> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<CustomString> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<CustomString> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<CustomString> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<CustomString> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<CustomString> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new HashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.UnorderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.UnorderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
            }
        }
    }
    public static void mainFloat () {
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
                Float[] items = new Float[size];
                rng.setStateA(size);
                rng.setStateB(~size);
                for (int j = 0; j < size; j++) {
                    items[j] = (rng.nextInt() << 10) * 1024f;// * 0.6180339887498949f;
                }
                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<Float> x;

                            @Override public void run () {
                                x = new HashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<Float> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<Float> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<Float> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<Float> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<Float> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<Float> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<Float, Float> x;

                            @Override public void run () {
                                x = new HashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.UnorderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.UnorderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<Float, Float> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<Float, Float> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<Float, Float> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
            }
        }
    }
    public static void mainInteger () {
        TangleRNG rng = new TangleRNG(-10000L, 1111111L);
        PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(".junk.txt#"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 6; i++) {
            if(i == 5)
                System.setOut(out);
            for (int size : new int[] {1, 10, 100, 1000, 10000, 100000, 1000000}) {
                Integer[] items = new Integer[size];
                rng.setStateA(size);
                rng.setStateB(~size);
                for (int j = 0; j < size; j++) {
                    items[j] = (rng.nextInt() << 10);// * 0.6180339887498949f;
                }

                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<Integer> x;

                            @Override public void run () {
                                x = new HashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<Integer> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<Integer> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<Integer> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<Integer> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<Integer> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<Integer> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new HashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.UnorderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.UnorderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, 0.8f);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
            }
        }
    }
}