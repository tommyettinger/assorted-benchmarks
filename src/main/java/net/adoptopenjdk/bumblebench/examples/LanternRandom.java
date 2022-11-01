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
 * Like LanternRandom, still guaranteeing a minimum period of 2 to the 64, but using only ARX operations.
 */
public class LanternRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "LanR";
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
	 * The fourth state; can be any long.
	 */
	protected long stateD;
	/**
	 * The fifth state; can be any long. The first call to {@link #nextLong()} will return this verbatim, if no other
	 * methods have been called.
	 */
	protected long stateE;

	/**
	 * Creates a new LanternRandom with a random state.
	 */
	public LanternRandom() {
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
		stateC = EnhancedRandom.seedFromMath();
		stateD = EnhancedRandom.seedFromMath();
		stateE = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new LanternRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public LanternRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new LanternRandom with the given four states; all {@code long} values are permitted.
	 * These states will be used verbatim.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 * @param stateC any {@code long} value
	 * @param stateD any {@code long} value
	 * @param stateE any {@code long} value
	 */
	public LanternRandom(long stateA, long stateB, long stateC, long stateD, long stateE) {
		this.stateA = stateA;
		this.stateB = stateB;
		this.stateC = stateC;
		this.stateD = stateD;
		this.stateE = stateE;
	}

	/**
	 * This generator has 5 {@code long} states, so this returns 5.
	 *
	 * @return 5 (five)
	 */
	@Override
	public int getStateCount () {
		return 5;
	}

	/**
	 * Gets the state determined by {@code selection}, as-is. The value for selection should be
	 * between 0 and 4, inclusive; if it is any other value this gets state E as if 4 was given.
	 *
	 * @param selection used to select which state variable to get; generally 0, 1, 2, 3, or 4
	 * @return the value of the selected state
	 */
	@Override
	public long getSelectedState (int selection) {
		switch (selection) {
			case 0:
				return stateA;
			case 1:
				return stateB;
			case 2:
				return stateC;
			case 3:
				return stateD;
			default:
				return stateE;
		}
	}

	/**
	 * Sets one of the states, determined by {@code selection}, to {@code value}, as-is.
	 * Selections 0, 1, 2, 3, and 4 refer to states A, B, C, D, and E, and if the selection is anything
	 * else, this treats it as 4 and sets stateE.
	 *
	 * @param selection used to select which state variable to set; generally 0, 1, 2, 3, or 4
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
			case 2:
				stateC = value;
				break;
			case 3:
				stateD = value;
				break;
			default:
				stateE = value;
		}
	}

	/**
	 * This initializes all 5 states of the generator to random values based on the given seed.
	 * (2 to the 64) possible initial generator states can be produced here, all with a different
	 * first value returned by {@link #nextLong()}.
	 *
	 * @param seed the initial seed; may be any long
	 */
	@Override
	public void setSeed (long seed) {
		stateA = seed;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		stateB = seed;
		stateC = seed ^ ~0xC6BC279692B5C323L;
		stateD = ~seed;
		stateE = seed ^ 0xC6BC279692B5C323L;
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

	public long getStateD () {
		return stateD;
	}

	/**
	 * Sets the fourth part of the state.
	 *
	 * @param stateD can be any long
	 */
	public void setStateD (long stateD) {
		this.stateD = stateD;
	}

	public long getStateE () {
		return stateE;
	}

	/**
	 * Sets the fifth part of the state.
	 *
	 * @param stateE can be any long
	 */
	public void setStateE (long stateE) {
		this.stateE = stateE;
	}

	/**
	 * Sets the state completely to the given four state variables.
	 * This is the same as calling {@link #setStateA(long)}, {@link #setStateB(long)},
	 * {@link #setStateC(long)}, {@link #setStateD(long)}, and {@link #setStateE(long)} as a group.
	 *
	 * @param stateA the first state; can be any long
	 * @param stateB the second state; can be any long
	 * @param stateC the third state; can be any long
	 * @param stateD the fourth state; can be any long
	 * @param stateE the fifth state; can be any long
	 */
	public void setState (long stateA, long stateB, long stateC, long stateD, long stateE) {
		this.stateA = stateA;
		this.stateB = stateB;
		this.stateC = stateC;
		this.stateD = stateD;
		this.stateE = stateE;
	}

	@Override
	public long nextLong () {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		final long fd = stateD;
		final long fe = stateE;
		stateA = fa + 0x9E3779B97F4A7C15L;
		stateB = (fe << 41 | fe >>> 23);
		stateC = fa ^ fb;
		stateD = (fc << 17 | fc >>> 47);
		return stateE = fc + fd;
	}

	@Override
	public long previousLong () {
		final long fb = stateB;
		final long fc = stateC;
		final long fd = stateD;
		final long fe = stateE;
		stateA -= 0x9E3779B97F4A7C15L;
		stateB = fc ^ stateA;
		stateC = (fd >>> 17 | fd << 47);
		stateD = fe - stateC;
		return stateE = (fb >>> 41 | fb << 23);
	}

	@Override
	public int next (int bits) {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		final long fd = stateD;
		final long fe = stateE;
		stateA = fa + 0x9E3779B97F4A7C15L;
		stateB = (fe << 41 | fe >>> 23);
		stateC = fa ^ fb;
		stateD = (fc << 17 | fc >>> 47);
		return (int) (stateE = fc + fd) >>> (32 - bits);
	}

	@Override
	public LanternRandom copy () {
		return new LanternRandom(stateA, stateB, stateC, stateD, stateE);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		LanternRandom that = (LanternRandom)o;

		return stateA == that.stateA && stateB == that.stateB && stateC == that.stateC && stateD == that.stateD &&
				stateE == that.stateE;
	}

	public String toString () {
		return "LanternRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L, stateC=" + (stateC) + "L, stateD=" + (stateD) + "L, stateE=" + (stateE) + "L}";
	}

//	public static void main(String[] args) {
//		LanternRandom random = new LanternRandom(0L);
//		long n0 = random.nextLong();
//		long n1 = random.nextLong();
//		long n2 = random.nextLong();
//		long n3 = random.nextLong();
//		long n4 = random.nextLong();
//		long n5 = random.nextLong();
//		long n6 = random.nextLong();
//		long p5 = random.previousLong();
//		long p4 = random.previousLong();
//		long p3 = random.previousLong();
//		long p2 = random.previousLong();
//		long p1 = random.previousLong();
//		long p0 = random.previousLong();
//		System.out.println(n0 == p0);
//		System.out.println(n1 == p1);
//		System.out.println(n2 == p2);
//		System.out.println(n3 == p3);
//		System.out.println(n4 == p4);
//		System.out.println(n5 == p5);
//		System.out.printf("0x%016XL vs. 0x%016XL\n", n0, p0);
//		System.out.printf("0x%016XL vs. 0x%016XL\n", n1, p1);
//		System.out.printf("0x%016XL vs. 0x%016XL\n", n2, p2);
//		System.out.printf("0x%016XL vs. 0x%016XL\n", n3, p3);
//		System.out.printf("0x%016XL vs. 0x%016XL\n", n4, p4);
//		System.out.printf("0x%016XL vs. 0x%016XL\n", n5, p5);
//	}
}
