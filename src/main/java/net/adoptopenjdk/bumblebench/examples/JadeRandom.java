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
 * A hash-on-counters type of RNG with two 64-bit states. This is like LaserRandom in many ways, but has much less
 * correlation between starting states. Has an exact period of 2 to the 64.
 */
public class JadeRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "JadR";
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
	 * Creates a new JadeRandom with a random state.
	 */
	public JadeRandom() {
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new JadeRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public JadeRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new JadeRandom with the given two states; all {@code long} values are permitted.
	 * These states will be used verbatim.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 */
	public JadeRandom(long stateA, long stateB) {
		this.stateA = stateA;
		this.stateB = stateB;
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
	 * Gets the state determined by {@code selection}, as-is. The value for selection should be
	 * either 0 and 1; if selection is an even number, this selects stateA, otherwise stateB.
	 *
	 * @param selection used to select which state variable to get; generally 0 or 1
	 * @return the value of the selected state
	 */
	@Override
	public long getSelectedState (int selection) {
		if ((selection & 1) == 0) {
			return stateA;
		}
		return stateB;
	}

	/**
	 * Sets one of the states, determined by {@code selection}, to {@code value}, as-is.
	 * Even selections refer to stateA, and odd ones to stateB.
	 *
	 * @param selection used to select which state variable to set; generally 0 or 1
	 * @param value     the exact value to use for the selected state, if valid
	 */
	@Override
	public void setSelectedState (int selection, long value) {
		if ((selection & 1) == 0) {
			stateA = value;
		} else {
			stateB = value;
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
		stateA = seed + 0xC6BC279692B5C323L;
		seed -= 0xC6BC279692B5C323L;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		stateB = seed;
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
	 * This is the same as calling {@link #setStateA(long)} and {@link #setStateB(long)} as a group.
	 *
	 * @param stateA the first state; can be any long
	 * @param stateB the second state; can be any long
	 */
	public void setState (long stateA, long stateB) {
		this.stateA = stateA;
		this.stateB = stateB;
	}

	@Override
	public long nextLong () {
		long x = stateA += 0xC13FA9A902A6328FL;
		long y = stateB += 0x91E10DA5C79E7B1DL;
		x *= (y ^ (y << 11 | y >>> 53) ^ (y << 50 | y >>> 14)) | 1L;
		y *= (x ^ (x << 31 | x >>> 33) ^ (x << 37 | x >>> 27)) | 1L;
		return y ^ y >>> 31;
	}

	/**
	 * Optional; advances or rolls back the {@code EnhancedRandom}' state without actually generating each number.
	 * Skips forward or backward a number of steps specified by advance, where a step is equal to one call to
	 * {@link #nextLong()}, and returns the random number produced at that step. Negative numbers can be used to
	 * step backward, or 0 can be given to get the most-recently-generated long from {@link #nextLong()}.
	 *
	 * <p>The public implementation throws an UnsupportedOperationException. Many types of random
	 * number generator do not have an efficient way of skipping arbitrarily through the state sequence,
	 * and those types should not implement this method differently.
	 *
	 * @param advance Number of future generations to skip over; can be negative to backtrack, 0 gets the most-recently-generated number
	 * @return the random long generated after skipping forward or backwards by {@code advance} numbers
	 */
	@Override
	public long skip(long advance) {
		long x = stateA += 0xC13FA9A902A6328FL * advance;
		long y = stateB += 0x91E10DA5C79E7B1DL * advance;
		x *= (y ^ (y << 11 | y >>> 53) ^ (y << 50 | y >>> 14)) | 1L;
		y *= (x ^ (x << 31 | x >>> 33) ^ (x << 37 | x >>> 27)) | 1L;
		return y ^ y >>> 31;
	}

	@Override
	public long previousLong () {
		long x = stateA -= 0xC13FA9A902A6328FL;
		long y = stateB -= 0x91E10DA5C79E7B1DL;
		x *= (y ^ (y << 11 | y >>> 53) ^ (y << 50 | y >>> 14)) | 1L;
		y *= (x ^ (x << 31 | x >>> 33) ^ (x << 37 | x >>> 27)) | 1L;
		return y ^ y >>> 31;
	}

	@Override
	public int next (int bits) {
		long x = stateA += 0xC13FA9A902A6328FL;
		long y = stateB += 0x91E10DA5C79E7B1DL;
		x *= (y ^ (y << 11 | y >>> 53) ^ (y << 50 | y >>> 14)) | 1L;
		y *= (x ^ (x << 31 | x >>> 33) ^ (x << 37 | x >>> 27)) | 1L;
		return (int) (y ^ y >>> 31) >>> (32 - bits);
	}

	@Override
	public JadeRandom copy () {
		return new JadeRandom(stateA, stateB);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		JadeRandom that = (JadeRandom)o;

		return stateA == that.stateA && stateB == that.stateB;
	}

	public String toString () {
		return "JadeRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L}";
	}

	public static void main(String[] args) {
		JadeRandom random = new JadeRandom(1L);
		long n0 = random.nextLong();
		long n1 = random.nextLong();
		long n2 = random.nextLong();
		long n3 = random.nextLong();
		long n4 = random.nextLong();
		long n5 = random.nextLong();
		long n6 = random.nextLong();
		long p5 = random.previousLong();
		long p4 = random.previousLong();
		long p3 = random.previousLong();
		long p2 = random.previousLong();
		long p1 = random.previousLong();
		long p0 = random.previousLong();
		System.out.println(n0 == p0);
		System.out.println(n1 == p1);
		System.out.println(n2 == p2);
		System.out.println(n3 == p3);
		System.out.println(n4 == p4);
		System.out.println(n5 == p5);
		System.out.println(n0 + " vs. " + p0);
		System.out.println(n1 + " vs. " + p1);
		System.out.println(n2 + " vs. " + p2);
		System.out.println(n3 + " vs. " + p3);
		System.out.println(n4 + " vs. " + p4);
		System.out.println(n5 + " vs. " + p5);
	}
}
