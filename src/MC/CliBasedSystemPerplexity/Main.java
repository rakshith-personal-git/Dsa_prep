package MC.CliBasedSystemPerplexity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Entry point and simple CLI driver that demonstrates commands and also allows interactive usage.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        TaskRepository repository = new TaskRepository();
        TaskService service = new TaskService(repository);
        CommandParser parser = new CommandParser();
        CommandExecutor executor = new CommandExecutor(service);

        // Demonstration script covering the sample commands from the problem
        System.out.println("--- Demo Script ---");
        String[] demo = new String[]{
                "ADD_TASK Alice Bob FixLoginIssue BugFix 3",
                "ADD_TASK Bob Charlie WriteDocs Documentation 2",
                "ADD_TASK Alice Bob AddSignupFeature Feature 5",
                "ADD_TASK Charlie Bob UpdateReadme Documentation 1",
                "COMPLETE_TASK Bob FixLoginIssue",
                "COMPLETE_TASK Bob AddSignupFeature",
                "SHOW_TASKS Bob ALL",
                "SHOW_PRODUCTIVITY",
                "CATEGORY_SUMMARY"
        };
        for (String cmd : demo) {
            System.out.println("> " + cmd);
            executor.execute(parser.parse(cmd));
            System.out.println();
        }

        // Optional interactive mode
        System.out.println("--- Interactive Mode (type EXIT to quit) ---");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.equalsIgnoreCase("EXIT")) {
                System.out.println("Exiting...");
                break;
            }
            try {
                executor.execute(parser.parse(line));
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}
