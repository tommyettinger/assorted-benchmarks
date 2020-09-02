/* ******************************************************************************
 * Copyright 2020 See AUTHORS file.
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
 ******************************************************************************/
package com.github.tommyettinger.squidlib;

import static com.badlogic.gdx.math.MathUtils.*;

/**
 * Math helper functions.
 */
public final class GtMathUtils {
    private GtMathUtils () {
    }

    private static final int ATAN2_SIZE = 10318;
    // ATAN2_SIZE was optimized for for LCH color interpolation accuracy. ColorConversion is off by as much as 1/255
    // for any of the three color channels. This is the minimum look-up table size to avoid increasing this maximum
    // error in LCH ColorConversion.
    private static final float[] ATAN2 = new float[ATAN2_SIZE + 1];
    static {
        for (int i = 0; i <= ATAN2_SIZE; i++) {
            ATAN2[i] = (float) Math.atan2((double) i / ATAN2_SIZE, 1.0);
        }
    }

    /**
     * Fast atan2, based on a look-up-table. More accurate than MathUtils.atan2. Average error 0.0004 radians
     * (0.022 degrees), largest error of 0.00010 radians (0.056 degrees).
     * <p>
     * Thanks to Icecore on JavaGaming.org for the algorithm, and to mooman219 on JavaGaming.org for benchmarking it.
     * These accuracy values are the same as mooman219's despite the much smaller look-up table.
     * @param y arctan numerator
     * @param x arctan denominator
     * @return A fast approximate atan2 angle in radians.
     */
    static public float atan2 (float y, float x) {
        if (y < 0) {
            if (x < 0) {
                if (y < x) {
                    return -PI / 2 - ATAN2[(int) (x / y * ATAN2_SIZE)];
                } else {
                    return -PI + ATAN2[(int) (y / x * ATAN2_SIZE)];
                }
            } else {
                y = -y;
                if (y > x) {
                    return -PI / 2 + ATAN2[(int) (x / y * ATAN2_SIZE)];
                } else {
                    return -ATAN2[(int) (y / x * ATAN2_SIZE)];
                }
            }
        } else {
            if (x < 0) {
                x = -x;
                if (y > x) {
                    return PI / 2 + ATAN2[(int) (x / y * ATAN2_SIZE)];
                } else {
                    return PI - ATAN2[(int) (y / x * ATAN2_SIZE)];
                }
            } else {
                if (y > x) {
                    return PI / 2 - ATAN2[(int) (x / y * ATAN2_SIZE)];
                } else {
                    return ATAN2[(int) (y / x * ATAN2_SIZE)];
                }
            }
        }
    }
    
    static public float atan2_imuli0(float y, float x){

        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;                                                  // [0,1]
        float th = (0.97239411f - 0.19194795f * z * z) * z;                                // [0,π/4]
        if(invert) th = 1.5707963267948966f - th;                                           // [0,π/2]
        if(x < 0) th = 3.141592653589793f - th;                                            // [0,π]
        return Math.copySign(th, y);                                                       // [-π,π]
    }
    
    static public float atan2_imuli2(float y, float x){

        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;                                                  // [0,1]
        float th = ((((-0.060317f * z) - 0.198146f) * z) + 1.044261f) * z - 0.002178f;     // [0,π/4]
        if(invert) th = 1.5707963267948966f - th;                                           // [0,π/2]
        if(x < 0) th = 3.141592653589793f - th;                                            // [0,π]
        return Math.copySign(th, y);                                                       // [-π,π]
    }
    static public float atan2_quartic(float y, float x){

        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;
        z = ((((0.141499f * z) - 0.343315f) * z - 0.016224f) * z + 1.003839f) * z - 0.000158f;
        if(invert) z = 1.5707963267948966f - z;
        if(x < 0) z = 3.141592653589793f - z;
        return Math.copySign(z, y);
    }

    static public float atan2_nt(float y, float x)
    {
        if (x != 0.0f)
        {
            if (Math.abs(x) > Math.abs(y))
            { 
                final float z = y / x;
                if (x > 0.0)
                {
                    // atan2(y,x) = atan(y/x) if x > 0
                    return (0.97239411f - 0.19194795f * z * z) * z;
                }
                else if (y >= 0.0)
                {
                    // atan2(y,x) = atan(y/x) + PI if x < 0, y >= 0
                    return (0.97239411f - 0.19194795f * z * z) * z + PI;
                }
                else
                {
                    // atan2(y,x) = atan(y/x) - PI if x < 0, y < 0
                    return (0.97239411f - 0.19194795f * z * z) * z - PI;
                }
            }
            else // Use property atan(y/x) = PI/2 - atan(x/y) if |y/x| > 1.
            {
                final float z = x / y;
                if (y > 0.0)
                {
                    // atan2(y,x) = PI/2 - atan(x/y) if |y/x| > 1, y > 0
                    return 1.5707963267948966f - (0.97239411f - 0.19194795f * z * z) * z;
                }
                else
                {
                    // atan2(y,x) = -PI/2 - atan(x/y) if |y/x| > 1, y < 0
                    return -1.5707963267948966f - (0.97239411f - 0.19194795f * z * z) * z;
                }
            }
        }
        else
        {
            if (y > 0.0f) // x = 0, y > 0
            {
                return 1.5707963267948966f;
            }
            else if (y < 0.0f) // x = 0, y < 0
            {
                return -1.5707963267948966f;
            }
        }
        return 0.0f; // x,y = 0. Could return NaN instead.
    }

    /**
     * Computes the common definition mathematical modulo of the given dividend and divisor, that is, the one with the
     * least positive remainder. The result is always between 0 (inclusive) and the absolute value of the divisor
     * (exclusive). This is useful for converting any angle, even negative angles, to the range {@code 0..360} or
     * {@code 0..2Pi}.
     * @param dividend Dividend (top number) of the Euclidean division.
     * @param divisor Divisor (bottom number) of the Euclidean division.
     * @return The least positive remainder of the division operation.
     */
    public static int modulo (int dividend, int divisor) {
        int mod = dividend % divisor;
        if (mod < 0) {
            mod = (divisor < 0) ? mod - divisor : mod + divisor;
        }
        return mod;
    }

    /**
     * Computes the common definition mathematical modulo of the given dividend and divisor, that is, the one with the
     * least positive remainder. The result is always between 0 (inclusive) and the absolute value of the divisor
     * (exclusive). This is useful for converting any angle, even negative angles, to the range {@code 0..360} or
     * {@code 0..2Pi}.
     * @param dividend Dividend (top number) of the Euclidean division.
     * @param divisor Divisor (bottom number) of the Euclidean division.
     * @return The least positive remainder of the division operation.
     */
    public static float modulo (float dividend, float divisor) {
        float mod = dividend % divisor;
        if (mod < 0f) {
            mod = (divisor < 0f) ? mod - divisor : mod + divisor;
        }
        return mod;
    }
}
