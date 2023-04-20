package se.thinkcode.todo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoControllerTest {
    @Test
    void should_add_a_task_to_buy_cat_food() {
        TodoRepository repository = new InMemoryTodoRepository();
        TodoService service = new TodoService(repository);
        TodoController controller = new TodoController(service);
        TaskRequest task = new TaskRequest("Thomas", "Buy cat food");

        controller.createNewTask(task);
        List<TaskResponse> actual = controller.getTasks("Thomas");

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).user()).isEqualTo("Thomas");
        assertThat(actual.get(0).chore()).isEqualTo("Buy cat food");
    }
}