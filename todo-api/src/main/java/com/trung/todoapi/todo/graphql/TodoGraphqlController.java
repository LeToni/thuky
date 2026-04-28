package com.trung.todoapi.todo.graphql;

import com.trung.todoapi.todo.dto.CreateTodoInput;
import com.trung.todoapi.todo.dto.UpdateTodoInput;
import com.trung.todoapi.todo.model.Todo;
import com.trung.todoapi.todo.model.TodoPriority;
import com.trung.todoapi.todo.model.TodoStatus;
import com.trung.todoapi.todo.service.TodoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class TodoGraphqlController {

	private final TodoService todoService;

	public TodoGraphqlController(TodoService todoService) {
		this.todoService = todoService;
	}

	@QueryMapping
	public Todo todo(@Argument Long id) {
		return todoService.findById(id).orElse(null);
	}

	@QueryMapping
	public List<Todo> todos(@Argument Long id, @Argument String name, @Argument TodoStatus status,
			@Argument TodoPriority priority) {
		return todoService.search(id, name, status, priority);
	}

	@MutationMapping
	public Todo createTodo(@Argument @Valid CreateTodoInput input) {
		return todoService.create(input);
	}

	@MutationMapping
	public Todo updateTodo(@Argument Long id, @Argument @Valid UpdateTodoInput input) {
		return todoService.update(id, input);
	}

	@MutationMapping
	public boolean deleteTodo(@Argument Long id) {
		return todoService.delete(id);
	}
}
