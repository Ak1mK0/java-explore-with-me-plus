package ru.practicum.stat.server.service;

import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerService {

    public EndpointHitDto saveHit(EndpointHitDto dto);

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
