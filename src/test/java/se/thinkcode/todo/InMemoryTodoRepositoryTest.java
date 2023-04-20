package se.thinkcode.todo;

public class InMemoryTodoRepositoryTest extends TodoRepositoryTest {
    public InMemoryTodoRepositoryTest() {
        repository = new InMemoryTodoRepository();
    }
}