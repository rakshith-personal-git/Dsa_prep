package DSA_Interview_Questions.Grind150;

public class AI_FloodFill {
    /**
     * An image is represented by an m x n integer grid image where image[i][j] represents the pixel value of the image.
     * You are also given three integers sr, sc, and color. You should perform a flood fill on the image starting from the pixel image[sr][sc].
     * To perform a flood fill, consider the starting pixel,
     * ....plus any pixels connected 4-directionally to the starting pixel of the same color as the starting pixel,
     * ....plus any pixels connected 4-directionally to those pixels (also with the same color), and so on.
     * Replace the color of all of the aforementioned pixels with color.
     * <p>
     * Return the modified image after performing the flood fill.
     * <p>
     * Example 1:
     * Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
     * Output: [[2,2,2],[2,2,0],[2,0,1]]
     * Explanation: From the center of the image with position (sr, sc) = (1, 1) (i.e., the red pixel), all pixels connected by a path of the same color as the starting pixel (i.e., the blue pixels) are colored with the new color.
     * Note the bottom corner is not colored 2, because it is not 4-directionally connected to the starting pixel.
     * <p>
     * Example 2:
     * Input: image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, color = 0
     * Output: [[0,0,0],[0,0,0]]
     * Explanation: The starting pixel is already colored 0, so no changes are made to the image.
     * <p>
     * <p>
     * Constraints:
     * m == image.length
     * n == image[i].length
     * 1 <= m, n <= 50
     * 0 <= image[i][j], color < 216
     * 0 <= sr < m
     * 0 <= sc < n
     */

    public static void main(String[] args) {
        int[][] image = {{1, 1, 1}, {1, 1, 0}, {1, 0, 1}};
        int sr = 1;
        int sc = 1;
        int color = 2;
        int[][] ans = floodFill(image, sr, sc, color);
        for (int[] an : ans) {
            for (int i : an) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    /**
     * why shd we choose dfs for this problem
     * explanation of why DFS is suitable for this flood fill problem:
     * Connected Pixels Exploration: In the flood fill problem, you need to explore and modify all the connected pixels of the same color as the starting pixel.
     * DFS allows you to explore the connected pixels in a systematic manner.
     *
     * Recursion for Backtracking: The flood fill problem requires backtracking when exploring connected pixels. DFS, especially implemented using recursion,
     * naturally lends itself to backtracking. As you move deeper into the recursion, you explore the pixels,
     * and when you reach the base case or encounter a pixel that doesn't need modification, you start backtracking.
     *
     * Exploration in 4 Directions: The problem statement specifies 4-directional connectivity. DFS can easily be adapted to explore in all four directions (up, down, left, right) from a given pixel.
     * Efficiency: The flood fill problem can be efficiently solved using DFS, especially when you mark visited pixels to avoid unnecessary re-exploration.
     * <p>
     * In the provided solution, the dfs function is a recursive DFS implementation. It explores pixels in the four directions and modifies them if they have the same color as the starting pixel. The base case for recursion ensures that the algorithm stops when it reaches the boundary or encounters pixels with a different color.
     * In summary, DFS is chosen for its ability to explore connected components, adaptability to backtracking, ease of handling directional exploration, and efficiency in solving this particular problem.
     */

    private static int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        int originalColor = image[sr][sc];
        if (originalColor != newColor) {
            dfs(image, sr, sc, originalColor, newColor);
        }
        return image;
    }

    private static void dfs(int[][] image, int row, int col, int originalColor, int newColor) {
        if (row < 0 || row >= image.length || col < 0 || col >= image[row].length || image[row][col] != originalColor) {
            return;
        }

        image[row][col] = newColor;

        // Explore 4 directions
        dfs(image, row + 1, col, originalColor, newColor); //below
        dfs(image, row - 1, col, originalColor, newColor); //up
        dfs(image, row, col + 1, originalColor, newColor); //forward
        dfs(image, row, col - 1, originalColor, newColor); //behind
    }

    //Time Complexity (TC): O(M×N), where M is the number of rows and N is the number of columns in the image.
    //Space Complexity (SC): O(M×N), due to the recursion stack in the worst case.
}
