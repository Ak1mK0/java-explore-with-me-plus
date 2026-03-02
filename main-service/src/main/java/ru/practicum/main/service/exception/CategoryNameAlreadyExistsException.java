package ru.practicum.main.service.exception;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String message) {
        super(message);
    }
}