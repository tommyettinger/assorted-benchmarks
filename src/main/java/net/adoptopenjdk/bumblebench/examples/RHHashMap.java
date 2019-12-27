package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * Robin Hood hash table.
 * <p>
 * See <a href="https://codecapsule.com/2013/11/11/robin-hood-hashing/">Emmanuel Goossaert's blog post</a>.
 * <p>
 * Non-licensed code; all copyright goes to Maksym Stepanenko:
 * <a href="https://github.com/mstepan/algorithms/blob/master/src/main/java/com/max/algs/hashing/robin_hood/RobinHoodHashMap.java">source here</a>.
 * This is only being used for evaluation purposes currently.
 */
public class RHHashMap<K, V> extends AbstractMap<K, V> {

    // big prime number
    private static final int SALT = 0x15BA25;//7_368_787;

    private static final int INITIAL_CAPACITY = 16;

    // Resize after hash table 50% filled in.
    private static final double RESIZE_LOAD_FACTOR = 0.5;

    private int size;

    private Entry[] data;

    public RHHashMap() {
        this.data = new Entry[INITIAL_CAPACITY];
    }

    public RHHashMap(int capacity) {
        this.data = new Entry[MathUtils.nextPowerOfTwo(capacity)];
    }

    private static int calculateDistance(int initialBucket, int curBucketIndex, int dataCapacity) {
        if (curBucketIndex >= initialBucket) {
            return curBucketIndex - initialBucket;
        }

        // TODO:
        return dataCapacity - initialBucket + curBucketIndex;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object keyObj) {
        if(keyObj == null)
            throw new NullPointerException("A RHHashMap doesn't allow null keys.");

        EntryWithBucket<K, V> node = findNodeWithBucket((K) keyObj);
        return node == null ? null : node.entry.value;
    }

    private EntryWithBucket<K, V> findNodeWithBucket(K key) {

        int keyHashCode = key.hashCode();
        int bucket = bucket(keyHashCode);

        for (int i = bucket; ; i = (i + 1) & (data.length - 1)) {

            // empty bucket
            if (data[i] == null) {
                return null;
            }

            @SuppressWarnings("unchecked")
            Entry<K, V> entry = data[i];

            if (key.equals(entry.key)) {
                return new EntryWithBucket<>(entry, i);
            }

            // cur node distance < distance for a search key
            //TODO: check if we need here Math.abs(i - bucket) or just i - bucket
            if (entry.distance(i, data.length) < calculateDistance(bucket, i, data.length)) {
                return null;
            }
        }
    }

    @Override
    public V put(K key, V value) {

        EntryWithBucket<K, V> node = findNodeWithBucket(key);

        // update existing entry
        if (node != null) {
            V oldValue = node.entry.value;
            node.entry.value = value;
            return oldValue;
        }

        Entry<K, V> entryToInsert = new Entry<>(key, value, bucket(key.hashCode()));

        for (int i = entryToInsert.initialBucket; ; i = (i + 1) & (data.length - 1)) {

            // found empty slot, insert entry
            if (data[i] == null) {
                data[i] = entryToInsert;
                break;
            }

            // found entry with smaller distance, swap entries and proceed
            else if (data[i].distance(i, data.length) < entryToInsert.distance(i, data.length)) {
                @SuppressWarnings("unchecked")
                Entry<K, V> temp = data[i];
                data[i] = entryToInsert;
                entryToInsert = temp;
            }
        }

        ++size;
        if (Double.compare(loadFactor(), RESIZE_LOAD_FACTOR) >= 0) {
            resize();
        }

        return null;
    }

    /**
     * Use backward-shift algorithm.
     * <p>
     * http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/
     */
    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object keyObj) {

        EntryWithBucket<K, V> node = findNodeWithBucket((K) keyObj);

        if (node == null) {
            return null;
        }

        data[node.bucketIndex] = null;

        for (int i = (node.bucketIndex + 1) & (data.length - 1);
             (data[i] != null && data[i].distance(i, data.length) != 0);
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
        return node.entry.value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map.Entry<K, V>> entrySet() {

        Set<Map.Entry<K, V>> entries = new RHHashSet<>(size);

        for (Entry<K, V> entry : data) {
            if (entry != null) {
                entries.add(new SimpleEntry<K, V>(entry.key, entry.value));
            }
        }

        return entries;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {

        Entry<K, V>[] tempData = data;

        data = new Entry[data.length << 1];

        size = 0;

        for (Entry<K, V> prevEntry : tempData) {
            if (prevEntry != null) {
                put(prevEntry.key, prevEntry.value);
            }
        }
    }

    private int bucket(int hashCode) {
        return (hashCode * SALT) & (data.length - 1);
    }

    private double loadFactor() {
        return ((double) size) / data.length;
    }

    private static final class EntryWithBucket<K, V> {
        private final Entry<K, V> entry;
        private final int bucketIndex;

        EntryWithBucket(Entry<K, V> entry, int bucketIndex) {
            this.entry = entry;
            this.bucketIndex = bucketIndex;
        }
    }

    private static final class Entry<K, V> {

        final K key;
        final int initialBucket;
        V value;

        Entry(K key, V value, int initialBucket) {
            this.key = key;
            this.value = value;
            this.initialBucket = initialBucket;
        }

        /**
         * DIB - distance to initial bucket
         */
        int distance(int curBucketIndex, int dataCapacity) {
            return calculateDistance(initialBucket, curBucketIndex, dataCapacity);
        }

        @Override
        public String toString() {
            return key + "=" + value + "[" + initialBucket + "]";
        }
    }

}