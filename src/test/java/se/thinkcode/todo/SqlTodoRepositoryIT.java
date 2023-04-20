package se.thinkcode.todo;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class SqlTodoRepositoryIT extends TodoRepositoryTest {

    private final SqlTodoRepository sqlTodoRepository;

    @Autowired
    public SqlTodoRepositoryIT(SqlTodoRepository repository) {
        sqlTodoRepository = repository;
    }

    @BeforeEach
    void setUp() {
        repository = sqlTodoRepository;
    }
}