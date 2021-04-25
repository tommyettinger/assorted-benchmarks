package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.utils.*;
import com.github.tommyettinger.ds.IndexedMap;
import com.github.tommyettinger.ds.IndexedSet;
import com.sun.management.ThreadMXBean;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import de.heidelberg.pvs.container_bench.generators.StringDictionaryGenerator;
import de.heidelberg.pvs.container_bench.generators.StringDictionaryGenerator.CustomString;
import squidpony.squidmath.UnorderedMap;
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
        return ((ThreadMXBean)ManagementFactory.getThreadMXBean())
            .getThreadAllocatedBytes(Thread.currentThread().getId());
    }
    
    public static void main(String[] args) {
        mainCharSeq();
        mainString();
        mainFloat();
        mainInteger();
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
                String[] items2 = gen.generateArrayAltered(size);

                
                
                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<String> x;

                            @Override public void run () {
                                x = new HashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<String> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<String> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectSet<String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            ObjectSetX<String> x;

                            @Override public void run () {
                                x = new ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<String> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<String> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<String> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectOrderedSet<String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectOrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            OrderedSetX<String> x;

                            @Override public void run () {
                                x = new OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<String> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<String, String> x;

                            @Override public void run () {
                                x = new HashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            UnorderedMap<String, String> x;

                            @Override public void run () {
                                x = new UnorderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<String, String> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectMap<String, String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            ObjectMapX<String, String> x;

                            @Override public void run () {
                                x = new ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<String, String> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<String, String> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<String, String> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectOrderedMap<String, String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectOrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            OrderedMapX<String, String> x;

                            @Override public void run () {
                                x = new OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<String, String> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
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
                CustomString[] items2 = gen.generateCustomArrayAltered(size);



                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<CustomString> x;

                            @Override public void run () {
                                x = new HashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<CustomString> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<CustomString> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS ObjectSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectSet<CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            ObjectSetX<CustomString> x;

                            @Override public void run () {
                                x = new ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<CustomString> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<CustomString> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<CustomString> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS OrderedSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectOrderedSet<CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectOrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            OrderedSetX<CustomString> x;

                            @Override public void run () {
                                x = new OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<CustomString> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new HashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            UnorderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new UnorderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            ObjectMapX<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectOrderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectOrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            OrderedMapX<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
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
                Float[] items2 = new Float[size];
                rng.setStateA(size);
                rng.setStateB(~size);
                for (int j = 0; j < size; j++) {
                    items[j] = (rng.nextInt() >>> 10) * 1024f;// * 0.6180339887498949f;
                    items2[j] = rng.nextInt() * 0.6180339887498949f;
                }



                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<Float> x;

                            @Override public void run () {
                                x = new HashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<Float> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<Float> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS ObjectSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectSet<Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            ObjectSetX<Float> x;

                            @Override public void run () {
                                x = new ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<Float> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<Float> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<Float> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS OrderedSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectOrderedSet<Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectOrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            OrderedSetX<Float> x;

                            @Override public void run () {
                                x = new OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<Float> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<Float, Float> x;

                            @Override public void run () {
                                x = new HashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            UnorderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new UnorderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<Float, Float> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS ObjectMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectMap<Float, Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            ObjectMapX<Float, Float> x;

                            @Override public void run () {
                                x = new ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<Float, Float> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS OrderedMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectOrderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectOrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            OrderedMapX<Float, Float> x;

                            @Override public void run () {
                                x = new OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<Float, Float> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
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
                Integer[] items2 = new Integer[size];
                rng.setStateA(size);
                rng.setStateB(~size);
                for (int j = 0; j < size; j++) {
                    items[j] = (rng.nextInt() << 10);// * 0.6180339887498949f;
                    items2[j] = (rng.nextInt());// * 0.6180339887498949f;
                }



                System.out.println("UNORDERED SET");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK HashSet", size,
                        measure(new Runnable() {
                            HashSet<Integer> x;

                            @Override public void run () {
                                x = new HashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib UnorderedSet", size,
                        measure(new Runnable() {
                            UnorderedSet<Integer> x;

                            @Override public void run () {
                                x = new UnorderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX ObjectSet", size,
                        measure(new Runnable() {
                            ObjectSet<Integer> x;

                            @Override public void run () {
                                x = new ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS ObjectSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectSet<Integer> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            ObjectSetX<Integer> x;

                            @Override public void run () {
                                x = new ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED SET:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK LinkedHashSet", size,
                        measure(new Runnable() {
                            LinkedHashSet<Integer> x;

                            @Override public void run () {
                                x = new LinkedHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib OrderedSet", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedSet<Integer> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX OrderedSet", size,
                        measure(new Runnable() {
                            OrderedSet<Integer> x;

                            @Override public void run () {
                                x = new OrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS OrderedSet", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectOrderedSet<Integer> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectOrderedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            OrderedSetX<Integer> x;

                            @Override public void run () {
                                x = new OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Atlantis IndexedSet", size,
                        measure(new Runnable() {
                            IndexedSet<Integer> x;

                            @Override public void run () {
                                x = new IndexedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));

                System.out.println("UNORDERED MAP");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK HashMap", size,
                        measure(new Runnable() {
                            HashMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new HashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib UnorderedMap", size,
                        measure(new Runnable() {
                            UnorderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new UnorderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS ObjectMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            ObjectMapX<Integer, Integer> x;

                            @Override public void run () {
                                x = new ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "SquidLib OrderedMap", size,
                        measure(new Runnable() {
                            squidpony.squidmath.OrderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new squidpony.squidmath.OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS OrderedMap", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.ObjectObjectOrderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.ObjectObjectOrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            OrderedMapX<Integer, Integer> x;

                            @Override public void run () {
                                x = new OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items2[j]);
                            }
                        }));

            }
        }
    }
}
