package DSA_Interview_Questions.Arrays;

public class SpiralOrderMatrix_II {

    /**
     * Problem Description
     * Given an integer A, generate a square matrix filled with elements from 1 to A2 in spiral order and return the generated square matrix.
     * <p>
     * Problem Constraints
     * 1 <= A <= 1000
     * <p>
     * Input Format
     * First and only argument is integer A
     * <p>
     * Output Format
     * Return a 2-D matrix which consists of the elements added in spiral order.
     * <p>
     * Example Input
     * Input 1:
     * 1
     * <p>
     * Input 2:
     * 2
     * <p>
     * Input 3:
     * 5
     * <p>
     * Example Output
     * Output 1:
     * [ [1] ]
     * <p>
     * Output 2:
     * [ [1, 2],
     * [4, 3] ]
     * <p>
     * Output 3:
     * [ [1,   2,  3,  4, 5],
     * [16, 17, 18, 19, 6],
     * [15, 24, 25, 20, 7],
     * [14, 23, 22, 21, 8],
     * [13, 12, 11, 10, 9] ]
     * <p>
     * <p>
     * Example Explanation
     * Explanation 1:
     * <p>
     * Only 1 is to be arranged.
     * Explanation 2:
     * <p>
     * 1 --> 2
     * ......|
     * ......|
     * 4<--- 3
     **/

    public static void main(String[] args) {
        int n = 5;
        int[][] reqMatrix = generateMatrix(n);
        for (int i = 0; i < reqMatrix.length; i++) {
            for (int j = 0; j < reqMatrix[i].length; j++) {
                System.out.print(reqMatrix[i][j] + " ");
            }
            System.out.println("");
        }
    }

    private static int[][] generateMatrix(int n) {
        int size = n * n;
        int rMin = 0;
        int rMax = n - 1;
        int cMin = 0;
        int cMax = n - 1;

        int[][] reqMatrix = new int[n][n];

        int count = 1;
        while (count <= size) {
            //top boundary
            for (int col = cMin; col <= cMax && count <= size; col++) {
                reqMatrix[rMin][col] = count;
                count++;
            }
            rMin++;

            //right boundary
            for (int row = rMin; row <= rMax && count <= size; row++) {
                reqMatrix[row][cMax] = count;
                count++;
            }
            cMax--;

            //bottom boundary
            for (int col = cMax; col >= cMin && count <= size; col--) {
                reqMatrix[rMax][col] = count;
                count++;
            }
            rMax--;

            //left boundary
            for (int row = rMax; row >= rMin && count <= size; row--) {
                reqMatrix[row][cMin] = count;
                count++;
            }
            cMin++;
        }

        return reqMatrix;
    }
}
