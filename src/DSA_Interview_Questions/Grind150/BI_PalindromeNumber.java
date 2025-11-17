package DSA_Interview_Questions.Grind150;

public class BI_PalindromeNumber {
    /**
     * Given an integer x, return true if x is a palindrome and false otherwise.
     * <p>
     * Example 1:
     * Input: x = 121
     * Output: true
     * Explanation: 121 reads as 121 from left to right and from right to left.
     * <p>
     * Example 2:
     * Input: x = -121
     * Output: false
     * Explanation: From left to right, it reads -121. From right to left, it becomes 121-. Therefore it is not a palindrome.
     * <p>
     * Example 3:
     * Input: x = 10
     * Output: false
     * Explanation: Reads 01 from right to left. Therefore it is not a palindrome.
     * <p>
     * <p>
     * Constraints:
     * -231 <= x <= 231 - 1
     * Follow up: Could you solve it without converting the integer to a string?
     */

    public static void main(String[] args) {
        int x = 121;
        System.out.println(isPalindrome(x));
        System.out.println(isPalindrome2(x));
    }

    private static boolean isPalindrome2(int x) {
        if (x < 0) {
            return false;
        }

        int originalNumber = x;
        int newNumber = 0;

        while (x != 0) {
            int mod = x % 10;
            newNumber = (newNumber * 10) + mod;
            x = x / 10;
        }

        return newNumber == originalNumber;
    }

    private static boolean isPalindrome(int x) {
        char[] array = String.valueOf(x).toCharArray();

        int frontPointer = 0;
        int leftPointer = array.length - 1;

        while (frontPointer < leftPointer) {
            if (array[frontPointer] != array[leftPointer]) {
                return false;
            }
            frontPointer++;
            leftPointer--;
        }
        return true;
    }
    // TC is O(N) where N is the number of digits in the integer X
    // SC is O(N) due to the space required for the string and character array representations of the integer.
}
