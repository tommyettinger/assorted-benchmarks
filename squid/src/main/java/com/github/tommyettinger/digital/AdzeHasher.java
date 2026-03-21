/*
 * Copyright (c) 2022-2023 See AUTHORS file.
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

package com.github.tommyettinger.digital;

import com.github.tommyettinger.digital.Hasher.HashFunction;
import com.github.tommyettinger.digital.Hasher.HashFunction64;
import com.github.tommyettinger.digital.Hasher.SeededHashFunction;
import com.github.tommyettinger.digital.Hasher.SeededHashFunction64;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.github.tommyettinger.digital.BitConversion.doubleToRawLongBits;
import static com.github.tommyettinger.digital.BitConversion.floatToRawIntBits;

/**
 * 64-bit and 32-bit hashing functions that we can rely on staying the same cross-platform.
 * This provides functions with an algorithm that is closely related to {@link Hasher#hashBulk(ByteBuffer)},
 * but isn't identical, and outperforms it significantly on very large keys. "Very large keys" include 5000-byte
 * ByteBuffers, but it performs well for any argument types it takes, as well as on small keys.
 * <br>
 * The newer algorithm present here in the {@link #hash} and {@link #hash64} methods is
 * based very loosely on the <a href="https://github.com/jonmaiga/mx3">MX3 hash</a>. This modified algorithm,
 * called Adze, is faster at hashing large keys than any of the Wyhash or RapidHash variants I've tried, and
 * also passes the SMHasher 3 test suite. It does noticeably better with large keys, but should perform about
 * as well as either algorithm in Hasher on small keys.
 * For the C++ code used to test Adze with SMHasher 3,
 * <a href="https://github.com/tommyettinger/smhasher-with-junk/blob/master/smhasher3/hashes/adze.cpp">it is here;</a>
 * the specific variant is "adze7d". While in C++, "adze7e" performs much better with small keys, when ported to Java,
 * "adze7d" is reliably faster. Both perform very well on large keys, and are slightly faster than Ax, the
 * algorithm used by {@link Hasher#hashBulk(ByteBuffer)}.
 * <br>
 * This provides an object-based API and a static API, where an AdzeHasher object is
 * instantiated with a seed, and the static methods take a seed as their first argument.
 * Any hash that this returns is always 0 when given null to hash. Arrays with
 * identical elements of identical types will hash identically. Arrays with identical
 * numerical values but different types will sometimes hash differently. This class
 * always provides 64-bit hashes via hash64() and 32-bit hashes via hash().
 * <br>
 * The hash64() and hash() methods use 64-bit math even when producing
 * 32-bit hashes, for GWT reasons. GWT doesn't have the same behavior as desktop and
 * Android applications when using ints because it treats ints mostly like doubles,
 * sometimes, due to it using JavaScript. If we use mainly longs, though, GWT emulates
 * the longs with a more complex technique behind-the-scenes, that behaves the same on
 * the web as it does on desktop or on a phone. Since AdzeHasher is supposed to be stable
 * cross-platform, this is the way we need to go, despite it being slightly slower.
 * <br>
 * There are also 428 predefined instances of AdzeHasher that you can either
 * select from the array {@link #predefined} or select by hand, such as {@link #omega}.
 * The predefined instances are named after the 24 Greek letters, then the same letters
 * with a trailing underscore, then
 * <a href="https://en.wikipedia.org/wiki/List_of_demons_in_the_Ars_Goetia">72 names of demons from the Ars Goetia</a>,
 * then the names of those demons with trailing underscores, then the names of 118 chemical elements, then those names
 * with trailing underscores. The Greek letters are traditional, the demons are perfectly fitting for video games, and
 * chemistry has been closely linked with computing for many years now.
 *
 * @author Tommy Ettinger
 */
public class AdzeHasher {
    /**
     * The seed used by all non-static hash() and hash64() methods in this class (the methods that don't take a seed).
     * You can create many different AdzeHasher objects, all with different seeds, and get very different hashes as a
     * result of any calls on them. Because making this field hidden in some way doesn't meaningfully contribute to
     * security, and only makes it harder to use this class, {@code seed} is public (and final, so it can't be
     * accidentally modified, but still can if needed via reflection).
     */
    public final long seed;

    /**
     * Creates a new AdzeHasher seeded, arbitrarily, with the constant 0xC4CEB9FE1A85EC53L, or -4265267296055464877L .
     */
    public AdzeHasher() {
        this(0xC4CEB9FE1A85EC53L);
    }

    /**
     * Initializes this AdzeHasher with the given seed, running it through {@link #forward(long)} before storing it in
     * the {@link #seed} field. It is recommended to use {@link Hasher#randomize3(long)} on the parameter if you don't
     * know if it is adequately-random. If the seed is the same for two different AdzeHasher
     * instances, and they are given the same inputs, they will produce the same results. If the seed is even slightly
     * different, the results of the two Hashers given the same input should be significantly different.
     *
     * @param seed a long that will be used to change the output of hash() and hash64() methods on the new AdzeHasher
     */
    public AdzeHasher(long seed) {
        this.seed = forward(seed);
    }

    /**
     * Constructs an AdzeHasher by hashing {@code seed} with {@link #hash64(long, CharSequence)}, and then running the result
     * through {@link Hasher#randomize3(long)}. This is the same as calling the constructor {@link #AdzeHasher(long)} and
     * passing it {@code Hasher.randomize3(AdzeHasher.hash64(1L, seed))} .
     *
     * @param seed a CharSequence, such as a String, that will be used to seed the AdzeHasher.
     */
    public AdzeHasher(final CharSequence seed) {
        this(Hasher.randomize3(hash64(1L, seed)));
    }

    /**
     * A long constant used as a multiplier by the MX3 unary hash.
     * Used in {@link #mix(long)}, as well as when hashing one Object.
     */
    public static final long C = 0xBEA225F9EB34556DL;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long Q = 0x9E3779B97F4A7C55L;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long R = 0xC13FA9A902A63293L;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long S = 0xD1B54A32D192ED2DL;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long T = 0xDB4F0B9175AE2169L;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long U = 0xE19B01AA9D42C66DL;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long V = 0xE60E2B722B53AEF3L;
    /** A 64-bit probable prime close to a "harmonious number" (See {@link MathTools#GOLDEN_LONGS}). */
    public static final long W = 0xE95E1DD17D35802BL;

    /**
     * A medium-quality, but fast, way to scramble a 64-bit input and get a 64-bit output.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is reversible, which allows all outputs to be possible for the hashing functions to produce.
     * However, this also allows the seed to be recovered if a zero-length input is supplied. That's why this
     * is a non-cryptographic hashing algorithm!
     * @param x any long
     * @return any long
     */
    public static long mix(long x) {
        x ^= (x << 23 | x >>> 41) ^ (x << 43 | x >>> 21);
        x *= C;
        return x ^ (x << 11 | x >>> 53) ^ (x << 50 | x >>> 14);
    }

    /**
     * Performs part of the hashing step applied to two 64-bit inputs at once, and typically added to a running
     * hash value directly.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is not reversible unless you know one of the parameters in full.
     * @param a any long, typically an item being hashed; mixed with b
     * @param b any long, typically an item being hashed; mixed with a
     * @return any long
     */
    public static long mixMultiple(long a, long b) {
        return ((a << 28 | a >>> 36) + b) * Q
             + ((b << 29 | b >>> 35) + a) * R;
    }

    /**
     * Performs part of the hashing step applied to three 64-bit inputs at once, and typically added to a running
     * hash value directly.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is not reversible under normal circumstances. It may be possible to recover one parameter if the others
     * are known in full. This uses three 64-bit primes as multipliers; the exact numbers don't matter as long as
     * they are odd and have sufficiently well-distributed bits (close to 32 '1' bits, and so on). If this is only
     * added to a running total, the result won't have very random low-order bits, so performing bitwise rotations
     * after at least some calls to this (or xorshifting right) is critical to keeping the hash high-quality.
     * @param a any long, typically an item being hashed; mixed with b and c
     * @param b any long, typically an item being hashed; mixed with c and a
     * @param c any long, typically an item being hashed; mixed with a and b
     * @return any long
     */
    public static long mixMultiple(long a, long b, long c) {
        return ((a << 28 | a >>> 36) + b) * Q
             + ((b << 29 | b >>> 35) + c) * R
             + ((c << 27 | c >>> 37) + a) * T;
    }

    /**
     * Performs part of the hashing step applied to four 64-bit inputs at once, and typically added to a running
     * hash value directly.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is not reversible under normal circumstances. It may be possible to recover one parameter if the others
     * are known in full. This uses four 64-bit primes as multipliers; the exact numbers don't matter as long as
     * they are odd and have sufficiently well-distributed bits (close to 32 '1' bits, and so on). If this is only
     * added to a running total, the result won't have very random low-order bits, so performing bitwise rotations
     * after at least some calls to this (or xorshifting right) is critical to keeping the hash high-quality.
     * @param a any long, typically an item being hashed; mixed with b and d
     * @param b any long, typically an item being hashed; mixed with c and a
     * @param c any long, typically an item being hashed; mixed with d and b
     * @param d any long, typically an item being hashed; mixed with a and c
     * @return any long
     */
    public static long mixMultiple(long a, long b, long c, long d) {
        return ((a << 28 | a >>> 36) + b) * Q
             + ((b << 29 | b >>> 35) + c) * R
             + ((c << 27 | c >>> 37) + d) * S
             + ((d << 25 | d >>> 39) + a) * T;
    }

    /**
     * Performs part of the hashing step applied to five 64-bit inputs at once, and typically added to a running
     * hash value directly.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is not reversible under normal circumstances. It may be possible to recover one parameter if the others
     * are known in full. This uses five 64-bit primes as multipliers; the exact numbers don't matter as long as
     * they are odd and have sufficiently well-distributed bits (close to 32 '1' bits, and so on). If this is only
     * added to a running total, the result won't have very random low-order bits, so performing bitwise rotations
     * after at least some calls to this (or xorshifting right) is critical to keeping the hash high-quality.
     * @param a any long, typically an item being hashed; mixed with b and e
     * @param b any long, typically an item being hashed; mixed with c and a
     * @param c any long, typically an item being hashed; mixed with d and b
     * @param d any long, typically an item being hashed; mixed with e and c
     * @param e any long, typically an item being hashed; mixed with a and d
     * @return any long
     */
    public static long mixMultiple(long a, long b, long c, long d, long e) {
        return ((a << 28 | a >>> 36) + b) * Q
             + ((b << 29 | b >>> 35) + c) * R
             + ((c << 27 | c >>> 37) + d) * S
             + ((d << 25 | d >>> 39) + e) * T
             + ((e << 26 | e >>> 38) + a) * U;
    }

    /**
     * Performs part of the hashing step applied to six 64-bit inputs at once, and typically added to a running
     * hash value directly.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is not reversible under normal circumstances. It may be possible to recover one parameter if the others
     * are known in full. This uses six 64-bit primes as multipliers; the exact numbers don't matter as long as
     * they are odd and have sufficiently well-distributed bits (close to 32 '1' bits, and so on). If this is only
     * added to a running total, the result won't have very random low-order bits, so performing bitwise rotations
     * after at least some calls to this (or xorshifting right) is critical to keeping the hash high-quality.
     * @param a any long, typically an item being hashed; mixed with b and f
     * @param b any long, typically an item being hashed; mixed with c and a
     * @param c any long, typically an item being hashed; mixed with d and b
     * @param d any long, typically an item being hashed; mixed with e and c
     * @param e any long, typically an item being hashed; mixed with f and d
     * @param f any long, typically an item being hashed; mixed with a and e
     * @return any long
     */
    public static long mixMultiple(long a, long b, long c, long d, long e, long f) {
        return ((a << 28 | a >>> 36) + b) * Q
             + ((b << 29 | b >>> 35) + c) * R
             + ((c << 27 | c >>> 37) + d) * S
             + ((d << 25 | d >>> 39) + e) * T
             + ((e << 26 | e >>> 38) + f) * U
             + ((f << 30 | f >>> 34) + a) * V;
    }

