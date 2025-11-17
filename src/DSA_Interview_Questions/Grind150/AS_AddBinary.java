package DSA_Interview_Questions.Grind150;

public class AS_AddBinary {
    /**
     * Given two binary strings a and b, return their sum as a binary string.
     *
     * Example 1:
     * Input: a = "11", b = "1"
     * Output: "100"
     *
     *  Example 2:
     * Input: a = "1010", b = "1011"
     * Output: "10101"
     *
     * Constraints:
     * 1 <= a.length, b.length <= 104
     * a and b consist only of '0' or '1' characters.
     * Each string does not contain leading zeros except for the zero itself.
     * */

    public static void main(String[] args) {
        String a = "1010";
        String b = "1011";
        String ans = addBinary(a, b);
        System.out.println(ans);
        ans = addBinary2(a, b);
        System.out.println(ans);
    }

    //approach 2 -> more readable and easy to understand
    private static String addBinary2(String a, String b) {
        int i = a.length() - 1;
        int j = b.length() - 1;

        int carry = 0;
        StringBuilder result = new StringBuilder();

        while (i >= 0 || j >= 0) {
            int sum = carry;
            if (i >= 0) {
                sum += a.charAt(i) - '0';
            }
            if (j >= 0) {
                sum += b.charAt(j) - '0';
            }
            if (sum == 0 || sum == 1) {
                result.append(sum);
                carry = 0;
            }
            else if (sum == 2) {
                result.append("0");
                carry = 1;
            }
            else {
                result.append("1");
                carry = 1;
            }
            i--;
            j--;
        }
        if (carry == 1) {
            result.append("1");
        }
        return result.reverse().toString();
    }


    //approach is ASCII value of 0 and is 48 and 49, which can be used here
    //If a.charAt(i) is '1', then '1' - '0' is equivalent to 49 - 48, resulting in 1.
    //If a.charAt(i) is '0', then '0' - '0' is equivalent to 48 - 48, resulting in 0.

    private static String addBinary(String a, String b) {
        StringBuilder result = new StringBuilder();
        int carry = 0;

        int i = a.length() - 1;
        int j = b.length() - 1;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;

            // Add the current bits from both strings
            if (i >= 0) {
                sum += a.charAt(i--) - '0';
            }
            if (j >= 0) {
                sum += b.charAt(j--) - '0';
            }

            // Append the current bit to the result
            result.insert(0, sum % 2);

            // Update the carry for the next iteration
            carry = sum / 2;
        }

        return result.toString();
    }
}
