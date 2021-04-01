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

/**
 * This implements the atan() approximation from sheet 9 of RAND Corporation's 1955 research study,
 * Approximations for Digital Computers. The copy used was https://www.researchgate.net/publication/318310473_Hastings%27_Approximations_for_Digital_Computers_Hastings_1955
 * <br>
 * Accuracy: absolute error 0.000051338, relative error -0.000000040, max error 0.000081490
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * Java 15 Hotspot:
 * <br>
 * ATan9FloatBench score: 63745312.000000 (63.75M 1797.0%)
 *             uncertainty:   2.1%
 */
public final class ATan9FloatBench extends MicroBench {
//	public static float atan(final float v) {
//		final float n = Math.abs(v);
//		final float x = (n - 1f) / (n + 1f);
//		final float x2 = x * x;
//		final float x3 = x * x2;
//		final float x5 = x3 * x2;
//		final float x7 = x5 * x2;
//		return Math.copySign(0.7853981633974483f +
//				(0.999215f * x - 0.3211819f * x3 + 0.1462766f * x5 - 0.0389929f * x7), v);
//	}
//	public static float atan2(float y, float x) {
//		if(x > 0)
//			return atan(y / x);
//		else if(x < 0) {
//			if(y >= 0)
//				return atan(y / x) + 3.14159265358979323846f;
//			else
//				return atan(y / x) - 3.14159265358979323846f;
//		}
//		else if(y > 0) return 1.5707963267948966f;
//		else if(y < 0) return -1.5707963267948966f;
//		else return 0.0f;
//	}

	/**
	 * Arc tangent approximation with very low error, using an algorithm from the 1955 research study
	 * "Approximations for Digital Computers," by RAND Corporation (this is sheet 9's algorithm, which is the
	 * second-fastest and second-least precise). This method is usually much faster than {@link Math#atan(double)},
	 * but is somewhat less precise than Math's implementation.
	 * @param i an input to the inverse tangent function; any finite double is accepted
	 * @return an output from the inverse tangent function, from PI/-2.0 to PI/2.0 inclusive
	 */
	public static double atan(final double i) {
		final double n = Math.min(Math.abs(i), Double.MAX_VALUE);
		final double c = (n - 1.0) / (n + 1.0);
		final double c2 = c * c;
		final double c3 = c * c2;
		final double c5 = c3 * c2;
		final double c7 = c5 * c2;
		return Math.copySign(0.7853981633974483 +
				(0.999215 * c - 0.3211819 * c3 + 0.1462766 * c5 - 0.0389929 * c7), i);
	}

	/**
	 * Arc tangent approximation with very low error, using an algorithm from the 1955 research study
	 * "Approximations for Digital Computers," by RAND Corporation (this is sheet 9's algorithm, which is the
	 * second-fastest and second-least precise). This method is usually much faster than {@link Math#atan(double)},
	 * but is somewhat less precise than Math's implementation.
	 * @param i an input to the inverse tangent function; any finite float is accepted
	 * @return an output from the inverse tangent function, from PI/-2.0 to PI/2.0 inclusive
	 */
	public static float atan(final float i) {
		final float n = Math.min(Math.abs(i), Float.MAX_VALUE);
		final float c = (n - 1f) / (n + 1f);
		final float c2 = c * c;
		final float c3 = c * c2;
		final float c5 = c3 * c2;
		final float c7 = c5 * c2;
		return Math.copySign(0.7853981633974483f +
				(0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7), i);
	}

