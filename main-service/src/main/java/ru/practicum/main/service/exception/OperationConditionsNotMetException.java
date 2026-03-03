package ru.practicum.main.service.exception;

public class OperationConditionsNotMetException extends RuntimeException {

    public OperationConditionsNotMetException(String message) {
        super(message);
    }
}