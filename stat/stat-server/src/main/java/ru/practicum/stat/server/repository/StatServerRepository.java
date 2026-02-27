package ru.practicum.stat.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.server.dto.EndpointHit;

public interface StatServerRepository extends JpaRepository<EndpointHit, Long> {
}