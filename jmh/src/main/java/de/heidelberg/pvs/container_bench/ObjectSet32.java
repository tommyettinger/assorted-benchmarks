package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectSet;

import javax.annotation.Nonnull;
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

    protected int hashMul = 0xEB18A809;
    @Override
    protected int place (Object item) {
        return item.hashCode() * hashMul >>> shift;
    }

    @Override
    protected void resize (int newSize) {
        int oldCapacity = keyTable.length;
        threshold = (int)(newSize * loadFactor);
        mask = newSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        hashMul *= 0x2E62A9C5;

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
