package de.heidelberg.pvs.container_bench;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import de.heidelberg.pvs.container_bench.generators.dictionary.IntegerDictionaryGenerator;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.IOException;
import java.util.*;

/**
 * Created by Tommy Ettinger on 1/28/2020.
 */
public class MemoryCheck {
	public static void main(String[] args) {
		{
			StringDictionaryGenerator gen = new StringDictionaryGenerator();
			for (int size : new int[]{1, 10, 100, 1000, 10000, 100000, 1000000}) {
				try {
					gen.init(size, -1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.gc();
				String[] words = gen.generateArray(size);
				List<String> wordList = Arrays.asList(words);
				System.out.println("LIST");
				System.out.println("----------------------------------------");
				{
					ArrayList<String> set = new ArrayList<>(size);
					set.addAll(wordList);
					System.out.printf("JDK ArrayList, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					Array<String> set = new Array<>(size);
					set.addAll(words);
					System.out.printf("GDX Array, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				System.out.println("UNORDERED");
				System.out.println("----------------------------------------");
				{
					HashSet<String> set = new HashSet<>(size);
					set.addAll(wordList);
					System.out.printf("JDK HashSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ObjectOpenHashSet<String> set = new ObjectOpenHashSet<>(size, 0.8f);
					set.addAll(wordList);
					System.out.printf("FastUtil Set, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ObjectSet<String> set = new ObjectSet<>(size);
					set.addAll(words);
					System.out.printf("GDX ObjectSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ds.merry.ObjectSet<String> set = new ds.merry.ObjectSet<>(size);
					set.addAll(words);
					System.out.printf("Merry ObjectSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				System.out.println("INSERTION-ORDERED:");
				System.out.println("----------------------------------------");
				{
					LinkedHashSet<String> set = new LinkedHashSet<>(size, 0.8f);
					set.addAll(wordList);
					System.out.printf("JDK LinkedHashSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ObjectLinkedOpenHashSet<String> set = new ObjectLinkedOpenHashSet<>(size, 0.8f);
					set.addAll(wordList);
					System.out.printf("FastUtil Linked Set, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					OrderedSet<String> set = new OrderedSet<>(size, 0.8f);
					set.addAll(words);
					System.out.printf("GDX OrderedSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ds.merry.OrderedSet<String> set = new ds.merry.OrderedSet<>(size, 0.8f);
					set.addAll(words);
					System.out.printf("Merry OrderedSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					com.github.tommyettinger.ds.IndexedSet<String> set = new com.github.tommyettinger.ds.IndexedSet<>(size, 0.8f);
					set.addAll(words);
					System.out.printf("Atlantis IndexedSet, %7d Strings:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}

			}
		}
		{
			IntegerDictionaryGenerator gen = new IntegerDictionaryGenerator();
			for (int size : new int[]{1, 10, 100, 1000, 10000, 100000, 1000000}) {
				try {
					gen.init(size, -1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.gc();
				int[] raw = gen.generateIntArray(size);
				Integer[] numbers = new Integer[size];
				List<Integer> numberList = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					numberList.add(numbers[i] = raw[i] << 10);
				}
				System.out.println("LIST");
				System.out.println("----------------------------------------");
				{
					ArrayList<Integer> set = new ArrayList<>(size);
					set.addAll(numberList);
					System.out.printf("JDK ArrayList, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					Array<Integer> set = new Array<>(size);
					set.addAll(numbers);
					System.out.printf("GDX Array, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				System.out.println("UNORDERED");
				System.out.println("----------------------------------------");
				{
					HashSet<Integer> set = new HashSet<>(size);
					set.addAll(numberList);
					System.out.printf("JDK HashSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ObjectOpenHashSet<Integer> set = new ObjectOpenHashSet<>(size, 0.8f);
					set.addAll(numberList);
					System.out.printf("FastUtil Set, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ObjectSet<Integer> set = new ObjectSet<>(size);
					set.addAll(numbers);
					System.out.printf("GDX ObjectSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ds.merry.ObjectSet<Integer> set = new ds.merry.ObjectSet<>(size);
					set.addAll(numbers);
					System.out.printf("Merry ObjectSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				System.out.println("INSERTION-ORDERED:");
				System.out.println("----------------------------------------");
				{
					LinkedHashSet<Integer> set = new LinkedHashSet<>(size, 0.8f);
					set.addAll(numberList);
					System.out.printf("JDK LinkedHashSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ObjectLinkedOpenHashSet<Integer> set = new ObjectLinkedOpenHashSet<>(size, 0.8f);
					set.addAll(numberList);
					System.out.printf("FastUtil Linked Set, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					OrderedSet<Integer> set = new OrderedSet<>(size, 0.8f);
					set.addAll(numbers);
					System.out.printf("GDX OrderedSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					ds.merry.OrderedSet<Integer> set = new ds.merry.OrderedSet<>(size, 0.8f);
					set.addAll(numbers);
					System.out.printf("Merry OrderedSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}
				{
					com.github.tommyettinger.ds.IndexedSet<Integer> set = new com.github.tommyettinger.ds.IndexedSet<>(size, 0.8f);
					set.addAll(numbers);
					System.out.printf("Atlantis IndexedSet, %7d Integers:\n", size);
					evaluate(set);
					System.out.println("----------------------------------------");
				}

			}
		}
	}
	public static void evaluate(Object o)
	{
		System.out.println(org.openjdk.jol.info.GraphLayout.parseInstance(o).totalSize());
//		System.out.println(org.openjdk.jol.info.ClassLayout.parseInstance(o).instanceSize());
	}
}
