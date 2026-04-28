package com.trung.todoapi.todo.dto;

import com.trung.todoapi.todo.model.TodoPriority;
import com.trung.todoapi.todo.model.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTodoInput(
	@NotBlank @Size(max = 150) String name,
	@Size(max = 1000) String description,
	@NotNull TodoStatus status,
	@NotNull TodoPriority priority
) {
}
