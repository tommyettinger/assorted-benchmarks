package de.heidelberg.pvs.container_bench;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.github.tommyettinger.ds.IndexedMap;
import com.github.tommyettinger.ds.IndexedSet;
import com.koloboke.collect.set.hash.HashObjSet;
import de.heidelberg.pvs.container_bench.factories.LoadFactor;
import de.heidelberg.pvs.container_bench.generators.TangleRNG;
import de.heidelberg.pvs.container_bench.generators.Wordlist;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator.CustomString;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.agrona.collections.Object2ObjectHashMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

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
                System.out.println("LIST");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK ArrayList", size,
                        measure(new Runnable() {
                            ArrayList<String> x;

                            @Override public void run () {
                                x = new ArrayList<>(size);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX Array", size, measure(new Runnable() {
                    Array<String> x;

                    @Override public void run () {
                        x = new Array<>(size);
                        for (int j = 0; j < size; j++) x.add(items[j]);
                    }
                }));

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
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Set", size,
                        measure(new Runnable() {
                            ObjectOpenHashSet<String> x;

                            @Override public void run () {
                                x = new ObjectOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Koloboke Set", size,
                        measure(new Runnable() {
                            HashObjSet<String> x;

                            @Override public void run () {
                                x = com.koloboke.collect.set.hash.HashObjSets.newMutableSet(size);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Eclipse Set", size,
                        measure(new Runnable() {
                            org.eclipse.collections.impl.set.mutable.UnifiedSet<String> x;

                            @Override public void run () {
                                x = new org.eclipse.collections.impl.set.mutable.UnifiedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Agrona Set", size,
                        measure(new Runnable() {
                            org.agrona.collections.ObjectHashSet<String> x;

                            @Override public void run () {
                                x = new org.agrona.collections.ObjectHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.ObjectSetX<String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Linked Set", size,
                        measure(new Runnable() {
                            ObjectLinkedOpenHashSet<String> x;

                            @Override public void run () {
                                x = new ObjectLinkedOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.OrderedSetX<String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Map", size,
                        measure(new Runnable() {
                            Object2ObjectOpenHashMap<String, String> x;

                            @Override public void run () {
                                x = new Object2ObjectOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Koloboke Map", size,
                        measure(new Runnable() {
                            com.koloboke.collect.map.hash.HashObjObjMap<String, String> x;

                            @Override public void run () {
                                x = com.koloboke.collect.map.hash.HashObjObjMaps.newMutableMap(size);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Eclipse Map", size,
                        measure(new Runnable() {
                            UnifiedMap<String, String> x;

                            @Override public void run () {
                                x = new UnifiedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Agrona Map", size,
                        measure(new Runnable() {
                            org.agrona.collections.Object2ObjectHashMap<String, String> x;

                            @Override public void run () {
                                x = new Object2ObjectHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<String, String> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.ObjectMapX<String, String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<String, String> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Linked Map", size,
                        measure(new Runnable() {
                            Object2ObjectLinkedOpenHashMap<String, String> x;

                            @Override public void run () {
                                x = new Object2ObjectLinkedOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<String, String> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.OrderedMapX<String, String> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<String, String> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.println("LIST");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK ArrayList", size,
                        measure(new Runnable() {
                            ArrayList<CustomString> x;

                            @Override public void run () {
                                x = new ArrayList<>(size);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX Array", size, measure(new Runnable() {
                    Array<CustomString> x;

                    @Override public void run () {
                        x = new Array<>(size);
                        for (int j = 0; j < size; j++) x.add(items[j]);
                    }
                }));

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
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "FastUtil Set", size,
                        measure(new Runnable() {
                            ObjectOpenHashSet<CustomString> x;

                            @Override public void run () {
                                x = new ObjectOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Koloboke Set", size,
                        measure(new Runnable() {
                            HashObjSet<CustomString> x;

                            @Override public void run () {
                                x = com.koloboke.collect.set.hash.HashObjSets.newMutableSet(size);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Eclipse Set", size,
                        measure(new Runnable() {
                            org.eclipse.collections.impl.set.mutable.UnifiedSet<CustomString> x;

                            @Override public void run () {
                                x = new org.eclipse.collections.impl.set.mutable.UnifiedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Agrona Set", size,
                        measure(new Runnable() {
                            org.agrona.collections.ObjectHashSet<CustomString> x;

                            @Override public void run () {
                                x = new org.agrona.collections.ObjectHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.ObjectSetX<CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "FastUtil Linked Set", size,
                        measure(new Runnable() {
                            ObjectLinkedOpenHashSet<CustomString> x;

                            @Override public void run () {
                                x = new ObjectLinkedOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.OrderedSetX<CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "FastUtil Map", size,
                        measure(new Runnable() {
                            Object2ObjectOpenHashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new Object2ObjectOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Koloboke Map", size,
                        measure(new Runnable() {
                            com.koloboke.collect.map.hash.HashObjObjMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = com.koloboke.collect.map.hash.HashObjObjMaps.newMutableMap(size);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Eclipse Map", size,
                        measure(new Runnable() {
                            UnifiedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new UnifiedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Agrona Map", size,
                        measure(new Runnable() {
                            org.agrona.collections.Object2ObjectHashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new Object2ObjectHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.ObjectMapX<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "FastUtil Linked Map", size,
                        measure(new Runnable() {
                            Object2ObjectLinkedOpenHashMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new Object2ObjectLinkedOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.OrderedMapX<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d CustomStrings: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<CustomString, CustomString> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
            }
        }
    }
    public static void mapStringInt () {
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
                Set<String> strings = new HashSet<>(size, LoadFactor.LOAD_FACTOR);
                try {
                    strings.addAll(Wordlist.loadWordSet(size,1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] items = new String[strings.size()];
                strings.toArray(items);

                System.out.println("UNORDERED BOXED");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK HashMap", size,
                    measure(new Runnable() {
                        HashMap<String, Integer> x;

                        @Override public void run () {
                            x = new HashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Map", size,
                    measure(new Runnable() {
                        Object2ObjectOpenHashMap<String, Integer> x;

                        @Override public void run () {
                            x = new Object2ObjectOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                    measure(new Runnable() {
                        ObjectMap<String, Integer> x;

                        @Override public void run () {
                            x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.ds.x.ObjectMapX<String, Integer> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.ds.x.ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));

                System.out.println("UNORDERED PRIMITIVE");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Primitive Map", size,
                    measure(new Runnable() {
                        Object2IntOpenHashMap<String> x;

                        @Override public void run () {
                            x = new Object2IntOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX ObjectIntMap", size,
                    measure(new Runnable() {
                        ObjectIntMap<String> x;

                        @Override public void run () {
                            x = new ObjectIntMap<>(size);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.println("INSERTION-ORDERED BOXED:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                    measure(new Runnable() {
                        LinkedHashMap<String, Integer> x;

                        @Override public void run () {
                            x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Linked Map", size,
                    measure(new Runnable() {
                        Object2ObjectLinkedOpenHashMap<String, Integer> x;

                        @Override public void run () {
                            x = new Object2ObjectLinkedOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                    measure(new Runnable() {
                        OrderedMap<String, Integer> x;

                        @Override public void run () {
                            x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.ds.x.OrderedMapX<String, Integer> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.ds.x.OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
                System.out.println("INSERTION-ORDERED PRIMITIVE:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "FastUtil Linked Primitive Map", size,
                    measure(new Runnable() {
                        Object2IntLinkedOpenHashMap<String> x;

                        @Override public void run () {
                            x = new Object2IntLinkedOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
                        }
                    }));
//                System.out.printf("%30s, %7d Strings: %d\n----------------------------------------\n", "JDKGDXDS ObjectIntOrderedMapX", size,
//                    measure(new Runnable() {
//                        com.github.tommyettinger.ds.x.ObjectIntOrderedMapX<String> x;
//
//                        @Override public void run () {
//                            x = new com.github.tommyettinger.ds.x.ObjectIntOrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
//                            for (int j = 0; j < size; j++) x.put(items[j], items[j].hashCode());
//                        }
//                    }));
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
                System.out.println("LIST");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK ArrayList", size,
                        measure(new Runnable() {
                            ArrayList<Float> x;

                            @Override public void run () {
                                x = new ArrayList<>(size);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX Array", size, measure(new Runnable() {
                    Array<Float> x;

                    @Override public void run () {
                        x = new Array<>(size);
                        for (int j = 0; j < size; j++) x.add(items[j]);
                    }
                }));

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
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "FastUtil Set", size,
                        measure(new Runnable() {
                            ObjectOpenHashSet<Float> x;

                            @Override public void run () {
                                x = new ObjectOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Koloboke Set", size,
                        measure(new Runnable() {
                            HashObjSet<Float> x;

                            @Override public void run () {
                                x = com.koloboke.collect.set.hash.HashObjSets.newMutableSet(size);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Eclipse Set", size,
                        measure(new Runnable() {
                            org.eclipse.collections.impl.set.mutable.UnifiedSet<Float> x;

                            @Override public void run () {
                                x = new org.eclipse.collections.impl.set.mutable.UnifiedSet<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.add(items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Agrona Set", size,
                        measure(new Runnable() {
                            org.agrona.collections.ObjectHashSet<Float> x;

                            @Override public void run () {
                                x = new org.agrona.collections.ObjectHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.ObjectSetX<Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "FastUtil Linked Set", size,
                        measure(new Runnable() {
                            ObjectLinkedOpenHashSet<Float> x;

                            @Override public void run () {
                                x = new ObjectLinkedOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.OrderedSetX<Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "FastUtil Map", size,
                        measure(new Runnable() {
                            Object2ObjectOpenHashMap<Float, Float> x;

                            @Override public void run () {
                                x = new Object2ObjectOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Koloboke Map", size,
                        measure(new Runnable() {
                            com.koloboke.collect.map.hash.HashObjObjMap<Float, Float> x;

                            @Override public void run () {
                                x = com.koloboke.collect.map.hash.HashObjObjMaps.newMutableMap(size);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Eclipse Map", size,
                        measure(new Runnable() {
                            UnifiedMap<Float, Float> x;

                            @Override public void run () {
                                x = new UnifiedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Agrona Map", size,
                        measure(new Runnable() {
                            org.agrona.collections.Object2ObjectHashMap<Float, Float> x;

                            @Override public void run () {
                                x = new Object2ObjectHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                        measure(new Runnable() {
                            ObjectMap<Float, Float> x;

                            @Override public void run () {
                                x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.ObjectMapX<Float, Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                        measure(new Runnable() {
                            LinkedHashMap<Float, Float> x;

                            @Override public void run () {
                                x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "FastUtil Linked Map", size,
                        measure(new Runnable() {
                            Object2ObjectLinkedOpenHashMap<Float, Float> x;

                            @Override public void run () {
                                x = new Object2ObjectLinkedOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<Float, Float> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                        measure(new Runnable() {
                            com.github.tommyettinger.ds.x.OrderedMapX<Float, Float> x;

                            @Override public void run () {
                                x = new com.github.tommyettinger.ds.x.OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Floats: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<Float, Float> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
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
                
                System.out.println("LIST");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK ArrayList", size,
                    measure(new Runnable() {
                        ArrayList<Integer> x;

                        @Override public void run () {
                            x = new ArrayList<>(size);
                            for (int j = 0; j < size; j++) x.add(items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX Array", size, measure(new Runnable() {
                    Array<Integer> x;

                    @Override public void run () {
                        x = new Array<>(size);
                        for (int j = 0; j < size; j++) x.add(items[j]);
                    }
                }));

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
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "FastUtil Set", size,
                    measure(new Runnable() {
                        ObjectOpenHashSet<Integer> x;

                        @Override public void run () {
                            x = new ObjectOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.add(items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Koloboke Set", size,
                    measure(new Runnable() {
                        HashObjSet<Integer> x;

                        @Override public void run () {
                            x = com.koloboke.collect.set.hash.HashObjSets.newMutableSet(size);
                            for (int j = 0; j < size; j++) x.add(items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Eclipse Set", size,
                    measure(new Runnable() {
                        org.eclipse.collections.impl.set.mutable.UnifiedSet<Integer> x;

                        @Override public void run () {
                            x = new org.eclipse.collections.impl.set.mutable.UnifiedSet<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.add(items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Agrona Set", size,
                    measure(new Runnable() {
                        org.agrona.collections.ObjectHashSet<Integer> x;

                        @Override public void run () {
                            x = new org.agrona.collections.ObjectHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS ObjectSetX", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.ds.x.ObjectSetX<Integer> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.ds.x.ObjectSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "FastUtil Linked Set", size,
                    measure(new Runnable() {
                        ObjectLinkedOpenHashSet<Integer> x;

                        @Override public void run () {
                            x = new ObjectLinkedOpenHashSet<>(size, LoadFactor.LOAD_FACTOR);
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
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS OrderedSetX", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.ds.x.OrderedSetX<Integer> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.ds.x.OrderedSetX<>(size, LoadFactor.LOAD_FACTOR);
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
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "FastUtil Map", size,
                    measure(new Runnable() {
                        Object2ObjectOpenHashMap<Integer, Integer> x;

                        @Override public void run () {
                            x = new Object2ObjectOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Koloboke Map", size,
                    measure(new Runnable() {
                        com.koloboke.collect.map.hash.HashObjObjMap<Integer, Integer> x;

                        @Override public void run () {
                            x = com.koloboke.collect.map.hash.HashObjObjMaps.newMutableMap(size);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Eclipse Map", size,
                    measure(new Runnable() {
                        UnifiedMap<Integer, Integer> x;

                        @Override public void run () {
                            x = new UnifiedMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Agrona Map", size,
                    measure(new Runnable() {
                        org.agrona.collections.Object2ObjectHashMap<Integer, Integer> x;

                        @Override public void run () {
                            x = new Object2ObjectHashMap<>(size, LoadFactor.LOAD_FACTOR); // agrona's default load factor
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX ObjectMap", size,
                    measure(new Runnable() {
                        ObjectMap<Integer, Integer> x;

                        @Override public void run () {
                            x = new ObjectMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS ObjectMapX", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.ds.x.ObjectMapX<Integer, Integer> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.ds.x.ObjectMapX<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.println("INSERTION-ORDERED MAP:");
                System.out.println("----------------------------------------");
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDK LinkedHashMap", size,
                    measure(new Runnable() {
                        LinkedHashMap<Integer, Integer> x;

                        @Override public void run () {
                            x = new LinkedHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "FastUtil Linked Map", size,
                    measure(new Runnable() {
                        Object2ObjectLinkedOpenHashMap<Integer, Integer> x;

                        @Override public void run () {
                            x = new Object2ObjectLinkedOpenHashMap<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "GDX OrderedMap", size,
                        measure(new Runnable() {
                            OrderedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new OrderedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "JDKGDXDS OrderedMapX", size,
                    measure(new Runnable() {
                        com.github.tommyettinger.ds.x.OrderedMapX<Integer, Integer> x;

                        @Override public void run () {
                            x = new com.github.tommyettinger.ds.x.OrderedMapX<>(size, LoadFactor.LOAD_FACTOR);
                            for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                        }
                    }));
                System.out.printf("%30s, %7d Integers: %d\n----------------------------------------\n", "Atlantis IndexedMap", size,
                        measure(new Runnable() {
                            IndexedMap<Integer, Integer> x;

                            @Override public void run () {
                                x = new IndexedMap<>(size, LoadFactor.LOAD_FACTOR);
                                for (int j = 0; j < size; j++) x.put(items[j], items[j]);
                            }
                        }));
            }
        }
    }
}