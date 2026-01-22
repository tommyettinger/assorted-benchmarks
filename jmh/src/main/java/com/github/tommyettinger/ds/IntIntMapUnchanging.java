package com.github.tommyettinger.ds;

import com.github.tommyettinger.digital.BitConversion;

public class IntIntMapUnchanging extends IntIntMap{
    public IntIntMapUnchanging() {
        super();
    }

    public IntIntMapUnchanging(int initialCapacity) {
        super(initialCapacity);
    }

    public IntIntMapUnchanging(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IntIntMapUnchanging(IntIntMap map) {
        super(map);
    }

    public IntIntMapUnchanging(int[] keys, int[] values) {
        super(keys, values);
    }

    public IntIntMapUnchanging(PrimitiveCollection.OfInt keys, PrimitiveCollection.OfInt values) {
        super(keys, values);
    }

    @Override
    protected int place(int item) {
        // Constants are faster here than using a variable, and this is called very often.
        // The choice of constants is... tricky.
        // 0x9E3779B9 is 2 to the 32 divided by the golden ratio, rounded to the nearest odd number.
        // 0xC143F257 is somewhat arbitrary; it is a prime number (when unsigned), but it was chosen along with
        // the multiplier so that a simple additive counter with the "wrong increment" (likely 0x144CBC89 or a similar
        // number; specifically that because 0x144CBC89 * 0x9E3779B9 == 1) won't create pathologically low outputs.
        //
        // The choice of numbers ensures that the problem multiplier 0x144CBC89 won't be part of a typical random
        // number generator; an LCG like java.util.Random (or an MLCG without a prime modulus) needs the lowest hex
        // digit to be 0x5 or 0xD, and the only type of simple random number generator that uses XOR and multiplication,
        // an XLCG, doesn't allow 0x144CBC89 as a multiplier either. If we just added a number to item, then the problem
        // multiplier is still an issue; it just results in item with an additive offset. If we added a number after
        // multiplying, that would also offset the result somewhat, and wouldn't change what outputs are before or after
        // other nearby outputs. Addition and multiplication are related, but XOR and multiplication aren't (as much).
        return BitConversion.imul(item ^ 0xC143F257, 0x9E3779B9) >>> shift;
    }
}
