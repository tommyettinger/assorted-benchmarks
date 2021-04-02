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

import squidpony.squidmath.NumberTools;

import static com.badlogic.gdx.math.MathUtils.PI;

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
        if(invert) th = 1.5707963267948966f - th;                                           // [0,π/2]
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

}
