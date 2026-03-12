package MC.CliBasedSystemPerplexity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for storing tasks by assignee.
 * Uses thread-safe collections to support concurrent access.
 */
public class TaskRepository {

    private final Map<String, List<Task>> tasksByUser = new ConcurrentHashMap<>();

    /**
     * Adds a task to the repository.
     */
    public void addTask(Task task) {
        tasksByUser.computeIfAbsent(task.getAssignedTo(), k -> Collections.synchronizedList(new ArrayList<>())).add(task);
    }

    /**
     * Returns all tasks assigned to a specific user.
     */
    public List<Task> getTasksForUser(String user) {
        return tasksByUser.getOrDefault(user, Collections.emptyList());
    }

    /**
     * Returns all tasks across all users.
     */
    public List<Task> getAllTasks() {
        List<Task> all = new ArrayList<>();
        for (List<Task> list : tasksByUser.values()) {
            synchronized (list) {
                all.addAll(list);
            }
        }
        return all;
    }
}
