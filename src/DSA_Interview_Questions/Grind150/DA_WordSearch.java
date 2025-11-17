package DSA_Interview_Questions.Grind150;

public class DA_WordSearch {
    /**
     * Given an m x n grid of characters board and a string word, return true if word exists in the grid.
     * The word can be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or vertically neighboring.
     * The same letter cell may not be used more than once.
     * <p>
     * Example 1:
     * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
     * Output: true
     * <p>
     * Example 2:
     * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "SEE"
     * Output: true
     * <p>
     * Example 3:
     * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCB"
     * Output: false
     * <p>
     * Constraints:
     * m == board.length
     * n = board[i].length
     * 1 <= m, n <= 6
     * 1 <= word.length <= 15
     * board and word consists of only lowercase and uppercase English letters.
     * <p>
     * Follow up: Could you use search pruning to make your solution faster with a larger board?
     */

    public static void main(String[] args) {
        String word = "ABCCED";
        char[][] board = {{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}};
        System.out.println(exist(board, word));
    }

    private static boolean exist(char[][] board, String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        char startingCharacter = word.charAt(0);
        boolean[][] visited = new boolean[board.length][board[0].length];


        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == startingCharacter) {
                    if (backTrack(row, col, word, 0, visited, board)) { //DFS
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean backTrack(int row, int col, String word, int level, boolean[][] visited, char[][] board) {
        if (level == word.length()) {
            return true;
        }

        int rowMax = board.length;
        int colMax = board[0].length;

        if (row < 0 || row >= rowMax || col < 0 || col >= colMax) {
            return false;
        }

        if (visited[row][col]) {
            return false;
        }

        if (board[row][col] != word.charAt(level)) {
            return false;
        }


        visited[row][col] = true;

        if (backTrack(row + 1, col, word, level + 1, visited, board) ||
                backTrack(row - 1, col, word, level + 1, visited, board) ||
                backTrack(row, col + 1, word, level + 1, visited, board) ||
                backTrack(row, col - 1, word, level + 1, visited, board)) {
            return true;
        }


        visited[row][col] = false;
        return false;
    }
}
