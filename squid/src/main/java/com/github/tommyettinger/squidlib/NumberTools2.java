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

import com.github.tommyettinger.digital.TrigTools;

import static com.badlogic.gdx.math.MathUtils.PI;
import static com.github.tommyettinger.digital.TrigTools.*;
import static com.github.tommyettinger.digital.TrigTools.SIN_TABLE;

/**
 * Math helper functions.
 */
public final class NumberTools2 {
    private NumberTools2() {
    }
    
    static public float atan2_imuli0(float y, float x){

        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;                                                  // [0,1]
        float th = (0.97239411f - 0.19194795f * z * z) * z;                                // [0,π/4]
        if(invert) th = 1.5707963267948966f - th;                                          // [0,π/2]
        if(x < 0) th = 3.141592653589793f - th;                                            // [0,π]
        return Math.copySign(th, y);                                                       // [-π,π]
    }
    
    public static float atan2_imuli_(float y, float x){
        if (y == 0f && x >= 0f) return 0f;
        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;
        z = (((((0.022520265292560102f) * z) - (0.054640279287594046f)) * z - (0.0025821297967229097f)) * z + (0.1597659389184251f)) * z - (0.000025146481008519463f);
        if(invert) z = 0.25f - z;
        if(x < 0) z = 0.5f - z;
        return y < 0 ? 1f - z : z;
    }

