package DSA_Interview_Questions.RecursionAndBackTracking;

public class SudokuSolver {

    /**
     * Write a program to solve a Sudoku puzzle by filling the empty cells.
     * <p>
     * A sudoku solution must satisfy all the following rules:-
     * Each of the digits 1-9 must occur exactly once in each row.
     * Each of the digits 1-9 must occur exactly once in each column.
     * Each of the digits 1-9 must occur exactly once in each of the 9 3x3 sub-boxes of the grid.
     * The '.' character indicates empty cells.
     * <p>
     * Example 1:
     * Input: board = [["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]
     * Output: [["5","3","4","6","7","8","9","1","2"],["6","7","2","1","9","5","3","4","8"],["1","9","8","3","4","2","5","6","7"],["8","5","9","7","6","1","4","2","3"],["4","2","6","8","5","3","7","9","1"],["7","1","3","9","2","4","8","5","6"],["9","6","1","5","3","7","2","8","4"],["2","8","7","4","1","9","6","3","5"],["3","4","5","2","8","6","1","7","9"]]
     * <p>
     * Explanation: The input board and the only valid solution is shown below:
     * [  ["5", "3", ".", ".", "7", ".", ".", ".", "."],
     * ["6", ".", ".", "1", "9", "5", ".", ".", "."],
     * [".", "9", "8", ".", ".", ".", ".", "6", "."],
     * ["8", ".", ".", ".", "6", ".", ".", ".", "3"],
     * ["4", ".", ".", "8", ".", "3", ".", ".", "1"],
     * ["7", ".", ".", ".", "2", ".", ".", ".", "6"],
     * [".", "6", ".", ".", ".", ".", "2", "8", "."],
     * [".", ".", ".", "4", "1", "9", ".", ".", "5"],
     * [".", ".", ".", ".", "8", ".", ".", "7", "9"]  ]
     * <p>
     * [  ["5", "3", "4", "6", "7", "8", "9", "1", "2"],
     * ["6", "7", "2", "1", "9", "5", "3", "4", "8"],
     * ["1", "9", "8", "3", "4", "2", "5", "6", "7"],
     * ["8", "5", "9", "7", "6", "1", "4", "2", "3"],
     * ["4", "2", "6", "8", "5", "3", "7", "9", "1"],
     * ["7", "1", "3", "9", "2", "4", "8", "5", "6"],
     * ["9", "6", "1", "5", "3", "7", "2", "8", "4"],
     * ["2", "8", "7", "4", "1", "9", "6", "3", "5"],
     * ["3", "4", "5", "2", "8", "6", "1", "7", "9"]  ]
     * <p>
     * <p>
     * Constraints:
     * board.length == 9
     * board[i].length == 9
     * board[i][j] is a digit or '.'.
     * It is guaranteed that the input board has only one solution.
     **/

    public static void main(String[] args) {
        char[][] arr = {{'5', '3', '.', '.', '7', '.', '.', '.', '.'},
                {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
                {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
                {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
                {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
                {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
                {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
                {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
                {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};
//        char[][] arr = {{'.','.','9','7','4','8','.','.','.'},{'7','.','.','.','.','.','.','.','.'},{'.','2','.','1','.','9','.','.','.'},{'.','.','7','.','.','.','2','4','.'},{'.','6','4','.','1','.','5','9','.'},{'.','9','8','.','.','.','3','.','.'},{'.','.','.','8','.','3','.','2','.'},{'.','.','.','.','.','.','.','.','6'},{'.','.','.','2','7','5','9','.','.'}};
        solveSudoku(arr);
        printSudoku(arr);

    }

    //TC is O(9^M) , SC is O(N^2)
    private static void solveSudoku(char[][] board) {
        if (board == null || board.length != 9 || board[0].length != 9) {
            return;
        }
        sudokuSolver(board, 0, 0);
    }

    private static boolean sudokuSolver(char[][] board, int rows, int columns) {
        if (rows == board.length) {
            //i,e all 9(0th to 8th) rows have been filled and current row value is 9
            return true;
        }

        int nextRow;
        int nextColumn;
        if (columns == 8) {
            nextRow = rows + 1; //going to next row
            nextColumn = 0; //resetting the column to 0
        } else {
            nextRow = rows; // keeping the row same
            nextColumn = columns + 1; // going to next column
        }

        if (board[rows][columns] == '.') {
            for (int val = 1; val <= 9; val++) {
                if (safeToPlace(board, rows, columns, val)) {
                    board[rows][columns] = (char) (val + '0');
                    if (sudokuSolver(board, nextRow, nextColumn)) {
                        return true;
                    }
                    board[rows][columns] = '.';
                }
            }
        } else {
            if (sudokuSolver(board, nextRow, nextColumn)) {
                return true;
            }
        }
        return false;
    }

    private static boolean safeToPlace(char[][] board, int row, int col, int val) {
        //check the row
        for (int columns = 0; columns < board[0].length; columns++) {
            if (board[row][columns] == (char) (val + '0')) {
                return false;
            }
        }

        //check the column
        for (int rows = 0; rows < board[0].length; rows++) {
            if (board[rows][col] == (char) (val + '0')) {
                return false;
            }
        }

        //check the 3*3 grid
        row = row - (row % 3);
        col = col - (col % 3);

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                if (board[row + i][col + j] == (char) (val + '0')) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void printSudoku(char[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}
