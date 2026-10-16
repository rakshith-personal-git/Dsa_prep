package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncodeAndDecodeStrings {

    /**
     * Encodes a list of strings into a single string using Length-Prefixing.
     *
     * @param strs List of input strings.
     * @return Single encoded string.
     */
    public static String encode(List<String> strs) {
        if (strs == null || strs.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            // Format: <length>#<string>
            sb.append(str.length()).append('#').append(str);
        }
        return sb.toString();
    }

    /**
     * Decodes a single string back into a list of strings.
     *
     * @param s Encoded string.
     * @return Reconstructed list of original strings.
     */
    public static List<String> decode(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.isEmpty()) {
            return result;
        }

        int i = 0;
        int n = s.length();

        while (i < n) {
            // Find the delimiter '#'
            int delimiterIndex = s.indexOf('#', i);
            if (delimiterIndex == -1) {
                break; // Safety check
            }

            // Parse length of payload string
            int length = Integer.parseInt(s.substring(i, delimiterIndex));

            // Extract the exact payload of size 'length'
            int start = delimiterIndex + 1;
            String str = s.substring(start, start + length);
            result.add(str);

            // Advance pointer past the extracted payload
            i = start + length;
        }

        return result;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test 1: Prompt Example (Empty strings mixed with words)
        List<String> test1 = Arrays.asList("", "hello", "");
        runTest("Test 1 (Empty strings mixed)", test1);

        // Test 2: Special characters & numbers inside strings
        List<String> test2 = Arrays.asList("5#hello", "12#world", "###", "");
        runTest("Test 2 (Strings containing delimiters & digits)", test2);

        // Test 3: Standard sentence array
        List<String> test3 = Arrays.asList("Toast", "POS", "Platform", "2026");
        runTest("Test 3 (Standard word list)", test3);

        // Test 4: Single empty string
        List<String> test4 = Arrays.asList("");
        runTest("Test 4 (Single empty string)", test4);
    }

    private static void runTest(String testName, List<String> original) {
        String encoded = encode(original);
        List<String> decoded = decode(encoded);

        if (original.equals(decoded)) {
            System.out.println(testName + " PASSED");
            System.out.println("   Encoded String: \"" + encoded + "\"");
            System.out.println("   Decoded Output: " + decoded);
        } else {
            System.out.println(testName + " FAILED");
            System.out.println("   Expected: " + original);
            System.out.println("   Got:      " + decoded);
        }
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N)
 *   Where N is the total number of characters across all strings in the list.
 *   - Encoding scans each character once to build the string buffer in O(N).
 *   - Decoding scans the encoded string linear to its length in O(N).
 *
 * Space Complexity: O(N) Auxiliary Space
 *   The StringBuilder in `encode` and the output List<String> in `decode` 
 *   store memory proportional to the total length N of all characters.
 * ============================================================================
 */