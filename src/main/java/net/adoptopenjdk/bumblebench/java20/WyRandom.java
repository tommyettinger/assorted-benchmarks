/*
 * Copyright (c) 2022-2023 See AUTHORS file.
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

package net.adoptopenjdk.bumblebench.java20;

import com.github.tommyettinger.random.*;

/**
 * A low-quality but maybe-fast generator that is only compatible with Java 20 and up (but possibly with 19 at a speed
 * penalty). It needs to be emphasized that this is Low Quality; the majority of {@code long} results are impossible for
 * {@link #nextLong()} to ever return here.
 * <br>
 * This implements all methods from {@link EnhancedRandom}, including the optional {@link #skip(long)} and
 * {@link #previousLong()} methods.
 */
public class WyRandom extends EnhancedRandom {

	/**
	 * The only state variable; can be any {@code long}.
	 */
	public long state;

	/**
	 * Creates a new WyRandom with a random state.
	 */
	public WyRandom() {
		this(EnhancedRandom.seedFromMath());
	}

	/**
	 * Creates a new WyRandom with the given state; all {@code long} values are permitted.
	 *
	 * @param state any {@code long} value
	 */
	public WyRandom(long state) {
		super(state);
		this.state = state;
	}

	@Override
	public String getTag() {
		return "WyrR";
	}

	/**
	 * This has one long state.
	 *
	 * @return 1 (one)
	 */
	@Override
	public int getStateCount () {
		return 1;
	}

	/**
	 * Gets the only state, which can be any long value.
	 *
	 * @param selection ignored; this always returns the same, only state
	 * @return the only state's exact value
	 */
	@Override
	public long getSelectedState (int selection) {
		return state;
	}

	/**
	 * Sets the only state, which can be given any long value. The selection
	 * can be anything and is ignored.
	 *
	 * @param selection ignored; this always sets the same, only state
	 * @param value     the exact value to use for the state; all longs are valid
	 */
	@Override
	public void setSelectedState (int selection, long value) {
		state = value;
	}

	/**
	 * Sets the only state, which can be given any long value; this seed value
	 * will not be altered. Equivalent to {@link #setSelectedState(int, long)}
	 * with any selection and {@code seed} passed as the {@code value}.
	 *
	 * @param seed the exact value to use for the state; all longs are valid
	 */
	@Override
	public void setSeed (long seed) {
		state = seed;
	}

	/**
	 * Gets the current state; it's already public, but I guess this could still
	 * be useful. The state can be any {@code long}.
	 *
	 * @return the current state, as a long
	 */
	public long getState () {
		return state;
	}

	/**
	 * Sets each state variable to the given {@code state}. This implementation
	 * simply sets the one state variable to {@code state}.
	 *
	 * @param state the long value to use for the state variable
	 */
	@Override
	public void setState (long state) {
		this.state = state;
	}

	@Override
	public long nextLong () {
		final long x = (state += 0xA0761D6478BD642FL);
		final long y = x ^ 0xE7037ED1A0B428DBL;
		return x * y ^ Math.unsignedMultiplyHigh(x, y);
	}

	/**
	 * Skips the state forward or backwards by the given {@code advance}, then returns the result of {@link #nextLong()}
	 * at the same point in the sequence. If advance is 1, this is equivalent to nextLong(). If advance is 0, this
	 * returns the same {@code long} as the previous call to the generator (if it called nextLong()), and doesn't change
	 * the state. If advance is -1, this moves the state backwards and produces the {@code long} before the last one
	 * generated by nextLong(). More positive numbers move the state further ahead, and more negative numbers move the
	 * state further behind; all of these take constant time.
	 *
	 * @param advance how many steps to advance the state before generating a {@code long}
	 * @return a random {@code long} by the same algorithm as {@link #nextLong()}, using the appropriately-advanced state
	 */
	@Override
	public long skip (long advance) {
		final long x = (state += 0xA0761D6478BD642FL * advance);
		final long y = x ^ 0xE7037ED1A0B428DBL;
		return x * y ^ Math.unsignedMultiplyHigh(x, y);
	}

	@Override
	public long previousLong () {
		final long x = state;
		state -= 0xA0761D6478BD642FL;
		final long y = x ^ 0xE7037ED1A0B428DBL;
		return x * y ^ Math.unsignedMultiplyHigh(x, y);
	}

	@Override
	public int next (int bits) {
		final long x = (state += 0xA0761D6478BD642FL);
		final long y = x ^ 0xE7037ED1A0B428DBL;
		return (int)(x * y ^ Math.unsignedMultiplyHigh(x, y)) >>> (32 - bits);
	}

	@Override
	public WyRandom copy () {
		return new WyRandom(state);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		WyRandom that = (WyRandom)o;

		return state == that.state;
	}

	@Override
	public String toString () {
		return "WyRandom{state=" + (state) + "L}";
	}
}
