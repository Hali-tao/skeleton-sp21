package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private int size;
    private final int initialSize;
    private final double maxLoad;

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        this.buckets = createTable(initialSize);
        this.size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = (Collection<Node>[]) new Collection[tableSize];
        for(int i = 0; i < tableSize;i++){
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    // some of the helper methods
    // Helper method to get the bucket index for a key
    private int getBucketIndex(K key){
        if(key == null){
            throw new IllegalArgumentException("Null keys are not allowed");
        }
        int hashcode = key.hashCode();
        return Math.floorMod(hashcode, buckets.length);
    }

    // Helper method to resize the hash table when load factor is exceeded
    private void resize(){
        double loadFactor = (double) size / buckets.length;
        if(loadFactor > maxLoad){
            int newSize = buckets.length * 2;
            Collection<Node>[] newBuckets = createTable(newSize);
            // Rehash all existing elements
            for (Collection<Node> bucket : buckets) {
                for (Node node : bucket) {
                    int newIndex = Math.floorMod(node.key.hashCode(), newSize);
                    newBuckets[newIndex].add(node);
                }
            }
            buckets = newBuckets;
        }
    }

    // Helper method to find a node in a bucket by key
    private Node findNode(Collection<Node> bucket, K key) {
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        // Create a new table instead of clearing each bucket individually
        this.buckets = createTable(initialSize);
        size = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        int index = getBucketIndex(key);
        Collection<Node> bucket = buckets[index];
        return findNode(bucket, key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        int index = getBucketIndex(key);
        Collection<Node> bucket = buckets[index];
        Node node = findNode(bucket, key);
        return node == null ? null : node.value;
    }

    /** Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value){
        int index = getBucketIndex(key);
        Collection<Node> bucket = buckets[index];
        Node existingNode = findNode(bucket, key);

        if (existingNode != null) {
            // Update existing value
            existingNode.value = value;
        } else {
            // Add new node
            bucket.add(createNode(key, value));
            size++;

            // Check if we need to resize after adding
            if ((double) size / buckets.length > maxLoad) {
                resize();
            }
        }
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet(){
        Set<K> keys = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                keys.add(node.key);
            }
        }
        return keys;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key){
        int index = getBucketIndex(key);
        Collection<Node> bucket = buckets[index];
        Iterator<Node> iterator = bucket.iterator();

        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.key.equals(key)) {
                V value = node.value;
                iterator.remove();
                size--;
                return value;
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value){
        int index = getBucketIndex(key);
        Collection<Node> bucket = buckets[index];
        Iterator<Node> iterator = bucket.iterator();

        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.key.equals(key) && node.value.equals(value)) {
                iterator.remove();
                size--;
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }
    private class MyHashMapIterator implements Iterator<K> {
        private Iterator<K> keyIterator;

        public MyHashMapIterator() {
            this.keyIterator = keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return keyIterator.hasNext();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return keyIterator.next();
        }
    }
}
