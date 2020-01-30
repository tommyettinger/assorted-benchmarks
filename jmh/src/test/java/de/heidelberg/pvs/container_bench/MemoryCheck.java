package de.heidelberg.pvs.container_bench;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Tommy Ettinger on 1/28/2020.
 */
public class MemoryCheck {
	public static void main(String[] args)
	{
		StringDictionaryGenerator gen = new StringDictionaryGenerator();
		for(int size : new int[]{1, 10, 100, 1000, 10000, 100000, 1000000}) {
			try {
				gen.init(size, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
				ObjectSet<String> set = new ObjectSet<>(size);
				set.addAll(words);
				System.out.printf("GDX ObjectSet, %7d Strings:\n", size);
				evaluate(set);
				System.out.println("----------------------------------------");
			}
			{
				com.github.tommyettinger.merry.lp.ObjectSet<String> set = new com.github.tommyettinger.merry.lp.ObjectSet<>(size);
				set.addAll(words);
				System.out.printf("Merry ObjectSet, %7d Strings:\n", size);
				evaluate(set);
				System.out.println("----------------------------------------");
			}
			System.out.println("INSERTION-ORDERED:");
			System.out.println("----------------------------------------");
			{
				LinkedHashSet<String> set = new LinkedHashSet<>(size);
				set.addAll(wordList);
				System.out.printf("JDK LinkedHashSet, %7d Strings:\n", size);
				evaluate(set);
				System.out.println("----------------------------------------");
			}
			{
				OrderedSet<String> set = new OrderedSet<>(size);
				set.addAll(words);
				System.out.printf("GDX OrderedSet, %7d Strings:\n", size);
				evaluate(set);
				System.out.println("----------------------------------------");
			}
			{
				com.github.tommyettinger.merry.lp.OrderedSet<String> set = new com.github.tommyettinger.merry.lp.OrderedSet<>(size);
				set.addAll(words);
				System.out.printf("Merry OrderedSet, %7d Strings:\n", size);
				evaluate(set);
				System.out.println("----------------------------------------");
			}
		}
	}
	public static void evaluate(Object o)
	{
		System.out.println(org.openjdk.jol.info.GraphLayout.parseInstance(o).totalSize());
//		System.out.println(org.openjdk.jol.info.ClassLayout.parseInstance(o).instanceSize());
	}
}
