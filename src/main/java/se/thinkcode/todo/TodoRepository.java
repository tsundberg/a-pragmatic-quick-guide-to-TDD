package se.thinkcode.todo;

import java.util.List;

public interface TodoRepository {
    void createNewTask(Task task);

    List<Task> getTasks(User user);
}
