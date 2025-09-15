package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private Node root;
    private int size;

    private class Node {
        private K key;
        private V value;
        private Node left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public BSTMap() {
        this.root = null;
        this.size = 0;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear(){
        root = null;
        size = 0;
    }

    /* Helper method to find a node with the given key */
    private Node getNode(Node node, K key){
        if(node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp < 0){
            return getNode(node.left,key);
        }
        else if(cmp > 0){
            return getNode(node.right, key);
        }
        else{
            return node;
        }
    }

    @Override
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return getNode(root, key) != null;
    }

    @Override
    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        Node node = getNode(root,key);
        return node == null ? null : node.value;
    }

    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }

    /* Helper method for recursive insertion */
    private Node put(Node node, K key, V value){
        if(node == null){
            size++;
            return new Node(key,value);
        }
        int cmp = key.compareTo(node.key);
        if(cmp < 0){
            node.left = put(node.left,key,value);
        }
        else if(cmp > 0){
            node.right = put(node.right,key,value);
        }
        else{
            node.value = value;
        }
        return node;
    }
    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        root = put(root, key ,value);
    }

    /* Prints out the BSTMap in order of increasing Key. */
    public void printInOrder() {
        printInOrder(root);
        System.out.println();
    }

    /* Helper method for in-order traversal */
    private void printInOrder(Node node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.print(node.key + "=" + node.value + " ");
        printInOrder(node.right);
    }

    @Override
    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet(){
        Set<K> keys = new HashSet<>();
        addKeys(root, keys);
        return keys;
    }
    /* Helper method to collect all keys using in-order traversal */
    private void addKeys(Node node, Set<K> keys){
        if(node == null){
            return;
        }
        addKeys(node.left, keys);
        keys.add(node.key);
        addKeys(node.right, keys);
    }
    /* Helper method for recursive removal */
    private Node remove(Node node, K key){
        if(node == null){
            return null;
        }

        int cmp =key.compareTo(node.key);
        if(cmp < 0){
            node.left = remove(node.left, key);
        }
        else if(cmp > 0){
            node.right = remove(node.right, key);
        }
        else{
            // Found the node to remove
            // the most important part

            // Case 1: No child or one child
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            // Case 2: Two children - find inorder successor
            Node successor = findMin(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = remove(node.right, successor.key);
        }
        return node;
    }

    /* Helper method to find the minimum node in a subtree */
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key){
        if (!containsKey(key)) {
            return null;
        }
        V value = get(key);
        root = remove(root, key);
        size--;
        return value;
    }

    @Override
    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        if (!containsKey(key) || !get(key).equals(value)) {
            return null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
