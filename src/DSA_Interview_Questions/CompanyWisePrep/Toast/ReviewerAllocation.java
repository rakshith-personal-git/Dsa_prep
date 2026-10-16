package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * PROBLEM: Reviewer Allocation
 * ============================================================================
 * A team lead wants to see every way the pending pull requests can be 
 * distributed among 6 reviewers.
 * 
 * The current assignment is given as a 6-character plan, with one character 
 * per reviewer. A digit is that reviewer's existing count, while '?' means 
 * the count is still open.
 * 
 * Return every valid complete plan in increasing order, where:
 *   1. The six counts add up to total_reviews.
 *   2. An open reviewer can take from 0 to reviewer_cap pull requests.
 *   3. Existing counts stay unchanged, even if they exceed reviewer_cap.
 * 
 * Constraints:
 *   - 1 <= total_reviews <= 54
 *   - 1 <= reviewer_cap <= 9
 *   - plan.length = 6
 *   - Each character in plan is a digit ('0'-'9') or '?'
 *   - At least one valid completed plan exists.
 * 
 * Example:
 *   Input: plan = "7?0?54", total_reviews = 20, reviewer_cap = 4
 *   Output: ["700454", "710354", "720254", "730154", "740054"]
 * ============================================================================
 */
public class ReviewerAllocation {

    public static List<String> assign_reviews(String plan, int total_reviews, int reviewer_cap) {
        List<String> result = new ArrayList<>();
        backtrack(plan, 0, 0, total_reviews, reviewer_cap, new char[6], result);
        return result;
    }

    private static void backtrack(
        String plan, 
        int index, 
        int currentSum, 
        int totalReviews, 
        int reviewerCap, 
        char[] currentPlan, 
        List<String> result
    ) {
        // Base Case: Stop when index reaches 6
        if (index == 6) {
            if (currentSum == totalReviews) {
                result.add(new String(currentPlan));
            }
            return; // Must return outside the inner 'if' to prevent IndexOutOfBoundsException
        }

        char ch = plan.charAt(index);
        
        if (ch != '?') {
            // Existing reviewer count stays fixed
            int val = ch - '0';
            currentPlan[index] = ch;
            backtrack(plan, index + 1, currentSum + val, totalReviews, reviewerCap, currentPlan, result);
        } else {
            // Open reviewer: explore numbers from 0 up to reviewerCap
            for (int val = 0; val <= reviewerCap; val++) {
                if (currentSum + val <= totalReviews) {
                    currentPlan[index] = (char) ('0' + val);
                    backtrack(plan, index + 1, currentSum + val, totalReviews, reviewerCap, currentPlan, result);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Example from problem statement
        String plan1 = "7?0?54";
        int totalReviews1 = 20;
        int reviewerCap1 = 4;
        
        System.out.println("Test Case 1 Results:");
        List<String> output1 = assign_reviews(plan1, totalReviews1, reviewerCap1);
        output1.forEach(System.out::println);

        System.out.println("\n------------------------------\n");

        // Test Case 2: All reviewers open
        String plan2 = "??????";
        int totalReviews2 = 6;
        int reviewerCap2 = 1;
        
        System.out.println("Test Case 2 Results:");
        List<String> output2 = assign_reviews(plan2, totalReviews2, reviewerCap2);
        output2.forEach(System.out::println);
    }
}