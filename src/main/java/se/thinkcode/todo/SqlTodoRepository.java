package se.thinkcode.todo;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SqlTodoRepository implements TodoRepository {
    private final TodoDao todoDao;

    public SqlTodoRepository(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    @Override
    public void createNewTask(Task task) {
        String id = UUID.randomUUID().toString();
        String owner = task.user().user();
        String chore = task.chore().chore();

        todoDao.createNewTask(id, owner, chore);
    }

    @Override
    public List<Task> getTasks(User user) {
        String owner = user.user();

        return todoDao.getTasks(owner);
    }
}