    /**
     * Credit to imuli and Nic Taylor; imuli commented on
     * <a href="https://www.dsprelated.com/showarticle/1052.php">Taylor's article</a> with very useful info.
     * @param y
     * @param x
     * @return
     */
    // credit to https://www.dsprelated.com/showarticle/1052.php
    public static float atan2_quartic(float y, float x)
    {
        if (y == 0f && x >= 0f) return 0f;
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
                    return -1.5707964f - (0.97239411f - 0.19194795f * z * z) * z;
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
                return -1.5707964f;
            }
        }
        return 0.0f; // x,y = 0. Could return NaN instead.
    }


    /**
     * Arc tangent approximation with very low error, using an algorithm from the 1955 research study
     * "Approximations for Digital Computers," by RAND Corporation (this is sheet 9's algorithm, which is the
     * second-fastest and second-least precise). This method is usually much faster than {@link Math#atan(double)},
     * but is somewhat less precise than Math's implementation.
     * @param i an input to the inverse tangent function; any double is accepted
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
     * @param i an input to the inverse tangent function; any float is accepted
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

    private static double atn(final double i) {
        final double n = Math.abs(i);
        final double c = (n - 1.0) / (n + 1.0);
        final double c2 = c * c;
        final double c3 = c * c2;
        final double c5 = c3 * c2;
        final double c7 = c5 * c2;
        return Math.copySign(0.7853981633974483 +
                (0.999215 * c - 0.3211819 * c3 + 0.1462766 * c5 - 0.0389929 * c7), i);
    }

    private static float atn(final float i) {
        final float n = Math.abs(i);
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
    public static double atan2Simple(final double y, final double x) {
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
    public static float atan2Simple(final float y, final float x) {
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
    public static double atan2Funky(final double y, double x) {
        double n = y / x;
        if(n != n) n = (y == x ? 1.0 : -1.0); // if both y and x are infinite, n would be NaN
        else if(n - n != n - n) x = 0.0; // if n is infinite, y is infinitely larger than x.
        if(x > 0)
            return atn(n);
        else if(x < 0) {
            if(y >= 0)
                return atn(n) + 3.14159265358979323846;
            else
                return atn(n) - 3.14159265358979323846;
        }
        else if(y > 0) return x + 1.5707963267948966;
        else if(y < 0) return x - 1.5707963267948966;
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }
    public static float atan2Funky(final float y, float x) {
        float n = y / x;
        if(n != n) n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if(n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if(x > 0)
            return atn(n);
        else if(x < 0) {
            if(y >= 0)
                return atn(n) + 3.14159265358979323846f;
            else
                return atn(n) - 3.14159265358979323846f;
        }
        else if(y > 0) return x + 1.5707963267948966f;
        else if(y < 0) return x - 1.5707963267948966f;
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }
    /**
     * This one's weird; unlike {@link #atan2Simple_(double, double)}, it can return negative results.
     * @param v any finite double
     * @return between -0.25 and 0.25
     */
    private static double atan_(final double v) {
        final double n = Math.min(Math.abs(v), Double.MAX_VALUE);
        final double c = (n - 1.0) / (n + 1.0);
        final double c2 = c * c;
        final double c3 = c * c2;
        final double c5 = c3 * c2;
        final double c7 = c5 * c2;
        return Math.copySign(0.125 + 0.1590300064615682 * c - 0.051117687016646825 * c3 + 0.02328064394867594 * c5
                - 0.006205912780487965 * c7, v);
    }

    /**
     * This one's weird; unlike {@link #atan2Simple_(float, float)}, it can return negative results.
     * @param v any finite float
     * @return between -0.25 and 0.25
     */
    private static float atan_(final float v) {
        final float n = Math.min(Math.abs(v), Float.MAX_VALUE);
        final float c = (n - 1f) / (n + 1f);
        final float c2 = c * c;
        final float c3 = c * c2;
        final float c5 = c3 * c2;
        final float c7 = c5 * c2;
        return Math.copySign(0.125f + 0.1590300064615682f * c - 0.051117687016646825f * c3 + 0.02328064394867594f * c5
                - 0.006205912780487965f * c7, v);
    }
    private static double atn_(final double v) {
        final double n = Math.abs(v);
        final double c = (n - 1.0) / (n + 1.0);
        final double c2 = c * c;
        final double c3 = c * c2;
        final double c5 = c3 * c2;
        final double c7 = c5 * c2;
        return Math.copySign(0.125 + 0.1590300064615682 * c - 0.051117687016646825 * c3 + 0.02328064394867594 * c5
                - 0.006205912780487965 * c7, v);
    }

    private static float atn_(final float v) {
        final float n = Math.abs(v);
        final float c = (n - 1f) / (n + 1f);
        final float c2 = c * c;
        final float c3 = c * c2;
        final float c5 = c3 * c2;
        final float c7 = c5 * c2;
        return Math.copySign(0.125f + 0.1590300064615682f * c - 0.051117687016646825f * c3 + 0.02328064394867594f * c5
                - 0.006205912780487965f * c7, v);
    }
    /**
     * Altered-range approximation of the frequently-used trigonometric method atan2, taking y and x positions as
     * doubles and returning an angle measured in turns from 0.0 to 1.0 (inclusive), with one cycle over the range
     * equivalent to 360 degrees or 2PI radians. You can multiply the angle by {@code 6.2831855f} to change to radians,
     * or by {@code 360f} to change to degrees. Takes y and x (in that unusual order) as doubles. Will never return a
     * negative number, which may help avoid costly floating-point modulus when you actually want a positive number.
     * <br>
     * Credit for this goes to the 1955 research study "Approximations for Digital Computers," by RAND Corporation. This
     * is sheet 9's algorithm, which is the second-fastest and second-least precise. The algorithm on sheet 8 is faster,
     * but only by a very small degree, and is considerably less precise. That study provides an {@link #atan(float)}
     * method, and the small code to make that work as atan2_() was worked out from Wikipedia.
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, as a double from 0.0 to 1.0, inclusive
     */
    public static double atan2Simple_(final double y, final double x) {
        if(x > 0) {
            if(y >= 0)
                return atan_(y / x);
            else
                return atan_(y / x) + 1.0;
        }
        else if(x < 0) {
            return atan_(y / x) + 0.5;
        }
        else if(y > 0) return x + 0.25;
        else if(y < 0) return x + 0.75;
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    /**
     * Altered-range approximation of the frequently-used trigonometric method atan2, taking y and x positions as floats
     * and returning an angle measured in turns from 0.0f to 1.0f, with one cycle over the range equivalent to 360
     * degrees or 2PI radians. You can multiply the angle by {@code 6.2831855f} to change to radians, or by {@code 360f}
     * to change to degrees. Takes y and x (in that unusual order) as floats. Will never return a negative number, which
     * may help avoid costly floating-point modulus when you actually want a positive number.
     * <br>
     * Credit for this goes to the 1955 research study "Approximations for Digital Computers," by RAND Corporation. This
     * is sheet 9's algorithm, which is the second-fastest and second-least precise. The algorithm on sheet 8 is faster,
     * but only by a very small degree, and is considerably less precise. That study provides an {@link #atan(float)}
     * method, and the small code to make that work as atan2_() was worked out from Wikipedia.
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, as a float from 0.0f to 1.0f, inclusive
     */
    public static float atan2Simple_(final float y, final float x) {
        if(x > 0) {
            if(y >= 0)
                return atan_(y / x);
            else
                return atan_(y / x) + 1f;
        }
        else if(x < 0) {
            return atan_(y / x) + 0.5f;
        }
        else if(y > 0) return x + 0.25f;
        else if(y < 0) return x + 0.75f;
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }
    public static double atan2Funky_(final double y, double x) {
        double n = y / x;
        if(n != n) n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if(n - n != n - n) x = 0.0; // if n is infinite, y is infinitely larger than x.
        if(x > 0) {
            if(y >= 0)
                return atn_(n);
            else
                return atn_(n) + 1.0;
        }
        else if(x < 0) {
            return atn_(n) + 0.5;
        }
        else if(y > 0) return x + 0.25;
        else if(y < 0) return x + 0.75;
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }
    public static float atan2Funky_(final float y, float x) {
        float n = y / x;
        if(n != n) n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if(n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if(x > 0) {
            if(y >= 0)
                return atn_(n);
            else
                return atn_(n) + 1f;
        }
        else if(x < 0) {
            return atn_(n) + 0.5f;
        }
        else if(y > 0) return x + 0.25f;
        else if(y < 0) return x + 0.75f;
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    public static float atan2General(float y, float x) {
        if(x > 0) {
            if(y >= 0){
                final float n = y / x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return 0.7853981633974483f +
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            }
            else {
                final float n = y / -x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return -0.7853981633974483f -
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            }
        } else if(x < 0) {
            if (y >= 0) {
                final float n = y / -x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return 2.356194490192345f -
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            } else {
                final float n = y / x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return -2.356194490192345f +
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            }
        }
        else return Math.copySign(1.5707963267948966f, y);
    }
    /**
     * A variant on {@link #atan(float)} that does not tolerate infinite inputs, and is much more precise
     * because it does its internal processing with double-precision and does more steps of the approximation
     * than atan().
     * @param i any finite float
     * @return an output from the inverse tangent function, from PI/-2.0 to PI/2.0 inclusive
     */
    private static float atnHP (final double i) {
        final double n = Math.abs(i);
        final double c = (n - 1.0) / (n + 1.0);
        final double c2 = c * c;
        final double c3 = c * c2;
        final double c5 = c3 * c2;
        final double c7 = c5 * c2;
        final double c9 = c7 * c2;
        final double c11 = c9 * c2;
        return (float)Math.copySign((Math.PI * 0.25) +
                (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11), i);
        //intermediate, -4.99936
//		return Math.copySign(0.7853981633974483f +
//			(0.999866f * c - 0.3302995f * c3 + 0.180141f * c5 - 0.085133f * c7 + 0.0208351f * c9), i);
        //old, -5.00103
//		return Math.copySign(0.7853981633974483f +
//			(0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7), i);
    }

    /**
     * Close approximation of the frequently-used trigonometric method atan2, with higher precision than libGDX's atan2
     * approximation. Maximum error should be below 0.000002 radians.
     * Takes y and x (in that unusual order) as floats, and returns the angle from the origin to that point in radians.
     * It is about 4 times faster than {@link Math#atan2(double, double)} (roughly 14.6 ns instead of roughly 60.4 ns
     * for Math, on Java 16 HotSpot).
     * <br>
     * Credit for this goes to the 1955 research study "Approximations for Digital Computers," by RAND Corporation. This
     * is sheet 11's algorithm, which is the fourth-fastest and fourth-least precise. Other algorithms provided in that
     * study are faster, but none much more so; this algorithm was chosen both because it has low enough error to
     * compete with {@link Math#atan2(double, double)} in some usages, and because it's almost exactly the same speed as
     * the atan2() approximation previously used by libGDX (which was much less precise).
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, in radians as a float; ranges from -PI to PI
     */
    public static float atan2HP (final float y, float x) {
        float n = y / x;
        if(n != n) n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if(n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if(x > 0)
            return atnHP(n);
        else if(x < 0) {
            if(y >= 0)
                return atnHP(n) + PI;
            else
                return atnHP(n) - PI;
        }
        else if(y > 0) return x + (PI * 0.5f);
        else if(y < 0) return x - (PI * 0.5f);
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    public static float atan2Remez (final float y, float x) {
        float n = y / x;
        if(n != n) n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if(n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if(x > 0)
            return atanRemez(n);
        else if(x < 0) {
            if(y >= 0)
                return atanRemez(n) + PI;
            else
                return atanRemez(n) - PI;
        }
        else if(y > 0) return x + (PI * 0.5f);
        else if(y < 0) return x - (PI * 0.5f);
        else return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    private static float atanRemez(float n)
    {
        float u = 5.1222859e-2f, a, x;
        if(n > 1) {
            x = 1f/n;
            a = (PI * 0.5f);
            n = -1f;
        }
        else if(n < -1) {
            x = -1f/n;
            a = (PI * -0.5f);
            n = 1f;
        }
        else if(n < 0f){
            x = -n;
            a = 0f;
        }
        else{
            x = n;
            a = 0f;
        }
        u = u * x + -2.1796094e-1f;
        u = u * x + 3.0998923e-1f;
        u = u * x + -2.7039137e-2f;
        u = u * x + -3.3092569e-1f;
        u = u * x + 1.3779105e-4f;
        u = u * x + 9.9997404e-1f;
        return a + Math.copySign(u * x + 4.0811908e-7f, n);
    }

    static public float atan2OldGDX (float y, float x) {
        if (x == 0f) {
            if (y > 0f) return PI / 2;
            if (y == 0f) return 0f;
            return -PI / 2;
        }
        final float atan, z = y / x;
        if (Math.abs(z) < 1f) {
            atan = z / (1f + 0.28f * z * z);
            if (x < 0f) return atan + (y < 0f ? -PI : PI);
            return atan;
        }
        atan = PI / 2 - z / (z * z + 0.28f);
        return y < 0f ? atan - PI : atan;
    }
    /** A variant on {@link #atan(float)} that does not tolerate infinite inputs for speed reasons. This can be given a double
     * parameter, but is otherwise the same as atan(float), and returns a float like that method. It uses the same approximation,
     * from sheet 11 of "Approximations for Digital Computers." This is mostly meant to be used inside
     * {@link #atan2NewGDX(float, float)}, but it may be a tiny bit faster than atan(float) in other code.
     * @param i any finite double or float, but more commonly a float
     * @return an output from the inverse tangent function, from {@code -HALF_PI} to {@code HALF_PI} inclusive */
    public static float atanUnchecked (double i) {
        // We use double precision internally, because some constants need double precision.
        double n = Math.abs(i);
        // c uses the "equally-good" formulation that permits n to be from 0 to almost infinity.
        double c = (n - 1.0) / (n + 1.0);
        // The approximation needs 6 odd powers of c.
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return (float)(Math.signum(i) * ((Math.PI * 0.25)
                + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11)));
    }

    /** Close approximation of the frequently-used trigonometric method atan2, with higher precision than libGDX's atan2
     * approximation. Average error is 1.057E-6 radians; maximum error is 1.922E-6. Takes y and x (in that unusual order) as
     * floats, and returns the angle from the origin to that point in radians. It is about 4 times faster than
     * {@link Math#atan2(double, double)} (roughly 15 ns instead of roughly 60 ns for Math, on Java 8 HotSpot). <br>
     * Credit for this goes to the 1955 research study "Approximations for Digital Computers," by RAND Corporation. This is sheet
     * 11's algorithm, which is the fourth-fastest and fourth-least precise. The algorithms on sheets 8-10 are faster, but only by
     * a very small degree, and are considerably less precise. That study provides an {@link #atan(float)} method, and that cleanly
     * translates to atan2().
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, in radians as a float; ranges from {@code -PI} to {@code PI} */
    public static float atan2NewGDX (final float y, float x) {
        float n = y / x;
        if (n != n)
            n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if (n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if (x > 0)
            return atanUnchecked(n);
        else if (x < 0) {
            if (y >= 0) return atanUnchecked(n) + PI;
            return atanUnchecked(n) - PI;
        } else if (y > 0)
            return x + HALF_PI;
        else if (y < 0) return x - HALF_PI;
        return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    /*
    fn sine(x: f32) -> f32 {
    let coeffs = [
        -0.10132118f32,          // x
         0.0066208798f32,        // x^3
        -0.00017350505f32,       // x^5
         0.0000025222919f32,     // x^7
        -0.000000023317787f32,   // x^9
         0.00000000013291342f32, // x^11
    ];
    let pi_major = 3.1415927f32;
    let pi_minor = -0.00000008742278f32;
    let x2 = x*x;
    let p11 = coeffs[5];
    let p9  = p11*x2 + coeffs[4];
    let p7  = p9*x2  + coeffs[3];
    let p5  = p7*x2  + coeffs[2];
    let p3  = p5*x2  + coeffs[1];
    let p1  = p3*x2  + coeffs[0];
    (x - pi_major - pi_minor) *
    (x + pi_major + pi_minor) * p1 * x
}



    -3.1415926444234477f;
     2.0261194642649887f;
    -0.5240361513980939f;
     0.0751872634325299f;
    -0.006860187425683514f;
     0.000385937753182769f;
     */

    /**
     * Sine approximation by Colin Wallace. It is only defined over -pi to pi.
     * From <a href="https://web.archive.org/web/20200628195036/http://mooooo.ooo/chebyshev-sine-approximation/">Colin Wallace's blog</a>.
     * @param x
     * @return
     */
    public static float sinWallace(float x) {
        final float m = -0.00000008742278f;
        final float c0 = -0.10132118f;          // x
        final float c1 =  0.0066208798f;        // x^3
        final float c2 = -0.00017350505f;       // x^5
        final float c3 =  0.0000025222919f;     // x^7
        final float c4 = -0.000000023317787f;   // x^9
        final float c5 =  0.00000000013291342f; // x^11
        final float x2 = x * x;
        return (x - PI - m) * (x + PI + m) * (((((c5 * x2 + c4) * x2 + c3) * x2 + c2) * x2 + c1) * x2 + c0) * x;
    }

    /**
     * Sine approximation by Colin Wallace. It is defined everywhere, but its reduction likely has issues.
     * From <a href="https://web.archive.org/web/20200628195036/http://mooooo.ooo/chebyshev-sine-approximation/">Colin Wallace's blog</a>.
     * @param x
     * @return
     */
    public static float sinWallaceN(float x) {
        x /= PI;
        int floor = (x >= 0f ? (int)x : (int)x - 1) & -2;
        x -= floor;

        final float c0 = -3.1415926444234477f;
        final float c1 =  2.0261194642649887f;
        final float c2 = -0.5240361513980939f;
        final float c3 =  0.0751872634325299f;
        final float c4 = -0.006860187425683514f;
        final float c5 =  0.000385937753182769f;
        final float x2 = x * x;
        return ((x - 1f) * (x + 1f) * (((((c5 * x2 + c4) * x2 + c3) * x2 + c2) * x2 + c1) * x2 + c0) * x) * ((floor & 2) - 1f);
    }

    /**
     * Wow, this one seems quite good.
     * Credit to <a href="https://math.stackexchange.com/a/3886664">This StackExchange answer by WimC</a>.
     * @param radians
     * @return
     */
    public static float sinBhaskaroid(float radians) {
        //Absolute error:      0.0001498343
        //Relative error:      0.0000000000
        //Maximum error:       0.0003550053
        //Worst input:         -4.2084822655
        //Worst approx output: 0.8753479123
        //Correct output:      0.8757029176
        radians *= TrigTools.PI_INVERSE * 2f;
        final int ceil = (int) Math.ceil(radians) & -2;
        radians -= ceil;
        final float x2 = radians * radians, x3 = radians * x2;
        return (((11 * radians - 3 * x3) / (7 + x2)) * (1 - (ceil & 2)));
    }

    /**
     * <a href="https://www.nullhardware.com/blog/fixed-point-sine-and-cosine-for-embedded-systems/">From here</a>, by
     * Andrew Steadman. This uses almost entirely integer (fixed-point) math, and like the sine-table-based
     * approximations here, it can't produce a perfectly smooth, continuously curving line of outputs. It is actually
     * rather accurate, though, other than that.
     * @param radians an angle in radians
     * @return a sine value from -1 to 1
     */
    public static float sinSteadman(float radians) {
        //Mean absolute error: 0.0001392388
        //Mean relative error: 0.0007815455
        //Maximum error:       0.00047594
        //Worst input:         -3.52834749
        //Worst approx output: 0.37670898
        //Correct output:      0.37718493


        int i = (short)(radians * 10430.378350470453f);//10430.378350470453f
        /* Convert (signed) input to a value between 0 and 8192. (8192 is pi/2, which is the region of the curve fit). */
        /* ------------------------------------------------------------------- */

        int c = i >> 31; //carry for output pos/neg


        // this block and the commented code just below it should be identical, just one is branchless.
        int fl = (i&0x4000); // flip input value to corresponding value in range [0..8192)
        int sg = -fl >> 31;
        i = (i + sg ^ sg);
//        if(0x4000 == (i&0x4000)) // flip input value to corresponding value in range [0..8192)
//            i = (1<<15) - i;


        i = (i & 0x7FFF) >>> 1;
        /* ------------------------------------------------------------------- */

    /* The following section implements the formula:
     = y * 2^-n * ( A1 - 2^(q-p)* y * 2^-n * y * 2^-n * [B1 - 2^-r * y * 2^-n * C1 * y]) * 2^(a-q)
    Where the constants are defined as follows:
    */
        final int A1 = 0xC8EC8A4B, B1 = 0xA3B2292C, C1 = 0x47645;

        int n=13, p=32, q=31, r=3, a=12;

        int y = (C1*i)>>>n;
        y = B1 - ((i*y)>>>r);
        y = i * (y>>>n);
        y = i * (y>>>n);
        y = A1 - (y>>>(p-q));
        y = i * (y>>>n);
        y = (y+(1<<(q-a-1)))>>>(q-a); // Rounding

        return (y + c ^ c) * 0x1p-12f;
    }

    public static float sinLerp(float radians) {
        radians *= radToIndex;
        final int floor = (int)(radians + 16384.0) - 16384;
        final int masked = floor & TABLE_MASK;
        final float from = SIN_TABLE[masked], to = SIN_TABLE[masked+1];
        return from + (to - from) * (radians - floor);
    }
    public static float cosLerp(float radians) {
        radians *= radToIndex;
        final int floor = (int)(radians + 16384.0) - 16384;
        final int masked = floor + SIN_TO_COS & TABLE_MASK;
        final float from = SIN_TABLE[masked], to = SIN_TABLE[masked+1];
        return from + (to - from) * (radians - floor);
    }

    public static float tanLerp(float radians) {
        radians *= radToIndex;
        final int floor = (int)(radians + 16384.0) - 16384;
        final int maskedS = floor & TABLE_MASK;
        final int maskedC = floor + SIN_TO_COS & TABLE_MASK;
        final float fromS = SIN_TABLE[maskedS], toS = SIN_TABLE[maskedS+1];
        final float fromC = SIN_TABLE[maskedC], toC = SIN_TABLE[maskedC+1];
        return (fromS + (toS - fromS) * (radians - floor))/(fromC + (toC - fromC) * (radians - floor));
    }
    public static float tanTable(float radians) {
        final int r = (int)(radians * radToIndex);
        return SIN_TABLE[r & TABLE_MASK] / SIN_TABLE[r + SIN_TO_COS & TABLE_MASK];
    }

    /**
     * Returns the tangent in radians, using a Padé approximant.
     * Padé approximants tend to be most accurate when they aren't producing results of extreme magnitude; in the tan()
     * function, those results occur on and near odd multiples of {@code PI/2}, and this method is least accurate when
     * given inputs near those multiples.
     * <br> For inputs between -1.57 to 1.57 (just inside half-pi), separated by 0x1p-20f,
     * absolute error is 0.00890192, relative error is 0.00000090, and the maximum error is 17.98901367 when given
     * 1.56999838. The maximum error might seem concerning, but it's the difference between the correct 1253.22167969
     * and the 1235.23266602 this returns, so for many purposes the difference won't be noticeable.
     * <br> For inputs between -1.55 to 1.55 (getting less close to half-pi), separated by 0x1p-20f, absolute error is
     * 0.00023368, relative error is -0.00000009, and the maximum error is 0.02355957 when given -1.54996467. The
     * maximum error is the difference between the correct -47.99691010 and the -47.97335052 this returns.
     * <br> Based on <a href="https://math.stackexchange.com/a/4453027">this Stack Exchange answer by Soonts</a>.
     *
     * @param radians a float angle in radians, where 0 to {@link TrigTools#PI2} is one rotation
     * @return a float approximation of tan()
     */
    public static float tanSoonts(float radians) {
        radians *= TrigTools.PI_INVERSE;
        radians += 0.5f;
        radians -= Math.floor(radians);
        radians -= 0.5f;
        radians *= TrigTools.PI;
        final float x2 = radians * radians, x4 = x2 * x2;
        return radians * ((0.0010582010582010583f) * x4 - (0.1111111111111111f) * x2 + 1f)
                / ((0.015873015873015872f) * x4 - (0.4444444444444444f) * x2 + 1f);
        // How we calculated those long constants above (from Stack Exchange, by Soonts):
//        return x * ((1.0/945.0) * x4 - (1.0/9.0) * x2 + 1.0) / ((1.0/63.0) * x4 - (4.0/9.0) * x2 + 1.0);
        // Normally, it would be best to show the division steps, but if GWT isn't computing mathematical constants at
        // compile-time, which I don't know if it does, that would make the shown-division way slower by 4 divisions.
    }
    public static float tanDivide(float radians) {
        final int idx = (int) (radians * TrigTools.TABLE_SIZE / TrigTools.PI2) & TrigTools.TABLE_MASK;
        return TrigTools.SIN_TABLE[idx] / TrigTools.SIN_TABLE[idx + TrigTools.SIN_TO_COS & TrigTools.TABLE_MASK];
    }

    public static void main(String[] args) {
        for (int i = 0; i < 360; i++) {
            double r = Math.toRadians(i);
            double x = Math.cos(r);
            double y = Math.sin(r);
            System.out.printf("(%1.6f,%1.6f): degrees=%d, radians=%1.6f, Math=%1.6f, Old=%1.9f, New=%1.9f", x, y, i, r,
                    Math.atan2(y, x), atan2OldGDX((float) y, (float) x), atan2NewGDX((float) y, (float) x));
        }
    }
}
