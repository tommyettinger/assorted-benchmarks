package com.github.tommyettinger.squidlib;

import squidpony.annotation.Beta;
import squidpony.squidmath.FastNoise;
import squidpony.squidmath.IPointHash;
import squidpony.squidmath.RNG;
import squidpony.squidmath.TangleRNG;

/**
 * An experimental way of speeding up {@link FastNoise} by using a large buffer of random bytes, and choosing a byte
 * from the buffer purely by the current seed and position (a simpler calculation than a hash). This currently gives a
 * modest speed boost to {@link #SIMPLEX}, {@link #PERLIN}, {@link #CUBIC}, and {@link #HONEY}, a smaller speed boost to
 * {@link #FOAM}, and no change for {@link #VALUE} or {@link #WHITE_NOISE}. This class only uses the least-significant
 * 20 bits of any seed in most cases; using unique non-negative seeds that are all less than 1000000 is one way to
 * ensure these bits are different. It should be interchangeable with FastNoise, but be aware that the first time this
 * class is imported, it will generate about a million random bytes using {@link TangleRNG}. Generating a million bytes
 * really doesn't take more time than the blink of an eye, but it does use 1MB of RAM for the class (not allocated per
 * object, at least). 
 * <br>
 * This is currently marked as Beta because it's unclear if it can be sped up further, or how that would happen, but if
 * an opportunity showed itself to accelerate XastNoise and change earlier results, the change would happen.
 * <br>
 * Created by Tommy Ettinger on 9/12/2020.
 */
@Beta
public class XastNoise extends FastNoise {
	/**
	 * A constructor that takes no parameters, and uses all default settings with a seed of 1337. An example call to
	 * this would be {@code new XastNoise()}, which makes noise with the seed 1337, a default frequency of 1.0f/32.0f, 1
	 * octave of Simplex noise (since this doesn't specify octave count, it always uses 1 even for the
	 * SIMPLEX_FRACTAL noiseType this uses, but you can call {@link #setFractalOctaves(int)} later to benefit from the
	 * fractal noiseType), and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
	 */
	public XastNoise() {
		super(1337 * 0xC13FB ^ 0x91E10DA5);
	}

	/**
	 * A constructor that takes only a parameter for the XastNoise's seed, which should produce different results for
	 * any different seeds. An example call to this would be {@code new XastNoise(1337)}, which makes noise with the
	 * seed 1337, a default frequency of 1.0f/32.0f, 1 octave of Simplex noise (since this doesn't specify octave count,
	 * it always uses 1 even for the SIMPLEX_FRACTAL noiseType this uses, but you can call
	 * {@link #setFractalOctaves(int)} later to benefit from the fractal noiseType), and normal lacunarity and gain
	 * (when unspecified, they are 2f and 0.5f).
	 *
	 * @param seed the int seed for the noise, which should significantly affect the produced noise
	 */
	public XastNoise(int seed) {
		super(seed * 0xC13FB ^ 0x91E10DA5);
	}

	/**
	 * A constructor that takes two parameters to specify the XastNoise from the start. An example call to this
	 * would be {@code new XastNoise(1337, 0.02f)}, which makes noise with the seed 1337, a lower
	 * frequency, 1 octave of Simplex noise (since this doesn't specify octave count, it always uses 1 even for the
	 * SIMPLEX_FRACTAL noiseType this uses, but you can call {@link #setFractalOctaves(int)} later to benefit from the
	 * fractal noiseType), and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
	 *
	 * @param seed      the int seed for the noise, which should significantly affect the produced noise
	 * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
	 */
	public XastNoise(int seed, float frequency) {
		super(seed * 0xC13FB ^ 0x91E10DA5, frequency);
	}

	/**
	 * A constructor that takes a few parameters to specify the XastNoise from the start. An example call to this
	 * would be {@code new XastNoise(1337, 0.02f, XastNoise.SIMPLEX)}, which makes noise with the seed 1337, a lower
	 * frequency, 1 octave of Simplex noise (since this doesn't specify octave count, it always uses 1 even for
	 * noiseTypes like SIMPLEX_FRACTAL, but using a fractal noiseType can make sense if you call
	 * {@link #setFractalOctaves(int)} later), and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
	 *
	 * @param seed      the int seed for the noise, which should significantly affect the produced noise
	 * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
	 * @param noiseType the noiseType, which should be a constant from this class (see {@link #setNoiseType(int)})
	 */
	public XastNoise(int seed, float frequency, int noiseType) {
		super(seed * 0xC13FB ^ 0x91E10DA5, frequency, noiseType);
	}

