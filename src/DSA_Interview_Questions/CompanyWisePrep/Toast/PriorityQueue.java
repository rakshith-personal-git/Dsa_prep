package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Production-grade implementation of a Max Priority Queue using an array-based Binary Max-Heap.
 *
 * @param <T> Type of element, must implement Comparable<T>.
 */
public class PriorityQueue<T extends Comparable<T>> {

    private final List<T> heap;

    /**
     * Constructs an empty Priority Queue.
     */
    public PriorityQueue() {
        this.heap = new ArrayList<>();
    }

    /**
     * Inserts a new element into the Priority Queue.
     *
     * <p>Time Complexity: O(log N)
     *
     * @param element The item to insert.
     */
    public void enqueue(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot enqueue null elements.");
        }

        // Step 1: Append new element to the end of the heap array
        heap.add(element);

        // Step 2: Bubble up to restore heap invariant
        siftUp(heap.size() - 1);
    }

    /**
     * Removes and returns the highest priority (maximum) element.
     *
     * <p>Time Complexity: O(log N)
     *
     * @return The highest priority element.
     * @throws NoSuchElementException if the queue is empty.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority Queue is empty.");
        }

        T maxValue = heap.get(0);

        // Move the last element to the root position
        T lastValue = heap.remove(heap.size() - 1);

        if (!isEmpty()) {
            heap.set(0, lastValue);
            // Bubble down to restore heap invariant
            siftDown(0);
        }

        return maxValue;
    }

    /**
     * Returns the highest priority element without removing it.
     *
     * <p>Time Complexity: O(1)
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority Queue is empty.");
        }
        return heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    // --- Helper Heap Operations ---

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            
            // If current element <= parent, heap property is satisfied
            if (heap.get(index).compareTo(heap.get(parentIndex)) <= 0) {
                break;
            }

            // Swap with parent
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void siftDown(int index) {
        int size = heap.size();

        while (index < size) {
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = 2 * index + 2;
            int largest = index;

            // Compare with left child
            if (leftChildIndex < size && heap.get(leftChildIndex).compareTo(heap.get(largest)) > 0) {
                largest = leftChildIndex;
            }

            // Compare with right child
            if (rightChildIndex < size && heap.get(rightChildIndex).compareTo(heap.get(largest)) > 0) {
                largest = rightChildIndex;
            }

            // If current node is larger than both children, heap property is satisfied
            if (largest == index) {
                break;
            }

            // Swap with largest child
            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    // --- Main Method with Executable Tests ---
    public static void main(String[] args) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        // Test 1: Enqueue sequence
        int[] elementsToInsert = {15, 30, 10, 50, 20};
        System.out.println("Enqueueing: 15, 30, 10, 50, 20");
        for (int val : elementsToInsert) {
            pq.enqueue(val);
        }

        // Test 2: Peek root
        System.out.println("Peek Max Element: " + pq.peek()); // Expected: 50

        // Test 3: Dequeue all in priority order (should yield 50, 30, 20, 15, 10)
        System.out.print("Dequeued Priority Order: ");
        while (!pq.isEmpty()) {
            System.out.print(pq.dequeue() + " ");
        }
        System.out.println(); // Expected output: 50 30 20 15 10
    }
}