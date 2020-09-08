package de.heidelberg.pvs.container_bench.generators;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Wordlist {
	
	private static final int DEFAULT_SEED = -1;

	/** File name of our input data. */
	public static final String FILENAME = "word_list.txt.gz";

	public static List<String> loadWords(int size, int seed) throws IOException {
		// Load the word list
		try (InputStream is = new FileInputStream(FILENAME);
			InputStream gi = new GZIPInputStream(is);
			Reader r = new InputStreamReader(gi);
			BufferedReader reader = new BufferedReader(r)) {

			// Always read at least the word list in full
			final int stop = 235971;
			ArrayList<String> words = new ArrayList<>(stop);

			String line;
			int len;
			double repeats;
			char c;
			while ((line = reader.readLine()) != null) {
				len = line.length();
				for (int i = 0; i < len; i++) {
					c = line.charAt(i);
					if(c < 'a' || c > 'z')
					{
						len += 3;
						break;
					}
				}
				len *= len;
				repeats = (line.hashCode() & 0xFFF) * 0x0.00FFFp-10 * size / (len * len) + 1.5;
				for (int i = 0; i < repeats; i++) {
					words.add(line);
				}
			}

			if (seed != DEFAULT_SEED) {
				final TangleRNG rng = new TangleRNG(seed);
				final int n = words.size();
				for (int i = n; i > 1; i--) {
					Collections.swap(words, rng.nextInt(i), i - 1);
				}
			}
			words.subList(size, words.size()).clear(); // Truncate
			return words;
		}
	}

	public static Set<String> loadWordSet(int size, int seed) throws IOException {

		// Load the word list
		try (InputStream is = new FileInputStream(FILENAME);
			InputStream gi = new GZIPInputStream(is);
			Reader r = new InputStreamReader(gi);
			BufferedReader reader = new BufferedReader(r)) {
			
			ArrayList<String> words = new ArrayList<>(size);

			String line;
			while ((line = reader.readLine()) != null) {
				words.add(line);
			}
			final int n = words.size();
			if (seed != DEFAULT_SEED) {
				final TangleRNG rng = new TangleRNG(seed);
				for (int i = n; i > 1; i--) {
					Collections.swap(words, rng.nextInt(i), i - 1);
				}
			}
			HashSet<String> set = new HashSet<>(size);
			for (int i = 0; i < size;) {
				for (int j = 0; j < n && i < size; j++) {
					set.add(words.get(j) + i++);
				}
			}
			return set;
		}
	}

	public static int[] loadInts(int size, int seed, int mask) throws IOException {
		List<String> words = loadWords(size, seed);
		int[] data = new int[words.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = words.get(i).hashCode() & mask;
		}
		return data;
	}
}
