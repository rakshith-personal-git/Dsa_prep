package DSA_Interview_Questions.Graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CycleInUndirectedGraph {
    /**
     * Check if undirected graph is Cyclic
     * Input:
     * V = 5, E = 5
     * adj = {{1}, {0, 2, 4}, {1, 3}, {2, 4}, {1, 3}}
     * Output: 1
     * Explanation:
     * 0->1->2->3->4->1 is a cycle.
     */

    public static void main(String[] args) {
        int V = 5; //number of vertices/nodes
        int E = 5; //number of edges
        ArrayList<Integer>[] graph = new ArrayList[V];

        // Initialize the array with ArrayLists
        for (int i = 0; i < V; i++) {
            graph[i] = new ArrayList<>();
        }

        // Populate the array based on the given adjacency list
        graph[0].add(1);
        graph[1].addAll(new ArrayList<>(List.of(0, 2, 4))); //these indicates the neighbors of each vertex, in this case 0,2,4 are neighbours of node 1
        graph[2].addAll(new ArrayList<>(List.of(1, 3)));
        graph[3].addAll(new ArrayList<>(List.of(2, 4)));
        graph[4].addAll(new ArrayList<>(List.of(1, 3)));

        // Example: Pass the ArrayList<Integer>[] to a function
        boolean ans = isCyclicUsingDFS(graph);
        System.out.println("using DFS: " + ans);
        boolean ans2 = isCyclicUsingBFS(graph);
        System.out.println("using BFS: " + ans2);
    }


    private static boolean isCyclicUsingDFS(ArrayList<Integer>[] graph) {
        boolean[] visited = new boolean[graph.length];
        boolean ans = false;

        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                if (isCyclicDFS(graph, i, visited, -1)) {
                    ans = true;
                    break;
                }
            }
        }
        return ans;
    }

    private static boolean isCyclicDFS(ArrayList<Integer>[] graph, int src, boolean[] visited, int parent) {
        if (visited[src] == true) {
            return true;
        }
        visited[src] = true;
        for (int neighbour : graph[src]) {
            if (!visited[neighbour]) {
                if (isCyclicDFS(graph, neighbour, visited, src)) {
                    return true;
                }
            } else if (neighbour != parent) {
                return true;
            }
        }
        return false;
    }


    private static boolean isCyclicUsingBFS(ArrayList<Integer>[] graph) {
        boolean[] visited = new boolean[graph.length];
        boolean ans = false;

        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                if (isCyclicBFS(graph, i, visited)) {
                    ans = true;
                    break;
                }
            }
        }
        return ans;
    }

    private static boolean isCyclicBFS(ArrayList<Integer>[] graph, int src, boolean[] visited) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(src);
        visited[src] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int neighbor : graph[current]) {
                if (!visited[neighbor]) {
                    queue.add(neighbor);
                    visited[neighbor] = true;
                } else if (current != neighbor) {
                    // If the neighbor is visited and not the parent, it's a back edge
                    return true;
                }
            }
        }
        return false;
    }

    //TC is O(V + E) , SC is O(V + height of Tree)
}
