package com.trung.todoapi.todo.graphql;

import com.trung.todoapi.TestcontainersConfiguration;
import com.trung.todoapi.todo.repository.TodoRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoGraphqlControllerTests {

	private GraphQlTester graphQlTester;

	@Autowired
	private TodoRepository todoRepository;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		WebTestClient webTestClient = WebTestClient.bindToServer()
			.baseUrl("http://localhost:" + port + "/graphql")
			.build();
		graphQlTester = HttpGraphQlTester.create(webTestClient);
		todoRepository.deleteAll();
	}

	@Test
	void createTodoAndFindById() {
		String createMutation = """
			mutation CreateTodo($input: CreateTodoInput!) {
			  createTodo(input: $input) {
			    id
			    name
			    description
			    status
			    priority
			  }
			}
			""";

		String id = graphQlTester.document(createMutation)
			.variable("input", Map.of(
				"name", "Buy milk",
				"description", "From local store",
				"status", "OPEN",
				"priority", "MEDIUM"
			))
			.execute()
			.path("createTodo.id")
			.entity(String.class)
			.get();

		String todoQuery = """
			query GetTodo($id: ID!) {
			  todo(id: $id) {
			    id
			    name
			    description
			    status
			    priority
			  }
			}
			""";

		graphQlTester.document(todoQuery)
			.variable("id", id)
			.execute()
			.path("todo.name")
			.entity(String.class)
			.isEqualTo("Buy milk");
	}

	@Test
	void searchTodosByCombinedFilters() {
		String createMutation = """
			mutation CreateTodo($input: CreateTodoInput!) {
			  createTodo(input: $input) {
			    id
			  }
			}
			""";

		graphQlTester.document(createMutation)
			.variable("input", Map.of(
				"name", "Write docs",
				"description", "Update readme",
				"status", "IN_PROGRESS",
				"priority", "HIGH"
			))
			.execute();

		graphQlTester.document(createMutation)
			.variable("input", Map.of(
				"name", "Deploy service",
				"description", "Release v1",
				"status", "DONE",
				"priority", "CRITICAL"
			))
			.execute();

		String query = """
			query SearchTodos($name: String, $status: TodoStatus, $priority: TodoPriority) {
			  todos(name: $name, status: $status, priority: $priority) {
			    name
			    status
			    priority
			  }
			}
			""";

		graphQlTester.document(query)
			.variable("name", "write")
			.variable("status", "IN_PROGRESS")
			.variable("priority", "HIGH")
			.execute()
			.path("todos")
			.entityList(Map.class)
			.hasSize(1)
			.satisfies(todos -> {
				@SuppressWarnings("unchecked")
				Map<String, Object> result = (Map<String, Object>) todos.getFirst();
				org.assertj.core.api.Assertions.assertThat(result.get("name")).isEqualTo("Write docs");
			});
	}

	@Test
	void updateAndDeleteTodo() {
		String createMutation = """
			mutation CreateTodo($input: CreateTodoInput!) {
			  createTodo(input: $input) {
			    id
			  }
			}
			""";

		String id = graphQlTester.document(createMutation)
			.variable("input", Map.of(
				"name", "Initial task",
				"description", "Initial description",
				"status", "OPEN",
				"priority", "LOW"
			))
			.execute()
			.path("createTodo.id")
			.entity(String.class)
			.get();

		String updateMutation = """
			mutation UpdateTodo($id: ID!, $input: UpdateTodoInput!) {
			  updateTodo(id: $id, input: $input) {
			    id
			    status
			    priority
			  }
			}
			""";

		graphQlTester.document(updateMutation)
			.variable("id", id)
			.variable("input", Map.of(
				"status", "DONE",
				"priority", "CRITICAL"
			))
			.execute()
			.path("updateTodo.status")
			.entity(String.class)
			.isEqualTo("DONE");

		String deleteMutation = """
			mutation DeleteTodo($id: ID!) {
			  deleteTodo(id: $id)
			}
			""";

		graphQlTester.document(deleteMutation)
			.variable("id", id)
			.execute()
			.path("deleteTodo")
			.entity(Boolean.class)
			.isEqualTo(true);
	}
}
