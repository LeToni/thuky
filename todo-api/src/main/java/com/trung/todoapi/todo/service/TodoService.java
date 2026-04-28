package com.trung.todoapi.todo.service;

import com.trung.todoapi.todo.dto.CreateTodoInput;
import com.trung.todoapi.todo.dto.UpdateTodoInput;
import com.trung.todoapi.todo.exception.TodoNotFoundException;
import com.trung.todoapi.todo.model.Todo;
import com.trung.todoapi.todo.model.TodoPriority;
import com.trung.todoapi.todo.model.TodoStatus;
import com.trung.todoapi.todo.repository.TodoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {

	private final TodoRepository todoRepository;

	public TodoService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@Transactional(readOnly = true)
	public Optional<Todo> findById(Long id) {
		return todoRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Todo> search(Long id, String name, TodoStatus status, TodoPriority priority) {
		Specification<Todo> specification = null;

		if (id != null) {
			specification = appendSpecification(specification,
				(root, query, cb) -> cb.equal(root.get("id"), id));
		}
		if (name != null && !name.isBlank()) {
			String normalizedName = "%" + name.trim().toLowerCase() + "%";
			specification = appendSpecification(specification,
				(root, query, cb) -> cb.like(cb.lower(root.get("name")), normalizedName)
			);
		}
		if (status != null) {
			specification = appendSpecification(specification,
				(root, query, cb) -> cb.equal(root.get("status"), status));
		}
		if (priority != null) {
			specification = appendSpecification(specification,
				(root, query, cb) -> cb.equal(root.get("priority"), priority));
		}

		return todoRepository.findAll(specification);
	}

	private Specification<Todo> appendSpecification(Specification<Todo> source,
			Specification<Todo> additional) {
		if (source == null) {
			return additional;
		}
		return source.and(additional);
	}

	@Transactional
	public Todo create(CreateTodoInput input) {
		Todo todo = new Todo();
		todo.setName(input.name().trim());
		todo.setDescription(input.description());
		todo.setStatus(input.status());
		todo.setPriority(input.priority());
		return todoRepository.save(todo);
	}

	@Transactional
	public Todo update(Long id, UpdateTodoInput input) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));

		if (input.name() != null) {
			todo.setName(input.name().trim());
		}
		if (input.description() != null) {
			todo.setDescription(input.description());
		}
		if (input.status() != null) {
			todo.setStatus(input.status());
		}
		if (input.priority() != null) {
			todo.setPriority(input.priority());
		}

		return todoRepository.save(todo);
	}

	@Transactional
	public boolean delete(Long id) {
		if (!todoRepository.existsById(id)) {
			return false;
		}
		todoRepository.deleteById(id);
		return true;
	}
}
