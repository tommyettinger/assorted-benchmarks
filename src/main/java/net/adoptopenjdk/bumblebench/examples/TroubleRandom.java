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

package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.EnhancedRandom;

/**
 * Don't use this; quality is unclear and speed is poor.
 */
public class TroubleRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "TroR";
	}

	/**
	 * The first state, also called the changing state; can be any long.
	 */
	protected long stateA;
	/**
	 * The second state, also called the stream; can be any odd-number long.
	 */
	protected long stateB;

	/**
	 * Creates a new TroubleRandom with a random state.
	 */
	public TroubleRandom() {
		super();
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath() | 1L;
	}

	/**
	 * Creates a new TroubleRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public TroubleRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new TroubleRandom with the given two states; all {@code long} values are permitted for
	 * stateA, and all odd-number {@code long} values are permitted for stateB. These states are not
	 * changed as long as they are permitted values.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value; should be odd, otherwise this will add 1 to make it odd
	 */
	public TroubleRandom(long stateA, long stateB) {
		this.stateA = stateA;
		this.stateB = stateB | 1L;
	}

	/**
	 * This generator has 2 {@code long} states, so this returns 2.
	 *
	 * @return 2 (two)
	 */
	@Override
	public int getStateCount () {
		return 2;
	}

	/**
	 * Gets the state determined by {@code selection}, as-is.
	 * Selections 0 (or any even number) and 1 (or any odd number) refer to states A and B.
	 *
	 * @param selection used to select which state variable to get; generally 0 or 1
	 * @return the value of the selected state
	 */
	@Override
	public long getSelectedState (int selection) {
		if ((selection & 1) == 1) {
			return stateB;
		}
		return stateA;
	}

	/**
	 * Sets one of the states, determined by {@code selection}, to {@code value}, as-is.
	 * Selections 0 (or any even number) and 1 (or any odd number) refer to states A and B.
	 *
	 * @param selection used to select which state variable to set; generally 0 or 1
	 * @param value     the exact value to use for the selected state, if valid
	 */
	@Override
	public void setSelectedState (int selection, long value) {
		if ((selection & 1) == 1) {
			stateB = value | 1L;
		} else {
			stateA = value;
		}
	}

	/**
	 * This initializes both states of the generator to random values based on the given seed.
	 * (2 to the 64) possible initial generator states can be produced here.
	 *
	 * @param seed the initial seed; may be any long
	 */
	@Override
	public void setSeed (long seed) {
		long x = (seed += 0x9E3779B97F4A7C15L);
		x ^= x >>> 27;
		x *= 0x3C79AC492BA7B653L;
		x ^= x >>> 33;
		x *= 0x1C69B3F74AC4AE35L;
		stateA = x ^ x >>> 27;
		x = (seed + 0x9E3779B97F4A7C15L);
		x ^= x >>> 27;
		x *= 0x3C79AC492BA7B653L;
		x ^= x >>> 33;
		x *= 0x1C69B3F74AC4AE35L;
		stateB = (x ^ x >>> 27) | 1L;
	}

	public long getStateA () {
		return stateA;
	}

	/**
	 * Sets the first part of the state (the changing state).
	 *
	 * @param stateA can be any long
	 */
	public void setStateA (long stateA) {
		this.stateA = stateA;
	}

	public long getStateB () {
		return stateB;
	}

	/**
	 * Sets the second part of the state (the stream). This must be odd, otherwise this will add 1 to make it odd.
	 *
	 * @param stateB can be any odd-number long; otherwise this adds 1 to make it odd
	 */
	public void setStateB (long stateB) {
		this.stateB = stateB | 1L;
	}

	/**
	 * Sets the state completely to the given three state variables.
	 * This is the same as calling {@link #setStateA(long)} and {@link #setStateB(long)}
	 * as a group.
	 *
	 * @param stateA the first state; can be any long
	 * @param stateB the second state; can be any odd-number long
	 */
	@Override
	public void setState (long stateA, long stateB) {
		this.stateA = stateA;
		this.stateB = stateB | 1L;
	}

	@Override
	public long nextLong () {
		stateA = stateA * (stateB += 0x9E3779B97F4A7C16L) + 0xC6BC279692B5C323L;
		return (stateA -= (stateA << 23 | stateA >>> 41));
	}

	@Override
	public int next (int bits) {
		stateA = stateA * (stateB += 0x9E3779B97F4A7C16L) + 0xC6BC279692B5C323L;
		return (int) (stateA -= (stateA << 23 | stateA >>> 41)) >>> (32 - bits);
	}

	@Override
	public TroubleRandom copy () {
		return new TroubleRandom(stateA, stateB);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TroubleRandom that = (TroubleRandom)o;

		if (stateA != that.stateA)
			return false;
		return stateB == that.stateB;
	}

	public String toString () {
		return "TroubleRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L}";
	}
}
