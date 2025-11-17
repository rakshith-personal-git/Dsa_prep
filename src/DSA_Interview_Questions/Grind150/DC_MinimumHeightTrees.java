package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DC_MinimumHeightTrees {
    /**
     * A tree is an undirected graph in which any two vertices are connected by exactly one path.
     * In other words, any connected graph without simple cycles is a tree.
     * Given a tree of n nodes labelled from 0 to n - 1, and an array of n - 1 edges where edges[i] = [ai, bi]
     * indicates that there is an undirected edge between the two nodes ai and bi in the tree,
     * you can choose any node of the tree as the root. When you select a node x as the root, the result tree has height h.
     * Among all possible rooted trees, those with minimum height (i.e. min(h))  are called minimum height trees (MHTs).
     * <p>
     * Return a list of all MHTs' root labels. You can return the answer in any order.
     * The height of a rooted tree is the number of edges on the longest downward path between the root and a leaf.
     * <p>
     * Example 1:
     * Input: n = 4, edges = [[1,0],[1,2],[1,3]]
     * Output: [1]
     * Explanation: As shown, the height of the tree is 1 when the root is the node with label 1 which is the only MHT.
     * <p>
     * Example 2:
     * Input: n = 6, edges = [[3,0],[3,1],[3,2],[3,4],[5,4]]
     * Output: [3,4]
     * <p>
     * Constraints:
     * 1 <= n <= 2 * 104
     * edges.length == n - 1
     * 0 <= ai, bi < n
     * ai != bi
     * All the pairs (ai, bi) are distinct.
     * The given input is guaranteed to be a tree and there will be no repeated edges.
     */

    public static void main(String[] args) {
        int n = 4;
        int[][] edges = {{1, 0}, {1, 2}, {1, 3}};
        List<Integer> ans = findMinHeightTrees(n, edges);
        ans.forEach(i -> System.out.printf(i + " "));
        System.out.println();
        ans = findMinHeightTreesOptimised(n, edges);
        ans.forEach(i -> System.out.printf(i + " "));
        System.out.println();
        ans = findMinHeightTreesReadable(n, edges);
        ans.forEach(i -> System.out.printf(i + " "));
    }

    /**
     * The actual implementation is similar to the BFS topological sort. Remove the leaves, update the degrees of inner vertexes.
     * Then remove the new leaves. Doing so level by level until there are 2 or 1 nodes left. What's left is our answer!
     */

    private static List<Integer> findMinHeightTrees(int n, int[][] edges) {
        List<Integer> result = new ArrayList<>();
        if (n == 1) {
            result.add(0);
            return result;
        }

        //Create adjacency list to represent the tree
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            adjacencyList.putIfAbsent(u, new ArrayList<>());
            adjacencyList.putIfAbsent(v, new ArrayList<>());
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u); //since its biDirectional
        }


        //find initial leaf nodes... i.e the one at the very end, by end we mean vertex of degree 1 (aka leaves).
        List<Integer> leaves = new ArrayList<>();
        for (int node = 0; node < n; node++) {
            if (adjacencyList.get(node).size() == 1) {
                leaves.add(node);
            }
        }

        // Continue until we're left with one or two nodes
        while (n > 2) {
            n = n - leaves.size();
            List<Integer> newLeaves = new ArrayList<>();
            for (int leaf : leaves) {
                int neighbour = adjacencyList.get(leaf).get(0); // Remove the leaf
                adjacencyList.get(neighbour).remove(Integer.valueOf(leaf)); // Remove its connection from that leaf
                if (adjacencyList.get(neighbour).size() == 1) {
                    newLeaves.add(neighbour);
                }
                leaves = newLeaves;
            }
        }

        return leaves; // Return the remaining node(s)
    }


    //another solution which is optimised and 3x faster since we are not using map

    private static List<Integer> findMinHeightTreesOptimised(int n, int[][] edges) {
        List<Integer> result = new ArrayList<>();
        if (n == 1) {
            result.add(0);
            return result;
        }

        List<Integer>[] adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        int[] degree = new int[n];

        for (int[] edge : edges) {
            adjList[edge[0]].add(edge[1]);
            adjList[edge[1]].add(edge[0]);
            degree[edge[0]]++;
            degree[edge[1]]++;
        }

        Queue<Integer> leaves = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (degree[i] == 1) {
                leaves.offer(i);
            }
        }

        while (n > 2) {
            int size = leaves.size();
            n -= size;
            for (int i = 0; i < size; i++) {
                int leaf = leaves.poll();
                for (int neighbor : adjList[leaf]) {
                    degree[neighbor]--;
                    if (degree[neighbor] == 1) {
                        leaves.offer(neighbor);
                    }
                }
            }
        }

        while (!leaves.isEmpty()) {
            result.add(leaves.poll());
        }
        return result;
    }

    //General Strategy:
    //Start from the leaves (nodes with degree 1).
    //Gradually remove the leaves layer by layer, similar to peeling an onion.
    //The last one or two nodes remaining after peeling off all leaves will be the MHT roots.
    private static List<Integer> findMinHeightTreesReadable(int n, int[][] edges) {
        if (n == 1) {
            return Collections.singletonList(0); // Corner case: single node tree
        }

        // Step 1: Build the graph
        List<Set<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new HashSet<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        // Step 2: Initialize the first layer of leaves
        List<Integer> leaves = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (graph.get(i).size() == 1) { // It's a leaf
                leaves.add(i);
            }
        }

        // Step 3: Trim the leaves layer by layer
        while (n > 2) {
            n -= leaves.size(); // Remove the current leaves
            List<Integer> newLeaves = new ArrayList<>();
            for (int leaf : leaves) {
                // The only neighbor of a leaf
                int neighbor = graph.get(leaf).iterator().next();
                graph.get(neighbor).remove(leaf); // Remove the edge to the leaf
                if (graph.get(neighbor).size() == 1) { // If it becomes a leaf
                    newLeaves.add(neighbor);
                }
            }
            leaves = newLeaves; // Update the leaves for the next round
        }

        // The remaining nodes are the MHT roots
        return leaves;
    }

}
