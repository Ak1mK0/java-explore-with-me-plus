package ru.practicum.main.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import ru.practicum.stat.client.StatsClient;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
@Import(StatsClient.class)
public class MainService {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainService.class, args);

        StatsClient statsClient = context.getBean(StatsClient.class);
        EndpointHitDto hit = new EndpointHitDto(null,
                "app",
                "uri",
                "111.111.111.111",
                LocalDateTime.now());
        hit = statsClient.hit(hit);
        System.out.println("hit: " + hit);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime start = LocalDateTime.parse("2020-05-05 00:00:00", formatter);
        LocalDateTime end = LocalDateTime.parse("2035-05-05 00:00:00", formatter);

        List<ViewStatsDto> dto = statsClient.getStats(start, end, null, null);
        System.out.println("dto: " + dto);
    }
}
