package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BW_CourseSchedule {
    /**
     * There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1.
     * You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.
     * <p>
     * For example, the pair [0, 1], indicates that to take course 0 you have to first take course 1.
     * Return true if you can finish all courses. Otherwise, return false.
     * <p>
     * Example 1:
     * Input: numCourses = 2, prerequisites = [[1,0]]
     * Output: true
     * Explanation: There are a total of 2 courses to take.
     * To take course 1 you should have finished course 0. So it is possible.
     * <p>
     * Example 2:
     * Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
     * Output: false
     * Explanation: There are a total of 2 courses to take.
     * To take course 1 you should have finished course 0, and to take course 0 you should also have finished course 1. So it is impossible.
     * <p>
     * Constraints:
     * 1 <= numCourses <= 2000
     * 0 <= prerequisites.length <= 5000
     * prerequisites[i].length == 2
     * 0 <= ai, bi < numCourses
     * All the pairs prerequisites[i] are unique.
     **/

    public static void main(String[] args) {
        int numCourses = 2;
        int[][] prerequisites = {{1, 0}, {0, 1}};
        boolean ans = canFinishUsingBFS(numCourses, prerequisites);
        System.out.println(ans);
        ans = canFinishUsingDFS(numCourses, prerequisites);
        System.out.println(ans);
    }

    /**
     * The intuition behind this approach is that if there is a cycle in the graph,
     * there will be at least one node that cannot be visited since it will always have a nonzero indegree.
     * On the other hand, if there are no cycles, all the nodes can be visited by starting from the nodes with no incoming edges
     * and removing their outgoing edges one by one. If all the nodes are visited in the end, it means that it is possible to finish all the courses.
     * determine whether it is possible to finish all the given courses without any cyclic dependencies.
     **/



    /**
     Approach A: DFS with state colors (very interview‑friendly)
     This is a classic cycle detection in directed graphs.

     Core idea
     Build adjacency list: adj[u] = list of courses that depend on u (edges u -> v).

     Track visit state for each node:
     0 = unvisited
     1 = visiting (currently in recursion stack)
     2 = visited (all its descendants checked, no cycle)

     DFS rule:
     When you start exploring a node, mark it visiting.
     For each neighbor:
     If neighbor is visiting → you found a back edge → cycle.
     If neighbor is unvisited, DFS on it.
     After all neighbors are processed with no cycle, mark node visited.

     Pseudocode thinking:
     Build adj from prerequisites.
     state[i] = 0 for all i.
     For each course i:
     If state[i] == 0, run DFS(i).
     If DFS ever report cycle, return false.
     If no cycle for any node, return true.
     */
    public static boolean canFinishUsingDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        // build edges: b -> a
        for (int[] p : prerequisites) {
            int a = p[0], b = p[1];
            graph.get(b).add(a);
        }

        int[] state = new int[numCourses]; // 0=unvisited, 1=visiting, 2=visited

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) {
                if (hasCycle(i, graph, state)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasCycle(int node, List<List<Integer>> graph, int[] state) {
        state[node] = 1; // visiting
        for (int neigbhour : graph.get(node)) {
            if (state[neigbhour] == 1) {
                // found a back edge -> cycle
                return true;
            }
            if (state[neigbhour] == 0 && hasCycle(neigbhour, graph, state)) {
                return true;
            }
        }
        state[node] = 2; // done
        return false;
    }


    /**
     * BFS
     * Using the topological sort algorithm, specifically Kahn's algorithm, to solve this problem.
     * How Kahn's Algorithm Works:
     * Initialization:
     * Start by initializing an empty list to store the topological ordering.
     * Calculate the in-degree (number of incoming edges) for each vertex in the graph.
     * <p>
     * Queue Initialization:
     * Enqueue all vertices with an in-degree of 0 into a queue.
     * <p>
     * Main Loop:
     * While the queue is not empty:
     * Dequeue a vertex from the queue and add it to the topological ordering.
     * For each neighbor of the dequeued vertex:
     * Decrement its in-degree by 1.
     * If the neighbor's in-degree becomes 0, enqueue it into the queue.
     * <p>
     * Termination:
     * If the topological ordering contains all vertices in the graph, return it. Otherwise, there exists a cycle in the graph, and topological sorting is not possible
     */

    //easier to understand
    private static boolean canFinishUsingBFS(int numCourses, int[][] prerequisites) {
        // Create an adjacency list to represent the graph
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        // Create an in-degree array to keep track of the number of incoming edges
        int[] inDegree = new int[numCourses];
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int pre = prerequisite[1];
            graph.get(pre).add(course);
            inDegree[course]++;
        }

        // Initialize the queue with all nodes having zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        // Process nodes using Kahn's algorithm
        int processedNodes = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            processedNodes++;
            for (int neighbor : graph.get(current)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Check if all courses have been processed
        return processedNodes == numCourses;
    }
}
