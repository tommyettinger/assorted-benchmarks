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
public class SplatterRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "SplR";
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
	protected long stateE;
	protected long stateF;
	protected long stateG;

	/**
	 * Creates a new TrimPostRandom with a random state.
	 */
	public SplatterRandom() {
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
		stateC = EnhancedRandom.seedFromMath();
		stateD = EnhancedRandom.seedFromMath();
		stateE = EnhancedRandom.seedFromMath();
		stateF = EnhancedRandom.seedFromMath();
		stateG = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new TrimPostRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public SplatterRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new TrimPostRandom with the given four states; all {@code long} values are permitted.
	 * These states will be used verbatim.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 * @param stateC any {@code long} value
	 * @param stateD any {@code long} value
	 */
	public SplatterRandom(long stateA, long stateB, long stateC, long stateD, long stateE, long stateF, long stateG) {
		this.stateA = stateA;
		this.stateB = stateB;
		this.stateC = stateC;
		this.stateD = stateD;
		this.stateE = stateE;
		this.stateF = stateF;
		this.stateG = stateG;
	}

	/**
	 * This generator has 7 {@code long} states, so this returns 7.
	 *
	 * @return 7 (seven)
	 */
	@Override
	public int getStateCount () {
		return 7;
	}

	/**
	 * Gets the state determined by {@code selection}, as-is. The value for selection should be
	 * between 0 and 3, inclusive; if it is any other value this gets state D as if 3 was given.
	 *
	 * @param selection used to select which state variable to get; generally 0, 1, 2, or 3
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
		case 4:
			return stateE;
		case 5:
			return stateF;
		default:
			return stateG;
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
		case 2:
			stateC = value;
			break;
		case 3:
			stateD = value;
			break;
		case 4:
			stateE = value;
			break;
		case 5:
			stateF = value;
			break;
		default:
			stateG = value;
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
		stateD = seed;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		stateE = seed ^ 0xD1342543DE82EF95L;
		stateF = ~seed;
		stateG = seed ^ ~0xD1342543DE82EF95L;
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

	/**
	 * Sets the state completely to the given four state variables.
	 * This is the same as calling {@link #setStateA(long)}, {@link #setStateB(long)},
	 * {@link #setStateC(long)}, and {@link #setStateD(long)} as a group.
	 *
	 * @param stateA the first state; can be any long
	 * @param stateB the second state; can be any long
	 * @param stateC the third state; can be any long
	 * @param stateD the fourth state; can be any long
	 */
	@Override
	public void setState (long stateA, long stateB, long stateC, long stateD) {
		this.stateA = stateA;
		this.stateB = stateB;
		this.stateC = stateC;
		this.stateD = stateD;
		this.stateE = ~stateB;
		this.stateF = ~stateC;
		this.stateG = ~stateD;
	}

	@Override
	public long nextLong () {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		final long fd = stateD;
		final long fe = stateE;
		final long ff = stateF;
		final long fg = stateG;
		stateA = fa + 0xDE916ABCC965815BL;
		stateB = (fg << 57 | fg >>> 7);
		stateC = (fe << 18 | fe >>> 46);
		stateD = (ff << 43 | ff >>> 21);
		stateE = fb + fa;
		stateF = fc + fg;
		stateG = fd + fe;
		return ff;
	}

	@Override
	public long previousLong () {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		stateD -= 0xDE916ABCC965815BL;
		long t = (fb >>> 18 | fb << 46);
		stateC = t ^ stateD;
		t = (fa >>> 57 | fa << 7);
		stateB = t ^ stateC;
		stateA = fc - t;
		return stateC;
	}

	@Override
	public int next (int bits) {
		final long fa = stateA;
		final long fb = stateB;
		final long fc = stateC;
		final long fd = stateD;
		final long fe = stateE;
		final long ff = stateF;
		final long fg = stateG;
		stateA = fa + 0xDE916ABCC965815BL;
		stateB = (fg << 57 | fg >>> 7);
		stateC = (fe << 18 | fe >>> 46);
		stateD = (ff << 43 | ff >>> 21);
		stateE = fb + fa;
		stateF = fc + fg;
		stateG = fd + fe;
		return (int)ff >>> (32 - bits);
	}

	@Override
	public SplatterRandom copy () {
		return new SplatterRandom(stateA, stateB, stateC, stateD, stateE, stateF, stateG);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SplatterRandom that = (SplatterRandom) o;

		if (stateA != that.stateA) return false;
		if (stateB != that.stateB) return false;
		if (stateC != that.stateC) return false;
		if (stateD != that.stateD) return false;
		if (stateE != that.stateE) return false;
		if (stateF != that.stateF) return false;
		return stateG == that.stateG;
	}

	public String toString () {
		return "SplatterRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L, stateC=" + (stateC) +
				"L, stateD=" + (stateD) + "L, stateE=" + (stateE) + "L, stateF=" + (stateF) + "L, stateG=" + (stateG) +  "L}";
	}
}
