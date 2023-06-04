package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.digital.Hasher;
import squidpony.squidmath.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.github.tommyettinger.squidlib.CrossHash.Water.*;
import static squidpony.squidmath.NumberTools.*;

/**
 * 64-bit and 32-bit hashing functions that we can rely on staying the same cross-platform.
 * Several algorithms are present here, each with some tradeoffs for performance, quality,
 * and extra features. Each algorithm was designed for speed and general-purpose usability,
 * but not cryptographic security.
 * <br>
 * The hashes this returns are always 0 when given null to hash. Arrays with
 * identical elements of identical types will hash identically. Arrays with identical
 * numerical values but different types will sometimes hash differently. This class
 * always provides 64-bit hashes via hash64() and 32-bit hashes via hash(), and Wisp
 * provides a hash32() method that matches older behavior and uses only 32-bit math.
 * The hash64() and hash() methods, except in Hive, use 64-bit math even when producing
 * 32-bit hashes, for GWT reasons. GWT doesn't have the same behavior as desktop and
 * Android applications when using ints because it treats doubles mostly like ints,
 * sometimes, due to it using JavaScript. If we use mainly longs, though, GWT emulates
 * the longs with a more complex technique behind-the-scenes, that behaves the same on
 * the web as it does on desktop or on a phone. Since CrossHash is supposed to be stable
 * cross-platform, this is the way we need to go, despite it being slightly slower.
 * <br>
 * The static methods in CrossHash, like {@link #hash64(int[])}, delegate to the {@link Water}
 * algorithm. This is a fairly fast and heavily-tested hash that developed from something like
 * Wang Yi's wyhash algorithm, though only the constants and the general concept of a mum()
 * function are shared with wyhash. There are several static inner classes in CrossHash
 * {@link Water} (already mentioned), {@link Yolk} (which is very close to Water but allows a
 * 64-bit salt or seed), {@link Curlup} (which is the fastest hash here for larger inputs, and
 * also allows a 64-bit seed), {@link Mist} (which allows a 128-bit salt, but has mediocre
 * quality), {@link Hive} (which is mostly here for compatibility, but has OK quality and good
 * collision rates), and {@link Wisp} (which is fast for small inputs but has bad collision
 * rates). There's also the inner IHasher interface, and the classes that implement it.
 * Water, Yolk, and Curlup all pass the rigorous SMHasher test battery. The others don't pass
 * it in full, or sometimes at all.
 * <br>
 * IHasher values are provided as static fields, and use Water to hash a specific type or fall
 * back to Object.hashCode if given an object with the wrong type. IHasher values are optional
 * parts of OrderedMap, OrderedSet, Arrangement, and the various classes that use Arrangement
 * like K2 and K2V1, and allow arrays to be used as keys in those collections while keeping
 * hashing by value instead of the normal hashing by reference for arrays. You probably won't
 * ever need to make a class that implements IHasher yourself; for some cases you may want to
 * look at the {@link Hashers} class for additional functions.
 * <br>
 * Note: This class was formerly called StableHash, but since that refers to a specific
 * category of hashing algorithm that this is not, and since the goal is to be cross-
 * platform, the name was changed to CrossHash.
 * Note 2: FNV-1a was removed from SquidLib on July 25, 2017, and replaced as default with Wisp; Wisp
 * was later replaced as default by Hive, and in June 2019 Hive was replaced by Water. Wisp was used
 * because at the time SquidLib preferred 64-bit math when math needed to be the same across platforms;
 * math on longs behaves the same on GWT as on desktop, despite being slower. Hive passed an older version
 * of SMHasher, a testing suite for hashes, where Wisp does not (it fails just like Arrays.hashCode()
 * does). Hive uses a cross-platform subset of the possible 32-bit math operations when producing 32-bit
 * hashes of data that doesn't involve longs or doubles, and this should speed up the CrossHash.Hive.hash()
 * methods a lot on GWT, but it slows down 32-bit output on desktop-class JVMs. Water became the default
 * when newer versions of SMHasher showed that Hive wasn't as high-quality as it had appeared, and the
 * recently-debuted wyhash by Wang Yi, a variation on a hash called MUM, opened some possibilities for
 * structures that are simple but also very fast. Water is modeled after wyhash and uses the same constants
 * in its hash64() methods, but avoids the 128-bit multiplication that wyhash uses. Because both wyhash and
 * Water operate on 4 items at a time, they tend to be very fast on desktop platforms, but Water probably
 * won't be amazing at GWT performance. Similarly, the recently-added Curlup performs very well due to SIMD
 * optimizations that HotSpot performs, and probably won't do as well on GWT or Android.
 * <br>
 * Created by Tommy Ettinger on 1/16/2016.
 * @author Tommy Ettinger
 */
public class CrossHash {
    public static long hash64(final CharSequence data) {
        return Water.hash64(data);
    }

    public static long hash64(final boolean[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final byte[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final short[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final int[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final long[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final char[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final float[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final double[] data) {
        return Water.hash64(data);
    }

    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the char array to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 64-bit hash code for the requested section of data
     */
    public static long hash64(final char[] data, final int start, final int end) {
        return Water.hash64(data, start, end);
    }

    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the String or other CharSequence to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 64-bit hash code for the requested section of data
     */
    public static long hash64(final CharSequence data, final int start, final int end) {
        return Water.hash64(data, start, end);
    }

    public static long hash64(final char[][] data) {
        return Water.hash64(data);
    }

    public static long hash64(final int[][] data) {
        return Water.hash64(data);
    }

    public static long hash64(final long[][] data) {
        return Water.hash64(data);
    }

    public static long hash64(final CharSequence[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final CharSequence[]... data) {
        return Water.hash64(data);
    }

    public static long hash64(final Iterable<? extends CharSequence> data) {
        return Water.hash64(data);
    }

    public static long hash64(final List<? extends CharSequence> data) {
        return Water.hash64(data);
    }

    public static long hash64(final Object[] data) {
        return Water.hash64(data);
    }

    public static long hash64(final Object data) {
        return Water.hash64(data);
    }


    public static int hash(final CharSequence data) {
        return Water.hash(data);
    }

    public static int hash(final boolean[] data) {
        return Water.hash(data);
    }

    public static int hash(final byte[] data) {
        return Water.hash(data);
    }

    public static int hash(final short[] data) {
        return Water.hash(data);
    }

    public static int hash(final int[] data) {
        return Water.hash(data);
    }

    public static int hash(final long[] data) {
        return Water.hash(data);
    }

    public static int hash(final char[] data) {
        return Water.hash(data);
    }

    public static int hash(final float[] data) {
        return Water.hash(data);
    }

    public static int hash(final double[] data) {
        return Water.hash(data);
    }

    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the char array to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 32-bit hash code for the requested section of data
     */
    public static int hash(final char[] data, final int start, final int end) {
        return Water.hash(data, start, end);
    }

    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the String or other CharSequence to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 32-bit hash code for the requested section of data
     */
    public static int hash(final CharSequence data, final int start, final int end) {
        return Water.hash(data, start, end);
    }


    public static int hash(final char[][] data) {
        return Water.hash(data);
    }

    public static int hash(final int[][] data) {
        return Water.hash(data);
    }

    public static int hash(final long[][] data) {
        return Water.hash(data);
    }

    public static int hash(final CharSequence[] data) {
        return Water.hash(data);
    }

    public static int hash(final CharSequence[]... data) {
        return Water.hash(data);
    }

    public static int hash(final Iterable<? extends CharSequence> data) {
        return Water.hash(data);
    }

    public static int hash(final List<? extends CharSequence> data) {
        return Water.hash(data);
    }

    public static int hash(final Object[] data) {
        return Water.hash(data);
    }

    public static int hash(final Object data) {
        return Water.hash(data);
    }

    /**
     * An interface that can be used to move the logic for the hashCode() and equals() methods from a class' methods to
     * an implementation of IHasher that certain collections in SquidLib can use. Primarily useful when the key type is
     * an array, which normally doesn't work as expected in Java hash-based collections, but can if the right collection
     * and IHasher are used. See also {@link Hashers} for additional implementations, some of which need dependencies on
     * things the rest of CrossHash doesn't, like a case-insensitive String hasher/equator that uses RegExodus to handle
     * CharSequence comparison on GWT.
     */
    public interface IHasher extends Serializable {
        /**
         * If data is a type that this IHasher can specifically hash, this method should use that specific hash; in
         * other situations, it should simply delegate to calling {@link Object#hashCode()} on data. The body of an
         * implementation of this method can be very small; for an IHasher that is meant for byte arrays, the body could
         * be: {@code return (data instanceof byte[]) ? CrossHash.Lightning.hash((byte[]) data) : data.hashCode();}
         *
         * @param data the Object to hash; this method should take any type but often has special behavior for one type
         * @return a 32-bit int hash code of data
         */
        int hash(final Object data);

        /**
         * Not all types you might want to use an IHasher on meaningfully implement .equals(), such as array types; in
         * these situations the areEqual method helps quickly check for equality by potentially having special logic for
         * the type this is meant to check. The body of implementations for this method can be fairly small; for byte
         * arrays, it looks like: {@code return left == right
         * || ((left instanceof byte[] && right instanceof byte[])
         * ? Arrays.equals((byte[]) left, (byte[]) right)
         * : Objects.equals(left, right));} , but for multidimensional arrays you should use the
         * {@link #equalityHelper(Object[], Object[], IHasher)} method with an IHasher for the inner arrays that are 1D
         * or otherwise already-hash-able, as can be seen in the body of the implementation for 2D char arrays, where
         * charHasher is an existing IHasher that handles 1D arrays:
         * {@code return left == right
         * || ((left instanceof char[][] && right instanceof char[][])
         * ? equalityHelper((char[][]) left, (char[][]) right, charHasher)
         * : Objects.equals(left, right));}
         *
         * @param left  allowed to be null; most implementations will have special behavior for one type
         * @param right allowed to be null; most implementations will have special behavior for one type
         * @return true if left is equal to right (preferably by value, but reference equality may sometimes be needed)
         */
        boolean areEqual(final Object left, final Object right);
    }

    /**
     * Not a general-purpose method; meant to ease implementation of {@link IHasher#areEqual(Object, Object)}
     * methods when the type being compared is a multi-dimensional array (which normally requires the heavyweight method
     * {@link Arrays#deepEquals(Object[], Object[])} or doing more work yourself; this reduces the work needed to
     * implement fixed-depth equality). As mentioned in the docs for {@link IHasher#areEqual(Object, Object)}, example
     * code that hashes 2D char arrays can be done using an IHasher for 1D char arrays called charHasher:
     * {@code return left == right
     * || ((left instanceof char[][] && right instanceof char[][])
     * ? equalityHelper((char[][]) left, (char[][]) right, charHasher)
     * : Objects.equals(left, right));}
     *
     * @param left an array of some kind of Object, usually an array, that the given IHasher can compare
     * @param right an array of some kind of Object, usually an array, that the given IHasher can compare
     * @param inner an IHasher to compare items in left with items in right
     * @return true if the contents of left and right are equal by the given IHasher, otherwise false
     */
    public static boolean equalityHelper(Object[] left, Object[] right, IHasher inner) {
        if (left == right)
            return true;
        if (left == null || right == null || left.length != right.length)
            return false;
        for (int i = 0; i < left.length; i++) {
            if (!inner.areEqual(left[i], right[i]))
                return false;
        }
        return true;
    }

    private static class BooleanHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        BooleanHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof boolean[]) ? CrossHash.hash((boolean[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof boolean[] && right instanceof boolean[]) ? Arrays.equals((boolean[]) left, (boolean[]) right) : Objects.equals(left, right));
        }
    }

    public static final IHasher booleanHasher = new BooleanHasher();

    private static class ByteHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        ByteHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof byte[]) ? CrossHash.hash((byte[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right
                    || ((left instanceof byte[] && right instanceof byte[])
                    ? Arrays.equals((byte[]) left, (byte[]) right)
                    : Objects.equals(left, right));
        }
    }

    public static final IHasher byteHasher = new ByteHasher();

    private static class ShortHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        ShortHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof short[]) ? CrossHash.hash((short[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof short[] && right instanceof short[]) ? Arrays.equals((short[]) left, (short[]) right) : Objects.equals(left, right));
        }
    }

    public static final IHasher shortHasher = new ShortHasher();

    private static class CharHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        CharHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof char[]) ? CrossHash.hash((char[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof char[] && right instanceof char[]) ? Arrays.equals((char[]) left, (char[]) right) : Objects.equals(left, right));
        }
    }

    public static final IHasher charHasher = new CharHasher();

    private static class IntHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        IntHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof int[]) ? CrossHash.hash((int[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return (left instanceof int[] && right instanceof int[]) ? Arrays.equals((int[]) left, (int[]) right) : Objects.equals(left, right);
        }
    }

    public static final IHasher intHasher = new IntHasher();

    private static class LongHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        LongHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof long[]) ? CrossHash.hash((long[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return (left instanceof long[] && right instanceof long[]) ? Arrays.equals((long[]) left, (long[]) right) : Objects.equals(left, right);
        }
    }

    public static final IHasher longHasher = new LongHasher();

    private static class FloatHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        FloatHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof float[]) ? CrossHash.hash((float[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof float[] && right instanceof float[]) ? Arrays.equals((float[]) left, (float[]) right) : Objects.equals(left, right));
        }
    }

    public static final IHasher floatHasher = new FloatHasher();