    /**
     * Performs part of the hashing step applied to seven 64-bit inputs at once, and typically added to a running
     * hash value directly.
     * Used by {@link #hash64} and {@link #hash}.
     * <br>
     * This is not reversible under normal circumstances. It may be possible to recover one parameter if the others
     * are known in full. This uses seven 64-bit primes as multipliers; the exact numbers don't matter as long as
     * they are odd and have sufficiently well-distributed bits (close to 32 '1' bits, and so on). If this is only
     * added to a running total, the result won't have very random low-order bits, so performing bitwise rotations
     * after at least some calls to this (or xorshifting right) is critical to keeping the hash high-quality.
     * @param a any long, typically an item being hashed; mixed with b and g
     * @param b any long, typically an item being hashed; mixed with c and a
     * @param c any long, typically an item being hashed; mixed with d and b
     * @param d any long, typically an item being hashed; mixed with e and c
     * @param e any long, typically an item being hashed; mixed with f and d
     * @param f any long, typically an item being hashed; mixed with g and e
     * @param g any long, typically an item being hashed; mixed with a and f
     * @return any long
     */
    public static long mixMultiple(long a, long b, long c, long d, long e, long f, long g) {
        return ((a << 28 | a >>> 36) + b) * Q
             + ((b << 29 | b >>> 35) + c) * R
             + ((c << 27 | c >>> 37) + d) * S
             + ((d << 25 | d >>> 39) + e) * T
             + ((e << 26 | e >>> 38) + f) * U
             + ((f << 30 | f >>> 34) + g) * V
             + ((g << 23 | g >>> 41) + a) * W;
    }


    /**
     * A very minimalist way to scramble inputs to be used as seeds; can be inverted using {@link #reverse(long)}.
     * This simply performs the XOR-rotate-XOR-rotate operation on x, using left rotations of 23 and 56.
     * @param x any long
     * @return a slightly scrambled version of x
     */
    public static long forward(long x) {
        return x ^ (x << 23 | x >>> 43) ^ (x << 56 | x >>> 8);
    }

    /**
     * Unscrambles the result of {@link #forward(long)} to get its original argument back.
     * @param x a long produced by {@link #forward(long)} or obtained from {@link #seed}
     * @return the original long that was provided to {@link #forward(long)}, before scrambling
     */
    public static long reverse(long x) {
        x = x ^ (x << 23 | x >>> 43) ^ (x << 56 | x >>>  8);
        x = x ^ (x << 46 | x >>> 18) ^ (x << 48 | x >>> 16);
        x = x ^ (x << 28 | x >>> 36) ^ (x << 32 | x >>> 32);
        x = (x << 8 | x >>> 56);
        return x;
    }


