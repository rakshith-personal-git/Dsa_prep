package DSA_Interview_Questions.CompanyWisePrep.Ambient;

import java.util.*;

public class Spreadsheet {

    // Stores the raw input string for each cell (e.g., "10", "= A1 + 10")
    private final Map<String, String> cellStore;

    public Spreadsheet() {
        this.cellStore = new HashMap<>();
    }

    /**
     * Stores the raw value or formula in O(1) time.
     */
    public void set(String cellId, String value) {
        if (cellId == null || value == null) {
            throw new IllegalArgumentException("Cell ID and value cannot be null");
        }
        cellStore.put(cellId.trim().toUpperCase(), value.trim());
    }

    /**
     * Retrieves and evaluates the computed value of the cell.
     */
    public String get(String cellId) {
        String normalizedId = cellId.trim().toUpperCase();
        if (!cellStore.containsKey(normalizedId)) {
            return "0"; // Default cell value for undefined references
        }

        Set<String> visiting = new HashSet<>();
        int result = evaluateCell(normalizedId, visiting);
        return String.valueOf(result);
    }

    /**
     * DFS evaluation with cycle detection.
     */
    private int evaluateCell(String cellId, Set<String> visiting) {
        // Cycle check: If current cell is already in the current evaluation path
        if (visiting.contains(cellId)) {
            throw new IllegalStateException("Circular dependency detected at cell: " + cellId);
        }

        String raw = cellStore.get(cellId);
        if (raw == null || raw.isEmpty()) {
            return 0;
        }

        // Case 1: Plain literal value (e.g., "10")
        if (!raw.startsWith("=")) {
            return parseLiteral(raw);
        }

        // Case 2: Formula (e.g., "= A1 + 10 + 20")
        visiting.add(cellId); // Mark as currently visiting on DFS stack
        int evaluatedValue = evaluateFormula(raw.substring(1).trim(), visiting);
        visiting.remove(cellId); // Backtrack

        return evaluatedValue;
    }

    /**
     * Parses and computes expressions like "10 + A1 + 20".
     * Extendable to -, *, / if needed.
     */
    private int evaluateFormula(String expression, Set<String> visiting) {
        int sum = 0;
        // Split by '+' operator and trim each operand token
        String[] tokens = expression.split("\\+");

        for (String rawToken : tokens) {
            String token = rawToken.trim();
            if (token.isEmpty()) continue;

            if (isInteger(token)) {
                sum += Integer.parseInt(token);
            } else {
                // Must be a cell reference (e.g., "A1")
                String refCell = token.toUpperCase();
                sum += evaluateCell(refCell, visiting);
            }
        }
        return sum;
    }

    private int parseLiteral(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid cell literal value: " + value);
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ==========================================
    // Verification & Test Suite
    // ==========================================
    public static void main(String[] args) {
        Spreadsheet cell = new Spreadsheet();

        // Test Case 1: Literal set and get
        cell.set("A1", "10");
        System.out.println("cell.get(\"A1\") -> " + cell.get("A1")); 
        // Output: 10

        // Test Case 2: Arithmetic formula without cell reference
        cell.set("A1", "= 10 + 10 + 10");
        System.out.println("cell.get(\"A1\") -> " + cell.get("A1")); 
        // Output: 30

        // Test Case 3: Cell reference combined with numbers
        cell.set("A2", "= A1 + 10 + 20");
        System.out.println("cell.get(\"A2\") -> " + cell.get("A2")); 
        // Output: 60

        // Test Case 4: Deep transitive dependency (A3 -> A2 -> A1)
        cell.set("A3", "= A2 + 40");
        System.out.println("cell.get(\"A3\") -> " + cell.get("A3")); 
        // Output: 100

        // Test Case 5: Dynamic update propagation
        cell.set("A1", "5"); // Updated A1 from 30 to 5
        System.out.println("After A1=5, cell.get(\"A2\") -> " + cell.get("A2")); 
        // Output: 35 (5 + 10 + 20)
        System.out.println("After A1=5, cell.get(\"A3\") -> " + cell.get("A3")); 
        // Output: 75 (35 + 40)

        // Test Case 6: Cycle Detection Verification
        try {
            cell.set("X1", "= X2 + 1");
            cell.set("X2", "= X1 + 1");
            cell.get("X1");
        } catch (IllegalStateException e) {
            System.out.println("Cycle successfully caught: " + e.getMessage());
            // Output: Circular dependency detected at cell: X1
        }
    }
}