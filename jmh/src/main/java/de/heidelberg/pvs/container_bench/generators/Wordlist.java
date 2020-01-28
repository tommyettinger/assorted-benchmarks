package de.heidelberg.pvs.container_bench.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Wordlist {
	
	private static final int DEFAULT_SEED = -1;

	/** File name of our input data. */
	public static final String HUGE_FILENAME = "enwiki-100m.txt.gz";
	public static final String FILENAME = "word_list.txt.gz";

	public static List<String> loadWiki(int size, int seed) throws IOException {

		// Load the Wikipedia word data.
		try (InputStream is = ClassLoader.getSystemResourceAsStream(HUGE_FILENAME);
			InputStream gi = new GZIPInputStream(is);
			Reader r = new InputStreamReader(gi);
			BufferedReader reader = new BufferedReader(r)) {

			// Always read twice as much the size
			final int stop = size << 1;
			List<String> words = new ArrayList<>(stop);

			String line;
			Matcher m = Pattern.compile("[\\w–-]+", Pattern.UNICODE_CHARACTER_CLASS).matcher("");
			while ((line = reader.readLine()) != null && words.size() < stop) {
				m.reset(line);
				while (m.find()) {
					String word = m.group();
					word.hashCode(); // Precompute hashcode
					words.add(word);
				}
			}

			if (seed != DEFAULT_SEED) {
				Collections.shuffle(words, new Random(seed));
			}
			words.subList(size, words.size()).clear(); // Truncate
			return words;
		}
	}

	public static List<String> loadWords(int size, int seed) throws IOException {

		// Load the word list
		try (InputStream is = ClassLoader.getSystemResourceAsStream(FILENAME);
			InputStream gi = new GZIPInputStream(is);
			Reader r = new InputStreamReader(gi);
			BufferedReader reader = new BufferedReader(r)) {

			// Always read twice as much the size
			final int stop = size << 1;
			ArrayList<String> words = new ArrayList<>(stop);

			String line;
			int repeats, len;
			char c;
			while ((line = reader.readLine()) != null && words.size() < stop) {
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
				repeats = (7777777 - (line.hashCode() & 0x3FFFF)) / (len * len) + 1;
				for (int i = 0; i < repeats; i++) {
					words.add(line);
				}
			}

			if (seed != DEFAULT_SEED) {
				//Collections.shuffle(words, new Random(seed));
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

	public static int[] loadInts(int size, int seed, int mask) throws IOException {
		List<String> words = loadWords(size, seed);
		int[] data = new int[words.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = words.get(i).hashCode() & mask;
		}
		return data;
	}
}
