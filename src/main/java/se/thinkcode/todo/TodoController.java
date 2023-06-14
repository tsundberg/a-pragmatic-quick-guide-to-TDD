package se.thinkcode.todo;

import java.util.List;

public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    public void createNewTask(TaskRequest taskRequest) {
        Task task = taskRequest.toModel();
        service.createNewTask(task);
    }

    public List<TaskResponse> getTasks(String user) {
        List<Task> tasks = service.getTasks(new User(user));
        return TaskResponse.fromModel(tasks);
    }
}