	/**
	 * A constructor that takes several parameters to specify the XastNoise from the start. An example call to this
	 * would be {@code new XastNoise(1337, 0.02f, XastNoise.SIMPLEX_FRACTAL, 4)}, which makes noise with the seed 1337, a lower
	 * frequency, 4 octaves of Simplex noise, and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
	 *
	 * @param seed      the int seed for the noise, which should significantly affect the produced noise
	 * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
	 * @param noiseType the noiseType, which should be a constant from this class (see {@link #setNoiseType(int)})
	 * @param octaves   how many octaves of noise to use when the noiseType is one of the _FRACTAL types
	 */
	public XastNoise(int seed, float frequency, int noiseType, int octaves) {
		super(seed * 0xC13FB ^ 0x91E10DA5, frequency, noiseType, octaves);
	}

	/**
	 * A constructor that takes a lot of parameters to specify the XastNoise from the start. An example call to this
	 * would be {@code new XastNoise(1337, 0.02f, XastNoise.SIMPLEX_FRACTAL, 4, 0.5f, 2f)}, which makes noise with a
	 * lower frequency, 4 octaves of Simplex noise, and the "inverse" effect on how those octaves work (which makes
	 * the extra added octaves be more significant to the final result and also have a lower frequency, while normally
	 * added octaves have a higher frequency and tend to have a minor effect on the large-scale shape of the noise).
	 *
	 * @param seed       the int seed for the noise, which should significantly affect the produced noise
	 * @param frequency  the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
	 * @param noiseType  the noiseType, which should be a constant from this class (see {@link #setNoiseType(int)})
	 * @param octaves    how many octaves of noise to use when the noiseType is one of the _FRACTAL types
	 * @param lacunarity typically 2.0, or 0.5 to change how extra octaves work (inverse mode)
	 * @param gain       typically 0.5, or 2.0 to change how extra octaves work (inverse mode)
	 */
	public XastNoise(int seed, float frequency, int noiseType, int octaves, float lacunarity, float gain) {
		super(seed * 0xC13FB ^ 0x91E10DA5, frequency, noiseType, octaves, lacunarity, gain);
	}

	/**
	 * Copy constructor; copies all non-temporary fields from  {@code other} into this. This uses the same reference to
	 * an {@link IPointHash} set with {@link #setPointHash(IPointHash)} and to another XastNoise set with
	 * {@link #setCellularNoiseLookup(FastNoise)}, but otherwise everything it copies is a primitive.
	 *
	 * @param other another FastNoise or XastNoise, which must not be null
	 */
	public XastNoise(FastNoise other) {
		super(other);
	}
	
	private static final byte[] BUFFER = new byte[0x100000];
	static {
		new RNG(new TangleRNG(0x01234567L, 0x89ABCDEFL)).nextBytes(BUFFER);
	}
	@Override
	protected int hash32(int x, int y, int z, int s) {
		return BUFFER[(s ^ x * 0xD1B55 ^ y * 0xABC99 ^ z * 0x8CB93) >>> 12] + s & 31;
	}

	@Override
	protected int hash256(int x, int y, int s) {
		return BUFFER[(s ^ x * 0xC13FB ^ y * 0x91E11) >>> 12] + s & 255;
	}

	@Override
	protected int hash256(int x, int y, int z, int s) {
		return BUFFER[(s ^ x * 0xD1B55 ^ y * 0xABC99 ^ z * 0x8CB93) >>> 12] + s & 255;
	}

	@Override
	protected int hash256(int x, int y, int z, int w, int s) {
		return BUFFER[(s ^ x * 0xDB4F1 ^ y * 0xBBE05 ^ z * 0xA0F2F ^ w * 0x89E19) >>> 12] + s & 255;
	}