    private static class DoubleHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        DoubleHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof double[]) ? CrossHash.hash((double[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof double[] && right instanceof double[]) ? Arrays.equals((double[]) left, (double[]) right) : Objects.equals(left, right));
        }
    }

    public static final IHasher doubleHasher = new DoubleHasher();

    private static class Char2DHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        Char2DHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof char[][]) ? CrossHash.hash((char[][]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right
                    || ((left instanceof char[][] && right instanceof char[][])
                    ? equalityHelper((char[][]) left, (char[][]) right, charHasher)
                    : Objects.equals(left, right));
        }
    }

    public static final IHasher char2DHasher = new Char2DHasher();

    private static class Int2DHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        Int2DHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof int[][]) ? CrossHash.hash((int[][]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right
                    || ((left instanceof int[][] && right instanceof int[][])
                    ? equalityHelper((int[][]) left, (int[][]) right, intHasher)
                    : Objects.equals(left, right));
        }
    }

    public static final IHasher int2DHasher = new Int2DHasher();

    private static class Long2DHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        Long2DHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof long[][]) ? CrossHash.hash((long[][]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right
                    || ((left instanceof long[][] && right instanceof long[][])
                    ? equalityHelper((long[][]) left, (long[][]) right, longHasher)
                    : Objects.equals(left, right));
        }
    }

    public static final IHasher long2DHasher = new Long2DHasher();

    private static class StringHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        StringHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof CharSequence) ? CrossHash.hash((CharSequence) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return Objects.equals(left, right);
        }
    }

    public static final IHasher stringHasher = new StringHasher();

    private static class StringArrayHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        StringArrayHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof CharSequence[]) ? CrossHash.hash((CharSequence[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof CharSequence[] && right instanceof CharSequence[]) ? equalityHelper((CharSequence[]) left, (CharSequence[]) right, stringHasher) : Objects.equals(left, right));
        }
    }

    /**
     * Though the name suggests this only hashes String arrays, it can actually hash any CharSequence array as well.
     */
    public static final IHasher stringArrayHasher = new StringArrayHasher();

    private static class ObjectArrayHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        ObjectArrayHasher() {
        }

        @Override
        public int hash(final Object data) {
            return (data instanceof Object[]) ? CrossHash.hash((Object[]) data) : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right || ((left instanceof Object[] && right instanceof Object[]) && Arrays.equals((Object[]) left, (Object[]) right) || Objects.equals(left, right));
        }
    }
    public static final IHasher objectArrayHasher = new ObjectArrayHasher();

    private static class DefaultHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 5L;

        DefaultHasher() {
        }

        @Override
        public int hash(final Object data) {
            if(data == null) return 0;
            final int x = data.hashCode() * 0x9E375;
            return x ^ x >>> 16;
        }

        @Override
        public boolean areEqual(final Object left, final Object right) {
            return (left == right) || (left != null && left.equals(right));
        }
    }

    public static final IHasher defaultHasher = new DefaultHasher();

    private static class MildHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 4L;

        MildHasher() {
        }

        @Override
        public int hash(final Object data) {
            return data != null ? data.hashCode() : 0;
        }

        @Override
        public boolean areEqual(final Object left, final Object right) {
            return (left == right) || (left != null && left.equals(right));
        }
    }

    /**
     * The most basic IHasher type; effectively delegates to {@link Objects#hashCode(Object)} and
     * {@link Objects#equals(Object, Object)}. Might not scramble the bits of a hash well enough to have good
     * performance in a hash table lke {@link OrderedMap} or {@link UnorderedSet}, unless the objects being hashed have
     * good hashCode() implementations already.
     */
    public static final IHasher mildHasher = new MildHasher();

    private static class IdentityHasher implements IHasher, Serializable
    {
        private static final long serialVersionUID = 4L;
        IdentityHasher() { }

        @Override
        public int hash(Object data) {
            return System.identityHashCode(data);
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return left == right;
        }
    }
    public static final IHasher identityHasher = new IdentityHasher();

    private static class GeneralHasher implements IHasher, Serializable {
        private static final long serialVersionUID = 3L;

        GeneralHasher() {
        }

        @Override
        public int hash(final Object data) {
            return CrossHash.hash(data);
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            if(left == right) return true;
            Class<?> l = left.getClass(), r = right.getClass();
            if(l == r)
            {
                if(l.isArray())
                {
                    if(left instanceof int[]) return Arrays.equals((int[]) left, (int[]) right);
                    else if(left instanceof long[]) return Arrays.equals((long[]) left, (long[]) right);
                    else if(left instanceof char[]) return Arrays.equals((char[]) left, (char[]) right);
                    else if(left instanceof double[]) return Arrays.equals((double[]) left, (double[]) right);
                    else if(left instanceof boolean[]) return Arrays.equals((boolean[]) left, (boolean[]) right);
                    else if(left instanceof byte[]) return Arrays.equals((byte[]) left, (byte[]) right);
                    else if(left instanceof float[]) return Arrays.equals((float[]) left, (float[]) right);
                    else if(left instanceof short[]) return Arrays.equals((short[]) left, (short[]) right);
                    else if(left instanceof char[][]) return equalityHelper((char[][]) left, (char[][]) right, charHasher);
                    else if(left instanceof int[][]) return equalityHelper((int[][]) left, (int[][]) right, intHasher);
                    else if(left instanceof long[][]) return equalityHelper((long[][]) left, (long[][]) right, longHasher);
                    else if(left instanceof CharSequence[]) return equalityHelper((CharSequence[]) left, (CharSequence[]) right, stringHasher);
                    else if(left instanceof Object[]) return Arrays.equals((Object[]) left, (Object[]) right);
                }
                return Objects.equals(left, right);
            }
            return false;
        }
    }

    /**
     * This IHasher is the one you should use if you aren't totally certain what types will go in an OrderedMap's keys
     * or an OrderedSet's items, since it can handle mixes of elements.
     */
    public static final IHasher generalHasher = new GeneralHasher();

    /**
     * A quick, simple hashing function that seems to have good results. Like LightRNG, it stores a state that
     * it updates independently of the output, and this starts at a large prime. At each step, it takes the
     * current item in the array being hashed, adds a large non-prime used in LightRNG's generation function
     * (it's 2 to the 64, times the golden ratio phi, and truncated to a signed long), multiplies by a prime
     * called the "state multiplier", adds the result to the state and stores it, multiplies the value of the
     * state by another prime called the "output multiplier", then XORs the current result with that value
     * before moving onto the next item in the array. A finalization step XORs the result with a complex value
     * made by adding the state (left over from the previous step) to what was the output multiplier, adding
     * the last known value for result to the phi-related constant from LightRNG, multiplying that pair, adding
     * the initial state (which turns out to be unusually good for this, despite no particularly special numeric
     * qualities other than being a probable prime) and then bitwise-rotating it left by a seemingly-random
     * number drawn from the highest 6 bits of the state.
     * <br>
     * This all can be done very quickly; a million hashes of a million different 16-element long arrays can be
     * computed in under 18-20 ms (in the benchmark, some amount of that is overhead from generating a new
     * array with LongPeriodRNG, since the benchmark uses that RNG's state for data, and the default
     * Arrays.hashCode implementation is only somewhat faster at under 16 ms). After several tries and tweaks
     * to the constants this uses, it also gets remarkably few hash collisions. On the same 0x100000, or
     * 1048576, RNG states for data, Lightning gets 110 collisions, the JDK Arrays.hashCode method gets 129
     * collisions, Sip (implementing SipHash) gets 145 collisions, and CrossHash (using the FNV-1a algorithm)
     * gets 135 collisions. Dispersion is not perfect, but
     * at many bit sizes Lightning continues to have less collisions (it disperses better than the other hashes
     * with several quantities of bits, at least on this test data). Lightning also does relatively well, though
     * it isn't clearly ahead of the rest all the time, when hashing Strings, especially ones that use a larger
     * set of letters, it seems (FakeLanguageGen was used to make test data, and languages that used more
     * characters in their alphabet seemed to hash better with this than competing hashes for some reason).
     * <br>
     * There is certainly room for improvement with the specific numbers chosen; earlier versions used the
     * state multiplier "Neely's number", which is a probable prime made by taking the radix-29 number
     * "HARGNALLINSCLOPIOPEEPIO" (a reference to a very unusual TV show), truncating to 64 bits, and rotating
     * right by 42 bits. This version uses "Neely's number" for an initial state and during finalization, and
     * uses a different probable prime as the state multiplier, made with a similar process; it starts with the
     * radix-36 number "EDSGERWDIJKSTRA", then does the same process but rotates right by 37 bits to obtain a
     * different prime. This tweak seems to help with hash collisions. Extensive trial and error was used to
     * find the current output multiplier, which has no real relationship to anything else but has exactly 32 of
     * 64 bits set to 1, has 1 in the least and most significant bit indices (meaning it is negative and odd),
     * and other than that seems to have better results on most inputs for mystifying reasons. Earlier versions
     * applied a Gray code step to alter the output instead of a multiplier that heavily overflows to obfuscate
     * state, but that had a predictable pattern for most of the inputs tried, which seemed less-than-ideal for
     * a hash. Vitally, Lightning avoids predictable collisions that Arrays.hashCode has, like
     * {@code Arrays.hashCode(new long[]{0})==Arrays.hashCode(new long[]{-1})}.
     * <br>
     * The output multiplier is 0xC6BC279692B5CC83L, the state multiplier is 0xD0E89D2D311E289FL, the number
     * added to the state (from LightRNG and code derived from FastUtil, but obtained from the golden ratio
     * phi) is 0x9E3779B97F4A7C15L, and the starting state ("Neely's Number") is 0x632BE59BD9B4E019L.
     * <br>
     * To help find patterns in hash output in a visual way, you can hash an x,y point, take the bottom 24 bits,
     * and use that as an RGB color for the pixel at that x,y point. On a 512x512 grid of points, the patterns
     * in Arrays.hashCode and the default CrossHash algorithm (FNV-1a) are evident, and Sip (implementing
     * SipHash) does approximately as well as Lightning, with no clear patterns visible (Sip has been removed
     * from SquidLib because it needs a lot of code and is slower than Mist). The
     * idea is from a technical report on visual uses for hashing,
     * http://www.clockandflame.com/media/Goulburn06.pdf .
     * <ul>
     * <li>{@link java.util.Arrays#hashCode(int[])}: http://i.imgur.com/S4Gh1sX.png</li>
     * <li>{@link CrossHash#hash(int[])}: http://i.imgur.com/x8SDqvL.png</li>
     * <li>(Former) CrossHash.Sip.hash(int[]): http://i.imgur.com/keSpIwm.png</li>
     * <li>{@link CrossHash.Lightning#hash(int[])}: http://i.imgur.com/afGJ9cA.png</li>
     * </ul>
     */
    // tested output multipliers
    // 0x DA1A459BD9B4C619L
    // 0x DC1A459879B5C619L
    // 0x DC1A479829B5E639L
    // 0x DC1A479C29B5C639L
    // 0x EA1C479692B5C639L
    // 0x CA1C479692B5C635L // this gets 105 collisions, low
    // 0x CABC479692B5C635L
    // 0x DC1A479C29B5C647L
    // 0x DC1A479C29B5C725L
    // 0x CABC279692B5CB21L
    // 0x C6BC279692B5CC83L // this gets 100 collisions, lowest
    // 0x C6BC279692B4D8A5L
    // 0x C6BC279692B4D345L
    // 0x C6EC273692B4A4B9L
    // 0x C6A3256B52D5B463L
    // 0x C6A3256B52D5B463L
    // 0x C6A3256D52D5B4C9L
    // 0x D8A3256D52D5B619L
    // 0x D96E6AC724658947L
    // 0x D96E6AC724658C2DL
    // 0x CCABF9E32FD684F9L
    // 0x C314163FAF912A01L
    // 0x C3246007A332C12AL
    // 0x CA1C479692B5C6ABL
    // 0x C6B5275692B5CC83 // untested so far
    public static final class Lightning {

        public static long hash64(final boolean[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] ? 0x9E3779B97F4A7C94L : 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final byte[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final short[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final char[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final int[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final long[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final float[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (floatToIntBits(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final double[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (NumberTools.doubleToMixedIntBits(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = start; i < end && i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }
        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = start; i < end && i < data.length; i += step) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final CharSequence data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length(); i++) {
                result ^= (z += (data.charAt(i) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final char[][] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final long[][] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final CharSequence[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (CharSequence datum : data) {
                result ^= (z += (hash64(datum) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final CharSequence[]... data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static long hash64(final Object[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            Object o;
            for (int i = 0; i < data.length; i++) {
                o = data[i];
                result ^= (z += ((o == null ? 0 : o.hashCode()) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return result ^ Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58));
        }

        public static int hash(final boolean[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] ? 0x9E3779B97F4A7C94L : 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final byte[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final short[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final char[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final int[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;

            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final long[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final float[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (floatToIntBits(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final double[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (NumberTools.doubleToMixedIntBits(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;

            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = start; i < end && i < data.length; i++) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }
        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;

            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = start; i < end && i < data.length; i += step) {
                result ^= (z += (data[i] + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final CharSequence data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length(); i++) {
                result ^= (z += (data.charAt(i) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final char[][] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final long[][] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final CharSequence[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (CharSequence datum : data) {
                result ^= (z += (hash64(datum) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final CharSequence[]... data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            for (int i = 0; i < data.length; i++) {
                result ^= (z += (hash64(data[i]) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }

        public static int hash(final Object[] data) {
            if (data == null)
                return 0;
            long z = 0x632BE59BD9B4E019L, result = 1L;
            Object o;
            for (int i = 0; i < data.length; i++) {
                o = data[i];
                result ^= (z += ((o == null ? 0 : o.hashCode()) + 0x9E3779B97F4A7C15L) * 0xD0E89D2D311E289FL) * 0xC6BC279692B5CC83L;
            }
            return (int) ((result ^= Long.rotateLeft((z * 0xC6BC279692B5CC83L ^ result * 0x9E3779B97F4A7C15L) + 0x632BE59BD9B4E019L, (int) (z >>> 58))) ^ (result >>> 32));
        }
    }

    // Nice ints, all probable primes except the last one, for 32-bit hashing
    // 0x62E2AC0D 0x632BE5AB 0x85157AF5 0x9E3779B9
    /**
     * The fastest hash in CrossHash, with middling quality. Uses a finely-tuned mix of very few operations for each
     * element, plus a minimal and simple finalization step, and as such obtains superior speed on the standard
     * benchmark SquidLib uses for hashes (hashing one million 16-element long arrays, remaining the best in both 32-bit
     * and 64-bit versions). Specifically, Wisp takes 9.478 ms to generate a million 64-bit hashes on a recent laptop
     * with an i7-6700HQ processor (removing the time the control takes to generate the million arrays). For comparison,
     * the JDK's Arrays.hashCode method takes 13.642 ms on the same workload, though it produces 32-bit hashes. Wisp
     * performs almost exactly as well producing 32-bit hashes as it does 64-bit hashes, where Hive slows down
     * significantly on some input types. This also passes visual tests where an earlier version of Wisp did not.
     * Collision rates are slightly worse than other CrossHash classes, but are better than the JDK's
     * Arrays.hashCode method, that is, acceptably low when given varied-enough inputs. On certain kinds of similar
     * inputs, Wisp will struggle with a higher collision rate. For example, when hashing Strings that contain only
     * several spaces, then some combination of digits 0-5, then more spaces, Wisp does very badly, worse than
     * {@link String#hashCode()} (which also does badly, though not as badly), while other hashes here do fine (such as
     * Water, which is the default for {@link CrossHash#hash(CharSequence)}).
     * <br>
     * This version replaces an older version of Wisp that had serious quality issues and wasn't quite as fast. Since
     * the only reason one would use the older version was speed without regard for quality, and it was marked as Beta,
     * a faster version makes sense to replace the slower one, rather than add yet another nested class in CrossHash.
     * <br>
     * Wisp is no longer considered Beta-quality, but even though it is rather fast, it has some cases where categories
     * of input cause frequent collisions. {@link Water} is about 20% slower but doesn't have such categories of
     * pathologically bad inputs, and passes tests that Wisp fails badly on. Because the hash-based collections in
     * SquidLib need a pretty good hash function to work at their best (they use linear-probing with open addressing,
     * which struggles when hashes are bad), 20% loss of speed during hashing to avoid slower
     * lookups/insertions/deletions from {@link OrderedMap}, {@link OrderedSet}, {@link UnorderedMap},
     * {@link UnorderedSet}, {@link Arrangement}, and others is probably worth it; if you really need speed then you
     * should first consider {@link Curlup}, which is faster than Wisp on moderately-long input arrays (with 20 or more
     * items, usually), and only if you need a fast hash for small inputs, where collisions aren't a problem, should you
     * turn to Wisp.
     */
    public static final class Wisp {
        public static long hash64(final boolean[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * (data[i] ? 0xC6BC279692B5CC83L : 0xAEF17502108EF2D9L));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final byte[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final short[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final char[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final int[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final long[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final float[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * floatToIntBits(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final double[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToMixedIntBits(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final CharSequence data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length();
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data.charAt(i));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i += step) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final char[][] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final int[][] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final long[][] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final CharSequence[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final CharSequence[]... data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            for (CharSequence datum : data) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(datum));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final List<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.size();
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data.get(i)));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final Object[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            Object o;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * ((o = data[i]) == null ? -1L : o.hashCode()));
            }
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static long hash64(final Object data) {
            if (data == null)
                return 0L;
            long a = 0x632BE59BD9B4E019L ^ 0x8329C6EB9E6AD3E3L * data.hashCode(), result = 0x9E3779B97F4A7C94L + a;
            return result * (a | 1L) ^ (result << 37 | result >>> 27);
        }

        public static int hash32(final boolean[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * (data[i] ? 0x789ABCDE : 0x62E2AC0D));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }


        public static int hash32(final byte[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * data[i]);
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final short[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * data[i]);
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final char[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * data[i]);
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }
        public static int hash32(final int[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * data[i]);
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final long[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)((a += (result << 37 | result >>> 27)) ^ (a >>> 32));
        }

        public static int hash32(final float[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * floatToIntBits(data[i]));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final double[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            double t;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * ((long) (-0xD0E8.9D2D311E289Fp-25 * (t = data[i]) + t * -0x1.39b4dce80194cp9)));
            }
            return (int)((result = (result * (a | 1L) ^ (result << 37 | result >>> 27))) ^ (result >>> 32));
        }

        public static int hash32(final CharSequence data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length();
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * data.charAt(i));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         * Uses 32-bit math on most platforms, but will give different results on GWT due to it using double values that
         * only somewhat act like int values.
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash32(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i++) {
                result += (a ^= 0x85157AF5 * data[i]);
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final char[][] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * hash32(data[i]));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final int[][] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * hash32(data[i]));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final long[][] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * hash32(data[i]));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final CharSequence[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * hash32(data[i]));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final CharSequence[]... data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * hash32(data[i]));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            for (CharSequence datum : data) {
                result += (a ^= 0x85157AF5 * hash32(datum));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final List<? extends CharSequence> data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.size();
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * hash32(data.get(i)));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final Object[] data) {
            if (data == null)
                return 0;
            int result = 0x9E3779B9, a = 0x632BE5AB;
            final int len = data.length;
            Object o;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x85157AF5 * ((o = data[i]) == null ? -1 : o.hashCode()));
            }
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }

        public static int hash32(final Object data) {
            if (data == null)
                return 0;
            int a = 0x632BE5AB ^ 0x85157AF5 * data.hashCode(), result = 0x9E3779B9 + a;
            return result * (a | 1) ^ (result >>> 11 | result << 21);
        }
        public static int hash(final boolean[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * (data[i] ? 0xC6BC279692B5CC83L : 0xAEF17502108EF2D9L));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final byte[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final short[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final char[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final int[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final long[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final float[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * floatToIntBits(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final double[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToMixedIntBits(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final CharSequence data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length();
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data.charAt(i));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }
        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i+= step) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]);
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final char[][] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final int[][] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final long[][] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final CharSequence[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final CharSequence[]... data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i]));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            for (CharSequence datum : data) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(datum));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final List<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.size();
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data.get(i)));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final Object[] data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
            final int len = data.length;
            Object o;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * ((o = data[i]) == null ? -1 : o.hashCode()));
            }
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

        public static int hash(final Object data) {
            if (data == null)
                return 0;
            long a = 0x632BE59BD9B4E019L ^ 0x8329C6EB9E6AD3E3L * data.hashCode(), result = 0x9E3779B97F4A7C94L + a;
            return (int)(result * (a | 1L) ^ (result << 37 | result >>> 27));
        }

    }

    /**
     * A whole cluster of Wisp-like hash functions that sacrifice a small degree of speed, but can be built with up
     * to 128 bits of salt values that help to obscure what hashing function is actually being used. This class is
     * similar to the older Storm variety, but is somewhat faster and has many more possible salt "states" when using
     * the constructors that take two longs or a CharSequence. There isn't really any reason to use Storm, so Mist has
     * now replaced Storm entirely. Code that used Storm should be able to just change any usage of "Storm" to "Mist",
     * or can instead use {@link Yolk} or {@link Curlup} for higher quality and speed but smaller salt size.
     * <br>
     * The salt fields are not serialized, so it is important that the same salt will be given by the
     * program when the same hash results are wanted for some inputs.
     * <br>
     * A group of 48 static, final, pre-initialized Mist members are present in this class, 24 with the
     * name of a letter in the Greek alphabet (this uses the convention on Wikipedia,
     * https://en.wikipedia.org/wiki/Greek_alphabet#Letters , where lambda is spelled with a 'b') and 24 with the same
     * name followed by an underscore, such as {@link #alpha_}. The whole group of 48 pre-initialized members are also
     * present in a static array called {@code predefined}. These can be useful when, for example, you want to get
     * multiple hashes of a single array or String as part of cuckoo hashing or similar techniques that need multiple
     * hashes for the same inputs.
     */
    public static final class Mist implements Serializable {
        private static final long serialVersionUID = -1275284837479983271L;

        private transient final long l1, l2;

        public Mist() {
            this(0x1234567876543210L, 0xEDCBA98789ABCDEFL);
        }

        public Mist(final CharSequence alteration) {
            this(CrossHash.hash64(alteration), Lightning.hash64(alteration));
        }
        private static int permute(final long state)
        {
            int s = (int)state ^ 0xD0E89D2D;
            s = (s >>> 19 | s << 13);
            s ^= state >>> (5 + (state >>> 59));
            return ((s *= 277803737) >>> 22) ^ s;
        }

        @SuppressWarnings("NumericOverflow")
        public Mist(final long alteration) {
            long l1, l2;
            l1 = alteration + permute(alteration);
            l1 = (l1 ^ (l1 >>> 30)) * 0xBF58476D1CE4E5B9L;
            l1 = (l1 ^ (l1 >>> 27)) * 0x94D049BB133111EBL;
            this.l1 = l1 ^ l1 >>> 31;

            l2 = alteration + 6 * 0x9E3779B97F4A7C15L;
            l2 = (l2 ^ (l2 >>> 30)) * 0xBF58476D1CE4E5B9L;
            l2 = (l2 ^ (l2 >>> 27)) * 0x94D049BB133111EBL;
            this.l2 = l2 ^ l2 >>> 31;
        }

        public Mist(final long alteration1, long alteration2) {
            final int i1 = permute(alteration1);
            l1 = alteration1 + i1;
            l2 = alteration2 + permute(alteration2 + i1);
        }

        /**
         * Makes a new Mist with all of the salt values altered based on the previous salt values.
         * This will make a different, incompatible Mist object that will give different results than the original.
         * Meant for use in Cuckoo Hashing, which can need the hash function to be updated or changed.
         * An alternative is to select a different Mist object from {@link #predefined}, or to simply
         * construct a new Mist with a different parameter or set of parameters.
         */
        @SuppressWarnings("NumericOverflow")
        public Mist randomize()
        {
            long l1, l2;
            l1 = this.l2 + permute(this.l2 + 3 * 0x9E3779B97F4A7C15L);
            l1 = (l1 ^ (l1 >>> 30)) * 0xBF58476D1CE4E5B9L;
            l1 = (l1 ^ (l1 >>> 27)) * 0x94D049BB133111EBL;
            l1 ^= l1 >>> 31;

            l2 = permute(l1 + 5 * 0x9E3779B97F4A7C15L) + 6 * 0x9E3779B97F4A7C15L;
            l2 = (l2 ^ (l2 >>> 30)) * 0xBF58476D1CE4E5B9L;
            l2 = (l2 ^ (l2 >>> 27)) * 0x94D049BB133111EBL;
            l2 ^= l2 >>> 31;

            return new Mist(l1, l2);
        }

        public static final Mist alpha = new Mist("alpha"), beta = new Mist("beta"), gamma = new Mist("gamma"),
                delta = new Mist("delta"), epsilon = new Mist("epsilon"), zeta = new Mist("zeta"),
                eta = new Mist("eta"), theta = new Mist("theta"), iota = new Mist("iota"),
                kappa = new Mist("kappa"), lambda = new Mist("lambda"), mu = new Mist("mu"),
                nu = new Mist("nu"), xi = new Mist("xi"), omicron = new Mist("omicron"), pi = new Mist("pi"),
                rho = new Mist("rho"), sigma = new Mist("sigma"), tau = new Mist("tau"),
                upsilon = new Mist("upsilon"), phi = new Mist("phi"), chi = new Mist("chi"), psi = new Mist("psi"),
                omega = new Mist("omega"),
                alpha_ = new Mist("ALPHA"), beta_ = new Mist("BETA"), gamma_ = new Mist("GAMMA"),
                delta_ = new Mist("DELTA"), epsilon_ = new Mist("EPSILON"), zeta_ = new Mist("ZETA"),
                eta_ = new Mist("ETA"), theta_ = new Mist("THETA"), iota_ = new Mist("IOTA"),
                kappa_ = new Mist("KAPPA"), lambda_ = new Mist("LAMBDA"), mu_ = new Mist("MU"),
                nu_ = new Mist("NU"), xi_ = new Mist("XI"), omicron_ = new Mist("OMICRON"), pi_ = new Mist("PI"),
                rho_ = new Mist("RHO"), sigma_ = new Mist("SIGMA"), tau_ = new Mist("TAU"),
                upsilon_ = new Mist("UPSILON"), phi_ = new Mist("PHI"), chi_ = new Mist("CHI"), psi_ = new Mist("PSI"),
                omega_ = new Mist("OMEGA");
        /**
         * Has a length of 48, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Mist[] predefined = new Mist[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
                kappa, lambda, mu, nu, xi, omicron, pi, rho, sigma, tau, upsilon, phi, chi, psi, omega,
                alpha_, beta_, gamma_, delta_, epsilon_, zeta_, eta_, theta_, iota_,
                kappa_, lambda_, mu_, nu_, xi_, omicron_, pi_, rho_, sigma_, tau_, upsilon_, phi_, chi_, psi_, omega_};

        public long hash64(final boolean[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * (data[i] ? 0x9E3779B97F4A7C15L : 0x789ABCDEFEDCBA98L)) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }


        public long hash64(final byte[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final short[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final char[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final int[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final long[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }


        public long hash64(final float[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * floatToIntBits(data[i])) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final double[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToMixedIntBits(data[i])) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = start; i < end && i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = start; i < end && i < len; i += step) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final CharSequence data) {
            if (data == null)
                return 0;
            final int len = data.length();
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data.charAt(i)) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final char[][] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final long[][] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final CharSequence[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (CharSequence datum : data) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(datum)) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final CharSequence[]... data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final Object[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            Object o;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * ((o = data[i]) == null ? -1 : o.hashCode())) ^ l2 * a + l1;
            }
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long a = 0x632BE59BD9B4E019L ^ 0x8329C6EB9E6AD3E3L * data.hashCode(),
                    result = 0x9E3779B97F4A7C94L + l2 + (a ^ l2 * a + l1);
            return result * (a * l1 | 1L) ^ (result << 37 | result >>> 27);
        }
        public int hash(final boolean[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * (data[i] ? 0x9E3779B97F4A7C15L : 0x789ABCDEFEDCBA98L)) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }


        public int hash(final byte[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final short[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final char[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final int[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final long[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }


        public int hash(final float[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * floatToIntBits(data[i])) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final double[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToMixedIntBits(data[i])) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = start; i < end && i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }
        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public int hash(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = start; i < end && i < len; i += step) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data[i]) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final CharSequence data) {
            if (data == null)
                return 0;
            final int len = data.length();
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * data.charAt(i)) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final char[][] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final long[][] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final CharSequence[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (CharSequence datum : data) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(datum)) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final CharSequence[]... data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(data[i])) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final Object[] data) {
            if (data == null)
                return 0;
            final int len = data.length;
            long result = 0x9E3779B97F4A7C94L + l2, a = 0x632BE59BD9B4E019L;
            Object o;
            for (int i = 0; i < len; i++) {
                result += (a ^= 0x8329C6EB9E6AD3E3L * ((o = data[i]) == null ? -1 : o.hashCode())) ^ l2 * a + l1;
            }
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }

        public int hash(final Object data) {
            if (data == null)
                return 0;
            final long a = 0x632BE59BD9B4E019L ^ 0x8329C6EB9E6AD3E3L * data.hashCode(),
                    result = 0x9E3779B97F4A7C94L + l2 + (a ^ l2 * a + l1);
            return (int)(result * (a * l1 | 1L) ^ (result << 37 | result >>> 27));
        }
    }

    /**
     * A reasonably-fast hashing function that passes some of SMHasher's quality tests, but neither critically fails nor
     * overwhelmingly succeeds the full SMHasher test battery. This was the default used by methods in the
     * outer class like {@link CrossHash#hash(int[])}, but it has been replaced there by {@link Water}; you should
     * prefer using the outer class since this inner class is only here for reference. The one advantage Hive has over
     * Water is potentially better optimization for GWT, but since the main usage of CrossHash is for 64-bit hashes, GWT
     * will be slow regardless for that usage.
     * <br>
     * This mixes three different algorithms: the main one is used whenever inputs or outputs are 64-bit (so, all
     * hash64() overloads and {@link Hive#hash(long[])} for long and double), a modification of the main one to perform
     * better on GWT (only used on hash() overloads, and only when inputs are individually 32-bit or less), and
     * a simpler algorithm (which was called Jolt) for hash64() on boolean and byte data.
     * <br>
     * Speed-wise, the main algorithm is about 20% slower than Wisp, but in hash tables it doesn't have clear failure
     * cases like Wisp does on some inputs (such as fixed-length Strings with identical prefixes). If collisions are
     * expensive or profiling shows that Wisp's algorithm is colliding at a high rate, you should probably use the
     * normal IHasher and CrossHash.hash() methods, since those will use Hive. The modified algorithm for GWT is a
     * little slower than the main algorithm in the C++ implementation that was used to check SMHasher quality, but it
     * may perform similarly to the main algorithm in Java on desktop platforms. Since it avoids creating longs or doing
     * any math on them, it should be at least 3x faster than the main algorithm on GWT (a GWT long is internally
     * represented by 3 JS numbers, so barring special optimizations it takes at least 3x as many math operations to use
     * longs there).
     * <br>
     * Its design uses two states like {@link Lightning} or {@link Wisp}, updating them differently from each other, and
     * bitwise-rotates one at each step. It combines the states (xorshifting one state, multiplying it by a huge
     * constant, and adding that to the other state) and then runs that through MurmurHash3's finalization function (its
     * {@code fmix64()} function; the main algorithm elides one xorshift at the end that proved unnecessary). Parts of
     * the code here are inspired by the design of {@link DiverRNG}, particularly its determine() method since both
     * use an XLCG (XOR Linear Congruential Generator, as PractRand calls it) as a processing step.
     * <br>
     * The name comes from the song I was listening to when I finally got the tests to pass ("Slave The Hive" by High On
     * Fire) and from the wide assortment of code that I had to employ to achieve a SMHasher successful run (which
     * turned out to be not-so-successful).
     */
    public static final class Hive {
        public static long hash64(final CharSequence data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length();
            for (int i = 0; i < len; i++) {
                result ^= (z += (data.charAt(i) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final boolean[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ? 0xFF51AFD7ED558CCDL : 0xC4CEB9FE1A85EC53L));
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result ^= result >>> 25 ^ z ^ z >>> 29;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            result = (result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L;
            return (result ^ result >>> 33);
        }

        public static long hash64(final byte[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0x9E3779B97F4A7C15L) * 0xC6BC279692B5CC83L);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result ^= result >>> 25 ^ z ^ z >>> 29;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            result = (result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L;
            return (result ^ result >>> 33);
        }

        public static long hash64(final short[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final int[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final long[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final char[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final float[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (floatToIntBits(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final double[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (NumberTools.doubleToLongBits(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static long hash64(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = Math.min(end, data.length());
            for (int i = start; i < len; i++) {
                result ^= (z += (data.charAt(i) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i += step) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public static long hash64(final CharSequence data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = Math.min(end, data.length());
            for (int i = start; i < len; i += step) {
                result ^= (z += (data.charAt(i) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final char[][] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final int[][] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final long[][] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final CharSequence[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final CharSequence[]... data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            for (CharSequence datum : data) {
                result ^= (z += (hash64(datum) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);

        }

        public static long hash64(final List<? extends CharSequence> data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.size();
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data.get(i)) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);

        }

        public static long hash64(final Object[] data) {
            if (data == null)
                return 0L;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            Object o;
            for (int i = 0; i < len; i++) {
                result ^= (z += (((o = data[i]) == null ? -1L : o.hashCode()) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return ((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static long hash64(final Object data) {
            if (data == null)
                return 0L;
            long result = data.hashCode() * 0xFF51AFD7ED558CCDL;
            result = (result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L;
            return (result ^ result >>> 33);
        }


        public static int hash(final CharSequence data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length();
            for (int i = 0; i < len; i++) {
                result ^= (z += (data.charAt(i) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final boolean[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ? 0x6F51AFDB : 0xC3564E95));
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final byte[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final short[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final int[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final long[] data) {
            if (data == null)
                return 0;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return (int)((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static int hash(final char[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final float[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (floatToIntBits(data[i]) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;

        }

        public static int hash(final double[] data) {
            if (data == null)
                return 0;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (NumberTools.doubleToLongBits(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return (int)((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i++) {
                result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = Math.min(end, data.length());
            for (int i = start; i < len; i++) {
                result ^= (z += (data.charAt(i) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = Math.min(end, data.length);
            for (int i = start; i < len; i += step) {
                result ^= (z += (data[i] ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive), ending before end (exclusive), and
         * moving between chars in increments of step (which is always greater than 0).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @param step  how many elements to advance after using one element from data; must be greater than 0
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final CharSequence data, final int start, final int end, final int step) {
            if (data == null || start >= end || step <= 0)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = Math.min(end, data.length());
            for (int i = start; i < len; i += step) {
                result ^= (z += (data.charAt(i) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final char[][] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash(data[i]) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final int[][] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash(data[i]) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final long[][] data) {
            if (data == null)
                return 0;
            long result = 0x1A976FDF6BF60B8EL, z = 0x60642E2A34326F15L;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash64(data[i]) ^ 0xC6BC279692B5CC85L) * 0x6C8E9CF570932BABL);
                result = (result << 54 | result >>> 10);
            }
            result += (z ^ z >>> 26) * 0x632BE59BD9B4E019L;
            result = (result ^ result >>> 33) * 0xFF51AFD7ED558CCDL;
            return (int)((result ^ result >>> 33) * 0xC4CEB9FE1A85EC53L);
        }

        public static int hash(final CharSequence[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash(data[i]) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final CharSequence[]... data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash(data[i]) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final Iterable<? extends CharSequence> data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            for (CharSequence datum : data) {
                result ^= (z += (hash(datum) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;

        }

        public static int hash(final List<? extends CharSequence> data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.size();
            for (int i = 0; i < len; i++) {
                result ^= (z += (hash(data.get(i)) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;

        }

        public static int hash(final Object[] data) {
            if (data == null)
                return 0;
            int result = 0x1A976FDF, z = 0x60642E25;
            final int len = data.length;
            Object o;
            for (int i = 0; i < len; i++) {
                result ^= (z += (((o = data[i]) == null ? -1 : o.hashCode()) ^ 0xC3564E95) * 0x9E375);
                z ^= (result = (result << 20 | result >>> 12));
            }
            result += (z ^ z >>> 15 ^ 0xAE932BD5) * 0x632B9;
            result = (result ^ result >>> 15) * 0xFF51D;
            result = (result ^ result >>> 15) * 0xC4CEB;
            return result ^ result >>> 15;
        }

        public static int hash(final Object data) {
            if (data == null)
                return 0;
            final int h = data.hashCode() * 0x9E375;
            return h ^ (h >>> 16);
        }
    }


    /**
     * A fairly fast hashing algorithm in general, Water performs especially well on large arrays, and passes SMHasher's
     * newest and most stringent version of tests. The int-hashing {@link #hash(int[])} method is almost twice as fast
     * as {@link Hive#hash(int[])} and faster than {@link Arrays#hashCode(int[])}; on longer arrays
     * {@link Curlup#hash(int[])} is faster. Based on <a href="https://github.com/wangyi-fudan/wyhash">wyhash</a>,
     * specifically <a href="https://github.com/tommyettinger/waterhash">the waterhash variant</a>. This version passes
     * SMHasher for both the 32-bit output hash() methods and the 64-bit output hash64() methods (which use the slightly
     * tweaked wheathash variant in the waterhash Git repo, or woothash for hashing long arrays). While an earlier
     * version passed rurban/smhasher, it failed demerphq/smhasher (Yves' more stringent fork), so some minor tweaks
     * allowed the latest code to pass Yves' test. Uses 64-bit math, so it won't be as fast on GWT. Currently, the
     * methods that hash types other than int arrays aren't as fast as the int array hash, but they are usually faster
     * than the former default Hive implementation, and unlike Hive, these pass SMHasher. If you want to have a seed the
     * hash, so hashing the same data with a different seed produces different output, you can use {@link Yolk} or
     * {@link Curlup}, preferring Curlup unless all of your data is in small arrays (under 20 length, give or take).
     * <br>
     * These hash functions are so fast because they operate in bulk on 4 items at a time, such as 4 ints (which is the
     * optimal case), 4 bytes, or 4 longs (which uses a different algorithm). This bulk operation usually entails 3
     * multiplications and some other, cheaper operations per 4 items hashed. For long arrays, it requires many more
     * multiplications, but modern CPUs can pipeline the operations on unrelated longs to run in parallel on one core.
     * If any items are left over after the bulk segment, Water uses the least effort possible to hash the remaining 1,
     * 2, or 3 items left. Most of these operations use the method {@link #mum(long, long)}, which helps take two inputs
     * and multiply them once, getting a more-random result after another small step. The long array code uses
     * {@link #wow(long, long)} (similar to mum upside-down), which mixes up its arguments with each other before
     * multplying. It finishes with either code similar to mum() for 32-bit output hash() methods, or a somewhat more
     * rigorous method for 64-bit output hash64() methods (still similar to mum).
     */
    public static final class Water {
        /**
         * Big constant 0.
         */
        public static final long b0 = 0xA0761D6478BD642FL;
        /**
         * Big constant 1.
         */
        public static final long b1 = 0xE7037ED1A0B428DBL;
        /**
         * Big constant 2.
         */
        public static final long b2 = 0x8EBC6AF09C88C6E3L;
        /**
         * Big constant 3.
         */
        public static final long b3 = 0x589965CC75374CC3L;
        /**
         * Big constant 4.
         */
        public static final long b4 = 0x1D8E4E27C47D124FL;
        /**
         * Big constant 5.
         */
        public static final long b5 = 0xEB44ACCAB455D165L;

        /**
         * Takes two arguments that are technically longs, and should be very different, and uses them to get a result
         * that is technically a long and mixes the bits of the inputs. The arguments and result are only technically
         * longs because their lower 32 bits matter much more than their upper 32, and giving just any long won't work.
         * <br>
         * This is very similar to wyhash's mum function, but doesn't use 128-bit math because it expects that its
         * arguments are only relevant in their lower 32 bits (allowing their product to fit in 64 bits).
         * @param a a long that should probably only hold an int's worth of data
         * @param b a long that should probably only hold an int's worth of data
         * @return a sort-of randomized output dependent on both inputs
         */
        public static long mum(final long a, final long b) {
            final long n = a * b;
            return n - (n >>> 32);
        }

        /**
         * A slower but higher-quality variant on {@link #mum(long, long)} that can take two arbitrary longs (with any
         * of their 64 bits containing relevant data) instead of mum's 32-bit sections of its inputs, and outputs a
         * 64-bit result that can have any of its bits used.
         * <br>
         * This was changed so it distributes bits from both inputs a little better on July 6, 2019.
         * @param a any long
         * @param b any long
         * @return a sort-of randomized output dependent on both inputs
         */
        public static long wow(final long a, final long b) {
            final long n = (a ^ (b << 39 | b >>> 25)) * (b ^ (a << 39 | a >>> 25));
            return n ^ (n >>> 32);
        }

        public static long hash64(final boolean[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;//seed = b1 ^ b1 >>> 29 ^ b1 >>> 43 ^ b1 << 7 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                        mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
                case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
                case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public static long hash64(final byte[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final short[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final char[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final CharSequence data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final int[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final int[] data, final int length) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            for (int i = 3; i < length; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (length & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
                case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
            }
            seed = (seed ^ seed << 16) * (length ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final long[] data) {
            if (data == null) return 0;
//          long seed = b0 ^ b0 >>> 23 ^ b0 >>> 48 ^ b0 << 7 ^ b0 << 53,
//                    a = seed + b4, b = seed + b3,
//                    c = seed + b2, d = seed + b1;
            long seed = 0x1E98AE18CA351B28L,
                    a = 0x3C26FC408EB22D77L, b = 0x773213E53F6C67EBL,
                    c = 0xAD55190966BDE20BL, d = 0x59C2CEA6AE94403L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public static long hash64(final float[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                        mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public static long hash64(final double[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(doubleToMixedIntBits(data[i-3]) ^ b1, doubleToMixedIntBits(data[i-2]) ^ b2) + seed,
                        mum(doubleToMixedIntBits(data[i-1]) ^ b3, doubleToMixedIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (doubleToMixedIntBits(data[len-1]) >>> 16), b3 ^ (doubleToMixedIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ doubleToMixedIntBits(data[len-2]), b0 ^ doubleToMixedIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ doubleToMixedIntBits(data[len-3]), b2 ^ doubleToMixedIntBits(data[len-2])) ^ mum(seed ^ doubleToMixedIntBits(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = 9069147967908697017L;
            final int len = Math.min(end, data.length);
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return mum(seed ^ seed << 16, len - start ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = 9069147967908697017L;
            final int len = Math.min(end, data.length());
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return mum(seed ^ seed << 16, len - start ^ b0);
        }


        public static long hash64(final char[][] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final int[][] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final long[][] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final CharSequence[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final CharSequence[]... data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.size();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data.get(i-3)) ^ b1, hash(data.get(i-2)) ^ b2) + seed,
                        mum(hash(data.get(i-1)) ^ b3, hash(data.get(i  )) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data.get(len-2)), b0 ^ hash(data.get(len-1))); break;
                case 3: seed = mum(seed ^ hash(data.get(len-3)), b2 ^ hash(data.get(len-2))) ^ mum(seed ^ hash(data.get(len-1)), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);

        }

        public static long hash64(final Object[] data) {
            if (data == null) return 0;
            long seed = 9069147967908697017L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = data.hashCode() * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }


        public static int hash(final boolean[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                        mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
                case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
                case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(final byte[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final short[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final char[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final CharSequence data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(final int[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(final int[] data, final int length) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            for (int i = 3; i < length; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (length & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
                case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
            }
            return (int) mum(seed ^ seed << 16, length ^ b0);
        }

        public static int hash(final long[] data) {
            if (data == null) return 0;
            //long seed = 0x1E98AE18CA351B28L,// seed = b0 ^ b0 >>> 23 ^ b0 >>> 48 ^ b0 << 7 ^ b0 << 53,
//                    a = seed ^ b4, b = (seed << 17 | seed >>> 47) ^ b3,
//                    c = (seed << 31 | seed >>> 33) ^ b2, d = (seed << 47 | seed >>> 17) ^ b1;
            //a = 0x316E03F0E480967L, b = 0x4A8F1A6436771F2L,
            //        c = 0xEBA6E76493C491EFL, d = 0x6A97719DF7B84DC1L;
            long seed = 0x1E98AE18CA351B28L, a = 0x3C26FC408EB22D77L, b = 0x773213E53F6C67EBL, c = 0xAD55190966BDE20BL, d = 0x59C2CEA6AE94403L;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return (int)(seed - (seed >>> 32));
        }

        public static int hash(final float[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                        mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(final double[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(doubleToMixedIntBits(data[i-3]) ^ b1, doubleToMixedIntBits(data[i-2]) ^ b2) + seed,
                        mum(doubleToMixedIntBits(data[i-1]) ^ b3, doubleToMixedIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (doubleToMixedIntBits(data[len-1]) >>> 16), b3 ^ (doubleToMixedIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ doubleToMixedIntBits(data[len-2]), b0 ^ doubleToMixedIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ doubleToMixedIntBits(data[len-3]), b2 ^ doubleToMixedIntBits(data[len-2])) ^ mum(seed ^ doubleToMixedIntBits(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = Math.min(end, data.length);
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len - start ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = Math.min(end, data.length());
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return (int) mum(seed ^ seed << 16, len - start ^ b0);
        }


        public static int hash(final char[][] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final int[][] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final long[][] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final CharSequence[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final CharSequence[]... data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.size();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data.get(i-3)) ^ b1, hash(data.get(i-2)) ^ b2) + seed,
                        mum(hash(data.get(i-1)) ^ b3, hash(data.get(i  )) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data.get(len-2)), b0 ^ hash(data.get(len-1))); break;
                case 3: seed = mum(seed ^ hash(data.get(len-3)), b2 ^ hash(data.get(len-2))) ^ mum(seed ^ hash(data.get(len-1)), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final Object[] data) {
            if (data == null) return 0;
            long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(final Object data) {
            if (data == null)
                return 0;
            final int h = data.hashCode() * 0x9E375;
            return h ^ (h >>> 16);
        }
    }

    /**
     * Like Mist, this is a class for hash functors, each an object with a 64-bit long seed, but it uses about the same
     * algorithm as {@link Water} instead of the older, less-robust style Mist uses. This can be faster than
     * {@link Curlup}, but only for small arrays as input (20 length or less); it tends to be slower on larger arrays,
     * though not by much, and should be the same for {@code long[]} since they share an implementation for that type.
     * Normally you should prefer Curlup if you know some or all of your arrays will be of moderate size or larger.
     * Has a lot of predefined functors (192, named after 24 Greek letters and 72 Goetic demons, see
     * <a href="https://en.wikipedia.org/wiki/Lesser_Key_of_Solomon#The_Seventy-Two_Demons">Wikipedia for the demons</a>,
     * in both lower case and lower case with a trailing underscore). You probably want to use {@link #predefined}
     * instead of wrangling demon names; you can always choose an element from predefined with a 7-bit number, and there
     * are 64 numbers outside that range so you can choose any of those when a functor must be different.
     */
    public static final class Yolk {
        private final long seed;

        public Yolk(){
            this.seed = 9069147967908697017L;
        }
        public Yolk(long seed)
        {
            seed += b1;
            this.seed = seed ^ seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
        }
        public Yolk(final CharSequence seed)
        {
            this(Hive.hash64(seed));
        }

        public static final Yolk alpha = new Yolk("alpha"), beta = new Yolk("beta"), gamma = new Yolk("gamma"),
                delta = new Yolk("delta"), epsilon = new Yolk("epsilon"), zeta = new Yolk("zeta"),
                eta = new Yolk("eta"), theta = new Yolk("theta"), iota = new Yolk("iota"),
                kappa = new Yolk("kappa"), lambda = new Yolk("lambda"), mu = new Yolk("mu"),
                nu = new Yolk("nu"), xi = new Yolk("xi"), omicron = new Yolk("omicron"), pi = new Yolk("pi"),
                rho = new Yolk("rho"), sigma = new Yolk("sigma"), tau = new Yolk("tau"),
                upsilon = new Yolk("upsilon"), phi = new Yolk("phi"), chi = new Yolk("chi"), psi = new Yolk("psi"),
                omega = new Yolk("omega"),
                alpha_ = new Yolk("ALPHA"), beta_ = new Yolk("BETA"), gamma_ = new Yolk("GAMMA"),
                delta_ = new Yolk("DELTA"), epsilon_ = new Yolk("EPSILON"), zeta_ = new Yolk("ZETA"),
                eta_ = new Yolk("ETA"), theta_ = new Yolk("THETA"), iota_ = new Yolk("IOTA"),
                kappa_ = new Yolk("KAPPA"), lambda_ = new Yolk("LAMBDA"), mu_ = new Yolk("MU"),
                nu_ = new Yolk("NU"), xi_ = new Yolk("XI"), omicron_ = new Yolk("OMICRON"), pi_ = new Yolk("PI"),
                rho_ = new Yolk("RHO"), sigma_ = new Yolk("SIGMA"), tau_ = new Yolk("TAU"),
                upsilon_ = new Yolk("UPSILON"), phi_ = new Yolk("PHI"), chi_ = new Yolk("CHI"), psi_ = new Yolk("PSI"),
                omega_ = new Yolk("OMEGA"),
                baal = new Yolk("baal"), agares = new Yolk("agares"), vassago = new Yolk("vassago"), samigina = new Yolk("samigina"),
                marbas = new Yolk("marbas"), valefor = new Yolk("valefor"), amon = new Yolk("amon"), barbatos = new Yolk("barbatos"),
                paimon = new Yolk("paimon"), buer = new Yolk("buer"), gusion = new Yolk("gusion"), sitri = new Yolk("sitri"),
                beleth = new Yolk("beleth"), leraje = new Yolk("leraje"), eligos = new Yolk("eligos"), zepar = new Yolk("zepar"),
                botis = new Yolk("botis"), bathin = new Yolk("bathin"), sallos = new Yolk("sallos"), purson = new Yolk("purson"),
                marax = new Yolk("marax"), ipos = new Yolk("ipos"), aim = new Yolk("aim"), naberius = new Yolk("naberius"),
                glasya_labolas = new Yolk("glasya_labolas"), bune = new Yolk("bune"), ronove = new Yolk("ronove"), berith = new Yolk("berith"),
                astaroth = new Yolk("astaroth"), forneus = new Yolk("forneus"), foras = new Yolk("foras"), asmoday = new Yolk("asmoday"),
                gaap = new Yolk("gaap"), furfur = new Yolk("furfur"), marchosias = new Yolk("marchosias"), stolas = new Yolk("stolas"),
                phenex = new Yolk("phenex"), halphas = new Yolk("halphas"), malphas = new Yolk("malphas"), raum = new Yolk("raum"),
                focalor = new Yolk("focalor"), vepar = new Yolk("vepar"), sabnock = new Yolk("sabnock"), shax = new Yolk("shax"),
                vine = new Yolk("vine"), bifrons = new Yolk("bifrons"), vual = new Yolk("vual"), haagenti = new Yolk("haagenti"),
                crocell = new Yolk("crocell"), furcas = new Yolk("furcas"), balam = new Yolk("balam"), alloces = new Yolk("alloces"),
                caim = new Yolk("caim"), murmur = new Yolk("murmur"), orobas = new Yolk("orobas"), gremory = new Yolk("gremory"),
                ose = new Yolk("ose"), amy = new Yolk("amy"), orias = new Yolk("orias"), vapula = new Yolk("vapula"),
                zagan = new Yolk("zagan"), valac = new Yolk("valac"), andras = new Yolk("andras"), flauros = new Yolk("flauros"),
                andrealphus = new Yolk("andrealphus"), kimaris = new Yolk("kimaris"), amdusias = new Yolk("amdusias"), belial = new Yolk("belial"),
                decarabia = new Yolk("decarabia"), seere = new Yolk("seere"), dantalion = new Yolk("dantalion"), andromalius = new Yolk("andromalius"),
                baal_ = new Yolk("BAAL"), agares_ = new Yolk("AGARES"), vassago_ = new Yolk("VASSAGO"), samigina_ = new Yolk("SAMIGINA"),
                marbas_ = new Yolk("MARBAS"), valefor_ = new Yolk("VALEFOR"), amon_ = new Yolk("AMON"), barbatos_ = new Yolk("BARBATOS"),
                paimon_ = new Yolk("PAIMON"), buer_ = new Yolk("BUER"), gusion_ = new Yolk("GUSION"), sitri_ = new Yolk("SITRI"),
                beleth_ = new Yolk("BELETH"), leraje_ = new Yolk("LERAJE"), eligos_ = new Yolk("ELIGOS"), zepar_ = new Yolk("ZEPAR"),
                botis_ = new Yolk("BOTIS"), bathin_ = new Yolk("BATHIN"), sallos_ = new Yolk("SALLOS"), purson_ = new Yolk("PURSON"),
                marax_ = new Yolk("MARAX"), ipos_ = new Yolk("IPOS"), aim_ = new Yolk("AIM"), naberius_ = new Yolk("NABERIUS"),
                glasya_labolas_ = new Yolk("GLASYA_LABOLAS"), bune_ = new Yolk("BUNE"), ronove_ = new Yolk("RONOVE"), berith_ = new Yolk("BERITH"),
                astaroth_ = new Yolk("ASTAROTH"), forneus_ = new Yolk("FORNEUS"), foras_ = new Yolk("FORAS"), asmoday_ = new Yolk("ASMODAY"),
                gaap_ = new Yolk("GAAP"), furfur_ = new Yolk("FURFUR"), marchosias_ = new Yolk("MARCHOSIAS"), stolas_ = new Yolk("STOLAS"),
                phenex_ = new Yolk("PHENEX"), halphas_ = new Yolk("HALPHAS"), malphas_ = new Yolk("MALPHAS"), raum_ = new Yolk("RAUM"),
                focalor_ = new Yolk("FOCALOR"), vepar_ = new Yolk("VEPAR"), sabnock_ = new Yolk("SABNOCK"), shax_ = new Yolk("SHAX"),
                vine_ = new Yolk("VINE"), bifrons_ = new Yolk("BIFRONS"), vual_ = new Yolk("VUAL"), haagenti_ = new Yolk("HAAGENTI"),
                crocell_ = new Yolk("CROCELL"), furcas_ = new Yolk("FURCAS"), balam_ = new Yolk("BALAM"), alloces_ = new Yolk("ALLOCES"),
                caim_ = new Yolk("CAIM"), murmur_ = new Yolk("MURMUR"), orobas_ = new Yolk("OROBAS"), gremory_ = new Yolk("GREMORY"),
                ose_ = new Yolk("OSE"), amy_ = new Yolk("AMY"), orias_ = new Yolk("ORIAS"), vapula_ = new Yolk("VAPULA"),
                zagan_ = new Yolk("ZAGAN"), valac_ = new Yolk("VALAC"), andras_ = new Yolk("ANDRAS"), flauros_ = new Yolk("FLAUROS"),
                andrealphus_ = new Yolk("ANDREALPHUS"), kimaris_ = new Yolk("KIMARIS"), amdusias_ = new Yolk("AMDUSIAS"), belial_ = new Yolk("BELIAL"),
                decarabia_ = new Yolk("DECARABIA"), seere_ = new Yolk("SEERE"), dantalion_ = new Yolk("DANTALION"), andromalius_ = new Yolk("ANDROMALIUS")
                ;
        /**
         * Has a length of 192, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Yolk[] predefined = new Yolk[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
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
                andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_};


        public long hash64(final boolean[] data) {
            if (data == null) return 0;
            long seed = this.seed;//seed = b1 ^ b1 >>> 29 ^ b1 >>> 43 ^ b1 << 7 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                        mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
                case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
                case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public long hash64(final byte[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final short[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final char[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final CharSequence data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final int[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final int[] data, final int length) {
            if (data == null) return 0;
            long seed = this.seed;
            for (int i = 3; i < length; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (length & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
                case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
            }
            seed = (seed ^ seed << 16) * (length ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final long[] data) {
            if (data == null) return 0;
            long seed = this.seed, a = this.seed + b4, b = this.seed + b3, c = this.seed + b2, d = this.seed + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public long hash64(final float[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                        mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public long hash64(final double[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(doubleToMixedIntBits(data[i-3]) ^ b1, doubleToMixedIntBits(data[i-2]) ^ b2) + seed,
                        mum(doubleToMixedIntBits(data[i-1]) ^ b3, doubleToMixedIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (doubleToMixedIntBits(data[len-1]) >>> 16), b3 ^ (doubleToMixedIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ doubleToMixedIntBits(data[len-2]), b0 ^ doubleToMixedIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ doubleToMixedIntBits(data[len-3]), b2 ^ doubleToMixedIntBits(data[len-2])) ^ mum(seed ^ doubleToMixedIntBits(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = this.seed;
            final int len = Math.min(end, data.length);
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return mum(seed ^ seed << 16, len - start ^ Water.b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = this.seed;
            final int len = Math.min(end, data.length());
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return mum(seed ^ seed << 16, len - start ^ b0);
        }


        public long hash64(final char[][] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final int[][] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final long[][] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final CharSequence[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final CharSequence[]... data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = this.seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.size();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data.get(i-3)) ^ b1, hash(data.get(i-2)) ^ b2) + seed,
                        mum(hash(data.get(i-1)) ^ b3, hash(data.get(i  )) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data.get(len-2)), b0 ^ hash(data.get(len-1))); break;
                case 3: seed = mum(seed ^ hash(data.get(len-3)), b2 ^ hash(data.get(len-2))) ^ mum(seed ^ hash(data.get(len-1)), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);

        }

        public long hash64(final Object[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }


        public int hash(final boolean[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                        mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
                case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
                case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public int hash(final byte[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final short[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final char[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final CharSequence data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public int hash(final int[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public int hash(final int[] data, final int length) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            for (int i = 3; i < length; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (length & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
                case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
            }
            return (int) mum(seed ^ seed << 16, length ^ b0);
        }

        public int hash(final long[] data) {
            if (data == null) return 0;
            long seed = this.seed, a = this.seed + b4, b = this.seed + b3, c = this.seed + b2, d = this.seed + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return (int)(seed - (seed >>> 32));
        }

        public int hash(final float[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                        mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public int hash(final double[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(doubleToMixedIntBits(data[i-3]) ^ b1, doubleToMixedIntBits(data[i-2]) ^ b2) + seed,
                        mum(doubleToMixedIntBits(data[i-1]) ^ b3, doubleToMixedIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (doubleToMixedIntBits(data[len-1]) >>> 16), b3 ^ (doubleToMixedIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ doubleToMixedIntBits(data[len-2]), b0 ^ doubleToMixedIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ doubleToMixedIntBits(data[len-3]), b2 ^ doubleToMixedIntBits(data[len-2])) ^ mum(seed ^ doubleToMixedIntBits(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = Math.min(end, data.length);
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len - start ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public int hash(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = Math.min(end, data.length());
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return (int) mum(seed ^ seed << 16, len - start ^ b0);
        }


        public int hash(final char[][] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final int[][] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final long[][] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final CharSequence[] data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final CharSequence[]... data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = this.seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = this.seed;//b1 ^ b1 >>> 41 ^ b1 << 53;
            final int len = data.size();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data.get(i-3)) ^ b1, hash(data.get(i-2)) ^ b2) + seed,
                        mum(hash(data.get(i-1)) ^ b3, hash(data.get(i  )) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data.get(len-2)), b0 ^ hash(data.get(len-1))); break;
                case 3: seed = mum(seed ^ hash(data.get(len-3)), b2 ^ hash(data.get(len-2))) ^ mum(seed ^ hash(data.get(len-1)), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final Object[] data) {
            if (data == null) return 0;
            long seed = this.seed;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                        mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
                case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + seed) * 0x9E3779B97F4A7C15L >>> 32);
        }


        public static long hash64(long seed, final boolean[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                        mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
                case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
                case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public static long hash64(long seed, final byte[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final short[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final char[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final CharSequence data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final int[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final int[] data, final int length) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            for (int i = 3; i < length; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (length & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
                case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
            }
            seed = (seed ^ seed << 16) * (length ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final long[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            long a = seed + b4, b = seed + b3, c = seed + b2, d = seed + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public static long hash64(long seed, final float[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                        mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public static long hash64(long seed, final double[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(doubleToMixedIntBits(data[i-3]) ^ b1, doubleToMixedIntBits(data[i-2]) ^ b2) + seed,
                        mum(doubleToMixedIntBits(data[i-1]) ^ b3, doubleToMixedIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (doubleToMixedIntBits(data[len-1]) >>> 16), b3 ^ (doubleToMixedIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ doubleToMixedIntBits(data[len-2]), b0 ^ doubleToMixedIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ doubleToMixedIntBits(data[len-3]), b2 ^ doubleToMixedIntBits(data[len-2])) ^ mum(seed ^ doubleToMixedIntBits(data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static long hash64(long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = Math.min(end, data.length);
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return mum(seed ^ seed << 16, len - start ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static long hash64(long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = Math.min(end, data.length());
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return mum(seed ^ seed << 16, len - start ^ b0);
        }


        public static long hash64(long seed, final char[][] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final int[][] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final long[][] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final CharSequence[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final CharSequence[]... data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(seed, it.next()) ^ b1, (it.hasNext() ? hash(seed, it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(seed, it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(seed, it.next()) ^ b4 ^ ++len : b4)));
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.size();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data.get(i-3)) ^ b1, hash(seed, data.get(i-2)) ^ b2) + seed,
                        mum(hash(seed, data.get(i-1)) ^ b3, hash(seed, data.get(i  )) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data.get(len-2)), b0 ^ hash(seed, data.get(len-1))); break;
                case 3: seed = mum(seed ^ hash(seed, data.get(len-3)), b2 ^ hash(seed, data.get(len-2))) ^ mum(seed ^ hash(seed, data.get(len-1)), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);

        }

        public static long hash64(long seed, final Object[] data) {
            if (data == null) return 0L;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public static long hash64(long seed, final Object data) {
            if (data == null)
                return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final long h = (data.hashCode() + seed) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }


        public static int hash(long seed, final boolean[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                        mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
                case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
                case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(long seed, final byte[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final short[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final char[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final CharSequence data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(long seed, final int[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(long seed, final int[] data, final int length) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            for (int i = 3; i < length; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (length & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
                case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
            }
            return (int) mum(seed ^ seed << 16, length ^ b0);
        }

        public static int hash(long seed, final long[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            long a = seed + b4, b = seed + b3, c = seed + b2, d = seed + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return (int)(seed - (seed >>> 32));
        }

        public static int hash(long seed, final float[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                        mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }
        public static int hash(long seed, final double[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(doubleToMixedIntBits(data[i-3]) ^ b1, doubleToMixedIntBits(data[i-2]) ^ b2) + seed,
                        mum(doubleToMixedIntBits(data[i-1]) ^ b3, doubleToMixedIntBits(data[i]) ^ b4));
            }
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ (doubleToMixedIntBits(data[len-1]) >>> 16), b3 ^ (doubleToMixedIntBits(data[len-1]) & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ doubleToMixedIntBits(data[len-2]), b0 ^ doubleToMixedIntBits(data[len-1])); break;
                case 3: seed = mum(seed ^ doubleToMixedIntBits(data[len-3]), b2 ^ doubleToMixedIntBits(data[len-2])) ^ mum(seed ^ doubleToMixedIntBits(data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = Math.min(end, data.length);
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                        mum(data[i-1] ^ b3, data[i] ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
                case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
                case 3: seed = mum(seed ^ data[len-3] ^ (long) data[len - 2] << 16, b1 ^ data[len-1]); break;
            }
            return (int) mum(seed ^ seed << 16, len - start ^ b0);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 32-bit hash code for the requested section of data
         */
        public static int hash(long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end)
                return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = Math.min(end, data.length());
            for (int i = start + 3; i < len; i+=4) {
                seed = mum(
                        mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                        mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
            }
            switch (len - start & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
                case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
                case 3: seed = mum(seed ^ data.charAt(len-3) ^ (long) data.charAt(len - 2) << 16, b1 ^ data.charAt(len-1)); break;
            }
            return (int) mum(seed ^ seed << 16, len - start ^ b0);
        }


        public static int hash(long seed, final char[][] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final int[][] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final long[][] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final CharSequence[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(seed, it.next()) ^ b1, (it.hasNext() ? hash(seed, it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(seed, it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(seed, it.next()) ^ b4 ^ ++len : b4)));
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.size();
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data.get(i-3)) ^ b1, hash(seed, data.get(i-2)) ^ b2) + seed,
                        mum(hash(seed, data.get(i-1)) ^ b3, hash(seed, data.get(i  )) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data.get(len-2)), b0 ^ hash(seed, data.get(len-1))); break;
                case 3: seed = mum(seed ^ hash(seed, data.get(len-3)), b2 ^ hash(seed, data.get(len-2))) ^ mum(seed ^ hash(seed, data.get(len-1)), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final Object[] data) {
            if (data == null) return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                seed = mum(
                        mum(hash(seed, data[i-3]) ^ b1, hash(seed, data[i-2]) ^ b2) + seed,
                        mum(hash(seed, data[i-1]) ^ b3, hash(seed, data[i  ]) ^ b4));
            }
            int t;
            switch (len & 3) {
                case 0: seed = mum(b1 ^ seed, b4 + seed); break;
                case 1: seed = mum(seed ^((t = hash(seed, data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
                case 2: seed = mum(seed ^ hash(seed, data[len-2]), b0 ^ hash(seed, data[len-1])); break;
                case 3: seed = mum(seed ^ hash(seed, data[len-3]), b2 ^ hash(seed, data[len-2])) ^ mum(seed ^ hash(seed, data[len-1]), b4); break;
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public static int hash(long seed, final Object data) {
            if (data == null)
                return 0;
            seed += b1; seed ^= seed >>> 23 ^ seed >>> 48 ^ seed << 7 ^ seed << 53;
            return (int)((data.hashCode() + seed) * 0x9E3779B97F4A7C15L >>> 32);
        }


    }

    /**
     * Like Yolk, this is a class for hash functors, each an object with a 64-bit long seed. It uses an odd-but-fast
     * SIMD-friendly technique when hashing 32-bit items or smaller, and falls back to Yolk's algorithm when hashing
     * long values. If you are mainly hashing int arrays, short arrays, or byte arrays, this is probably the fastest
     * hash here unless the arrays are small (it outperforms all of the other hashes here on int arrays when those
     * arrays have length 50, and probably is faster than some sooner than that). Notably, on arrays 50 or longer this
     * runs in very close to half the time of {@link Arrays#hashCode(int[])}. This passes SMHasher for at least 64-bit
     * output. Has a lot of predefined functors (192, named after 24 Greek letters and 72 Goetic demons, see
     * <a href="https://en.wikipedia.org/wiki/Lesser_Key_of_Solomon#The_Seventy-Two_Demons">Wikipedia for the demons</a>,
     * in both lower case and lower case with a trailing underscore). You probably want to use {@link #predefined}
     * instead of wrangling demon names; you can always choose an element from predefined with a 7-bit number, and there
     * are 64 numbers outside that range so you can choose any of those when a functor must be different.
     * <br>
     * This hash is much more effective with large inputs because it takes advantage of HotSpot's optimizations for code
     * that looks like a dot product over part of an array. The general concept for this hash came from the "Unrolled"
     * hash in <a href="https://richardstartin.github.io/posts/collecting-rocks-and-benchmarks">one of Richard Startin's
     * blog posts</a>, which traces back to
     * <a href="http://mail.openjdk.java.net/pipermail/core-libs-dev/2014-September/028898.html">Peter Levart posting a
     * related improvement on String.hashCode()</a> in 2014. This isn't as fast as Startin's "Vectorized" hash, but this
     * works on variable array lengths and also passes SMHasher.
     * <br>
     * The name curlup comes from an M.C. Escher painting of a creature, whose name translates to curl-up, that could
     * walk on six legs to climb stairs, or roll at high speeds when the conditions were right.
     */
    public static final class Curlup {
        private final long seed;

        public Curlup(){
            this.seed = 0xC4CEB9FE1A85EC53L;
        }
        public Curlup(long seed)
        {
            this.seed = randomize(seed);
        }

        /**
         * Very similar to Pelican and related unary hashes; uses "xor rotate xor rotate" as an early step to mix any
         * clustered bits all around the result, then the rest is like MurmurHash3's mixer.
         * @param seed any long; there is no fix point at 0
         * @return any long
         */
        public static long randomize(long seed) {
            seed ^= (seed << 41 | seed >>> 23) ^ (seed << 17 | seed >>> 47) ^ 0xCB9C59B3F9F87D4DL;
            seed *= 0x369DEA0F31A53F85L;
            seed ^= seed >>> 31;
            seed *= 0xDB4F0B9175AE2165L;
            return seed ^ seed >>> 28;
        }

        public Curlup(final CharSequence seed)
        {
            this(Water.hash64(seed));
        }

        public static final Curlup alpha = new Curlup("alpha"), beta = new Curlup("beta"), gamma = new Curlup("gamma"),
                delta = new Curlup("delta"), epsilon = new Curlup("epsilon"), zeta = new Curlup("zeta"),
                eta = new Curlup("eta"), theta = new Curlup("theta"), iota = new Curlup("iota"),
                kappa = new Curlup("kappa"), lambda = new Curlup("lambda"), mu = new Curlup("mu"),
                nu = new Curlup("nu"), xi = new Curlup("xi"), omicron = new Curlup("omicron"), pi = new Curlup("pi"),
                rho = new Curlup("rho"), sigma = new Curlup("sigma"), tau = new Curlup("tau"),
                upsilon = new Curlup("upsilon"), phi = new Curlup("phi"), chi = new Curlup("chi"), psi = new Curlup("psi"),
                omega = new Curlup("omega"),
                alpha_ = new Curlup("ALPHA"), beta_ = new Curlup("BETA"), gamma_ = new Curlup("GAMMA"),
                delta_ = new Curlup("DELTA"), epsilon_ = new Curlup("EPSILON"), zeta_ = new Curlup("ZETA"),
                eta_ = new Curlup("ETA"), theta_ = new Curlup("THETA"), iota_ = new Curlup("IOTA"),
                kappa_ = new Curlup("KAPPA"), lambda_ = new Curlup("LAMBDA"), mu_ = new Curlup("MU"),
                nu_ = new Curlup("NU"), xi_ = new Curlup("XI"), omicron_ = new Curlup("OMICRON"), pi_ = new Curlup("PI"),
                rho_ = new Curlup("RHO"), sigma_ = new Curlup("SIGMA"), tau_ = new Curlup("TAU"),
                upsilon_ = new Curlup("UPSILON"), phi_ = new Curlup("PHI"), chi_ = new Curlup("CHI"), psi_ = new Curlup("PSI"),
                omega_ = new Curlup("OMEGA"),
                baal = new Curlup("baal"), agares = new Curlup("agares"), vassago = new Curlup("vassago"), samigina = new Curlup("samigina"),
                marbas = new Curlup("marbas"), valefor = new Curlup("valefor"), amon = new Curlup("amon"), barbatos = new Curlup("barbatos"),
                paimon = new Curlup("paimon"), buer = new Curlup("buer"), gusion = new Curlup("gusion"), sitri = new Curlup("sitri"),
                beleth = new Curlup("beleth"), leraje = new Curlup("leraje"), eligos = new Curlup("eligos"), zepar = new Curlup("zepar"),
                botis = new Curlup("botis"), bathin = new Curlup("bathin"), sallos = new Curlup("sallos"), purson = new Curlup("purson"),
                marax = new Curlup("marax"), ipos = new Curlup("ipos"), aim = new Curlup("aim"), naberius = new Curlup("naberius"),
                glasya_labolas = new Curlup("glasya_labolas"), bune = new Curlup("bune"), ronove = new Curlup("ronove"), berith = new Curlup("berith"),
                astaroth = new Curlup("astaroth"), forneus = new Curlup("forneus"), foras = new Curlup("foras"), asmoday = new Curlup("asmoday"),
                gaap = new Curlup("gaap"), furfur = new Curlup("furfur"), marchosias = new Curlup("marchosias"), stolas = new Curlup("stolas"),
                phenex = new Curlup("phenex"), halphas = new Curlup("halphas"), malphas = new Curlup("malphas"), raum = new Curlup("raum"),
                focalor = new Curlup("focalor"), vepar = new Curlup("vepar"), sabnock = new Curlup("sabnock"), shax = new Curlup("shax"),
                vine = new Curlup("vine"), bifrons = new Curlup("bifrons"), vual = new Curlup("vual"), haagenti = new Curlup("haagenti"),
                crocell = new Curlup("crocell"), furcas = new Curlup("furcas"), balam = new Curlup("balam"), alloces = new Curlup("alloces"),
                caim = new Curlup("caim"), murmur = new Curlup("murmur"), orobas = new Curlup("orobas"), gremory = new Curlup("gremory"),
                ose = new Curlup("ose"), amy = new Curlup("amy"), orias = new Curlup("orias"), vapula = new Curlup("vapula"),
                zagan = new Curlup("zagan"), valac = new Curlup("valac"), andras = new Curlup("andras"), flauros = new Curlup("flauros"),
                andrealphus = new Curlup("andrealphus"), kimaris = new Curlup("kimaris"), amdusias = new Curlup("amdusias"), belial = new Curlup("belial"),
                decarabia = new Curlup("decarabia"), seere = new Curlup("seere"), dantalion = new Curlup("dantalion"), andromalius = new Curlup("andromalius"),
                baal_ = new Curlup("BAAL"), agares_ = new Curlup("AGARES"), vassago_ = new Curlup("VASSAGO"), samigina_ = new Curlup("SAMIGINA"),
                marbas_ = new Curlup("MARBAS"), valefor_ = new Curlup("VALEFOR"), amon_ = new Curlup("AMON"), barbatos_ = new Curlup("BARBATOS"),
                paimon_ = new Curlup("PAIMON"), buer_ = new Curlup("BUER"), gusion_ = new Curlup("GUSION"), sitri_ = new Curlup("SITRI"),
                beleth_ = new Curlup("BELETH"), leraje_ = new Curlup("LERAJE"), eligos_ = new Curlup("ELIGOS"), zepar_ = new Curlup("ZEPAR"),
                botis_ = new Curlup("BOTIS"), bathin_ = new Curlup("BATHIN"), sallos_ = new Curlup("SALLOS"), purson_ = new Curlup("PURSON"),
                marax_ = new Curlup("MARAX"), ipos_ = new Curlup("IPOS"), aim_ = new Curlup("AIM"), naberius_ = new Curlup("NABERIUS"),
                glasya_labolas_ = new Curlup("GLASYA_LABOLAS"), bune_ = new Curlup("BUNE"), ronove_ = new Curlup("RONOVE"), berith_ = new Curlup("BERITH"),
                astaroth_ = new Curlup("ASTAROTH"), forneus_ = new Curlup("FORNEUS"), foras_ = new Curlup("FORAS"), asmoday_ = new Curlup("ASMODAY"),
                gaap_ = new Curlup("GAAP"), furfur_ = new Curlup("FURFUR"), marchosias_ = new Curlup("MARCHOSIAS"), stolas_ = new Curlup("STOLAS"),
                phenex_ = new Curlup("PHENEX"), halphas_ = new Curlup("HALPHAS"), malphas_ = new Curlup("MALPHAS"), raum_ = new Curlup("RAUM"),
                focalor_ = new Curlup("FOCALOR"), vepar_ = new Curlup("VEPAR"), sabnock_ = new Curlup("SABNOCK"), shax_ = new Curlup("SHAX"),
                vine_ = new Curlup("VINE"), bifrons_ = new Curlup("BIFRONS"), vual_ = new Curlup("VUAL"), haagenti_ = new Curlup("HAAGENTI"),
                crocell_ = new Curlup("CROCELL"), furcas_ = new Curlup("FURCAS"), balam_ = new Curlup("BALAM"), alloces_ = new Curlup("ALLOCES"),
                caim_ = new Curlup("CAIM"), murmur_ = new Curlup("MURMUR"), orobas_ = new Curlup("OROBAS"), gremory_ = new Curlup("GREMORY"),
                ose_ = new Curlup("OSE"), amy_ = new Curlup("AMY"), orias_ = new Curlup("ORIAS"), vapula_ = new Curlup("VAPULA"),
                zagan_ = new Curlup("ZAGAN"), valac_ = new Curlup("VALAC"), andras_ = new Curlup("ANDRAS"), flauros_ = new Curlup("FLAUROS"),
                andrealphus_ = new Curlup("ANDREALPHUS"), kimaris_ = new Curlup("KIMARIS"), amdusias_ = new Curlup("AMDUSIAS"), belial_ = new Curlup("BELIAL"),
                decarabia_ = new Curlup("DECARABIA"), seere_ = new Curlup("SEERE"), dantalion_ = new Curlup("DANTALION"), andromalius_ = new Curlup("ANDROMALIUS")
                ;
        /**
         * Has a length of 192, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Curlup[] predefined = new Curlup[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
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
                andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_};

        /**
         * Big constant 0.
         */
        public static final long b0 = 0xA0761D6478BD642FL;
        /**
         * Big constant 1.
         */
        public static final long b1 = 0xE7037ED1A0B428DBL;
        /**
         * Big constant 2.
         */
        public static final long b2 = 0x8EBC6AF09C88C6E3L;
        /**
         * Big constant 3.
         */
        public static final long b3 = 0x589965CC75374CC3L;
        /**
         * Big constant 4.
         */
        public static final long b4 = 0x1D8E4E27C47D124FL;
        /**
         * Big constant 5.
         */
        public static final long b5 = 0xEB44ACCAB455D165L;

        /**
         * A slower but higher-quality "mum function" that can take two arbitrary longs (with any
         * of their 64 bits containing relevant data) instead of mum's 32-bit sections of its inputs, and outputs a
         * 64-bit result that can have any of its bits used.
         * <br>
         * This was changed so it distributes bits from both inputs a little better on July 6, 2019.
         * @param a any long
         * @param b any long
         * @return a sort-of randomized output dependent on both inputs
         */
        public static long wow(final long a, final long b) {
            final long n = (a ^ (b << 39 | b >>> 25)) * (b ^ (a << 39 | a >>> 25));
            return n ^ (n >>> 32);
        }

        public long hash64(final boolean[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  result      * 0xEBEDEED9D803C815L
                        + (data[i]     ? 0xD96EB1A810CAAF5FL : 0xCAAF5FD96EB1A810L)
                        + (data[i + 1] ? 0xC862B36DAF790DD5L : 0x790DD5C862B36DAFL)
                        + (data[i + 2] ? 0xB8ACD90C142FE10BL : 0x2FE10BB8ACD90C14L)
                        + (data[i + 3] ? 0xAA324F90DED86B69L : 0xD86B69AA324F90DEL)
                        + (data[i + 4] ? 0x9CDA5E693FEA10AFL : 0xEA10AF9CDA5E693FL)
                        + (data[i + 5] ? 0x908E3D2C82567A73L : 0x567A73908E3D2C82L)
                        + (data[i + 6] ? 0x8538ECB5BD456EA3L : 0x456EA38538ECB5BDL)
                        + (data[i + 7] ? 0xD1B54A32D192ED03L : 0x92ED03D1B54A32D1L)
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + (data[i] ? 0xEBEDEED9D803C815L : 0xD9D803C815EBEDEEL);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }
        public long hash64(final byte[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final short[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final char[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final CharSequence data) {
            if (data == null) return 0;
            final int length = data.length();
            long result = seed ^ length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final int[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long result = seed ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final long[] data) {
            if (data == null) return 0;
            long seed = this.seed, a = this.seed + b4, b = this.seed + b3, c = this.seed + b2, d = this.seed + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return seed - (seed >>> 31) + (seed << 33);
        }
        public long hash64(final float[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * floatToIntBits(data[i])
                        + 0xC862B36DAF790DD5L * floatToIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * floatToIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * floatToIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * floatToIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * floatToIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * floatToIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * floatToIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + floatToIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }
        public long hash64(final double[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * doubleToMixedIntBits(data[i])
                        + 0xC862B36DAF790DD5L * doubleToMixedIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * doubleToMixedIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * doubleToMixedIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * doubleToMixedIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * doubleToMixedIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * doubleToMixedIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * doubleToMixedIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + doubleToMixedIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);

            long result = seed ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());

            long result = seed ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }


        public long hash64(final char[][] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final int[][] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final long[][] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final CharSequence[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final CharSequence[]... data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = this.seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
            }
            seed = (seed ^ seed << 16) * (len ^ b0);
            return seed - (seed >>> 31) + (seed << 33);
        }

        public long hash64(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long result = seed ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data.get(i))
                        + 0xC862B36DAF790DD5L * hash(data.get(i + 1))
                        + 0xB8ACD90C142FE10BL * hash(data.get(i + 2))
                        + 0xAA324F90DED86B69L * hash(data.get(i + 3))
                        + 0x9CDA5E693FEA10AFL * hash(data.get(i + 4))
                        + 0x908E3D2C82567A73L * hash(data.get(i + 5))
                        + 0x8538ECB5BD456EA3L * hash(data.get(i + 6))
                        + 0xD1B54A32D192ED03L * hash(data.get(i + 7))
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data.get(i));
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);

        }

        public long hash64(final Object[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }

        public int hash(final boolean[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  result      * 0xEBEDEED9D803C815L
                        + (data[i]     ? 0xD96EB1A810CAAF5FL : 0xCAAF5FD96EB1A810L)
                        + (data[i + 1] ? 0xC862B36DAF790DD5L : 0x790DD5C862B36DAFL)
                        + (data[i + 2] ? 0xB8ACD90C142FE10BL : 0x2FE10BB8ACD90C14L)
                        + (data[i + 3] ? 0xAA324F90DED86B69L : 0xD86B69AA324F90DEL)
                        + (data[i + 4] ? 0x9CDA5E693FEA10AFL : 0xEA10AF9CDA5E693FL)
                        + (data[i + 5] ? 0x908E3D2C82567A73L : 0x567A73908E3D2C82L)
                        + (data[i + 6] ? 0x8538ECB5BD456EA3L : 0x456EA38538ECB5BDL)
                        + (data[i + 7] ? 0xD1B54A32D192ED03L : 0x92ED03D1B54A32D1L)
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + (data[i] ? 0xEBEDEED9D803C815L : 0xD9D803C815EBEDEEL);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }
        public int hash(final byte[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final short[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final char[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final CharSequence data) {
            if (data == null) return 0;
            final int length = data.length();
            long result = seed ^ length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final int[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long result = seed ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final long[] data) {
            if (data == null) return 0;
            long seed = this.seed, a = this.seed + b4, b = this.seed + b3, c = this.seed + b2, d = this.seed + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                seed += a + b + c + d;
            }
            seed += b5;
            switch (len & 3) {
                case 1: seed = wow(seed, b1 ^ data[len-1]); break;
                case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
                case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
            }
            seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
            return (int)(seed - (seed >>> 32));
        }

        public int hash(final float[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * floatToIntBits(data[i])
                        + 0xC862B36DAF790DD5L * floatToIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * floatToIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * floatToIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * floatToIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * floatToIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * floatToIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * floatToIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + floatToIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }
        public int hash(final double[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * doubleToMixedIntBits(data[i])
                        + 0xC862B36DAF790DD5L * doubleToMixedIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * doubleToMixedIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * doubleToMixedIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * doubleToMixedIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * doubleToMixedIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * doubleToMixedIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * doubleToMixedIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + doubleToMixedIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);

            long result = seed ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public int hash(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());

            long result = seed ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }


        public int hash(final char[][] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final int[][] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final long[][] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final CharSequence[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final CharSequence[]... data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long seed = this.seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                seed = mum(
                        mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                        mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
            }
            return (int) mum(seed ^ seed << 16, len ^ b0);
        }

        public int hash(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long result = seed ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data.get(i))
                        + 0xC862B36DAF790DD5L * hash(data.get(i + 1))
                        + 0xB8ACD90C142FE10BL * hash(data.get(i + 2))
                        + 0xAA324F90DED86B69L * hash(data.get(i + 3))
                        + 0x9CDA5E693FEA10AFL * hash(data.get(i + 4))
                        + 0x908E3D2C82567A73L * hash(data.get(i + 5))
                        + 0x8538ECB5BD456EA3L * hash(data.get(i + 6))
                        + 0xD1B54A32D192ED03L * hash(data.get(i + 7))
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data.get(i));
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);

        }

        public int hash(final Object[] data) {
            if (data == null) return 0;
            long result = seed ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(data[i])
                        + 0xC862B36DAF790DD5L * hash(data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(data[i + 2])
                        + 0xAA324F90DED86B69L * hash(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(data[i + 4])
                        + 0x908E3D2C82567A73L * hash(data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public int hash(final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + seed) * 0x9E3779B97F4A7C15L >>> 32);
        }

































        public static long hash64(final long seed, final boolean[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  result      * 0xEBEDEED9D803C815L
                        + (data[i]     ? 0xD96EB1A810CAAF5FL : 0xCAAF5FD96EB1A810L)
                        + (data[i + 1] ? 0xC862B36DAF790DD5L : 0x790DD5C862B36DAFL)
                        + (data[i + 2] ? 0xB8ACD90C142FE10BL : 0x2FE10BB8ACD90C14L)
                        + (data[i + 3] ? 0xAA324F90DED86B69L : 0xD86B69AA324F90DEL)
                        + (data[i + 4] ? 0x9CDA5E693FEA10AFL : 0xEA10AF9CDA5E693FL)
                        + (data[i + 5] ? 0x908E3D2C82567A73L : 0x567A73908E3D2C82L)
                        + (data[i + 6] ? 0x8538ECB5BD456EA3L : 0x456EA38538ECB5BDL)
                        + (data[i + 7] ? 0xD1B54A32D192ED03L : 0x92ED03D1B54A32D1L)
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + (data[i] ? 0xEBEDEED9D803C815L : 0xD9D803C815EBEDEEL);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }
        public static long hash64(final long seed, final byte[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final short[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final char[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final CharSequence data) {
            if (data == null) return 0;
            final int length = data.length();
            long result = randomize(seed) ^ length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final int[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long result = randomize(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final long[] data) {
            if (data == null) return 0;
            long s = randomize(seed), a = s + b4, b = s + b3, c = s + b2, d = s + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                s += a + b + c + d;
            }
            s += b5;
            switch (len & 3) {
                case 1: s = wow(s, b1 ^ data[len-1]); break;
                case 2: s = wow(s + data[len-2], b2 + data[len-1]); break;
                case 3: s = wow(s + data[len-3], b2 + data[len-2]) ^ wow(s + data[len-1], s ^ b3); break;
            }
            s = (s ^ s << 16) * (len ^ b0 ^ s >>> 32);
            return s - (s >>> 31) + (s << 33);
        }
        public static long hash64(final long seed, final float[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * floatToIntBits(data[i])
                        + 0xC862B36DAF790DD5L * floatToIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * floatToIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * floatToIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * floatToIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * floatToIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * floatToIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * floatToIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + floatToIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }
        public static long hash64(final long seed, final double[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * doubleToMixedIntBits(data[i])
                        + 0xC862B36DAF790DD5L * doubleToMixedIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * doubleToMixedIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * doubleToMixedIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * doubleToMixedIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * doubleToMixedIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * doubleToMixedIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * doubleToMixedIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + doubleToMixedIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);

            long result = randomize(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());

            long result = randomize(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }


        public static long hash64(final long seed, final char[][] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final int[][] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final long[][] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final CharSequence[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long s = randomize(seed);
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                s = mum(
                        mum(hash(seed, it.next()) ^ b1, (it.hasNext() ? hash(seed, it.next()) ^ b2 ^ ++len : b2)) + s,
                        mum((it.hasNext() ? hash(seed, it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(seed, it.next()) ^ b4 ^ ++len : b4)));
            }
            s = (s ^ s << 16) * (len ^ b0);
            return s - (s >>> 31) + (s << 33);
        }

        public static long hash64(final long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long result = randomize(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data.get(i))
                        + 0xC862B36DAF790DD5L * hash(seed, data.get(i + 1))
                        + 0xB8ACD90C142FE10BL * hash(seed, data.get(i + 2))
                        + 0xAA324F90DED86B69L * hash(seed, data.get(i + 3))
                        + 0x9CDA5E693FEA10AFL * hash(seed, data.get(i + 4))
                        + 0x908E3D2C82567A73L * hash(seed, data.get(i + 5))
                        + 0x8538ECB5BD456EA3L * hash(seed, data.get(i + 6))
                        + 0xD1B54A32D192ED03L * hash(seed, data.get(i + 7))
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data.get(i));
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);

        }

        public static long hash64(final long seed, final Object[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + randomize(seed)) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }

        public static int hash(final long seed, final boolean[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  result      * 0xEBEDEED9D803C815L
                        + (data[i]     ? 0xD96EB1A810CAAF5FL : 0xCAAF5FD96EB1A810L)
                        + (data[i + 1] ? 0xC862B36DAF790DD5L : 0x790DD5C862B36DAFL)
                        + (data[i + 2] ? 0xB8ACD90C142FE10BL : 0x2FE10BB8ACD90C14L)
                        + (data[i + 3] ? 0xAA324F90DED86B69L : 0xD86B69AA324F90DEL)
                        + (data[i + 4] ? 0x9CDA5E693FEA10AFL : 0xEA10AF9CDA5E693FL)
                        + (data[i + 5] ? 0x908E3D2C82567A73L : 0x567A73908E3D2C82L)
                        + (data[i + 6] ? 0x8538ECB5BD456EA3L : 0x456EA38538ECB5BDL)
                        + (data[i + 7] ? 0xD1B54A32D192ED03L : 0x92ED03D1B54A32D1L)
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + (data[i] ? 0xEBEDEED9D803C815L : 0xD9D803C815EBEDEEL);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }
        public static int hash(final long seed, final byte[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final short[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final char[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final CharSequence data) {
            if (data == null) return 0;
            final int length = data.length();
            long result = randomize(seed) ^ length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final int[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long result = randomize(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final long[] data) {
            if (data == null) return 0;
            long s = randomize(seed), a = s + b4, b = s + b3, c = s + b2, d = s + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                s += a + b + c + d;
            }
            s += b5;
            switch (len & 3) {
                case 1: s = wow(s, b1 ^ data[len-1]); break;
                case 2: s = wow(s + data[len-2], b2 + data[len-1]); break;
                case 3: s = wow(s + data[len-3], b2 + data[len-2]) ^ wow(s + data[len-1], s ^ b3); break;
            }
            s = (s ^ s << 16) * (len ^ b0 ^ s >>> 32);
            return (int)(s - (s >>> 32));
        }

        public static int hash(final long seed, final float[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * floatToIntBits(data[i])
                        + 0xC862B36DAF790DD5L * floatToIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * floatToIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * floatToIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * floatToIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * floatToIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * floatToIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * floatToIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + floatToIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }
        public static int hash(final long seed, final double[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * doubleToMixedIntBits(data[i])
                        + 0xC862B36DAF790DD5L * doubleToMixedIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * doubleToMixedIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * doubleToMixedIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * doubleToMixedIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * doubleToMixedIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * doubleToMixedIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * doubleToMixedIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + doubleToMixedIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static int hash(final long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);

            long result = randomize(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static int hash(final long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());

            long result = randomize(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }


        public static int hash(final long seed, final char[][] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final int[][] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final long[][] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final CharSequence[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long s = randomize(seed);
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                s = mum(
                        mum(hash(seed, it.next()) ^ b1, (it.hasNext() ? hash(seed, it.next()) ^ b2 ^ ++len : b2)) + s,
                        mum((it.hasNext() ? hash(seed, it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(seed, it.next()) ^ b4 ^ ++len : b4)));
            }
            return (int) mum(s ^ s << 16, len ^ b0);
        }

        public static int hash(final long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long result = randomize(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data.get(i))
                        + 0xC862B36DAF790DD5L * hash(seed, data.get(i + 1))
                        + 0xB8ACD90C142FE10BL * hash(seed, data.get(i + 2))
                        + 0xAA324F90DED86B69L * hash(seed, data.get(i + 3))
                        + 0x9CDA5E693FEA10AFL * hash(seed, data.get(i + 4))
                        + 0x908E3D2C82567A73L * hash(seed, data.get(i + 5))
                        + 0x8538ECB5BD456EA3L * hash(seed, data.get(i + 6))
                        + 0xD1B54A32D192ED03L * hash(seed, data.get(i + 7))
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data.get(i));
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);

        }

        public static int hash(final long seed, final Object[] data) {
            if (data == null) return 0;
            long result = randomize(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + randomize(seed)) * 0x9E3779B97F4A7C15L >>> 32);
        }
    }
    public static final class Mx {

        public static final long C = 0xBEA225F9EB34556DL;
        private final long seed;

        public Mx(){
            this.seed = 0xC4CEB9FE1A85EC53L;
        }
        public Mx(long seed)
        {
            this.seed = mix(~seed);
        }

        /**
         * This is Jon Kagstrom's <a href="https://github.com/jonmaiga/mx3/blob/master/mx3.h">MX3 Mixer v3</a>.
         * @param state any long
         * @return any long
         */
        public static long mix(long state) {
            state ^= state >>> 32;
            state *= C;
            state ^= state >>> 29;
            state *= C;
            state ^= state >>> 32;
            state *= C;
            return state ^ state >>> 29;
        }

        public static long mixStream(long h, long x) {
            x *= C;
            x ^= x >>> 37;
            h += x;
            h *= C;
//            x *= C;
//            x ^= x >>> 39;
//            h += x * C;
//            h *= C;
            return h;
        }

        public static long mixStream(long h, long a, long b, long c, long d) {
            a *= C;
            b *= C;
            c *= C;
            d *= C;
            a ^= a >>> 37;
            b ^= b >>> 37;
            c ^= c >>> 37;
            d ^= d >>> 37;
            h += a;
            h *= C;
            h += b;
            h *= C;
            h += c;
            h *= C;
            h += d;
            h *= C;
//            a *= C;
//            b *= C;
//            c *= C;
//            d *= C;
//            a ^= a >>> 39;
//            b ^= b >>> 39;
//            c ^= c >>> 39;
//            d ^= d >>> 39;
//            h += a * C;
//            h *= C;
//            h += b * C;
//            h *= C;
//            h += c * C;
//            h *= C;
//            h += d * C;
//            h *= C;
            return h;
        }
        public Mx(final CharSequence seed)
        {
            this(Water.hash64(seed));
        }

        public static final Mx alpha = new Mx("alpha"), beta = new Mx("beta"), gamma = new Mx("gamma"),
                delta = new Mx("delta"), epsilon = new Mx("epsilon"), zeta = new Mx("zeta"),
                eta = new Mx("eta"), theta = new Mx("theta"), iota = new Mx("iota"),
                kappa = new Mx("kappa"), lambda = new Mx("lambda"), mu = new Mx("mu"),
                nu = new Mx("nu"), xi = new Mx("xi"), omicron = new Mx("omicron"), pi = new Mx("pi"),
                rho = new Mx("rho"), sigma = new Mx("sigma"), tau = new Mx("tau"),
                upsilon = new Mx("upsilon"), phi = new Mx("phi"), chi = new Mx("chi"), psi = new Mx("psi"),
                omega = new Mx("omega"),
                alpha_ = new Mx("ALPHA"), beta_ = new Mx("BETA"), gamma_ = new Mx("GAMMA"),
                delta_ = new Mx("DELTA"), epsilon_ = new Mx("EPSILON"), zeta_ = new Mx("ZETA"),
                eta_ = new Mx("ETA"), theta_ = new Mx("THETA"), iota_ = new Mx("IOTA"),
                kappa_ = new Mx("KAPPA"), lambda_ = new Mx("LAMBDA"), mu_ = new Mx("MU"),
                nu_ = new Mx("NU"), xi_ = new Mx("XI"), omicron_ = new Mx("OMICRON"), pi_ = new Mx("PI"),
                rho_ = new Mx("RHO"), sigma_ = new Mx("SIGMA"), tau_ = new Mx("TAU"),
                upsilon_ = new Mx("UPSILON"), phi_ = new Mx("PHI"), chi_ = new Mx("CHI"), psi_ = new Mx("PSI"),
                omega_ = new Mx("OMEGA"),
                baal = new Mx("baal"), agares = new Mx("agares"), vassago = new Mx("vassago"), samigina = new Mx("samigina"),
                marbas = new Mx("marbas"), valefor = new Mx("valefor"), amon = new Mx("amon"), barbatos = new Mx("barbatos"),
                paimon = new Mx("paimon"), buer = new Mx("buer"), gusion = new Mx("gusion"), sitri = new Mx("sitri"),
                beleth = new Mx("beleth"), leraje = new Mx("leraje"), eligos = new Mx("eligos"), zepar = new Mx("zepar"),
                botis = new Mx("botis"), bathin = new Mx("bathin"), sallos = new Mx("sallos"), purson = new Mx("purson"),
                marax = new Mx("marax"), ipos = new Mx("ipos"), aim = new Mx("aim"), naberius = new Mx("naberius"),
                glasya_labolas = new Mx("glasya_labolas"), bune = new Mx("bune"), ronove = new Mx("ronove"), berith = new Mx("berith"),
                astaroth = new Mx("astaroth"), forneus = new Mx("forneus"), foras = new Mx("foras"), asmoday = new Mx("asmoday"),
                gaap = new Mx("gaap"), furfur = new Mx("furfur"), marchosias = new Mx("marchosias"), stolas = new Mx("stolas"),
                phenex = new Mx("phenex"), halphas = new Mx("halphas"), malphas = new Mx("malphas"), raum = new Mx("raum"),
                focalor = new Mx("focalor"), vepar = new Mx("vepar"), sabnock = new Mx("sabnock"), shax = new Mx("shax"),
                vine = new Mx("vine"), bifrons = new Mx("bifrons"), vual = new Mx("vual"), haagenti = new Mx("haagenti"),
                crocell = new Mx("crocell"), furcas = new Mx("furcas"), balam = new Mx("balam"), alloces = new Mx("alloces"),
                caim = new Mx("caim"), murmur = new Mx("murmur"), orobas = new Mx("orobas"), gremory = new Mx("gremory"),
                ose = new Mx("ose"), amy = new Mx("amy"), orias = new Mx("orias"), vapula = new Mx("vapula"),
                zagan = new Mx("zagan"), valac = new Mx("valac"), andras = new Mx("andras"), flauros = new Mx("flauros"),
                andrealphus = new Mx("andrealphus"), kimaris = new Mx("kimaris"), amdusias = new Mx("amdusias"), belial = new Mx("belial"),
                decarabia = new Mx("decarabia"), seere = new Mx("seere"), dantalion = new Mx("dantalion"), andromalius = new Mx("andromalius"),
                baal_ = new Mx("BAAL"), agares_ = new Mx("AGARES"), vassago_ = new Mx("VASSAGO"), samigina_ = new Mx("SAMIGINA"),
                marbas_ = new Mx("MARBAS"), valefor_ = new Mx("VALEFOR"), amon_ = new Mx("AMON"), barbatos_ = new Mx("BARBATOS"),
                paimon_ = new Mx("PAIMON"), buer_ = new Mx("BUER"), gusion_ = new Mx("GUSION"), sitri_ = new Mx("SITRI"),
                beleth_ = new Mx("BELETH"), leraje_ = new Mx("LERAJE"), eligos_ = new Mx("ELIGOS"), zepar_ = new Mx("ZEPAR"),
                botis_ = new Mx("BOTIS"), bathin_ = new Mx("BATHIN"), sallos_ = new Mx("SALLOS"), purson_ = new Mx("PURSON"),
                marax_ = new Mx("MARAX"), ipos_ = new Mx("IPOS"), aim_ = new Mx("AIM"), naberius_ = new Mx("NABERIUS"),
                glasya_labolas_ = new Mx("GLASYA_LABOLAS"), bune_ = new Mx("BUNE"), ronove_ = new Mx("RONOVE"), berith_ = new Mx("BERITH"),
                astaroth_ = new Mx("ASTAROTH"), forneus_ = new Mx("FORNEUS"), foras_ = new Mx("FORAS"), asmoday_ = new Mx("ASMODAY"),
                gaap_ = new Mx("GAAP"), furfur_ = new Mx("FURFUR"), marchosias_ = new Mx("MARCHOSIAS"), stolas_ = new Mx("STOLAS"),
                phenex_ = new Mx("PHENEX"), halphas_ = new Mx("HALPHAS"), malphas_ = new Mx("MALPHAS"), raum_ = new Mx("RAUM"),
                focalor_ = new Mx("FOCALOR"), vepar_ = new Mx("VEPAR"), sabnock_ = new Mx("SABNOCK"), shax_ = new Mx("SHAX"),
                vine_ = new Mx("VINE"), bifrons_ = new Mx("BIFRONS"), vual_ = new Mx("VUAL"), haagenti_ = new Mx("HAAGENTI"),
                crocell_ = new Mx("CROCELL"), furcas_ = new Mx("FURCAS"), balam_ = new Mx("BALAM"), alloces_ = new Mx("ALLOCES"),
                caim_ = new Mx("CAIM"), murmur_ = new Mx("MURMUR"), orobas_ = new Mx("OROBAS"), gremory_ = new Mx("GREMORY"),
                ose_ = new Mx("OSE"), amy_ = new Mx("AMY"), orias_ = new Mx("ORIAS"), vapula_ = new Mx("VAPULA"),
                zagan_ = new Mx("ZAGAN"), valac_ = new Mx("VALAC"), andras_ = new Mx("ANDRAS"), flauros_ = new Mx("FLAUROS"),
                andrealphus_ = new Mx("ANDREALPHUS"), kimaris_ = new Mx("KIMARIS"), amdusias_ = new Mx("AMDUSIAS"), belial_ = new Mx("BELIAL"),
                decarabia_ = new Mx("DECARABIA"), seere_ = new Mx("SEERE"), dantalion_ = new Mx("DANTALION"), andromalius_ = new Mx("ANDROMALIUS")
                ;
        /**
         * Has a length of 192, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Mx[] predefined = new Mx[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
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
                andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_};

        /**
         * Big constant 0.
         */
        public static final long b0 = 0xA0761D6478BD642FL;
        /**
         * Big constant 1.
         */
        public static final long b1 = 0xE7037ED1A0B428DBL;
        /**
         * Big constant 2.
         */
        public static final long b2 = 0x8EBC6AF09C88C6E3L;
        /**
         * Big constant 3.
         */
        public static final long b3 = 0x589965CC75374CC3L;
        /**
         * Big constant 4.
         */
        public static final long b4 = 0x1D8E4E27C47D124FL;
        /**
         * Big constant 5.
         */
        public static final long b5 = 0xEB44ACCAB455D165L;

        /**
         * A slower but higher-quality "mum function" that can take two arbitrary longs (with any
         * of their 64 bits containing relevant data) instead of mum's 32-bit sections of its inputs, and outputs a
         * 64-bit result that can have any of its bits used.
         * <br>
         * This was changed so it distributes bits from both inputs a little better on July 6, 2019.
         * @param a any long
         * @param b any long
         * @return a sort-of randomized output dependent on both inputs
         */
        public static long wow(final long a, final long b) {
            final long n = (a ^ (b << 39 | b >>> 25)) * (b ^ (a << 39 | a >>> 25));
            return n ^ (n >>> 32);
        }

        public long hash64(final char[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data[  i], data[i+1], data[i+2], data[i+3]);
                h = mixStream(h, data[i+4], data[i+5], data[i+6], data[i+7]);
            }
            for (; i < len; i++) {
                h = mixStream(h, data[i]);
            }
            return mix(h);
        }

        public long hash64(final CharSequence data) {
            if (data == null) return 0;
            int i = 0, len = data.length();
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data.charAt(  i), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
                h = mixStream(h, data.charAt(i+4), data.charAt(i+5), data.charAt(i+6), data.charAt(i+7));
            }
            for (; i < len; i++) {
                h = mixStream(h, data.charAt(i));
            }
            return mix(h);
        }

        public long hash64(final int[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data[  i], data[i+1], data[i+2], data[i+3]);
                h = mixStream(h, data[i+4], data[i+5], data[i+6], data[i+7]);
            }
            for (; i < len; i++) {
                h = mixStream(h, data[i]);
            }
            return mix(h);
        }

        public long hash64(final long[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data[  i], data[i+1], data[i+2], data[i+3]);
                h = mixStream(h, data[i+4], data[i+5], data[i+6], data[i+7]);
            }
            for (; i < len; i++) {
                h = mixStream(h, data[i]);
            }
            return mix(h);
        }

        public long hash64(final double[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, doubleToRawLongBits(data[  i]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]));
                h = mixStream(h, doubleToRawLongBits(data[i+4]), doubleToRawLongBits(data[i+5]), doubleToRawLongBits(data[i+6]), doubleToRawLongBits(data[i+7]));
            }
            for (; i < len; i++) {
                h = mixStream(h, doubleToRawLongBits(data[i]));
            }
            return mix(h);
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }

        public int hash(final char[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data[  i], data[i+1], data[i+2], data[i+3]);
                h = mixStream(h, data[i+4], data[i+5], data[i+6], data[i+7]);
            }
            for (; i < len; i++) {
                h = mixStream(h, data[i]);
            }
            return (int) mix(h);
        }

        public int hash(final CharSequence data) {
            if (data == null) return 0;
            int i = 0, len = data.length();
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data.charAt(  i), data.charAt(i+1), data.charAt(i+2), data.charAt(i+3));
                h = mixStream(h, data.charAt(i+4), data.charAt(i+5), data.charAt(i+6), data.charAt(i+7));
            }
            for (; i < len; i++) {
                h = mixStream(h, data.charAt(i));
            }
            return (int)mix(h);
        }

        public int hash(final int[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data[  i], data[i+1], data[i+2], data[i+3]);
                h = mixStream(h, data[i+4], data[i+5], data[i+6], data[i+7]);
            }
            for (; i < len; i++) {
                h = mixStream(h, data[i]);
            }
            return (int) mix(h);
        }

        public int hash(final long[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, data[  i], data[i+1], data[i+2], data[i+3]);
                h = mixStream(h, data[i+4], data[i+5], data[i+6], data[i+7]);
            }
            for (; i < len; i++) {
                h = mixStream(h, data[i]);
            }
            return (int) mix(h);
        }

        public int hash(final double[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length;
            long h = mixStream(seed, len + 1);
            for (; i + 7 < len; i += 8) {

                h = mixStream(h, doubleToRawLongBits(data[  i]), doubleToRawLongBits(data[i+1]), doubleToRawLongBits(data[i+2]), doubleToRawLongBits(data[i+3]));
                h = mixStream(h, doubleToRawLongBits(data[i+4]), doubleToRawLongBits(data[i+5]), doubleToRawLongBits(data[i+6]), doubleToRawLongBits(data[i+7]));
            }
            for (; i < len; i++) {
                h = mixStream(h, doubleToRawLongBits(data[i]));
            }
            return (int)mix(h);
        }

        public int hash(final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + seed) * 0x9E3779B97F4A7C15L >>> 32);
        }

































        public static long hash64(final long seed, final boolean[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  result      * 0xEBEDEED9D803C815L
                        + (data[i]     ? 0xD96EB1A810CAAF5FL : 0xCAAF5FD96EB1A810L)
                        + (data[i + 1] ? 0xC862B36DAF790DD5L : 0x790DD5C862B36DAFL)
                        + (data[i + 2] ? 0xB8ACD90C142FE10BL : 0x2FE10BB8ACD90C14L)
                        + (data[i + 3] ? 0xAA324F90DED86B69L : 0xD86B69AA324F90DEL)
                        + (data[i + 4] ? 0x9CDA5E693FEA10AFL : 0xEA10AF9CDA5E693FL)
                        + (data[i + 5] ? 0x908E3D2C82567A73L : 0x567A73908E3D2C82L)
                        + (data[i + 6] ? 0x8538ECB5BD456EA3L : 0x456EA38538ECB5BDL)
                        + (data[i + 7] ? 0xD1B54A32D192ED03L : 0x92ED03D1B54A32D1L)
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + (data[i] ? 0xEBEDEED9D803C815L : 0xD9D803C815EBEDEEL);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }
        public static long hash64(final long seed, final byte[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final short[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final char[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final CharSequence data) {
            if (data == null) return 0;
            final int length = data.length();
            long result = mix(seed) ^ length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final int[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long result = mix(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final long[] data) {
            if (data == null) return 0;
            long s = mix(seed), a = s + b4, b = s + b3, c = s + b2, d = s + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                s += a + b + c + d;
            }
            s += b5;
            switch (len & 3) {
                case 1: s = wow(s, b1 ^ data[len-1]); break;
                case 2: s = wow(s + data[len-2], b2 + data[len-1]); break;
                case 3: s = wow(s + data[len-3], b2 + data[len-2]) ^ wow(s + data[len-1], s ^ b3); break;
            }
            s = (s ^ s << 16) * (len ^ b0 ^ s >>> 32);
            return s - (s >>> 31) + (s << 33);
        }
        public static long hash64(final long seed, final float[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * floatToIntBits(data[i])
                        + 0xC862B36DAF790DD5L * floatToIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * floatToIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * floatToIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * floatToIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * floatToIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * floatToIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * floatToIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + floatToIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }
        public static long hash64(final long seed, final double[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * doubleToMixedIntBits(data[i])
                        + 0xC862B36DAF790DD5L * doubleToMixedIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * doubleToMixedIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * doubleToMixedIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * doubleToMixedIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * doubleToMixedIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * doubleToMixedIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * doubleToMixedIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + doubleToMixedIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);

            long result = mix(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());

            long result = mix(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }


        public static long hash64(final long seed, final char[][] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final int[][] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final long[][] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final CharSequence[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long s = mix(seed);
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                s = mum(
                        mum(hash(seed, it.next()) ^ b1, (it.hasNext() ? hash(seed, it.next()) ^ b2 ^ ++len : b2)) + s,
                        mum((it.hasNext() ? hash(seed, it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(seed, it.next()) ^ b4 ^ ++len : b4)));
            }
            s = (s ^ s << 16) * (len ^ b0);
            return s - (s >>> 31) + (s << 33);
        }

        public static long hash64(final long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long result = mix(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data.get(i))
                        + 0xC862B36DAF790DD5L * hash(seed, data.get(i + 1))
                        + 0xB8ACD90C142FE10BL * hash(seed, data.get(i + 2))
                        + 0xAA324F90DED86B69L * hash(seed, data.get(i + 3))
                        + 0x9CDA5E693FEA10AFL * hash(seed, data.get(i + 4))
                        + 0x908E3D2C82567A73L * hash(seed, data.get(i + 5))
                        + 0x8538ECB5BD456EA3L * hash(seed, data.get(i + 6))
                        + 0xD1B54A32D192ED03L * hash(seed, data.get(i + 7))
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data.get(i));
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);

        }

        public static long hash64(final long seed, final Object[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (result ^ result >>> 28);
        }

        public static long hash64(final long seed, final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + mix(seed)) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }

        public static int hash(final long seed, final boolean[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  result      * 0xEBEDEED9D803C815L
                        + (data[i]     ? 0xD96EB1A810CAAF5FL : 0xCAAF5FD96EB1A810L)
                        + (data[i + 1] ? 0xC862B36DAF790DD5L : 0x790DD5C862B36DAFL)
                        + (data[i + 2] ? 0xB8ACD90C142FE10BL : 0x2FE10BB8ACD90C14L)
                        + (data[i + 3] ? 0xAA324F90DED86B69L : 0xD86B69AA324F90DEL)
                        + (data[i + 4] ? 0x9CDA5E693FEA10AFL : 0xEA10AF9CDA5E693FL)
                        + (data[i + 5] ? 0x908E3D2C82567A73L : 0x567A73908E3D2C82L)
                        + (data[i + 6] ? 0x8538ECB5BD456EA3L : 0x456EA38538ECB5BDL)
                        + (data[i + 7] ? 0xD1B54A32D192ED03L : 0x92ED03D1B54A32D1L)
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + (data[i] ? 0xEBEDEED9D803C815L : 0xD9D803C815EBEDEEL);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }
        public static int hash(final long seed, final byte[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final short[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final char[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final CharSequence data) {
            if (data == null) return 0;
            final int length = data.length();
            long result = mix(seed) ^ length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final int[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long result = mix(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final long[] data) {
            if (data == null) return 0;
            long s = mix(seed), a = s + b4, b = s + b3, c = s + b2, d = s + b1;
            final int len = data.length;
            for (int i = 3; i < len; i+=4) {
                a ^= data[i-3] * b1; a = (a << 23 | a >>> 41) * b3;
                b ^= data[i-2] * b2; b = (b << 25 | b >>> 39) * b4;
                c ^= data[i-1] * b3; c = (c << 29 | c >>> 35) * b5;
                d ^= data[i  ] * b4; d = (d << 31 | d >>> 33) * b1;
                s += a + b + c + d;
            }
            s += b5;
            switch (len & 3) {
                case 1: s = wow(s, b1 ^ data[len-1]); break;
                case 2: s = wow(s + data[len-2], b2 + data[len-1]); break;
                case 3: s = wow(s + data[len-3], b2 + data[len-2]) ^ wow(s + data[len-1], s ^ b3); break;
            }
            s = (s ^ s << 16) * (len ^ b0 ^ s >>> 32);
            return (int)(s - (s >>> 32));
        }

        public static int hash(final long seed, final float[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * floatToIntBits(data[i])
                        + 0xC862B36DAF790DD5L * floatToIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * floatToIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * floatToIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * floatToIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * floatToIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * floatToIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * floatToIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + floatToIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }
        public static int hash(final long seed, final double[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * doubleToMixedIntBits(data[i])
                        + 0xC862B36DAF790DD5L * doubleToMixedIntBits(data[i + 1])
                        + 0xB8ACD90C142FE10BL * doubleToMixedIntBits(data[i + 2])
                        + 0xAA324F90DED86B69L * doubleToMixedIntBits(data[i + 3])
                        + 0x9CDA5E693FEA10AFL * doubleToMixedIntBits(data[i + 4])
                        + 0x908E3D2C82567A73L * doubleToMixedIntBits(data[i + 5])
                        + 0x8538ECB5BD456EA3L * doubleToMixedIntBits(data[i + 6])
                        + 0xD1B54A32D192ED03L * doubleToMixedIntBits(data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + doubleToMixedIntBits(data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static int hash(final long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);

            long result = mix(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data[i]
                        + 0xC862B36DAF790DD5L * data[i + 1]
                        + 0xB8ACD90C142FE10BL * data[i + 2]
                        + 0xAA324F90DED86B69L * data[i + 3]
                        + 0x9CDA5E693FEA10AFL * data[i + 4]
                        + 0x908E3D2C82567A73L * data[i + 5]
                        + 0x8538ECB5BD456EA3L * data[i + 6]
                        + 0xD1B54A32D192ED03L * data[i + 7]
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data[i];
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static int hash(final long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());

            long result = mix(seed) ^ (len - start) * 0x9E3779B97F4A7C15L;
            int i = start;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * data.charAt(i)
                        + 0xC862B36DAF790DD5L * data.charAt(i + 1)
                        + 0xB8ACD90C142FE10BL * data.charAt(i + 2)
                        + 0xAA324F90DED86B69L * data.charAt(i + 3)
                        + 0x9CDA5E693FEA10AFL * data.charAt(i + 4)
                        + 0x908E3D2C82567A73L * data.charAt(i + 5)
                        + 0x8538ECB5BD456EA3L * data.charAt(i + 6)
                        + 0xD1B54A32D192ED03L * data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + data.charAt(i);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }


        public static int hash(final long seed, final char[][] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final int[][] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final long[][] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final CharSequence[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long s = mix(seed);
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                ++len;
                s = mum(
                        mum(hash(seed, it.next()) ^ b1, (it.hasNext() ? hash(seed, it.next()) ^ b2 ^ ++len : b2)) + s,
                        mum((it.hasNext() ? hash(seed, it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(seed, it.next()) ^ b4 ^ ++len : b4)));
            }
            return (int) mum(s ^ s << 16, len ^ b0);
        }

        public static int hash(final long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long result = mix(seed) ^ len * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < len; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data.get(i))
                        + 0xC862B36DAF790DD5L * hash(seed, data.get(i + 1))
                        + 0xB8ACD90C142FE10BL * hash(seed, data.get(i + 2))
                        + 0xAA324F90DED86B69L * hash(seed, data.get(i + 3))
                        + 0x9CDA5E693FEA10AFL * hash(seed, data.get(i + 4))
                        + 0x908E3D2C82567A73L * hash(seed, data.get(i + 5))
                        + 0x8538ECB5BD456EA3L * hash(seed, data.get(i + 6))
                        + 0xD1B54A32D192ED03L * hash(seed, data.get(i + 7))
                ;
            }
            for (; i < len; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data.get(i));
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);

        }

        public static int hash(final long seed, final Object[] data) {
            if (data == null) return 0;
            long result = mix(seed) ^ data.length * 0x9E3779B97F4A7C15L;
            int i = 0;
            for (; i + 7 < data.length; i += 8) {
                result =  0xEBEDEED9D803C815L * result
                        + 0xD96EB1A810CAAF5FL * hash(seed, data[i])
                        + 0xC862B36DAF790DD5L * hash(seed, data[i + 1])
                        + 0xB8ACD90C142FE10BL * hash(seed, data[i + 2])
                        + 0xAA324F90DED86B69L * hash(seed, data[i + 3])
                        + 0x9CDA5E693FEA10AFL * hash(seed, data[i + 4])
                        + 0x908E3D2C82567A73L * hash(seed, data[i + 5])
                        + 0x8538ECB5BD456EA3L * hash(seed, data[i + 6])
                        + 0xD1B54A32D192ED03L * hash(seed, data[i + 7])
                ;
            }
            for (; i < data.length; i++) {
                result = 0x9E3779B97F4A7C15L * result + hash(seed, data[i]);
            }
            result *= 0x94D049BB133111EBL;
            result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
            result *= 0x369DEA0F31A53F85L;
            result ^= result >>> 31;
            result *= 0xDB4F0B9175AE2165L;
            return (int)(result ^ result >>> 28);
        }

        public static int hash(final long seed, final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + mix(seed)) * 0x9E3779B97F4A7C15L >>> 32);
        }
    }
    public static final class Purple {

        public static final long C = 0x818102004182A025L;
        private final long seed;

        public Purple(){
            this.seed = 0xDB4F0B9175AE2165L;
        }
        public Purple(long seed)
        {
            this.seed = seed;
        }

        public Purple(final CharSequence seed)
        {
            this(Water.hash64(seed));
        }

        public static final Purple alpha = new Purple("alpha"), beta = new Purple("beta"), gamma = new Purple("gamma"),
                delta = new Purple("delta"), epsilon = new Purple("epsilon"), zeta = new Purple("zeta"),
                eta = new Purple("eta"), theta = new Purple("theta"), iota = new Purple("iota"),
                kappa = new Purple("kappa"), lambda = new Purple("lambda"), mu = new Purple("mu"),
                nu = new Purple("nu"), xi = new Purple("xi"), omicron = new Purple("omicron"), pi = new Purple("pi"),
                rho = new Purple("rho"), sigma = new Purple("sigma"), tau = new Purple("tau"),
                upsilon = new Purple("upsilon"), phi = new Purple("phi"), chi = new Purple("chi"), psi = new Purple("psi"),
                omega = new Purple("omega"),
                alpha_ = new Purple("ALPHA"), beta_ = new Purple("BETA"), gamma_ = new Purple("GAMMA"),
                delta_ = new Purple("DELTA"), epsilon_ = new Purple("EPSILON"), zeta_ = new Purple("ZETA"),
                eta_ = new Purple("ETA"), theta_ = new Purple("THETA"), iota_ = new Purple("IOTA"),
                kappa_ = new Purple("KAPPA"), lambda_ = new Purple("LAMBDA"), mu_ = new Purple("MU"),
                nu_ = new Purple("NU"), xi_ = new Purple("XI"), omicron_ = new Purple("OMICRON"), pi_ = new Purple("PI"),
                rho_ = new Purple("RHO"), sigma_ = new Purple("SIGMA"), tau_ = new Purple("TAU"),
                upsilon_ = new Purple("UPSILON"), phi_ = new Purple("PHI"), chi_ = new Purple("CHI"), psi_ = new Purple("PSI"),
                omega_ = new Purple("OMEGA"),
                baal = new Purple("baal"), agares = new Purple("agares"), vassago = new Purple("vassago"), samigina = new Purple("samigina"),
                marbas = new Purple("marbas"), valefor = new Purple("valefor"), amon = new Purple("amon"), barbatos = new Purple("barbatos"),
                paimon = new Purple("paimon"), buer = new Purple("buer"), gusion = new Purple("gusion"), sitri = new Purple("sitri"),
                beleth = new Purple("beleth"), leraje = new Purple("leraje"), eligos = new Purple("eligos"), zepar = new Purple("zepar"),
                botis = new Purple("botis"), bathin = new Purple("bathin"), sallos = new Purple("sallos"), purson = new Purple("purson"),
                marax = new Purple("marax"), ipos = new Purple("ipos"), aim = new Purple("aim"), naberius = new Purple("naberius"),
                glasya_labolas = new Purple("glasya_labolas"), bune = new Purple("bune"), ronove = new Purple("ronove"), berith = new Purple("berith"),
                astaroth = new Purple("astaroth"), forneus = new Purple("forneus"), foras = new Purple("foras"), asmoday = new Purple("asmoday"),
                gaap = new Purple("gaap"), furfur = new Purple("furfur"), marchosias = new Purple("marchosias"), stolas = new Purple("stolas"),
                phenex = new Purple("phenex"), halphas = new Purple("halphas"), malphas = new Purple("malphas"), raum = new Purple("raum"),
                focalor = new Purple("focalor"), vepar = new Purple("vepar"), sabnock = new Purple("sabnock"), shax = new Purple("shax"),
                vine = new Purple("vine"), bifrons = new Purple("bifrons"), vual = new Purple("vual"), haagenti = new Purple("haagenti"),
                crocell = new Purple("crocell"), furcas = new Purple("furcas"), balam = new Purple("balam"), alloces = new Purple("alloces"),
                caim = new Purple("caim"), murmur = new Purple("murmur"), orobas = new Purple("orobas"), gremory = new Purple("gremory"),
                ose = new Purple("ose"), amy = new Purple("amy"), orias = new Purple("orias"), vapula = new Purple("vapula"),
                zagan = new Purple("zagan"), valac = new Purple("valac"), andras = new Purple("andras"), flauros = new Purple("flauros"),
                andrealphus = new Purple("andrealphus"), kimaris = new Purple("kimaris"), amdusias = new Purple("amdusias"), belial = new Purple("belial"),
                decarabia = new Purple("decarabia"), seere = new Purple("seere"), dantalion = new Purple("dantalion"), andromalius = new Purple("andromalius"),
                baal_ = new Purple("BAAL"), agares_ = new Purple("AGARES"), vassago_ = new Purple("VASSAGO"), samigina_ = new Purple("SAMIGINA"),
                marbas_ = new Purple("MARBAS"), valefor_ = new Purple("VALEFOR"), amon_ = new Purple("AMON"), barbatos_ = new Purple("BARBATOS"),
                paimon_ = new Purple("PAIMON"), buer_ = new Purple("BUER"), gusion_ = new Purple("GUSION"), sitri_ = new Purple("SITRI"),
                beleth_ = new Purple("BELETH"), leraje_ = new Purple("LERAJE"), eligos_ = new Purple("ELIGOS"), zepar_ = new Purple("ZEPAR"),
                botis_ = new Purple("BOTIS"), bathin_ = new Purple("BATHIN"), sallos_ = new Purple("SALLOS"), purson_ = new Purple("PURSON"),
                marax_ = new Purple("MARAX"), ipos_ = new Purple("IPOS"), aim_ = new Purple("AIM"), naberius_ = new Purple("NABERIUS"),
                glasya_labolas_ = new Purple("GLASYA_LABOLAS"), bune_ = new Purple("BUNE"), ronove_ = new Purple("RONOVE"), berith_ = new Purple("BERITH"),
                astaroth_ = new Purple("ASTAROTH"), forneus_ = new Purple("FORNEUS"), foras_ = new Purple("FORAS"), asmoday_ = new Purple("ASMODAY"),
                gaap_ = new Purple("GAAP"), furfur_ = new Purple("FURFUR"), marchosias_ = new Purple("MARCHOSIAS"), stolas_ = new Purple("STOLAS"),
                phenex_ = new Purple("PHENEX"), halphas_ = new Purple("HALPHAS"), malphas_ = new Purple("MALPHAS"), raum_ = new Purple("RAUM"),
                focalor_ = new Purple("FOCALOR"), vepar_ = new Purple("VEPAR"), sabnock_ = new Purple("SABNOCK"), shax_ = new Purple("SHAX"),
                vine_ = new Purple("VINE"), bifrons_ = new Purple("BIFRONS"), vual_ = new Purple("VUAL"), haagenti_ = new Purple("HAAGENTI"),
                crocell_ = new Purple("CROCELL"), furcas_ = new Purple("FURCAS"), balam_ = new Purple("BALAM"), alloces_ = new Purple("ALLOCES"),
                caim_ = new Purple("CAIM"), murmur_ = new Purple("MURMUR"), orobas_ = new Purple("OROBAS"), gremory_ = new Purple("GREMORY"),
                ose_ = new Purple("OSE"), amy_ = new Purple("AMY"), orias_ = new Purple("ORIAS"), vapula_ = new Purple("VAPULA"),
                zagan_ = new Purple("ZAGAN"), valac_ = new Purple("VALAC"), andras_ = new Purple("ANDRAS"), flauros_ = new Purple("FLAUROS"),
                andrealphus_ = new Purple("ANDREALPHUS"), kimaris_ = new Purple("KIMARIS"), amdusias_ = new Purple("AMDUSIAS"), belial_ = new Purple("BELIAL"),
                decarabia_ = new Purple("DECARABIA"), seere_ = new Purple("SEERE"), dantalion_ = new Purple("DANTALION"), andromalius_ = new Purple("ANDROMALIUS")
                ;
        /**
         * Has a length of 192, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Purple[] predefined = new Purple[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
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
                andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_};

        public long hash64(final boolean[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += (data[i] ? 0x9E3779B97F4A7C15L : 0x789ABCDEFEDCBA92L) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }
        public long hash64(final byte[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final short[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final char[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final CharSequence data) {
            if (data == null) return 0;
            final int len = data.length();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final int[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final long[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }
        public long hash64(final float[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += floatToRawIntBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }
        public long hash64(final double[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += doubleToRawLongBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public long hash64(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }


        public long hash64(final char[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final int[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final long[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final CharSequence[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final CharSequence[]... data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long a = seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                a += hash64(it.next()) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data.get(i)) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final Object[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * C;
            return h - (h >>> 31) + (h << 33);
        }

        public int hash(final boolean[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += (data[i] ? 0x9E3779B97F4A7C15L : 0x789ABCDEFEDCBA92L) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }
        public int hash(final byte[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final short[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final char[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final CharSequence data) {
            if (data == null) return 0;
            final int len = data.length();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final int[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final long[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }
        public int hash(final float[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += floatToRawIntBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }
        public int hash(final double[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += doubleToRawLongBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return (int)a 64-bit hash code for the requested section of data
         */
        public int hash(final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return (int)a 64-bit hash code for the requested section of data
         */
        public int hash(final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }


        public int hash(final char[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final int[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final long[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final CharSequence[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final CharSequence[]... data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long a = seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                a += hash64(it.next()) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data.get(i)) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final Object[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public int hash(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * C;
            return (int)(h ^ h >>> 31);
        }

































        public static long hash64(final long seed, final boolean[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += (data[i] ? 0x9E3779B97F4A7C15L : 0x789ABCDEFEDCBA92L) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }
        public static long hash64(final long seed, final byte[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final short[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final char[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final CharSequence data) {
            if (data == null) return 0;
            final int len = data.length();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final int[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final long[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }
        public static long hash64(final long seed, final float[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += floatToRawIntBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }
        public static long hash64(final long seed, final double[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += doubleToRawLongBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return a 64-bit hash code for the requested section of data
         */
        public static long hash64(final long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }


        public static long hash64(final long seed, final char[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final int[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final long[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final CharSequence[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long a = seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                a += hash64(seed, it.next()) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data.get(i)) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final Object[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return a;
        }

        public static long hash64(final long seed, final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * C;
            return h - (h >>> 31) + (h << 33);
        }

        public static int hash(final long seed, final boolean[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += (data[i] ? 0x9E3779B97F4A7C15L : 0x789ABCDEFEDCBA92L) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }
        public static int hash(final long seed, final byte[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final short[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final char[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final CharSequence data) {
            if (data == null) return 0;
            final int len = data.length();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final int[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final int[] data, final int length) {
            if (data == null) return 0;
            final int len = Math.min(length, data.length);
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final long[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }
        public static int hash(final long seed, final float[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += floatToRawIntBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }
        public static int hash(final long seed, final double[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += doubleToRawLongBits(data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the char array to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return (int)a 64-bit hash code for the requested section of data
         */
        public static int hash(final long seed, final char[] data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length);
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data[i] * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        /**
         * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
         *
         * @param data  the String or other CharSequence to hash
         * @param start the start of the section to hash (inclusive)
         * @param end   the end of the section to hash (exclusive)
         * @return (int)a 64-bit hash code for the requested section of data
         */
        public static int hash(final long seed, final CharSequence data, final int start, final int end) {
            if (data == null || start >= end) return 0;
            final int len = Math.min(end, data.length());
            long a = seed;
            for (int i = start; i < len; i++) {
                a += data.charAt(i) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }


        public static int hash(final long seed, final char[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final int[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final long[][] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final CharSequence[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final CharSequence[]... data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final Iterable<? extends CharSequence> data) {
            if (data == null) return 0;
            long a = seed;
            final Iterator<? extends CharSequence> it = data.iterator();
            int len = 0;
            while (it.hasNext())
            {
                a += hash64(seed, it.next()) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final List<? extends CharSequence> data) {
            if (data == null) return 0;
            final int len = data.size();
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data.get(i)) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final Object[] data) {
            if (data == null) return 0;
            final int len = data.length;
            long a = seed;
            for (int i = 0; i < len; i++) {
                a += hash64(seed, data[i]) * C ^ (a << 17 | a >>> 47);
            }
            a ^= a >>> 27;
            a *= 0x3C79AC492BA7B653L;
            a ^= a >>> 33;
            a *= 0x1C69B3F74AC4AE35L;
            a ^= a >>> 27;
            return (int)a;
        }

        public static int hash(final long seed, final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * C;
            return (int)(h ^ h >>> 31);
        }
    }

    /**
     * From <a href="https://richardstartin.github.io/posts/collecting-rocks-and-benchmarks">Richard Startin's blog</a>,
     * originally by Peter Levart.
     */
    public static final class Levart {
        public static int hash_31(CharSequence data) {
            if (data == null)
                return 0;

            int i = 0;
            int len = data.length();
            int low = len - 7;
            int result = ~len;
            for (; i < low; i += 8) {
                result = 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * result
                        + 31 * 31 * 31 * 31 * 31 * 31 * 31 * data.charAt(i)
                        + 31 * 31 * 31 * 31 * 31 * 31 * data.charAt(i + 1)
                        + 31 * 31 * 31 * 31 * 31 * data.charAt(i + 2)
                        + 31 * 31 * 31 * 31 * data.charAt(i + 3)
                        + 31 * 31 * 31 * data.charAt(i + 4)
                        + 31 * 31 * data.charAt(i + 5)
                        + 31 * data.charAt(i + 6)
                        + data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 31 * result + data.charAt(i);
            }
            return result;
        }
        public static int hash_31(String[] data) {
            if (data == null)
                return 0;

            int i = 0;
            int len = data.length;
            int low = len - 7;
            int result = ~len;
            for (; i < low; i += 8) {
                result = 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * result
                        + 31 * 31 * 31 * 31 * 31 * 31 * 31 * hash_31(data[i])
                        + 31 * 31 * 31 * 31 * 31 * 31 * hash_31(data[i + 1])
                        + 31 * 31 * 31 * 31 * 31 * hash_31(data[i + 2])
                        + 31 * 31 * 31 * 31 * hash_31(data[i + 3])
                        + 31 * 31 * 31 * hash_31(data[i + 4])
                        + 31 * 31 * hash_31(data[i + 5])
                        + 31 * hash_31(data[i + 6])
                        + hash_31(data[i + 7])
                ;
            }
            for (; i < len; i++) {
                result = 31 * result + hash_31(data[i]);
            }
            return result;
        }
        public static int hash_109(CharSequence data) {
            if (data == null)
                return 0;

            int i = 0;
            int len = data.length();
            int low = len - 7;
            int result = ~len;
            for (; i < low; i += 8) {
                result = 109 * 109 * 109 * 109 * 109 * 109 * 109 * 109 * result
                        + 109 * 109 * 109 * 109 * 109 * 109 * 109 * data.charAt(i)
                        + 109 * 109 * 109 * 109 * 109 * 109 * data.charAt(i + 1)
                        + 109 * 109 * 109 * 109 * 109 * data.charAt(i + 2)
                        + 109 * 109 * 109 * 109 * data.charAt(i + 3)
                        + 109 * 109 * 109 * data.charAt(i + 4)
                        + 109 * 109 * data.charAt(i + 5)
                        + 109 * data.charAt(i + 6)
                        + data.charAt(i + 7)
                ;
            }
            for (; i < len; i++) {
                result = 109 * result + data.charAt(i);
            }
            return result;
        }
        public static int hash_109(String[] data) {
            if (data == null)
                return 0;

            int i = 0;
            int len = data.length;
            int low = len - 7;
            int result = ~len;
            for (; i < low; i += 8) {
                result = 109 * 109 * 109 * 109 * 109 * 109 * 109 * 109 * result
                        + 109 * 109 * 109 * 109 * 109 * 109 * 109 * hash_109(data[i])
                        + 109 * 109 * 109 * 109 * 109 * 109 * hash_109(data[i + 1])
                        + 109 * 109 * 109 * 109 * 109 * hash_109(data[i + 2])
                        + 109 * 109 * 109 * 109 * hash_109(data[i + 3])
                        + 109 * 109 * 109 * hash_109(data[i + 4])
                        + 109 * 109 * hash_109(data[i + 5])
                        + 109 * hash_109(data[i + 6])
                        + hash_109(data[i + 7])
                ;
            }
            for (; i < len; i++) {
                result = 109 * result + hash_109(data[i]);
            }
            return result;
        }
    }

    public static final class Tern {
        public long seed;

        public Tern(){
            this.seed = 0xC4CEB9FE1A85EC53L;
        }
        public Tern(long seed)
        {
            this.seed = seed;
        }

        public Tern(final CharSequence seed)
        {
            this(Water.hash64(seed));
        }

        public static final Tern alpha = new Tern("alpha"), beta = new Tern("beta"), gamma = new Tern("gamma"),
                delta = new Tern("delta"), epsilon = new Tern("epsilon"), zeta = new Tern("zeta"),
                eta = new Tern("eta"), theta = new Tern("theta"), iota = new Tern("iota"),
                kappa = new Tern("kappa"), lambda = new Tern("lambda"), mu = new Tern("mu"),
                nu = new Tern("nu"), xi = new Tern("xi"), omicron = new Tern("omicron"), pi = new Tern("pi"),
                rho = new Tern("rho"), sigma = new Tern("sigma"), tau = new Tern("tau"),
                upsilon = new Tern("upsilon"), phi = new Tern("phi"), chi = new Tern("chi"), psi = new Tern("psi"),
                omega = new Tern("omega"),
                alpha_ = new Tern("ALPHA"), beta_ = new Tern("BETA"), gamma_ = new Tern("GAMMA"),
                delta_ = new Tern("DELTA"), epsilon_ = new Tern("EPSILON"), zeta_ = new Tern("ZETA"),
                eta_ = new Tern("ETA"), theta_ = new Tern("THETA"), iota_ = new Tern("IOTA"),
                kappa_ = new Tern("KAPPA"), lambda_ = new Tern("LAMBDA"), mu_ = new Tern("MU"),
                nu_ = new Tern("NU"), xi_ = new Tern("XI"), omicron_ = new Tern("OMICRON"), pi_ = new Tern("PI"),
                rho_ = new Tern("RHO"), sigma_ = new Tern("SIGMA"), tau_ = new Tern("TAU"),
                upsilon_ = new Tern("UPSILON"), phi_ = new Tern("PHI"), chi_ = new Tern("CHI"), psi_ = new Tern("PSI"),
                omega_ = new Tern("OMEGA"),
                baal = new Tern("baal"), agares = new Tern("agares"), vassago = new Tern("vassago"), samigina = new Tern("samigina"),
                marbas = new Tern("marbas"), valefor = new Tern("valefor"), amon = new Tern("amon"), barbatos = new Tern("barbatos"),
                paimon = new Tern("paimon"), buer = new Tern("buer"), gusion = new Tern("gusion"), sitri = new Tern("sitri"),
                beleth = new Tern("beleth"), leraje = new Tern("leraje"), eligos = new Tern("eligos"), zepar = new Tern("zepar"),
                botis = new Tern("botis"), bathin = new Tern("bathin"), sallos = new Tern("sallos"), purson = new Tern("purson"),
                marax = new Tern("marax"), ipos = new Tern("ipos"), aim = new Tern("aim"), naberius = new Tern("naberius"),
                glasya_labolas = new Tern("glasya_labolas"), bune = new Tern("bune"), ronove = new Tern("ronove"), berith = new Tern("berith"),
                astaroth = new Tern("astaroth"), forneus = new Tern("forneus"), foras = new Tern("foras"), asmoday = new Tern("asmoday"),
                gaap = new Tern("gaap"), furfur = new Tern("furfur"), marchosias = new Tern("marchosias"), stolas = new Tern("stolas"),
                phenex = new Tern("phenex"), halphas = new Tern("halphas"), malphas = new Tern("malphas"), raum = new Tern("raum"),
                focalor = new Tern("focalor"), vepar = new Tern("vepar"), sabnock = new Tern("sabnock"), shax = new Tern("shax"),
                vine = new Tern("vine"), bifrons = new Tern("bifrons"), vual = new Tern("vual"), haagenti = new Tern("haagenti"),
                crocell = new Tern("crocell"), furcas = new Tern("furcas"), balam = new Tern("balam"), alloces = new Tern("alloces"),
                caim = new Tern("caim"), murmur = new Tern("murmur"), orobas = new Tern("orobas"), gremory = new Tern("gremory"),
                ose = new Tern("ose"), amy = new Tern("amy"), orias = new Tern("orias"), vapula = new Tern("vapula"),
                zagan = new Tern("zagan"), valac = new Tern("valac"), andras = new Tern("andras"), flauros = new Tern("flauros"),
                andrealphus = new Tern("andrealphus"), kimaris = new Tern("kimaris"), amdusias = new Tern("amdusias"), belial = new Tern("belial"),
                decarabia = new Tern("decarabia"), seere = new Tern("seere"), dantalion = new Tern("dantalion"), andromalius = new Tern("andromalius"),
                baal_ = new Tern("BAAL"), agares_ = new Tern("AGARES"), vassago_ = new Tern("VASSAGO"), samigina_ = new Tern("SAMIGINA"),
                marbas_ = new Tern("MARBAS"), valefor_ = new Tern("VALEFOR"), amon_ = new Tern("AMON"), barbatos_ = new Tern("BARBATOS"),
                paimon_ = new Tern("PAIMON"), buer_ = new Tern("BUER"), gusion_ = new Tern("GUSION"), sitri_ = new Tern("SITRI"),
                beleth_ = new Tern("BELETH"), leraje_ = new Tern("LERAJE"), eligos_ = new Tern("ELIGOS"), zepar_ = new Tern("ZEPAR"),
                botis_ = new Tern("BOTIS"), bathin_ = new Tern("BATHIN"), sallos_ = new Tern("SALLOS"), purson_ = new Tern("PURSON"),
                marax_ = new Tern("MARAX"), ipos_ = new Tern("IPOS"), aim_ = new Tern("AIM"), naberius_ = new Tern("NABERIUS"),
                glasya_labolas_ = new Tern("GLASYA_LABOLAS"), bune_ = new Tern("BUNE"), ronove_ = new Tern("RONOVE"), berith_ = new Tern("BERITH"),
                astaroth_ = new Tern("ASTAROTH"), forneus_ = new Tern("FORNEUS"), foras_ = new Tern("FORAS"), asmoday_ = new Tern("ASMODAY"),
                gaap_ = new Tern("GAAP"), furfur_ = new Tern("FURFUR"), marchosias_ = new Tern("MARCHOSIAS"), stolas_ = new Tern("STOLAS"),
                phenex_ = new Tern("PHENEX"), halphas_ = new Tern("HALPHAS"), malphas_ = new Tern("MALPHAS"), raum_ = new Tern("RAUM"),
                focalor_ = new Tern("FOCALOR"), vepar_ = new Tern("VEPAR"), sabnock_ = new Tern("SABNOCK"), shax_ = new Tern("SHAX"),
                vine_ = new Tern("VINE"), bifrons_ = new Tern("BIFRONS"), vual_ = new Tern("VUAL"), haagenti_ = new Tern("HAAGENTI"),
                crocell_ = new Tern("CROCELL"), furcas_ = new Tern("FURCAS"), balam_ = new Tern("BALAM"), alloces_ = new Tern("ALLOCES"),
                caim_ = new Tern("CAIM"), murmur_ = new Tern("MURMUR"), orobas_ = new Tern("OROBAS"), gremory_ = new Tern("GREMORY"),
                ose_ = new Tern("OSE"), amy_ = new Tern("AMY"), orias_ = new Tern("ORIAS"), vapula_ = new Tern("VAPULA"),
                zagan_ = new Tern("ZAGAN"), valac_ = new Tern("VALAC"), andras_ = new Tern("ANDRAS"), flauros_ = new Tern("FLAUROS"),
                andrealphus_ = new Tern("ANDREALPHUS"), kimaris_ = new Tern("KIMARIS"), amdusias_ = new Tern("AMDUSIAS"), belial_ = new Tern("BELIAL"),
                decarabia_ = new Tern("DECARABIA"), seere_ = new Tern("SEERE"), dantalion_ = new Tern("DANTALION"), andromalius_ = new Tern("ANDROMALIUS")
                ;
        /**
         * Has a length of 192, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Tern[] predefined = new Tern[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
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
                andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_};

        public long hash64(final char[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
               + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
               + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
               + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final CharSequence data) {
            if (data == null) return 0;
            int i = 0, len = data.length(), strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data.charAt(i    );
                long fb = c ^ data.charAt(i + 1);
                long fc = d ^ data.charAt(i + 2);
                long fd = a ^ data.charAt(i + 3);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data.charAt(i);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final int[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final long[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final double[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ doubleToRawLongBits(data[i    ]);
                long fb = c ^ doubleToRawLongBits(data[i + 1]);
                long fc = d ^ doubleToRawLongBits(data[i + 2]);
                long fd = a ^ doubleToRawLongBits(data[i + 3]);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = doubleToRawLongBits(data[i]);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }

        public int hash(final char[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final CharSequence data) {
            if (data == null) return 0;
            int i = 0, len = data.length(), strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data.charAt(i    );
                long fb = c ^ data.charAt(i + 1);
                long fc = d ^ data.charAt(i + 2);
                long fd = a ^ data.charAt(i + 3);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data.charAt(i);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final int[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final long[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final double[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ doubleToRawLongBits(data[i    ]);
                long fb = c ^ doubleToRawLongBits(data[i + 1]);
                long fc = d ^ doubleToRawLongBits(data[i + 2]);
                long fd = a ^ doubleToRawLongBits(data[i + 3]);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = doubleToRawLongBits(data[i]);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + seed) * 0x9E3779B97F4A7C15L >>> 32);
        }
    }
    public static final class Terra {
        public long seed;

        public Terra(){
            this.seed = 0xC4CEB9FE1A85EC53L;
        }
        public Terra(long seed)
        {
            this.seed = seed;
        }

        public Terra(final CharSequence seed)
        {
            this(Water.hash64(seed));
        }

        public static final Terra alpha = new Terra("alpha"), beta = new Terra("beta"), gamma = new Terra("gamma"),
                delta = new Terra("delta"), epsilon = new Terra("epsilon"), zeta = new Terra("zeta"),
                eta = new Terra("eta"), theta = new Terra("theta"), iota = new Terra("iota"),
                kappa = new Terra("kappa"), lambda = new Terra("lambda"), mu = new Terra("mu"),
                nu = new Terra("nu"), xi = new Terra("xi"), omicron = new Terra("omicron"), pi = new Terra("pi"),
                rho = new Terra("rho"), sigma = new Terra("sigma"), tau = new Terra("tau"),
                upsilon = new Terra("upsilon"), phi = new Terra("phi"), chi = new Terra("chi"), psi = new Terra("psi"),
                omega = new Terra("omega"),
                alpha_ = new Terra("ALPHA"), beta_ = new Terra("BETA"), gamma_ = new Terra("GAMMA"),
                delta_ = new Terra("DELTA"), epsilon_ = new Terra("EPSILON"), zeta_ = new Terra("ZETA"),
                eta_ = new Terra("ETA"), theta_ = new Terra("THETA"), iota_ = new Terra("IOTA"),
                kappa_ = new Terra("KAPPA"), lambda_ = new Terra("LAMBDA"), mu_ = new Terra("MU"),
                nu_ = new Terra("NU"), xi_ = new Terra("XI"), omicron_ = new Terra("OMICRON"), pi_ = new Terra("PI"),
                rho_ = new Terra("RHO"), sigma_ = new Terra("SIGMA"), tau_ = new Terra("TAU"),
                upsilon_ = new Terra("UPSILON"), phi_ = new Terra("PHI"), chi_ = new Terra("CHI"), psi_ = new Terra("PSI"),
                omega_ = new Terra("OMEGA"),
                baal = new Terra("baal"), agares = new Terra("agares"), vassago = new Terra("vassago"), samigina = new Terra("samigina"),
                marbas = new Terra("marbas"), valefor = new Terra("valefor"), amon = new Terra("amon"), barbatos = new Terra("barbatos"),
                paimon = new Terra("paimon"), buer = new Terra("buer"), gusion = new Terra("gusion"), sitri = new Terra("sitri"),
                beleth = new Terra("beleth"), leraje = new Terra("leraje"), eligos = new Terra("eligos"), zepar = new Terra("zepar"),
                botis = new Terra("botis"), bathin = new Terra("bathin"), sallos = new Terra("sallos"), purson = new Terra("purson"),
                marax = new Terra("marax"), ipos = new Terra("ipos"), aim = new Terra("aim"), naberius = new Terra("naberius"),
                glasya_labolas = new Terra("glasya_labolas"), bune = new Terra("bune"), ronove = new Terra("ronove"), berith = new Terra("berith"),
                astaroth = new Terra("astaroth"), forneus = new Terra("forneus"), foras = new Terra("foras"), asmoday = new Terra("asmoday"),
                gaap = new Terra("gaap"), furfur = new Terra("furfur"), marchosias = new Terra("marchosias"), stolas = new Terra("stolas"),
                phenex = new Terra("phenex"), halphas = new Terra("halphas"), malphas = new Terra("malphas"), raum = new Terra("raum"),
                focalor = new Terra("focalor"), vepar = new Terra("vepar"), sabnock = new Terra("sabnock"), shax = new Terra("shax"),
                vine = new Terra("vine"), bifrons = new Terra("bifrons"), vual = new Terra("vual"), haagenti = new Terra("haagenti"),
                crocell = new Terra("crocell"), furcas = new Terra("furcas"), balam = new Terra("balam"), alloces = new Terra("alloces"),
                caim = new Terra("caim"), murmur = new Terra("murmur"), orobas = new Terra("orobas"), gremory = new Terra("gremory"),
                ose = new Terra("ose"), amy = new Terra("amy"), orias = new Terra("orias"), vapula = new Terra("vapula"),
                zagan = new Terra("zagan"), valac = new Terra("valac"), andras = new Terra("andras"), flauros = new Terra("flauros"),
                andrealphus = new Terra("andrealphus"), kimaris = new Terra("kimaris"), amdusias = new Terra("amdusias"), belial = new Terra("belial"),
                decarabia = new Terra("decarabia"), seere = new Terra("seere"), dantalion = new Terra("dantalion"), andromalius = new Terra("andromalius"),
                baal_ = new Terra("BAAL"), agares_ = new Terra("AGARES"), vassago_ = new Terra("VASSAGO"), samigina_ = new Terra("SAMIGINA"),
                marbas_ = new Terra("MARBAS"), valefor_ = new Terra("VALEFOR"), amon_ = new Terra("AMON"), barbatos_ = new Terra("BARBATOS"),
                paimon_ = new Terra("PAIMON"), buer_ = new Terra("BUER"), gusion_ = new Terra("GUSION"), sitri_ = new Terra("SITRI"),
                beleth_ = new Terra("BELETH"), leraje_ = new Terra("LERAJE"), eligos_ = new Terra("ELIGOS"), zepar_ = new Terra("ZEPAR"),
                botis_ = new Terra("BOTIS"), bathin_ = new Terra("BATHIN"), sallos_ = new Terra("SALLOS"), purson_ = new Terra("PURSON"),
                marax_ = new Terra("MARAX"), ipos_ = new Terra("IPOS"), aim_ = new Terra("AIM"), naberius_ = new Terra("NABERIUS"),
                glasya_labolas_ = new Terra("GLASYA_LABOLAS"), bune_ = new Terra("BUNE"), ronove_ = new Terra("RONOVE"), berith_ = new Terra("BERITH"),
                astaroth_ = new Terra("ASTAROTH"), forneus_ = new Terra("FORNEUS"), foras_ = new Terra("FORAS"), asmoday_ = new Terra("ASMODAY"),
                gaap_ = new Terra("GAAP"), furfur_ = new Terra("FURFUR"), marchosias_ = new Terra("MARCHOSIAS"), stolas_ = new Terra("STOLAS"),
                phenex_ = new Terra("PHENEX"), halphas_ = new Terra("HALPHAS"), malphas_ = new Terra("MALPHAS"), raum_ = new Terra("RAUM"),
                focalor_ = new Terra("FOCALOR"), vepar_ = new Terra("VEPAR"), sabnock_ = new Terra("SABNOCK"), shax_ = new Terra("SHAX"),
                vine_ = new Terra("VINE"), bifrons_ = new Terra("BIFRONS"), vual_ = new Terra("VUAL"), haagenti_ = new Terra("HAAGENTI"),
                crocell_ = new Terra("CROCELL"), furcas_ = new Terra("FURCAS"), balam_ = new Terra("BALAM"), alloces_ = new Terra("ALLOCES"),
                caim_ = new Terra("CAIM"), murmur_ = new Terra("MURMUR"), orobas_ = new Terra("OROBAS"), gremory_ = new Terra("GREMORY"),
                ose_ = new Terra("OSE"), amy_ = new Terra("AMY"), orias_ = new Terra("ORIAS"), vapula_ = new Terra("VAPULA"),
                zagan_ = new Terra("ZAGAN"), valac_ = new Terra("VALAC"), andras_ = new Terra("ANDRAS"), flauros_ = new Terra("FLAUROS"),
                andrealphus_ = new Terra("ANDREALPHUS"), kimaris_ = new Terra("KIMARIS"), amdusias_ = new Terra("AMDUSIAS"), belial_ = new Terra("BELIAL"),
                decarabia_ = new Terra("DECARABIA"), seere_ = new Terra("SEERE"), dantalion_ = new Terra("DANTALION"), andromalius_ = new Terra("ANDROMALIUS")
                ;
        /**
         * Has a length of 192, which may be relevant if automatically choosing a predefined hash functor.
         */
        public static final Terra[] predefined = new Terra[]{alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota,
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
                andrealphus_, kimaris_, amdusias_, belial_, decarabia_, seere_, dantalion_, andromalius_};

        public long hash64(final char[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, stricter = len - 15, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < stricter; i += 16) {
                long fa = b ^ data[i      ] ^ (long)data[i + 0x1] << 16 ^ (long)data[i + 0x2] << 32 ^ (long)data[i + 0x3] << 48;
                long fb = c ^ data[i + 0x4] ^ (long)data[i + 0x5] << 16 ^ (long)data[i + 0x6] << 32 ^ (long)data[i + 0x7] << 48;
                long fc = d ^ data[i + 0x8] ^ (long)data[i + 0x9] << 16 ^ (long)data[i + 0xA] << 32 ^ (long)data[i + 0xB] << 48;
                long fd = a ^ data[i + 0xC] ^ (long)data[i + 0xD] << 16 ^ (long)data[i + 0xE] << 32 ^ (long)data[i + 0xF] << 48;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
               + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
               + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
               + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final CharSequence data) {
            if (data == null) return 0;
            int i = 0, len = data.length(), stricter = len - 15, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < stricter; i += 16) {
                long fa = b ^ data.charAt(i      ) ^ (long)data.charAt(i + 0x1) << 16 ^ (long)data.charAt(i + 0x2) << 32 ^ (long)data.charAt(i + 0x3) << 48;
                long fb = c ^ data.charAt(i + 0x4) ^ (long)data.charAt(i + 0x5) << 16 ^ (long)data.charAt(i + 0x6) << 32 ^ (long)data.charAt(i + 0x7) << 48;
                long fc = d ^ data.charAt(i + 0x8) ^ (long)data.charAt(i + 0x9) << 16 ^ (long)data.charAt(i + 0xA) << 32 ^ (long)data.charAt(i + 0xB) << 48;
                long fd = a ^ data.charAt(i + 0xC) ^ (long)data.charAt(i + 0xD) << 16 ^ (long)data.charAt(i + 0xE) << 32 ^ (long)data.charAt(i + 0xF) << 48;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < strict; i += 4) {
                long fa = b ^ data.charAt(i    );
                long fb = c ^ data.charAt(i + 1);
                long fc = d ^ data.charAt(i + 2);
                long fd = a ^ data.charAt(i + 3);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data.charAt(i);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final int[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, stricter = len - 7, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < stricter; i += 8) {
                long fa = b ^ (data[i      ] & 0xFFFFFFFFL) ^ (long)data[i + 0x1] << 32;
                long fb = c ^ (data[i + 0x2] & 0xFFFFFFFFL) ^ (long)data[i + 0x3] << 32;
                long fc = d ^ (data[i + 0x4] & 0xFFFFFFFFL) ^ (long)data[i + 0x5] << 32;
                long fd = a ^ (data[i + 0x6] & 0xFFFFFFFFL) ^ (long)data[i + 0x7] << 32;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < strict; i += 4) {
                long fa = b ^ (data[i    ] & 0xFFFFFFFFL);
                long fb = c ^ (data[i + 1] & 0xFFFFFFFFL);
                long fc = d ^ (data[i + 2] & 0xFFFFFFFFL);
                long fd = a ^ (data[i + 3] & 0xFFFFFFFFL);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = (data[i] & 0xFFFFFFFFL);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final long[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final double[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ doubleToRawLongBits(data[i    ]);
                long fb = c ^ doubleToRawLongBits(data[i + 1]);
                long fc = d ^ doubleToRawLongBits(data[i + 2]);
                long fd = a ^ doubleToRawLongBits(data[i + 3]);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = doubleToRawLongBits(data[i]);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return m;
        }

        public long hash64(final Object data) {
            if (data == null)
                return 0;
            final long h = (data.hashCode() + seed) * 0x9E3779B97F4A7C15L;
            return h - (h >>> 31) + (h << 33);
        }

        public int hash(final char[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, stricter = len - 15, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < stricter; i += 16) {
                long fa = b ^ data[i      ] ^ (long)data[i + 0x1] << 16 ^ (long)data[i + 0x2] << 32 ^ (long)data[i + 0x3] << 48;
                long fb = c ^ data[i + 0x4] ^ (long)data[i + 0x5] << 16 ^ (long)data[i + 0x6] << 32 ^ (long)data[i + 0x7] << 48;
                long fc = d ^ data[i + 0x8] ^ (long)data[i + 0x9] << 16 ^ (long)data[i + 0xA] << 32 ^ (long)data[i + 0xB] << 48;
                long fd = a ^ data[i + 0xC] ^ (long)data[i + 0xD] << 16 ^ (long)data[i + 0xE] << 32 ^ (long)data[i + 0xF] << 48;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final CharSequence data) {
            if (data == null) return 0;
            int i = 0, len = data.length(), stricter = len - 15, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < stricter; i += 16) {
                long fa = b ^ data.charAt(i      ) ^ (long)data.charAt(i + 0x1) << 16 ^ (long)data.charAt(i + 0x2) << 32 ^ (long)data.charAt(i + 0x3) << 48;
                long fb = c ^ data.charAt(i + 0x4) ^ (long)data.charAt(i + 0x5) << 16 ^ (long)data.charAt(i + 0x6) << 32 ^ (long)data.charAt(i + 0x7) << 48;
                long fc = d ^ data.charAt(i + 0x8) ^ (long)data.charAt(i + 0x9) << 16 ^ (long)data.charAt(i + 0xA) << 32 ^ (long)data.charAt(i + 0xB) << 48;
                long fd = a ^ data.charAt(i + 0xC) ^ (long)data.charAt(i + 0xD) << 16 ^ (long)data.charAt(i + 0xE) << 32 ^ (long)data.charAt(i + 0xF) << 48;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < strict; i += 4) {
                long fa = b ^ data.charAt(i    );
                long fb = c ^ data.charAt(i + 1);
                long fc = d ^ data.charAt(i + 2);
                long fd = a ^ data.charAt(i + 3);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data.charAt(i);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final int[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, stricter = len - 7, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < stricter; i += 8) {
                long fa = b ^ (data[i      ] & 0xFFFFFFFFL) ^ (long)data[i + 0x1] << 32;
                long fb = c ^ (data[i + 0x2] & 0xFFFFFFFFL) ^ (long)data[i + 0x3] << 32;
                long fc = d ^ (data[i + 0x4] & 0xFFFFFFFFL) ^ (long)data[i + 0x5] << 32;
                long fd = a ^ (data[i + 0x6] & 0xFFFFFFFFL) ^ (long)data[i + 0x7] << 32;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < strict; i += 4) {
                long fa = b ^ (data[i    ] & 0xFFFFFFFFL);
                long fb = c ^ (data[i + 1] & 0xFFFFFFFFL);
                long fc = d ^ (data[i + 2] & 0xFFFFFFFFL);
                long fd = a ^ (data[i + 3] & 0xFFFFFFFFL);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i] & 0xFFFFFFFFL;
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final long[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ data[i    ];
                long fb = c ^ data[i + 1];
                long fc = d ^ data[i + 2];
                long fd = a ^ data[i + 3];
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = data[i];
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final double[] data) {
            if (data == null) return 0;
            int i = 0, len = data.length, strict = len - 3;
            long a = seed ^ len, b = a + 0xF7C2EBC08F67F2B5L, c = ~a + 0x94D049BB133111EBL, d = ~b + 0x8538ECB5BD456EA3L;
            long m = 0xDB4F0B9175AE2165L ^ a;

            for (; i < strict; i += 4) {
                long fa = b ^ doubleToRawLongBits(data[i    ]);
                long fb = c ^ doubleToRawLongBits(data[i + 1]);
                long fc = d ^ doubleToRawLongBits(data[i + 2]);
                long fd = a ^ doubleToRawLongBits(data[i + 3]);
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            for (; i < len; i++) {
                long item = doubleToRawLongBits(data[i]);
                long fa = b ^ item;
                long fb = c ^ item;
                long fc = d ^ item;
                long fd = a ^ item;
                a += (fa << 25 | fa >>> 39);
                b += (fb << 44 | fb >>> 20);
                c += (fc << 37 | fc >>> 27);
                d += (fd << 18 | fd >>> 46);
                m += a ^ b ^ c ^ d;
                m = (m << 42 | m >>> 22);
            }
            m += (a ^ (a << 25 | a >>> 39) ^ (a << 38 | a >>> 26))
                    + (b ^ (b << 47 | b >>> 17) ^ (b << 19 | b >>> 45))
                    + (c ^ (c << 11 | c >>> 53) ^ (c << 58 | c >>>  6))
                    + (d ^ (d << 37 | d >>> 27) ^ (d << 21 | d >>> 43));

            m ^= m >>> 27;
            m *= 0x3C79AC492BA7B653L;
            m ^= m >>> 33;
            m *= 0x1C69B3F74AC4AE35L;
            m ^= m >>> 27;
            return (int)m;
        }

        public int hash(final Object data) {
            if (data == null) return 0;
            return (int)((data.hashCode() + seed) * 0x9E3779B97F4A7C15L >>> 32);
        }
    }
}