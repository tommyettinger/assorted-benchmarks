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
        return BitConversion.imul(item, 0x9E3779B1) + hashMultiplier >>> shift;
    }
}