	@Override
	protected int hash256(int x, int y, int z, int w, int u, int s) {
		return BUFFER[(s ^ x * 0xE19B1 ^ y * 0xC6D1D ^ z * 0xAF36D ^ w * 0x9A695 ^ u * 0x88141) >>> 12] + s & 255;
	}

	@Override
	protected int hash256(int x, int y, int z, int w, int u, int v, int s) {
		return BUFFER[(s ^ x * 0xE60E3 ^ y * 0xCEBD7 ^ z * 0xB9C9B ^ w * 0xA6F57 ^ u * 0x9609D ^ v * 0x86D51) >>> 12] + s & 255;
	}
	/*
            {0xC13FA9A902A6328FL, 0x91E10DA5C79E7B1DL},
            {0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L},
            {0xDB4F0B9175AE2165L, 0xBBE0563303A4615FL, 0xA0F2EC75A1FE1575L, 0x89E182857D9ED689L},
            {0xE19B01AA9D42C633L, 0xC6D1D6C8ED0C9631L, 0xAF36D01EF7518DBBL, 0x9A69443F36F710E7L, 0x881403B9339BD42DL},
            {0xE60E2B722B53AEEBL, 0xCEBD76D9EDB6A8EFL, 0xB9C9AA3A51D00B65L, 0xA6F5777F6F88983FL, 0x9609C71EB7D03F7BL,
                    0x86D516E50B04AB1BL},
 */

	@Override
	protected float valueNoise (int seed, float x, float y) {
		int xFloor = x >= 0 ? (int) x : (int) x - 1;
		x -= xFloor;
		x *= x * (3 - 2 * x);
		int yFloor = y >= 0 ? (int) y : (int) y - 1;
		y -= yFloor;
		y *= y * (3 - 2 * y);
		return ((1 - y) * ((1 - x) * hash256(xFloor, yFloor, seed) + x * hash256(xFloor + 1, yFloor, seed))
				+ y * ((1 - x) * hash256(xFloor, yFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, seed)))
				* 0.003921569f;
	}

