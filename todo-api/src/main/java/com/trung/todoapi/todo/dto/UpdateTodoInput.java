package com.trung.todoapi.todo.dto;

import com.trung.todoapi.todo.model.TodoPriority;
import com.trung.todoapi.todo.model.TodoStatus;
import jakarta.validation.constraints.Size;

public record UpdateTodoInput(
	@Size(min = 1, max = 150) String name,
	@Size(max = 1000) String description,
	TodoStatus status,
	TodoPriority priority
) {
}
