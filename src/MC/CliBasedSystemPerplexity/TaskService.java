package MC.CliBasedSystemPerplexity;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core domain service that encapsulates task creation, update, and reporting logic.
 */
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates and stores a new task.
     */
    public Task addTask(String assignedBy, String assignedTo, String title, String category, int hours) {
        Task task = new Task(assignedBy, assignedTo, title, category, hours);
        repository.addTask(task);
        return task;
    }

    /**
     * Marks a task as completed for a given user and title.
     * Returns true if a matching pending task was found and completed.
     */
    public boolean completeTask(String user, String title) {
        List<Task> tasks = repository.getTasksForUser(user);
        for (Task t : tasks) {
            if (t.getTitle().equals(title) && t.getStatus() == TaskStatus.PENDING) {
                t.complete();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns tasks for a user filtered by status keyword (PENDING, COMPLETED, ALL).
     */
    public List<Task> getTasksForUser(String user, String statusFilter) {
        TaskStatus filter;
        if ("ALL".equalsIgnoreCase(statusFilter)) {
            filter = null;
        } else {
            filter = TaskStatus.valueOf(statusFilter.toUpperCase(Locale.ROOT));
        }
        List<Task> tasks = repository.getTasksForUser(user);
        if (filter == null) {
            return tasks;
        }
        return tasks.stream().filter(t -> t.getStatus() == filter).toList();
    }

    /**
     * Computes productivity statistics per user.
     * Returns map user -> array [totalTasks, completedTasks].
     */
    public Map<String, int[]> computeProductivity() {
        Map<String, int[]> stats = new ConcurrentHashMap<>();
        for (Task t : repository.getAllTasks()) {
            stats.computeIfAbsent(t.getAssignedTo(), k -> new int[2]);
            int[] arr = stats.get(t.getAssignedTo());
            arr[0]++;
            if (t.getStatus() == TaskStatus.COMPLETED) {
                arr[1]++;
            }
        }
        return stats;
    }

    /**
     * Computes total completed hours per category.
     */
    public Map<String, Integer> computeCategorySummary() {
        Map<String, Integer> summary = new ConcurrentHashMap<>();
        for (Task t : repository.getAllTasks()) {
            if (t.getStatus() == TaskStatus.COMPLETED) {
                summary.merge(t.getCategory(), t.getEstimatedHours(), Integer::sum);
            }
        }
        return summary;
    }
}