    public static final AdzeHasher alpha = new AdzeHasher("alpha"), beta = new AdzeHasher("beta"), gamma = new AdzeHasher("gamma"),
            delta = new AdzeHasher("delta"), epsilon = new AdzeHasher("epsilon"), zeta = new AdzeHasher("zeta"),
            eta = new AdzeHasher("eta"), theta = new AdzeHasher("theta"), iota = new AdzeHasher("iota"),
            kappa = new AdzeHasher("kappa"), lambda = new AdzeHasher("lambda"), mu = new AdzeHasher("mu"),
            nu = new AdzeHasher("nu"), xi = new AdzeHasher("xi"), omicron = new AdzeHasher("omicron"), pi = new AdzeHasher("pi"),
            rho = new AdzeHasher("rho"), sigma = new AdzeHasher("sigma"), tau = new AdzeHasher("tau"),
            upsilon = new AdzeHasher("upsilon"), phi = new AdzeHasher("phi"), chi = new AdzeHasher("chi"), psi = new AdzeHasher("psi"),
            omega = new AdzeHasher("omega"),
            alpha_ = new AdzeHasher("ALPHA"), beta_ = new AdzeHasher("BETA"), gamma_ = new AdzeHasher("GAMMA"),
            delta_ = new AdzeHasher("DELTA"), epsilon_ = new AdzeHasher("EPSILON"), zeta_ = new AdzeHasher("ZETA"),
            eta_ = new AdzeHasher("ETA"), theta_ = new AdzeHasher("THETA"), iota_ = new AdzeHasher("IOTA"),
            kappa_ = new AdzeHasher("KAPPA"), lambda_ = new AdzeHasher("LAMBDA"), mu_ = new AdzeHasher("MU"),
            nu_ = new AdzeHasher("NU"), xi_ = new AdzeHasher("XI"), omicron_ = new AdzeHasher("OMICRON"), pi_ = new AdzeHasher("PI"),
            rho_ = new AdzeHasher("RHO"), sigma_ = new AdzeHasher("SIGMA"), tau_ = new AdzeHasher("TAU"),
            upsilon_ = new AdzeHasher("UPSILON"), phi_ = new AdzeHasher("PHI"), chi_ = new AdzeHasher("CHI"), psi_ = new AdzeHasher("PSI"),
            omega_ = new AdzeHasher("OMEGA"),
            baal = new AdzeHasher("baal"), agares = new AdzeHasher("agares"), vassago = new AdzeHasher("vassago"), samigina = new AdzeHasher("samigina"),
            marbas = new AdzeHasher("marbas"), valefor = new AdzeHasher("valefor"), amon = new AdzeHasher("amon"), barbatos = new AdzeHasher("barbatos"),
            paimon = new AdzeHasher("paimon"), buer = new AdzeHasher("buer"), gusion = new AdzeHasher("gusion"), sitri = new AdzeHasher("sitri"),
            beleth = new AdzeHasher("beleth"), leraje = new AdzeHasher("leraje"), eligos = new AdzeHasher("eligos"), zepar = new AdzeHasher("zepar"),
            botis = new AdzeHasher("botis"), bathin = new AdzeHasher("bathin"), sallos = new AdzeHasher("sallos"), purson = new AdzeHasher("purson"),
            marax = new AdzeHasher("marax"), ipos = new AdzeHasher("ipos"), aim = new AdzeHasher("aim"), naberius = new AdzeHasher("naberius"),
            glasya_labolas = new AdzeHasher("glasya_labolas"), bune = new AdzeHasher("bune"), ronove = new AdzeHasher("ronove"), berith = new AdzeHasher("berith"),
            astaroth = new AdzeHasher("astaroth"), forneus = new AdzeHasher("forneus"), foras = new AdzeHasher("foras"), asmoday = new AdzeHasher("asmoday"),
            gaap = new AdzeHasher("gaap"), furfur = new AdzeHasher("furfur"), marchosias = new AdzeHasher("marchosias"), stolas = new AdzeHasher("stolas"),
            phenex = new AdzeHasher("phenex"), halphas = new AdzeHasher("halphas"), malphas = new AdzeHasher("malphas"), raum = new AdzeHasher("raum"),
            focalor = new AdzeHasher("focalor"), vepar = new AdzeHasher("vepar"), sabnock = new AdzeHasher("sabnock"), shax = new AdzeHasher("shax"),
            vine = new AdzeHasher("vine"), bifrons = new AdzeHasher("bifrons"), vual = new AdzeHasher("vual"), haagenti = new AdzeHasher("haagenti"),
            crocell = new AdzeHasher("crocell"), furcas = new AdzeHasher("furcas"), balam = new AdzeHasher("balam"), alloces = new AdzeHasher("alloces"),
            caim = new AdzeHasher("caim"), murmur = new AdzeHasher("murmur"), orobas = new AdzeHasher("orobas"), gremory = new AdzeHasher("gremory"),
            ose = new AdzeHasher("ose"), amy = new AdzeHasher("amy"), orias = new AdzeHasher("orias"), vapula = new AdzeHasher("vapula"),
            zagan = new AdzeHasher("zagan"), valac = new AdzeHasher("valac"), andras = new AdzeHasher("andras"), flauros = new AdzeHasher("flauros"),
            andrealphus = new AdzeHasher("andrealphus"), kimaris = new AdzeHasher("kimaris"), amdusias = new AdzeHasher("amdusias"), belial = new AdzeHasher("belial"),
            decarabia = new AdzeHasher("decarabia"), seere = new AdzeHasher("seere"), dantalion = new AdzeHasher("dantalion"), andromalius = new AdzeHasher("andromalius"),
            baal_ = new AdzeHasher("BAAL"), agares_ = new AdzeHasher("AGARES"), vassago_ = new AdzeHasher("VASSAGO"), samigina_ = new AdzeHasher("SAMIGINA"),
            marbas_ = new AdzeHasher("MARBAS"), valefor_ = new AdzeHasher("VALEFOR"), amon_ = new AdzeHasher("AMON"), barbatos_ = new AdzeHasher("BARBATOS"),
            paimon_ = new AdzeHasher("PAIMON"), buer_ = new AdzeHasher("BUER"), gusion_ = new AdzeHasher("GUSION"), sitri_ = new AdzeHasher("SITRI"),
            beleth_ = new AdzeHasher("BELETH"), leraje_ = new AdzeHasher("LERAJE"), eligos_ = new AdzeHasher("ELIGOS"), zepar_ = new AdzeHasher("ZEPAR"),
            botis_ = new AdzeHasher("BOTIS"), bathin_ = new AdzeHasher("BATHIN"), sallos_ = new AdzeHasher("SALLOS"), purson_ = new AdzeHasher("PURSON"),
            marax_ = new AdzeHasher("MARAX"), ipos_ = new AdzeHasher("IPOS"), aim_ = new AdzeHasher("AIM"), naberius_ = new AdzeHasher("NABERIUS"),
            glasya_labolas_ = new AdzeHasher("GLASYA_LABOLAS"), bune_ = new AdzeHasher("BUNE"), ronove_ = new AdzeHasher("RONOVE"), berith_ = new AdzeHasher("BERITH"),
            astaroth_ = new AdzeHasher("ASTAROTH"), forneus_ = new AdzeHasher("FORNEUS"), foras_ = new AdzeHasher("FORAS"), asmoday_ = new AdzeHasher("ASMODAY"),
            gaap_ = new AdzeHasher("GAAP"), furfur_ = new AdzeHasher("FURFUR"), marchosias_ = new AdzeHasher("MARCHOSIAS"), stolas_ = new AdzeHasher("STOLAS"),
            phenex_ = new AdzeHasher("PHENEX"), halphas_ = new AdzeHasher("HALPHAS"), malphas_ = new AdzeHasher("MALPHAS"), raum_ = new AdzeHasher("RAUM"),
            focalor_ = new AdzeHasher("FOCALOR"), vepar_ = new AdzeHasher("VEPAR"), sabnock_ = new AdzeHasher("SABNOCK"), shax_ = new AdzeHasher("SHAX"),
            vine_ = new AdzeHasher("VINE"), bifrons_ = new AdzeHasher("BIFRONS"), vual_ = new AdzeHasher("VUAL"), haagenti_ = new AdzeHasher("HAAGENTI"),
            crocell_ = new AdzeHasher("CROCELL"), furcas_ = new AdzeHasher("FURCAS"), balam_ = new AdzeHasher("BALAM"), alloces_ = new AdzeHasher("ALLOCES"),
            caim_ = new AdzeHasher("CAIM"), murmur_ = new AdzeHasher("MURMUR"), orobas_ = new AdzeHasher("OROBAS"), gremory_ = new AdzeHasher("GREMORY"),
            ose_ = new AdzeHasher("OSE"), amy_ = new AdzeHasher("AMY"), orias_ = new AdzeHasher("ORIAS"), vapula_ = new AdzeHasher("VAPULA"),
            zagan_ = new AdzeHasher("ZAGAN"), valac_ = new AdzeHasher("VALAC"), andras_ = new AdzeHasher("ANDRAS"), flauros_ = new AdzeHasher("FLAUROS"),
            andrealphus_ = new AdzeHasher("ANDREALPHUS"), kimaris_ = new AdzeHasher("KIMARIS"), amdusias_ = new AdzeHasher("AMDUSIAS"), belial_ = new AdzeHasher("BELIAL"),
            decarabia_ = new AdzeHasher("DECARABIA"), seere_ = new AdzeHasher("SEERE"), dantalion_ = new AdzeHasher("DANTALION"), andromalius_ = new AdzeHasher("ANDROMALIUS"),
            hydrogen = new AdzeHasher("hydrogen"), helium = new AdzeHasher("helium"), lithium = new AdzeHasher("lithium"), beryllium = new AdzeHasher("beryllium"), boron = new AdzeHasher("boron"), carbon = new AdzeHasher("carbon"), nitrogen = new AdzeHasher("nitrogen"), oxygen = new AdzeHasher("oxygen"), fluorine = new AdzeHasher("fluorine"), neon = new AdzeHasher("neon"), sodium = new AdzeHasher("sodium"), magnesium = new AdzeHasher("magnesium"), aluminium = new AdzeHasher("aluminium"), silicon = new AdzeHasher("silicon"), phosphorus = new AdzeHasher("phosphorus"), sulfur = new AdzeHasher("sulfur"), chlorine = new AdzeHasher("chlorine"), argon = new AdzeHasher("argon"), potassium = new AdzeHasher("potassium"), calcium = new AdzeHasher("calcium"), scandium = new AdzeHasher("scandium"), titanium = new AdzeHasher("titanium"), vanadium = new AdzeHasher("vanadium"), chromium = new AdzeHasher("chromium"), manganese = new AdzeHasher("manganese"), iron = new AdzeHasher("iron"), cobalt = new AdzeHasher("cobalt"), nickel = new AdzeHasher("nickel"), copper = new AdzeHasher("copper"), zinc = new AdzeHasher("zinc"), gallium = new AdzeHasher("gallium"), germanium = new AdzeHasher("germanium"), arsenic = new AdzeHasher("arsenic"), selenium = new AdzeHasher("selenium"), bromine = new AdzeHasher("bromine"), krypton = new AdzeHasher("krypton"), rubidium = new AdzeHasher("rubidium"), strontium = new AdzeHasher("strontium"), yttrium = new AdzeHasher("yttrium"), zirconium = new AdzeHasher("zirconium"), niobium = new AdzeHasher("niobium"), molybdenum = new AdzeHasher("molybdenum"), technetium = new AdzeHasher("technetium"), ruthenium = new AdzeHasher("ruthenium"), rhodium = new AdzeHasher("rhodium"), palladium = new AdzeHasher("palladium"), silver = new AdzeHasher("silver"), cadmium = new AdzeHasher("cadmium"), indium = new AdzeHasher("indium"), tin = new AdzeHasher("tin"), antimony = new AdzeHasher("antimony"), tellurium = new AdzeHasher("tellurium"), iodine = new AdzeHasher("iodine"), xenon = new AdzeHasher("xenon"), caesium = new AdzeHasher("caesium"), barium = new AdzeHasher("barium"), lanthanum = new AdzeHasher("lanthanum"), cerium = new AdzeHasher("cerium"), praseodymium = new AdzeHasher("praseodymium"), neodymium = new AdzeHasher("neodymium"), promethium = new AdzeHasher("promethium"), samarium = new AdzeHasher("samarium"), europium = new AdzeHasher("europium"), gadolinium = new AdzeHasher("gadolinium"), terbium = new AdzeHasher("terbium"), dysprosium = new AdzeHasher("dysprosium"), holmium = new AdzeHasher("holmium"), erbium = new AdzeHasher("erbium"), thulium = new AdzeHasher("thulium"), ytterbium = new AdzeHasher("ytterbium"), lutetium = new AdzeHasher("lutetium"), hafnium = new AdzeHasher("hafnium"), tantalum = new AdzeHasher("tantalum"), tungsten = new AdzeHasher("tungsten"), rhenium = new AdzeHasher("rhenium"), osmium = new AdzeHasher("osmium"), iridium = new AdzeHasher("iridium"), platinum = new AdzeHasher("platinum"), gold = new AdzeHasher("gold"), mercury = new AdzeHasher("mercury"), thallium = new AdzeHasher("thallium"), lead = new AdzeHasher("lead"), bismuth = new AdzeHasher("bismuth"), polonium = new AdzeHasher("polonium"), astatine = new AdzeHasher("astatine"), radon = new AdzeHasher("radon"), francium = new AdzeHasher("francium"), radium = new AdzeHasher("radium"), actinium = new AdzeHasher("actinium"), thorium = new AdzeHasher("thorium"), protactinium = new AdzeHasher("protactinium"), uranium = new AdzeHasher("uranium"), neptunium = new AdzeHasher("neptunium"), plutonium = new AdzeHasher("plutonium"), americium = new AdzeHasher("americium"), curium = new AdzeHasher("curium"), berkelium = new AdzeHasher("berkelium"), californium = new AdzeHasher("californium"), einsteinium = new AdzeHasher("einsteinium"), fermium = new AdzeHasher("fermium"), mendelevium = new AdzeHasher("mendelevium"), nobelium = new AdzeHasher("nobelium"), lawrencium = new AdzeHasher("lawrencium"), rutherfordium = new AdzeHasher("rutherfordium"), dubnium = new AdzeHasher("dubnium"), seaborgium = new AdzeHasher("seaborgium"), bohrium = new AdzeHasher("bohrium"), hassium = new AdzeHasher("hassium"), meitnerium = new AdzeHasher("meitnerium"), darmstadtium = new AdzeHasher("darmstadtium"), roentgenium = new AdzeHasher("roentgenium"), copernicium = new AdzeHasher("copernicium"), nihonium = new AdzeHasher("nihonium"), flerovium = new AdzeHasher("flerovium"), moscovium = new AdzeHasher("moscovium"), livermorium = new AdzeHasher("livermorium"), tennessine = new AdzeHasher("tennessine"), oganesson = new AdzeHasher("oganesson"),
            hydrogen_ = new AdzeHasher("HYDROGEN"), helium_ = new AdzeHasher("HELIUM"), lithium_ = new AdzeHasher("LITHIUM"), beryllium_ = new AdzeHasher("BERYLLIUM"), boron_ = new AdzeHasher("BORON"), carbon_ = new AdzeHasher("CARBON"), nitrogen_ = new AdzeHasher("NITROGEN"), oxygen_ = new AdzeHasher("OXYGEN"), fluorine_ = new AdzeHasher("FLUORINE"), neon_ = new AdzeHasher("NEON"), sodium_ = new AdzeHasher("SODIUM"), magnesium_ = new AdzeHasher("MAGNESIUM"), aluminium_ = new AdzeHasher("ALUMINIUM"), silicon_ = new AdzeHasher("SILICON"), phosphorus_ = new AdzeHasher("PHOSPHORUS"), sulfur_ = new AdzeHasher("SULFUR"), chlorine_ = new AdzeHasher("CHLORINE"), argon_ = new AdzeHasher("ARGON"), potassium_ = new AdzeHasher("POTASSIUM"), calcium_ = new AdzeHasher("CALCIUM"), scandium_ = new AdzeHasher("SCANDIUM"), titanium_ = new AdzeHasher("TITANIUM"), vanadium_ = new AdzeHasher("VANADIUM"), chromium_ = new AdzeHasher("CHROMIUM"), manganese_ = new AdzeHasher("MANGANESE"), iron_ = new AdzeHasher("IRON"), cobalt_ = new AdzeHasher("COBALT"), nickel_ = new AdzeHasher("NICKEL"), copper_ = new AdzeHasher("COPPER"), zinc_ = new AdzeHasher("ZINC"), gallium_ = new AdzeHasher("GALLIUM"), germanium_ = new AdzeHasher("GERMANIUM"), arsenic_ = new AdzeHasher("ARSENIC"), selenium_ = new AdzeHasher("SELENIUM"), bromine_ = new AdzeHasher("BROMINE"), krypton_ = new AdzeHasher("KRYPTON"), rubidium_ = new AdzeHasher("RUBIDIUM"), strontium_ = new AdzeHasher("STRONTIUM"), yttrium_ = new AdzeHasher("YTTRIUM"), zirconium_ = new AdzeHasher("ZIRCONIUM"), niobium_ = new AdzeHasher("NIOBIUM"), molybdenum_ = new AdzeHasher("MOLYBDENUM"), technetium_ = new AdzeHasher("TECHNETIUM"), ruthenium_ = new AdzeHasher("RUTHENIUM"), rhodium_ = new AdzeHasher("RHODIUM"), palladium_ = new AdzeHasher("PALLADIUM"), silver_ = new AdzeHasher("SILVER"), cadmium_ = new AdzeHasher("CADMIUM"), indium_ = new AdzeHasher("INDIUM"), tin_ = new AdzeHasher("TIN"), antimony_ = new AdzeHasher("ANTIMONY"), tellurium_ = new AdzeHasher("TELLURIUM"), iodine_ = new AdzeHasher("IODINE"), xenon_ = new AdzeHasher("XENON"), caesium_ = new AdzeHasher("CAESIUM"), barium_ = new AdzeHasher("BARIUM"), lanthanum_ = new AdzeHasher("LANTHANUM"), cerium_ = new AdzeHasher("CERIUM"), praseodymium_ = new AdzeHasher("PRASEODYMIUM"), neodymium_ = new AdzeHasher("NEODYMIUM"), promethium_ = new AdzeHasher("PROMETHIUM"), samarium_ = new AdzeHasher("SAMARIUM"), europium_ = new AdzeHasher("EUROPIUM"), gadolinium_ = new AdzeHasher("GADOLINIUM"), terbium_ = new AdzeHasher("TERBIUM"), dysprosium_ = new AdzeHasher("DYSPROSIUM"), holmium_ = new AdzeHasher("HOLMIUM"), erbium_ = new AdzeHasher("ERBIUM"), thulium_ = new AdzeHasher("THULIUM"), ytterbium_ = new AdzeHasher("YTTERBIUM"), lutetium_ = new AdzeHasher("LUTETIUM"), hafnium_ = new AdzeHasher("HAFNIUM"), tantalum_ = new AdzeHasher("TANTALUM"), tungsten_ = new AdzeHasher("TUNGSTEN"), rhenium_ = new AdzeHasher("RHENIUM"), osmium_ = new AdzeHasher("OSMIUM"), iridium_ = new AdzeHasher("IRIDIUM"), platinum_ = new AdzeHasher("PLATINUM"), gold_ = new AdzeHasher("GOLD"), mercury_ = new AdzeHasher("MERCURY"), thallium_ = new AdzeHasher("THALLIUM"), lead_ = new AdzeHasher("LEAD"), bismuth_ = new AdzeHasher("BISMUTH"), polonium_ = new AdzeHasher("POLONIUM"), astatine_ = new AdzeHasher("ASTATINE"), radon_ = new AdzeHasher("RADON"), francium_ = new AdzeHasher("FRANCIUM"), radium_ = new AdzeHasher("RADIUM"), actinium_ = new AdzeHasher("ACTINIUM"), thorium_ = new AdzeHasher("THORIUM"), protactinium_ = new AdzeHasher("PROTACTINIUM"), uranium_ = new AdzeHasher("URANIUM"), neptunium_ = new AdzeHasher("NEPTUNIUM"), plutonium_ = new AdzeHasher("PLUTONIUM"), americium_ = new AdzeHasher("AMERICIUM"), curium_ = new AdzeHasher("CURIUM"), berkelium_ = new AdzeHasher("BERKELIUM"), californium_ = new AdzeHasher("CALIFORNIUM"), einsteinium_ = new AdzeHasher("EINSTEINIUM"), fermium_ = new AdzeHasher("FERMIUM"), mendelevium_ = new AdzeHasher("MENDELEVIUM"), nobelium_ = new AdzeHasher("NOBELIUM"), lawrencium_ = new AdzeHasher("LAWRENCIUM"), rutherfordium_ = new AdzeHasher("RUTHERFORDIUM"), dubnium_ = new AdzeHasher("DUBNIUM"), seaborgium_ = new AdzeHasher("SEABORGIUM"), bohrium_ = new AdzeHasher("BOHRIUM"), hassium_ = new AdzeHasher("HASSIUM"), meitnerium_ = new AdzeHasher("MEITNERIUM"), darmstadtium_ = new AdzeHasher("DARMSTADTIUM"), roentgenium_ = new AdzeHasher("ROENTGENIUM"), copernicium_ = new AdzeHasher("COPERNICIUM"), nihonium_ = new AdzeHasher("NIHONIUM"), flerovium_ = new AdzeHasher("FLEROVIUM"), moscovium_ = new AdzeHasher("MOSCOVIUM"), livermorium_ = new AdzeHasher("LIVERMORIUM"), tennessine_ = new AdzeHasher("TENNESSINE"), oganesson_ = new AdzeHasher("OGANESSON");

