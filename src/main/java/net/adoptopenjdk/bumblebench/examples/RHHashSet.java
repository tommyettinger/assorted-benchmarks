package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;

import java.util.*;

/**
 * Robin Hood hash table.
 * <p>
 * See <a href="https://codecapsule.com/2013/11/11/robin-hood-hashing/">Emmanuel Goossaert's blog post</a>.
 * <p>
 * Non-licensed code; all copyright goes to Maksym Stepanenko:
 * <a href="https://github.com/mstepan/algorithms/blob/master/src/main/java/com/max/algs/hashing/robin_hood/RobinHoodHashMap.java">source here</a>.
 * This is only being used for evaluation purposes currently.
 */
public class RHHashSet<K> extends AbstractSet<K> {

    // big prime number, not too big for GWT
    private static final int SALT = 0x15BA25;//7_368_787;
    // 2 to the 64 divided by the golden ratio
    //private static final long SALT = 0x9E3779B97F4A7C15L;

    private static final int INITIAL_CAPACITY = 16;

    // Default: resize after hash table 50% filled in.
    private final float f;

    private int size;

    private K[] data;
    /**
     * Initial Bucket positions.
     */
    private int[] ib;
    
    private int mask;
    //private int shift;

    public RHHashSet() {
        this.data = (K[])(new Object[INITIAL_CAPACITY]);
        this.ib = new int[INITIAL_CAPACITY];
        f = 0.5f;
        mask = INITIAL_CAPACITY - 1;
//        shift = 60;
    }

    public RHHashSet(int capacity) {
        this(capacity, 0.5f);
    }
    public RHHashSet(int capacity, float loadFactor) {
        capacity = MathUtils.nextPowerOfTwo(capacity);
        this.data = (K[])(new Object[capacity]);
        this.ib = new int[capacity];
        f = loadFactor;
        mask = capacity - 1;
//        shift = Long.numberOfLeadingZeros(mask);
    }

    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            private int index = -1, traversed = 0;
            @Override
            public boolean hasNext() {
                return traversed < size;
            }

            @Override
            public K next() {
                while (data[++index] == null);
                ++traversed;
                return data[index];
            }
        };
    }

    private int calculateDistance(final int initialBucket, final int curBucketIndex) {
        return curBucketIndex - initialBucket & mask;
    }

    private int findNodeWithBucket(K key) {

        int keyHashCode = key.hashCode();
        int bucket = bucket(keyHashCode);

        for (int i = bucket; ; i = (i + 1) & mask) {
            // empty bucket
            if (data[i] == null) {
                return -1;
            }
            if (key.equals(data[i])) {
                return i;
            }
            // cur node distance < distance for a search key
            if (calculateDistance(ib[i], i) < calculateDistance(bucket, i)) {
                return -1;
            }
        }
    }

    private int findNodeWithBucket(K key, int bucket) {
        for (int i = bucket; ; i = (i + 1) & mask) {
            // empty bucket
            if (data[i] == null) {
                return -1;
            }
            if (key.equals(data[i])) {
                return i;
            }
            // cur node distance < distance for a search key
            if (calculateDistance(ib[i], i) < calculateDistance(bucket, i)) {
                return -1;
            }
        }
    }

    @Override
    public boolean add(K key) {
        int b = bucket(key.hashCode());
        // an identical key already exists
        if (findNodeWithBucket(key, b) != -1) {
            return false;
        }
        for (int i = b; ; i = (i + 1) & mask) {
            // space is available so we insert and break (resize is later)
            if (data[i] == null) {
                data[i] = key;
                ib[i] = b;
                break;
            }
            // if there is a key with a lower probe distance, we swap with it
            // and keep going until we find a place we can insert
            else if (calculateDistance(ib[i], i) < calculateDistance(b, i)) {
                K temp = data[i];
                int tb = ib[i];
                data[i] = key;
                ib[i] = b;
                key = temp;
                b = tb;
            }
        }
        if (++size > data.length * f) {
            resize();
        }
        return true;
    }

    /**
     * Use backward-shift algorithm.
     * <p>
     * http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object keyObj) {

        int node = findNodeWithBucket((K) keyObj);

        if (node == -1) {
            return false;
        }

        data[node] = null;

        for (int i = (node + 1) & mask; (data[i] != null && calculateDistance(ib[node], i) != 0); i = (i + 1) & mask) {
            data[i - 1 & mask] = data[i];
            ib[i - 1 & mask] = ib[i];
            data[i] = null;
            ib[i] = 0;
        }

        --size;
        return true;
    }

    public boolean contains(Object key) {
        if(key == null)
            throw new NullPointerException("A RHHashMap doesn't allow null keys.");
        return data[findNodeWithBucket((K) key)] != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {

        K[] tempData = data;

        data = (K[])(new Object[tempData.length << 1]);
        ib = new int[data.length];
        mask = data.length - 1;
//        shift = Long.numberOfLeadingZeros(mask);
        size = 0;

        for (K prevKey : tempData) {
            if (prevKey != null) {
                add(prevKey);
            }
        }
    }

    private int bucket(final int hashCode) {
        // basic hashCode expansion (from multiplication) and
        // truncation (by masking to fit in the tables).
        return (hashCode * SALT & mask);
        // fibonacci hashing; may improve resistance to bad hashCode()s
        // fibonacci uses 0x9E3779B97F4A7C15L as SALT
        //return (int) (hashCode * SALT >>> shift);
    }
}