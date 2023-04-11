# The implementation, step by step

## The problem

The goal is to show a pragmatic use of Test-Driven Development from a seemingly real life experience.

This presents a few problem. We don't know the same domain. I have only some 30 minutes to show a way of working that
works extremely well for me.

I prefer, when possible, to do outside in development. This ensures that whatever I build is aimed for usage and not
hidden somewhere in the middle of a big ball of mud.

## An endpoint for creating and getting tasks todo

Create a `TodoControllerTest` that verifies that it is possible to

* Create a task
* Get a list of tasks

```
@Test
void should_add_a_task_to_buy_cat_food() {
    TodoRepository repository = new InMemoryTodoRepository();
    TodoService service = new TodoService(repository);
    TodoController controller = new TodoController(service);
    TaskRequest task = new TaskRequest("Thomas", "Buy cat food");

    controller.createNewTask(task);
    List<TaskResponse> actual = controller.getTasks(user);

    assertThat(actual).hasSize(1);
    assertThat(actual.get(0).user()).isEqualTo("Thomas");
    assertThat(actual.get(0).chore()).isEqualTo("Buy cat food");
}
```

### A service that that will connect the controller to the rest of the system

Create a TodoService that receives a domain object for creating a task and return a list of domain objects with task for
a person.

### A repository that receives domain objects

Create a repository that will be able to hold tasks for users

Commit with the message

```
An initial in memory solution
```

## And endpoint

We have something that seems to work. But it should be an endpoint. And it isn't.

In order for that to happen, we need to wire it up as an endpoint. And make sure the controller receives the
dependencies it needs.

But we test drive stuff, so we will create an integration test that will connect to a running instance, connect to an
endpoint for adding a task and an endpoint for getting the tasks.

Let's start with an integration test like this:

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerIT {
    private String baseUrl;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/v1/";
    }

    @Test
    void should_add_a_task_to_buy_minttu() {
        TaskResponse expected = new TaskResponse("Malin", "Buy Minttu");
        TaskRequest request = new TaskRequest("Malin", "Buy Minttu");

        // addTasks(request);
        // List<TaskResponse> actual = getTasks();

        // assertThat(actualTask).containsExactly(expected)
    }

    /*
    private void addTasks(TaskRequest request) {
        String path = "addTask";
        WebTestClient.RequestHeadersSpec<?> client = WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(path)
                .body(Mono.just(request), TaskRequest.class);

        WebTestClient.ResponseSpec actual = client.exchange();

        actual.expectStatus().isCreated();
    }
    */

    /*
    private List<TaskResponse> getTasks() {
        String path = "getTasks" + "/" + "Malin";
        WebTestClient.RequestHeadersSpec<?> client = WebTestClient
                .bindToServer()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(path);

        WebTestClient.ResponseSpec actual = client.exchange();

        actual.expectStatus().isOk();
        actual.expectBodyList(TaskResponse.class).value(actualTask ->
                assertThat(actualTask).containsExactly(expected)
        );
        
        return the body
    }
    */
}
```

Enable addTasks and run the test.

It failed because the endpoint doesn't exist yet.

Let's fix it with some magic.

Annotate the controller with

```
@RestController
@RequestMapping(value = "/v1")
```

Run the test

Failure!

Annotate the Service as a Component

Re-run the tests

Failure!

Move the `InMemoryTodoRepository` to the production coe and annotate is a component.

Now we got a 404. More annotations to the resque.

and enhance `createNewTask` with

```
@PostMapping("/addTask")
@ResponseStatus(HttpStatus.CREATED)
```

Run the test again.

Finally, annotate the `createNewTask` with

```
@RequestBody 
```

Re-run and see the test pass.

However, we are only half way.

Enable the rest of the test and re-run.

Import

```
import static org.assertj.core.api.Assertions.assertThat;
```

Another failure

and `getTasks` with

```
@GetMapping("/getTasks/{user}")
```

Commit with the message:

```
A proper endpoint with an in memory solution
```

The endpoint works and can be tested by our consumers

However, it is rather useless as it will not remember anything after a program restart.

## Add proper persistence support

Last step today is to add proper persistence support.

A large step with lots of moving parts

We want to keep the code working so let's do this in small steps.

Copy the `InMemoryTodoRepository` and call it `SqlTodoRepository`

Move `InMemoryTodoRepository` to the test code and remove the `Component` annotation.

The tests should still pass. Run them and verify that they still pass.

Create a datasource, call it `DatasourceConfiguration` that will be used to talk to a PostgreSQL database using JDBI.

```
@Configuration
public class DatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    DataSource dataSource() {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    JdbiPlugin sqlObjectPlugin() {
        return new SqlObjectPlugin();
    }

