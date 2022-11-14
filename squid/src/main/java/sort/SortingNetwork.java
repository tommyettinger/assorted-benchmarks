/*
 * Copyright (c) 2022 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package sort;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public final class SortingNetwork {
	private SortingNetwork() {
	}

	private static <K> void swap (final K[] items, final int first, final int second) {
		final K firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	/**
	 * One Compare-Exchange node.
	 * @param items
	 * @param first
	 * @param second
	 * @param comp
	 * @param <K>
	 */
	private static <K> void ce (final K[] items, final int first, final int second, final Comparator<? super K> comp) {
		final K firstValue = items[first], secondValue = items[second];
		if(comp.compare(firstValue, secondValue) < 0) {
			items[first] = secondValue;
			items[second] = firstValue;
		}
	}

	private static <K> void ce2 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 1, c);
	}

	private static <K> void ce3 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 2, c);
		ce(items, 0, 1, c);
		ce(items, 1, 2, c);
	}

	private static <K> void ce4 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 2, c);
		ce(items, 1, 3, c);
		ce(items, 0, 1, c);
		ce(items, 2, 3, c);
		ce(items, 1, 2, c);
	}

	private static <K> void ce5 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 3, c);
		ce(items, 1, 4, c);
		ce(items, 0, 2, c);
		ce(items, 1, 3, c);
		ce(items, 0, 1, c);
		ce(items, 2, 4, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 2, 3, c);
	}

	private static <K> void ce6 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 5, c);
		ce(items, 1, 3, c);
		ce(items, 2, 4, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 0, 3, c);
		ce(items, 2, 5, c);
		ce(items, 0, 1, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
	}

	private static <K> void ce7 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 6, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 0, 2, c);
		ce(items, 1, 4, c);
		ce(items, 3, 6, c);
		ce(items, 0, 1, c);
		ce(items, 2, 5, c);
		ce(items, 3, 4, c);
		ce(items, 1, 2, c);
		ce(items, 4, 6, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
	}

	private static <K> void ce8 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 2, c);
		ce(items, 1, 3, c);
		ce(items, 4, 6, c);
		ce(items, 5, 7, c);
		ce(items, 0, 4, c);
		ce(items, 1, 5, c);
		ce(items, 2, 6, c);
		ce(items, 3, 7, c);
		ce(items, 0, 1, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 2, 4, c);
		ce(items, 3, 5, c);
		ce(items, 1, 4, c);
		ce(items, 3, 6, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
	}

	private static <K> void ce9 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 3, c);
		ce(items, 1, 7, c);
		ce(items, 2, 5, c);
		ce(items, 4, 8, c);
		ce(items, 0, 7, c);
		ce(items, 2, 4, c);
		ce(items, 3, 8, c);
		ce(items, 5, 6, c);
		ce(items, 0, 2, c);
		ce(items, 1, 3, c);
		ce(items, 4, 5, c);
		ce(items, 7, 8, c);
		ce(items, 1, 4, c);
		ce(items, 3, 6, c);
		ce(items, 5, 7, c);
		ce(items, 0, 1, c);
		ce(items, 2, 4, c);
		ce(items, 3, 5, c);
		ce(items, 6, 8, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
	}

	private static <K> void ce10 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 8, c);
		ce(items, 1, 9, c);
		ce(items, 2, 7, c);
		ce(items, 3, 5, c);
		ce(items, 4, 6, c);
		ce(items, 0, 2, c);
		ce(items, 1, 4, c);
		ce(items, 5, 8, c);
		ce(items, 7, 9, c);
		ce(items, 0, 3, c);
		ce(items, 2, 4, c);
		ce(items, 5, 7, c);
		ce(items, 6, 9, c);
		ce(items, 0, 1, c);
		ce(items, 3, 6, c);
		ce(items, 8, 9, c);
		ce(items, 1, 5, c);
		ce(items, 2, 3, c);
		ce(items, 4, 8, c);
		ce(items, 6, 7, c);
		ce(items, 1, 2, c);
		ce(items, 3, 5, c);
		ce(items, 4, 6, c);
		ce(items, 7, 8, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
	}

	private static <K> void ce11 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 9, c);
		ce(items, 1, 6, c);
		ce(items, 2, 4, c);
		ce(items, 3, 7, c);
		ce(items, 5, 8, c);
		ce(items, 0, 1, c);
		ce(items, 3, 5, c);
		ce(items, 4, 10, c);
		ce(items, 6, 9, c);
		ce(items, 7, 8, c);
		ce(items, 1, 3, c);
		ce(items, 2, 5, c);
		ce(items, 4, 7, c);
		ce(items, 8, 10, c);
		ce(items, 0, 4, c);
		ce(items, 1, 2, c);
		ce(items, 3, 7, c);
		ce(items, 5, 9, c);
		ce(items, 6, 8, c);
		ce(items, 0, 1, c);
		ce(items, 2, 6, c);
		ce(items, 4, 5, c);
		ce(items, 7, 8, c);
		ce(items, 9, 10, c);
		ce(items, 2, 4, c);
		ce(items, 3, 6, c);
		ce(items, 5, 7, c);
		ce(items, 8, 9, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
		ce(items, 7, 8, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
	}

	private static <K> void ce12 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 8, c);
		ce(items, 1, 7, c);
		ce(items, 2, 6, c);
		ce(items, 3, 11, c);
		ce(items, 4, 10, c);
		ce(items, 5, 9, c);
		ce(items, 0, 1, c);
		ce(items, 2, 5, c);
		ce(items, 3, 4, c);
		ce(items, 6, 9, c);
		ce(items, 7, 8, c);
		ce(items, 10, 11, c);
		ce(items, 0, 2, c);
		ce(items, 1, 6, c);
		ce(items, 5, 10, c);
		ce(items, 9, 11, c);
		ce(items, 0, 3, c);
		ce(items, 1, 2, c);
		ce(items, 4, 6, c);
		ce(items, 5, 7, c);
		ce(items, 8, 11, c);
		ce(items, 9, 10, c);
		ce(items, 1, 4, c);
		ce(items, 3, 5, c);
		ce(items, 6, 8, c);
		ce(items, 7, 10, c);
		ce(items, 1, 3, c);
		ce(items, 2, 5, c);
		ce(items, 6, 9, c);
		ce(items, 8, 10, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 8, 9, c);
		ce(items, 4, 6, c);
		ce(items, 5, 7, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
		ce(items, 7, 8, c);
	}

	private static <K> void ce13 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 12, c);
		ce(items, 1, 10, c);
		ce(items, 2, 9, c);
		ce(items, 3, 7, c);
		ce(items, 5, 11, c);
		ce(items, 6, 8, c);
		ce(items, 1, 6, c);
		ce(items, 2, 3, c);
		ce(items, 4, 11, c);
		ce(items, 7, 9, c);
		ce(items, 8, 10, c);
		ce(items, 0, 4, c);
		ce(items, 1, 2, c);
		ce(items, 3, 6, c);
		ce(items, 7, 8, c);
		ce(items, 9, 10, c);
		ce(items, 11, 12, c);
		ce(items, 4, 6, c);
		ce(items, 5, 9, c);
		ce(items, 8, 11, c);
		ce(items, 10, 12, c);
		ce(items, 0, 5, c);
		ce(items, 3, 8, c);
		ce(items, 4, 7, c);
		ce(items, 6, 11, c);
		ce(items, 9, 10, c);
		ce(items, 0, 1, c);
		ce(items, 2, 5, c);
		ce(items, 6, 9, c);
		ce(items, 7, 8, c);
		ce(items, 10, 11, c);
		ce(items, 1, 3, c);
		ce(items, 2, 4, c);
		ce(items, 5, 6, c);
		ce(items, 9, 10, c);
		ce(items, 1, 2, c);
		ce(items, 3, 4, c);
		ce(items, 5, 7, c);
		ce(items, 6, 8, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 8, 9, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
	}

	private static <K> void ce14 (final K[] items, final Comparator<? super K> c) {
		ce(items, 0, 1, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 8, 9, c);
		ce(items, 10, 11, c);
		ce(items, 12, 13, c);
		ce(items, 0, 2, c);
		ce(items, 1, 3, c);
		ce(items, 4, 8, c);
		ce(items, 5, 9, c);
		ce(items, 10, 12, c);
		ce(items, 11, 13, c);
		ce(items, 0, 4, c);
		ce(items, 1, 2, c);
		ce(items, 3, 7, c);
		ce(items, 5, 8, c);
		ce(items, 6, 10, c);
		ce(items, 9, 13, c);
		ce(items, 11, 12, c);
		ce(items, 0, 6, c);
		ce(items, 1, 5, c);
		ce(items, 3, 9, c);
		ce(items, 4, 10, c);
		ce(items, 7, 13, c);
		ce(items, 8, 12, c);
		ce(items, 2, 10, c);
		ce(items, 3, 11, c);
		ce(items, 4, 6, c);
		ce(items, 7, 9, c);
		ce(items, 1, 3, c);
		ce(items, 2, 8, c);
		ce(items, 5, 11, c);
		ce(items, 6, 7, c);
		ce(items, 10, 12, c);
		ce(items, 1, 4, c);
		ce(items, 2, 6, c);
		ce(items, 3, 5, c);
		ce(items, 7, 11, c);
		ce(items, 8, 10, c);
		ce(items, 9, 12, c);
		ce(items, 2, 4, c);
		ce(items, 3, 6, c);
		ce(items, 5, 8, c);
		ce(items, 7, 10, c);
		ce(items, 9, 11, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
		ce(items, 7, 8, c);
		ce(items, 9, 10, c);
		ce(items, 6, 7, c);
	}

	private static <K> void ce15 (final K[] items, final Comparator<? super K> c) {
		ce(items, 1, 2, c);
		ce(items, 3, 10, c);
		ce(items, 4, 14, c);
		ce(items, 5, 8, c);
		ce(items, 6, 13, c);
		ce(items, 7, 12, c);
		ce(items, 9, 11, c);
		ce(items, 0, 14, c);
		ce(items, 1, 5, c);
		ce(items, 2, 8, c);
		ce(items, 3, 7, c);
		ce(items, 6, 9, c);
		ce(items, 10, 12, c);
		ce(items, 11, 13, c);
		ce(items, 0, 7, c);
		ce(items, 1, 6, c);
		ce(items, 2, 9, c);
		ce(items, 4, 10, c);
		ce(items, 5, 11, c);
		ce(items, 8, 13, c);
		ce(items, 12, 14, c);
		ce(items, 0, 6, c);
		ce(items, 2, 4, c);
		ce(items, 3, 5, c);
		ce(items, 7, 11, c);
		ce(items, 8, 10, c);
		ce(items, 9, 12, c);
		ce(items, 13, 14, c);
		ce(items, 0, 3, c);
		ce(items, 1, 2, c);
		ce(items, 4, 7, c);
		ce(items, 5, 9, c);
		ce(items, 6, 8, c);
		ce(items, 10, 11, c);
		ce(items, 12, 13, c);
		ce(items, 0, 1, c);
		ce(items, 2, 3, c);
		ce(items, 4, 6, c);
		ce(items, 7, 9, c);
		ce(items, 10, 12, c);
		ce(items, 11, 13, c);
		ce(items, 1, 2, c);
		ce(items, 3, 5, c);
		ce(items, 8, 10, c);
		ce(items, 11, 12, c);
		ce(items, 3, 4, c);
		ce(items, 5, 6, c);
		ce(items, 7, 8, c);
		ce(items, 9, 10, c);
		ce(items, 2, 3, c);
		ce(items, 4, 5, c);
		ce(items, 6, 7, c);
		ce(items, 8, 9, c);
		ce(items, 10, 11, c);
		ce(items, 5, 6, c);
		ce(items, 7, 8, c);
	}

	/**
	 * Transforms two consecutive sorted ranges into a single sorted range. The initial ranges are
	 * {@code [first..middle)} and {@code [middle..last)}, and the resulting range is
	 * {@code [first..last)}. Elements in the first input range will precede equal elements in
	 * the second.
	 */
	private static <K> void inPlaceMerge (K[] items, final int from, int mid, final int to, final Comparator<? super K> comp) {
		if (from >= mid || mid >= to) {return;}
		if (to - from == 2) {
//			if (comp.compare(items[mid], items[from]) < 0) {swap(items, from, mid);}
			ce(items, mid, from, comp);
			return;
		}

		int firstCut;
		int secondCut;

		if (mid - from > to - mid) {
			firstCut = from + (mid - from) / 2;
			secondCut = lowerBound(items, mid, to, firstCut, comp);
		} else {
			secondCut = mid + (to - mid) / 2;
			firstCut = upperBound(items, from, mid, secondCut, comp);
		}

		int first2 = firstCut;
		int middle2 = mid;
		int last2 = secondCut;
		if (middle2 != first2 && middle2 != last2) {
			int first1 = first2;
			int last1 = middle2;
			while (first1 < --last1) {swap(items, first1++, last1);}
			first1 = middle2;
			last1 = last2;
			while (first1 < --last1) {swap(items, first1++, last1);}
			first1 = first2;
			last1 = last2;
			while (first1 < --last1) {swap(items, first1++, last1);}
		}

		mid = firstCut + secondCut - mid;
		inPlaceMerge(items, from, firstCut, mid, comp);
		inPlaceMerge(items, mid, secondCut, to, comp);
	}

	/**
	 * Performs a binary search on an already-sorted range: finds the first position where an
	 * element can be inserted without violating the ordering. Sorting is by a user-supplied
	 * comparison function.
	 *
	 * @param items the List to be sorted
	 * @param from  the index of the first element (inclusive) to be included in the binary search.
	 * @param to    the index of the last element (exclusive) to be included in the binary search.
	 * @param pos   the position of the element to be searched for.
	 * @param comp  the comparison function.
	 * @return the largest index i such that, for every j in the range {@code [first..i)},
	 * {@code comp.compare(get(j), get(pos))} is {@code true}.
	 */
	private static <K> int lowerBound (K[] items, int from, final int to, final int pos, final Comparator<? super K> comp) {
		int len = to - from;
		while (len > 0) {
			int half = len / 2;
			int middle = from + half;
			if (comp.compare(items[middle], items[pos]) < 0) {
				from = middle + 1;
				len -= half + 1;
			} else {
				len = half;
			}
		}
		return from;
	}

	/**
	 * Performs a binary search on an already sorted range: finds the last position where an element
	 * can be inserted without violating the ordering. Sorting is by a user-supplied comparison
	 * function.
	 *
	 * @param items the List to be sorted
	 * @param from  the index of the first element (inclusive) to be included in the binary search.
	 * @param to    the index of the last element (exclusive) to be included in the binary search.
	 * @param pos   the position of the element to be searched for.
	 * @param comp  the comparison function.
	 * @return The largest index i such that, for every j in the range {@code [first..i)},
	 * {@code comp.compare(get(pos), get(j))} is {@code false}.
	 */
	private static <K> int upperBound (K[] items, int from, final int to, final int pos, final Comparator<? super K> comp) {
		int len = to - from;
		while (len > 0) {
			int half = len / 2;
			int middle = from + half;
			if (comp.compare(items[pos], items[middle]) < 0) {
				len = half;
			} else {
				from = middle + 1;
				len -= half + 1;
			}
		}
		return from;
	}

	/**
	 * Sorts all of {@code items} by simply calling {@link #sort(Object[], int, int, Comparator)}
	 * setting {@code from} and {@code to} so the whole array is sorted.
	 *
	 * @param items the List to be sorted
	 * @param c     a Comparator to alter the sort order; if null, the natural order will be used
	 */
	public static <K> void sort (K[] items, final @Nullable Comparator<? super K> c) {
		sort(items, 0, items.length, c);
	}

	/**
	 * Sorts the specified range of elements according to the order induced by the specified
	 * comparator using mergesort.
	 *
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort. The sorting algorithm is an in-place mergesort that is significantly slower than a
	 * standard mergesort, as its running time is <i>O</i>(<var>n</var>&nbsp;(log&nbsp;<var>n</var>)<sup>2</sup>),
	 * but it does not allocate additional memory; as a result, it can be
	 * used as a generic sorting algorithm.
	 *
	 * @param items the List to be sorted
	 * @param from  the index of the first element (inclusive) to be sorted.
	 * @param to    the index of the last element (exclusive) to be sorted.
	 * @param c     a Comparator to alter the sort order; if null, the natural order will be used
	 */
	public static <K> void sort (final K[] items, final int from, final int to, final @Nullable Comparator<? super K> c) {
		if (to <= 0) {
			return;
		}
		if (from < 0 || from >= items.length || to > items.length) {
			throw new UnsupportedOperationException("The given from/to range in Comparators.sort() is invalid.");
		}
		if (c == null) {
			sort(items, from, to, (Comparator<K>)Comparator.naturalOrder());
			return;
		}
		/*
		 * We retain the same method signature as quickSort. Given only a comparator and this list
		 * do not know how to copy and move elements from/to temporary arrays. Hence, in contrast to
		 * the JDK mergesorts this is an "in-place" mergesort, i.e. does not allocate any temporary
		 * arrays. A non-inplace mergesort would perhaps be faster in most cases, but would require
		 * non-intuitive delegate objects...
		 */
		final int length = to - from;

//		// Choose a known-best sorting network for the smallest arrays (0-15 elements)
//		// This uses info from https://bertdobbelaere.github.io/sorting_networks.html
//		if (length < 16) {
//			switch (length){
//				case 2:
//					ce2(items, c);
//				break;
//				case 3:
//					ce3(items, c);
//				break;
//				case 4:
//					ce4(items, c);
//					break;
//				case 5:
//					ce5(items, c);
//					break;
//				case 6:
//					ce6(items, c);
//					break;
//				case 7:
//					ce7(items, c);
//					break;
//				case 8:
//					ce8(items, c);
//					break;
//				case 9:
//					ce9(items, c);
//					break;
//				case 10:
//					ce10(items, c);
//					break;
//				case 11:
//					ce11(items, c);
//					break;
//				case 12:
//					ce12(items, c);
//					break;
//				case 13:
//					ce13(items, c);
//					break;
//				case 14:
//					ce14(items, c);
//					break;
//				case 15:
//					ce15(items, c);
//					break;
//			}
//			return;
//		}
		// Insertion sort on smallest arrays, less than 32 items
		if (length < 32) {
			for (int i = from; i < to; i++) {
				for (int j = i; j > from && c.compare(items[j - 1], items[j]) > 0; j--) {
					swap(items, j, j - 1);
				}
			}
			return;
		}

		// Recursively sort halves
		int mid = from + to >>> 1;
		sort(items, from, mid, c);
		sort(items, mid, to, c);

		// If list is already sorted, nothing left to do. This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if (c.compare(items[mid - 1], items[mid]) <= 0) {return;}

		// Merge sorted halves
		inPlaceMerge(items, from, mid, to, c);
	}
}
