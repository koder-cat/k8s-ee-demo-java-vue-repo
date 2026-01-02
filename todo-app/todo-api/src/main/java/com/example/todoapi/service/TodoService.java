package com.example.todoapi.service;

import com.example.todoapi.model.Todo;
import com.example.todoapi.model.dto.CreateTodoDto;
import com.example.todoapi.model.dto.UpdateTodoDto;
import com.example.todoapi.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAllByOrderByCreatedAtAsc();
    }

    public Optional<Todo> findById(String id) {
        return todoRepository.findById(id);
    }

    public Todo create(CreateTodoDto dto) {
        String sanitizedTitle = sanitizeHtml(dto.getTitle());
        Todo todo = new Todo(sanitizedTitle);
        return todoRepository.save(todo);
    }

    public Optional<Todo> update(String id, UpdateTodoDto dto) {
        return todoRepository.findById(id)
                .map(todo -> {
                    if (dto.getTitle() != null) {
                        todo.setTitle(sanitizeHtml(dto.getTitle()));
                    }
                    if (dto.getCompleted() != null) {
                        todo.setCompleted(dto.getCompleted());
                    }
                    todo.setUpdatedAt(LocalDateTime.now());
                    return todoRepository.save(todo);
                });
    }

    public boolean delete(String id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>]", "");
    }
}
