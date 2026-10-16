package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.*;

public class ToastOAPunctuationWordReverser {

    /**
     * Toast OA Task: Reverse words in a string while keeping non-alphanumeric
     * character positions completely fixed at their original index locations.
     *
     * @param input Input string containing words and punctuation.
     * @return Formatted string with reversed word characters and fixed punctuation.
     */
    public static String reverseWordsKeepPunctuation(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 1. Collect all alphanumeric characters in their original sequence
        List<Character> alphaChars = new ArrayList<>();
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                alphaChars.add(c);
            }
        }

        // 2. Pointer to pull alphanumeric characters in REVERSE order
        int alphaPointer = alphaChars.size() - 1;

        // 3. Reconstruct output string preserving non-alphanumeric positions
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            if (Character.isLetterOrDigit(curr)) {
                // Replace with the alphanumeric character from the tail end
                result.append(alphaChars.get(alphaPointer));
                alphaPointer--;
            } else {
                // Keep non-alphanumeric character fixed at index i
                result.append(curr);
            }
        }

        return result.toString();
    }

    // --- Main Executable for Testing ---
    public static void main(String[] args) {
        // Test 1: Contiguous punctuation and spaces
        runTest("Test 1 (Standard Toast Prompt)", "I'm great, thanks!", "s'nk ahterg, m'I!");

        // Test 2: Standard word reversal with spaces
        runTest("Test 2 (Basic Spaces)", "hello world", "dlrow olleh");

        // Test 3: Multiple special characters at boundaries
        runTest("Test 3 (Boundaries)", "!!!a-b,c???", "!!!c-b,a???");

        // Test 4: Pure non-alphanumeric string
        runTest("Test 4 (Only Punctuation)", "   ,,, !!!", "   ,,, !!!");

        // Test 5: Empty input
        runTest("Test 5 (Empty String)", "", "");
    }

    private static void runTest(String testName, String input, String expected) {
        String result = reverseWordsKeepPunctuation(input);
        if (Objects.equals(result, expected)) {
            System.out.println(testName + " PASSED -> \"" + result + "\"");
        } else {
            System.out.println(testName + " FAILED -> Expected: \"" + expected + "\", Got: \"" + result + "\"");
        }
    }
}
