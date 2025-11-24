package com.github.tommyettinger.squidlib;

public final class Atan2Approximations {
    private Atan2Approximations(){}

    /**
     * Credit to imuli and Nic Taylor; imuli commented on
     * <a href="https://www.dsprelated.com/showarticle/1052.php">Taylor's article</a> with very useful info.
     * Uses the "Sheet 13" algorithm from "Approximations for Digital Computers," by RAND Corporation (1955)
     * for its atan() approximation.
     * @param y
     * @param x
     * @return
     */
    public static float atan2imuliSheet13(float y, float x)
    {
        if (y == 0f && x >= 0f) return y;
        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;
        float s = z * z;
        z *= (((((((-0.004054058f * s + 0.0218612288f) * s - 0.0559098861f) * s + 0.0964200441f) * s - 0.1390853351f) * s + 0.1994653599f) * s - 0.3332985605f) * s + 0.9999993329f);
        if (invert) z = 1.5707964f - z;
        if (x < 0) z = 3.1415927f - z;
        return Math.copySign(z, y);
    }

    /**
     * Credit to imuli and Nic Taylor; imuli commented on
     * <a href="https://www.dsprelated.com/showarticle/1052.php">Taylor's article</a> with very useful info.
     * Uses the "Sheet 13" algorithm from "Approximations for Digital Computers," by RAND Corporation (1955)
     * for its atan() approximation over the {@code (0,1]} domain.
     * Restructured to use a single return statement and avoid Math.copySign().
     *
     * @param y any finite float; note the unusual argument order (y is first here!)
     * @param x any finite float; note the unusual argument order (x is second here!)
     * @return the angle in radians from the origin to the given point
     */
    public static float atan2imuliSheet13Alt(float y, float x)
    {
        float r;
        if (y == 0f && x >= 0f) {
            r = y;
        } else {
            float ay = Math.abs(y), ax = Math.abs(x);
            boolean invert = ay > ax;
            float z = invert ? ax / ay : ay / ax;
            float s = z * z;
            z *= (((((((-0.004054058f * s + 0.0218612288f) * s - 0.0559098861f) * s + 0.0964200441f) * s - 0.1390853351f) * s + 0.1994653599f) * s - 0.3332985605f) * s + 0.9999993329f);
            if (invert) z = 1.5707964f - z;
            if (x < 0) z = 3.1415927f - z;
            r = y < 0.0 ? -z : z;
        }
        return r;
    }

    /**
     * Credit to imuli and Nic Taylor; imuli commented on
     * <a href="https://www.dsprelated.com/showarticle/1052.php">Taylor's article</a> with very useful info.
     * Uses the "Sheet 13" algorithm from "Approximations for Digital Computers," by RAND Corporation (1955)
     * for its atan() approximation over the {@code (0,1]} domain.
     * Restructured to return early (thanks to MrGlockenspiel for finding this is better) and avoid Math.copySign().
     *
     * @param y any finite float; note the unusual argument order (y is first here!)
     * @param x any finite float; note the unusual argument order (x is second here!)
     * @return the angle in radians from the origin to the given point
     */
    public static float atan2imuliSheet13AltB(float y, float x) {
        if (y == 0f && x >= 0f) return y;
        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax / ay : ay / ax;
        float s = z * z;
        z *= (((((((-0.004054058f * s + 0.0218612288f) * s - 0.0559098861f) * s + 0.0964200441f) * s - 0.1390853351f) * s + 0.1994653599f) * s - 0.3332985605f) * s + 0.9999993329f);
        if (invert) z = 1.5707964f - z;
        if (x < 0) z = 3.1415927f - z;
        return y < 0.0 ? -z : z;
    }


    /**
     * Credit to imuli and Nic Taylor; imuli commented on
     * <a href="https://www.dsprelated.com/showarticle/1052.php">Taylor's article</a> with very useful info.
     * Uses the "Sheet 13" algorithm from "Approximations for Digital Computers," by RAND Corporation (1955)
     * for its atan() approximation over the {@code (0,1]} domain.
     * Restructured to use a single return statement and avoid Math.copySign().
     *
     * @param y any finite float; note the unusual argument order (y is first here!)
     * @param x any finite float; note the unusual argument order (x is second here!)
     * @return the angle in radians from the origin to the given point
     */
    public static float atan2imuliSheet13CopySign(float y, float x)
    {
        float r;
        if (y == 0f && x >= 0f) {
            r = y;
        } else {
            float ay = Math.abs(y), ax = Math.abs(x);
            boolean invert = ay > ax;
            float z = invert ? ax / ay : ay / ax;
            float s = z * z;
            z *= (((((((-0.004054058f * s + 0.0218612288f) * s - 0.0559098861f) * s + 0.0964200441f) * s - 0.1390853351f) * s + 0.1994653599f) * s - 0.3332985605f) * s + 0.9999993329f);
            if (invert) z = 1.5707964f - z;
            if (x < 0) z = 3.1415927f - z;
            r = Math.copySign(z, y);
        }
        return r;
    }

}
