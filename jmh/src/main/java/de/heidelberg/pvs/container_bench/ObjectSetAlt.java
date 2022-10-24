package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectSet;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ObjectSetAlt<T> extends ObjectSet<T> {
    public ObjectSetAlt() {
    }

    public ObjectSetAlt(int initialCapacity) {
        super(initialCapacity);
    }

    public ObjectSetAlt(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ObjectSetAlt(ObjectSet<? extends T> set) {
        super(set);
    }

    public ObjectSetAlt(Collection<? extends T> coll) {
        super(coll);
    }

    public ObjectSetAlt(T[] array, int offset, int length) {
        super(array, offset, length);
    }

    public ObjectSetAlt(T[] array) {
        super(array);
    }

    {
        hashMultiplier = 0x769C3DC968DB6A07L;
    }
    @Override
    protected int place (Object item) {
        return (int)(item.hashCode() * hashMultiplier >>> shift); // total collisions: 1761470,    longest pileup: 19
    }

    @Override
    protected void addResize (@Nonnull Object key) {
        Object[] keyTable = this.keyTable;
        for (int i = place(key), p = 0; ; i = i + 1 & mask, p++) {
            if (keyTable[i] == null) {
                keyTable[i] = key;
                return;
            }
        }
    }

    @Override
    protected void resize (int newSize) {
        int oldCapacity = keyTable.length;
        threshold = (int)(newSize * loadFactor);
        mask = newSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        hashMultiplier *= (long)size << 3 ^ 0xF1357AEA2E62A9C5L;

        Object[] oldKeyTable = keyTable;

        keyTable = (T[])new Object[newSize];

        if (size > 0) {
            for (int i = 0; i < oldCapacity; i++) {
                Object key = oldKeyTable[i];
                if (key != null) {addResize(key);}
            }
        }
    }

}
