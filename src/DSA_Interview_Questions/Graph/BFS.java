package DSA_Interview_Questions.Graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.*;

public class BFS {
    /**
     * BFS stands for Breadth-First Search.
     * It is a graph traversal algorithm that starts traversing the graph from a selected node (often referred to as the "root" node), and explores all of its neighbors at the present depth before moving on to the nodes at the next depth level.
     * In other words, it explores the neighbor nodes of the current node before moving on to the next level of neighbors.
     *
     * Here's how BFS works:
     * Start with a queue and enqueue the starting node.
     * Dequeue a node from the queue and visit it.
     * Enqueue all its adjacent nodes that have not been visited yet.
     * Repeat steps 2-3 until the queue is empty.
     * */

    /**
     * Some specific applications of BFS include:
     * Shortest Path: BFS can be used to find the shortest path between two nodes in an unweighted graph. Since BFS traverses the graph level by level, the first time a node is visited guarantees the shortest path to it.
     * Maze Solving: BFS can be applied to solve maze problems by finding the shortest path from the start to the exit of the maze.
     * Web Crawling: BFS is used by search engines to crawl and index web pages. Starting from a seed URL, the search engine uses BFS to discover and index all reachable pages on the web.
     * Network Broadcasting: BFS can be used in network protocols to broadcast messages to all nodes in a network.
     * Finding Connected Components: BFS can determine the connected components in an undirected graph, where nodes within the same connected component can reach each other through a path.
     * */


    public class ShortestPath {
        public static List<Integer> shortestPath(Map<Integer, List<Integer>> graph, int start, int end) {
            Queue<Integer> queue = new LinkedList<>();
            Map<Integer, Integer> parentMap = new HashMap<>();
            Set<Integer> visited = new HashSet<>();

            queue.offer(start);
            visited.add(start);
            parentMap.put(start, null);

            while (!queue.isEmpty()) {
                int node = queue.poll();
                if (node == end) {
                    break; // Found the end node
                }
                List<Integer> neighbors = graph.getOrDefault(node, new ArrayList<>());
                for (int neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                        parentMap.put(neighbor, node);
                    }
                }
            }

            // Reconstruct the path from end to start
            List<Integer> path = new ArrayList<>();
            Integer current = end;
            while (current != null) {
                path.add(current);
                current = parentMap.get(current);
            }
            Collections.reverse(path);
            return path;
        }

