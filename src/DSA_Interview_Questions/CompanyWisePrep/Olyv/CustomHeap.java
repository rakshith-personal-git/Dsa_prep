package DSA_Interview_Questions.CompanyWisePrep.Olyv;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class CustomHeap {

    private int[] heap;
    private int size;
    private final boolean isMinHeap; // true = MinHeap, false = MaxHeap
    private static final int DEFAULT_CAPACITY = 16;

    // [SAY OUT LOUD]: "Constructor 1: Initialize an empty heap with default capacity and specified type."
    public CustomHeap(boolean isMinHeap) {
        this(DEFAULT_CAPACITY, isMinHeap);
    }

    public CustomHeap(int initialCapacity, boolean isMinHeap) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        this.heap = new int[initialCapacity];
        this.size = 0;
        this.isMinHeap = isMinHeap;
    }

    // [SAY OUT LOUD]: "Constructor 2: Floyd's O(N) heapify build from an existing array."
    public CustomHeap(int[] array, boolean isMinHeap) {
        if (array == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        this.isMinHeap = isMinHeap;
        this.size = array.length;
        this.heap = Arrays.copyOf(array, Math.max(array.length, DEFAULT_CAPACITY));

        // [SAY OUT LOUD]: "Start from the last non-leaf node down to 0 and sift-down each."
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    // [SAY OUT LOUD]: "Returns the root element without removing it in O(1) time."
    public int peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return heap[0];
    }

    // [SAY OUT LOUD]: "Inserts an element into the heap in O(log N) amortized time."
    public void insert(int val) {
        ensureCapacity();
        heap[size] = val;
        size++;
        siftUp(size - 1);
    }

    // [SAY OUT LOUD]: "Extracts and returns the root element (Min or Max) in O(log N) time."
    public int poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        int rootVal = heap[0];
        // [SAY OUT LOUD]: "Move the last valid leaf to the root and contract the logical size."
        heap[0] = heap[size - 1];
        size--;

        // [SAY OUT LOUD]: "Restore the heap invariant from root downwards."
        if (size > 0) {
            siftDown(0);
        }
        return rootVal;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // [SAY OUT LOUD]: "Sift-Up restores the invariant when an element is added at index k."
    private void siftUp(int k) {
        int target = heap[k];
        while (k > 0) {
            int parentIdx = (k - 1) >>> 1; // Unsigned shift equivalent to (k - 1) / 2
            int parentVal = heap[parentIdx];

            // [SAY OUT LOUD]: "If the relationship between target and parent satisfies the invariant, stop."
            if (dominates(parentVal, target)) {
                break;
            }

            // [SAY OUT LOUD]: "Otherwise, pull parent down and advance up the branch."
            heap[k] = parentVal;
            k = parentIdx;
        }
        heap[k] = target;
    }

    // [SAY OUT LOUD]: "Sift-Down restores the invariant when root or an internal node violates it."
    private void siftDown(int k) {
        int target = heap[k];
        int half = size >>> 1; // Loop while node k has at least one child (non-leaf)

        while (k < half) {
            int leftChildIdx = (k << 1) + 1; // 2 * k + 1
            int rightChildIdx = leftChildIdx + 1;
            int bestChildIdx = leftChildIdx;

            // [SAY OUT LOUD]: "Check if right child exists and dominates over the left child."
            if (rightChildIdx < size && dominates(heap[rightChildIdx], heap[leftChildIdx])) {
                bestChildIdx = rightChildIdx;
            }

            // [SAY OUT LOUD]: "If target already dominates the best child, invariant holds."
            if (dominates(target, heap[bestChildIdx])) {
                break;
            }

            // [SAY OUT LOUD]: "Promote the dominant child and continue downwards."
            heap[k] = heap[bestChildIdx];
            k = bestChildIdx;
        }
        heap[k] = target;
    }

    // [SAY OUT LOUD]: "Dominance comparator: encapsulates whether a should sit above b."
    private boolean dominates(int a, int b) {
        return isMinHeap ? (a <= b) : (a >= b);
    }

    // [SAY OUT LOUD]: "Dynamic array expansion: doubles capacity when array is fully saturated."
    private void ensureCapacity() {
        if (size == heap.length) {
            int newCapacity = heap.length + (heap.length >> 1); // 1.5x growth like ArrayList
            if (newCapacity < 0) { // Overflow protection
                throw new OutOfMemoryError("Heap capacity exceeded maximum bounds");
            }
            heap = Arrays.copyOf(heap, newCapacity);
        }
    }

    public static void main(String[] args) {
        // Test Case 1: MaxHeap Verification
        System.out.println("--- Test 1: MaxHeap Operations ---");
        CustomHeap maxHeap = new CustomHeap(false);
        int[] stream = {10, 4, 15, 20, 0, 30, 2};
        for (int x : stream) {
            maxHeap.insert(x);
        }
        System.out.print("MaxHeap Extracted Descending: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " "); // Expected: 30 20 15 10 4 2 0
        }
        System.out.println();

        // Test Case 2: MinHeap Verification
        System.out.println("\n--- Test 2: MinHeap Operations ---");
        CustomHeap minHeap = new CustomHeap(true);
        for (int x : stream) {
            minHeap.insert(x);
        }
        System.out.print("MinHeap Extracted Ascending: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " "); // Expected: 0 2 4 10 15 20 30
        }
        System.out.println();

        // Test Case 3: Floyd's O(N) In-Place Heapify Constructor
        System.out.println("\n--- Test 3: Floyd's O(N) BuildHeap ---");
        int[] rawArray = {9, 3, 7, 1, 6, 8, 2, 5, 4};
        CustomHeap builtMaxHeap = new CustomHeap(rawArray, false);
        System.out.println("Peak Element: " + builtMaxHeap.peek()); // Expected: 9
        System.out.print("Extracting 3 largest: ");
        System.out.print(builtMaxHeap.poll() + " "); // 9
        System.out.print(builtMaxHeap.poll() + " "); // 8
        System.out.println(builtMaxHeap.poll());     // 7
    }
}

/*
 * COMPLEXITY BREAKDOWN:
 * - Time Complexity:
 *   - peek(): O(1) constant time access to array[0].
 *   - insert(): O(log N) amortized. Sift-up traverses at most the tree height h = floor(log2 N).
 *   - poll(): O(log N). Sift-down traverses at most the tree height h = floor(log2 N).
 *   - buildHeap (Floyd): Strictly O(N). Sum of (h / 2^h) converges to 2.
 *
 * - Auxiliary Space Complexity: O(N)
 *   Contiguous primitive 32-bit integer array. Zero pointer overhead or object boxing.
 */