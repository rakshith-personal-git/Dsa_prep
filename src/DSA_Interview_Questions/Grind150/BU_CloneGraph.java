package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BU_CloneGraph {
    /**
     * Given a reference of a node in a connected undirected graph.
     * Return a deep copy (clone) of the graph.
     * Each node in the graph contains a value (int) and a list (List[Node]) of its neighbors.
     * <p>
     * class Node {
     * public int val;
     * public List<Node> neighbors;
     * }
     * <p>
     * <p>
     * Test case format:
     * For simplicity, each node's value is the same as the node's index (1-indexed).
     * For example, the first node with val == 1, the second node with val == 2, and so on. The graph is represented in the test case using an adjacency list.
     * An adjacency list is a collection of unordered lists used to represent a finite graph. Each list describes the set of neighbors of a node in the graph.
     * The given node will always be the first node with val = 1. You must return the copy of the given node as a reference to the cloned graph.
     * <p>
     * Example 1:
     * Input: adjList = [[2,4],[1,3],[2,4],[1,3]]
     * Output: [[2,4],[1,3],[2,4],[1,3]]
     * Explanation: There are 4 nodes in the graph.
     * 1st node (val = 1)'s neighbors are 2nd node (val = 2) and 4th node (val = 4).
     * 2nd node (val = 2)'s neighbors are 1st node (val = 1) and 3rd node (val = 3).
     * 3rd node (val = 3)'s neighbors are 2nd node (val = 2) and 4th node (val = 4).
     * 4th node (val = 4)'s neighbors are 1st node (val = 1) and 3rd node (val = 3).
     * <p>
     * Example 2:
     * Input: adjList = [[]]
     * Output: [[]]
     * Explanation: Note that the input contains one empty list. The graph consists of only one node with val = 1 and it does not have any neighbors.
     * <p>
     * Example 3:
     * Input: adjList = []
     * Output: []
     * Explanation: This an empty graph, it does not have any nodes.
     * <p>
     * Constraints:
     * The number of nodes in the graph is in the range [0, 100].
     * 1 <= Node.val <= 100
     * Node.val is unique for each node.
     * There are no repeated edges and no self-loops in the graph.
     * The Graph is connected and all nodes can be visited starting from the given node.
     */

    static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

    public static Node createGraph(int[][] adjList) {
        Node[] nodes = new Node[adjList.length];

        // Create nodes
        for (int i = 0; i < adjList.length; i++) {
            nodes[i] = new Node(i + 1); // Assuming nodes are 1-indexed
        }

        // Connect neighbors
        for (int i = 0; i < adjList.length; i++) {
            for (int neighborIndex : adjList[i]) {
                nodes[i].neighbors.add(nodes[neighborIndex - 1]); // Adjusting index for 1-based indexing
            }
        }

        return nodes[0]; // Return the top node (val = 1)
    }

    public static void main(String[] args) {
        int[][] adjList = {{2, 4}, {1, 3}, {2, 4}, {1, 3}};
        Node node = createGraph(adjList);
        printGraphInArray(node);
        System.out.println("solutions:- ");
        Node ans = cloneGraph(node);
        printGraphInArray(ans);
        System.out.println();
        ans = cloneGraph2(node);
        printGraphInArray(ans);
    }


    private static Map<Node, Node> map = new HashMap<>();  //Keep a map of old node to new node

    private static Node cloneGraph(Node node) {
        if (node == null) {
            return null;
        }

        Node newNode = new Node(node.val); //Whenever a new node is created update the map
        map.put(node, newNode);
        for (Node neighbour : node.neighbors) {
            if (!map.containsKey(neighbour)) {
                newNode.neighbors.add(cloneGraph(neighbour)); //If we encounter a node which is not in map, we call clone again
            } else {
                newNode.neighbors.add(map.get(neighbour)); //If map already contains the old to new node mapping, use the new node
            }
        }
        return newNode;
    }

    //a bit more optimised
    public static Node cloneGraph2(Node node) {
        if (node == null) {
            return null;
        }

        Map<Node, Node> map = new HashMap<>();
        return dfs(node, map);
    }

    private static Node dfs(Node node, Map<Node, Node> map) {
        if (map.containsKey(node)) {
            return map.get(node);
        }

        Node clone = new Node(node.val);
        map.put(node, clone);

        for (Node neighbor : node.neighbors) {
            Node neighborClone = dfs(neighbor, map);
            clone.neighbors.add(neighborClone);
        }

        return clone;
    }
    //TC is O(V+E)
    //SC is O(V)


    private static void printGraphInArray(Node node) {
        Set<Node> visited = new HashSet<>();
        dfsForPrint(node, visited);
    }

    private static void dfsForPrint(Node node, Set<Node> visited) {
        if (visited.contains(node)) {
            return;
        }

        visited.add(node);
        System.out.print("Node " + node.val + " has neighbors: ");
        for (Node neighbor : node.neighbors) {
            System.out.print(neighbor.val + " ");
        }
        System.out.println();

        for (Node neighbor : node.neighbors) {
            dfsForPrint(neighbor, visited);
        }
    }
}
