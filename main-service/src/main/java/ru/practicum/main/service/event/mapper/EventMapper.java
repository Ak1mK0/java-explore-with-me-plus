package ru.practicum.main.service.event.mapper;

import ru.practicum.main.service.event.dto.EventShortDto;
import ru.practicum.main.service.event.model.Event;

public class EventMapper {
    public static EventShortDto toShortDto(Event event) {
        // временная реализация, пока модуль событий не готов
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }
}