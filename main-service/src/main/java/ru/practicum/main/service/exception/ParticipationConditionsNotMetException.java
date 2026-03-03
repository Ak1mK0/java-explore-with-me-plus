package ru.practicum.main.service.exception;

public class ParticipationConditionsNotMetException extends RuntimeException {
    public ParticipationConditionsNotMetException(String message) {
        super(message);
    }
}
