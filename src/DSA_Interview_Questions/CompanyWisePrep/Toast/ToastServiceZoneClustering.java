package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.*;

/**
 * Solution for Connected Components / Service Zone Clustering (LeetCode 547 variant).
 *
 * <p>Strategy: Graph Traversal using Depth-First Search (DFS) with a visited array.
 *
 * <p>Time Complexity: O(N^2) where N is the number of tables/locations.
 * Space Complexity: O(N) auxiliary space for visited array and call stack.
 */
public class ToastServiceZoneClustering {

    /**
     * Calculates total number of connected service zones (connected components).
     *
     * @param isConnected N x N adjacency matrix where 1 indicates a connection.
     * @return Total count of connected components.
     */
    public static int findNumOfServiceZones(int[][] isConnected) {
        // Defensive check for invalid or empty matrix inputs
        if (isConnected == null || isConnected.length == 0 || isConnected[0].length == 0) {
            return 0;
        }

        int n = isConnected.length;
        //Since connections are undirected, isConnected[i][j] == isConnected[j][i] for all $i$ and $j$.
        //By keeping a visited array of size $N$, we ensure every table is processed in the outer loop and marked visited at most once.
        boolean[] visited = new boolean[n];
        int serviceZoneCount = 0;

        // Iterate through all table nodes
        for (int i = 0; i < n; i++) {
            // If table i has not been processed, it starts a new service zone
            if (!visited[i]) {
                serviceZoneCount++;
                // Perform DFS to mark all tables in this zone as visited
                dfs(isConnected, visited, i);
            }
        }

        return serviceZoneCount;
    }

    /**
     * Recursive DFS Helper: Traverses all reachable tables starting from current table 'node'.
     */
    private static void dfs(int[][] isConnected, boolean[] visited, int node) {
        visited[node] = true;

        // Scan all potential neighbor nodes for 'node'
        for (int neighbor = 0; neighbor < isConnected.length; neighbor++) {
            // If connected and neighbor has not been visited yet
            if (isConnected[node][neighbor] == 1 && !visited[neighbor]) {
                dfs(isConnected, visited, neighbor);
            }
        }
    }

    // --- Main Method with Executable Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: 3 tables, 2 service zones ({0,1} and {2})
        int[][] test1 = {
            {1, 1, 0},
            {1, 1, 0},
            {0, 0, 1}
        };
        runTest("Test Case 1 (Standard 2 Zones)", test1, 2);

        // Test Case 2: Completely disconnected (3 single-table zones)
        int[][] test2 = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        };
        runTest("Test Case 2 (All Disconnected)", test2, 3);

        // Test Case 3: Fully connected (1 zone for all tables)
        int[][] test3 = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };
        runTest("Test Case 3 (Fully Connected)", test3, 1);

        // Test Case 4: Indirect connections ({0,1} and {1,2} -> all 1 zone)
        int[][] test4 = {
            {1, 1, 0},
            {1, 1, 1},
            {0, 1, 1}
        };
        runTest("Test Case 4 (Indirect Connection)", test4, 1);

        // Test Case 5: Single Table
        int[][] test5 = {
            {1}
        };
        runTest("Test Case 5 (Single Table)", test5, 1);
    }

    private static void runTest(String testName, int[][] isConnected, int expected) {
        int result = findNumOfServiceZones(isConnected);
        if (result == expected) {
            System.out.println(testName + " PASSED -> Output: " + result);
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + ", Got: " + result);
        }
    }
}