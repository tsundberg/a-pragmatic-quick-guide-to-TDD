package se.thinkcode.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTodoRepository implements TodoRepository {
    private final Map<User, List<Task>> database = new HashMap<>();

    @Override
    public void createNewTask(Task task) {
        User user = task.user();
        List<Task> tasks = database.getOrDefault(user, new ArrayList<>());
        tasks.add(task);
        database.put(user, tasks);
    }

    @Override
    public List<Task> getTasks(User user) {
        return database.getOrDefault(user, new ArrayList<>());
    }
}
