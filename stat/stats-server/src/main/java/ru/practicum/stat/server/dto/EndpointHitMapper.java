package ru.practicum.stat.server.dto;

import ru.practicum.stat.dto.EndpointHitDto;

public class EndpointHitMapper {

    public static EndpointHit toEntity(EndpointHitDto dto) {
        return new EndpointHit(dto.getId(),
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp());
    }

    public static EndpointHitDto toDto(EndpointHit eh) {
        return new EndpointHitDto(eh.getId(),
                eh.getApp(),
                eh.getUri(),
                eh.getIp(),
                eh.getTimestamp());
    }
}
