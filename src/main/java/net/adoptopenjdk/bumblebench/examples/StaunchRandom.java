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
public class StaunchRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "StuR";
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
	 * Creates a new StaunchRandom with a random state.
	 */
	public StaunchRandom() {
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
		stateC = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new StaunchRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public StaunchRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new StaunchRandom with the given three states; all {@code long} values are permitted.
	 * These states will be used verbatim.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 * @param stateC any {@code long} value
	 */
	public StaunchRandom(long stateA, long stateB, long stateC) {
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
	 * Selections 0, 1, and 2 refer to states A, B, and C and if the selection is anything
	 * else, this treats it as 2 and sets stateC.
	 *
	 * @param selection used to select which state variable to set; generally 0, 1, or 2
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
		stateA = seed ^ 0xC6BC279692B5C323L;
		stateB = ~seed;
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
		long fa = (stateA += 0xD1B54A32D192ED03L);
		long fb = (stateB += 0xABC98388FB8FAC03L);
		long fc = (stateC += 0x8CB92BA72F3D8DD7L);
		fb += fa ^ (fa << 29 | fa >>> 35) ^ (fa << 53 | fa >>> 11);
		fc += fb ^ (fb <<  7 | fb >>> 57) ^ (fb << 19 | fb >>> 45);
		fa += fc ^ (fc << 37 | fc >>> 27) ^ (fc << 47 | fc >>> 17);
		return fa;
	}

	@Override
	public long skip(long advance) {
		long fa = (stateA += 0xD1B54A32D192ED03L * advance);
		long fb = (stateB += 0xABC98388FB8FAC03L * advance);
		long fc = (stateC += 0x8CB92BA72F3D8DD7L * advance);
		fb += fa ^ (fa << 29 | fa >>> 35) ^ (fa << 53 | fa >>> 11);
		fc += fb ^ (fb <<  7 | fb >>> 57) ^ (fb << 19 | fb >>> 45);
		fa += fc ^ (fc << 37 | fc >>> 27) ^ (fc << 47 | fc >>> 17);
		return fa;

	}

	@Override
	public long previousLong () {
		long fa = stateA;
		long fb = stateB;
		long fc = stateC;
		stateA -= 0xD1B54A32D192ED03L;
		stateB -= 0xABC98388FB8FAC03L;
		stateC -= 0x8CB92BA72F3D8DD7L;
		fb += fa ^ (fa << 29 | fa >>> 35) ^ (fa << 53 | fa >>> 11);
		fc += fb ^ (fb <<  7 | fb >>> 57) ^ (fb << 19 | fb >>> 45);
		fa += fc ^ (fc << 37 | fc >>> 27) ^ (fc << 47 | fc >>> 17);
		return fa;
	}

	@Override
	public int next (int bits) {
		long fa = (stateA += 0xD1B54A32D192ED03L);
		long fb = (stateB += 0xABC98388FB8FAC03L);
		long fc = (stateC += 0x8CB92BA72F3D8DD7L);
		fb += fa ^ (fa << 29 | fa >>> 35) ^ (fa << 53 | fa >>> 11);
		fc += fb ^ (fb <<  7 | fb >>> 57) ^ (fb << 19 | fb >>> 45);
		fa += fc ^ (fc << 37 | fc >>> 27) ^ (fc << 47 | fc >>> 17);
		return (int)fa >>> (32 - bits);
	}

	@Override
	public StaunchRandom copy () {
		return new StaunchRandom(stateA, stateB, stateC);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		StaunchRandom that = (StaunchRandom)o;

		return stateA == that.stateA && stateB == that.stateB && stateC == that.stateC;
	}

	public String toString () {
		return "StaunchRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L, stateC=" + (stateC) + "L}";
	}

//	public static void main(String[] args) {
//		StaunchRandom random = new StaunchRandom(1L);
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
