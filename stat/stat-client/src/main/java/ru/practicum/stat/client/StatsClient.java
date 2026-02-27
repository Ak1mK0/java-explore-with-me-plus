package ru.practicum.stat.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate rest;
    private final String serverUrl;

    // Конструктор с параметрами из application.properties основного сервиса
    public StatsClient(@Value("${client.url}") String serverUrl, RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.rest = builder.build();
    }

    /**
     * Сохраняет информацию о запросе к эндпоинту.
     */
    public void hit(EndpointHitDto hit) {
        rest.postForEntity(serverUrl + "/hit", hit, Void.class);
    }

    /**
     * Получает статистику по посещениям за указанный период.
     *
     * @param start  начало диапазона
     * @param end    конец диапазона
     * @param uris   список uri (может быть null или пустым)
     * @param unique учитывать только уникальные ip
     * @return список ViewStatsDto
     */
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", uris)   // автоматически преобразуется в несколько параметров uris=...
                .queryParam("unique", unique)
                .encode()
                .toUriString();

        ResponseEntity<ViewStatsDto[]> response = rest.getForEntity(url, ViewStatsDto[].class);
        return Arrays.asList(response.getBody());
    }
}