package DSA_Interview_Questions.Grind150;

import java.util.Stack;

public class AL_ImplementQueueUsingStacks {
    /**
     * Implement a first in first out (FIFO) queue using only two stacks.
     * The implemented queue should support all the functions of a normal queue (push, peek, pop, and empty).
     * <p>
     * Implement the MyQueue class:
     * void push(int x) Pushes element x to the back of the queue.
     * int pop() Removes the element from the front of the queue and returns it.
     * int peek() Returns the element at the front of the queue.
     * boolean empty() Returns true if the queue is empty, false otherwise.
     * <p>
     * Notes:
     * You must use only standard operations of a stack, which means only push to top, peek/pop from top, size, and is empty operations are valid.
     * Depending on your language, the stack may not be supported natively.
     * You may simulate a stack using a list or deque (double-ended queue) as long as you use only a stack's standard operations.
     * <p>
     * Example 1:
     * Input
     * ["MyQueue", "push", "push", "peek", "pop", "empty"]
     * [[], [1], [2], [], [], []]
     * Output
     * [null, null, null, 1, 1, false]
     * <p>
     * Explanation
     * MyQueue myQueue = new MyQueue();
     * myQueue.push(1); // queue is: [1]
     * myQueue.push(2); // queue is: [1, 2] (leftmost is front of the queue)
     * myQueue.peek(); // return 1
     * myQueue.pop(); // return 1, queue is [2]
     * myQueue.empty(); // return false
     * <p>
     * Constraints:
     * 1 <= x <= 9
     * At most 100 calls will be made to push, pop, peek, and empty.
     * All the calls to pop and peek are valid.
     * <p>
     * Follow-up: Can you implement the queue such that each operation is amortized O(1) time complexity? In other words, performing n operations will take overall O(n) time even if one of those operations may take longer.
     */

    public static void main(String[] args) {
        String[] input = {"MyQueue", "push", "push", "peek", "pop", "empty"};
        int [][] inputOps = {{},{1},{2},{},{},{}};
        MyQueue myQueue = new MyQueue();
        myQueue.push(1);
        System.out.println("null");
        myQueue.push(2);
        System.out.println("null");
        System.out.println(myQueue.peek());
        System.out.println(myQueue.pop());
        System.out.println(myQueue.empty());

    }

    /**Approach:
     * When a new element is pushed into the queue (push operation):
     * All elements from s1 are popped and pushed onto s2. This effectively reverses the order of elements.
     * The new element is pushed onto the now-empty s1.
     * Finally, elements are popped from s2 and pushed back onto s1 to maintain the original order.
     * Dequeue Operation (Pop):
     *
     * Time complexity:
     * Enqueue (push): O(n)O(n)O(n)
     * Dequeue (pop): O(1)O(1)O(1)
     * Peek (peek): O(1)O(1)O(1)
     * Empty Check (empty): O(1)O(1)O(1)
     * (where n is the number of elements in the queue.)
     *
     * Space complexity:
     * O(n)O(n)O(n)**
     * */

    static class MyQueue {

        private Stack<Integer> stack1;
        private Stack<Integer> stack2;

        public MyQueue() {
            stack1 = new Stack<>();
            stack2 = new Stack<>();
        }

        public void push(int x) {
            while (!stack1.isEmpty()){
                stack2.push(stack1.pop());
            }
            stack1.push(x);
            while (!stack2.isEmpty()) {
                stack1.push(stack2.pop());
            }
        }

        public int pop() {
            return stack1.pop();
        }

        public int peek() {
            return stack1.peek();
        }

        public boolean empty() {
            return stack1.isEmpty();
        }
    }

}
