package se.thinkcode.todo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping("/addTask")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewTask(@RequestBody TaskRequest taskRequest) {
        Task task = taskRequest.toModel();
        service.createNewTask(task);
    }

    @GetMapping("/getTasks/{user}")
    public List<TaskResponse> getTasks(@PathVariable String user) {
        List<Task> tasks = service.getTasks(new User(user));
        return TaskResponse.fromModel(tasks);
    }
}
