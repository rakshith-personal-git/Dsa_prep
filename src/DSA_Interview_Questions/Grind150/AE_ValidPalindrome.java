package DSA_Interview_Questions.Grind150;

public class AE_ValidPalindrome {
    /**
     * A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters,
     * it reads the same forward and backward. Alphanumeric characters include letters and numbers.
     * Given a string s, return true if it is a palindrome, or false otherwise.
     * <p>
     * Example 1:
     * Input: s = "A man, a plan, a canal: Panama"
     * Output: true
     * Explanation: "amanaplanacanalpanama" is a palindrome.
     * <p>
     * Example 2:
     * Input: s = "race a car"
     * Output: false
     * Explanation: "raceacar" is not a palindrome.
     * <p>
     * Example 3:
     * Input: s = " "
     * Output: true
     * Explanation: s is an empty string "" after removing non-alphanumeric characters.
     * Since an empty string reads the same forward and backward, it is a palindrome.
     * <p>
     * Constraints:
     * 1 <= s.length <= 2 * 105
     * s consists only of printable ASCII characters.
     */

    public static void main(String[] args) {
        String s = "A man, a plan, a canal: Panama";
        boolean ans = isPalindrome(s);
        boolean ans2 = isPalindrome2(s);
        boolean ans3 = isPalindrome3(s);
        System.out.println(ans);
        System.out.println(ans2);
        System.out.println(ans3);
    }


    //brute force approach
    private static boolean isPalindrome(String s) {
        // Step 1: Convert to lowercase
        s = s.toLowerCase();

        // Step 2: Remove non-alphanumeric characters
        s = s.replaceAll("[^a-zA-Z0-9]", "");

        // Step 3: Check if it's a palindrome
        return s.contentEquals(new StringBuilder(s).reverse());
    }

    //optimalApproach TC is O(n) and SC is O(1)
    private static boolean isPalindrome2(String s) {
        if (s == null || s.isEmpty() || s.length() == 1) {
            return true;
        }
        int frontPointer = 0;
        int backPointer = s.length() - 1;

        while (frontPointer < backPointer) {
            char frontChar = s.charAt(frontPointer);
            char backChar = s.charAt(backPointer);

            if (!Character.isLetterOrDigit(frontChar)) {
                frontPointer++;
                continue;
            } else if (!Character.isLetterOrDigit(backChar)) {
                backPointer--;
                continue;
            } else {
                if (Character.toLowerCase(frontChar) != Character.toLowerCase(backChar)) {
                    return false;
                }
                frontPointer++;
                backPointer--;
            }
        }
        return true;
    }



    //another optimalApproach
    public static boolean isPalindrome3(String s) {
        int i = 0;
        int j = s.length() - 1;

        char leftCharacter;
        char rightCharacter;

        while (i < j) {
            leftCharacter = toLowerCase(s.charAt(i));
            rightCharacter = toLowerCase(s.charAt(j));

            if (!isAlphaNumeric(leftCharacter)) {
                i++;
                continue;
            }

            if (!isAlphaNumeric(rightCharacter)) {
                j--;
                continue;
            }

            if (leftCharacter != rightCharacter) {
                return false;
            }

            i++;
            j--;
        }
        return true;
    }

    private static char toLowerCase(char character) {
        return (char)((character >= 97 && character <= 122) ? character - 32: character);

    }

    private static boolean isAlphaNumeric(char character) {
        return (character >= 65 && character <= 90) || (character >= 97 && character <= 122) ||
                (character >= 48 && character <= 57);
    }

}
