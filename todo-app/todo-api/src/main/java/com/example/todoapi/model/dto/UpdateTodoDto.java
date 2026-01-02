package com.example.todoapi.model.dto;

import jakarta.validation.constraints.Size;

public class UpdateTodoDto {

    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    private Boolean completed;

    public UpdateTodoDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
