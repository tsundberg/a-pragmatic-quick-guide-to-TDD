package se.thinkcode.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        addTasks(request);
        List<TaskResponse> actual = getTasks();

        assertThat(actual).containsExactly(expected);
    }

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

    private List<TaskResponse> getTasks() {
        String path = "getTasks" + "/" + "Malin";
        WebTestClient.RequestHeadersSpec<?> client = WebTestClient
                .bindToServer()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(path);

        WebTestClient.ResponseSpec actualResponse = client.exchange();

        actualResponse.expectStatus().isOk();
        EntityExchangeResult<List<TaskResponse>> result = actualResponse
                .expectBodyList(TaskResponse.class)
                .returnResult();

        return result.getResponseBody();
    }
}