	@Override
	protected float valueNoise (int seed, float x, float y, float z)
	{
		int xFloor = x >= 0 ? (int) x : (int) x - 1;
		x -= xFloor;
		x *= x * (3 - 2 * x);
		int yFloor = y >= 0 ? (int) y : (int) y - 1;
		y -= yFloor;
		y *= y * (3 - 2 * y);
		int zFloor = z >= 0 ? (int) z : (int) z - 1;
		z -= zFloor;
		z *= z * (3 - 2 * z);
		return ((1 - z) *
				((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, seed))
						+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, seed)))
				+ z *
				((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, seed))
						+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, seed)))
		) * 0.003921569f;
	}

	@Override
	protected float valueNoise(int seed, float x, float y, float z, float w)
	{
		int xFloor = x >= 0 ? (int) x : (int) x - 1;
		x -= xFloor;
		x *= x * (3 - 2 * x);
		int yFloor = y >= 0 ? (int) y : (int) y - 1;
		y -= yFloor;
		y *= y * (3 - 2 * y);
		int zFloor = z >= 0 ? (int) z : (int) z - 1;
		z -= zFloor;
		z *= z * (3 - 2 * z);
		int wFloor = w >= 0 ? (int) w : (int) w - 1;
		w -= wFloor;
		w *= w * (3 - 2 * w);
		return ((1 - w) *
				((1 - z) *
						((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, seed))
								+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, seed)))
						+ z *
						((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, seed))
								+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, seed))))
				+ (w *
				((1 - z) *
						((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, seed))
								+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, seed)))
						+ z *
						((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, seed))
								+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, seed)))
				))) * 0.003921569f;
	}
	
	@Override
	protected float valueNoise(int seed, float x, float y, float z, float w, float u) {
		int xFloor = x >= 0 ? (int) x : (int) x - 1;
		x -= xFloor;
		x *= x * (3 - 2 * x);
		int yFloor = y >= 0 ? (int) y : (int) y - 1;
		y -= yFloor;
		y *= y * (3 - 2 * y);
		int zFloor = z >= 0 ? (int) z : (int) z - 1;
		z -= zFloor;
		z *= z * (3 - 2 * z);
		int wFloor = w >= 0 ? (int) w : (int) w - 1;
		w -= wFloor;
		w *= w * (3 - 2 * w);
		int uFloor = u >= 0 ? (int) u : (int) u - 1;
		u -= uFloor;
		u *= u * (3 - 2 * u);
		return ((1 - u) *
				((1 - w) *
						((1 - z) *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, uFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, uFloor, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, uFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, uFloor, seed)))
								+ z *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, uFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, uFloor, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, uFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, uFloor, seed))))
						+ (w *
						((1 - z) *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, uFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, uFloor, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, uFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, uFloor, seed)))
								+ z *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, uFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, uFloor, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, uFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, uFloor, seed)))
						)))
				+ (u *
				((1 - w) *
						((1 - z) *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, uFloor + 1, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, uFloor + 1, seed)))
								+ z *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, uFloor + 1, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, uFloor + 1, seed))))
						+ (w *
						((1 - z) *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, uFloor + 1, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, uFloor + 1, seed)))
								+ z *
								((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, uFloor + 1, seed))
										+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, uFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, uFloor + 1, seed)))
						))))
		) * 0.003921569f;
	}
	
	@Override
	protected float valueNoise(int seed, float x, float y, float z, float w, float u, float v) {
		int xFloor = x >= 0 ? (int) x : (int) x - 1;
		x -= xFloor;
		x *= x * (3 - 2 * x);
		int yFloor = y >= 0 ? (int) y : (int) y - 1;
		y -= yFloor;
		y *= y * (3 - 2 * y);
		int zFloor = z >= 0 ? (int) z : (int) z - 1;
		z -= zFloor;
		z *= z * (3 - 2 * z);
		int wFloor = w >= 0 ? (int) w : (int) w - 1;
		w -= wFloor;
		w *= w * (3 - 2 * w);
		int uFloor = u >= 0 ? (int) u : (int) u - 1;
		u -= uFloor;
		u *= u * (3 - 2 * u);
		int vFloor = v >= 0 ? (int) v : (int) v - 1;
		v -= vFloor;
		v *= v * (3 - 2 * v);
		return ((1 - v) *
				((1 - u) *
						((1 - w) *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, uFloor, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, uFloor, vFloor, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, uFloor, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, uFloor, vFloor, seed))))
								+ (w *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, uFloor, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, uFloor, vFloor, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, uFloor, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, uFloor, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, uFloor, vFloor, seed)))
								)))
						+ (u *
						((1 - w) *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, uFloor + 1, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, uFloor + 1, vFloor, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, uFloor + 1, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, uFloor + 1, vFloor, seed))))
								+ (w *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, uFloor + 1, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, uFloor + 1, vFloor, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, uFloor + 1, vFloor, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, uFloor + 1, vFloor, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, uFloor + 1, vFloor, seed)))
								)))))
				+ (v *
				((1 - u) *
						((1 - w) *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, uFloor, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, uFloor, vFloor + 1, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, uFloor, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, uFloor, vFloor + 1, seed))))
								+ (w *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, uFloor, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, uFloor, vFloor + 1, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, uFloor, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, uFloor, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, uFloor, vFloor + 1, seed)))
								)))
						+ (u *
						((1 - w) *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor, uFloor + 1, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor, uFloor + 1, vFloor + 1, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor, uFloor + 1, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor, uFloor + 1, vFloor + 1, seed))))
								+ (w *
								((1 - z) *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor, wFloor + 1, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor, wFloor + 1, uFloor + 1, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor, wFloor + 1, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor, wFloor + 1, uFloor + 1, vFloor + 1, seed)))
										+ z *
										((1 - y) * ((1 - x) * hash256(xFloor, yFloor, zFloor + 1, wFloor + 1, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor, zFloor + 1, wFloor + 1, uFloor + 1, vFloor + 1, seed))
												+ y * ((1 - x) * hash256(xFloor, yFloor + 1, zFloor + 1, wFloor + 1, uFloor + 1, vFloor + 1, seed) + x * hash256(xFloor + 1, yFloor + 1, zFloor + 1, wFloor + 1, uFloor + 1, vFloor + 1, seed)))
								))))))
		) * 0.003921569f;
	}
}
