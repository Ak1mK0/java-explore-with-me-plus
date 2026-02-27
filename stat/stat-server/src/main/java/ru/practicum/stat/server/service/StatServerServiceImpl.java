package ru.practicum.stat.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat.server.dto.EndpointHit;
import ru.practicum.stat.server.repository.StatServerRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatServerServiceImpl implements StatServerService {
    private final StatServerRepository statServerRepository;

    @Transactional
    public void saveHit() {
        EndpointHit dto = new EndpointHit(
                null,
                "app",
                "/1",
                "192.160.1.1",
                LocalDateTime.now());
        dto = statServerRepository.save(dto);
        log.debug("Added hit: {}", dto);
    }

    public void getHits() {
        Optional<EndpointHit> dto = statServerRepository.findById(1L);
        log.debug("Get hit: {}", dto);
    }
}
