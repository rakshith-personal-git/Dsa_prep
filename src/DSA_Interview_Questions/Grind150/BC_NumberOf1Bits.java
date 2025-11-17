package DSA_Interview_Questions.Grind150;

public class BC_NumberOf1Bits {
    /**
     * Write a function that takes the binary representation of an unsigned integer and returns the number of '1' bits it has (also known as the Hamming weight).
     *
     * Note:
     * Note that in some languages, such as Java, there is no unsigned integer type. In this case, the input will be given as a signed integer type. It should not affect your implementation, as the integer's internal binary representation is the same, whether it is signed or unsigned.
     * In Java, the compiler represents the signed integers using 2's complement notation. Therefore, in Example 3, the input represents the signed integer. -3.
     *
     * Example 1:
     * Input: n = 00000000000000000000000000001011
     * Output: 3
     * Explanation: The input binary string 00000000000000000000000000001011 has a total of three '1' bits.
     *
     * Example 2:
     * Input: n = 00000000000000000000000010000000
     * Output: 1
     * Explanation: The input binary string 00000000000000000000000010000000 has a total of one '1' bit.
     *
     *  Example 3:
     * Input: n = 11111111111111111111111111111101
     * Output: 31
     * Explanation: The input binary string 11111111111111111111111111111101 has a total of thirty one '1' bits.
     *
     * Constraints:
     * The input must be a binary string of length 32.
     *
     * Follow up: If this function is called many times, how would you optimize it?
     * */

    public static void main(String[] args) {
        int n = 00000000000000000000000000001011;
        int hammingWeight = hammingWeight(n);
        System.out.println(hammingWeight);
    }

    private static int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>=1;
        }
        return count;
    }

    //note :-
    // >> (Signed Right Shift):
    //The >> operator performs a signed right shift. When you perform a signed right shift (>>) on a signed integer, the sign bit (the leftmost bit) is preserved. This means that if the number is negative (i.e., if the sign bit is 1), then 1 bits are filled in on the left side to preserve the sign.
    //For example, if you perform x >> n on a negative number x, n bits are shifted to the right, and the leftmost n bits are filled with 1 bits to maintain the sign.
    int x = -8;  // Binary representation: 1111 1111 1111 1000
    int result = x >> 1;  // After signed right shift by 1 position: 1111 1111 1111 1100 (result = -4)


    //>>> (Unsigned Right Shift):
    //The >>> operator performs an unsigned right shift. It fills the vacant bits on the left side with 0 bits, regardless of the sign bit. This operation treats the number as an unsigned integer.
    int y = -8;  // Binary representation: 1111 1111 1111 1000
    int result1 = x >>> 1;  // After unsigned right shift by 1 position: 0111 1111 1111 1100 (result = 2147483644)


}