    @Bean
    Jdbi jdbi(javax.sql.DataSource dataSource,
              List<JdbiPlugin> jdbiPlugins) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);
        Jdbi jdbi = Jdbi.create(proxy);

        jdbiPlugins.forEach(jdbi::installPlugin);

        return jdbi;
    }

    // @Bean
    // TodoDao todoDao(Jdbi jdbi) {
    //     return jdbi.onDemand(TodoDao.class);
    // }
}
```

Add a dev profile in

```
src/test/resources/application-dev.properties
```

and add this configuration to it

```
spring.datasource.url=jdbc:tc:postgresql:13.10:///todo
```

Finally, annotate the two integration tests with

```
@ActiveProfiles("dev")
```

Run the tests and verify that this step works.

The setup still works. But we are not using the database yet.

Let's test drive the database implementation.

Add a test for the repository called `TodoRepositoryTest` like this:

```
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TodoRepositoryTest {

    TodoRepository repository ;

    @Test
    void should_add_a_task_and_see_it() {
        User user = new User("Emil");
        Chore chore = new Chore("Must chop wood");
        Task expected = new Task(user, chore);
        Task task = new Task(user, chore);

        repository.addTask(task);
        List<Task> actual = repository.getTasks(user);

        assertThat(actual).containsExactly(expected);
    }
}
```

This test class is abstract, it can't be executed.

Add a runner for the in memory implementation like this

```
public class InMemoryTodoRepositoryTest extends TodoRepositoryTest {
    public InMemoryTodoRepositoryTest() {
        repository = new InMemoryTodoRepository();
    }
}
```

Run the repository test and see that it passes.

We used the im memory implementation as master. If we now use the same tests and use it to drive the sql implementation,
we have a fast and easy to change repository. And a harder to change but more persistent implementation. We will use the
fast one as much as we can in the tests and the slow one verifying that everything works together.

Add a runner for running the repository tests using SQL `SqlTodoRepositoryIT`

```
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class SqlTodoRepositoryIT extends TodoRepositoryTest {

    private final SqlTodoRepository SqlTodoRepository;

    @Autowired
    public SqlTodoRepositoryIT(SqlTodoRepository repository) {
        SqlTodoRepository = repository;
    }

    @BeforeEach
    void setUp() {
        repository = SqlTodoRepository;
    }
}
```

Run the tests and see that they pass.

However, we know that we don't use the database properly yet. Let's make sure we do.

First step is to clear the current implementation.

Then add a `TodoDao` as a constructor argument. This will be our way to talk to the databse.

Enable the `todoDao` bean in the database source configuration.

Implement the repository like this:

```
@Component
public class SqlTodoRepository implements TodoRepository {
    private final TodoDao todoDao;

    public SqlTodoRepository(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    @Override
    public void addTask(Task task) {
        String id = UUID.randomUUID().toString();
        String owner = task.user().user();
        String chore = task.chore().chore();

        todoDao.addTask(id, owner, chore);
    }

    @Override
    public List<Task> getTasks(User user) {
        String owner = user.user();

        return todoDao.getTasks(owner);
    }
}
```

Running the tests fails. It fails because JDBI is unhappy. We need some SQL in the dao for it to be happy.

Let's implement the dao like this:

```
@SqlUpdate("""
        insert into tasks (id, owner, chore)
        values (:id, :owner, :chore )
        """)
void addTask(String id,
             String owner,
             String chore);
```

and

```
@SqlQuery("""
        select owner, 
               chore
        from tasks
        where owner = :owner
        """)
@RegisterRowMapper(TaskMapper.class)
List<Task> getTasks(String owner);
```

Running the tests show us that we need to implement a mapper SQL to the domain.

Everything compiles but we are still failing. This time because we donät have any tables in the database.

Let's use FlyWay to create a table.

This is a two step process:

Add a configuration for FlyWay in `application.properties`

```
spring.flyway.locations=classpath:db/migration
```

Create the actual table in `src/main/resources/db/migration/V001__create_todo_table.sql`

```
CREATE TABLE tasks
(
    id    varchar(36) PRIMARY KEY,
    owner varchar(16),
    chore varchar(256)
);
```

Running the tests shows us that there is a missing implementation in the mapper.

```
String owner = rs.getString("owner");
User user = new User(owner);
String choreStr = rs.getString("chore");
Chore chore = new Chore(choreStr);

return new Task(user, chore);
```

Run the tests and see that it all passes.

We now have a working todo list with a proper database support. We are in control over things like the database using SQL.

Commit with

```
A proper database used during testing
```

All annotations added have been verified with tests.

We probably have a rather high test coverage percentage.

In order to start testcontainers from a test, I had to create a link to `~/.docker/run/docker.sock`
from `/var/run/docker.sock` using the command

```
sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
```