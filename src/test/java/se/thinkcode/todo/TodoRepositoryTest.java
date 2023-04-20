package se.thinkcode.todo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TodoRepositoryTest {

    TodoRepository repository;

    @Test
    void should_add_a_task_and_see_it() {
        User user = new User("Emil");
        Chore chore = new Chore("Must chop wood");
        Task sample = new Task(user, chore);

        User expectedUser = new User("Emil");
        Chore expectedChore = new Chore("Must chop wood");
        Task expected = new Task(expectedUser, expectedChore);


        repository.createNewTask(sample);
        List<Task> actual = repository.getTasks(user);


        assertThat(actual).containsExactly(expected);
    }
}