	/**
	 * Close approximation of the frequently-used trigonometric method atan2, with higher precision than libGDX's atan2
	 * approximation. Maximum error is below 0.00009 radians.
	 * Takes y and x (in that unusual order) as doubles, and returns the angle from the origin to that point in radians.
	 * It is about 5 times faster than {@link Math#atan2(double, double)} (roughly 12 ns instead of roughly 62 ns for
	 * Math, on Java 8 HotSpot). It is slightly faster than libGDX' MathUtils approximation of the same method;
	 * MathUtils seems to have worse average error, though.
	 * <br>
	 * Credit for this goes to the 1955 research study "Approximations for Digital Computers," by RAND Corporation. This
	 * is sheet 9's algorithm, which is the second-fastest and second-least precise. The algorithm on sheet 8 is faster,
	 * but only by a very small degree, and is considerably less precise. That study provides an {@link #atan(double)}
	 * method, and the small code to make that work as atan2() was worked out from Wikipedia.
	 * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
	 * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
	 * @return the angle to the given point, in radians as a double; ranges from -PI to PI
	 */
	public static double atan2(final double y, final double x) {
		if(x > 0)
			return atan(y / x);
		else if(x < 0) {
			if(y >= 0)
				return atan(y / x) + 3.14159265358979323846;
			else
				return atan(y / x) - 3.14159265358979323846;
		}
		else if(y > 0) return x + 1.5707963267948966;
		else if(y < 0) return x - 1.5707963267948966;
		else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
	}

	/**
	 * Close approximation of the frequently-used trigonometric method atan2, with higher precision than libGDX's atan2
	 * approximation. Maximum error is below 0.00009 radians.
	 * Takes y and x (in that unusual order) as floats, and returns the angle from the origin to that point in radians.
	 * It is about 5 times faster than {@link Math#atan2(double, double)} (roughly 12 ns instead of roughly 62 ns for
	 * Math, on Java 8 HotSpot). It is slightly faster than libGDX' MathUtils approximation of the same method;
	 * MathUtils seems to have worse average error, though.
	 * <br>
	 * Credit for this goes to the 1955 research study "Approximations for Digital Computers," by RAND Corporation. This
	 * is sheet 9's algorithm, which is the second-fastest and second-least precise. The algorithm on sheet 8 is faster,
	 * but only by a very small degree, and is considerably less precise. That study provides an {@link #atan(float)}
	 * method, and the small code to make that work as atan2() was worked out from Wikipedia.
	 * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
	 * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
	 * @return the angle to the given point, in radians as a float; ranges from -PI to PI
	 */
	public static float atan2(final float y, final float x) {
		if(x > 0)
			return atan(y / x);
		else if(x < 0) {
			if(y >= 0)
				return atan(y / x) + 3.14159265358979323846f;
			else
				return atan(y / x) - 3.14159265358979323846f;
		}
		else if(y > 0) return x + 1.5707963267948966f;
		else if(y < 0) return x - 1.5707963267948966f;
		else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 0.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= atan((sum + i) * shrink);
		return numIterations;
	}

	public static void main(String[] args) {
		{
			System.out.println("atan()");
			double absolute = 0.0, relative = 0.0, max = 0.0;
			float ctr = -4f;
			for (int i = 0; i < 2048; i++) {
				final double error = Math.atan(ctr) - atan(ctr);
				relative += error;
				max = Math.max(max, Math.abs(error));
				absolute += Math.abs(error);
				ctr += 0x1p-8f;
			}
			System.out.printf("Accuracy: absolute error %2.9f, relative error %2.9f, max error %2.9f",
					absolute * 0x1p-11, relative * 0x1p-11, max);
		}
		{
			System.out.println("\n\natan2()");
			double absolute = 0.0, relative = 0.0, max = 0.0;
			float xc = -4f, yc = -4f;
			for (int i = 0; i < 256; i++) {
				for (int j = 0; j < 256; j++) {
					final double error = Math.atan2(yc, xc) - atan2(yc, xc);
					relative += error;
					max = Math.max(max, Math.abs(error));
					if(error <= -1)
						System.out.printf("atan2(%f, %f) has error %f\n", yc, xc, error);
					absolute += Math.abs(error);
					xc += 0x1p-5f;
				}
				yc += 0x1p-5f;
				xc = -4f;
			}
			System.out.printf("Accuracy: absolute error %2.9f, relative error %2.9f, max error %2.9f",
					absolute * 0x1p-16, relative * 0x1p-16, max);
		}
	}
}
