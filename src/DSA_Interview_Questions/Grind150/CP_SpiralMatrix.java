package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.List;

public class CP_SpiralMatrix {
    /**
     * Given an m x n matrix, return all elements of the matrix in spiral order.
     * <p>
     * Example 1:
     * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
     * Output: [1,2,3,6,9,8,7,4,5]
     * <p>
     * Example 2:
     * Input: matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
     * Output: [1,2,3,4,8,12,11,10,9,5,6,7]
     * <p>
     * Constraints:
     * m == matrix.length
     * n == matrix[i].length
     * 1 <= m, n <= 10
     * -100 <= matrix[i][j] <= 100
     */

    public static void main(String[] args) {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        List<Integer> ans = spiralOrder(matrix);
        ans.forEach(i -> System.out.printf(i + " "));
    }

    private static List<Integer> spiralOrder(int[][] matrix) {
        int rowMin = 0;
        int rowMax = matrix.length - 1;
        int colMin = 0;
        int colMax = matrix[0].length - 1;
        List<Integer> result = new ArrayList<>();

        int size = matrix.length * matrix[0].length;
        int count = 1;

        while (count <= size) {
            //top boundary
            for (int col = colMin; col <= colMax && count <= size; col++) {
                result.add(matrix[rowMin][col]);
                count++;
            }
            rowMin++;

            //right boundary
            for (int row = rowMin; row <= rowMax && count <= size; row++) {
                result.add(matrix[row][colMax]);
                count++;
            }
            colMax--;

            //bottom boundary
            for (int col = colMax; col >= colMin && count <= size; col--) {
                result.add(matrix[rowMax][col]);
                count++;
            }
            rowMax--;

            //left boundary
            for (int row = rowMax; row >= rowMin && count <= size; row--) {
                result.add(matrix[row][colMin]);
                count++;
            }
            colMin++;
        }

        return result;
    }
}
