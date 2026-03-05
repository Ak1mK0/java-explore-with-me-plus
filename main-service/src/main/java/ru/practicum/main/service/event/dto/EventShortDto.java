package ru.practicum.main.service.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventShortDto {
    private Integer id;

    private String title;
}