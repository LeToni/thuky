package com.trung.todoapi;

import org.springframework.boot.SpringApplication;

public class TestTodoApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TodoApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
