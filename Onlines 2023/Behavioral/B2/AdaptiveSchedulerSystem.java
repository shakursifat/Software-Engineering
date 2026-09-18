import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// --- ENUMS ---
enum Priority {
    LOW, MEDIUM, HIGH // Declared in ascending order of importance
}

// --- TASK MODEL ---
class Task {
    private String taskId;
    private int startTime;
    private int endTime;
    private Priority priority;

    public Task(String taskId, int startTime, int endTime, Priority priority) {
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }

    public String getTaskId() { return taskId; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
    public Priority getPriority() { return priority; }
    
    // Execution Time = End Time - Start Time
    public int getExecutionTime() { 
        return endTime - startTime; 
    }

    @Override
    public String toString() {
        return "Task{" + "id='" + taskId + '\'' + ", executionTime=" + getExecutionTime() +
               ", priority=" + priority + ", start=" + startTime + '}';
    }
}

// --- STRATEGY INTERFACE ---
interface SchedulingPolicy {
    Task selectTask(List<Task> tasks);
    String getPolicyName();
}

// --- CONCRETE STRATEGIES ---

/**
 * FCFS: Selected according to their start time. Earliest start time executed first[cite: 3].
 */
class FCFSPolicy implements SchedulingPolicy {
    @Override
    public Task selectTask(List<Task> tasks) {
        return tasks.stream()
                .min(Comparator.comparingInt(Task::getStartTime))
                .orElse(null);
    }

    @Override
    public String getPolicyName() { return "FCFS"; }
}

/**
 * Priority: HIGH > MEDIUM > LOW. Tie-breaker: earliest start time[cite: 3].
 */
class PriorityPolicy implements SchedulingPolicy {
    @Override
    public Task selectTask(List<Task> tasks) {
        return tasks.stream()
                .min(Comparator.comparing(Task::getPriority).reversed()
                        .thenComparingInt(Task::getStartTime))
                .orElse(null);
    }

    @Override
    public String getPolicyName() { return "Priority Scheduling"; }
}

/**
 * SJF: Smallest execution time selected first. Tie-breaker: earliest start time[cite: 3].
 */
class SJFPolicy implements SchedulingPolicy {
    @Override
    public Task selectTask(List<Task> tasks) {
        return tasks.stream()
                .min(Comparator.comparingInt(Task::getExecutionTime)
                        .thenComparingInt(Task::getStartTime))
                .orElse(null);
    }

    @Override
    public String getPolicyName() { return "SJF"; }
}

// --- CONTEXT / SCHEDULER ---
class TaskScheduler {
    private List<Task> waitingQueue;
    private SchedulingPolicy preferredPolicy;

    // The user chooses one of the three scheduling policies as the preferred policy[cite: 3]
    public TaskScheduler(SchedulingPolicy preferredPolicy) {
        this.waitingQueue = new ArrayList<>();
        this.preferredPolicy = preferredPolicy;
    }

    public void addTask(Task task) {
        waitingQueue.add(task);
    }

    // The preferred policy may also be changed while the program is running[cite: 3]
    public void setPreferredPolicy(SchedulingPolicy preferredPolicy) {
        this.preferredPolicy = preferredPolicy;
    }

    /**
     * Determines which scheduling policy should be used based on current workload[cite: 3].
     */
    private SchedulingPolicy determineCurrentPolicy() {
        if (waitingQueue.isEmpty()) return preferredPolicy;

        boolean hasHighPriority = waitingQueue.stream()
                .anyMatch(t -> t.getPriority() == Priority.HIGH);

        // Rule 1: Urgent Workload -> At least one HIGH priority task[cite: 3]
        if (hasHighPriority) {
            return new PriorityPolicy();
        }

        long shortTasksCount = waitingQueue.stream()
                .filter(t -> t.getExecutionTime() <= 3)
                .count();

        // Rule 2: Short-Task Workload -> No HIGH priority, but at least 3 short tasks[cite: 3]
        if (shortTasksCount >= 3) {
            return new SJFPolicy();
        }

        // Rule 3: Normal Workload -> Neither condition is satisfied[cite: 3]
        return preferredPolicy;
    }

    /**
     * Examines workload, determines policy, selects task, executes/removes it, and displays info[cite: 3].
     */
    public void executeNextTask() {
        if (waitingQueue.isEmpty()) {
            System.out.println("No tasks to execute.");
            return;
        }

        // The scheduling decision must be made again after each task is executed[cite: 3]
        SchedulingPolicy activePolicy = determineCurrentPolicy();
        Task selectedTask = activePolicy.selectTask(waitingQueue);

        waitingQueue.remove(selectedTask);
        System.out.println("Executed " + selectedTask.getTaskId() + " using policy: " + activePolicy.getPolicyName());
    }

    /**
     * Repeatedly executes tasks until the waiting queue becomes empty[cite: 3].
     */
    public void executeAll() {
        while (!waitingQueue.isEmpty()) {
            executeNextTask();
        }
    }
}

// --- MAIN CLASS (TESTING) ---
public class AdaptiveSchedulerSystem {
    public static void main(String[] args) {
        // Example Scenario setup: Preferred policy is FCFS[cite: 3]
        TaskScheduler scheduler = new TaskScheduler(new FCFSPolicy());

        // Adding tasks from the example scenario[cite: 3]
        scheduler.addTask(new Task("T1", 0, 8, Priority.MEDIUM));
        scheduler.addTask(new Task("T2", 1, 4, Priority.LOW));     // Exec Time = 3
        scheduler.addTask(new Task("T3", 2, 4, Priority.MEDIUM));  // Exec Time = 2
        scheduler.addTask(new Task("T4", 3, 4, Priority.LOW));     // Exec Time = 1
        scheduler.addTask(new Task("T5", 4, 9, Priority.HIGH));    // Exec Time = 5

        System.out.println("--- Starting Execution ---");
        // Because a HIGH priority task is present, it will adaptively use Priority Scheduling first
        // Once T5 is removed, 3 short tasks (T2, T3, T4) remain, triggering SJF.
        // Finally, FCFS resumes.
        scheduler.executeAll();
    }
}
