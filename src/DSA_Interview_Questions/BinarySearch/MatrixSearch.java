package DSA_Interview_Questions.BinarySearch;

public class MatrixSearch {

    /**
     * Given a matrix of integers A of size N x M and an integer B. Write an efficient algorithm that searches for integer B in matrix A.
     * <p>
     * This matrix A has the following properties:
     * <p>
     * Integers in each row are sorted from left to right.
     * The first integer of each row is greater than or equal to the last integer of the previous row.
     * Return 1 if B is present in A, else return 0.
     * <p>
     * NOTE: Rows are numbered from top to bottom, and columns are from left to right.
     * <p>
     * <p>
     * <p>
     * Problem Constraints
     * 1 <= N, M <= 1000
     * 1 <= A[i][j], B <= 106
     * <p>
     * <p>
     * <p>
     * Input Format
     * The first argument given is the integer matrix A.
     * The second argument given is the integer B.
     * <p>
     * <p>
     * <p>
     * Output Format
     * Return 1 if B is present in A else, return 0.
     * <p>
     * <p>
     * <p>
     * Example Input
     * Input 1:
     * A = [
     * [1,   3,  5,  7]
     * [10, 11, 16, 20]
     * [23, 30, 34, 50]
     * ]
     * B = 3
     * <p>
     * Input 2:
     * A = [
     * [5, 17, 100, 111]
     * [119, 120, 127, 131]
     * ]
     * B = 3
     * <p>
     * <p>
     * Example Output
     * Output 1:
     * 1
     * <p>
     * Output 2:
     * 0
     * <p>
     * <p>
     * Example Explanation
     * Explanation 1:
     * 3 is present in the matrix at A[0][1] position so return 1.
     * <p>
     * Explanation 2:
     * 3 is not present in the matrix so return 0.
     **/

    public static void main(String[] args) {
        int[][] matrix = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 50}};
        int target = 3;

        boolean index = searchIn2dMatrix(matrix, target);
        System.out.println(index);

    }


    //start from topRight, then traverse down if value is less or traverse left if value is more

    private static boolean searchIn2dMatrix(int[][] matrix, int target) {
        int row = 0;
        int col = matrix[0].length - 1;

        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] == target) {
                return true;
            }

            if (matrix[row][col] < target) {
                row++; //traverse down
            } else {
                col--; //traverse left
            }
        }

        return false;
    }
}
