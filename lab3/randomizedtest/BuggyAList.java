package randomizedtest;

/** Array based list.
 *  @author Josh Hug
 */

//         0 1  2 3 4 5 6 7
// items: [6 9 -1 2 0 0 0 0 ...]
// size: 5

/* Invariants:
 addLast: The next item we want to add, will go into position size
 getLast: The item we want to return is in position size - 1
 size: The number of items in the list should be size.
*/

public class BuggyAList<Item> {
    private Item[] items;
    private int size;

    /** Creates an empty list. */
    public BuggyAList() {
        items = (Item[]) new Object[1];
        size = 0;
    }

    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            a[i] = items[i];
        }
        items = a;
    }

    /** Inserts X into the back of the list. */
    public void addLast(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[size] = x;
        size = size + 1;
    }

    /** Returns the item from the back of the list. */
    public Item getLast() {
        if (size == 0) {
            throw new IllegalStateException("List is empty");
        }
        return items[size - 1];
    }

    /** Gets the ith item in the list (0 is the front). */
    public Item get(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
        }
        return items[i];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    /** Deletes item from back of the list and returns deleted item. */
    public Item removeLast() {
        if (size == 0) {
            throw new IllegalStateException("List is empty");
        }

        Item x = getLast();
        items[size - 1] = null;
        size = size - 1;

        // 更保守的缩容：确保缩容后至少还有1的容量
        if (size > 0 && size <= items.length / 4) {
            int newCapacity = Math.max(1, items.length / 2); // 至少保持容量为1
            resize(newCapacity);
        }

        return x;
    }
}
