package MC.CliBasedSystemPerplexity;

import java.util.List;
import java.util.Map;

/**
 * Executes parsed commands against the TaskService and prints results to the console.
 */
public class CommandExecutor {

    private final TaskService taskService;

    public CommandExecutor(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Executes a parsed command.
     */
    public void execute(CommandParser.ParsedCommand command) {
        switch (command.getType()) {
            case ADD_TASK -> handleAddTask(command.getArgs());
            case COMPLETE_TASK -> handleCompleteTask(command.getArgs());
            case SHOW_TASKS -> handleShowTasks(command.getArgs());
            case SHOW_PRODUCTIVITY -> handleShowProductivity();
            case CATEGORY_SUMMARY -> handleCategorySummary();
            case EXIT -> System.out.println("Exiting...");
            default -> throw new IllegalArgumentException("Unsupported command");
        }
    }

    private void handleAddTask(List<String> args) {
        if (args.size() != 5) {
            System.out.println("Usage: ADD_TASK <assignedBy> <assignedTo> <title> <category> <hours>");
            return;
        }
        String assignedBy = args.get(0);
        String assignedTo = args.get(1);
        String title = args.get(2);
        String category = args.get(3);
        int hours = Integer.parseInt(args.get(4));
        Task task = taskService.addTask(assignedBy, assignedTo, title, category, hours);
        System.out.println("Added: " + task);
    }

    private void handleCompleteTask(List<String> args) {
        if (args.size() != 2) {
            System.out.println("Usage: COMPLETE_TASK <user> <title>");
            return;
        }
        String user = args.get(0);
        String title = args.get(1);
        boolean success = taskService.completeTask(user, title);
        if (success) {
            System.out.println("Task marked as completed.");
        } else {
            System.out.println("No matching pending task found for user " + user + " and title " + title);
        }
    }

    private void handleShowTasks(List<String> args) {
        if (args.size() != 2) {
            System.out.println("Usage: SHOW_TASKS <user> <PENDING|COMPLETED|ALL>");
            return;
        }
        String user = args.get(0);
        String filter = args.get(1);
        List<Task> tasks = taskService.getTasksForUser(user, filter);
        if (tasks.isEmpty()) {
            System.out.println("No tasks for user " + user + " with filter " + filter);
            return;
        }
        System.out.println("Tasks for " + user + " (" + filter + "):");
        for (Task t : tasks) {
            System.out.println(t);
        }
    }

    private void handleShowProductivity() {
        Map<String, int[]> stats = taskService.computeProductivity();
        if (stats.isEmpty()) {
            System.out.println("No tasks available to compute productivity.");
            return;
        }
        System.out.println("Productivity Report:");
        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            String user = e.getKey();
            int total = e.getValue()[0];
            int completed = e.getValue()[1];
            double percentage = total == 0 ? 0.0 : (completed * 100.0) / total;
            System.out.printf("User: %s, Total: %d, Completed: %d, Completion: %.2f%%%n", user, total, completed, percentage);
        }
    }

    private void handleCategorySummary() {
        Map<String, Integer> summary = taskService.computeCategorySummary();
        if (summary.isEmpty()) {
            System.out.println("No completed tasks to summarize by category.");
            return;
        }
        System.out.println("Category Summary (completed hours):");
        for (Map.Entry<String, Integer> e : summary.entrySet()) {
            System.out.printf("Category: %s, Hours: %d%n", e.getKey(), e.getValue());
        }
    }
}
