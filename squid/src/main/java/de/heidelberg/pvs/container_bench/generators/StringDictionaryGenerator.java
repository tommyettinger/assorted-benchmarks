package de.heidelberg.pvs.container_bench.generators;

import com.github.tommyettinger.ds.ObjectOrderedSet;
import com.github.tommyettinger.random.WhiskerRandom;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StringDictionaryGenerator implements ElementGenerator<String> {
	
	protected ObjectOrderedSet<String> words;
	
	protected WhiskerRandom generator;
	
	protected int seed; 
	protected int poolSize;
	
	@Override
	public void init(int size, int seed) throws IOException {
		this.seed = seed;
		this.poolSize = size;
		
		generator = new WhiskerRandom(seed);
		// Read all -> might be too expensive
		words = Wordlist.loadWordSet(size, seed);
	}

	@Override
	public int generateIndex(int range) {
		return generator.nextInt(range);
	}
	
	@Override
	public String[] generateArray(int arraySize) {
		String[] array = new String[arraySize];
		return words.order().subList(0, arraySize).toArray(array);
	}
	
	public List<String> generateList(int listSize) {
		return words.order().subList(0, listSize);
	}

	public CustomString[] generateCustomArray(int arraySize) {
		CustomString[] array = new CustomString[arraySize];
		for (int i = 0; i < arraySize; i++) {
			array[i] = new CustomString(words.getAt(i));
		}
		return array;
	}

	public String[] generateArrayAltered(int arraySize) {
		String[] array = new String[arraySize];
		for (int i = 0; i < arraySize; i++) {
			array[i] = (words.getAt(i) + generator.nextInt());
		}
		return array;
	}


	public CustomString[] generateCustomArrayAltered(int arraySize) {
		CustomString[] array = new CustomString[arraySize];
		for (int i = 0; i < arraySize; i++) {
			array[i] = new CustomString(words.getAt(i) + generator.nextInt());
		}
		return array;
	}


	public static class CustomString implements CharSequence {
		private final char[] data;

		private CustomString() {
			data = new char[0];
		}
		public CustomString(String string) {
			data = string.toCharArray();
		}
		
		public CustomString(char[] string, int offset, int length) {
			data = new char[length];
			System.arraycopy(string, offset, data, 0, length);
		}
		
		/**
		 * Returns the length of this string.
		 * The length is equal to the number of <a href="Character.html#unicode">Unicode
		 * code units</a> in the string.
		 *
		 * @return the length of the sequence of characters represented by this
		 *          object.
		 */
		public int length () {
			return data.length;
		}

		/**
		 * Returns the <code>char</code> value at the specified index.  An index ranges from zero
		 * to <tt>length() - 1</tt>.  The first <code>char</code> value of the sequence is at
		 * index zero, the next at index one, and so on, as for array
		 * indexing.
		 *
		 * <p>If the <code>char</code> value specified by the index is a
		 * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the surrogate
		 * value is returned.
		 *
		 * @param index the index of the <code>char</code> value to be returned
		 * @return the specified <code>char</code> value
		 * @throws IndexOutOfBoundsException if the <tt>index</tt> argument is negative or not less than
		 *                                   <tt>length()</tt>
		 */
		public char charAt (int index) {
			return data[index];
		}

		/**
		 * Returns a <code>CharSequence</code> that is a subsequence of this sequence.
		 * The subsequence starts with the <code>char</code> value at the specified index and
		 * ends with the <code>char</code> value at index <tt>end - 1</tt>.  The length
		 * (in <code>char</code>s) of the
		 * returned sequence is <tt>end - start</tt>, so if <tt>start == end</tt>
		 * then an empty sequence is returned.
		 *
		 * @param start the start index, inclusive
		 * @param end   the end index, exclusive
		 * @return the specified subsequence
		 * @throws IndexOutOfBoundsException if <tt>start</tt> or <tt>end</tt> are negative,
		 *                                   if <tt>end</tt> is greater than <tt>length()</tt>,
		 *                                   or if <tt>start</tt> is greater than <tt>end</tt>
		 */
		public CharSequence subSequence (int start, int end) {
			return new CustomString(data, start, end - start);
		}

		public boolean equals (Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			CustomString that = (CustomString)o;

			return Arrays.equals(data, that.data);
		}

		public int hashCode () {
			int result = 0x1A976FDF, z = 0x60642E25;
			final int len = data.length;
			for (int i = 0; i < len; i++) {
				result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
				z ^= (result = (result << 20 | result >>> 12));
			}
			result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
			result = (result ^ result >>> 15) * 0xFF51D;
			result = (result ^ result >>> 15) * 0xC4CEB;
			return result ^ result >>> 15;
		}
	}

}
