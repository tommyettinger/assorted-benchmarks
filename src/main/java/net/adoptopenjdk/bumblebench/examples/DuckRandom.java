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
 */
public class DuckRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "DucR";
	}

	/**
	 * The first state; can be any long.
	 */
	protected long stateA;
	/**
	 * The second state; can be any long.
	 */
	protected long stateB;
	/**
	 * The third state; can be any long.
	 */
	protected long stateC;

	/**
	 * Creates a new TrimPostRandom with a random state.
	 */
	public DuckRandom() {
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
		stateC = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new TrimPostRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public DuckRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new TrimPostRandom with the given four states; all {@code long} values are permitted.
	 * These states will be used verbatim.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 * @param stateC any {@code long} value
	 */
	public DuckRandom(long stateA, long stateB, long stateC) {
		this.stateA = stateA;
		this.stateB = stateB;
		this.stateC = stateC;
	}

	/**
	 * This generator has 3 {@code long} states, so this returns 3.
	 *
	 * @return 3 (three)
	 */
	@Override
	public int getStateCount () {
		return 3;
	}

	/**
	 * Gets the state determined by {@code selection}, as-is. The value for selection should be
	 * between 0 and 2, inclusive; if it is any other value this gets state C as if 2 was given.
	 *
	 * @param selection used to select which state variable to get; generally 0, 1, or 2
	 * @return the value of the selected state
	 */
	@Override
	public long getSelectedState (int selection) {
		switch (selection) {
			case 0:
				return stateA;
			case 1:
				return stateB;
			default:
				return stateC;
		}
	}

	/**
	 * Sets one of the states, determined by {@code selection}, to {@code value}, as-is.
	 * Selections 0, 1, 2, and 3 refer to states A, B, C, and D,  and if the selection is anything
	 * else, this treats it as 3 and sets stateD.
	 *
	 * @param selection used to select which state variable to set; generally 0, 1, 2, or 3
	 * @param value     the exact value to use for the selected state, if valid
	 */
	@Override
	public void setSelectedState (int selection, long value) {
		switch (selection) {
		case 0:
			stateA = value;
			break;
		case 1:
			stateB = value;
			break;
		default:
			stateC = value;
			break;
		}
	}

	/**
	 * This initializes all 4 states of the generator to random values based on the given seed.
	 * (2 to the 64) possible initial generator states can be produced here, all with a different
	 * first value returned by {@link #nextLong()}.
	 *
	 * @param seed the initial seed; may be any long
	 */
	@Override
	public void setSeed (long seed) {
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		stateA = seed;
		stateB = seed ^ 0xC6BC279692B5C323L;
		stateC = seed ^ ~0xC6BC279692B5C323L;
	}

	public long getStateA () {
		return stateA;
	}

	/**
	 * Sets the first part of the state.
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
	 * Sets the second part of the state.
	 *
	 * @param stateB can be any long
	 */
	public void setStateB (long stateB) {
		this.stateB = stateB;
	}

	public long getStateC () {
		return stateC;
	}

	/**
	 * Sets the third part of the state.
	 *
	 * @param stateC can be any long
	 */
	public void setStateC (long stateC) {
		this.stateC = stateC;
	}

	/**
	 * Sets the state completely to the given four state variables.
	 * This is the same as calling {@link #setStateA(long)}, {@link #setStateB(long)},
	 * and {@link #setStateC(long)} as a group.
	 *
	 * @param stateA the first state; can be any long
	 * @param stateB the second state; can be any long
	 * @param stateC the third state; can be any long
	 */
	@Override
	public void setState (long stateA, long stateB, long stateC) {
		this.stateA = stateA;
		this.stateB = stateB;
		this.stateC = stateC;
	}

	@Override
	public long nextLong () {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		final long result = fa ^ fb;
		stateA = fb * 0xF1357AEA2E62A9C5L;
		stateB = (fa << 42 | fa >>> 22) + fc;
		stateC = fc + 0xC6BC279692B5C323L;
		return result;
	}

	@Override
	public int next (int bits) {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		final long result = fa ^ fb;
		stateA = fb * 0xF1357AEA2E62A9C5L;
		stateB = (fa << 42 | fa >>> 22) + fc;
		stateC = fc + 0xC6BC279692B5C323L;
		return (int)result >>> (32 - bits);
	}

	@Override
	public DuckRandom copy () {
		return new DuckRandom(stateA, stateB, stateC);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DuckRandom that = (DuckRandom)o;

		return stateA == that.stateA && stateB == that.stateB && stateC == that.stateC;
	}

	public String toString () {
		return "DuckRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L, stateC=" + (stateC) + "L}";
	}

//	public static void main(String[] args) {
//		SlashRandom random = new SlashRandom(0L);
//		long n0 = random.nextLong();
//		long n1 = random.nextLong();
//		long n2 = random.nextLong();
//		long n3 = random.nextLong();
//		long p2 = random.previousLong();
//		long p1 = random.previousLong();
//		long p0 = random.previousLong();
//		System.out.println(n0 == p0);
//		System.out.println(n1 == p1);
//		System.out.println(n2 == p2);
//	}
}
