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
public class HarshRandom extends EnhancedRandom {
	@Override
	public String getTag() {
		return "HarR";
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
	 * Creates a new MarshRandom with a random state.
	 */
	public HarshRandom() {
		stateA = EnhancedRandom.seedFromMath();
		stateB = EnhancedRandom.seedFromMath();
	}

	/**
	 * Creates a new MarshRandom with the given seed; all {@code long} values are permitted.
	 * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
	 *
	 * @param seed any {@code long} value
	 */
	public HarshRandom(long seed) {
		setSeed(seed);
	}

	/**
	 * Creates a new MarshRandom with the given two states; all {@code long} values are permitted.
	 * These states will be used verbatim for stateA and stateB.
	 *
	 * @param stateA any {@code long} value
	 * @param stateB any {@code long} value
	 */
	public HarshRandom(long stateA, long stateB) {
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
	 * between 0 and 1, inclusive; if it is any other value this gets state B as if 1 was given.
	 *
	 * @param selection used to select which state variable to get; generally 0 or 1
	 * @return the value of the selected state
	 */
	@Override
	public long getSelectedState (int selection) {
		switch (selection) {
			case 0:
				return stateA;
			default:
				return stateB;
		}
	}

	/**
	 * Sets one of the states, determined by {@code selection}, to {@code value}, as-is.
	 * Selections 0 and 1 refer to states A and B, and if the selection is anything
	 * else, this ignores it and sets nothing.
	 *
	 * @param selection used to select which state variable to set; generally 0 or 1
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
		}
	}

	/**
	 * This initializes both states of the generator to random values based on the given seed.
	 * (2 to the 64) possible initial generator states can be produced here, though there are
	 * (2 to the 192) possible states in total.
	 *
	 * @param seed the initial seed; may be any long
	 */
	@Override
	public void setSeed (long seed) {
		// This is based on MX3, but pulls out values and assigns them to states mid-way, sometimes XORing them.
		seed ^= seed >>> 32;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 29;
		seed *= 0xbea225f9eb34556dL;
		seed ^= seed >>> 32;
		stateA = (seed ^ 0xC6BC279692B5C323L);
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
	 * This is the same as calling {@link #setStateA(long)} and {@link #setStateB(long)} as a group.
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
		long a = (stateA += 0xDE916ABCC965815BL); // the eighth number from the 39-dimensional harmonious sequence
		long b = (stateB += 0xF1357AEA2E62A9C5L); // from https://arxiv.org/abs/2001.05304 (a 64-bit MCG constant)
		a += (b << 17 | b >>> 47);
		a = (a ^ a >>> 27) * 0x3C79AC492BA7B653L; // from http://mostlymangling.blogspot.com/2019/12/stronger-better-morer-moremur-better.html
		a = (a ^ a >>> 33) * 0x1C69B3F74AC4AE35L;
		return a ^ a >>> 27;
	}

	@Override
	public long skip(long advance) {
		long a = (stateA += 0xDE916ABCC965815BL * advance);
		long b = (stateB += 0xF1357AEA2E62A9C5L * advance);
		a += (b << 17 | b >>> 47);
		a = (a ^ a >>> 27) * 0x3C79AC492BA7B653L;
		a = (a ^ a >>> 33) * 0x1C69B3F74AC4AE35L;
		return a ^ a >>> 27;
	}

	@Override
	public long previousLong () {
        long a = stateA -= 0xDE916ABCC965815BL;
		long b = stateB -= 0xF1357AEA2E62A9C5L;
		a += (b << 17 | b >>> 47);
		a = (a ^ a >>> 27) * 0x3C79AC492BA7B653L;
		a = (a ^ a >>> 33) * 0x1C69B3F74AC4AE35L;
		return a ^ a >>> 27;
	}

	@Override
	public int next (int bits) {
		long a = (stateA += 0xDE916ABCC965815BL);
		long b = (stateB += 0xF1357AEA2E62A9C5L);
		a += (b << 17 | b >>> 47);
		a = (a ^ a >>> 27) * 0x3C79AC492BA7B653L;
		a = (a ^ a >>> 33) * 0x1C69B3F74AC4AE35L;
		return (int)(a ^ a >>> 27) >>> (32 - bits);
	}

	@Override
	public HarshRandom copy () {
		return new HarshRandom(stateA, stateB);
	}

	/**
	 * Gets a long that identifies which of the 2 to the 64 possible streams this is on.
	 * If the streams are different for two generators, their output should be very different.
	 * <br>
	 * This takes constant time.
	 *
	 * @return a long that identifies which stream the main state of the generator is on
	 */
	public long getStream() {
		return stateB * 0x781494A55DAAED0DL - stateA * 0xF8B010FB25FEC6D3L;
	}

	/**
	 * Changes the generator's stream to any of the 2 to the 64 possible streams this can be on.
	 * The {@code stream} this takes uses the same numbering convention used by {@link #getStream()} and
	 * {@link #shiftStream(long)}. This makes an absolute change to the stream, while shiftStream() is relative.
	 * <br>
	 * This takes constant time.
	 *
	 * @param stream the number of the stream to change to; may be any long
	 */
	public void setStream(long stream) {
		stateB += 0xF1357AEA2E62A9C5L * (stream - (stateB * 0x781494A55DAAED0DL - stateA * 0xF8B010FB25FEC6D3L));
	}

	/**
	 * Adjusts the generator's stream "up" or "down" to any of the 2 to the 64 possible streams this can be on.
	 * The {@code difference} this takes will be the difference between the result of {@link #getStream()} before
	 * the shift, and after the shift. This makes a relative change to the stream, while setStream() is absolute.
	 * <br>
	 * This takes constant time.
	 *
	 * @param difference how much to change the stream by; may be any long
	 */
	public void shiftStream(long difference) {
		stateB += 0xF1357AEA2E62A9C5L * difference;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		HarshRandom that = (HarshRandom)o;

		return stateA == that.stateA && stateB == that.stateB;
	}

	public String toString () {
		return "MarshRandom{" + "stateA=" + (stateA) + "L, stateB=" + (stateB) + "L}";
	}

	public static void main(String[] args) {
		HarshRandom random = new HarshRandom(1L);
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
