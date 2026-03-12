package MC.CliBasedSystemPerplexity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single task assigned from one user to another.
 */
public class Task {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    private final int id;
    private final String title;
    private final String category;
    private final int estimatedHours;
    private final String assignedBy;
    private final String assignedTo;
    private TaskStatus status;

    /**
     * Constructs a new Task instance.
     */
    public Task(String assignedBy, String assignedTo, String title, String category, int estimatedHours) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.title = title;
        this.category = category;
        this.estimatedHours = estimatedHours;
        this.status = TaskStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Marks the task as completed.
     */
    public void complete() {
        this.status = TaskStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", category='" + category + "'" +
                ", hours=" + estimatedHours +
                ", assignedBy='" + assignedBy + "'" +
                ", assignedTo='" + assignedTo + "'" +
                ", status=" + status +
                '}';
    }
}
