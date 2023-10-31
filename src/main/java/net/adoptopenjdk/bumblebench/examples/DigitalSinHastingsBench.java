/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

package net.adoptopenjdk.bumblebench.examples;

import net.adoptopenjdk.bumblebench.core.MicroBench;

import static com.github.tommyettinger.digital.TrigTools.PI_INVERSE;


/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 *
 * <br>
 * GraalVM Java 20:
 * <br>
 *
 */
public final class DigitalSinHastingsBench extends MicroBench {
	/**
	 * Credit to <a href="https://stackoverflow.com/a/524606">Darius Bacon's Stack Overflow answer</a>.
	 * The algorithm is by Hastings, from Approximations For Digital Computers.
	 * The use of a triangle wave to reduce the range was my idea. This doesn't use a LUT.
	 * @param radians the angle to get the sine of, in radians
	 * @return the sine of the given angle
	 */
	public static float sinHastings(float radians) {
		radians = radians * (PI_INVERSE * 0.5f) + 0.25f;
		radians = 4f * Math.abs(radians - ((int)(radians + 16384.5) - 16384)) - 1f;
		float r2 = radians * radians;
		return ((((0.00015148419f * r2
				- 0.00467376557f) * r2
				+ 0.07968967928f) * r2
				- 0.64596371106f) * r2
				+ 1.57079631847f) * radians;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		for (long i = 0L, bits = 123L; i < numIterations; i++, bits += 0x9E3779B97F4A7C15L) {
			sum -= sinHastings(
					Float.intBitsToFloat(129 - Long.numberOfLeadingZeros(bits) << 23 | ((int) bits & 0x807FFFFF))
			);
		}
		return numIterations;
	}
}

/* OLD
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * DigitalSinHastingsBench score: 44779748.000000 (44.78M 1761.7%)
 *                     uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalSinHastingsBench score: 43633048.000000 (43.63M 1759.1%)
 *                     uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalSinHastingsBench score: 44882296.000000 (44.88M 1762.0%)
 *                     uncertainty:   0.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalSinHastingsBench score: 44730416.000000 (44.73M 1761.6%)
 *                     uncertainty:   0.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalSinHastingsBench score: 45279212.000000 (45.28M 1762.8%)
 *                     uncertainty:   1.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * DigitalSinHastingsBench score: 43640848.000000 (43.64M 1759.2%)
 *                     uncertainty:   0.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalSinHastingsBench score: 44826616.000000 (44.83M 1761.8%)
 *                     uncertainty:   0.2%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalSinHastingsBench score: 44618220.000000 (44.62M 1761.4%)
 *                     uncertainty:   0.2%
 * <br>
 * HotSpot Java 20 (BellSoft):
 * <br>
 * DigitalSinHastingsBench score: 44845940.000000 (44.85M 1761.9%)
 *                     uncertainty:   0.1%
 * <br>
 * GraalVM Java 20:
 * <br>
 * DigitalSinHastingsBench score: 45158560.000000 (45.16M 1762.6%)
 *                     uncertainty:   0.9%
 */