package DSA_Interview_Questions.RecursionAndBackTracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class N_Queens {

    /**
     * The n-queens puzzle is the problem of placing n queens on an n x n chessboard such that no two queens attack each other.
     * Given an integer n, return all distinct solutions to the n-queens puzzle. You may return the answer in any order.
     * Each solution contains a distinct board configuration of the n-queens' placement, where 'Q' and '.' both indicate a queen and an empty space, respectively.
     * <p>
     * Example 1:
     * Input: n = 4
     * Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
     * Explanation: There exist two distinct solutions to the 4-queens puzzle as shown above
     * <p>
     * Example 2:
     * Input: n = 1
     * Output: [["Q"]]
     * <p>
     * Constraints:
     * 1 <= n <= 9
     **/

    public static void main(String[] args) {
        int n = 4;
        List<List<String>> nQueensSolution = solveNQueens(n);
        System.out.println("Final Answer:-------");
        printNQueens(nQueensSolution);
    }

    private static List<List<String>> solveNQueens(int n) {
        List<List<String>> solution = new ArrayList<>();
        char[][] board = new char[n][n];
        //populating with "." to get the required o/p for empty cells
        for (char[] row : board) {
            Arrays.fill(row, '.');
        }

        boolean[] column = new boolean[n];
        boolean[] upperDiagonalLeft = new boolean[2 * n - 1];
        boolean[] upperDiagonalRight = new boolean[2 * n - 1];


        nQueens(0, board, solution, column, upperDiagonalLeft, upperDiagonalRight);
        return solution;
    }

    private static void nQueens(int row, char[][] arr, List<List<String>> solution,
                                boolean[] column, boolean[] upperDiagonalLeft, boolean[] upperDiagonalRight) {
        //base condition...
        if (row == arr.length) {
            //decided for every row i.e all queens placed


            List<String> ans = new ArrayList<>();
            for (char[] eachRow : arr) {
                ans.add(Arrays.toString(eachRow));
            } //converting to List<String> to add to solution

            solution.add(ans);
            return;
        }

        for (int col = 0; col < arr[row].length; col++) {

            if (column[col] == false && upperDiagonalLeft[row - col + arr.length - 1] == false && upperDiagonalRight[row + col] == false) {
                arr[row][col] = 'Q';
                column[col] = true;
                upperDiagonalRight[row + col] = true;
                upperDiagonalLeft[row - col + arr.length - 1] = true;

                nQueens(row + 1, arr, solution, column, upperDiagonalLeft, upperDiagonalRight);
                // resetting all values
                arr[row][col] = '.';
                column[col] = false;
                upperDiagonalRight[row + col] = false;
                upperDiagonalLeft[row - col + arr.length - 1] = false;
            }

        }
    }

    private static void printNQueens(List<List<String>> nQueensSolution) {
        for (int i = 0; i < nQueensSolution.size(); i++) {
            for (int j = 0; j < nQueensSolution.get(i).size(); j++) {
                System.out.println(nQueensSolution.get(i).get(j));
            }
            System.out.println("-------------");
        }
    }


}
