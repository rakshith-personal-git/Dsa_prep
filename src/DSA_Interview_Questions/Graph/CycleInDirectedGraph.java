package DSA_Interview_Questions.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycleInDirectedGraph {
    /**
     * Cycle in Directed Graph
     * Problem Description
     * Given an directed graph having A nodes. A matrix B of size M x 2 is given which represents the M edges
     * such that there is a edge directed from node B[i][0] to node B[i][1].
     * <p>
     * Find whether the graph contains a cycle or not, return 1 if cycle is present else return 0.
     * NOTE:
     * The cycle must contain atleast two nodes.
     * There are no self-loops in the graph.
     * There are no multiple edges between two nodes.
     * The graph may or may not be connected.
     * Nodes are numbered from 1 to A.
     * Your solution will run on multiple test cases. If you are using global variables make sure to clear them.
     * <p>
     * Problem Constraints
     * 2 <= A <= 105
     * 1 <= M <= min(200000,A*(A-1))
     * 1 <= B[i][0], B[i][1] <= A
     * Input Format
     * The first argument given is an integer A representing the number of nodes in the graph.
     * The second argument given a matrix B of size M x 2 which represents the M edges such that there is a edge directed from node B[i][0] to node B[i][1].
     * <p>
     * Output Format
     * Return 1 if cycle is present else return 0.
     * <p>
     * Example Input
     * Input 1:
     * A = 5
     * B = [  [1, 2]
     * [4, 1]
     * [2, 4]
     * [3, 4]
     * [5, 2]
     * [1, 3] ]
     * <p>
     * Input 2:
     * A = 5
     * B = [  [1, 2]
     * [2, 3]
     * [3, 4]
     * [4, 5] ]
     * <p>
     * <p>
     * Example Output
     * Output 1:
     * 1
     * Output 2:
     * 0
     * <p>
     * <p>
     * Example Explanation
     * Explanation 1:
     * The given graph contain cycle 1 -> 3 -> 4 -> 1 or the cycle 1 -> 2 -> 4 -> 1
     * Explanation 2:
     * The given graph doesn't contain any cycle.
     */

    // A = 5
    //     * B = [  [1, 2]
    //     * [4, 1]
    //     * [2, 4]
    //     * [3, 4]
    //     * [5, 2]
    //     * [1, 3] ]
    public static void main(String[] args) {
        int A1 = 5;
        int[][] B1 = {{1, 2}, {4, 1}, {2, 4}, {3, 4}, {5, 2}, {1, 3}};
        System.out.println(isCyclic(A1, B1));  // Output: true

        int A2 = 5;
        int[][] B2 = {{1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println(isCyclic(A2, B2));  // Output: false
    }

    private static boolean isCyclic(int A, int[][] B) {
        Map<Integer, List<Integer>> graph = new HashMap<>();

        // Initialize the graph
        for (int i = 1; i <= A; i++) {
            graph.put(i, new ArrayList<>());
        }

        // Build the graph from edges
        for (int[] edge : B) {
            graph.get(edge[0]).add(edge[1]);
        }

        boolean[] visited = new boolean[A + 1];
        boolean[] recursionStack = new boolean[A + 1];

        for (int node = 1; node <= A; node++) { //since Nodes are numbered from 1 to A, starting i from 1
            if (!visited[node]) {
                if (isCyclicDFS(graph, node, visited, recursionStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isCyclicDFS(Map<Integer, List<Integer>> graph, int node, boolean[] visited, boolean[] recursionStack) {
        visited[node] = true;
        recursionStack[node] = true;

        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                if (isCyclicDFS(graph, neighbor, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack[neighbor]) {
                return true;
            }
        }

        recursionStack[node] = false;
        return false;
    }
}