        public static void main(String[] args) {
            Map<Integer, List<Integer>> graph = new HashMap<>();
            graph.put(0, Arrays.asList(1, 2));
            graph.put(1, Arrays.asList(0, 3, 4));
            graph.put(2, Arrays.asList(0, 4));
            graph.put(3, Arrays.asList(1));
            graph.put(4, Arrays.asList(1, 2));

            int startNode = 0;
            int endNode = 4;
            List<Integer> shortestPath = shortestPath(graph, startNode, endNode);
            System.out.println("Shortest Path from " + startNode + " to " + endNode + ": " + shortestPath);
        }
    }


    public class MazeSolver {
        static class Point {
            int x;
            int y;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        public static List<Point> solveMaze(int[][] maze, Point start, Point end) {
            int m = maze.length;
            int n = maze[0].length;
            Queue<Point> queue = new LinkedList<>();
            Map<Point, Point> parentMap = new HashMap<>();
            Set<Point> visited = new HashSet<>();

            queue.offer(start);
            visited.add(start);
            parentMap.put(start, null);

            while (!queue.isEmpty()) {
                Point current = queue.poll();
                if (current.x == end.x && current.y == end.y) {
                    break; // Found the end point
                }
                int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                for (int[] dir : directions) {
                    int newX = current.x + dir[0];
                    int newY = current.y + dir[1];
                    if (newX >= 0 && newX < m && newY >= 0 && newY < n && maze[newX][newY] == 0) {
                        Point neighbor = new Point(newX, newY);
                        if (!visited.contains(neighbor)) {
                            queue.offer(neighbor);
                            visited.add(neighbor);
                            parentMap.put(neighbor, current);
                        }
                    }
                }
            }

            // Reconstruct the path from end to start
            List<Point> path = new ArrayList<>();
            Point current = end;
            while (current != null) {
                path.add(current);
                current = parentMap.get(current);
            }
            Collections.reverse(path);
            return path;
        }

        public static void main(String[] args) {
            int[][] maze = {
                    {0, 1, 0, 0, 0},
                    {0, 1, 0, 1, 0},
                    {0, 0, 0, 1, 0},
                    {1, 1, 1, 1, 0},
                    {0, 0, 0, 1, 0}
            };
            Point start = new Point(0, 0);
            Point end = new Point(4, 4);

            List<Point> solution = solveMaze(maze, start, end);
            if (solution.isEmpty()) {
                System.out.println("No path found!");
            } else {
                System.out.println("Path found:");
                for (Point point : solution) {
                    System.out.println("(" + point.x + ", " + point.y + ")");
                }
            }
        }
    }


    public class WebCrawler {
        public static void crawl(String startUrl, int maxDepth) {
            Queue<String> queue = new LinkedList<>();
            Set<String> visited = new HashSet<>();

            queue.offer(startUrl);
            visited.add(startUrl);
            int depth = 0;

            while (!queue.isEmpty() && depth <= maxDepth) {
                int size = queue.size();
                System.out.println("Depth " + depth + ": ");
                for (int i = 0; i < size; i++) {
                    String url = queue.poll();
                    System.out.println(url);
                    // In real-world scenario, fetch page content, parse links, and enqueue them
                    // Here, we'll just simulate by adding some hardcoded links
                    List<String> links = getLinksFromUrl(url);
                    for (String link : links) {
                        if (!visited.contains(link)) {
                            queue.offer(link);
                            visited.add(link);
                        }
                    }
                }
                depth++;
            }
        }

        // Simulated method to get links from a web page
        private static List<String> getLinksFromUrl(String url) {
            // Simulated method, return some hardcoded links based on the given URL
            List<String> links = new ArrayList<>();
            if (url.equals("https://example.com")) {
                links.add("https://example.com/page1");
                links.add("https://example.com/page2");
                links.add("https://example.com/page3");
            } else if (url.equals("https://example.com/page1")) {
                links.add("https://example.com");
                links.add("https://example.com/page4");
            }
            // Add more links for other URLs
            return links;
        }

        public static void main(String[] args) {
            String startUrl = "https://example.com";
            int maxDepth = 2;
            crawl(startUrl, maxDepth);
        }
    }


    public class NetworkBroadcast {
        public static void broadcast(Map<Integer, List<Integer>> graph, int source) {
            Queue<Integer> queue = new LinkedList<>();
            Set<Integer> visited = new HashSet<>();

            queue.offer(source);
            visited.add(source);

            while (!queue.isEmpty()) {
                int node = queue.poll();
                System.out.println("Broadcasting message to node: " + node);
                // Add node's neighbors to the queue
                List<Integer> neighbors = graph.getOrDefault(node, new ArrayList<>());
                for (int neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }

        public static void main(String[] args) {
            Map<Integer, List<Integer>> graph = new HashMap<>();
            graph.put(0, Arrays.asList(1, 2));
            graph.put(1, Arrays.asList(0, 3, 4));
            graph.put(2, Arrays.asList(0, 4));
            graph.put(3, Arrays.asList(1));
            graph.put(4, Arrays.asList(1, 2));

            int sourceNode = 0;
            broadcast(graph, sourceNode);
        }
    }


    public class ConnectedComponents {
        public static List<List<Integer>> findConnectedComponents(Map<Integer, List<Integer>> graph) {
            List<List<Integer>> connectedComponents = new ArrayList<>();
            Set<Integer> visited = new HashSet<>();

            for (int node : graph.keySet()) {
                if (!visited.contains(node)) {
                    List<Integer> component = new ArrayList<>();
                    Queue<Integer> queue = new LinkedList<>();

                    queue.offer(node);
                    visited.add(node);

                    while (!queue.isEmpty()) {
                        int currNode = queue.poll();
                        component.add(currNode);
                        List<Integer> neighbors = graph.getOrDefault(currNode, new ArrayList<>());
                        for (int neighbor : neighbors) {
                            if (!visited.contains(neighbor)) {
                                queue.offer(neighbor);
                                visited.add(neighbor);
                            }
                        }
                    }
                    connectedComponents.add(component);
                }
            }
            return connectedComponents;
        }

        public static void main(String[] args) {
            Map<Integer, List<Integer>> graph = new HashMap<>();
            graph.put(0, Arrays.asList(1, 2));
            graph.put(1, Arrays.asList(0, 2));
            graph.put(2, Arrays.asList(0, 1));
            graph.put(3, Arrays.asList(4));
            graph.put(4, Arrays.asList(3));

            List<List<Integer>> connectedComponents = findConnectedComponents(graph);
            System.out.println("Connected Components: " + connectedComponents);
        }
    }

}

