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


    //link to explain the logic if can't understand -> https://youtu.be/Zbbe9FYVnM4?si=YPFJYjYkiq2yKNhY
    private static boolean canFinishUsingDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>()); //basically numCourses is number of vertices and prerequisites are directed edges
        }

        for (int[] pair : prerequisites) {
            graph.get(pair[1]).add(pair[0]); //because in constraints its mentioned that prerequisites[i].length == 2
        }

        boolean[] visited = new boolean[numCourses];
        boolean[] recursion = new boolean[numCourses];

        for (int i = 0; i< numCourses; i++) {
            if (!visited[i]) {
                if (!dfs(graph, i, visited, recursion)) {
                    return false;
                }
            }
        }
        return true;


    }

    private static boolean dfs(List<List<Integer>> graph, int i, boolean[] visited, boolean[] recursion) {
        visited[i] = true;
        recursion[i] = true;

        if (graph.get(i) != null) {
            for (int neighbour : graph.get(i)) {
                if (recursion[neighbour]) {
                    return false;
                }
                if (!visited[neighbour]) {
                    if (!dfs(graph, neighbour, visited, recursion)) {
                        return false;
                    }
                }
            }
        }
        recursion[i] = false;
        return true;
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
