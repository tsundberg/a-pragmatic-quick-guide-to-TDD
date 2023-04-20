package se.thinkcode.todo;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoService {
    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public void createNewTask(Task task) {
        repository.createNewTask(task);
    }

    public List<Task> getTasks(User user) {
        return repository.getTasks(user);
    }
}
