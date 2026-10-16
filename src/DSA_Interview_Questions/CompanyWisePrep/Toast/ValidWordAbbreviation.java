package DSA_Interview_Questions.CompanyWisePrep.Toast;

public class ValidWordAbbreviation {

    /**
     * Determines if abbr is a valid abbreviation for word.
     *
     * @param word Original target string.
     * @param abbr Candidate abbreviation string.
     * @return true if valid, false otherwise.
     */
    public static boolean validWordAbbreviation(String word, String abbr) {
        if (word == null || abbr == null) {
            return false;
        }

        int i = 0; // Pointer for word
        int j = 0; // Pointer for abbr

        int n = word.length();
        int m = abbr.length();

        while (i < n && j < m) {
            char abbrChar = abbr.charAt(j);

            // Rule 1: Leading zeros are strictly invalid
            if (abbrChar == '0') {
                return false;
            }

            // Rule 2: If character, perform exact character matching
            if (Character.isLetter(abbrChar)) {
                if (word.charAt(i) != abbrChar) {
                    return false;
                }
                i++;
                j++;
            } 
            // Rule 3: If digit, parse complete multi-digit skip number
            else if (Character.isDigit(abbrChar)) {
                int num = 0;
                while (j < m && Character.isDigit(abbr.charAt(j))) {
                    num = num * 10 + (abbr.charAt(j) - '0');
                    j++;
                }
                // Skip word pointer by calculated digit count
                i += num;
            }
        }

        // Rule 4: Both pointers must reach the exact end of their strings
        return i == n && j == m;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test 1: Prompt Example 1 (word = "apple", abbr = "a2e" -> Expected: false)
        runTest("Test 1 (Skipping too short/mismatch)", "apple", "a2e", false);

        // Test 2: Standard Valid Case (word = "internationalization", abbr = "i12iz4n")
        runTest("Test 2 (Multi-digit valid abbreviation)", "internationalization", "i12iz4n", true);

        // Test 3: Leading Zero Case (word = "a", abbr = "01" -> Expected: false)
        runTest("Test 3 (Leading zero invalid)", "a", "01", false);

        // Test 4: Single Number Skipping Whole Word (word = "apple", abbr = "5")
        runTest("Test 4 (Numeric skip whole word)", "apple", "5", true);

        // Test 5: Number exceeds word length (word = "hi", abbr = "3")
        runTest("Test 5 (Skip exceeds length)", "hi", "3", false);
    }

    private static void runTest(String testName, String word, String abbr, boolean expected) {
        boolean result = validWordAbbreviation(word, abbr);
        if (result == expected) {
            System.out.println(testName + " PASSED -> Output: " + result);
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + ", Got: " + result);
        }
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N + M)
 *   Where N is word.length() and M is abbr.length(). Pointer `j` moves from 
 *   left to right across `abbr` in a single pass, and pointer `i` advances 
 *   across `word` by character matches or numerical jumps.
 *
 * Space Complexity: O(1)
 *   Only scalar primitive variables (pointers i, j, lengths n, m, and num) 
 *   are allocated.
 * ============================================================================
 */