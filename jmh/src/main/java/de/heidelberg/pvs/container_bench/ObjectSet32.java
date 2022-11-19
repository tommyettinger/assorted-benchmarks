package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectSet;

import java.util.Collection;

public class ObjectSet32<T> extends ObjectSet<T> {
    public ObjectSet32() {
    }

    public ObjectSet32(int initialCapacity) {
        super(initialCapacity);
    }

    public ObjectSet32(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ObjectSet32(ObjectSet<? extends T> set) {
        super(set);
    }

    public ObjectSet32(Collection<? extends T> coll) {
        super(coll);
    }

    public ObjectSet32(T[] array, int offset, int length) {
        super(array, offset, length);
    }

    public ObjectSet32(T[] array) {
        super(array);
    }

    protected static final int hashMul = 0x000CF093;
    protected int hashXor = 0x2E62A9C5;
//    protected int hashMul = 0x000CF093;
//    protected int hashMul = 0xEB18A809;
    @Override
    protected int place (Object item) {
        return (item.hashCode() ^ hashXor) * hashMul >>> shift;
    }

    @Override
    protected void resize (int newSize) {
        int oldCapacity = keyTable.length;
        threshold = (int)(newSize * loadFactor);
        mask = newSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        hashXor = hashXor * 0x5538A ^ 0x9E3779B7;
//        hashMul *= 0x2E62A9C5;
//        hashMul =  hashMul * 0x9E377 & 0xFFFFF;

        T[] oldKeyTable = keyTable;

        keyTable = (T[])new Object[newSize];

        if (size > 0) {
            for (int i = 0; i < oldCapacity; i++) {
                T key = oldKeyTable[i];
                if (key != null) {addResize(key);}
            }
        }
    }

}
