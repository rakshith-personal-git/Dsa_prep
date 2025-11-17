package DSA_Interview_Questions.Grind150;

public class DM_TrappingRainWater2 {
    /**
     * Given an m x n integer matrix heightMap representing the height of each unit cell in a 2D elevation map,
     * return the volume of water it can trap after raining.
     * <p>
     * Example 1:
     * Input: heightMap = [[1,4,3,1,3,2],[3,2,1,3,2,4],[2,3,3,2,3,1]]
     * Output: 4
     * Explanation: After the rain, water is trapped between the blocks.
     * We have two small ponds 1 and 3 units trapped.
     * The total volume of water trapped is 4.
     * <p>
     * Example 2:
     * Input: heightMap = [[3,3,3,3,3],[3,2,2,2,3],[3,2,1,2,3],[3,2,2,2,3],[3,3,3,3,3]]
     * Output: 10
     * <p>
     * Constraints:
     * m == heightMap.length
     * n == heightMap[i].length
     * 1 <= m, n <= 200
     * 0 <= heightMap[i][j] <= 2 * 104
     */

    public static void main(String[] args) {
//        int[][] heightMap = {
//                {1, 4, 3, 1, 3, 2},
//                {3, 2, 1, 3, 2, 4},
//                {2, 3, 3, 2, 3, 1}};
        int[][] heightMap = {
                {12, 13, 1, 12},
                {13, 4, 13, 12},
                {13, 8, 10, 12},
                {12, 13, 12, 12},
                {13, 13, 13, 13}};
//        System.out.println(trapRainWater(heightMap));
        System.out.println(trapRainWater2(heightMap));
    }

    //go over this and learn in deep
    public static int trapRainWater2(int[][] heightMap) {
        int trappedWater = 0;
        int[][] waterLevelMap = new int[heightMap.length][heightMap[0].length];

        for (int i = 1; i < heightMap.length - 1; i++) {
            waterLevelMap[i][0] = heightMap[i][0];
            for (int j = 1; j < heightMap[i].length - 1; j++) {
                waterLevelMap[i][j] = 20000; //constraints limit
            }
            waterLevelMap[i][heightMap[i].length - 1] = heightMap[i][heightMap[i].length - 1];
        }
        for (int i = 0; i < heightMap[0].length; i++) {
            waterLevelMap[0][i] = heightMap[0][i];
            waterLevelMap[heightMap.length - 1][i] = heightMap[heightMap.length - 1][i];
        }

        boolean drain = true;
        while (drain) {
            drain = false;
            for (int i = 1; i < heightMap[0].length - 1; i++) {
                for (int j = 1; j < heightMap.length - 1; j++) {
                    if (waterLevelMap[j][i] > heightMap[j][i]) {
                        if (waterLevelMap[j][i] > waterLevelMap[j][i - 1])
                            waterLevelMap[j][i] = Integer.max(waterLevelMap[j][i - 1], heightMap[j][i]);
                        if (waterLevelMap[j][i] > waterLevelMap[j - 1][i])
                            waterLevelMap[j][i] = Integer.max(waterLevelMap[j - 1][i], heightMap[j][i]);
                    }
                }
            }
            for (int i = heightMap[0].length - 2; i > 0; i--) {
                for (int j = heightMap.length - 2; j > 0; j--) {
                    if (waterLevelMap[j][i] > heightMap[j][i]) {
                        if (waterLevelMap[j][i] > waterLevelMap[j][i + 1])
                            waterLevelMap[j][i] = Integer.max(waterLevelMap[j][i + 1], heightMap[j][i]);
                        if (waterLevelMap[j][i] > waterLevelMap[j + 1][i])
                            waterLevelMap[j][i] = Integer.max(waterLevelMap[j + 1][i], heightMap[j][i]);
                        if (waterLevelMap[j][i] < waterLevelMap[j][i + 1] && waterLevelMap[j][i + 1] > heightMap[j][i + 1]
                                || waterLevelMap[j][i] < waterLevelMap[j + 1][i] && waterLevelMap[j + 1][i] > heightMap[j + 1][i])
                            drain = true;
                    }
                }
            }
        }

        int[][] result = new int[heightMap.length][heightMap[0].length];
        for (int i = 1; i < waterLevelMap.length - 1; i++) {
            for (int j = 1; j < waterLevelMap[i].length - 1; j++) {
                trappedWater += waterLevelMap[i][j] - heightMap[i][j];
                result[i][j] = waterLevelMap[i][j] - heightMap[i][j];
            }
        }

        return trappedWater;
    }


    //this is incorrect
    private static int trapRainWater(int[][] heightMap) {
        int m = heightMap.length;
        int n = heightMap[0].length;
        int[][] left = new int[m][n];
        int[][] right = new int[m][n];
        int[][] up = new int[m][n];
        int[][] down = new int[m][n];


        //matrix which stores the maximum height from the index to its left
        for (int i = 0; i < m; i++) {
            left[i][0] = heightMap[i][0];
            for (int j = 1; j < n; j++) {
                left[i][j] = Math.max(left[i][j - 1], heightMap[i][j]);
            }
        }

        //matrix which stores the maximum height from the index to its right
        for (int i = 0; i < m; i++) {
            right[i][n - 1] = heightMap[i][n - 1];
            for (int j = n - 2; j >= 0; j--) {
                right[i][j] = Math.max(right[i][j + 1], heightMap[i][j]);
            }
        }

        //matrix which stores the maximum height from the index to its top
        for (int j = 0; j < n; j++) {
            up[0][j] = heightMap[0][j];
            for (int i = 1; i < m; i++) {
                up[i][j] = Math.max(up[i - 1][j], heightMap[i][j]);
            }
        }

        //matrix which stores the maximum height from the index to its bottom
        for (int j = 0; j < n; j++) {
            down[m - 1][j] = heightMap[m - 1][j];
            for (int i = m - 2; i >= 0; i--) {
                down[i][j] = Math.max(down[i + 1][j], heightMap[i][j]);
            }
        }


        int ans = 0;
        int[][] result = new int[m][n];
        //formula is min(left, right, up, down) - current index height
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int minHeight = Math.min(Math.min(left[i][j], right[i][j]), Math.min(up[i][j], down[i][j]));
                ans += Math.max(0, minHeight - heightMap[i][j]); // Ensure no negative value contributes to the trapped water
                result[i][j] = Math.max(0, minHeight - heightMap[i][j]);
            }
        }


        return ans;
    }

}
