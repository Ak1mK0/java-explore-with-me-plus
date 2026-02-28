package ru.practicum.stat.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class StatsClient {
    private final RestClient restClient;
    private final String serverUrl;

    public StatsClient(@Value("${client.url}") String serverUrl) {
        this.serverUrl = serverUrl;

        // Настройка клиента с таймаутами
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(5))
                .withReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    public void hit(EndpointHitDto hit) {
        restClient.post()
                .uri("/hit")
                .body(hit)
                .retrieve()
                .toBodilessEntity(); // 201 Created без тела
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        String uri = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build()
                .toUriString();

        ViewStatsDto[] response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(ViewStatsDto[].class);

        return Arrays.asList(response);
    }
}