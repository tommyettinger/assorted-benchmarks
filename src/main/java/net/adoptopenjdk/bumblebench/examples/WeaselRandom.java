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

package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.digital.BitConversion;
import com.github.tommyettinger.random.EnhancedRandom;

public class WeaselRandom extends EnhancedRandom {

	/**
	 * The first state; can be any long.
	 */
	protected long stateA;
	/**
	 * The second state; can be any long.
	 */
	protected long stateB;

	/**
	 * Creates a new WeaselRandom with a random state.
	 */
	public WeaselRandom() {
		super();
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new WeaselRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public WeaselRandom(long seed) {
		super(seed);
		setSeed(seed);
	}

	/**
	 * Creates a new WeaselRandom with the given two states; all {@code long} values are permitted for
	 * stateA and for stateB. These states are not changed during assignment.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 */
	public WeaselRandom(long stateA, long stateB) {
		super(stateA);
		this.stateA = stateA;
		this.stateB = stateB;
	}

	@Override
	public String getTag() {
		return "CngR";
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
			stateB = value;
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
		// This is based on MX3, but pulls out values and assigns them to states mid-way, sometimes XORing them.
		seed += 0x9E3779B97F4A7C15L;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		seed *= 0xbea225f9eb34556dL;
		stateA = (seed ^ 0xC6BC279692B5C323L);
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		stateB = (seed ^ ~0xC6BC279692B5C323L);
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

	/**
	 * Sets the state completely to the given two state variables.
	 * This is the same as calling {@link #setStateA(long)} and {@link #setStateB(long)}
	 * as a group.
	 *
	 * @param stateA the first state; can be any long
	 * @param stateB the second state; can be any long
	 */
	@Override
	public void setState (long stateA, long stateB) {
		this.stateA = stateA;
		this.stateB = stateB;
	}

	@Override
	public long nextLong () {
		final long x = stateA, y = stateB, z = ((x << 3 | x >>> 61) + y ^ (y << 56 | y >>> 8)) * 0xD1342543DE82EF95L;
		stateA = x + 0xC6BC279692B5CC83L;
		stateB = y + (x ^ BitConversion.countLeadingZeros(x));
		return z ^ (z << 29 | z >>> 35) ^ (z << 40 | z >>> 24);
	}

	@Override
	public int next (int bits) {
		final long x = stateA, y = stateB, z = ((x << 3 | x >>> 61) + y ^ (y << 56 | y >>> 8)) * 0xD1342543DE82EF95L;
		stateA = x + 0xC6BC279692B5CC83L;
		stateB = y + (x ^ BitConversion.countLeadingZeros(x));
		return (int)(z ^ (z << 29 | z >>> 35) ^ (z << 40 | z >>> 24)) >>> (32 - bits);
	}

	@Override
	public WeaselRandom copy () {
		return new WeaselRandom(stateA, stateB);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		WeaselRandom that = (WeaselRandom)o;

		return stateA == that.stateA && stateB == that.stateB;
	}

	public String toString () {
		return "WeaselRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L}";
	}

//	public static void main(String[] args) {
//		WeaselRandom random = new WeaselRandom(1L);
//		long n0 = random.nextLong();
//		long n1 = random.nextLong();
//		long n2 = random.nextLong();
//		long n3 = random.nextLong();
//		long n4 = random.nextLong();
//		long n5 = random.nextLong();
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
//		System.out.println(n0 + " vs. " + p0);
//		System.out.println(n1 + " vs. " + p1);
//		System.out.println(n2 + " vs. " + p2);
//		System.out.println(n3 + " vs. " + p3);
//		System.out.println(n4 + " vs. " + p4);
//		System.out.println(n5 + " vs. " + p5);
//	}

}
