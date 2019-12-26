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

    // big prime number
    private static final int SALT = 0x15BA25;//7_368_787;

    private static final int INITIAL_CAPACITY = 16;

    // Resize after hash table 50% filled in.
    private static final double RESIZE_LOAD_FACTOR = 0.5;

    private int size;

    private K[] data;
    /**
     * Initial Bucket positions.
     */
    private int[] ib;

    public RHHashSet() {
        this.data = (K[])(new Object[INITIAL_CAPACITY]);
        this.ib = new int[INITIAL_CAPACITY];
    }

    public RHHashSet(int capacity) {
        capacity = MathUtils.nextPowerOfTwo(capacity);
        this.data = (K[])(new Object[capacity]);
        this.ib = new int[capacity];
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

    private static int calculateDistance(int initialBucket, int curBucketIndex, int dataCapacity) {
        if (curBucketIndex >= initialBucket) {
            return curBucketIndex - initialBucket;
        }

        // TODO:
        return dataCapacity - initialBucket + curBucketIndex;
    }

    private int findNodeWithBucket(K key) {

        int keyHashCode = key.hashCode();
        int bucket = bucket(keyHashCode);

        for (int i = bucket; ; i = (i + 1) & (data.length - 1)) {

            // empty bucket
            if (data[i] == null) {
                return -1;
            }

            @SuppressWarnings("unchecked")
            K k = data[i];

            if (key.equals(k)) {
                return i;
            }

            // cur node distance < distance for a search key
            //TODO: check if we need here Math.abs(i - bucket) or just i - bucket
            if (calculateDistance(ib[i], i, data.length) < calculateDistance(bucket, i, data.length)) {
                return -1;
            }
        }
    }

    @Override
    public boolean add(K key) {

        int node = findNodeWithBucket(key);

        // update existing entry
        if (node != -1) {
            return false;
        }

        int b = bucket(key.hashCode());
        
        
        for (int i = b; ; i = (i + 1) & (data.length - 1)) {

            // found empty slot, insert entry
            if (data[i] == null) {
                data[i] = key;
                ib[i] = b;
                break;
            }

            // found entry with smaller distance, swap entries and proceed
            else if (calculateDistance(ib[i], i, data.length) < calculateDistance(b, i, data.length)) {
                @SuppressWarnings("unchecked")
                K temp = data[i];
                int tb = ib[i];
                data[i] = key;
                ib[i] = b;
                key = temp;
                b = tb;
            }
        }

        ++size;
        if (Double.compare(loadFactor(), RESIZE_LOAD_FACTOR) >= 0) {
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

        for (int i = (node + 1) & (data.length - 1);
             (data[i] != null && calculateDistance(ib[node], i, data.length) != 0);
             i = (i + 1) & (data.length - 1)) {

            if (i == 0) {
                data[data.length - 1] = data[0];
                data[0] = null;
            }
            else {
                data[i - 1] = data[i];
                data[i] = null;
            }
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
        size = 0;

        for (K prevKey : tempData) {
            if (prevKey != null) {
                add(prevKey);
            }
        }
    }

    private int bucket(int hashCode) {
        return (hashCode * SALT) & (data.length - 1);
    }

    private double loadFactor() {
        return ((double) size) / data.length;
    }
}