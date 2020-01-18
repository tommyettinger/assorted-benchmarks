package com.github.tommyettinger;

import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import squidpony.StringKit;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.SilkRNG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * There aren't many real words with hash collisions in an English word list.
 * Using a FreeBSD /usr/share/dict file, https://svnweb.freebsd.org/base/head/share/dict/web2?view=co ,
 * agarwal collides with hazardless,
 * dentinalgia collides with poised,
 * hypoplankton collides with unheavenly,
 * fineable collides with unapprehending,
 * Clinopodium collides with inextinguishably, and
 * acouasm collides with kindergartener.
 * <br>
 * On the other hand, with random alphanumeric "words" of length 5, collisions can be more frequent.
 * All of these "words" collide, 14 total: l1nqp kPoRp jpOqp jpOs2 jop3p l2Os2 l2Q4Q l2Q3p l2PSQ kR0rQ l1p3p l1nrQ l2OrQ kQPRp
 * All of these "words" collide, 10 total: oSUsX nrW4w nrVSw p57Sw p3v5X oSVU9 oRuSw oSVTX nqtsX nqv5X
 * All of these "words" collide, 10 total: E0qpT E1SR5 Cp52T E24QT E24R5 E0rR5 CnrPs DOs2T DPSR5 E0rPs
 * All of these "words" collide, 10 total: o2bUp o3D6p mpbW2 nRCUp nQav2 mqBv2 o3Bv2 o2c7Q nRCW2 nRCVQ
 * <br>
 * More sophisticated techniques can generate Strings given a target hashCode() to collide with.
 * <br>
 * Created by Tommy Ettinger on 1/14/2020.
 */
public class ProblemWordFinder {
	public static final boolean USE_LIST = false;
	public static void main(String[] args)
	{
		final String[] words;
		if(USE_LIST) {
			String book = "";
			try {
				book = new String(Files.readAllBytes(Paths.get("res/word_list.txt")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			words = StringKit.split(book, "\n");
		}
		else 
		{
			// will take a short while to run with this LIMIT, maybe a minute or two
			final int LIMIT = 0x2000000;
			
			HashSet<String> wordSet = new HashSet<>(LIMIT);
//			SilkRNG rng = new SilkRNG(0);
//			SilkRNG rng = new SilkRNG(1234567890);
			SilkRNG rng = new SilkRNG();
			System.out.println(rng.getState());
			char[] options = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".toCharArray();
			char[] w = new char[5];
			for (int i = 0; i < LIMIT; i++) {
				int bits = rng.nextInt();
				w[0] = options[bits & 63];
				w[1] = options[bits >>> 6 & 63];
				w[2] = options[bits >>> 12 & 63];
				w[3] = options[bits >>> 18 & 63];
				w[4] = options[bits >>> 24 & 63];
				wordSet.add(String.valueOf(w));
			}
			words = wordSet.toArray(new String[0]);
		}
		IntMap<String> ratings = new IntMap<>(words.length);
		IntIntMap counts = new IntIntMap(words.length);
		for(String s : words)
		{
			int hc = s.hashCode();
			String existing = ratings.get(hc, "");
			if(existing.isEmpty())
				ratings.put(hc, s + ' ');
			else
				ratings.put(hc, existing + s + ' ');
			counts.getAndIncrement(hc, 0, 1);
		}
		
		IntIntMap.Keys k = counts.keys();
		IntIntMap.Values v = counts.values();
		OrderedMap<Integer, Integer> sorting = new OrderedMap<>(counts.size);
		while (k.hasNext && v.hasNext) {
			sorting.put(k.next(), v.next());
		}
		sorting.sortByValue(Comparator.<Integer>naturalOrder().reversed());
		StringBuilder sb = new StringBuilder(0xC0000);
		final int sz = sorting.size();
		int count = 0;
		for (int i = 0, current; i < sz; i++) {
			if((current = sorting.getAt(i)) < 2 || (count += current) > 0x2000F)
				break;
			if(i < 100)
				System.out.println(current + ": " + ratings.get(sorting.keyAt(i)));
			sb.append(ratings.get(sorting.keyAt(i)));
		}
		System.out.println(count);
		try {
			Files.write(Paths.get("res/problem_words.txt"), Collections.singletonList(sb), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