    /**
     * Has a length of 428, which may be relevant if automatically choosing a predefined hash functor.
     */
    public static final AdzeHasher[] predefined = new AdzeHasher[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
            kappa, lambda, mu, nu, xi, omicron, pi, rho, sigma, tau, upsilon, phi, chi, psi, omega,
            alpha_, beta_, gamma_, delta_, epsilon_, zeta_, eta_, theta_, iota_,
            kappa_, lambda_, mu_, nu_, xi_, omicron_, pi_, rho_, sigma_, tau_, upsilon_, phi_, chi_, psi_, omega_,
            baal, agares, vassago, samigina, marbas, valefor, amon, barbatos,
            paimon, buer, gusion, sitri, beleth, leraje, eligos, zepar,
            botis, bathin, sallos, purson, marax, ipos, aim, naberius,
            glasya_labolas, bune, ronove, berith, astaroth, forneus, foras, asmoday,
            gaap, furfur, marchosias, stolas, phenex, halphas, malphas, raum,
            focalor, vepar, sabnock, shax, vine, bifrons, vual, haagenti,
            crocell, furcas, balam, alloces, caim, murmur, orobas, gremory,
            ose, amy, orias, vapula, zagan, valac, andras, flauros,
            andrealphus, kimaris, amdusias, belial, decarabia, seere, dantalion, andromalius,
            baal_, agares_, vassago_, samigina_, marbas_, valefor_, amon_, barbatos_,
            paimon_, buer_, gusion_, sitri_, beleth_, leraje_, eligos_, zepar_,
            botis_, bathin_, sallos_, purson_, marax_, ipos_, aim_, naberius_,
            glasya_labolas_, bune_, ronove_, berith_, astaroth_, forneus_, foras_, asmoday_,
            gaap_, furfur_, marchosias_, stolas_, phenex_, halphas_, malphas_, raum_,
            focalor_, vepar_, sabnock_, shax_, vine_, bifrons_, vual_, haagenti_,
            crocell_, furcas_, balam_, alloces_, caim_, murmur_, orobas_, gremory_,
            ose_, amy_, orias_, vapula_, zagan_, valac_, andras_, flauros_,
            andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_,

            hydrogen, helium, lithium, beryllium, boron, carbon, nitrogen, oxygen, fluorine, neon,
            sodium, magnesium, aluminium, silicon, phosphorus, sulfur, chlorine, argon, potassium,
            calcium, scandium, titanium, vanadium, chromium, manganese, iron, cobalt, nickel,
            copper, zinc, gallium, germanium, arsenic, selenium, bromine, krypton, rubidium,
            strontium, yttrium, zirconium, niobium, molybdenum, technetium, ruthenium, rhodium,
            palladium, silver, cadmium, indium, tin, antimony, tellurium, iodine, xenon, caesium,
            barium, lanthanum, cerium, praseodymium, neodymium, promethium, samarium, europium,
            gadolinium, terbium, dysprosium, holmium, erbium, thulium, ytterbium, lutetium, hafnium,
            tantalum, tungsten, rhenium, osmium, iridium, platinum, gold, mercury, thallium, lead,
            bismuth, polonium, astatine, radon, francium, radium, actinium, thorium, protactinium,
            uranium, neptunium, plutonium, americium, curium, berkelium, californium, einsteinium,
            fermium, mendelevium, nobelium, lawrencium, rutherfordium, dubnium, seaborgium, bohrium,
            hassium, meitnerium, darmstadtium, roentgenium, copernicium, nihonium, flerovium, moscovium,
            livermorium, tennessine, oganesson,

            hydrogen_, helium_, lithium_, beryllium_, boron_, carbon_, nitrogen_, oxygen_, fluorine_, neon_,
            sodium_, magnesium_, aluminium_, silicon_, phosphorus_, sulfur_, chlorine_, argon_, potassium_,
            calcium_, scandium_, titanium_, vanadium_, chromium_, manganese_, iron_, cobalt_, nickel_,
            copper_, zinc_, gallium_, germanium_, arsenic_, selenium_, bromine_, krypton_, rubidium_,
            strontium_, yttrium_, zirconium_, niobium_, molybdenum_, technetium_, ruthenium_, rhodium_,
            palladium_, silver_, cadmium_, indium_, tin_, antimony_, tellurium_, iodine_, xenon_, caesium_,
            barium_, lanthanum_, cerium_, praseodymium_, neodymium_, promethium_, samarium_, europium_,
            gadolinium_, terbium_, dysprosium_, holmium_, erbium_, thulium_, ytterbium_, lutetium_, hafnium_,
            tantalum_, tungsten_, rhenium_, osmium_, iridium_, platinum_, gold_, mercury_, thallium_, lead_,
            bismuth_, polonium_, astatine_, radon_, francium_, radium_, actinium_, thorium_, protactinium_,
            uranium_, neptunium_, plutonium_, americium_, curium_, berkelium_, californium_, einsteinium_,
            fermium_, mendelevium_, nobelium_, lawrencium_, rutherfordium_, dubnium_, seaborgium_, bohrium_,
            hassium_, meitnerium_, darmstadtium_, roentgenium_, copernicium_, nihonium_, flerovium_, moscovium_,
            livermorium_, tennessine_, oganesson_,

    };

