package com.example.todoapi.controller;

import com.example.todoapi.model.Todo;
import com.example.todoapi.model.dto.CreateTodoDto;
import com.example.todoapi.model.dto.UpdateTodoDto;
import com.example.todoapi.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> getAll() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getById(@PathVariable String id) {
        return todoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todo> create(@Valid @RequestBody CreateTodoDto dto) {
        Todo created = todoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable String id, @Valid @RequestBody UpdateTodoDto dto) {
        return todoService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (todoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