    // hashing section, member functions

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final long[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final long[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final long[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final long[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final int[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final int[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final int[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final int[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final short[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final short[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final short[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final short[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[])}  and passing them to
     * {@link #hash64(ByteBuffer)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final byte[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[], int, int)}  and passing them to
     * {@link #hash64(ByteBuffer, int, int)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final byte[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[])}  and passing them to
     * {@link #hash(ByteBuffer)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final byte[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[], int, int)}  and passing them to
     * {@link #hash(ByteBuffer, int, int)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final byte[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final float[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final float[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]), floatToRawIntBits(data[i+4]), floatToRawIntBits(data[i+5]), floatToRawIntBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(floatToRawIntBits(data[i+7]), floatToRawIntBits(data[i+8]), floatToRawIntBits(data[i+9]), floatToRawIntBits(data[i+10]), floatToRawIntBits(data[i+11]), floatToRawIntBits(data[i+12]), floatToRawIntBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, floatToRawIntBits(data[i])); break;
            case 2: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1])); break;
            case 3: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final float[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final float[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]), floatToRawIntBits(data[i+4]), floatToRawIntBits(data[i+5]), floatToRawIntBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(floatToRawIntBits(data[i+7]), floatToRawIntBits(data[i+8]), floatToRawIntBits(data[i+9]), floatToRawIntBits(data[i+10]), floatToRawIntBits(data[i+11]), floatToRawIntBits(data[i+12]), floatToRawIntBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, floatToRawIntBits(data[i])); break;
            case 2: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1])); break;
            case 3: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2])); break;
        }
        return (int)mix(h);
    }
    
    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final double[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final double[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]), doubleToRawLongBits(data[i+4]), doubleToRawLongBits(data[i+5]), doubleToRawLongBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(doubleToRawLongBits(data[i+7]), doubleToRawLongBits(data[i+8]), doubleToRawLongBits(data[i+9]), doubleToRawLongBits(data[i+10]), doubleToRawLongBits(data[i+11]), doubleToRawLongBits(data[i+12]), doubleToRawLongBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, doubleToRawLongBits(data[i])); break;
            case 2: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1])); break;
            case 3: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final double[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final double[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]), doubleToRawLongBits(data[i+4]), doubleToRawLongBits(data[i+5]), doubleToRawLongBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(doubleToRawLongBits(data[i+7]), doubleToRawLongBits(data[i+8]), doubleToRawLongBits(data[i+9]), doubleToRawLongBits(data[i+10]), doubleToRawLongBits(data[i+11]), doubleToRawLongBits(data[i+12]), doubleToRawLongBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, doubleToRawLongBits(data[i])); break;
            case 2: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1])); break;
            case 3: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final char[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final char[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final char[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final char[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final boolean[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final boolean[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L, data[i+4] ? C : 0L, data[i+5] ? C : 0L, data[i+6] ? C : 0L);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7] ? C : 0L, data[i+8] ? C : 0L, data[i+9] ? C : 0L, data[i+10] ? C : 0L, data[i+11] ? C : 0L, data[i+12] ? C : 0L, data[i+13] ? C : 0L);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i] ? C : 0L); break;
            case 2: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L); break;
            case 3: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final boolean[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final boolean[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L, data[i+4] ? C : 0L, data[i+5] ? C : 0L, data[i+6] ? C : 0L);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7] ? C : 0L, data[i+8] ? C : 0L, data[i+9] ? C : 0L, data[i+10] ? C : 0L, data[i+11] ? C : 0L, data[i+12] ? C : 0L, data[i+13] ? C : 0L);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i] ? C : 0L); break;
            case 2: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L); break;
            case 3: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final CharSequence data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length());
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final CharSequence data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return mix(h);
    }

    /**
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final CharSequence data) {
        if (data == null) return 0;
        return hash(data, 0, data.length());
    }

    /**
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final CharSequence data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return (int)mix(h);
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param data input array
     * @return the 64-bit hash of data
     */
    public long hash64(final String data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length());
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final String data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return mix(h);
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param data input array
     * @return the 32-bit hash of data
     */
    public int hash(final String data) {
        if (data == null) return 0;
        return hash(data, 0, data.length());
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final String data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return (int)mix(h);
    }

    /**
     * @param data input array; null items are tolerated
     * @return the 64-bit hash of data
     */
    public long hash64(final Object[] data) {
        if (data == null) return 0;
        return hash64(data, 0, data.length);
    }

    /**
     * @param data input array; null items are tolerated
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public long hash64(final Object[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]), Objects.hashCode(data[i+4]), Objects.hashCode(data[i+5]), Objects.hashCode(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(Objects.hashCode(data[i+7]), Objects.hashCode(data[i+8]), Objects.hashCode(data[i+9]), Objects.hashCode(data[i+10]), Objects.hashCode(data[i+11]), Objects.hashCode(data[i+12]), Objects.hashCode(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, Objects.hashCode(data[i])); break;
            case 2: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1])); break;
            case 3: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * @param data input array; null items are tolerated
     * @return the 32-bit hash of data
     */
    public int hash(final Object[] data) {
        if (data == null) return 0;
        return hash(data, 0, data.length);
    }

    /**
     * @param data input array; null items are tolerated
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public int hash(final Object[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]), Objects.hashCode(data[i+4]), Objects.hashCode(data[i+5]), Objects.hashCode(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(Objects.hashCode(data[i+7]), Objects.hashCode(data[i+8]), Objects.hashCode(data[i+9]), Objects.hashCode(data[i+10]), Objects.hashCode(data[i+11]), Objects.hashCode(data[i+12]), Objects.hashCode(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, Objects.hashCode(data[i])); break;
            case 2: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1])); break;
            case 3: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param data an input ByteBuffer
     * @return the 64-bit hash of data
     */
    public long hash64(final ByteBuffer data) {
        return hash64(data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 64-bit hash of data
     */
    public long hash64(final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        long h = len ^ seed;
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24), data.getLong(start + 32), data.getLong(start + 40), data.getLong(start + 48));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(start + 56), data.getLong(start + 64), data.getLong(start + 72), data.getLong(start + 80), data.getLong(start + 88), data.getLong(start + 96), data.getLong(start + 104));
            start += 112;
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24));
            start += 32;
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get(start)); break;
            case 2 :  h = mixMultiple(h, data.getShort(start)); break;
            case 3 :  h = mixMultiple(h, data.getShort(start), data.get(start + 2)); break;
            case 4 :  h = mixMultiple(h, data.getInt(start)); break;
            case 5 :  h = mixMultiple(h, data.getInt(start), data.get(start + 4)); break;
            case 6 :  h = mixMultiple(h, data.getInt(start), data.getShort(start + 4)); break;
            case 7 :  h = mixMultiple(h, data.getInt(start), data.getInt(start + 3)); break;
            case 8 :  h = mixMultiple(h, data.getLong(start)); break;
            case 9 :  h = mixMultiple(h, data.getLong(start), data.get(start + 8)); break;
            case 10:  h = mixMultiple(h, data.getLong(start), data.getShort(start + 8)); break;
            case 11:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 7)); break;
            case 12:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 8)); break;
            case 13:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 5)); break;
            case 14:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 6)); break;
            case 15:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 7)); break;
            case 16:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8)); break;
            case 17:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.get(start + 16)); break;
            case 18:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getShort(start + 16)); break;
            case 19:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 15)); break;
            case 20:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 16)); break;
            case 21:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 13)); break;
            case 22:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 14)); break;
            case 23:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 15)); break;
            case 24:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16)); break;
            case 25:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.get(start + 24)); break;
            case 26:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getShort(start + 24)); break;
            case 27:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 23)); break;
            case 28:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 24)); break;
            case 29:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 21)); break;
            case 30:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 22)); break;
            case 31:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 23)); break;
        }
        return mix(h);
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * <br>
     * This is likely to significantly outperform {@link #hash(byte[])} on all but
     * the smallest sequences of bytes (under 20 bytes).
     * @param data an input ByteBuffer
     * @return the 32-bit hash of data
     */
    public int hash(final ByteBuffer data) {
        return hash(data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * <br>
     * This is likely to significantly outperform {@link #hash(byte[], int, int)}
     * on all but the smallest sequences of bytes (under 20 bytes).
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 32-bit hash of data
     */
    public int hash(final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        long h = len ^ seed;
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24), data.getLong(start + 32), data.getLong(start + 40), data.getLong(start + 48));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(start + 56), data.getLong(start + 64), data.getLong(start + 72), data.getLong(start + 80), data.getLong(start + 88), data.getLong(start + 96), data.getLong(start + 104));
            start += 112;
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24));
            start += 32;
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get(start)); break;
            case 2 :  h = mixMultiple(h, data.getShort(start)); break;
            case 3 :  h = mixMultiple(h, data.getShort(start), data.get(start + 2)); break;
            case 4 :  h = mixMultiple(h, data.getInt(start)); break;
            case 5 :  h = mixMultiple(h, data.getInt(start), data.get(start + 4)); break;
            case 6 :  h = mixMultiple(h, data.getInt(start), data.getShort(start + 4)); break;
            case 7 :  h = mixMultiple(h, data.getInt(start), data.getInt(start + 3)); break;
            case 8 :  h = mixMultiple(h, data.getLong(start)); break;
            case 9 :  h = mixMultiple(h, data.getLong(start), data.get(start + 8)); break;
            case 10:  h = mixMultiple(h, data.getLong(start), data.getShort(start + 8)); break;
            case 11:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 7)); break;
            case 12:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 8)); break;
            case 13:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 5)); break;
            case 14:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 6)); break;
            case 15:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 7)); break;
            case 16:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8)); break;
            case 17:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.get(start + 16)); break;
            case 18:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getShort(start + 16)); break;
            case 19:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 15)); break;
            case 20:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 16)); break;
            case 21:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 13)); break;
            case 22:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 14)); break;
            case 23:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 15)); break;
            case 24:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16)); break;
            case 25:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.get(start + 24)); break;
            case 26:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getShort(start + 24)); break;
            case 27:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 23)); break;
            case 28:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 24)); break;
            case 29:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 21)); break;
            case 30:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 22)); break;
            case 31:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 23)); break;
        }
        return (int) mix(h);
    }

    // member functions, function parameter section

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public <T> long hash64(final HashFunction64<T> function, final T[] data) {
        if (data == null) return 0;
        return hash64(function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public <T> long hash64(final HashFunction64<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash64(data[i  ]), function.hash64(data[i+1]), function.hash64(data[i+2]), function.hash64(data[i+3]), function.hash64(data[i+4]), function.hash64(data[i+5]), function.hash64(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash64(data[i+7]), function.hash64(data[i+8]), function.hash64(data[i+9]), function.hash64(data[i+10]), function.hash64(data[i+11]), function.hash64(data[i+12]), function.hash64(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash64(data[i  ]), function.hash64(data[i+1]), function.hash64(data[i+2]), function.hash64(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash64(data[i])); break;
            case 2: h = mixMultiple(h, function.hash64(data[i]), function.hash64(data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash64(data[i]), function.hash64(data[i+1]), function.hash64(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash} method here.
     * @param function typically a method reference to a {@link #hash} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public <T> long hash64(final HashFunction<T> function, final T[] data) {
        if (data == null) return 0;
        return hash64(function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash} method here.
     * @param function typically a method reference to a {@link #hash} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public <T> long hash64(final HashFunction<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash(data[i  ]), function.hash(data[i+1]), function.hash(data[i+2]), function.hash(data[i+3]), function.hash(data[i+4]), function.hash(data[i+5]), function.hash(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash(data[i+7]), function.hash(data[i+8]), function.hash(data[i+9]), function.hash(data[i+10]), function.hash(data[i+11]), function.hash(data[i+12]), function.hash(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash(data[i  ]), function.hash(data[i+1]), function.hash(data[i+2]), function.hash(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash(data[i])); break;
            case 2: h = mixMultiple(h, function.hash(data[i]), function.hash(data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash(data[i]), function.hash(data[i+1]), function.hash(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 32-bit hash of data
     */
    public <T> int hash(final HashFunction64<T> function, final T[] data) {
        if (data == null) return 0;
        return hash(function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 32-bit hash of data
     */
    public <T> int hash(final HashFunction64<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash64(data[i  ]), function.hash64(data[i+1]), function.hash64(data[i+2]), function.hash64(data[i+3]), function.hash64(data[i+4]), function.hash64(data[i+5]), function.hash64(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash64(data[i+7]), function.hash64(data[i+8]), function.hash64(data[i+9]), function.hash64(data[i+10]), function.hash64(data[i+11]), function.hash64(data[i+12]), function.hash64(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash64(data[i  ]), function.hash64(data[i+1]), function.hash64(data[i+2]), function.hash64(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash64(data[i])); break;
            case 2: h = mixMultiple(h, function.hash64(data[i]), function.hash64(data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash64(data[i]), function.hash64(data[i+1]), function.hash64(data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash} method here.
     * @param function typically a method reference to a {@link #hash} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 32-bit hash of data
     */
    public <T> int hash(final HashFunction<T> function, final T[] data) {
        if (data == null) return 0;
        return hash(function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link HashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash} method here.
     * @param function typically a method reference to a {@link #hash} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 32-bit hash of data
     */
    public <T> int hash(final HashFunction<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ seed;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash(data[i  ]), function.hash(data[i+1]), function.hash(data[i+2]), function.hash(data[i+3]), function.hash(data[i+4]), function.hash(data[i+5]), function.hash(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash(data[i+7]), function.hash(data[i+8]), function.hash(data[i+9]), function.hash(data[i+10]), function.hash(data[i+11]), function.hash(data[i+12]), function.hash(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash(data[i  ]), function.hash(data[i+1]), function.hash(data[i+2]), function.hash(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash(data[i])); break;
            case 2: h = mixMultiple(h, function.hash(data[i]), function.hash(data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash(data[i]), function.hash(data[i+1]), function.hash(data[i+2])); break;
        }
        return (int)mix(h);
    }

    // hashing section, seeded static functions

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final long[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final long[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final long[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final long[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final int[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final int[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final int[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final int[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final short[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final short[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final short[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final short[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[])} and passing them to
     * {@link #hash64(long, ByteBuffer)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final byte[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[], int, int)}  and passing them to
     * {@link #hash64(long, ByteBuffer, int, int)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final byte[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[])}  and passing them to
     * {@link #hash(long, ByteBuffer)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final byte[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * If you are expecting mostly inputs larger than about 100 bytes, you should consider wrapping any byte arrays you
     * have using {@link ByteBuffer#wrap(byte[], int, int)}  and passing them to
     * {@link #hash(long, ByteBuffer, int, int)} instead, which
     * will generally be much faster but may accumulate garbage references to ByteBuffers.
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final byte[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final float[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final float[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]), floatToRawIntBits(data[i+4]), floatToRawIntBits(data[i+5]), floatToRawIntBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(floatToRawIntBits(data[i+7]), floatToRawIntBits(data[i+8]), floatToRawIntBits(data[i+9]), floatToRawIntBits(data[i+10]), floatToRawIntBits(data[i+11]), floatToRawIntBits(data[i+12]), floatToRawIntBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, floatToRawIntBits(data[i])); break;
            case 2: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1])); break;
            case 3: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final float[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final float[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]), floatToRawIntBits(data[i+4]), floatToRawIntBits(data[i+5]), floatToRawIntBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(floatToRawIntBits(data[i+7]), floatToRawIntBits(data[i+8]), floatToRawIntBits(data[i+9]), floatToRawIntBits(data[i+10]), floatToRawIntBits(data[i+11]), floatToRawIntBits(data[i+12]), floatToRawIntBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, floatToRawIntBits(data[i  ]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2]), floatToRawIntBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, floatToRawIntBits(data[i])); break;
            case 2: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1])); break;
            case 3: h = mixMultiple(h, floatToRawIntBits(data[i]), floatToRawIntBits(data[i+1]), floatToRawIntBits(data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final double[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final double[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]), doubleToRawLongBits(data[i+4]), doubleToRawLongBits(data[i+5]), doubleToRawLongBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(doubleToRawLongBits(data[i+7]), doubleToRawLongBits(data[i+8]), doubleToRawLongBits(data[i+9]), doubleToRawLongBits(data[i+10]), doubleToRawLongBits(data[i+11]), doubleToRawLongBits(data[i+12]), doubleToRawLongBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, doubleToRawLongBits(data[i])); break;
            case 2: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1])); break;
            case 3: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final double[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final double[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]), doubleToRawLongBits(data[i+4]), doubleToRawLongBits(data[i+5]), doubleToRawLongBits(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(doubleToRawLongBits(data[i+7]), doubleToRawLongBits(data[i+8]), doubleToRawLongBits(data[i+9]), doubleToRawLongBits(data[i+10]), doubleToRawLongBits(data[i+11]), doubleToRawLongBits(data[i+12]), doubleToRawLongBits(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, doubleToRawLongBits(data[i  ]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, doubleToRawLongBits(data[i])); break;
            case 2: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1])); break;
            case 3: h = mixMultiple(h, doubleToRawLongBits(data[i]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final char[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final char[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final char[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final char[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ], data[i+1], data[i+2], data[i+3], data[i+4], data[i+5], data[i+6]);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7], data[i+8], data[i+9], data[i+10], data[i+11], data[i+12], data[i+13]);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ], data[i+1], data[i+2], data[i+3]);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i]); break;
            case 2: h = mixMultiple(h, data[i], data[i+1]); break;
            case 3: h = mixMultiple(h, data[i], data[i+1], data[i+2]); break;
        }
        return (int)mix(h);
    }
    
    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final boolean[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final boolean[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L, data[i+4] ? C : 0L, data[i+5] ? C : 0L, data[i+6] ? C : 0L);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7] ? C : 0L, data[i+8] ? C : 0L, data[i+9] ? C : 0L, data[i+10] ? C : 0L, data[i+11] ? C : 0L, data[i+12] ? C : 0L, data[i+13] ? C : 0L);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i] ? C : 0L); break;
            case 2: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L); break;
            case 3: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final boolean[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final boolean[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L, data[i+4] ? C : 0L, data[i+5] ? C : 0L, data[i+6] ? C : 0L);
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data[i+7] ? C : 0L, data[i+8] ? C : 0L, data[i+9] ? C : 0L, data[i+10] ? C : 0L, data[i+11] ? C : 0L, data[i+12] ? C : 0L, data[i+13] ? C : 0L);
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data[i  ] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L, data[i+3] ? C : 0L);
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data[i] ? C : 0L); break;
            case 2: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L); break;
            case 3: h = mixMultiple(h, data[i] ? C : 0L, data[i+1] ? C : 0L, data[i+2] ? C : 0L); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final CharSequence data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length());
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final CharSequence data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final CharSequence data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length());
    }

    /**
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final CharSequence data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return (int)mix(h);
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param seed any long seed
     * @param data input array
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final String data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length());
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final String data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return mix(h);
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param seed any long seed
     * @param data input array
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final String data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length());
    }

    /**
     * This overload uses the more common
     * String type, rather than CharSequence, to better support hashing arrays of String in other methods.
     * @param seed any long seed
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final String data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length())
            return 0;
        int len = Math.min(length, data.length() - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3), data.charAt(i+4), data.charAt(i+5), data.charAt(i+6));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.charAt(i+7), data.charAt(i+8), data.charAt(i+9), data.charAt(i+10), data.charAt(i+11), data.charAt(i+12), data.charAt(i+13));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, data.charAt(i  ), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, data.charAt(i)); break;
            case 2: h = mixMultiple(h, data.charAt(i), data.charAt(i+1)); break;
            case 3: h = mixMultiple(h, data.charAt(i), data.charAt(i+1), data.charAt(i+2)); break;
        }
        return (int)mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array; null items are tolerated
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final Object[] data) {
        if (data == null) return 0;
        return hash64(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array; null items are tolerated
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final Object[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]), Objects.hashCode(data[i+4]), Objects.hashCode(data[i+5]), Objects.hashCode(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(Objects.hashCode(data[i+7]), Objects.hashCode(data[i+8]), Objects.hashCode(data[i+9]), Objects.hashCode(data[i+10]), Objects.hashCode(data[i+11]), Objects.hashCode(data[i+12]), Objects.hashCode(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, Objects.hashCode(data[i])); break;
            case 2: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1])); break;
            case 3: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * @param seed any long seed
     * @param data input array; null items are tolerated
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final Object[] data) {
        if (data == null) return 0;
        return hash(seed, data, 0, data.length);
    }

    /**
     * @param seed any long seed
     * @param data input array; null items are tolerated
     * @param start starting index in data
     * @param length how many items to use from data
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final Object[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long h = len ^ forward(seed);
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]), Objects.hashCode(data[i+4]), Objects.hashCode(data[i+5]), Objects.hashCode(data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(Objects.hashCode(data[i+7]), Objects.hashCode(data[i+8]), Objects.hashCode(data[i+9]), Objects.hashCode(data[i+10]), Objects.hashCode(data[i+11]), Objects.hashCode(data[i+12]), Objects.hashCode(data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, Objects.hashCode(data[i  ]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2]), Objects.hashCode(data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, Objects.hashCode(data[i])); break;
            case 2: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1])); break;
            case 3: h = mixMultiple(h, Objects.hashCode(data[i]), Objects.hashCode(data[i+1]), Objects.hashCode(data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * <br>
     * This is likely to significantly outperform {@link #hash64(byte[])} on all but
     * the smallest sequences of bytes (under 20 bytes).
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final ByteBuffer data) {
        return hash64(seed, data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * <br>
     * This is likely to significantly outperform {@link #hash64(byte[], int, int)}
     * on all but the smallest sequences of bytes (under 20 bytes).
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 64-bit hash of data
     */
    public static long hash64(final long seed, final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        long h = len ^ forward(seed);
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24), data.getLong(start + 32), data.getLong(start + 40), data.getLong(start + 48));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(start + 56), data.getLong(start + 64), data.getLong(start + 72), data.getLong(start + 80), data.getLong(start + 88), data.getLong(start + 96), data.getLong(start + 104));
            start += 112;
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24));
            start += 32;
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get(start)); break;
            case 2 :  h = mixMultiple(h, data.getShort(start)); break;
            case 3 :  h = mixMultiple(h, data.getShort(start), data.get(start + 2)); break;
            case 4 :  h = mixMultiple(h, data.getInt(start)); break;
            case 5 :  h = mixMultiple(h, data.getInt(start), data.get(start + 4)); break;
            case 6 :  h = mixMultiple(h, data.getInt(start), data.getShort(start + 4)); break;
            case 7 :  h = mixMultiple(h, data.getInt(start), data.getInt(start + 3)); break;
            case 8 :  h = mixMultiple(h, data.getLong(start)); break;
            case 9 :  h = mixMultiple(h, data.getLong(start), data.get(start + 8)); break;
            case 10:  h = mixMultiple(h, data.getLong(start), data.getShort(start + 8)); break;
            case 11:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 7)); break;
            case 12:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 8)); break;
            case 13:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 5)); break;
            case 14:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 6)); break;
            case 15:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 7)); break;
            case 16:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8)); break;
            case 17:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.get(start + 16)); break;
            case 18:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getShort(start + 16)); break;
            case 19:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 15)); break;
            case 20:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 16)); break;
            case 21:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 13)); break;
            case 22:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 14)); break;
            case 23:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 15)); break;
            case 24:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16)); break;
            case 25:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.get(start + 24)); break;
            case 26:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getShort(start + 24)); break;
            case 27:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 23)); break;
            case 28:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 24)); break;
            case 29:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 21)); break;
            case 30:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 22)); break;
            case 31:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 23)); break;
        }
        return mix(h);
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * <br>
     * This is likely to significantly outperform {@link #hash(byte[])} on all but
     * the smallest sequences of bytes (under 20 bytes).
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final ByteBuffer data) {
        return hash(seed, data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * <br>
     * This is likely to significantly outperform {@link #hash(byte[], int, int)}
     * on all but the smallest sequences of bytes (under 20 bytes).
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 32-bit hash of data
     */
    public static int hash(final long seed, final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        long h = len ^ forward(seed);
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24), data.getLong(start + 32), data.getLong(start + 40), data.getLong(start + 48));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(start + 56), data.getLong(start + 64), data.getLong(start + 72), data.getLong(start + 80), data.getLong(start + 88), data.getLong(start + 96), data.getLong(start + 104));
            start += 112;
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 24));
            start += 32;
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get(start)); break;
            case 2 :  h = mixMultiple(h, data.getShort(start)); break;
            case 3 :  h = mixMultiple(h, data.getShort(start), data.get(start + 2)); break;
            case 4 :  h = mixMultiple(h, data.getInt(start)); break;
            case 5 :  h = mixMultiple(h, data.getInt(start), data.get(start + 4)); break;
            case 6 :  h = mixMultiple(h, data.getInt(start), data.getShort(start + 4)); break;
            case 7 :  h = mixMultiple(h, data.getInt(start), data.getInt(start + 3)); break;
            case 8 :  h = mixMultiple(h, data.getLong(start)); break;
            case 9 :  h = mixMultiple(h, data.getLong(start), data.get(start + 8)); break;
            case 10:  h = mixMultiple(h, data.getLong(start), data.getShort(start + 8)); break;
            case 11:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 7)); break;
            case 12:  h = mixMultiple(h, data.getLong(start), data.getInt(start + 8)); break;
            case 13:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 5)); break;
            case 14:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 6)); break;
            case 15:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 7)); break;
            case 16:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8)); break;
            case 17:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.get(start + 16)); break;
            case 18:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getShort(start + 16)); break;
            case 19:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 15)); break;
            case 20:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getInt(start + 16)); break;
            case 21:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 13)); break;
            case 22:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 14)); break;
            case 23:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 15)); break;
            case 24:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16)); break;
            case 25:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.get(start + 24)); break;
            case 26:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getShort(start + 24)); break;
            case 27:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 23)); break;
            case 28:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getInt(start + 24)); break;
            case 29:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 21)); break;
            case 30:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 22)); break;
            case 31:  h = mixMultiple(h, data.getLong(start), data.getLong(start + 8), data.getLong(start + 16), data.getLong(start + 23)); break;
        }
        return (int) mix(h);
    }

    // seeded static functions, function parameter section

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> long hash64(final long seed, final SeededHashFunction64<T> function, final T[] data) {
        if (data == null) return 0;
        return hash64(seed, function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> long hash64(final long seed, final SeededHashFunction64<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long sd = forward(seed), h = len ^ sd;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash64(sd, data[i  ]), function.hash64(sd, data[i+1]), function.hash64(sd, data[i+2]), function.hash64(sd, data[i+3]), function.hash64(sd, data[i+4]), function.hash64(sd, data[i+5]), function.hash64(sd, data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash64(sd, data[i+7]), function.hash64(sd, data[i+8]), function.hash64(sd, data[i+9]), function.hash64(sd, data[i+10]), function.hash64(sd, data[i+11]), function.hash64(sd, data[i+12]), function.hash64(sd, data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash64(sd, data[i  ]), function.hash64(sd, data[i+1]), function.hash64(sd, data[i+2]), function.hash64(sd, data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash64(sd, data[i])); break;
            case 2: h = mixMultiple(h, function.hash64(sd, data[i]), function.hash64(sd, data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash64(sd, data[i]), function.hash64(sd, data[i+1]), function.hash64(sd, data[i+2])); break;
        }
        return mix(h);
    }
    
    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> long hash64(final long seed, final SeededHashFunction<T> function, final T[] data) {
        if (data == null) return 0;
        return hash64(seed, function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> long hash64(final long seed, final SeededHashFunction<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long sd = forward(seed), h = len ^ sd;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash(sd, data[i  ]), function.hash(sd, data[i+1]), function.hash(sd, data[i+2]), function.hash(sd, data[i+3]), function.hash(sd, data[i+4]), function.hash(sd, data[i+5]), function.hash(sd, data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash(sd, data[i+7]), function.hash(sd, data[i+8]), function.hash(sd, data[i+9]), function.hash(sd, data[i+10]), function.hash(sd, data[i+11]), function.hash(sd, data[i+12]), function.hash(sd, data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash(sd, data[i  ]), function.hash(sd, data[i+1]), function.hash(sd, data[i+2]), function.hash(sd, data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash(sd, data[i])); break;
            case 2: h = mixMultiple(h, function.hash(sd, data[i]), function.hash(sd, data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash(sd, data[i]), function.hash(sd, data[i+1]), function.hash(sd, data[i+2])); break;
        }
        return mix(h);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> int hash(final long seed, final SeededHashFunction64<T> function, final T[] data) {
        if (data == null) return 0;
        return hash(seed, function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction64} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> int hash(final long seed, final SeededHashFunction64<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long sd = forward(seed), h = len ^ sd;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash64(sd, data[i  ]), function.hash64(sd, data[i+1]), function.hash64(sd, data[i+2]), function.hash64(sd, data[i+3]), function.hash64(sd, data[i+4]), function.hash64(sd, data[i+5]), function.hash64(sd, data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash64(sd, data[i+7]), function.hash64(sd, data[i+8]), function.hash64(sd, data[i+9]), function.hash64(sd, data[i+10]), function.hash64(sd, data[i+11]), function.hash64(sd, data[i+12]), function.hash64(sd, data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash64(sd, data[i  ]), function.hash64(sd, data[i+1]), function.hash64(sd, data[i+2]), function.hash64(sd, data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash64(sd, data[i])); break;
            case 2: h = mixMultiple(h, function.hash64(sd, data[i]), function.hash64(sd, data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash64(sd, data[i]), function.hash64(sd, data[i+1]), function.hash64(sd, data[i+2])); break;
        }
        return (int)mix(h);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash64} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash64} method here
     * @param data input array
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> int hash(final long seed, final SeededHashFunction<T> function, final T[] data) {
        if (data == null) return 0;
        return hash(seed, function, data, 0, data.length);
    }

    /**
     * Meant to handle hashing larger 2D arrays (or higher dimensions), this lets you pass a {@link SeededHashFunction} as
     * the first parameter, and then this uses that function to get a hash for each T item in data. T is usually an
     * array type, and function is usually a method reference to a {@link #hash} method here.
     * @param seed any long seed
     * @param function typically a method reference to a {@link #hash} method here
     * @param data input array
     * @param start starting index in data
     * @param length how many items to use from data
     * @param <T> typically an array type, often of primitive items; may be more than one-dimensional
     * @return the 64-bit hash of data
     */
    public static <T> int hash(final long seed, final SeededHashFunction<T> function, final T[] data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.length)
            return 0;
        int len = Math.min(length, data.length - start);
        long sd = forward(seed), h = len ^ sd;
        int i = start;
        while(len >= 14){
            len -= 14;
            h *= C;
            h += mixMultiple(function.hash(sd, data[i  ]), function.hash(sd, data[i+1]), function.hash(sd, data[i+2]), function.hash(sd, data[i+3]), function.hash(sd, data[i+4]), function.hash(sd, data[i+5]), function.hash(sd, data[i+6]));
            h = (h << 39 | h >>> 25);
            h += mixMultiple(function.hash(sd, data[i+7]), function.hash(sd, data[i+8]), function.hash(sd, data[i+9]), function.hash(sd, data[i+10]), function.hash(sd, data[i+11]), function.hash(sd, data[i+12]), function.hash(sd, data[i+13]));
            i += 14;
        }
        while(len >= 4){
            len -= 4;
            h = mixMultiple(h, function.hash(sd, data[i  ]), function.hash(sd, data[i+1]), function.hash(sd, data[i+2]), function.hash(sd, data[i+3]));
            i += 4;
        }
        switch (len) {
            case 1: h = mixMultiple(h, function.hash(sd, data[i])); break;
            case 2: h = mixMultiple(h, function.hash(sd, data[i]), function.hash(sd, data[i+1])); break;
            case 3: h = mixMultiple(h, function.hash(sd, data[i]), function.hash(sd, data[i+1]), function.hash(sd, data[i+2])); break;
        }
        return (int)mix(h);
    }

    // predefined HashFunction instances, to avoid lots of casting

    public static final SeededHashFunction64<boolean[]> booleanArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<byte[]> byteArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<short[]> shortArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<int[]> intArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<long[]> longArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<float[]> floatArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<double[]> doubleArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<char[]> charArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<CharSequence> charSequenceHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<String> stringHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<Object[]> objectArrayHash64 = AdzeHasher::hash64;
    public static final SeededHashFunction64<ByteBuffer> byteBufferHash64 = AdzeHasher::hash64;

    public static final SeededHashFunction64<boolean[][]> booleanArray2DHash64 = (long seed, boolean[][] data) -> hash64(seed, booleanArrayHash64, data);
    public static final SeededHashFunction64<byte[][]> byteArray2DHash64 = (long seed, byte[][] data) -> hash64(seed, byteArrayHash64, data);
    public static final SeededHashFunction64<short[][]> shortArray2DHash64 = (long seed, short[][] data) -> hash64(seed, shortArrayHash64, data);
    public static final SeededHashFunction64<int[][]> intArray2DHash64 = (long seed, int[][] data) -> hash64(seed, intArrayHash64, data);
    public static final SeededHashFunction64<long[][]> longArray2DHash64 = (long seed, long[][] data) -> hash64(seed, longArrayHash64, data);
    public static final SeededHashFunction64<float[][]> floatArray2DHash64 = (long seed, float[][] data) -> hash64(seed, floatArrayHash64, data);
    public static final SeededHashFunction64<double[][]> doubleArray2DHash64 = (long seed, double[][] data) -> hash64(seed, doubleArrayHash64, data);
    public static final SeededHashFunction64<char[][]> charArray2DHash64 = (long seed, char[][] data) -> hash64(seed, charArrayHash64, data);
    public static final SeededHashFunction64<CharSequence[]> charSequenceArrayHash64 = (long seed, CharSequence[] data) -> hash64(seed, charSequenceHash64, data);
    public static final SeededHashFunction64<String[]> stringArrayHash64 = (long seed, String[] data) -> hash64(seed, stringHash64, data);
    public static final SeededHashFunction64<Object[][]> objectArray2DHash64 = (long seed, Object[][] data) -> hash64(seed, objectArrayHash64, data);
    public static final SeededHashFunction64<ByteBuffer[]> byteBufferArrayHash64 = (long seed, ByteBuffer[] data) -> hash64(seed, byteBufferHash64, data);

    public static final SeededHashFunction64<boolean[][][]> booleanArray3DHash64 = (long seed, boolean[][][] data) -> hash64(seed, booleanArray2DHash64, data);
    public static final SeededHashFunction64<byte[][][]> byteArray3DHash64 = (long seed, byte[][][] data) -> hash64(seed, byteArray2DHash64, data);
    public static final SeededHashFunction64<short[][][]> shortArray3DHash64 = (long seed, short[][][] data) -> hash64(seed, shortArray2DHash64, data);
    public static final SeededHashFunction64<int[][][]> intArray3DHash64 = (long seed, int[][][] data) -> hash64(seed, intArray2DHash64, data);
    public static final SeededHashFunction64<long[][][]> longArray3DHash64 = (long seed, long[][][] data) -> hash64(seed, longArray2DHash64, data);
    public static final SeededHashFunction64<float[][][]> floatArray3DHash64 = (long seed, float[][][] data) -> hash64(seed, floatArray2DHash64, data);
    public static final SeededHashFunction64<double[][][]> doubleArray3DHash64 = (long seed, double[][][] data) -> hash64(seed, doubleArray2DHash64, data);
    public static final SeededHashFunction64<char[][][]> charArray3DHash64 = (long seed, char[][][] data) -> hash64(seed, charArray2DHash64, data);
    public static final SeededHashFunction64<CharSequence[][]> charSequenceArray2DHash64 = (long seed, CharSequence[][] data) -> hash64(seed, charSequenceArrayHash64, data);
    public static final SeededHashFunction64<String[][]> stringArray2DHash64 = (long seed, String[][] data) -> hash64(seed, stringArrayHash64, data);
    public static final SeededHashFunction64<Object[][][]> objectArray3DHash64 = (long seed, Object[][][] data) -> hash64(seed, objectArray2DHash64, data);
    public static final SeededHashFunction64<ByteBuffer[][]> byteBufferArray2DHash64 = (long seed, ByteBuffer[][] data) -> hash64(seed, byteBufferArrayHash64, data);

    public static final SeededHashFunction64<CharSequence[][][]> charSequenceArray3DHash64 = (long seed, CharSequence[][][] data) -> hash64(seed, charSequenceArray2DHash64, data);
    public static final SeededHashFunction64<String[][][]> stringArray3DHash64 = (long seed, String[][][] data) -> hash64(seed, stringArray2DHash64, data);
    public static final SeededHashFunction64<ByteBuffer[][][]> byteBufferArray3DHash64 = (long seed, ByteBuffer[][][] data) -> hash64(seed, byteBufferArray2DHash64, data);

    public static final SeededHashFunction<boolean[]> booleanArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<byte[]> byteArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<short[]> shortArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<int[]> intArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<long[]> longArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<float[]> floatArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<double[]> doubleArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<char[]> charArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<CharSequence> charSequenceHash = AdzeHasher::hash;
    public static final SeededHashFunction<String> stringHash = AdzeHasher::hash;
    public static final SeededHashFunction<Object[]> objectArrayHash = AdzeHasher::hash;
    public static final SeededHashFunction<ByteBuffer> byteBufferHash = AdzeHasher::hash;

    public static final SeededHashFunction<boolean[][]> booleanArray2DHash = (long seed, boolean[][] data) -> hash(seed, booleanArrayHash, data);
    public static final SeededHashFunction<byte[][]> byteArray2DHash = (long seed, byte[][] data) -> hash(seed, byteArrayHash, data);
    public static final SeededHashFunction<short[][]> shortArray2DHash = (long seed, short[][] data) -> hash(seed, shortArrayHash, data);
    public static final SeededHashFunction<int[][]> intArray2DHash = (long seed, int[][] data) -> hash(seed, intArrayHash, data);
    public static final SeededHashFunction<long[][]> longArray2DHash = (long seed, long[][] data) -> hash(seed, longArrayHash, data);
    public static final SeededHashFunction<float[][]> floatArray2DHash = (long seed, float[][] data) -> hash(seed, floatArrayHash, data);
    public static final SeededHashFunction<double[][]> doubleArray2DHash = (long seed, double[][] data) -> hash(seed, doubleArrayHash, data);
    public static final SeededHashFunction<char[][]> charArray2DHash = (long seed, char[][] data) -> hash(seed, charArrayHash, data);
    public static final SeededHashFunction<CharSequence[]> charSequenceArrayHash = (long seed, CharSequence[] data) -> hash(seed, charSequenceHash, data);
    public static final SeededHashFunction<String[]> stringArrayHash = (long seed, String[] data) -> hash(seed, stringHash, data);
    public static final SeededHashFunction<Object[][]> objectArray2DHash = (long seed, Object[][] data) -> hash(seed, objectArrayHash, data);
    public static final SeededHashFunction<ByteBuffer[]> byteBufferArrayHash = (long seed, ByteBuffer[] data) -> hash(seed, byteBufferHash, data);

    public static final SeededHashFunction<boolean[][][]> booleanArray3DHash = (long seed, boolean[][][] data) -> hash(seed, booleanArray2DHash, data);
    public static final SeededHashFunction<byte[][][]> byteArray3DHash = (long seed, byte[][][] data) -> hash(seed, byteArray2DHash, data);
    public static final SeededHashFunction<short[][][]> shortArray3DHash = (long seed, short[][][] data) -> hash(seed, shortArray2DHash, data);
    public static final SeededHashFunction<int[][][]> intArray3DHash = (long seed, int[][][] data) -> hash(seed, intArray2DHash, data);
    public static final SeededHashFunction<long[][][]> longArray3DHash = (long seed, long[][][] data) -> hash(seed, longArray2DHash, data);
    public static final SeededHashFunction<float[][][]> floatArray3DHash = (long seed, float[][][] data) -> hash(seed, floatArray2DHash, data);
    public static final SeededHashFunction<double[][][]> doubleArray3DHash = (long seed, double[][][] data) -> hash(seed, doubleArray2DHash, data);
    public static final SeededHashFunction<char[][][]> charArray3DHash = (long seed, char[][][] data) -> hash(seed, charArray2DHash, data);
    public static final SeededHashFunction<CharSequence[][]> charSequenceArray2DHash = (long seed, CharSequence[][] data) -> hash(seed, charSequenceArrayHash, data);
    public static final SeededHashFunction<String[][]> stringArray2DHash = (long seed, String[][] data) -> hash(seed, stringArrayHash, data);
    public static final SeededHashFunction<Object[][][]> objectArray3DHash = (long seed, Object[][][] data) -> hash(seed, objectArray2DHash, data);
    public static final SeededHashFunction<ByteBuffer[][]> byteBufferArray2DHash = (long seed, ByteBuffer[][] data) -> hash(seed, byteBufferArrayHash, data);

    public static final SeededHashFunction<CharSequence[][][]> charSequenceArray3DHash = (long seed, CharSequence[][][] data) -> hash(seed, charSequenceArray2DHash, data);
    public static final SeededHashFunction<String[][][]> stringArray3DHash = (long seed, String[][][] data) -> hash(seed, stringArray2DHash, data);
    public static final SeededHashFunction<ByteBuffer[][][]> byteBufferArray3DHash = (long seed, ByteBuffer[][][] data) -> hash(seed, byteBufferArray2DHash, data);

    // normal Java Object stuff

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdzeHasher hasher = (AdzeHasher) o;

        return seed == hasher.seed;
    }

    /**
     * Produces a String that holds the entire seed of this AdzeHasher. An AdzeHasher is immutable, so to load the
     * serialized state you must create a new AdzeHasher with {@link #deserializeFromString(CharSequence)}.
     * @return a String holding the seed of this AdzeHasher, to be loaded by {@link #deserializeFromString(CharSequence)}
     */
    public String serializeToString() {
        return appendSerialized(new StringBuilder(11)).toString();
    }
    /**
     * Appends the textual form of this AdzeHasher to the given StringBuilder, StringBuffer, CharBuffer, or similar.
     * You can recover this state from such a textual form by calling {@link #deserializeFromString(CharSequence)} to
     * create a new AdzeHasher.
     * @param sb an Appendable CharSequence that will be modified
     * @return {@code sb}, for chaining
     * @param <T> any type that is both a CharSequence and an Appendable, such as StringBuilder, StringBuffer, or CharBuffer
     */
    public <T extends CharSequence & Appendable> T appendSerialized(T sb) {
        Base.SIMPLE64.appendUnsigned(sb, reverse(seed));
        return sb;
    }

    /**
     * Given a String or other CharSequence produced by {@link #serializeToString()}, this creates a new AdzeHasher with the
     * seed stored in the start of that CharSequence.
     *
     * @param data a String or other CharSequence produced by {@link #serializeToString()}
     * @return a new AdzeHasher with a seed loaded from the given String or other CharSequence
     */
    public static AdzeHasher deserializeFromString(CharSequence data) {
        return deserializeFromString(data, 0);
    }
    /**
     * Given a String or other CharSequence produced by {@link #serializeToString()} or
     * {@link #appendSerialized(CharSequence)} and an offset to indicate where to
     * read 11 chars from that CharSequence, this creates a new AdzeHasher with the seed stored in that CharSequence.
     * @param data a String or other CharSequence produced by {@link #serializeToString()}
     * @param offset where to start reading the 11 chars of a serialized state from data
     * @return a new AdzeHasher with a seed loaded from the given String or other CharSequence
     */
    public static AdzeHasher deserializeFromString(CharSequence data, int offset) {
        if(data == null || offset < 0 || data.length() - offset < 11) return AdzeHasher.hydrogen;
        return new AdzeHasher(Base.SIMPLE64.readLong(data, offset, offset + 11));
    }

    /**
     * This shouldn't ever be necessary, because an AdzeHasher is entirely immutable, but if for some reason you need a
     * duplicate of an existing AdzeHasher, this exists. Normally you can just reference an existing AdzeHasher, though!
     * @return a new AdzeHasher with the same seed as this one
     */
    public AdzeHasher copy() {
        return new AdzeHasher(reverse(seed));
    }

    @Override
    public int hashCode() {
        return (int) (seed ^ (seed >>> 32));
    }

    @Override
    public String toString() {
        return "AdzeHasher{" +
                "seed=" + seed +
                '}';
    }


    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param data an input ByteBuffer
     * @return the 64-bit hash of data
     */
    public long hashBulk64Old(final ByteBuffer data) {
        return hashBulk64Old(data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 64-bit hash of data
     */
    public long hashBulk64Old(final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        data.position(start);
        long h = len ^ forward(seed);
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get()); break;
            case 2 :  h = mixMultiple(h, data.getShort()); break;
            case 3 :  h = mixMultiple(h, data.getShort(), data.get()); break;
            case 4 :  h = mixMultiple(h, data.getInt()); break;
            case 5 :  h = mixMultiple(h, data.getInt(), data.get()); break;
            case 6 :  h = mixMultiple(h, data.getInt(), data.getShort()); break;
            case 7 :  h = mixMultiple(h, data.getInt(), data.getShort(), data.get()); break;
            case 8 :  h = mixMultiple(h, data.getLong()); break;
            case 9 :  h = mixMultiple(h, data.getLong(), data.get()); break;
            case 10:  h = mixMultiple(h, data.getLong(), data.getShort()); break;
            case 11:  h = mixMultiple(h, data.getLong(), data.getShort(), data.get()); break;
            case 12:  h = mixMultiple(h, data.getLong(), data.getInt()); break;
            case 13:  h = mixMultiple(h, data.getLong(), data.getInt(), data.get()); break;
            case 14:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort()); break;
            case 15:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 16:  h = mixMultiple(h, data.getLong(), data.getLong()); break;
            case 17:  h = mixMultiple(h, data.getLong(), data.getLong(), data.get()); break;
            case 18:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort()); break;
            case 19:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 20:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt()); break;
            case 21:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 22:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 23:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 24:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong()); break;
            case 25:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.get()); break;
            case 26:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort()); break;
            case 27:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 28:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt()); break;
            case 29:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 30:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 31:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
        }
        return mix(h);
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param data an input ByteBuffer
     * @return the 32-bit hash of data
     */
    public int hashBulkOld(final ByteBuffer data) {
        return hashBulkOld(data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 32-bit hash of data
     */
    public int hashBulkOld(final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        data.position(start);
        long h = len ^ forward(seed);
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get()); break;
            case 2 :  h = mixMultiple(h, data.getShort()); break;
            case 3 :  h = mixMultiple(h, data.getShort(), data.get()); break;
            case 4 :  h = mixMultiple(h, data.getInt()); break;
            case 5 :  h = mixMultiple(h, data.getInt(), data.get()); break;
            case 6 :  h = mixMultiple(h, data.getInt(), data.getShort()); break;
            case 7 :  h = mixMultiple(h, data.getInt(), data.getShort(), data.get()); break;
            case 8 :  h = mixMultiple(h, data.getLong()); break;
            case 9 :  h = mixMultiple(h, data.getLong(), data.get()); break;
            case 10:  h = mixMultiple(h, data.getLong(), data.getShort()); break;
            case 11:  h = mixMultiple(h, data.getLong(), data.getShort(), data.get()); break;
            case 12:  h = mixMultiple(h, data.getLong(), data.getInt()); break;
            case 13:  h = mixMultiple(h, data.getLong(), data.getInt(), data.get()); break;
            case 14:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort()); break;
            case 15:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 16:  h = mixMultiple(h, data.getLong(), data.getLong()); break;
            case 17:  h = mixMultiple(h, data.getLong(), data.getLong(), data.get()); break;
            case 18:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort()); break;
            case 19:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 20:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt()); break;
            case 21:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 22:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 23:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 24:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong()); break;
            case 25:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.get()); break;
            case 26:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort()); break;
            case 27:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 28:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt()); break;
            case 29:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 30:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 31:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
        }
        return (int) mix(h);
    }


    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @return the 64-bit hash of data
     */
    public static long hashBulk64Old(final long seed, final ByteBuffer data) {
        return hashBulk64Old(seed, data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 64-bit hash of data
     */
    public static long hashBulk64Old(final long seed, final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        data.position(start);
        long h = len ^ forward(seed);
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get()); break;
            case 2 :  h = mixMultiple(h, data.getShort()); break;
            case 3 :  h = mixMultiple(h, data.getShort(), data.get()); break;
            case 4 :  h = mixMultiple(h, data.getInt()); break;
            case 5 :  h = mixMultiple(h, data.getInt(), data.get()); break;
            case 6 :  h = mixMultiple(h, data.getInt(), data.getShort()); break;
            case 7 :  h = mixMultiple(h, data.getInt(), data.getShort(), data.get()); break;
            case 8 :  h = mixMultiple(h, data.getLong()); break;
            case 9 :  h = mixMultiple(h, data.getLong(), data.get()); break;
            case 10:  h = mixMultiple(h, data.getLong(), data.getShort()); break;
            case 11:  h = mixMultiple(h, data.getLong(), data.getShort(), data.get()); break;
            case 12:  h = mixMultiple(h, data.getLong(), data.getInt()); break;
            case 13:  h = mixMultiple(h, data.getLong(), data.getInt(), data.get()); break;
            case 14:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort()); break;
            case 15:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 16:  h = mixMultiple(h, data.getLong(), data.getLong()); break;
            case 17:  h = mixMultiple(h, data.getLong(), data.getLong(), data.get()); break;
            case 18:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort()); break;
            case 19:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 20:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt()); break;
            case 21:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 22:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 23:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 24:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong()); break;
            case 25:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.get()); break;
            case 26:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort()); break;
            case 27:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 28:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt()); break;
            case 29:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 30:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 31:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
        }
        return mix(h);
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, hashing everything from index 0 to just before index
     * {@link ByteBuffer#limit()}. The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @return the 32-bit hash of data
     */
    public static int hashBulkOld(final long seed, final ByteBuffer data) {
        return hashBulkOld(seed, data, 0, data.limit());
    }

    /**
     * A hashing function that operates on a {@link ByteBuffer}, using the given {@code start} index (measured in bytes)
     * and {@code length} (also in bytes). The {@link ByteBuffer#limit() limit} must be set on data; this will not read
     * past the limit.
     * @param seed any long seed
     * @param data an input ByteBuffer
     * @param start the starting index, measured in bytes
     * @param length the number of bytes to hash
     * @return the 32-bit hash of data
     */
    public static int hashBulkOld(final long seed, final ByteBuffer data, int start, int length) {
        if (data == null || start < 0 || length < 0 || start >= data.limit())
            return 0;
        int len = Math.min(length, data.limit() - start);
        data.position(start);
        long h = len ^ forward(seed);
        while(len >= 112){
            len -= 112;
            h *= C;
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
            h = (h << 39 | h >>> 25);
            h += mixMultiple(data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        while(len >= 32){
            len -= 32;
            h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getLong());
        }
        switch (len) {
            case 1 :  h = mixMultiple(h, data.get()); break;
            case 2 :  h = mixMultiple(h, data.getShort()); break;
            case 3 :  h = mixMultiple(h, data.getShort(), data.get()); break;
            case 4 :  h = mixMultiple(h, data.getInt()); break;
            case 5 :  h = mixMultiple(h, data.getInt(), data.get()); break;
            case 6 :  h = mixMultiple(h, data.getInt(), data.getShort()); break;
            case 7 :  h = mixMultiple(h, data.getInt(), data.getShort(), data.get()); break;
            case 8 :  h = mixMultiple(h, data.getLong()); break;
            case 9 :  h = mixMultiple(h, data.getLong(), data.get()); break;
            case 10:  h = mixMultiple(h, data.getLong(), data.getShort()); break;
            case 11:  h = mixMultiple(h, data.getLong(), data.getShort(), data.get()); break;
            case 12:  h = mixMultiple(h, data.getLong(), data.getInt()); break;
            case 13:  h = mixMultiple(h, data.getLong(), data.getInt(), data.get()); break;
            case 14:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort()); break;
            case 15:  h = mixMultiple(h, data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 16:  h = mixMultiple(h, data.getLong(), data.getLong()); break;
            case 17:  h = mixMultiple(h, data.getLong(), data.getLong(), data.get()); break;
            case 18:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort()); break;
            case 19:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 20:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt()); break;
            case 21:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 22:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 23:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
            case 24:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong()); break;
            case 25:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.get()); break;
            case 26:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort()); break;
            case 27:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getShort(), data.get()); break;
            case 28:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt()); break;
            case 29:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.get()); break;
            case 30:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort()); break;
            case 31:  h = mixMultiple(h, data.getLong(), data.getLong(), data.getLong(), data.getInt(), data.getShort(), data.get()); break;
        }
        return (int) mix(h);
    }
}
